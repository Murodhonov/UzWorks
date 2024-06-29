package com.goblindevs.uzworks.fragments.announcement

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.adapter.CategoryAdapter
import com.goblindevs.uzworks.adapter.DistrictAdapter
import com.goblindevs.uzworks.adapter.RegionAdapter
import com.goblindevs.uzworks.databinding.BottomSelectionBinding
import com.goblindevs.uzworks.databinding.FragmentEditJobBinding
import com.goblindevs.uzworks.databinding.LoadingDialogBinding
import com.goblindevs.uzworks.databinding.MapFailedDialogBinding
import com.goblindevs.uzworks.databinding.SelectJobLocationDialogBinding
import com.goblindevs.uzworks.models.response.JobResponse
import com.goblindevs.uzworks.utils.ConstValues.TAG
import com.goblindevs.uzworks.utils.GenderEnum
import com.goblindevs.uzworks.utils.convertPhoneNumber
import com.goblindevs.uzworks.utils.formatSalary
import com.goblindevs.uzworks.utils.formatTgUsername
import com.goblindevs.uzworks.utils.isoToDmy
import com.goblindevs.uzworks.utils.selectFemale
import com.goblindevs.uzworks.utils.selectMale
import com.goblindevs.uzworks.vm.AddressViewModel
import com.goblindevs.uzworks.vm.ApiStatus
import com.goblindevs.uzworks.vm.EditJobViewModel
import com.goblindevs.uzworks.vm.JobCategoryViewModel

@AndroidEntryPoint
class EditJobFragment : Fragment() {

    private var _binding: FragmentEditJobBinding? = null
    private val binding get() = _binding!!

    private val editJobViewModel: EditJobViewModel by viewModels()

    private val addressViewModel: AddressViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingBinding: LoadingDialogBinding

    private lateinit var locationDialog: BottomSheetDialog
    private lateinit var locationBinding: SelectJobLocationDialogBinding

    private lateinit var regionDialog: BottomSheetDialog
    private lateinit var regionDialogItemBinding: BottomSelectionBinding

    private lateinit var districtDialog: BottomSheetDialog
    private lateinit var districtDialogItemBinding: BottomSelectionBinding

    private lateinit var categoryDialog: BottomSheetDialog
    private lateinit var categoryDialogItemBinding: BottomSelectionBinding

    private lateinit var openMapFailedDialog: AlertDialog
    private lateinit var mapFailedDialogBinding: MapFailedDialogBinding

    private var previousMarker: Marker? = null
    private lateinit var googleMap: GoogleMap
    private var selectedLocation = LatLng(0.0, 0.0)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditJobBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            editJobViewModel.jobId = arguments?.getString("announcement_id")!!
            if (editJobViewModel.jobLiveData.value !is ApiStatus.Success) {
                editJobViewModel.fetchJob().observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            error()
                        }

                        is ApiStatus.Loading -> {
                            loading()
                        }

                        is ApiStatus.Success -> {
                            succeeded(it.response!!)
                        }
                    }
                }
            }
        }
    }

    private fun error() {
        loadingDialog.dismiss()
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.fetch_job_failed),
            Toast.LENGTH_SHORT
        ).show()
        findNavController().popBackStack()
    }

    private fun loading() {
        try {
            loadingDialog.show()
        } catch (e: Exception) {
            loadingDialog = AlertDialog.Builder(requireContext()).create()
            loadingBinding = LoadingDialogBinding.inflate(layoutInflater)
            loadingDialog.setView(loadingBinding.root)
            loadingDialog.setCancelable(false)
            loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loadingDialog.show()
        }
    }

    private fun succeeded(jobResponse: JobResponse) {
        loadingDialog.dismiss()
        binding.apply {
            titleEt.editText?.setText(jobResponse.title)
            salaryEt.editText?.setText(jobResponse.salary.toString().formatSalary())
            workingTimeEt.editText?.setText(jobResponse.workingTime)
            workingScheduleEt.editText?.setText(jobResponse.workingSchedule)
            tgUserNameEt.editText?.setText(jobResponse.tgUserName.formatTgUsername())
            benefitEt.editText?.setText(jobResponse.benefit)
            requirementEt.editText?.setText(jobResponse.requirement)
            minAgeEt.editText?.setText(jobResponse.minAge.toString())
            maxAgeEt.editText?.setText(jobResponse.maxAge.toString())
            phoneNumber.text = jobResponse.phoneNumber.convertPhoneNumber()
            deadlineEt.editText?.setText(jobResponse.deadline.isoToDmy())
            genderLayout.apply {
                when (jobResponse.gender) {
                    GenderEnum.MALE.label -> {
                        selectMale(resources)
                    }

                    GenderEnum.FEMALE.label -> {
                        selectFemale(resources)
                    }

                    else -> {}
                }
            }
            if (jobResponse.latitude != 0.0 && jobResponse.longitude != 0.0) {
                selectAddress.text = resources.getString(R.string.change_location)
                selectAddress.setTextColor(resources.getColor(R.color.white))
                selectAddress.setBackgroundResource(R.drawable.enabled_button_background)
            }

            region.text = jobResponse.district.region.name
            district.text = jobResponse.district.name
            category.text = jobResponse.jobCategory.title

            back.setOnClickListener {
                findNavController().popBackStack()
            }

            editJobViewModel.controlInput(
                requireActivity(),
                topTv,
                titleEt,
                salaryEt,
                workingTimeEt,
                workingScheduleEt,
                tgUserNameEt,
                benefitEt,
                requirementEt,
                minAgeEt,
                maxAgeEt,
                deadlineEt,
                genderLayout
            )

            phoneNumber.setOnClickListener {
                val clipboardManager =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData =
                    ClipData.newPlainText("label", phoneNumber.text.toString())
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(
                    requireContext(),
                    requireContext().resources.getString(R.string.phone_number_copied),
                    Toast.LENGTH_SHORT
                ).show()
            }

            cancelBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            selectAddress.setOnClickListener {
                showLocationDialog()
            }

            saveBtn.setOnClickListener {
                edit()
            }

            region.setOnClickListener {
                showRegion()
            }

            district.setOnClickListener {
                if (region.text.toString() == resources.getString(R.string.select_region)) {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.select_region_first),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    showDistrict()
                }
            }

            category.setOnClickListener {
                showCategory()
            }
        }
    }

    private fun showLocationDialog() {
        try {
            locationDialog.show()
        } catch (e: Exception) {
            locationDialog = BottomSheetDialog(requireContext())
            locationBinding = SelectJobLocationDialogBinding.inflate(layoutInflater)
            locationDialog.setContentView(locationBinding.root)
            locationDialog.setCancelable(false)
            locationDialog.show()
        }

        Log.d(TAG, "showLocationDialog: ${editJobViewModel.latLng}")

        locationBinding.apply {
            selectedLocation = editJobViewModel.latLng
            try {
                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.select_job_map) as SupportMapFragment
                mapFragment.getMapAsync { map ->
                    cancelBtn.visibility = View.VISIBLE
                    setLocationBtn.visibility = View.VISIBLE

                    googleMap = map

                    previousMarker =
                        googleMap.addMarker(MarkerOptions().position(selectedLocation))
                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            selectedLocation,
                            15f
                        )
                    )

                    googleMap.setOnMapClickListener {
                        previousMarker?.remove()
                        val newMarker = googleMap.addMarker(MarkerOptions().position(it))
                        previousMarker = newMarker
                        selectedLocation = it
                    }
                }
            } catch (e: Exception) {
                openMapFailed()
            }

            cancelBtn.setOnClickListener {
                locationDialog.dismiss()
            }

            setLocationBtn.setOnClickListener {
                binding.apply {
                    selectAddress.setBackgroundResource(R.drawable.enabled_button_background)
                    selectAddress.setTextColor(resources.getColor(R.color.white))
                }
                editJobViewModel.latLng = selectedLocation
                locationDialog.dismiss()
            }

            locationDialog.setOnDismissListener {
                previousMarker?.remove()
            }
        }
    }

    private fun openMapFailed() {
        try {
            openMapFailedDialog.show()
        } catch (e: Exception) {
            openMapFailedDialog = AlertDialog.Builder(requireContext()).create()
            mapFailedDialogBinding = MapFailedDialogBinding.inflate(layoutInflater)
            openMapFailedDialog.setView(mapFailedDialogBinding.root)
            openMapFailedDialog.setCancelable(false)
            openMapFailedDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            openMapFailedDialog.show()
        }
        mapFailedDialogBinding.close.setOnClickListener {
            openMapFailedDialog.dismiss()
            locationDialog.dismiss()
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

            addressViewModel.regionLiveData.observe(viewLifecycleOwner) {
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
                                editJobViewModel.regionId = regionId
                                editJobViewModel.districtId = ""
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

            addressViewModel.districtsByRegionId(editJobViewModel.regionId.toString())
                .observe(viewLifecycleOwner) {
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
                                editJobViewModel.districtId = districtId
                                districtDialog.dismiss()
                            }
                            selectionRv.adapter = districtAdapter
                        }
                    }
                }
        }
    }

    private fun showCategory() {
        try {
            categoryDialog.show()
        } catch (e: Exception) {
            categoryDialog = BottomSheetDialog(requireContext())
            categoryDialogItemBinding = BottomSelectionBinding.inflate(layoutInflater)
            categoryDialog.setContentView(categoryDialogItemBinding.root)
            categoryDialog.show()
        }
        categoryDialogItemBinding.apply {
            jobCategoryViewModel.jobCategoriesLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "failed to load categories",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ApiStatus.Loading -> {
                        progress.visibility = View.VISIBLE
                    }

                    is ApiStatus.Success -> {
                        progress.visibility = View.GONE
                        Log.d("TAG", "showCategory: ${it.response!!.size}")
                        Log.d("TAG", "showCategory: ${it.response.map { it.title }}")
                        val categoryAdapter =
                            CategoryAdapter(it.response) { categoryId, categoryName ->
                                binding.category.text = categoryName
                                binding.category.setBackgroundResource(R.drawable.enabled_tv_background)
                                editJobViewModel.categoryId = categoryId
                                categoryDialog.dismiss()
                            }
                        selectionRv.adapter = categoryAdapter
                    }
                }
            }
        }
    }

    private fun edit() {
        binding.apply {
            editJobViewModel.editJob(
                context = requireContext(),
                titleEt = titleEt,
                salaryEt = salaryEt,
                workingTimeEt = workingTimeEt,
                workingScheduleEt = workingScheduleEt,
                tgUserNameEt = tgUserNameEt,
                benefitEt = benefitEt,
                requirementEt = requirementEt,
                minAgeEt = minAgeEt,
                maxAgeEt = maxAgeEt,
                deadlineEt = deadlineEt,
                district = district,
                category = category
            ).observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(requireContext(), resources.getString(R.string.edit_job_announcement_failed), Toast.LENGTH_SHORT)
                            .show()
                    }

                    is ApiStatus.Loading -> {
                        loading()
                    }

                    is ApiStatus.Success -> {
                        loadingDialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.job_announcement_edited),
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: ")
        _binding = null
    }
}