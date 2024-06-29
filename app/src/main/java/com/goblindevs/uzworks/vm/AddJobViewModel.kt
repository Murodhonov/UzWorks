package com.goblindevs.uzworks.vm

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
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.databinding.GenderChoiceLayoutBinding
import com.goblindevs.uzworks.models.request.JobCreateRequest
import com.goblindevs.uzworks.models.response.JobCreateResponse
import com.goblindevs.uzworks.repository.AnnouncementRepository
import com.goblindevs.uzworks.repository.SecurityRepository
import com.goblindevs.uzworks.utils.ConstValues.TAG
import com.goblindevs.uzworks.utils.DateEnum
import com.goblindevs.uzworks.utils.GenderEnum
import com.goblindevs.uzworks.utils.NetworkHelper
import com.goblindevs.uzworks.utils.extractDateValue
import com.goblindevs.uzworks.utils.extractErrorMessage
import com.goblindevs.uzworks.utils.formatSalary
import com.goblindevs.uzworks.utils.formatTgUsername
import com.goblindevs.uzworks.utils.selectFemale
import com.goblindevs.uzworks.utils.selectMale
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

    private val _gender = MutableLiveData(GenderEnum.UNKNOWN.code)
    val gender get() = _gender

    private val _phoneNumber = MutableLiveData(securityRepository.getPhoneNumber())
    val phoneNumber get() = _phoneNumber

    private val _latitude = MutableLiveData<Double?>(null)

    val latitude get() = _latitude

    private val _longitude = MutableLiveData<Double?>(null)

    val longitude get() = _longitude

    private val _categoryId = MutableLiveData("")
    val categoryId get() = _categoryId

    private val _districtId = MutableLiveData("")
    val districtId get() = _districtId

    private val _regionId = MutableLiveData("")
    val regionId get() = _regionId

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
        deadlineEt: TextInputLayout,
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
        deadlineEt.editText?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
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
                            deadlineEt.isErrorEnabled = false
                            deadlineEt.editText?.setText(formatter.format(selectedCalendar.time))
                        }
                    },
                    deadlineEt.editText?.text.toString()
                        .extractDateValue(DateEnum.YEAR.dateLabel),
                    deadlineEt.editText?.text.toString()
                        .extractDateValue(DateEnum.MONTH.dateLabel) - 1,
                    deadlineEt.editText?.text.toString()
                        .extractDateValue(DateEnum.DATE.dateLabel)
                )

                datePickerDialog.show()

                datePickerDialog.setOnDismissListener {
                    deadlineEt.clearFocus()
                }
            }
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

    fun setLatitude(value: Double) {
        _latitude.value = value
    }

    fun setLongitude(value: Double) {
        _longitude.value = value
    }

    fun isFormValid(
        context: Context,
        deadlineEt: TextInputLayout,
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
        if (latitude.value == null && longitude.value == null) {
            Toast.makeText(context, context.getString(R.string.select_location), Toast.LENGTH_SHORT).show()
        }
        return !deadlineEt.isErrorEnabled &&
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
                latitude.value != null &&
                longitude.value != null
    }

}