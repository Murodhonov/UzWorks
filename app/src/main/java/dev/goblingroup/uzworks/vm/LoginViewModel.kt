package dev.goblingroup.uzworks.vm

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.repository.AuthRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.LanguageEnum
import dev.goblingroup.uzworks.utils.LanguageManager
import dev.goblingroup.uzworks.utils.LanguageSelectionListener
import dev.goblingroup.uzworks.utils.isEmpty
import dev.goblingroup.uzworks.utils.languageDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val securityRepository: SecurityRepository
) : ViewModel() {

    private val loginLiveData = MutableLiveData<ApiStatus<LoginResponse>>(ApiStatus.Loading())

    fun login(loginRequest: LoginRequest): LiveData<ApiStatus<LoginResponse>> {
        viewModelScope.launch(Dispatchers.IO) {
            if (loginLiveData.value !is ApiStatus.Loading) {
                loginLiveData.postValue(ApiStatus.Loading())
            }
            val response = authRepository.login(loginRequest)
            if (response.isSuccessful) {
                if (saveAuth(response.body()!!)) {
                    loginLiveData.postValue(
                        ApiStatus.Success(
                            response = response.body()
                        )
                    )
                }
            } else {
                loginLiveData.postValue(ApiStatus.Error(Throwable("Some error in ${this@LoginViewModel::class.java.simpleName}")))
                Log.e(TAG, "login: $response")
                Log.e(TAG, "login: ${response.message()}")
                Log.e(TAG, "login: ${response.code()}")
                Log.e(TAG, "login: ${response.errorBody()}")
            }
        }
        return loginLiveData
    }

    private fun saveAuth(loginResponse: LoginResponse): Boolean {
        val rolesSaved = securityRepository.setUserRoles(loginResponse.roles)
        val tokenSaved = securityRepository.setToken(loginResponse.token)
        val userIdSaved = securityRepository.setUserId(loginResponse.id)
        val tokenExpirationSaved = securityRepository.setExpirationDate(loginResponse.expiration)
        val genderSaved = securityRepository.setGender(loginResponse.gender!!)
        return tokenSaved && userIdSaved && rolesSaved && tokenExpirationSaved && genderSaved
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
        usernameEt: TextInputLayout,
        passwordEt: TextInputLayout,
    ) {
        usernameEt.editText?.addTextChangedListener {
            if (usernameEt.isErrorEnabled && it.toString().isNotEmpty()) {
                usernameEt.isErrorEnabled = false
            }
        }

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
        if (usernameEt.isEmpty()) {
            usernameEt.error = resources.getString(R.string.enter_phone_number)
            usernameEt.isErrorEnabled = true
        }
        if (passwordEt.isEmpty()) {
            passwordEt.error = resources.getString(R.string.enter_password)
            passwordEt.isErrorEnabled = true
        }
        return !usernameEt.isErrorEnabled && !passwordEt.isErrorEnabled
    }
}