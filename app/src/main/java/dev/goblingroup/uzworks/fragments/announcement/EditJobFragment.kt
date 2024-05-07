package dev.goblingroup.uzworks.fragments.announcement

import android.Manifest
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentEditJobBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogItemBinding
import dev.goblingroup.uzworks.databinding.SelectJobLocationDialogBinding
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.convertPhoneNumber
import dev.goblingroup.uzworks.utils.formatSalary
import dev.goblingroup.uzworks.utils.formatTgUsername
import dev.goblingroup.uzworks.utils.isoToDmy
import dev.goblingroup.uzworks.utils.selectFemale
import dev.goblingroup.uzworks.utils.selectMale
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.EditJobViewModel
import dev.goblingroup.uzworks.vm.JobCategoryViewModel

@AndroidEntryPoint
class EditJobFragment : Fragment() {

    private var _binding: FragmentEditJobBinding? = null
    private val binding get() = _binding!!

    private val editJobViewModel: EditJobViewModel by viewModels()

    private val addressViewModel: AddressViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingBinding: LoadingDialogItemBinding

    private lateinit var regionAdapter: ArrayAdapter<String>
    private lateinit var categoryAdapter: ArrayAdapter<String>

    private lateinit var locationDialog: BottomSheetDialog
    private lateinit var locationBinding: SelectJobLocationDialogBinding

    private var previousMarker: Marker? = null
    private lateinit var googleMap: GoogleMap

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
            loadingBinding = LoadingDialogItemBinding.inflate(layoutInflater)
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
            if (editJobViewModel.latLng.latitude != 0.0 && editJobViewModel.latLng.longitude != 0.0) {
                selectAddressBtn.text = resources.getString(R.string.select_location)
            }
            phoneNumberEt.editText?.setText(jobResponse.phoneNumber.convertPhoneNumber())
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
                selectAddressBtn.text = resources.getString(R.string.change_location)
                selectAddressBtn.setTextColor(resources.getColor(R.color.white))
                selectAddressBtn.setBackgroundResource(R.drawable.enabled_button_background)
            }

            regionChoice.setText(jobResponse.regionName)
            districtChoice.setText(jobResponse.districtName)
            jobCategoryChoice.setText(jobResponse.categoryName)
            editJobViewModel.regionId =
                addressViewModel.listRegions().find { it.name == jobResponse.regionName }!!.id
            editJobViewModel.districtId =
                addressViewModel.listDistrictsByRegionId(editJobViewModel.regionId)
                    .find { it.name == jobResponse.districtName }!!.id
            editJobViewModel.categoryId = jobCategoryViewModel.listJobCategories()
                .find { it.title == jobResponse.categoryName }!!.id

            setDistricts(jobResponse.regionName)

            regionAdapter = ArrayAdapter(
                requireContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                addressViewModel.listRegions().map { it.name }
            )
            regionChoice.setAdapter(regionAdapter)

            categoryAdapter = ArrayAdapter(
                requireContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                jobCategoryViewModel.listJobCategories().map { it.title }
            )
            jobCategoryChoice.setAdapter(categoryAdapter)

            regionChoice.setOnItemClickListener { parent, view, position, id ->
                districtChoice.text.clear()
                districtChoice.hint = resources.getString(R.string.select_district)
                editJobViewModel.regionId = addressViewModel.listRegions()[position].id
                setDistricts(regionAdapter.getItem(position).toString())
            }

            districtChoice.setOnItemClickListener { parent, view, position, id ->
                if (districtLayout.isErrorEnabled) {
                    districtLayout.isErrorEnabled = false
                }
                editJobViewModel.districtId =
                    addressViewModel.listDistrictsByRegionId(
                        editJobViewModel.regionId
                    )[position].id
            }

            jobCategoryChoice.setOnItemClickListener { parent, view, position, id ->
                if (jobCategoryLayout.isErrorEnabled) {
                    jobCategoryLayout.isErrorEnabled = false
                }
                editJobViewModel.categoryId = jobCategoryViewModel.listJobCategories()[position].id
            }

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

            phoneNumberEt.editText?.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                        phoneNumberEt.windowToken,
                        0
                    )
                    val clipboardManager =
                        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData =
                        ClipData.newPlainText("label", phoneNumberEt.editText?.text.toString())
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(
                        requireContext(),
                        requireContext().resources.getString(R.string.phone_number_copied),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            cancelBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            selectAddressBtn.setOnClickListener {
                showLocationDialog()
            }

            saveBtn.setOnClickListener {
                edit()
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
            var selectedLocation = editJobViewModel.latLng
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.select_job_map) as SupportMapFragment
            mapFragment.getMapAsync { map ->
                updateFindBtn()
                cancelBtn.visibility = View.VISIBLE
                setLocationBtn.visibility = View.VISIBLE
                findMeBtn.visibility = View.VISIBLE

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
            cancelBtn.setOnClickListener {
                locationDialog.dismiss()
            }

            setLocationBtn.setOnClickListener {
                binding.apply {
                    selectAddressBtn.setBackgroundResource(R.drawable.enabled_button_background)
                    selectAddressBtn.setTextColor(resources.getColor(R.color.white))
                }
                editJobViewModel.latLng = selectedLocation
                locationDialog.dismiss()
            }

            locationDialog.setOnDismissListener {
                previousMarker?.remove()
            }

            findMeBtn.setOnClickListener {
                if (checkLocationPermission()) {
                    findUser()
                } else {
                    requestLocationPermission()
                }
            }
        }
    }

    private fun findUser() {
        if (!checkLocationPermission())
            return
        LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation
            .addOnSuccessListener {
                if (it != null) {
                    editJobViewModel.latLng = LatLng(it.latitude, it.longitude)
                    previousMarker?.remove()
                    val cameraUpdate =
                        CameraUpdateFactory.newLatLngZoom(editJobViewModel.latLng, 15f)
                    googleMap.animateCamera(cameraUpdate, 1000, null)
                    previousMarker =
                        googleMap.addMarker(MarkerOptions().position(editJobViewModel.latLng))
                } else {
                    Toast.makeText(requireContext(), "$it", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "failed to get location", Toast.LENGTH_SHORT)
                    .show()
                Log.e(TAG, "findUser: ${it.message}")
            }
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findUser()
                updateFindBtn()
            } else {
                Toast.makeText(requireContext(), "denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun updateFindBtn() {
        if (checkLocationPermission()) {
            locationBinding.findMeBtn.setImageResource(R.drawable.ic_find_me)
        } else {
            locationBinding.findMeBtn.setImageResource(R.drawable.ic_location_permission_required)
        }
    }

    private fun setDistricts(regionName: String) {
        binding.apply {
            val districtList = ArrayList(
                addressViewModel.listDistrictsByRegionId(
                    addressViewModel.listRegions().find { it.name == regionName }!!.id
                )
            )
            Log.d(TAG, "setDistricts: setting districts in $regionName")
            Log.d(TAG, "setDistricts: ${districtList.toArray()}")
            val districtAdapter = ArrayAdapter(
                requireContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                districtList.map { it.name }
            )
            districtChoice.setAdapter(districtAdapter)
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
                districtLayout = districtLayout,
                categoryLayout = jobCategoryLayout
            ).observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(requireContext(), "failed to edit job", Toast.LENGTH_SHORT)
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