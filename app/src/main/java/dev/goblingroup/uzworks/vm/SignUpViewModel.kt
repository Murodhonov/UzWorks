package dev.goblingroup.uzworks.vm

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.models.response.SignUpResponse
import dev.goblingroup.uzworks.repository.AuthRepository
import dev.goblingroup.uzworks.utils.isStrongPassword
import dev.goblingroup.uzworks.utils.splitFullName
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val signUpLiveData =
        MutableLiveData<ApiStatus<SignUpResponse>>(ApiStatus.Loading())

    private val _fullName = MutableLiveData("")
    val fullName get() = _fullName

    private val _username = MutableLiveData("")
    val username get() = _username

    private val _password = MutableLiveData("")
    val password get() = _password

    private val _confirmPassword = MutableLiveData("")
    val confirmPassword get() = _confirmPassword

    fun signup(signupRequest: SignUpRequest): LiveData<ApiStatus<SignUpResponse>> {
        viewModelScope.launch {
            val response = authRepository.signup(signupRequest)
            if (response.isSuccessful) {
                signUpLiveData.postValue(ApiStatus.Success(response.body()))
            } else {
                signUpLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
            }
        }
        return signUpLiveData
    }

    fun isFormValid(
        fullNameEt: TextInputLayout,
        usernameEt: TextInputLayout,
        passwordEt: TextInputLayout,
        confirmPasswordEt: TextInputLayout,
        resources: Resources
    ): Boolean {
        var validation = true
        val (firstName, lastName) = fullNameEt.splitFullName()
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
}