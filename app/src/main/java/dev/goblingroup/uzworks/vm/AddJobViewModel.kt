package dev.goblingroup.uzworks.vm

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import dev.goblingroup.uzworks.models.response.JobCreateResponse
import dev.goblingroup.uzworks.repository.AnnouncementRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.DateEnum
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.utils.extractDateValue
import dev.goblingroup.uzworks.utils.extractErrorMessage
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
class AddJobViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val securityRepository: SecurityRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val createLiveData =
        MutableLiveData<ApiStatus<JobCreateResponse>>(ApiStatus.Loading())

    private val _title = MutableLiveData("")
    val title get() = _title

    private val _salary = MutableLiveData(0)
    val salary get() = _salary

    private val _gender = MutableLiveData(GenderEnum.UNKNOWN.code)
    val gender get() = _gender

    private val _workingTime = MutableLiveData("")
    val workingTime get() = _workingTime

    private val _workingSchedule = MutableLiveData("")
    val workingSchedule get() = _workingSchedule

    private val _deadline = MutableLiveData("")
    val deadline get() = _deadline

    private val _tgUserName = MutableLiveData("")
    val tgUserName get() = _tgUserName

    private val _phoneNumber = MutableLiveData(securityRepository.getPhoneNumber())
    val phoneNumber get() = _phoneNumber

    private val _benefit = MutableLiveData("")
    val benefit get() = _benefit

    private val _requirement = MutableLiveData("")
    val requirement get() = _requirement

    private val _minAge = MutableLiveData(0)
    val minAge get() = _minAge

    private val _maxAge = MutableLiveData(0)
    val maxAge get() = _maxAge

    private val _latitude = MutableLiveData(0.0)

    val latitude get() = _latitude

    private val _longitude = MutableLiveData(0.0)

    val longitude get() = _longitude

    private val _categoryId = MutableLiveData("")
    val categoryId get() = _categoryId

    private val _districtId = MutableLiveData("")
    val districtId get() = _districtId

    private val _regionId = MutableLiveData("")
    val regionId get() = _regionId

    private val _regionName = MutableLiveData("")
    val regionName get() = _regionName

    private val _districtName = MutableLiveData("")
    val districtName get() = _districtName

    private val _categoryName = MutableLiveData("")
    val categoryName get() = _categoryName

    fun createJob(jobCreateRequest: JobCreateRequest): LiveData<ApiStatus<JobCreateResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                createLiveData.postValue(ApiStatus.Loading())
                Log.d(TAG, "createJob: creating job $jobCreateRequest")
                val response = announcementRepository.createJob(jobCreateRequest)
                if (response.isSuccessful) {
                    createLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    Log.e(TAG, "createJob: ${response.code()}")
                    Log.e(TAG, "createJob: ${response.errorBody()?.extractErrorMessage()}")
                    createLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            }
        }
        return createLiveData
    }

    @SuppressLint("ClickableViewAccessibility")
    fun controlInput(
        context: Context,
        deadline: TextView,
        benefitEt: TextInputLayout,
        requirementEt: TextInputLayout,
        minAgeEt: TextInputLayout,
        maxAgeEt: TextInputLayout,
        salaryEt: TextInputLayout,
        tgUserNameEt: TextInputLayout,
        genderLayout: GenderChoiceLayoutBinding,
        titleEt: TextInputLayout,
        workingTimeEt: TextInputLayout,
        workingScheduleEt: TextInputLayout
    ) {

        deadline.setOnClickListener {
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
                            context.resources.getString(R.string.invalid_deadline),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val formatter = SimpleDateFormat(
                            "dd.MM.yyyy", Locale.getDefault()
                        )
                        deadline.setBackgroundResource(R.drawable.enabled_tv_background)
                        deadline.text = formatter.format(selectedCalendar.time)
                    }
                },
                deadline.text.toString()
                    .extractDateValue(DateEnum.YEAR.dateLabel),
                deadline.text.toString()
                    .extractDateValue(DateEnum.MONTH.dateLabel) - 1,
                deadline.text.toString()
                    .extractDateValue(DateEnum.DATE.dateLabel)
            )

            datePickerDialog.show()
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
                if (s.toString().isNotEmpty()) minAgeEt.isErrorEnabled = false

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
                if (s.toString().isNotEmpty()) maxAgeEt.isErrorEnabled = false

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
                if (formattedTgUsername.isNotEmpty()) tgUserNameEt.isErrorEnabled = false

                isFormatting = false
            }

        })

        genderLayout.apply {
            maleBtn.setOnClickListener {
                if (gender.value?.toInt() != GenderEnum.MALE.code) {
                    _gender.value = GenderEnum.MALE.code
                    selectMale(context.resources)
                }
            }
            femaleBtn.setOnClickListener {
                if (gender.value?.toInt() != GenderEnum.FEMALE.code) {
                    _gender.value = GenderEnum.FEMALE.code
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

        benefitEt.editText?.doAfterTextChanged {
            if (benefitEt.isErrorEnabled && it.toString().isNotEmpty()) {
                benefitEt.isErrorEnabled = false
            }
        }

        requirementEt.editText?.doAfterTextChanged {
            if (requirementEt.isErrorEnabled && it.toString().isNotEmpty()) {
                requirementEt.isErrorEnabled = false
            }
        }
    }

    fun setBenefit(value: String) {
        _benefit.value = value
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

    fun setDistrictName(value: String) {
        _districtName.value = value
    }

    fun setRegionName(value: String) {
        _regionName.value = value
    }

    fun setCategoryName(value: String) {
        _categoryName.value = value
    }

    fun setMaxAge(value: Int) {
        _maxAge.value = value
    }

    fun setMinAge(value: Int) {
        _minAge.value = value
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
        deadlineInput: TextView,
        titleEt: TextInputLayout,
        salaryEt: TextInputLayout,
        workingTimeEt: TextInputLayout,
        workingScheduleEt: TextInputLayout,
        tgUserNameEt: TextInputLayout,
        benefitEt: TextInputLayout,
        requirementEt: TextInputLayout,
        minAgeEt: TextInputLayout,
        maxAgeEt: TextInputLayout,
        district: TextView,
        category: TextView
    ): Boolean {
        if (deadlineInput.text.toString() == context.resources.getString(R.string.deadline)) {
            deadlineInput.setBackgroundResource(R.drawable.disabled_tv_background)
            deadlineInput.error = context.resources.getString(R.string.deadline_error)
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
        if (districtId.value?.isEmpty() == true) {
            district.setBackgroundResource(R.drawable.disabled_tv_background)
        }
        if (categoryId.value?.isEmpty() == true) {
            category.setBackgroundResource(R.drawable.disabled_tv_background)
        }
        if (latitude.value == 0.0 && longitude.value == 0.0) {
            Toast.makeText(context, context.getString(R.string.select_location), Toast.LENGTH_SHORT).show()
        }
        return deadlineInput.text.toString() != context.resources.getString(R.string.deadline) &&
                !titleEt.isErrorEnabled &&
                !salaryEt.isErrorEnabled &&
                !workingTimeEt.isErrorEnabled &&
                !workingScheduleEt.isErrorEnabled &&
                !tgUserNameEt.isErrorEnabled &&
                !benefitEt.isErrorEnabled &&
                !minAgeEt.isErrorEnabled &&
                !maxAgeEt.isErrorEnabled &&
                districtId.value.toString().isNotEmpty() &&
                categoryId.value.toString().isNotEmpty() &&
                latitude.value != 0.0 &&
                longitude.value != 0.0
    }

    fun clearLiveData() {
        _title.value = ""
        _salary.value = 0
        _gender.value = GenderEnum.UNKNOWN.code
        _workingTime.value = ""
        _workingSchedule.value = ""
        _deadline.value = ""
        _tgUserName.value = ""
        _phoneNumber.value = securityRepository.getPhoneNumber() // Optionally reset to initial value
        _benefit.value = ""
        _requirement.value = ""
        _minAge.value = 0
        _maxAge.value = 0
        _latitude.value = 0.0
        _longitude.value = 0.0
        _categoryId.value = ""
        _districtId.value = ""
        _regionId.value = ""
    }
}