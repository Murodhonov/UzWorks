package com.goblindevs.uzworks.vm

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.goblindevs.uzworks.repository.SecurityRepository
import com.goblindevs.uzworks.utils.LanguageEnum
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val securityRepository: SecurityRepository
) : ViewModel() {

    var selectedLanguageCode: String? = null

    fun logout() = securityRepository.logout()

    fun getLanguageCode() = securityRepository.getLanguageCode()

    fun setLanguageCode(languageCode: String?) {
        if (languageCode == null) securityRepository.setLanguageCode(LanguageEnum.KIRILL_UZB.code)
        else securityRepository.setLanguageCode(languageCode)
    }

    fun isEmployee() = securityRepository.isEmployee()

    fun isEmployer() = securityRepository.isEmployer()

}