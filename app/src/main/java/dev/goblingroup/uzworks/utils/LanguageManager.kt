package dev.goblingroup.uzworks.utils

import android.content.Context
import java.util.Locale

object LanguageManager {

    fun setLanguage(languageCode: String, context: Context) {
        updateResources(languageCode, context)
    }

    private fun updateResources(languageCode: String, context: Context) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

}