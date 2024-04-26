package dev.goblingroup.uzworks.fragments.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentPersonalInfoBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogBinding
import dev.goblingroup.uzworks.models.request.UserUpdateRequest
import dev.goblingroup.uzworks.models.response.UserResponse
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.convertPhoneNumber
import dev.goblingroup.uzworks.utils.dmyToIso
import dev.goblingroup.uzworks.utils.isoToDmy
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.PersonalInfoViewModel
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@AndroidEntryPoint
class PersonalInfoFragment : Fragment() {

    private var _binding: FragmentPersonalInfoBinding? = null
    private val binding get() = _binding!!

    private var userResponse: UserResponse? = null

    private val personalInfoViewModel: PersonalInfoViewModel by viewModels()

    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalInfoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            back.setOnClickListener {
                findNavController().popBackStack()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                userResponse = arguments?.getParcelable("user_response", UserResponse::class.java)
            }
            if (userResponse != null) {
                setData()
            } else {
                personalInfoViewModel.userLiveData.observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "failed to load user data",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(TAG, "onViewCreated: ${it.error.message}")
                        }

                        is ApiStatus.Loading -> {
                            loading()
                        }

                        is ApiStatus.Success -> {
                            userResponse = it.response
                            setData()
                        }
                    }
                }
            }

            personalInfoViewModel.controlInput(
                requireContext(),
                resources,
                firstNameEt,
                lastNameEt,
                emailEt,
                phoneNumberEt,
                genderLayout,
                birthdayEt
            )
        }
    }

    private fun loading() {
        dialog = AlertDialog.Builder(requireContext()).create()
        val itemBinding = LoadingDialogBinding.inflate(layoutInflater)
        dialog.setView(itemBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
    }

    @SuppressLint("ClickableViewAccessibility", "HardwareIds")
    private fun setData() {
        binding.apply {
            dialog.dismiss()

            firstNameEt.editText?.setText(userResponse?.firstName ?: "")
            lastNameEt.editText?.setText(userResponse?.lastName ?: "")

            birthdayEt.editText?.setText(
                if (userResponse?.birthDate?.isoToDmy() == null) resources.getString(R.string.birth_date) else userResponse?.birthDate?.isoToDmy()
            )

            emailEt.editText?.setText(userResponse?.email ?: "")
            phoneNumberEt.editText?.setText(
                userResponse?.phoneNumber?.convertPhoneNumber()
                    ?: resources.getString(R.string.phone_number_prefix)
            )

            personalInfoViewModel.selectGender(userResponse?.gender, genderLayout, resources)

            saveBtn.setOnClickListener {
                if (personalInfoViewModel.isFormValid(
                        resources,
                        firstNameEt,
                        lastNameEt,
                        emailEt,
                        phoneNumberEt,
                        birthdayEt
                    )
                ) {
                    lifecycleScope.launch {
                        personalInfoViewModel.updateUser(
                            UserUpdateRequest(
                                birthDate = birthdayEt.editText?.text.toString().dmyToIso()
                                    .toString(),
                                email = emailEt.editText?.text.toString(),
                                firstName = firstNameEt.editText?.text.toString(),
                                gender = personalInfoViewModel.selectedGender,
                                id = personalInfoViewModel.getUserId(),
                                lastName = lastNameEt.editText?.text.toString(),
                                mobileId = Settings.Secure.getString(
                                    requireContext().contentResolver,
                                    Settings.Secure.ANDROID_ID
                                ),
                                phoneNumber = phoneNumberEt.editText?.text.toString()
                                    .filter { !it.isWhitespace() }
                            )
                        ).observe(viewLifecycleOwner) {
                            when (it) {
                                is ApiStatus.Error -> {
                                    Toast.makeText(
                                        requireContext(),
                                        "something went wrong while updating",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }

                                is ApiStatus.Loading -> {
                                    loading()
                                }

                                is ApiStatus.Success -> {
                                    Toast.makeText(
                                        requireContext(),
                                        resources.getString(R.string.update_succeeded),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    findNavController().popBackStack()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}