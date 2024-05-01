package dev.goblingroup.uzworks.vm

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
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
import dev.goblingroup.uzworks.models.response.UserResponse
import dev.goblingroup.uzworks.models.response.UserUpdateResponse
import dev.goblingroup.uzworks.repository.ProfileRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.DateEnum
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.utils.extractDateValue
import dev.goblingroup.uzworks.utils.extractErrorMessage
import dev.goblingroup.uzworks.utils.formatPhoneNumber
import dev.goblingroup.uzworks.utils.selectFemale
import dev.goblingroup.uzworks.utils.selectMale
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val securityRepository: SecurityRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _userLiveData = MutableLiveData<ApiStatus<UserResponse>>(ApiStatus.Loading())
    val userLiveData = _userLiveData

    private val updateUserLiveData =
        MutableLiveData<ApiStatus<UserUpdateResponse>>(ApiStatus.Loading())

    var selectedGender: Int = GenderEnum.UNKNOWN.code

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val userResponse = profileRepository.getUserById(securityRepository.getUserId())
                if (userResponse.isSuccessful) {
                    selectedGender = userResponse.body()?.gender!!
                    _userLiveData.postValue(ApiStatus.Success(userResponse.body()))
                } else {
                    _userLiveData.postValue(ApiStatus.Error(Throwable(userResponse.message())))
                    Log.e(TAG, "fetchUserData: ${userResponse.code()}")
                    Log.e(TAG, "fetchUserData: ${userResponse.message()}")
                    Log.e(TAG, "fetchUserData: ${userResponse.errorBody()}")
                    Log.e(
                        TAG,
                        "fetchUserData: ${
                            userResponse.errorBody()?.extractErrorMessage().toString()
                        }"
                    )
                }
            }
        }
    }

    fun updateUser(userUpdateRequest: UserUpdateRequest): LiveData<ApiStatus<UserUpdateResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                Log.d(TAG, "updateUser: updating user $userUpdateRequest")
                val updateUserResponse = profileRepository.updateUser(userUpdateRequest)
                if (updateUserResponse.isSuccessful) {
                    if (updateInfo(updateUserResponse.body()!!)) {
                        updateUserLiveData.postValue(ApiStatus.Success(updateUserResponse.body()))
                    }
                } else {
                    updateUserLiveData.postValue(ApiStatus.Error(Throwable(updateUserResponse.message())))
                    Log.e(
                        TAG,
                        "updateUser: ${updateUserResponse.errorBody()?.extractErrorMessage()}",
                    )
                }
            }
        }
        return updateUserLiveData
    }

    private fun updateInfo(userUpdateResponse: UserUpdateResponse): Boolean {
        return securityRepository.setGender(userUpdateResponse.gender) && securityRepository.setBirthdate(
            userUpdateResponse.birthDate
        )
    }

    fun getUserId() = securityRepository.getUserId()

    @SuppressLint("ClickableViewAccessibility")
    fun controlInput(
        fragmentActivity: FragmentActivity,
        firstNameEt: TextInputLayout,
        lastNameEt: TextInputLayout,
        emailEt: TextInputLayout,
        phoneNumberEt: TextInputLayout,
        genderLayout: GenderChoiceLayoutBinding,
        birthdayEt: TextInputLayout
    ) {
        firstNameEt.editText?.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                firstNameEt.isErrorEnabled = false
            }
        }

        lastNameEt.editText?.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                lastNameEt.isErrorEnabled = false
            }
        }

        emailEt.editText?.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                emailEt.isErrorEnabled = false
            }
        }

        genderLayout.apply {
            maleBtn.setOnClickListener {
                if (selectedGender == GenderEnum.FEMALE.code || selectedGender == GenderEnum.UNKNOWN.code) {
                    selectedGender = GenderEnum.MALE.code
                    selectMale(fragmentActivity.resources)
                }
            }
            femaleBtn.setOnClickListener {
                if (selectedGender == GenderEnum.MALE.code || selectedGender == GenderEnum.UNKNOWN.code) {
                    selectedGender = GenderEnum.FEMALE.code
                    selectFemale(fragmentActivity.resources)
                }
            }
        }

        phoneNumberEt.editText?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                (fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    phoneNumberEt.windowToken,
                    0
                )
                val clipboardManager =
                    fragmentActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData =
                    ClipData.newPlainText("label", phoneNumberEt.editText?.text.toString())
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(
                    fragmentActivity,
                    fragmentActivity.resources.getString(R.string.phone_number_copied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

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
                if (isFormatting) {
                    return
                }

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
                if (formattedPhone.trim()
                        .filter { !it.isWhitespace() }.length == 13 && phoneNumberEt.isErrorEnabled
                ) {
                    phoneNumberEt.isErrorEnabled = false
                }

                isFormatting = false
            }
        })

        birthdayEt.editText?.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
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
            }
            true
        }
    }

    fun isFormValid(
        resources: Resources,
        firstNameEt: TextInputLayout,
        lastNameEt: TextInputLayout,
        emailEt: TextInputLayout
    ): Boolean {
        var result = true
        if (firstNameEt.editText?.text.toString().isEmpty()) {
            result = false
            firstNameEt.isErrorEnabled = true
            firstNameEt.error = resources.getString(R.string.enter_firstname)
        }
        if (lastNameEt.editText?.text.toString().isEmpty()) {
            result = false
            lastNameEt.isErrorEnabled = true
            lastNameEt.error = resources.getString(R.string.enter_lastname)
        }
        if (!isEmailValid(emailEt.editText?.text.toString())) {
            result = false
            emailEt.isErrorEnabled = true
            emailEt.error = resources.getString(R.string.enter_valid_email)
        }
        return result
    }

    private fun isEmailValid(email: String): Boolean {
        if (email.isEmpty()) return true
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

}