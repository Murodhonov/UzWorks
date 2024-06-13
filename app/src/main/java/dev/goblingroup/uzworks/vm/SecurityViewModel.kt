package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.LanguageEnum
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val securityRepository: SecurityRepository
) : ViewModel() {

    fun logout() = securityRepository.logout()

    fun getLanguageCode() = securityRepository.getLanguageCode()

    fun setLanguageCode(languageCode: String?) {
        if (languageCode == null) securityRepository.setLanguageCode(LanguageEnum.KIRILL_UZB.code)
        else securityRepository.setLanguageCode(languageCode)
    }

    fun isEmployee() = securityRepository.isEmployee()

    fun isEmployer() = securityRepository.isEmployer()

}