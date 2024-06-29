package com.goblindevs.uzworks.fragments.profile

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
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.adapter.DistrictAdapter
import com.goblindevs.uzworks.adapter.RegionAdapter
import com.goblindevs.uzworks.databinding.BottomSelectionBinding
import com.goblindevs.uzworks.databinding.FragmentPersonalInfoBinding
import com.goblindevs.uzworks.databinding.LoadingDialogBinding
import com.goblindevs.uzworks.models.request.UserUpdateRequest
import com.goblindevs.uzworks.models.response.UserResponse
import com.goblindevs.uzworks.utils.ConstValues.DEFAULT_BIRTHDAY
import com.goblindevs.uzworks.utils.ConstValues.TAG
import com.goblindevs.uzworks.utils.GenderEnum
import com.goblindevs.uzworks.utils.convertPhoneNumber
import com.goblindevs.uzworks.utils.dmyToIso
import com.goblindevs.uzworks.utils.isoToDmy
import com.goblindevs.uzworks.utils.selectFemale
import com.goblindevs.uzworks.utils.selectMale
import com.goblindevs.uzworks.vm.ApiStatus
import com.goblindevs.uzworks.vm.PersonalInfoViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonalInfoFragment : Fragment() {

    private var _binding: FragmentPersonalInfoBinding? = null
    private val binding get() = _binding!!

    private val personalInfoViewModel: PersonalInfoViewModel by viewModels()

    private lateinit var regionDialog: BottomSheetDialog
    private lateinit var regionDialogItemBinding: BottomSelectionBinding

    private lateinit var districtDialog: BottomSheetDialog
    private lateinit var districtDialogItemBinding: BottomSelectionBinding

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
                phoneNumber,
                genderLayout,
                birthday
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
                                birthDate = if (birthday.text.toString()
                                        .isEmpty()
                                ) DEFAULT_BIRTHDAY else birthday.text.toString()
                                    .dmyToIso().toString(),
                                districtId = personalInfoViewModel.districtId,
                                districtName = district.text.toString().ifEmpty { null },
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
                                regionName = region.text.toString().ifEmpty { null }
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

            region.setOnClickListener {
                showRegion()
            }

            district.setOnClickListener {
                showDistrict()
            }
        }
    }

    private fun showRegion() {
        try {
            regionDialog.show()
        } catch (e: Exception) {
            regionDialog = BottomSheetDialog(requireContext())
            regionDialogItemBinding = BottomSelectionBinding.inflate(layoutInflater)
            regionDialog.setContentView(regionDialogItemBinding.root)
            regionDialog.show()
        }
        regionDialogItemBinding.apply {

            personalInfoViewModel.fetchRegions().observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "failed to load regions",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    is ApiStatus.Loading -> {
                        progress.visibility = View.VISIBLE
                    }

                    is ApiStatus.Success -> {
                        progress.visibility = View.GONE
                        val regionAdapter = RegionAdapter(
                            it.response!!
                        ) { regionId, regionName ->
                            if (regionName != binding.region.text.toString()) {
                                binding.region.text = regionName
                                personalInfoViewModel.regionId = regionId
                                personalInfoViewModel.districtId = null
                                binding.district.text = resources.getString(R.string.select_region)
                            }
                            regionDialog.dismiss()
                        }
                        selectionRv.adapter = regionAdapter
                    }
                }
            }
        }
    }

    private fun showDistrict() {
        try {
            districtDialog.show()
        } catch (e: Exception) {
            districtDialog = BottomSheetDialog(requireContext())
            districtDialogItemBinding = BottomSelectionBinding.inflate(layoutInflater)
            districtDialog.setContentView(districtDialogItemBinding.root)
            districtDialog.show()
        }
        districtDialogItemBinding.apply {

            personalInfoViewModel.fetchDistrictsByRegionId().observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "failed to load districts",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    is ApiStatus.Loading -> {
                        progress.visibility = View.VISIBLE
                        selectionRv.visibility = View.GONE
                    }

                    is ApiStatus.Success -> {
                        progress.visibility = View.GONE
                        selectionRv.visibility = View.VISIBLE
                        val districtAdapter = DistrictAdapter(
                            it.response!!
                        ) { districtId, districtName ->
                            binding.district.text = districtName
                            binding.district.setBackgroundResource(R.drawable.enabled_tv_background)
                            personalInfoViewModel.districtId = districtId
                            districtDialog.dismiss()
                        }
                        selectionRv.adapter = districtAdapter
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
                birthday.text = response.birthDate.isoToDmy()
            }

            emailEt.editText?.setText(response.email)
            phoneNumber.text = response.phoneNumber.convertPhoneNumber()

            region.text = response.regionName
            district.text = response.districtName

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