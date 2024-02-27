package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.LanguageEnum
import javax.inject.Inject

@HiltViewModel
class GetStartedViewModel @Inject constructor(
    private val securityRepository: SecurityRepository
) : ViewModel() {

    init {
        val languageCode = securityRepository.getLanguageCode()
        if (languageCode == null) {
            Log.d(TAG, "languageCode $languageCode is null: ")
            securityRepository.setLanguageCode(LanguageEnum.LATIN_UZB.code)
        } else
            Log.d(TAG, "languageCode $languageCode is not null: ")

    }

    fun getLanguageCode() = securityRepository.getLanguageCode()

    fun setLanguageCode(languageCode: String) = securityRepository.setLanguageCode(languageCode)

}