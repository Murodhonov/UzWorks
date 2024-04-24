package dev.goblingroup.uzworks.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.MainActivity
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentSettingsBinding
import dev.goblingroup.uzworks.databinding.LogoutDialogItemBinding
import dev.goblingroup.uzworks.utils.LanguageManager
import dev.goblingroup.uzworks.utils.LanguageSelectionListener
import dev.goblingroup.uzworks.utils.languageDialog
import dev.goblingroup.uzworks.utils.turnSwitchOff
import dev.goblingroup.uzworks.utils.turnSwitchOn
import dev.goblingroup.uzworks.vm.SecurityViewModel
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
                languageDialog(
                    securityViewModel.getLanguageCode(),
                    requireContext(),
                    layoutInflater,
                    object : LanguageSelectionListener {
                        override fun onLanguageSelected(
                            languageCode: String?,
                            languageName: String?
                        ) {
                            securityViewModel.setLanguageCode(languageCode)
                            LanguageManager.setLanguage(languageCode.toString(), requireContext())
                            updateTexts()
                        }

                        override fun onCanceled() {

                        }
                    })
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