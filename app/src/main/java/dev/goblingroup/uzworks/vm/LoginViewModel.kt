package dev.goblingroup.uzworks.vm

import android.content.Context
import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.request.VerifyPhoneRequest
import dev.goblingroup.uzworks.models.response.ErrorResponse
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.repository.AuthRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.LanguageEnum
import dev.goblingroup.uzworks.utils.LanguageManager
import dev.goblingroup.uzworks.utils.LanguageSelectionListener
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.utils.extractErrorMessage
import dev.goblingroup.uzworks.utils.formatPhoneNumber
import dev.goblingroup.uzworks.utils.isEmpty
import dev.goblingroup.uzworks.utils.languageDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val securityRepository: SecurityRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

//    private val loginLiveData =
//        MutableLiveData<AuthApiStatus<LoginResponse>>(AuthApiStatus.Loading())

//    private val verifyPhoneLiveData = MutableLiveData<AuthApiStatus<Unit>>(AuthApiStatus.Loading())

    private val _phoneNumber = MutableLiveData("")
    val phoneNumber get() = _phoneNumber

    private val _password = MutableLiveData("")
    val password get() = _password

    fun login(loginRequest: LoginRequest): LiveData<AuthApiStatus<LoginResponse>> {
        val loginLiveData =
            MutableLiveData<AuthApiStatus<LoginResponse>>(AuthApiStatus.Loading())

        viewModelScope.launch(Dispatchers.IO) {
            if (networkHelper.isNetworkConnected()) {
                Log.d(TAG, "login: requesting to $loginRequest")
                val response = authRepository.login(loginRequest)
                if (response.isSuccessful) {
                    if (saveAuth(response.body()!!)) {
                        loginLiveData.postValue(
                            AuthApiStatus.Success(
                                response = response.body()
                            )
                        )
                    }
                } else {
                    loginLiveData.postValue(
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
        return loginLiveData
    }

    suspend fun verifyPhone(verifyPhoneRequest: VerifyPhoneRequest): LiveData<AuthApiStatus<Unit>> {
        val verifyPhoneLiveData = MutableLiveData<AuthApiStatus<Unit>>(AuthApiStatus.Loading())
        if (networkHelper.isNetworkConnected()) {
            val response = authRepository.verifyPhone(verifyPhoneRequest)
            if (response.isSuccessful) {
                verifyPhoneLiveData.postValue(AuthApiStatus.Success(response.body()))
            } else {
                verifyPhoneLiveData.postValue(
                    AuthApiStatus.Error(
                        ErrorResponse(
                            response.code(),
                            response.errorBody()?.extractErrorMessage().toString()
                        )
                    )
                )
            }
        }
        return verifyPhoneLiveData
    }

    private fun saveAuth(loginResponse: LoginResponse): Boolean {
        val rolesSaved = securityRepository.setUserRoles(loginResponse.roles)
        val tokenSaved = securityRepository.setToken(loginResponse.token)
        val userIdSaved = securityRepository.setUserId(loginResponse.id)
        val tokenExpirationSaved = securityRepository.setExpirationDate(loginResponse.expiration)
        val genderSaved = securityRepository.setGender(loginResponse.gender!!)
        val birthdateSaved = securityRepository.setBirthdate(loginResponse.birthDate)
        val phoneNumberSaved = securityRepository.setPhoneNumber(loginResponse.phoneNumber)
        return tokenSaved && userIdSaved && rolesSaved && tokenExpirationSaved && genderSaved && birthdateSaved && phoneNumberSaved
    }

    private fun getLanguageCode() = securityRepository.getLanguageCode()

    fun getLanguageName(): String {
        return when (getLanguageCode()) {
            LanguageEnum.LATIN_UZB.code -> {
                LanguageEnum.LATIN_UZB.languageName
            }

            LanguageEnum.KIRILL_UZB.code -> {
                LanguageEnum.KIRILL_UZB.languageName
            }

            LanguageEnum.RUSSIAN.code -> {
                LanguageEnum.RUSSIAN.languageName
            }

            LanguageEnum.ENGLISH.code -> {
                LanguageEnum.ENGLISH.languageName
            }

            else -> {
                ""
            }
        }
    }

    fun setLanguageCode(languageCode: String?) {
        if (languageCode == null) securityRepository.setLanguageCode(LanguageEnum.KIRILL_UZB.code)
        else securityRepository.setLanguageCode(languageCode)
    }

    fun chooseLanguage(
        context: Context,
        layoutInflater: LayoutInflater,
        listener: LanguageSelectionListener
    ) {
        languageDialog(
            currentLanguageCode = getLanguageCode(),
            context = context,
            layoutInflater = layoutInflater,
            listener = object : LanguageSelectionListener {
                override fun onLanguageSelected(languageCode: String?, languageName: String?) {
                    setLanguageCode(languageCode)
                    LanguageManager.setLanguage(languageCode.toString(), context)
                    listener.onLanguageSelected(languageCode, languageName)
                }

                override fun onCanceled() {

                }

            }
        )
    }

    fun controlInput(
        phoneNumberEt: TextInputLayout,
        passwordEt: TextInputLayout,
    ) {
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

        passwordEt.editText?.addTextChangedListener {
            if (passwordEt.isErrorEnabled && it.toString().isNotEmpty()) {
                passwordEt.isErrorEnabled = false
            }
        }
    }

    fun isFormValid(
        usernameEt: TextInputLayout,
        passwordEt: TextInputLayout,
        resources: Resources
    ): Boolean {
        if (usernameEt.editText?.text.toString().trim()
                .filter { !it.isWhitespace() }.length != 13
        ) {
            usernameEt.error = resources.getString(R.string.enter_phone_number)
            usernameEt.isErrorEnabled = true
        }
        if (passwordEt.isEmpty()) {
            passwordEt.error = resources.getString(R.string.enter_password)
            passwordEt.isErrorEnabled = true
        }
        return !usernameEt.isErrorEnabled && !passwordEt.isErrorEnabled
    }

    fun isCodeValid(
        context: Context,
        code1: EditText,
        code2: EditText,
        code3: EditText,
        code4: EditText
    ): Boolean {
        if (code1.text.toString().isEmpty() || code2.text.toString()
                .isEmpty() || code3.text.toString().isEmpty() || code4.text.toString().isEmpty()
        ) {
            Toast.makeText(
                context,
                context.resources.getString(R.string.verification_code_error),
                Toast.LENGTH_SHORT
            ).show()
        }
        return code1.text.toString().isNotEmpty() && code2.text.toString()
            .isNotEmpty() && code3.text.toString().isNotEmpty() && code4.text.toString()
            .isNotEmpty()
    }

    fun setPhoneNumber(phoneNumber: String) {
        _phoneNumber.value = phoneNumber
    }

    fun setPassword(password: String) {
        _password.value = password
    }
}