package dev.goblingroup.uzworks.fragments.main.profile.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentSettingsBinding
import dev.goblingroup.uzworks.databinding.LogoutDialogItemBinding
import dev.goblingroup.uzworks.utils.LanguageSelectionListener
import dev.goblingroup.uzworks.utils.extensions.turnSwitchOff
import dev.goblingroup.uzworks.utils.extensions.turnSwitchOn
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.utils.languageDialog

class SettingsFragment : Fragment() {

    private val TAG = "SettingsFragment"

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var notificationState = true
    private var isNotificationAnimating = false

    private var themeState = true
    private var isThemeAnimating = false

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
                    requireContext(),
                    layoutInflater,
                    object : LanguageSelectionListener {
                        override fun onLanguageSelected(language: String?) {
                            Log.d(TAG, "onLanguageSelected: language $language")
                        }
                    })
            }

            passwordBtn.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.updatePasswordFragment,
                    args = null,
                    navOptions = getNavOptions()
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
        findNavController().navigate(R.id.getStartedFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}