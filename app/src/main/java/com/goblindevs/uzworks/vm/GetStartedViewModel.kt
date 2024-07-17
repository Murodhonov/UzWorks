package com.goblindevs.uzworks.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import com.goblindevs.uzworks.repository.SecurityRepository
import com.goblindevs.uzworks.utils.ConstValues.TAG
import com.goblindevs.uzworks.utils.LanguageEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GetStartedViewModel @Inject constructor(
    private val securityRepository: SecurityRepository
) : ViewModel() {

    var selectedLanguageCode: String? = null

    init {
        val languageCode = securityRepository.getLanguageCode()
        if (languageCode == null) {
            Log.d(TAG, "languageCode $languageCode is null: ")
            securityRepository.setLanguageCode(LanguageEnum.LATIN_UZB.code)
        } else
            Log.d(TAG, "languageCode $languageCode is not null: ")

    }

    fun getLanguageCode() = securityRepository.getLanguageCode()

    fun setLanguageCode(languageCode: String?) {
        if (languageCode == null) securityRepository.setLanguageCode(LanguageEnum.KIRILL_UZB.code)
        else securityRepository.setLanguageCode(languageCode)
    }

}