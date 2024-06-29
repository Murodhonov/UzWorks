package com.goblindevs.uzworks.vm

import android.content.Context
import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.models.request.SignUpRequest
import com.goblindevs.uzworks.models.request.VerifyPhoneRequest
import com.goblindevs.uzworks.models.response.ErrorResponse
import com.goblindevs.uzworks.models.response.SignUpResponse
import com.goblindevs.uzworks.repository.AuthRepository
import com.goblindevs.uzworks.utils.NetworkHelper
import com.goblindevs.uzworks.utils.UserRole
import com.goblindevs.uzworks.utils.extractErrorMessage
import com.goblindevs.uzworks.utils.formatPhoneNumber
import com.goblindevs.uzworks.utils.isStrongPassword
import com.goblindevs.uzworks.utils.splitFullName
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedSignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _signUpLiveData =
        MutableLiveData<AuthApiStatus<SignUpResponse>>(AuthApiStatus.Loading())

    private val _verifyPhoneLiveData = MutableLiveData<AuthApiStatus<Unit>>(AuthApiStatus.Loading())

    private val _fullName = MutableLiveData("")
    val fullName get() = _fullName

    private val _phoneNumber = MutableLiveData("")
    val phoneNumber get() = _phoneNumber

    private val _password = MutableLiveData("")
    val password get() = _password

    private val _confirmPassword = MutableLiveData("")
    val confirmPassword get() = _confirmPassword

    private val _selectedRole = MutableLiveData("")
    val selectedRole get() = _selectedRole

    suspend fun signup(signupRequest: SignUpRequest): LiveData<AuthApiStatus<SignUpResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = authRepository.signup(signupRequest)
                if (response.isSuccessful) {
                    _signUpLiveData.postValue(AuthApiStatus.Success(response.body()))
                } else {
                    _signUpLiveData.postValue(
                        AuthApiStatus.Error(
                            ErrorResponse(
                                response.code(),
                                response.errorBody()?.extractErrorMessage().toString()
                            )
                        )
                    )
                }
            }
        }
        return _signUpLiveData
    }

    suspend fun verifyPhone(verifyPhoneRequest: VerifyPhoneRequest): LiveData<AuthApiStatus<Unit>> {
        if (networkHelper.isNetworkConnected()) {
            val response = authRepository.verifyPhone(verifyPhoneRequest)
            if (response.isSuccessful) {
                _verifyPhoneLiveData.postValue(AuthApiStatus.Success(response.body()))
            } else {
                _verifyPhoneLiveData.postValue(
                    AuthApiStatus.Error(
                        ErrorResponse(
                            code = response.code(),
                            message = response.errorBody()?.extractErrorMessage().toString()
                        )
                    )
                )
            }
        }
        return _verifyPhoneLiveData
    }

    fun isFormValid(
        fullNameEt: TextInputLayout,
        usernameEt: TextInputLayout,
        passwordEt: TextInputLayout,
        confirmPasswordEt: TextInputLayout,
        resources: Resources
    ): Boolean {
        var validation = true
        val (firstName, lastName) = fullNameEt.editText?.text.toString().splitFullName()
        if (firstName == null && lastName == null) {
            fullNameEt.isErrorEnabled = true
            fullNameEt.error = resources.getString(R.string.enter_full_name)
            validation = false
        }
        if (usernameEt.editText?.text.toString().isEmpty()) {
            usernameEt.isErrorEnabled = true
            usernameEt.error = resources.getString(R.string.enter_phone_number)
            validation = false
        }
        if (passwordEt.editText?.text.toString().isEmpty()) {
            passwordEt.isErrorEnabled = true
            passwordEt.error = resources.getString(R.string.enter_password)
            validation = false
        } else {
            if (!passwordEt.editText?.text.toString().isStrongPassword()) {
                passwordEt.isErrorEnabled = true
                passwordEt.error = resources.getString(R.string.password_requirements)
                validation = false
            }
        }
        if (confirmPasswordEt.editText?.text.toString().isEmpty() ||
            confirmPasswordEt.editText?.text.toString() != passwordEt.editText?.text.toString()
        ) {
            confirmPasswordEt.isErrorEnabled = true
            confirmPasswordEt.error = resources.getString(R.string.confirm_password)
            validation = false
        }
        return validation
    }

    fun controlInput(
        fullNameEt: TextInputLayout,
        phoneNumberEt: TextInputLayout,
        passwordEt: TextInputLayout,
        confirmPasswordEt: TextInputLayout,
        motionLayout: MotionLayout
    ) {
        fullNameEt.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                motionLayout.setTransitionDuration(500)
                motionLayout.transitionToEnd()
                motionLayout.setTransitionDuration(1000)
            }
        }
        phoneNumberEt.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                motionLayout.setTransitionDuration(500)
                motionLayout.transitionToEnd()
                motionLayout.setTransitionDuration(1000)
            }
        }
        passwordEt.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                motionLayout.setTransitionDuration(500)
                motionLayout.transitionToEnd()
                motionLayout.setTransitionDuration(1000)
            }
        }
        confirmPasswordEt.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                motionLayout.setTransitionDuration(500)
                motionLayout.transitionToEnd()
                motionLayout.setTransitionDuration(1000)
            }
        }

        fullNameEt.editText?.addTextChangedListener {
            if (fullNameEt.isErrorEnabled && it.toString().isNotEmpty()) {
                fullNameEt.isErrorEnabled = false
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

                isFormatting = false
            }
        })

        passwordEt.editText?.addTextChangedListener {
            if (passwordEt.isErrorEnabled && it.toString().isNotEmpty()) {
                passwordEt.isErrorEnabled = false
            }
        }

        confirmPasswordEt.editText?.addTextChangedListener {
            if (confirmPasswordEt.isErrorEnabled && it.toString()
                    .isNotEmpty()
            ) {
                confirmPasswordEt.isErrorEnabled = false
            }
        }
    }

    fun setFullName(fullName: String) {
        _fullName.value = fullName
    }

    fun setPhoneNumber(username: String) {
        _phoneNumber.value = username
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun setConfirmPassword(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
    }

    fun setSelectedRole(selectedRole: String) {
        _selectedRole.value = selectedRole
    }

    fun isEmployer() =
        selectedRole.value == UserRole.EMPLOYER.roleName || selectedRole.value.toString().isEmpty()

    fun isEmployee() =
        selectedRole.value == UserRole.EMPLOYEE.roleName || selectedRole.value.toString().isEmpty()

    fun isRoleSelected() = selectedRole.value.toString().isNotEmpty()
    fun isCodeValid(
        context: Context,
        resources: Resources,
        code1: EditText,
        code2: EditText,
        code3: EditText,
        code4: EditText
    ): Boolean {
        if (code1.text.isEmpty() || code2.text.isEmpty() || code3.text.isEmpty() || code4.text.isEmpty()) {
            Toast.makeText(
                context,
                resources.getString(R.string.verification_code_error),
                Toast.LENGTH_SHORT
            ).show()
        }
        return code1.text.isNotEmpty() && code2.text.isNotEmpty() && code3.text.isNotEmpty() && code4.text.isNotEmpty()
    }

}