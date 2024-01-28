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
    context: Context,
    layoutInflater: LayoutInflater,
    listener: LanguageSelectionListener
) {
    AtomicReference<String?>()
    var selectedLanguage: String? = "Ўзбекча"

    val builder = AlertDialog.Builder(context)
    val alertDialog = builder.create()
    val binding = LanguageDialogItemBinding.inflate(layoutInflater)
    binding.apply {
        alertDialog.setView(root)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        updateSelection(binding, null, LanguageEnum.KIRILL_UZB.language)

        kirillUzbBtn.setOnClickListener {
            updateSelection(binding, selectedLanguage, LanguageEnum.KIRILL_UZB.language)
            selectedLanguage = LanguageEnum.KIRILL_UZB.language
        }

        latinUzbBtn.setOnClickListener {
            updateSelection(binding, selectedLanguage, LanguageEnum.LATIN_UZB.language)
            selectedLanguage = LanguageEnum.LATIN_UZB.language
        }

        ruBtn.setOnClickListener {
            updateSelection(binding, selectedLanguage, LanguageEnum.RU.language)
            selectedLanguage = LanguageEnum.RU.language
        }

        enBtn.setOnClickListener {
            updateSelection(binding, selectedLanguage, LanguageEnum.EN.language)
            selectedLanguage = LanguageEnum.EN.language
        }

        saveBtn.setOnClickListener {
            listener.onLanguageSelected(selectedLanguage)
            alertDialog.dismiss()
        }

        cancelBtn.setOnClickListener {
            listener.onLanguageSelected(null)
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
            LanguageEnum.KIRILL_UZB.language -> {
                kirillUzbLayout.background = null
                val marginLayoutParams = kirillUzbFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(0, 0, 0, 0)
                kirillUzbFlag.layoutParams = marginLayoutParams
                kirillUzbSelect.visibility = View.GONE
            }

            LanguageEnum.LATIN_UZB.language -> {
                latinUzbLayout.background = null
                val marginLayoutParams = latinUzbFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(0, 0, 0, 0)
                latinUzbFlag.layoutParams = marginLayoutParams
                latinUzbSelect.visibility = View.GONE
            }
            LanguageEnum.RU.language -> {
                ruLayout.background = null
                val marginLayoutParams = ruFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(0, 0, 0, 0)
                ruFlag.layoutParams = marginLayoutParams
                ruSelect.visibility = View.GONE
            }

            LanguageEnum.EN.language -> {
                enLayout.background = null
                val marginLayoutParams = enFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(0, 0, 0, 0)
                enFlag.layoutParams = marginLayoutParams
                enSelect.visibility = View.GONE
            }
        }
        when (languageTo) {
            LanguageEnum.KIRILL_UZB.language -> {
                kirillUzbLayout.setBackgroundResource(R.drawable.corner_dash_stroke_rectangle)
                val marginLayoutParams = kirillUzbFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(10, 0, 0, 0)
                kirillUzbFlag.layoutParams = marginLayoutParams
                kirillUzbSelect.visibility = View.VISIBLE
            }

            LanguageEnum.LATIN_UZB.language -> {
                latinUzbLayout.setBackgroundResource(R.drawable.corner_dash_stroke_rectangle)
                val marginLayoutParams = latinUzbFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(10, 0, 0, 0)
                latinUzbFlag.layoutParams = marginLayoutParams
                latinUzbSelect.visibility = View.VISIBLE
            }
            LanguageEnum.RU.language -> {
                ruLayout.setBackgroundResource(R.drawable.corner_dash_stroke_rectangle)
                val marginLayoutParams = ruFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(10, 0, 0, 0)
                ruFlag.layoutParams = marginLayoutParams
                ruSelect.visibility = View.VISIBLE
            }
            LanguageEnum.EN.language -> {
                enLayout.setBackgroundResource(R.drawable.corner_dash_stroke_rectangle)
                val marginLayoutParams = enFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(10, 0, 0, 0)
                enFlag.layoutParams = marginLayoutParams
                enSelect.visibility = View.VISIBLE
            }
        }
    }
}