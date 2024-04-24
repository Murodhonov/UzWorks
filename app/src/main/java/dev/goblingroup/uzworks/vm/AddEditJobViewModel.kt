package dev.goblingroup.uzworks.vm

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.GenderChoiceLayoutBinding
import dev.goblingroup.uzworks.models.request.JobCreateRequest
import dev.goblingroup.uzworks.models.request.JobEditRequest
import dev.goblingroup.uzworks.models.response.JobCreateResponse
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.repository.AnnouncementRepository
import dev.goblingroup.uzworks.repository.SecuredJobRepository
import dev.goblingroup.uzworks.utils.ConstValues
import dev.goblingroup.uzworks.utils.DateEnum
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.clear
import dev.goblingroup.uzworks.utils.extractDateValue
import dev.goblingroup.uzworks.utils.formatPhoneNumber
import dev.goblingroup.uzworks.utils.formatSalary
import dev.goblingroup.uzworks.utils.formatTgUsername
import dev.goblingroup.uzworks.utils.selectFemale
import dev.goblingroup.uzworks.utils.selectMale
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddEditJobViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val securedJobRepository: SecuredJobRepository
) : ViewModel() {

    private val TAG = "SecuredDistrictViewMode"

    private val createLiveData =
        MutableLiveData<ApiStatus<JobCreateResponse>>(ApiStatus.Loading())

    private val editLiveData =
        MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    lateinit var status: String
    lateinit var jobId: String
    var selectedGender = ""

    private val _jobLiveData = MutableLiveData<ApiStatus<JobResponse>>(ApiStatus.Loading())

    private val _benefit = MutableLiveData("")
    val benefit: LiveData<String> get() = _benefit

    private val _categoryId = MutableLiveData("")
    val categoryId: LiveData<String> get() = _categoryId

    private val _latitude = MutableLiveData(0.0)

    val latitude: LiveData<Double> get() = _latitude

    private val _longitude = MutableLiveData(0.0)

    val longitude: LiveData<Double> get() = _longitude

    private val _deadline = MutableLiveData("")
    val deadline: LiveData<String> get() = _deadline

    private val _districtId = MutableLiveData("")
    val districtId: LiveData<String> get() = _districtId

    private val _gender = MutableLiveData("")
    val gender: LiveData<String> get() = _gender

    private val _maxAge = MutableLiveData(0)
    val maxAge: LiveData<Int> get() = _maxAge

    private val _minAge = MutableLiveData(0)
    val minAge: LiveData<Int> get() = _minAge

    private val _phoneNumber = MutableLiveData("")
    val phoneNumber: LiveData<String> get() = _phoneNumber

    private val _requirement = MutableLiveData("")
    val requirement: LiveData<String> get() = _requirement

    private val _salary = MutableLiveData(0)
    val salary: LiveData<Int> get() = _salary

    private val _tgUserName = MutableLiveData("")
    val tgUserName: LiveData<String> get() = _tgUserName

    private val _title = MutableLiveData("")
    val title: LiveData<String> get() = _title

    private val _workingSchedule = MutableLiveData("")
    val workingSchedule: LiveData<String> get() = _workingSchedule

    private val _workingTime = MutableLiveData("")
    val workingTime: LiveData<String> get() = _workingTime

    private val _categoryIndex = MutableLiveData(0)
    val categoryIndex: LiveData<Int> get() = _categoryIndex

    private val _regionIndex = MutableLiveData(0)
    val regionIndex: LiveData<Int> get() = _regionIndex

    private val _districtIndex = MutableLiveData(0)
    val districtIndex: LiveData<Int> get() = _districtIndex

    fun getJob(id: String): LiveData<ApiStatus<JobResponse>> {
        viewModelScope.launch {
            jobId = id
            val response = announcementRepository.getJobById(jobId)
            if (response.isSuccessful) {
                _jobLiveData.postValue(ApiStatus.Success(response.body()))
            } else {
                _jobLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
            }
        }
        return _jobLiveData
    }

    fun createJob(jobCreateRequest: JobCreateRequest): LiveData<ApiStatus<JobCreateResponse>> {
        viewModelScope.launch {
            val response = securedJobRepository.createJob(jobCreateRequest)
            if (response.isSuccessful) {
                createLiveData.postValue(ApiStatus.Success(response.body()))
            } else {
                createLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
            }
        }
        return createLiveData
    }

    fun editJob(jobEditRequest: JobEditRequest): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
            val response = securedJobRepository.editJob(jobEditRequest)
            if (response.isSuccessful) {
                editLiveData.postValue(ApiStatus.Success(null))
            } else {
                editLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
            }
        }
        return editLiveData
    }

    @SuppressLint("ClickableViewAccessibility")
    fun controlInput(
        context: Context,
        topTv: TextView,
        deadlineEt: TextInputLayout,
        minAgeEt: TextInputLayout,
        maxAgeEt: TextInputLayout,
        salaryEt: TextInputLayout,
        phoneNumberEt: TextInputLayout,
        tgUserNameEt: TextInputLayout,
        genderLayout: GenderChoiceLayoutBinding,
        titleEt: TextInputLayout,
        workingTimeEt: TextInputLayout,
        workingScheduleEt: TextInputLayout,
        regionChoice: AutoCompleteTextView,
        jobCategoryChoice: AutoCompleteTextView,
        jobCategoryLayout: TextInputLayout,
        districtChoice: AutoCompleteTextView,
        districtLayout: TextInputLayout
    ) {
        topTv.isSelected = true

        deadlineEt.clear()

        deadlineEt.editText?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val datePickerDialog = DatePickerDialog(
                    context,
                    R.style.DatePickerDialogTheme,
                    { _, year, month, dayOfMonth ->
                        val selectedCalendar = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }

                        val currentCalendar = Calendar.getInstance()

                        if (selectedCalendar.before(currentCalendar)) {
                            Toast.makeText(
                                context,
                                "Cannot select date before current date",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val formatter = SimpleDateFormat(
                                "dd.MM.yyyy", Locale.getDefault()
                            )
                            deadlineEt.isErrorEnabled = false
                            deadlineEt.editText?.setText(formatter.format(selectedCalendar.time))
                        }
                    },
                    deadlineEt.editText?.text.toString()
                        .extractDateValue(DateEnum.YEAR.dateLabel),
                    deadlineEt.editText?.text.toString()
                        .extractDateValue(DateEnum.MONTH.dateLabel),
                    deadlineEt.editText?.text.toString()
                        .extractDateValue(DateEnum.DATE.dateLabel)
                )

                datePickerDialog.show()
            }
            true
        }

        minAgeEt.editText?.addTextChangedListener(object : TextWatcher {
            var isFormatting = false

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting)
                    return

                if (s.toString() == "0")
                    return

                isFormatting = true
                if (s.toString().length > 2) {
                    minAgeEt.editText?.setText(s.toString().substring(0, 2))
                    minAgeEt.editText?.setSelection(2)
                }

                isFormatting = false
            }

        })

        maxAgeEt.editText?.addTextChangedListener(object : TextWatcher {
            var isFormatting = false

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting)
                    return

                if (s.toString() == "0")
                    return

                isFormatting = true
                if (s.toString().length > 2) {
                    maxAgeEt.editText?.setText(s.toString().substring(0, 2))
                    maxAgeEt.editText?.setSelection(2)
                }

                isFormatting = false
            }

        })

        salaryEt.editText?.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting)
                    return

                isFormatting = true
                val formattedSalary = s?.filter { !it.isWhitespace() }.toString().formatSalary()
                salaryEt.editText?.setText(formattedSalary)
                salaryEt.editText?.setSelection(formattedSalary.length)

                isFormatting = false
            }

        })

        phoneNumberEt.editText?.setText(context.resources.getString(R.string.phone_number_prefix))
        phoneNumberEt.editText?.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting)
                    return

                isFormatting = true
                val newText = s.toString().filter { !it.isWhitespace() }
                val oldText =
                    phoneNumberEt.editText?.tag.toString().filter { !it.isWhitespace() }
                val formattedPhone =
                    s?.filter { !it.isWhitespace() }.toString()
                        .formatPhoneNumber(newText.length < oldText.length)
                phoneNumberEt.editText?.setText(formattedPhone)
                phoneNumberEt.editText?.setSelection(formattedPhone.length)
                phoneNumberEt.tag = formattedPhone

                isFormatting = false
            }
        })

        tgUserNameEt.editText?.addTextChangedListener(object : TextWatcher {
            var isFormatting = false

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting)
                    return
                isFormatting = true
                val formattedTgUsername = s.toString().formatTgUsername()
                tgUserNameEt.editText?.setText(formattedTgUsername)
                tgUserNameEt.editText?.setSelection(formattedTgUsername.length)

                isFormatting = false
            }

        })

        genderLayout.apply {
            maleBtn.setOnClickListener {
                if (selectedGender == GenderEnum.FEMALE.label || selectedGender.isEmpty()) {
                    selectedGender = GenderEnum.MALE.label
                    selectMale(context.resources)
                }
            }
            femaleBtn.setOnClickListener {
                if (selectedGender == GenderEnum.MALE.label || selectedGender.isEmpty()) {
                    selectedGender = GenderEnum.FEMALE.label
                    selectFemale(context.resources)
                }
            }
        }

        titleEt.editText?.doAfterTextChanged {
            if (titleEt.isErrorEnabled && it.toString().isNotEmpty()) {
                titleEt.isErrorEnabled = false
            }
        }

        salaryEt.editText?.doAfterTextChanged {
            if (salaryEt.isErrorEnabled && it.toString().isNotEmpty()) {
                salaryEt.isErrorEnabled = false
            }
        }

        workingTimeEt.editText?.doAfterTextChanged {
            if (workingTimeEt.isErrorEnabled && it.toString().isNotEmpty()) {
                workingTimeEt.isErrorEnabled = false
            }
        }

        workingScheduleEt.editText?.doAfterTextChanged {
            if (workingScheduleEt.isErrorEnabled && it.toString().isNotEmpty()) {
                workingScheduleEt.isErrorEnabled = false
            }
        }

        tgUserNameEt.editText?.doAfterTextChanged {
            if (tgUserNameEt.isErrorEnabled && it.toString().isNotEmpty()) {
                tgUserNameEt.isErrorEnabled = false
            }
        }

        phoneNumberEt.editText?.doAfterTextChanged {
            if (phoneNumberEt.isErrorEnabled && it.toString().isNotEmpty()) {
                phoneNumberEt.isErrorEnabled = false
            }
        }

        regionChoice.setOnItemClickListener { adapterView, view, i, l ->
            if (districtLayout.isErrorEnabled) {
                districtLayout.isErrorEnabled = false
                districtLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU
            }
        }

        jobCategoryChoice.setOnClickListener {
            jobCategoryLayout.isErrorEnabled = false
            jobCategoryLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU
        }

        districtChoice.setOnClickListener {
            districtLayout.isErrorEnabled = false
            districtLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU

        }
    }

    fun setBenefit(value: String) {
        _benefit.value = value
    }

    fun setCategoryId(value: String) {
        _categoryId.value = value
    }


    fun setLatitude(value: Double) {
        _latitude.value = value
    }

    fun setLongitude(value: Double) {
        _longitude.value = value
    }

    fun setDeadline(value: String) {
        _deadline.value = value
    }

    fun setDistrictId(value: String) {
        _districtId.value = value
    }

    fun setGender(value: String) {
        Log.d(ConstValues.TAG, "setGender: $value")
        _gender.value = value
    }

    fun setMaxAge(value: Int) {
        _maxAge.value = value
    }

    fun setMinAge(value: Int) {
        _minAge.value = value
    }

    fun setPhoneNumber(value: String) {
        _phoneNumber.value = value
    }

    fun setRequirement(value: String) {
        _requirement.value = value
    }

    fun setSalary(value: Int) {
        _salary.value = value
    }

    fun setTgUsername(value: String) {
        _tgUserName.value = value
    }

    fun setTitle(value: String) {
        _title.value = value
    }

    fun setWorkingSchedule(value: String) {
        _workingSchedule.value = value
    }

    fun setWorkingTime(value: String) {
        _workingTime.value = value
    }

    fun isFormValid(
        context: Context,
        deadlineEt: TextInputLayout,
        titleEt: TextInputLayout,
        salaryEt: TextInputLayout,
        workingTimeEt: TextInputLayout,
        workingScheduleEt: TextInputLayout,
        tgUserNameEt: TextInputLayout,
        phoneNumberEt: TextInputLayout,
        benefitEt: TextInputLayout,
        requirementEt: TextInputLayout,
        minAgeEt: TextInputLayout,
        maxAgeEt: TextInputLayout,
        districtChoice: AutoCompleteTextView,
        districtLayout: TextInputLayout,
        jobCategoryChoice: AutoCompleteTextView,
        jobCategoryLayout: TextInputLayout
    ): Boolean {
        if (deadlineEt.editText?.text.toString().isEmpty()) {
            deadlineEt.isErrorEnabled = true
            deadlineEt.error = context.resources.getString(R.string.deadline_error)
        }
        if (titleEt.editText?.text.toString().isEmpty()) {
            titleEt.isErrorEnabled = true
            titleEt.error = context.resources.getString(R.string.title_error)
        }
        if (salaryEt.editText?.text.toString().isEmpty()) {
            salaryEt.isErrorEnabled = true
            salaryEt.error = context.resources.getString(R.string.salary_error)
        }
        if (workingTimeEt.editText?.text.toString().isEmpty()) {
            workingTimeEt.isErrorEnabled = true
            workingTimeEt.error = context.resources.getString(R.string.working_time_error)
        }
        if (workingScheduleEt.editText?.text.toString().isEmpty()) {
            workingScheduleEt.isErrorEnabled = true
            workingScheduleEt.error = context.resources.getString(R.string.working_schedule_error)
        }
        if (tgUserNameEt.editText?.text.toString()
                .isEmpty() || tgUserNameEt.editText?.text.toString() == "@"
        ) {
            tgUserNameEt.isErrorEnabled = true
            tgUserNameEt.error = context.resources.getString(R.string.tg_username_error)
        }
        if (phoneNumberEt.editText?.text.toString().filter { !it.isWhitespace() }.length != 13) {
            phoneNumberEt.isErrorEnabled = true
            phoneNumberEt.error = context.resources.getString(R.string.phone_number_error)
        }
        if (benefitEt.editText?.text.toString().isEmpty()) {
            benefitEt.isErrorEnabled = true
            benefitEt.error = context.resources.getString(R.string.benefit_error)
        }
        if (requirementEt.editText?.text.toString().isEmpty()) {
            requirementEt.isErrorEnabled = true
            requirementEt.error = context.resources.getString(R.string.benefit_error)
        }
        if (minAgeEt.editText?.text.toString().isEmpty()) {
            minAgeEt.isErrorEnabled = true
            minAgeEt.error = context.resources.getString(R.string.benefit_error)
        }
        if (maxAgeEt.editText?.text.toString().isEmpty()) {
            maxAgeEt.isErrorEnabled = true
            maxAgeEt.error = context.resources.getString(R.string.benefit_error)
        }
        if (!districtChoice.isSelected) {
            districtChoice.error = context.resources.getString(R.string.district_error)
            districtLayout.endIconMode = TextInputLayout.END_ICON_NONE
        }
        if (!jobCategoryChoice.isSelected) {
            jobCategoryChoice.error = context.resources.getString(R.string.district_error)
            jobCategoryLayout.endIconMode = TextInputLayout.END_ICON_NONE
        }
        if (selectedGender.isEmpty()) {
            Toast.makeText(
                context,
                context.resources.getString(R.string.confirm_gender),
                Toast.LENGTH_SHORT
            ).show()
        }
        if (latitude.value == 0.0 && longitude.value == 0.0) {
            Toast.makeText(context, context.getString(R.string.select_location), Toast.LENGTH_SHORT).show()
        }
        return !deadlineEt.isErrorEnabled &&
                !titleEt.isErrorEnabled &&
                !salaryEt.isErrorEnabled &&
                selectedGender.isNotEmpty() &&
                !workingTimeEt.isErrorEnabled &&
                !workingScheduleEt.isErrorEnabled &&
                !tgUserNameEt.isErrorEnabled &&
                !benefitEt.isErrorEnabled &&
                !minAgeEt.isErrorEnabled &&
                !maxAgeEt.isErrorEnabled &&
                !districtLayout.isErrorEnabled &&
                !jobCategoryLayout.isErrorEnabled &&
                latitude.value != 0.0 &&
                longitude.value != 0.0
    }

}