package dev.goblingroup.uzworks.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.LanguageDialogItemBinding
import java.util.concurrent.atomic.AtomicReference

fun languageDialog(
    currentLanguageCode: String?,
    context: Context,
    layoutInflater: LayoutInflater,
    listener: LanguageSelectionListener
) {
    AtomicReference<String?>()
    var selectedLanguageCode: String? = currentLanguageCode
    var selectedLanguageName: String? = when (currentLanguageCode) {
        LanguageEnum.ENGLISH.code -> {
            LanguageEnum.ENGLISH.languageName
        }

        LanguageEnum.RUSSIAN.code -> {
            LanguageEnum.RUSSIAN.languageName
        }

        LanguageEnum.KIRILL_UZB.code -> {
            LanguageEnum.KIRILL_UZB.languageName
        }

        LanguageEnum.LATIN_UZB.code -> {
            LanguageEnum.LATIN_UZB.languageName
        }

        else -> {
            null
        }
    }
    val builder = AlertDialog.Builder(context)
    val alertDialog = builder.create()
    val binding = LanguageDialogItemBinding.inflate(layoutInflater)
    binding.apply {
        alertDialog.setView(root)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        updateSelection(binding, null, currentLanguageCode)

        kirillUzbBtn.setOnClickListener {
            updateSelection(binding, selectedLanguageCode, LanguageEnum.KIRILL_UZB.code)
            selectedLanguageCode = LanguageEnum.KIRILL_UZB.code
            selectedLanguageName = LanguageEnum.KIRILL_UZB.languageName
        }

        latinUzbBtn.setOnClickListener {
            updateSelection(binding, selectedLanguageCode, LanguageEnum.LATIN_UZB.code)
            selectedLanguageCode = LanguageEnum.LATIN_UZB.code
            selectedLanguageName = LanguageEnum.LATIN_UZB.languageName
        }

        ruBtn.setOnClickListener {
            updateSelection(binding, selectedLanguageCode, LanguageEnum.RUSSIAN.code)
            selectedLanguageCode = LanguageEnum.RUSSIAN.code
            selectedLanguageName = LanguageEnum.RUSSIAN.languageName
        }

        enBtn.setOnClickListener {
            updateSelection(binding, selectedLanguageCode, LanguageEnum.ENGLISH.code)
            selectedLanguageCode = LanguageEnum.ENGLISH.code
            selectedLanguageName = LanguageEnum.ENGLISH.languageName
        }

        saveBtn.setOnClickListener {
            listener.onLanguageSelected(selectedLanguageCode, selectedLanguageName)
            alertDialog.dismiss()
        }

        cancelBtn.setOnClickListener {
            listener.onLanguageSelected(null, null)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}

private fun updateSelection(
    binding: LanguageDialogItemBinding,
    languageFrom: String?,
    languageTo: String?
) {
    binding.apply {
        when (languageFrom) {
            LanguageEnum.KIRILL_UZB.code -> {
                kirillUzbLayout.background = null
                val marginLayoutParams = kirillUzbFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(0, 0, 0, 0)
                kirillUzbFlag.layoutParams = marginLayoutParams
                kirillUzbSelect.visibility = View.GONE
            }

            LanguageEnum.LATIN_UZB.code -> {
                latinUzbLayout.background = null
                val marginLayoutParams = latinUzbFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(0, 0, 0, 0)
                latinUzbFlag.layoutParams = marginLayoutParams
                latinUzbSelect.visibility = View.GONE
            }

            LanguageEnum.RUSSIAN.code -> {
                ruLayout.background = null
                val marginLayoutParams = ruFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(0, 0, 0, 0)
                ruFlag.layoutParams = marginLayoutParams
                ruSelect.visibility = View.GONE
            }

            LanguageEnum.ENGLISH.code -> {
                enLayout.background = null
                val marginLayoutParams = enFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(0, 0, 0, 0)
                enFlag.layoutParams = marginLayoutParams
                enSelect.visibility = View.GONE
            }
        }
        when (languageTo) {
            LanguageEnum.KIRILL_UZB.code -> {
                kirillUzbLayout.setBackgroundResource(R.drawable.corner_dash_stroke_rectangle)
                val marginLayoutParams = kirillUzbFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(10, 0, 0, 0)
                kirillUzbFlag.layoutParams = marginLayoutParams
                kirillUzbSelect.visibility = View.VISIBLE
            }

            LanguageEnum.LATIN_UZB.code -> {
                latinUzbLayout.setBackgroundResource(R.drawable.corner_dash_stroke_rectangle)
                val marginLayoutParams = latinUzbFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(10, 0, 0, 0)
                latinUzbFlag.layoutParams = marginLayoutParams
                latinUzbSelect.visibility = View.VISIBLE
            }

            LanguageEnum.RUSSIAN.code -> {
                ruLayout.setBackgroundResource(R.drawable.corner_dash_stroke_rectangle)
                val marginLayoutParams = ruFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(10, 0, 0, 0)
                ruFlag.layoutParams = marginLayoutParams
                ruSelect.visibility = View.VISIBLE
            }

            LanguageEnum.ENGLISH.code -> {
                enLayout.setBackgroundResource(R.drawable.corner_dash_stroke_rectangle)
                val marginLayoutParams = enFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(10, 0, 0, 0)
                enFlag.layoutParams = marginLayoutParams
                enSelect.visibility = View.VISIBLE
            }
        }
    }
}