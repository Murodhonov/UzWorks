package dev.goblingroup.uzworks.utils

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import java.util.Locale

object LanguageManager {

    fun setLanguage(language: String, context: Context) {
        updateResources(language, context)
    }

    private fun updateResources(language: String, context: Context) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

}