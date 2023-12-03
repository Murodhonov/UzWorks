package dev.goblingroup.uzworks.utils

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapters.rv_adapters.ExperienceAdapter
import dev.goblingroup.uzworks.databinding.AboutDialogItemBinding
import dev.goblingroup.uzworks.databinding.ExperienceDialogItemBinding
import dev.goblingroup.uzworks.databinding.FieldsDialogItemBinding
import dev.goblingroup.uzworks.databinding.LanguageDialogItemBinding
import dev.goblingroup.uzworks.databinding.PersonalInfoDialogItemBinding
import java.util.concurrent.atomic.AtomicReference

const val KIRILL_UZB = "Ўзбекча"
const val LATIN_UZB = "O’zbekcha"
const val RU = "Russian"
const val EN = "English"

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
        updateSelection(binding, null, KIRILL_UZB)

        kirillUzbBtn.setOnClickListener {
            updateSelection(binding, selectedLanguage, KIRILL_UZB)
            selectedLanguage = KIRILL_UZB
        }

        latinUzbBtn.setOnClickListener {
            updateSelection(binding, selectedLanguage, LATIN_UZB)
            selectedLanguage = LATIN_UZB
        }

        ruBtn.setOnClickListener {
            updateSelection(binding, selectedLanguage, RU)
            selectedLanguage = RU
        }

        enBtn.setOnClickListener {
            updateSelection(binding, selectedLanguage, EN)
            selectedLanguage = EN
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
            KIRILL_UZB -> {
                kirillUzbLayout.background = null
                val marginLayoutParams = kirillUzbFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(0, 0, 0, 0)
                kirillUzbFlag.layoutParams = marginLayoutParams
                kirillUzbSelect.visibility = View.GONE
            }

            LATIN_UZB -> {
                latinUzbLayout.background = null
                val marginLayoutParams = latinUzbFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(0, 0, 0, 0)
                latinUzbFlag.layoutParams = marginLayoutParams
                latinUzbSelect.visibility = View.GONE
            }
            RU -> {
                ruLayout.background = null
                val marginLayoutParams = ruFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(0, 0, 0, 0)
                ruFlag.layoutParams = marginLayoutParams
                ruSelect.visibility = View.GONE
            }

            EN -> {
                enLayout.background = null
                val marginLayoutParams = enFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(0, 0, 0, 0)
                enFlag.layoutParams = marginLayoutParams
                enSelect.visibility = View.GONE
            }
        }
        when (languageTo) {
            KIRILL_UZB -> {
                kirillUzbLayout.setBackgroundResource(R.drawable.corner_dash_stroke_rectangle)
                val marginLayoutParams = kirillUzbFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(10, 0, 0, 0)
                kirillUzbFlag.layoutParams = marginLayoutParams
                kirillUzbSelect.visibility = View.VISIBLE
            }

            LATIN_UZB -> {
                latinUzbLayout.setBackgroundResource(R.drawable.corner_dash_stroke_rectangle)
                val marginLayoutParams = latinUzbFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(10, 0, 0, 0)
                latinUzbFlag.layoutParams = marginLayoutParams
                latinUzbSelect.visibility = View.VISIBLE
            }
            RU -> {
                ruLayout.setBackgroundResource(R.drawable.corner_dash_stroke_rectangle)
                val marginLayoutParams = ruFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(10, 0, 0, 0)
                ruFlag.layoutParams = marginLayoutParams
                ruSelect.visibility = View.VISIBLE
            }
            EN -> {
                enLayout.setBackgroundResource(R.drawable.corner_dash_stroke_rectangle)
                val marginLayoutParams = enFlag.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(10, 0, 0, 0)
                enFlag.layoutParams = marginLayoutParams
                enSelect.visibility = View.VISIBLE
            }
        }
    }
}

fun personalInfoDialog(
    context: Context,
    layoutInflater: LayoutInflater
) {
    val alertDialog = AlertDialog.Builder(context).create()
    val binding = PersonalInfoDialogItemBinding.inflate(layoutInflater)
    binding.apply {
        alertDialog.setView(root)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()
    }
}

fun aboutDialog(
    context: Context,
    layoutInflater: LayoutInflater
) {
    val alertDialog = AlertDialog.Builder(context).create()
    val binding = AboutDialogItemBinding.inflate(layoutInflater)
    binding.apply {
        alertDialog.setView(root)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()
    }
}

fun fieldsDialog(
    context: Context,
    layoutInflater: LayoutInflater,
    resources: Resources
) {
    val alertDialog = AlertDialog.Builder(context).create()
    val binding = FieldsDialogItemBinding.inflate(layoutInflater)
    binding.apply {
        alertDialog.setView(root)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setChipGroup(chipGroup, context, resources)
        alertDialog.show()
    }
}

private fun setChipGroup(
    chipGroup: ChipGroup,
    context: Context,
    resources: Resources
) {
    val list = arrayOf(
        "History",
        "Sport",
        "Art",
        "Entertainment",
        "Outdoor",
        "Music",
        "Social",
        "Nightlife",
        "Concerts",
        "Health",
        "Submarine",
        "Shopping",
        "Walking",
        "Museum",
        "Cinema",
        "Adventure",
        "Animals",
        "Food",
        "Party",
        "Nature",
    )
    for (chipText in list) {
        val chip = Chip(context)
        chip.text = chipText
        chip.setChipBackgroundColorResource(R.color.middle_green)
        chip.setChipStrokeColorResource(R.color.middle_green)
        chip.setTextColor(resources.getColor(R.color.text_color))
        chip.typeface = ResourcesCompat.getFont(context, R.font.dmsans_regular)
        chip.chipCornerRadius = dpToPx(7f)
        chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        chipGroup.addView(chip)
    }
}

fun experienceDialog(
    context: Context,
    layoutInflater: LayoutInflater
) {
    val alertDialog = AlertDialog.Builder(context).create()
    val binding = ExperienceDialogItemBinding.inflate(layoutInflater)
    binding.apply {
        alertDialog.setView(root)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        experienceRv.adapter = ExperienceAdapter()
        alertDialog.show()
    }
}