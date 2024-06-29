package com.goblindevs.uzworks.vm

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.res.Resources
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
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.models.request.WorkerCreateRequest
import com.goblindevs.uzworks.models.response.WorkerCreateResponse
import com.goblindevs.uzworks.repository.AnnouncementRepository
import com.goblindevs.uzworks.repository.SecurityRepository
import com.goblindevs.uzworks.utils.ConstValues.TAG
import com.goblindevs.uzworks.utils.DateEnum
import com.goblindevs.uzworks.utils.NetworkHelper
import com.goblindevs.uzworks.utils.extractDateValue
import com.goblindevs.uzworks.utils.extractErrorMessage
import com.goblindevs.uzworks.utils.formatSalary
import com.goblindevs.uzworks.utils.formatTgUsername
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddWorkerViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    securityRepository: SecurityRepository,
    private val networkHelper: NetworkHelper,
) : ViewModel() {

    private val addLiveData = MutableLiveData<ApiStatus<WorkerCreateResponse>>(ApiStatus.Loading())

    var regionId = ""
    var districtId = ""
    var jobCategoryId = ""
    val gender = securityRepository.getGender()
    val birthdate = securityRepository.getBirthdate()
    val phoneNumber = securityRepository.getPhoneNumber().toString()

    fun addWorker(workerCreateRequest: WorkerCreateRequest): LiveData<ApiStatus<WorkerCreateResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val createWorker = announcementRepository.createWorker(workerCreateRequest)
                if (createWorker.isSuccessful) {
                    addLiveData.postValue(ApiStatus.Success(createWorker.body()))
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
        titleEt: TextInputLayout,
        salaryEt: TextInputLayout,
        workingTimeEt: TextInputLayout,
        workingScheduleEt: TextInputLayout,
        tgUserNameEt: TextInputLayout
    ) {
        deadlineEt.editText?.setOnFocusChangeListener { _, hasFocus ->
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
        district: TextView,
        category: TextView,
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
            district.setBackgroundResource(R.drawable.disabled_tv_background)
        }
        if (jobCategoryId.isEmpty()) {
            category.setBackgroundResource(R.drawable.disabled_tv_background)
        }
        return !deadlineEt.isErrorEnabled &&
                !titleEt.isErrorEnabled &&
                !salaryEt.isErrorEnabled &&
                !workingTimeEt.isErrorEnabled &&
                !workingScheduleEt.isErrorEnabled &&
                !tgUserNameEt.isErrorEnabled &&
                jobCategoryId.isNotEmpty() &&
                districtId.isNotEmpty()
    }
}