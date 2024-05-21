package dev.goblingroup.uzworks.vm

import android.content.Context
import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.models.request.ForgotPasswordRequest
import dev.goblingroup.uzworks.models.request.ResetPasswordRequest
import dev.goblingroup.uzworks.repository.AuthRepository
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.utils.formatPhoneNumber
import dev.goblingroup.uzworks.utils.isStrongPassword
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _forgotPasswordLiveData = MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    private val _resetPasswordLiveData = MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    suspend fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): LiveData<ApiStatus<Unit>> {
        if (networkHelper.isNetworkConnected()) {
            val response = authRepository.forgotPassword(forgotPasswordRequest)
            if (response.isSuccessful) {
                _forgotPasswordLiveData.postValue(ApiStatus.Success(response.body()))
            } else {
                _forgotPasswordLiveData.postValue(ApiStatus.Error(Throwable("Error")))
            }
        }
        return _forgotPasswordLiveData
    }

    suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): LiveData<ApiStatus<Unit>> {
        if (networkHelper.isNetworkConnected()) {
            val response = authRepository.resetPassword(resetPasswordRequest)
            if (response.isSuccessful) {
                _resetPasswordLiveData.postValue(ApiStatus.Success(response.body()))
            } else {
                _resetPasswordLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
            }
        }
        return _resetPasswordLiveData
    }

    fun isFormValid(
        requireContext: Context,
        resources: Resources,
        code1: EditText,
        code2: EditText,
        code3: EditText,
        code4: EditText,
        passwordEt: TextInputLayout,
        confirmPasswordEt: TextInputLayout
    ): Boolean {
        if (code1.text.toString().isEmpty() || code2.text.toString()
                .isEmpty() || code3.text.toString().isEmpty() || code4.text.toString().isEmpty()
        ) {
            Toast.makeText(
                requireContext,
                resources.getText(R.string.verification_code_error),
                Toast.LENGTH_SHORT
            ).show()
        }
        if (passwordEt.editText?.text.toString().isStrongPassword()) {
            passwordEt.isErrorEnabled = true
            passwordEt.error = resources.getString(R.string.password_requirements)
        }
        if (confirmPasswordEt.editText?.text.toString() != passwordEt.editText?.text.toString()) {
            confirmPasswordEt.isErrorEnabled = true
            confirmPasswordEt.error = resources.getString(R.string.confirm_password)
        }
        return code1.text.toString().isNotEmpty() && code2.text.toString()
            .isNotEmpty() && code3.text.toString().isNotEmpty() && code4.text.toString()
            .isNotEmpty() && passwordEt.editText?.text.toString()
            .isStrongPassword() && confirmPasswordEt.editText?.text.toString() == passwordEt.editText?.text.toString()
    }

    fun controlPhoneNumber(phoneNumberEt: TextInputLayout) {
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
    }
}