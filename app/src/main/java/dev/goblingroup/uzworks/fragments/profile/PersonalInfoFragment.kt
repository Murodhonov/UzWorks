package dev.goblingroup.uzworks.fragments.profile

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.RegionEntity
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
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.PersonalInfoViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonalInfoFragment : Fragment() {

    private var _binding: FragmentPersonalInfoBinding? = null
    private val binding get() = _binding!!

    private val personalInfoViewModel: PersonalInfoViewModel by viewModels()
    private val addressViewModel: AddressViewModel by viewModels()

    private lateinit var regionAdapter: ArrayAdapter<String>
    private lateinit var districtAdapter: ArrayAdapter<String>

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
            toolbar.setNavigationOnClickListener {
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

            regionChoice.setOnClickListener {
                addressViewModel.regionLiveData.observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "failed to load regions",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is ApiStatus.Loading -> {
                            regionProgress.visibility = View.VISIBLE
                            regionLayout.endIconMode = TextInputLayout.END_ICON_NONE
                        }

                        is ApiStatus.Success -> {
                            regionProgress.visibility = View.GONE
                            regionLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU
                            setRegions(it.response!!)
                        }
                    }
                }
            }

            regionChoice.setOnItemClickListener { parent, view, position, id ->
                districtChoice.text.clear()
                districtChoice.hint = getString(R.string.select_district)
                setDistricts(parent.getItemAtPosition(position) as String)
            }

            districtChoice.setOnClickListener {
                if (regionChoice.text.toString().isNotEmpty()) {
                    addressViewModel.regionLiveData.observe(viewLifecycleOwner) {
                        when (it) {
                            is ApiStatus.Error -> {

                            }

                            is ApiStatus.Loading -> {

                            }

                            is ApiStatus.Success -> {
                                setDistricts(regionChoice.text.toString())
                            }
                        }
                    }
                }
            }

            saveBtn.setOnClickListener {
                if (personalInfoViewModel.isFormValid(
                        resources,
                        firstNameEt,
                        lastNameEt,
                        emailEt
                    )
                ) {
                    lifecycleScope.launch {
                        Log.d(
                            TAG,
                            "onViewCreated: selecting ${regionChoice.text} as region and ${districtChoice.text} as district"
                        )
                        personalInfoViewModel.updateUser(
                            UserUpdateRequest(
                                birthDate = if (birthdayEt.editText?.text.toString()
                                        .isEmpty()
                                ) DEFAULT_BIRTHDAY else birthdayEt.editText?.text.toString()
                                    .dmyToIso().toString(),
                                districtId = if (districtChoice.text.toString()
                                        .isEmpty()
                                ) null else addressViewModel.listDistricts()
                                    .find { it.name == districtChoice.text.toString() }?.id,
                                districtName = districtChoice.text.toString().ifEmpty { null },
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
                                ),
                                regionName = regionChoice.text.toString().ifEmpty { null }
                            )
                        ).observe(viewLifecycleOwner) {
                            when (it) {
                                is ApiStatus.Error -> {
                                    hideLoading()
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
                                    hideLoading()
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

    private fun setRegions(response: List<RegionEntity>) {
        binding.apply {
            regionAdapter = ArrayAdapter(
                requireContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                response.map { it.name }
            )
            regionChoice.threshold = 1
            regionChoice.setAdapter(regionAdapter)
        }
    }

    private fun setDistricts(regionName: String) {
        binding.apply {
            lifecycleScope.launch {
                addressViewModel.districtByRegionName(regionName).observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "failed to fetch districts by $regionName",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is ApiStatus.Loading -> {
                            districtProgress.visibility = View.VISIBLE
                            districtLayout.endIconMode = TextInputLayout.END_ICON_NONE
                        }

                        is ApiStatus.Success -> {
                            districtProgress.visibility = View.GONE
                            districtLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU
                            districtAdapter = ArrayAdapter(
                                requireContext(),
                                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                                it.response!!.map { it.name }
                            )
                            districtChoice.setAdapter(districtAdapter)
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

    private fun setData(response: UserResponse) {
        binding.apply {
            hideLoading()
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

            regionChoice.setText(response.regionName)
            districtChoice.setText(response.districtName)

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

    private fun hideLoading() {
        try {
            loadingDialog.dismiss()
        } catch (e: Exception) {
            Log.e(TAG, "hideLoading: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}