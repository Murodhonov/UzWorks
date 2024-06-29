package com.goblindevs.uzworks.fragments.onboarding

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.databinding.FragmentGetStartedBinding
import com.goblindevs.uzworks.utils.LanguageEnum
import com.goblindevs.uzworks.utils.LanguageManager
import com.goblindevs.uzworks.utils.LanguageSelectionListener
import com.goblindevs.uzworks.vm.GetStartedViewModel

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
        getStartedViewModel.chooseLanguage(
            requireContext(),
            layoutInflater,
            object : LanguageSelectionListener {
                override fun onLanguageSelected(
                    languageCode: String?,
                    languageName: String?
                ) {
                    binding.languageTv.text = languageName
                    updateTexts()
                }

                override fun onCanceled() {

                }

            }
        )
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