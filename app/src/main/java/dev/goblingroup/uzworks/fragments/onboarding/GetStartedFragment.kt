package dev.goblingroup.uzworks.fragments.onboarding

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentGetStartedBinding
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.LanguageEnum
import dev.goblingroup.uzworks.utils.LanguageManager
import dev.goblingroup.uzworks.utils.LanguageSelectionListener
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.utils.languageDialog
import dev.goblingroup.uzworks.vm.GetStartedViewModel

@AndroidEntryPoint
class GetStartedFragment : Fragment() {

    private var _binding: FragmentGetStartedBinding? = null
    private val binding get() = _binding!!

    private val getStartedViewModel: GetStartedViewModel by viewModels()

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
            if (getStartedViewModel.getLanguageCode() != null) {
                LanguageManager.setLanguage(getStartedViewModel.getLanguageCode().toString(), requireContext())
                updateTexts()
            }

            nextBtn.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.loginFragment,
                    args = null,
                    navOptions = getNavOptions()
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
        languageDialog(
            getStartedViewModel.getLanguageCode(),
            requireContext(),
            layoutInflater,
            object : LanguageSelectionListener {
                override fun onLanguageSelected(languageCode: String?, languageName: String?) {
                    binding.languageTv.text = languageName
                    getStartedViewModel.setLanguageCode(languageCode.toString())
                    LanguageManager.setLanguage(languageCode.toString(), requireContext())
                    updateTexts()
                }
            })
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