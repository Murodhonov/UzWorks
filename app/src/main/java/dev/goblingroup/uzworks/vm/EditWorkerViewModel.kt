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
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.models.request.WorkerEditRequest
import dev.goblingroup.uzworks.models.response.District
import dev.goblingroup.uzworks.models.response.WorkerEditResponse
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.repository.AnnouncementRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues
import dev.goblingroup.uzworks.utils.ConstValues.DEFAULT_BIRTHDAY
import dev.goblingroup.uzworks.utils.DateEnum
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.utils.dmyToIso
import dev.goblingroup.uzworks.utils.extractDateValue
import dev.goblingroup.uzworks.utils.extractErrorMessage
import dev.goblingroup.uzworks.utils.formatSalary
import dev.goblingroup.uzworks.utils.formatTgUsername
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EditWorkerViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val securityRepository: SecurityRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val workerLiveData = MutableLiveData<ApiStatus<WorkerResponse>>(ApiStatus.Loading())

    private val editLiveData = MutableLiveData<ApiStatus<WorkerEditResponse>>(ApiStatus.Loading())

    lateinit var workerId: String
    private lateinit var workerResponse: WorkerResponse
    var regionId: String? = null
    var districtId: String? = null
    var categoryId: String? = null
    val birthdate = securityRepository.getBirthdate()
    val gender = securityRepository.getGender()
    val phoneNumber = securityRepository.getPhoneNumber().toString()

    fun fetchWorker(): LiveData<ApiStatus<WorkerResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = announcementRepository.getWorkerById(workerId)
                if (response.isSuccessful) {
                    workerResponse = response.body()!!
                    regionId = workerResponse.district.region.id
                    districtId = workerResponse.district.id
                    categoryId = workerResponse.jobCategory.id
                    workerLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    workerLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            }
        }
        return workerLiveData
    }

    fun editWorker(
        context: Context,
        titleEt: TextInputLayout,
        salaryEt: TextInputLayout,
        workingTimeEt: TextInputLayout,
        workingScheduleEt: TextInputLayout,
        tgUserNameEt: TextInputLayout,
        deadlineEt: TextInputLayout,
        birthdayEt: TextInputLayout,
        district: TextView,
        category: TextView,
    ): LiveData<ApiStatus<WorkerEditResponse>> {
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
                if (districtId == null) {
                    district.setBackgroundResource(R.drawable.disabled_tv_background)
                }
                if (categoryId == null) {
                    category.setBackgroundResource(R.drawable.disabled_tv_background)
                }
                if (deadlineEt.isErrorEnabled ||
                    titleEt.isErrorEnabled ||
                    salaryEt.isErrorEnabled ||
                    workingTimeEt.isErrorEnabled ||
                    workingScheduleEt.isErrorEnabled ||
                    tgUserNameEt.isErrorEnabled ||
                    districtId == null ||
                    categoryId == null
                ) {
                    editLiveData.postValue(ApiStatus.Error(Throwable(ConstValues.INPUT_ERROR)))
                } else {
                    val response = announcementRepository.editWorker(
                        WorkerEditRequest(
                            categoryId = categoryId.toString(),
                            deadline = deadlineEt.editText?.text.toString().dmyToIso().toString(),
                            districtId = districtId.toString(),
                            gender = gender,
                            id = workerId,
                            instagramLink = workerResponse.instagramLink,
                            phoneNumber = securityRepository.getPhoneNumber().toString(),
                            salary = salaryEt.editText?.text.toString().trim()
                                .filter { !it.isWhitespace() }.toInt(),
                            telegramLink = workerResponse.telegramLink,
                            tgUserName = tgUserNameEt.editText?.text.toString().trim().substring(1),
                            title = titleEt.editText?.text.toString(),
                            workingSchedule = workingScheduleEt.editText?.text.toString(),
                            workingTime = workingTimeEt.editText?.text.toString(),
                            birthDate = if (birthdayEt.editText?.text.toString()
                                    .isEmpty()
                            ) DEFAULT_BIRTHDAY else birthdayEt.editText?.text.toString().dmyToIso()
                                .toString()
                        )
                    )
                    if (response.isSuccessful) {
                        editLiveData.postValue(ApiStatus.Success(response.body()))
                    } else {
                        editLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                        Log.e(ConstValues.TAG, "editJob: ${response.code()}")
                        Log.e(
                            ConstValues.TAG,
                            "editJob: ${response.errorBody()?.extractErrorMessage()}"
                        )
                    }
                }
            }
        }
        return editLiveData
    }

    fun controlInput(
        fragmentActivity: FragmentActivity,
        titleEt: TextInputLayout,
        salaryEt: TextInputLayout,
        workingTimeEt: TextInputLayout,
        workingScheduleEt: TextInputLayout,
        tgUserNameEt: TextInputLayout,
        deadlineEt: TextInputLayout
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
    }
}