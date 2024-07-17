package com.goblindevs.uzworks.fragments.profile

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.goblindevs.uzworks.MainActivity
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.databinding.FragmentSettingsBinding
import com.goblindevs.uzworks.databinding.LanguageDialogItemBinding
import com.goblindevs.uzworks.databinding.LogoutDialogItemBinding
import com.goblindevs.uzworks.utils.LanguageEnum
import com.goblindevs.uzworks.utils.LanguageManager
import com.goblindevs.uzworks.utils.turnSwitchOff
import com.goblindevs.uzworks.utils.turnSwitchOn
import com.goblindevs.uzworks.vm.SecurityViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var notificationState = true
    private var isNotificationAnimating = false

    private var themeState = true
    private var isThemeAnimating = false

    private val securityViewModel: SecurityViewModel by viewModels()

    private lateinit var languageDialog: AlertDialog
    private lateinit var languageDialogBinding: LanguageDialogItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            notificationSwitch.setOnClickListener {
                if (!isNotificationAnimating) {
                    if (notificationState) {
                        isNotificationAnimating = true
                        turnSwitchOff(resources, notificationTrack, notificationThumb) {
                            isNotificationAnimating = false
                        }
                    } else {
                        isNotificationAnimating = true
                        turnSwitchOn(resources, notificationTrack, notificationThumb) {
                            isNotificationAnimating = false
                        }
                    }
                    notificationState = !notificationState
                }
            }

            themeSwitch.setOnClickListener {
                if (!isThemeAnimating) {
                    if (themeState) {
                        isThemeAnimating = true
                        turnSwitchOff(resources, themeTrack, themeThumb) {
                            isThemeAnimating = false
                        }
                    } else {
                        isThemeAnimating = true
                        turnSwitchOn(resources, themeTrack, themeThumb) {
                            isThemeAnimating = false
                        }
                    }
                    themeState = !themeState
                }
            }

            languageBtn.setOnClickListener {
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
                    when (securityViewModel.getLanguageCode()) {
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
                                    securityViewModel.selectedLanguageCode =
                                        LanguageEnum.KIRILL_UZB.code
                                }

                                R.id.radio_uz -> {
                                    binding.languageTv.text = LanguageEnum.LATIN_UZB.languageName
                                    securityViewModel.selectedLanguageCode =
                                        LanguageEnum.LATIN_UZB.code
                                }

                                R.id.radio_ru -> {
                                    binding.languageTv.text = LanguageEnum.RUSSIAN.languageName
                                    securityViewModel.selectedLanguageCode =
                                        LanguageEnum.RUSSIAN.code
                                }

                                R.id.radio_en -> {
                                    binding.languageTv.text = LanguageEnum.ENGLISH.languageName
                                    securityViewModel.selectedLanguageCode =
                                        LanguageEnum.ENGLISH.code
                                }
                            }
                        }
                        securityViewModel.setLanguageCode(securityViewModel.selectedLanguageCode)
                        LanguageManager.setLocale(
                            securityViewModel.getLanguageCode().toString(),
                            requireContext()
                        )
                        updateTexts()
                    }

                    cancelBtn.setOnClickListener {
                        languageDialog.dismiss()
                    }
                }
            }

            passwordBtn.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.action_settingsFragment_to_updatePasswordFragment,
                    args = null
                )
            }

            logoutBtn.setOnClickListener {
                showLogoutDialog()
            }

            back.setOnClickListener {
                findNavController().popBackStack()
            }

            saveBtn.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun updateTexts() {
        binding.apply {
            settingsTv.text = resources.getString(R.string.settings)
            notificationTv.text = resources.getString(R.string.notifications)
            themeModeTv.text = resources.getString(R.string.dark_mode)
            languageTv.text = resources.getString(R.string.language)
            passwordTv.text = resources.getString(R.string.password)
            logoutTv.text = resources.getString(R.string.logout)
            saveBtn.text = resources.getString(R.string.save)
            val mainActivity = activity as MainActivity
            mainActivity.recreate()
        }
    }

    private fun showLogoutDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val logoutBinding = LogoutDialogItemBinding.inflate(layoutInflater)
        logoutBinding.apply {
            bottomSheetDialog.setContentView(root)
            yesBtn.setOnClickListener {
                bottomSheetDialog.dismiss()
                logout()
            }

            cancelBtn.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.show()
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            if (securityViewModel.logout()) {
                findNavController().navigate(R.id.getStartedFragment)
            } else {
                Snackbar.make(
                    binding.root,
                    "Couldn't logout",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}