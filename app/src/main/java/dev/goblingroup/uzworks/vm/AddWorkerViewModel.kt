package dev.goblingroup.uzworks.vm

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.GenderChoiceLayoutBinding
import dev.goblingroup.uzworks.models.request.UserUpdateRequest
import dev.goblingroup.uzworks.models.request.WorkerCreateRequest
import dev.goblingroup.uzworks.models.response.WorkerCreateResponse
import dev.goblingroup.uzworks.repository.AnnouncementRepository
import dev.goblingroup.uzworks.repository.ProfileRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.DEFAULT_BIRTHDAY
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
class AddWorkerViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val profileRepository: ProfileRepository,
    private val securityRepository: SecurityRepository,
    private val networkHelper: NetworkHelper,
) : ViewModel() {

    private val addLiveData = MutableLiveData<ApiStatus<WorkerCreateResponse>>(ApiStatus.Loading())

    var districtId = ""
    var jobCategoryId = ""
    var gender = securityRepository.getGender()
    var birthdate = securityRepository.getBirthdate()
    val phoneNumber = securityRepository.getPhoneNumber().toString()

    fun addWorker(workerCreateRequest: WorkerCreateRequest): LiveData<ApiStatus<WorkerCreateResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val createWorker = announcementRepository.createWorker(workerCreateRequest)
                if (createWorker.isSuccessful) {
                    val userResponse = profileRepository.getUserById(securityRepository.getUserId())
                    if (userResponse.isSuccessful) {
                        val updateResponse = profileRepository.updateUser(
                            UserUpdateRequest(
                                birthDate = birthdate.toString(),
                                email = userResponse.body()?.email,
                                firstName = userResponse.body()?.firstName.toString(),
                                gender = gender,
                                id = userResponse.body()?.id.toString(),
                                lastName = userResponse.body()?.lastName.toString(),
                                mobileId = ""
                            )
                        )
                        if (updateResponse.isSuccessful) {
                            securityRepository.setBirthdate(workerCreateRequest.birthDate)
                            securityRepository.setGender(gender)
                            addLiveData.postValue(ApiStatus.Success(createWorker.body()))
                        }
                    }
                } else {
                    addLiveData.postValue(ApiStatus.Error(Throwable(createWorker.message())))
                    Log.e(TAG, "addWorker: ${createWorker.errorBody()?.extractErrorMessage()}")
                    Log.e(TAG, "addWorker: ${createWorker.code()}")
                }
            }
        }
        return addLiveData
    }

    @SuppressLint("ClickableViewAccessibility")
    fun controlInput(
        fragmentActivity: FragmentActivity,
        deadlineEt: TextInputLayout,
        birthdayEt: TextInputLayout,
        titleEt: TextInputLayout,
        salaryEt: TextInputLayout,
        genderLayout: GenderChoiceLayoutBinding,
        workingTimeEt: TextInputLayout,
        workingScheduleEt: TextInputLayout,
        tgUserNameEt: TextInputLayout
    ) {
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
                            deadlineEt.isErrorEnabled = true
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

        birthdayEt.editText?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                val datePickerDialog = DatePickerDialog(
                    fragmentActivity,
                    R.style.DatePickerDialogTheme,
                    { _, year, month, dayOfMonth ->
                        val selectedCalendar = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }

                        val minimumCalendar =
                            Calendar.getInstance().apply { add(Calendar.YEAR, -16) }

                        if (selectedCalendar.after(minimumCalendar)) {
                            Toast.makeText(
                                fragmentActivity,
                                fragmentActivity.resources.getString(R.string.birthdate_requirement),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val formatter = SimpleDateFormat(
                                "dd.MM.yyyy", Locale.getDefault()
                            )
                            birthdayEt.editText?.setText(formatter.format(selectedCalendar.time))
                            birthdate = birthdayEt.editText?.text.toString()
                        }
                    },
                    birthdayEt.editText?.text.toString()
                        .extractDateValue(DateEnum.YEAR.dateLabel),
                    birthdayEt.editText?.text.toString()
                        .extractDateValue(DateEnum.MONTH.dateLabel) - 1,
                    birthdayEt.editText?.text.toString()
                        .extractDateValue(DateEnum.DATE.dateLabel),
                )

                datePickerDialog.show()

                datePickerDialog.setOnDismissListener {
                    birthdayEt.clearFocus()
                }
            }
        }

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
                if (formattedSalary.isNotEmpty()) salaryEt.isErrorEnabled = false

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
            if (it.toString().isNotEmpty()) {
                titleEt.isErrorEnabled = false
            }
        }

        salaryEt.editText?.doAfterTextChanged {
            if (it.toString().isNotEmpty()) {
                salaryEt.isErrorEnabled = false
            }
        }

        workingTimeEt.editText?.doAfterTextChanged {
            if (it.toString().isNotEmpty()) {
                workingTimeEt.isErrorEnabled = false
            }
        }

        workingScheduleEt.editText?.doAfterTextChanged {
            if (it.toString().isNotEmpty()) {
                workingScheduleEt.isErrorEnabled = false
            }
        }

        tgUserNameEt.editText?.doAfterTextChanged {
            if (it.toString().isNotEmpty()) {
                tgUserNameEt.isErrorEnabled = false
            }
        }
    }

    fun isFormValid(
        resources: Resources,
        deadlineEt: TextInputLayout,
        titleEt: TextInputLayout,
        salaryEt: TextInputLayout,
        workingTimeEt: TextInputLayout,
        workingScheduleEt: TextInputLayout,
        tgUserNameEt: TextInputLayout,
        districtLayout: TextInputLayout,
        jobCategoryLayout: TextInputLayout,
    ): Boolean {
        if (deadlineEt.editText?.text.toString().isEmpty()) {
            deadlineEt.error = resources.getString(R.string.deadline_error)
            deadlineEt.isErrorEnabled = true
        }
        if (titleEt.editText?.text.toString().isEmpty()) {
            titleEt.error = resources.getString(R.string.title_error)
            titleEt.isErrorEnabled = true
        }
        if (salaryEt.editText?.text.toString().isEmpty()) {
            salaryEt.error = resources.getString(R.string.salary_error)
            salaryEt.isErrorEnabled = true
        }
        if (workingTimeEt.editText?.text.toString().isEmpty()) {
            workingTimeEt.error = resources.getString(R.string.working_time_error)
            workingTimeEt.isErrorEnabled = true
        }
        if (workingScheduleEt.editText?.text.toString().isEmpty()) {
            workingScheduleEt.error = resources.getString(R.string.working_schedule_error)
            workingScheduleEt.isErrorEnabled = true
        }
        if (tgUserNameEt.editText?.text.toString().length <= 1) {
            tgUserNameEt.error = resources.getString(R.string.tg_username_error)
            tgUserNameEt.isErrorEnabled = true
        }
        if (districtId.isEmpty()) {
            districtLayout.error = resources.getString(R.string.district_error)
            districtLayout.isErrorEnabled = true
        }
        if (jobCategoryId.isEmpty()) {
            jobCategoryLayout.error = resources.getString(R.string.job_category_error)
            jobCategoryLayout.isErrorEnabled = true
        }
        return !deadlineEt.isErrorEnabled &&
                !titleEt.isErrorEnabled &&
                !salaryEt.isErrorEnabled &&
                !workingTimeEt.isErrorEnabled &&
                !workingScheduleEt.isErrorEnabled &&
                !tgUserNameEt.isErrorEnabled &&
                !jobCategoryLayout.isErrorEnabled &&
                !districtLayout.isErrorEnabled
    }
}