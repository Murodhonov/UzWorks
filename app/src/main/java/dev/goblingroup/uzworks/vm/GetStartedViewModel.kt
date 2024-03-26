package dev.goblingroup.uzworks.vm

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.LanguageEnum
import dev.goblingroup.uzworks.utils.LanguageManager
import dev.goblingroup.uzworks.utils.LanguageSelectionListener
import dev.goblingroup.uzworks.utils.languageDialog
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
            object : LanguageSelectionListener {
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

}