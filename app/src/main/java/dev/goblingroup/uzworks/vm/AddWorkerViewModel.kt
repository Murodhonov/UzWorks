package dev.goblingroup.uzworks.vm

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
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
import dev.goblingroup.uzworks.models.request.WorkerCreateRequest
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.repository.SecuredWorkerRepository
import dev.goblingroup.uzworks.utils.DateEnum
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.clear
import dev.goblingroup.uzworks.utils.extractDateValue
import dev.goblingroup.uzworks.utils.formatPhoneNumber
import dev.goblingroup.uzworks.utils.formatSalary
import dev.goblingroup.uzworks.utils.formatTgUsername
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddWorkerViewModel @Inject constructor(
    private val securedWorkerRepository: SecuredWorkerRepository
) : ViewModel() {

    private val addLiveData = MutableLiveData<ApiStatus<WorkerResponse>>(ApiStatus.Loading())

    var selectedDistrictId = ""
    var selectedCategoryId = ""
    var selectedGender = ""

    fun addWorker(workerCreateRequest: WorkerCreateRequest): LiveData<ApiStatus<WorkerResponse>> {
        viewModelScope.launch {
            val addWorkerResponse = securedWorkerRepository.createWorker(workerCreateRequest)
            if (addWorkerResponse.isSuccessful) {
                addLiveData.postValue(ApiStatus.Success(addWorkerResponse.body()))
            } else {
                addLiveData.postValue(ApiStatus.Error(Throwable(addWorkerResponse.message())))
            }
        }
        return addLiveData
    }

    @SuppressLint("ClickableViewAccessibility")
    fun controlInput(
        context: Context,
        resources: Resources,
        deadlineEt: TextInputLayout,
        birthdayEt: TextInputLayout,
        titleEt: TextInputLayout,
        salaryEt: TextInputLayout,
        genderLayout: GenderChoiceLayoutBinding,
        workingTimeEt: TextInputLayout,
        tgUserNameEt: TextInputLayout,
        phoneNumberEt: TextInputLayout
    ) {
        deadlineEt.clear()
        birthdayEt.clear()

        deadlineEt.editText?.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
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

        birthdayEt.editText?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val datePickerDialog = DatePickerDialog(
                    context,
                    R.style.DatePickerDialogTheme,
                    { _, year, month, dayOfMonth ->
                        val selectedCalendar = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }
                        if (checkBirthdate(selectedCalendar)) {
                            val formatter = SimpleDateFormat(
                                "dd.MM.yyyy", Locale.getDefault()
                            )
                            birthdayEt.isErrorEnabled = false
                            birthdayEt.editText?.setText(formatter.format(selectedCalendar.time))
                        } else {
                            Toast.makeText(
                                context,
                                resources.getString(R.string.unavailable_date),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    birthdayEt.editText?.text.toString()
                        .extractDateValue(DateEnum.YEAR.dateLabel),
                    birthdayEt.editText?.text.toString()
                        .extractDateValue(DateEnum.MONTH.dateLabel),
                    birthdayEt.editText?.text.toString()
                        .extractDateValue(DateEnum.DATE.dateLabel)
                )

                datePickerDialog.show()
            }
            true
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

        phoneNumberEt.editText?.setText(resources.getString(R.string.phone_number_prefix))
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
                    maleStroke.setBackgroundResource(R.drawable.gender_stroke_selected)
                    femaleStroke.setBackgroundResource(R.drawable.gender_stroke_unselected)
                    maleCircle.visibility = View.VISIBLE
                    femaleCircle.visibility = View.GONE
                    maleTv.setTextColor(resources.getColor(R.color.black_blue))
                    femaleTv.setTextColor(resources.getColor(R.color.text_color))
                    maleBtn.strokeColor = resources.getColor(R.color.black_blue)
                    femaleBtn.strokeColor = resources.getColor(R.color.text_color)
                }
            }
            femaleBtn.setOnClickListener {
                if (selectedGender == GenderEnum.MALE.label || selectedGender.isEmpty()) {
                    selectedGender = GenderEnum.FEMALE.label
                    femaleStroke.setBackgroundResource(R.drawable.gender_stroke_selected)
                    maleStroke.setBackgroundResource(R.drawable.gender_stroke_unselected)
                    femaleCircle.visibility = View.VISIBLE
                    maleCircle.visibility = View.GONE
                    femaleTv.setTextColor(resources.getColor(R.color.black_blue))
                    maleTv.setTextColor(resources.getColor(R.color.text_color))
                    femaleBtn.strokeColor = resources.getColor(R.color.black_blue)
                    maleBtn.strokeColor = resources.getColor(R.color.text_color)
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

        tgUserNameEt.editText?.doAfterTextChanged {
            if (it.toString().isNotEmpty()) {
                tgUserNameEt.isErrorEnabled = false
            }
        }

        phoneNumberEt.editText?.doAfterTextChanged {
            if (it.toString().filter { !it.isWhitespace() }.length == 13) {
                phoneNumberEt.isErrorEnabled = false
            }
        }
    }


    private fun checkBirthdate(selectedCalendar: Calendar): Boolean {
        val minimumAge = 16
        val minimumCalendar = Calendar.getInstance().clone() as Calendar
        minimumCalendar.add(Calendar.YEAR, -minimumAge)
        return selectedCalendar.after(minimumCalendar)
    }

    fun isFormValid(
        context: Context,
        resources: Resources,
        deadlineEt: TextInputLayout,
        birthdayEt: TextInputLayout,
        titleEt: TextInputLayout,
        salaryEt: TextInputLayout,
        workingTimeEt: TextInputLayout,
        tgUserNameEt: TextInputLayout,
        phoneNumberEt: TextInputLayout,
        districtLayout: TextInputLayout,
        jobCategoryLayout: TextInputLayout,
    ): Boolean {
        if (deadlineEt.editText?.text.toString().isEmpty()) {
            deadlineEt.error = resources.getString(R.string.deadline_error)
            deadlineEt.isErrorEnabled = true
        }
        if (birthdayEt.editText?.text.toString().isEmpty()) {
            birthdayEt.error = resources.getString(R.string.birthday_error)
            birthdayEt.isErrorEnabled = true
        }
        if (titleEt.editText?.text.toString().isEmpty()) {
            titleEt.error = resources.getString(R.string.title_error)
            titleEt.isErrorEnabled = true
        }
        if (salaryEt.editText?.text.toString().isEmpty()) {
            salaryEt.error = resources.getString(R.string.salary_error)
            salaryEt.isErrorEnabled = true
        }
        if (selectedGender.isEmpty()) {
            Toast.makeText(
                context,
                resources.getString(R.string.confirm_gender),
                Toast.LENGTH_SHORT
            ).show()
        }
        if (workingTimeEt.editText?.text.toString().isEmpty()) {
            workingTimeEt.error = resources.getString(R.string.working_time_error)
            workingTimeEt.isErrorEnabled = true
        }
        if (tgUserNameEt.editText?.text.toString().length <= 1) {
            tgUserNameEt.error = resources.getString(R.string.tg_username_error)
            tgUserNameEt.isErrorEnabled = true
        }
        if (phoneNumberEt.editText?.text.toString().filter { !it.isWhitespace() }.length != 13) {
            phoneNumberEt.error = resources.getString(R.string.phone_number_error)
            phoneNumberEt.isErrorEnabled = true
        }
        if (selectedDistrictId.isEmpty()) {
            districtLayout.error = resources.getString(R.string.district_error)
            districtLayout.isErrorEnabled = true
            districtLayout.endIconMode = TextInputLayout.END_ICON_NONE
        }
        if (selectedCategoryId.isEmpty()) {
            jobCategoryLayout.error = resources.getString(R.string.job_category_error)
//            jobCategoryLayout.isErrorEnabled = true
            districtLayout.endIconMode = TextInputLayout.END_ICON_NONE
        }
        return !deadlineEt.isErrorEnabled &&
                !birthdayEt.isErrorEnabled &&
                !titleEt.isErrorEnabled &&
                !salaryEt.isErrorEnabled &&
                !workingTimeEt.isErrorEnabled &&
                !tgUserNameEt.isErrorEnabled &&
                !phoneNumberEt.isErrorEnabled &&
                selectedGender.isNotEmpty() &&
                selectedCategoryId.isNotEmpty()
    }

}