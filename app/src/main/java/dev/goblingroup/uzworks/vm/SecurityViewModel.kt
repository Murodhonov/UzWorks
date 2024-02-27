package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.repository.SecurityRepository
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val securityRepository: SecurityRepository
) : ViewModel() {

    fun getUserRoles() = securityRepository.getUserRoles()

    fun deleteUser() = securityRepository.deleteUser()

    fun getLanguageCode() = securityRepository.getLanguageCode()

    fun setLanguageCode(languageCode: String) = securityRepository.setLanguageCode(languageCode)

}