package com.goblindevs.uzworks.fragments.onboarding

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.databinding.FragmentGetStartedBinding
import com.goblindevs.uzworks.databinding.LanguageDialogItemBinding
import com.goblindevs.uzworks.utils.ConstValues.TAG
import com.goblindevs.uzworks.utils.LanguageEnum
import com.goblindevs.uzworks.utils.LanguageManager
import com.goblindevs.uzworks.vm.GetStartedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GetStartedFragment : Fragment() {

    private var _binding: FragmentGetStartedBinding? = null
    private val binding get() = _binding!!

    private val getStartedViewModel: GetStartedViewModel by viewModels()

    private lateinit var languageDialog: AlertDialog
    private lateinit var languageDialogBinding: LanguageDialogItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGetStartedBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            getStartedTitleTv.text = Html.fromHtml(getString(R.string.get_started_title))

            languageTv.text = when (getStartedViewModel.getLanguageCode()) {
                LanguageEnum.KIRILL_UZB.code -> {
                    LanguageEnum.KIRILL_UZB.languageName
                }

                LanguageEnum.LATIN_UZB.code -> {
                    LanguageEnum.LATIN_UZB.languageName
                }

                LanguageEnum.ENGLISH.code -> {
                    LanguageEnum.ENGLISH.languageName
                }

                LanguageEnum.RUSSIAN.code -> {
                    LanguageEnum.RUSSIAN.languageName
                }

                else -> {
                    ""
                }
            }

            nextBtn.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.action_getStartedFragment_to_loginFragment,
                    args = null
                )
            }

            languageBtn.setOnClickListener {
                chooseLanguage()
            }

            languageTv.setOnClickListener {
                chooseLanguage()
            }
        }
    }

    private fun chooseLanguage() {
        try {
            languageDialog.show()
        } catch (e: Exception) {
            languageDialog = AlertDialog.Builder(requireContext()).create()
            languageDialogBinding = LanguageDialogItemBinding.inflate(layoutInflater)
            languageDialog.setView(languageDialogBinding.root)
            languageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            languageDialog.show()
        }

        languageDialogBinding.apply {
            when (getStartedViewModel.getLanguageCode()) {
                LanguageEnum.KIRILL_UZB.code -> {
                    radioCyr.isChecked = true
                }

                LanguageEnum.LATIN_UZB.code -> {
                    radioUz.isChecked = true
                }

                LanguageEnum.RUSSIAN.code -> {
                    radioRu.isChecked = true
                }

                LanguageEnum.ENGLISH.code -> {
                    radioEn.isChecked = true
                }

                else -> {}
            }

            saveBtn.setOnClickListener {
                if (languageGroup.checkedRadioButtonId != -1) {
                    languageDialog.dismiss()
                    when (languageGroup.checkedRadioButtonId) {
                        R.id.radio_cyr -> {
                            binding.languageTv.text = LanguageEnum.KIRILL_UZB.languageName
                            getStartedViewModel.selectedLanguageCode = LanguageEnum.KIRILL_UZB.code
                        }

                        R.id.radio_uz -> {
                            binding.languageTv.text = LanguageEnum.LATIN_UZB.languageName
                            getStartedViewModel.selectedLanguageCode = LanguageEnum.LATIN_UZB.code
                        }

                        R.id.radio_ru -> {
                            binding.languageTv.text = LanguageEnum.RUSSIAN.languageName
                            getStartedViewModel.selectedLanguageCode = LanguageEnum.RUSSIAN.code
                        }

                        R.id.radio_en -> {
                            binding.languageTv.text = LanguageEnum.ENGLISH.languageName
                            getStartedViewModel.selectedLanguageCode = LanguageEnum.ENGLISH.code
                        }
                    }
                    getStartedViewModel.setLanguageCode(getStartedViewModel.selectedLanguageCode)
                    LanguageManager.setLocale(
                        getStartedViewModel.selectedLanguageCode.toString(),
                        requireContext()
                    )
                    updateTexts()
                }
            }
            cancelBtn.setOnClickListener {
                languageDialog.dismiss()
            }
        }
    }

    private fun updateTexts() {
        binding.apply {
            getStartedTitleTv.text = Html.fromHtml(getString(R.string.get_started_title))
            getStartedBottomTv.text = resources.getString(R.string.get_started_bottom)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}