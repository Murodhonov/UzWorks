package com.goblindevs.uzworks.utils

interface LanguageSelectionListener {
    fun onLanguageSelected(languageCode: String?, languageName: String?)

    fun onCanceled()
}