package dev.goblingroup.uzworks.fragments.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import dev.goblingroup.uzworks.utils.ConstValues.DEFAULT_BIRTHDAY
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.convertPhoneNumber
import dev.goblingroup.uzworks.utils.dmyToIso
import dev.goblingroup.uzworks.utils.isoToDmy
import dev.goblingroup.uzworks.utils.selectFemale
import dev.goblingroup.uzworks.utils.selectMale
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.PersonalInfoViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonalInfoFragment : Fragment() {

    private var _binding: FragmentPersonalInfoBinding? = null
    private val binding get() = _binding!!

    private val personalInfoViewModel: PersonalInfoViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog

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

            cancelBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            personalInfoViewModel.userLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.load_user_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(TAG, "onViewCreated: ${it.error.message}")
                    }

                    is ApiStatus.Loading -> {
                        loading()
                    }

                    is ApiStatus.Success -> {
                        setData(it.response!!)
                    }
                }
            }

            personalInfoViewModel.controlInput(
                requireActivity(),
                firstNameEt,
                lastNameEt,
                emailEt,
                phoneNumberEt,
                genderLayout,
                birthdayEt
            )

            saveBtn.setOnClickListener {
                if (personalInfoViewModel.isFormValid(
                        resources,
                        firstNameEt,
                        lastNameEt,
                        emailEt
                    )
                ) {
                    lifecycleScope.launch {
                        personalInfoViewModel.updateUser(
                            UserUpdateRequest(
                                birthDate = if (birthdayEt.editText?.text.toString()
                                        .isEmpty()
                                ) DEFAULT_BIRTHDAY else birthdayEt.editText?.text.toString()
                                    .dmyToIso().toString(),
                                email = emailEt.editText?.text.toString().ifEmpty {
                                    null
                                },
                                firstName = firstNameEt.editText?.text.toString(),
                                gender = personalInfoViewModel.selectedGender,
                                id = personalInfoViewModel.getUserId(),
                                lastName = lastNameEt.editText?.text.toString(),
                                mobileId = Settings.Secure.getString(
                                    requireContext().contentResolver,
                                    Settings.Secure.ANDROID_ID
                                )
                            )
                        ).observe(viewLifecycleOwner) {
                            when (it) {
                                is ApiStatus.Error -> {
                                    loadingDialog.dismiss()
                                    Toast.makeText(
                                        requireContext(),
                                        resources.getString(R.string.update_user_failed),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }

                                is ApiStatus.Loading -> {
                                    loading()
                                }

                                is ApiStatus.Success -> {
                                    loadingDialog.dismiss()
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

    private fun loading() {
        try {
            loadingDialog.show()
        } catch (e: Exception) {
            loadingDialog = AlertDialog.Builder(requireContext()).create()
            loadingDialog.setView(LoadingDialogBinding.inflate(layoutInflater).root)
            loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loadingDialog.setCancelable(false)
            loadingDialog.show()
        }
    }

    @SuppressLint("ClickableViewAccessibility", "HardwareIds")
    private fun setData(response: UserResponse) {
        binding.apply {
            loadingDialog.dismiss()

            Log.d(TAG, "setData: $response")

            firstNameEt.editText?.setText(response.firstName)
            lastNameEt.editText?.setText(response.lastName)

            if (response.birthDate.isoToDmy() != null && response.birthDate != DEFAULT_BIRTHDAY) {
                birthdayEt.editText?.setText(response.birthDate.isoToDmy())
            }

            emailEt.editText?.setText(response.email)
            phoneNumberEt.editText?.setText(
                response.phoneNumber.convertPhoneNumber()
            )

            when (response.gender) {
                GenderEnum.MALE.code -> {
                    genderLayout.selectMale(resources)
                }

                GenderEnum.FEMALE.code -> {
                    genderLayout.selectFemale(resources)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}