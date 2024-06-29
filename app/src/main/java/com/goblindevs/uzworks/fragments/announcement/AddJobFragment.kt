package com.goblindevs.uzworks.fragments.announcement

import android.annotation.SuppressLint
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
import com.goblindevs.uzworks.databinding.FragmentAddJobBinding
import com.goblindevs.uzworks.databinding.LoadingDialogBinding
import com.goblindevs.uzworks.databinding.MapFailedDialogBinding
import com.goblindevs.uzworks.databinding.SelectJobLocationDialogBinding
import com.goblindevs.uzworks.models.request.JobCreateRequest
import com.goblindevs.uzworks.utils.ConstValues.DEFAULT_LATITUDE
import com.goblindevs.uzworks.utils.ConstValues.DEFAULT_LONGITUDE
import com.goblindevs.uzworks.utils.convertPhoneNumber
import com.goblindevs.uzworks.utils.dmyToIso
import com.goblindevs.uzworks.vm.AddJobViewModel
import com.goblindevs.uzworks.vm.AddressViewModel
import com.goblindevs.uzworks.vm.ApiStatus
import com.goblindevs.uzworks.vm.JobCategoryViewModel

@AndroidEntryPoint
class AddJobFragment : Fragment() {

    private var _binding: FragmentAddJobBinding? = null
    private val binding get() = _binding!!

    private val addJobViewModel: AddJobViewModel by viewModels()

    private val addressViewModel: AddressViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingDialogBinding: LoadingDialogBinding

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
        _binding = FragmentAddJobBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {

            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            cancelBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            addJobViewModel.controlInput(
                requireContext(),
                deadlineEt,
                benefitEt,
                requirementEt,
                minAgeEt,
                maxAgeEt,
                salaryEt,
                tgUserNameEt,
                genderLayout,
                titleEt,
                workingTimeEt,
                workingScheduleEt,
            )

            phoneNumber.text = addJobViewModel.phoneNumber.value.toString().convertPhoneNumber()

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

            saveBtn.setOnClickListener {
                if (addJobViewModel.isFormValid(
                        requireContext(),
                        deadlineEt,
                        titleEt,
                        salaryEt,
                        workingTimeEt,
                        workingScheduleEt,
                        tgUserNameEt,
                        benefitEt,
                        requirementEt,
                        minAgeEt,
                        maxAgeEt,
                        district,
                        category
                    )
                ) {
                    add()
                }
            }

            selectAddress.setOnClickListener {
                showLocationDialog()
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

        locationBinding.apply {
            selectedLocation =
                LatLng(
                    addJobViewModel.latitude.value ?: DEFAULT_LATITUDE,
                    addJobViewModel.longitude.value ?: DEFAULT_LONGITUDE
                )
            try {
                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.select_job_map) as SupportMapFragment
                mapFragment.getMapAsync { map ->
                    cancelBtn.visibility = View.VISIBLE
                    setLocationBtn.visibility = View.VISIBLE

                    googleMap = map

                    if (selectedLocation.latitude != DEFAULT_LATITUDE && selectedLocation.longitude != DEFAULT_LONGITUDE) {
                        previousMarker =
                            googleMap.addMarker(MarkerOptions().position(selectedLocation))
                    }
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
                if (selectedLocation.latitude != DEFAULT_LATITUDE && selectedLocation.longitude != DEFAULT_LONGITUDE) {
                    binding.apply {
                        selectAddress.text = resources.getString(R.string.change_location)
                        selectAddress.setTextColor(resources.getColor(R.color.white))
                        selectAddress.setBackgroundResource(R.drawable.enabled_button_background)
                        addJobViewModel.setLatitude(selectedLocation.latitude)
                        addJobViewModel.setLongitude(selectedLocation.longitude)
                    }
                    locationDialog.dismiss()
                } else {
                    locationDialog.dismiss()
                }
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
                                addJobViewModel.regionId.postValue(regionId)
                                addJobViewModel.districtId.postValue(null)
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

            addressViewModel.districtsByRegionId(addJobViewModel.regionId.value.toString())
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
                                addJobViewModel.districtId.postValue(districtId)
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
                                addJobViewModel.categoryId.postValue(categoryId)
                                categoryDialog.dismiss()
                            }
                        selectionRv.adapter = categoryAdapter
                    }
                }
            }
        }
    }

    private fun add() {
        binding.apply {
            addJobViewModel.createJob(
                JobCreateRequest(
                    benefit = benefitEt.editText?.text.toString(),
                    categoryId = addJobViewModel.categoryId.value.toString(),
                    deadline = deadlineEt.editText?.text.toString().dmyToIso().toString(),
                    districtId = addJobViewModel.districtId.value.toString(),
                    gender = addJobViewModel.gender.value,
                    instagramLink = "test",
                    latitude = addJobViewModel.latitude.value!!,
                    longitude = addJobViewModel.longitude.value!!,
                    maxAge = maxAgeEt.editText?.text.toString().toInt(),
                    minAge = minAgeEt.editText?.text.toString().toInt(),
                    phoneNumber = addJobViewModel.phoneNumber.value.toString(),
                    requirement = requirementEt.editText?.text.toString(),
                    salary = salaryEt.editText?.text.toString().trim().filter { !it.isWhitespace() }.toInt(),
                    telegramLink = "test",
                    tgUserName = tgUserNameEt.editText?.text.toString().substring(1),
                    title = titleEt.editText?.text.toString(),
                    workingSchedule = workingScheduleEt.editText?.text.toString(),
                    workingTime = workingTimeEt.editText?.text.toString()
                )
            ).observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        error()
                    }

                    is ApiStatus.Loading -> {
                        loading()
                    }

                    is ApiStatus.Success -> {
                        success()
                    }
                }
            }
        }
    }

    private fun error() {
        loadingDialog.dismiss()
        Toast.makeText(requireContext(), resources.getString(R.string.create_job_failed), Toast.LENGTH_SHORT).show()
    }

    private fun loading() {
        try {
            loadingDialog.show()
        } catch (e: Exception) {
            loadingDialog = AlertDialog.Builder(requireContext()).create()
            loadingDialogBinding = LoadingDialogBinding.inflate(layoutInflater)
            loadingDialog.setView(loadingDialogBinding.root)
            loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loadingDialog.setCancelable(false)
            loadingDialog.show()
        }
    }

    private fun success() {
        loadingDialog.dismiss()
        Toast.makeText(requireContext(), resources.getString(R.string.job_announcement_created), Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_addJobFragment_to_myAnnouncementsFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}