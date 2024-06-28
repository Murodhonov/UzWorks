package dev.goblingroup.uzworks.vm

import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.GenderChoiceLayoutBinding
import dev.goblingroup.uzworks.models.request.JobEditRequest
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.repository.AnnouncementRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.INPUT_ERROR
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.DateEnum
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.utils.dmyToIso
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
class EditJobViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val securityRepository: SecurityRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _jobLiveData = MutableLiveData<ApiStatus<JobResponse>>(ApiStatus.Loading())
    val jobLiveData get() = _jobLiveData

    private val editLiveData = MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    lateinit var jobId: String
    private lateinit var jobResponse: JobResponse
    var regionId: String? = null
    var districtId: String? = null
    var categoryId: String? = null
    lateinit var latLng: LatLng
    var gender = GenderEnum.UNKNOWN.code

    fun fetchJob(): LiveData<ApiStatus<JobResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = announcementRepository.getJobById(jobId)
                if (response.isSuccessful) {
                    jobResponse = response.body()!!
                    latLng = LatLng(jobResponse.latitude, jobResponse.longitude)
                    gender = when (jobResponse.gender) {
                        GenderEnum.MALE.label -> {
                            GenderEnum.MALE.code
                        }

                        GenderEnum.FEMALE.label -> {
                            GenderEnum.FEMALE.code
                        }

                        GenderEnum.UNKNOWN.label -> {
                            GenderEnum.UNKNOWN.code
                        }

                        else -> {
                            -1
                        }
                    }
                    regionId = jobResponse.district.region.id
                    districtId = jobResponse.district.id
                    categoryId = jobResponse.jobCategory.id
                    _jobLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    _jobLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                    Log.e(TAG, "fetchJob: ${response.code()}")
                    Log.e(TAG, "fetchJob: ${response.errorBody()?.extractErrorMessage()}")
                }
            }
        }
        return jobLiveData
    }

    fun editJob(
        context: Context,
        titleEt: TextInputLayout,
        salaryEt: TextInputLayout,
        workingTimeEt: TextInputLayout,
        workingScheduleEt: TextInputLayout,
        tgUserNameEt: TextInputLayout,
        benefitEt: TextInputLayout,
        requirementEt: TextInputLayout,
        minAgeEt: TextInputLayout,
        maxAgeEt: TextInputLayout,
        deadlineEt: TextInputLayout,
        district: TextView,
        category: TextView,
    ): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
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
                    workingScheduleEt.error =
                        context.resources.getString(R.string.working_schedule_error)
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
                if (districtId == null) {
                    district.setBackgroundResource(R.drawable.disabled_tv_background)
                }
                if (categoryId == null) {
                    category.setBackgroundResource(R.drawable.disabled_tv_background)
                }
                if (latLng.latitude == 0.0 && latLng.longitude == 0.0) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.select_location),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (deadlineEt.editText?.text.toString().isEmpty() ||
                    titleEt.isErrorEnabled ||
                    salaryEt.isErrorEnabled ||
                    workingTimeEt.isErrorEnabled ||
                    workingScheduleEt.isErrorEnabled ||
                    tgUserNameEt.isErrorEnabled ||
                    benefitEt.isErrorEnabled ||
                    minAgeEt.isErrorEnabled ||
                    maxAgeEt.isErrorEnabled ||
                    districtId == null ||
                    categoryId == null ||
                    latLng.latitude == 0.0 ||
                    latLng.longitude == 0.0
                ) {
                    editLiveData.postValue(ApiStatus.Error(Throwable(INPUT_ERROR)))
                } else {
                    val response = announcementRepository.editJob(
                        JobEditRequest(
                            benefit = benefitEt.editText?.text.toString(),
                            categoryId = categoryId.toString(),
                            deadline = deadlineEt.editText?.text.toString().dmyToIso().toString(),
                            districtId = districtId.toString(),
                            gender = gender,
                            id = jobId,
                            instagramLink = jobResponse.instagramLink,
                            latitude = latLng.latitude,
                            longitude = latLng.longitude,
                            maxAge = maxAgeEt.editText?.text.toString().toInt(),
                            minAge = minAgeEt.editText?.text.toString().toInt(),
                            phoneNumber = securityRepository.getPhoneNumber().toString(),
                            requirement = requirementEt.editText?.text.toString(),
                            salary = salaryEt.editText?.text.toString().trim()
                                .filter { !it.isWhitespace() }.toInt(),
                            telegramLink = jobResponse.telegramLink,
                            tgUserName = tgUserNameEt.editText?.text.toString().trim().substring(1),
                            title = titleEt.editText?.text.toString(),
                            workingSchedule = workingScheduleEt.editText?.text.toString(),
                            workingTime = workingTimeEt.editText?.text.toString()
                        )
                    )
                    if (response.isSuccessful) {
                        editLiveData.postValue(ApiStatus.Success(response.body()))
                    } else {
                        editLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                        Log.e(TAG, "editJob: ${response.code()}")
                        Log.e(TAG, "editJob: ${response.errorBody()?.extractErrorMessage()}")
                    }
                }
            }
        }
        return editLiveData
    }

    fun controlInput(
        fragmentActivity: FragmentActivity,
        topTv: TextView,
        titleEt: TextInputLayout,
        salaryEt: TextInputLayout,
        workingTimeEt: TextInputLayout,
        workingScheduleEt: TextInputLayout,
        tgUserNameEt: TextInputLayout,
        benefitEt: TextInputLayout,
        requirementEt: TextInputLayout,
        minAgeEt: TextInputLayout,
        maxAgeEt: TextInputLayout,
        deadlineEt: TextInputLayout,
        genderLayout: GenderChoiceLayoutBinding
    ) {
        topTv.isSelected = true

        deadlineEt.editText?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                val datePickerDialog = DatePickerDialog(
                    fragmentActivity,
                    R.style.DatePickerDialogTheme,
                    { _, year, month, dayOfMonth ->
                        val selectedCalendar = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }

                        val currentCalendar = Calendar.getInstance()

                        if (selectedCalendar.before(currentCalendar)) {
                            Toast.makeText(
                                fragmentActivity,
                                fragmentActivity.resources.getString(R.string.invalid_deadline),
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
                if (gender != GenderEnum.MALE.code) {
                    gender = GenderEnum.MALE.code
                    selectMale(fragmentActivity.resources)
                }
            }
            femaleBtn.setOnClickListener {
                if (gender != GenderEnum.FEMALE.code) {
                    gender = GenderEnum.FEMALE.code
                    selectFemale(fragmentActivity.resources)
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

}