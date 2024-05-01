package dev.goblingroup.uzworks.fragments.announcement

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.databinding.FragmentAddJobBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogBinding
import dev.goblingroup.uzworks.models.request.JobCreateRequest
import dev.goblingroup.uzworks.models.response.JobCreateResponse
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.dmyToIso
import dev.goblingroup.uzworks.utils.selectFemale
import dev.goblingroup.uzworks.utils.selectMale
import dev.goblingroup.uzworks.vm.AddJobViewModel
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddJobFragment : Fragment() {

    private var _binding: FragmentAddJobBinding? = null
    private val binding get() = _binding!!

    private val addJobViewModel: AddJobViewModel by viewModels()

    private val addressViewModel: AddressViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog

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
            loadRegions()
            loadDistricts()
            loadCategories()

            back.setOnClickListener {
                findNavController().popBackStack()
            }


            addJobViewModel.controlInput(
                requireContext(),
                topTv,
                deadlineEt,
                minAgeEt,
                maxAgeEt,
                salaryEt,
                phoneNumberEt,
                tgUserNameEt,
                genderLayout,
                titleEt,
                workingTimeEt,
                workingScheduleEt,
                regionChoice,
                jobCategoryChoice,
                jobCategoryLayout,
                districtChoice,
                districtLayout
            )

            saveBtn.setOnClickListener {
                if (addJobViewModel.isFormValid(
                        requireContext(),
                        deadlineEt,
                        titleEt,
                        salaryEt,
                        workingTimeEt,
                        workingScheduleEt,
                        tgUserNameEt,
                        phoneNumberEt,
                        benefitEt,
                        requirementEt,
                        minAgeEt,
                        maxAgeEt,
                        districtChoice,
                        districtLayout,
                        jobCategoryChoice,
                        jobCategoryLayout
                    )
                ) {
                    add()
                }
            }

            cancelBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            selectAddressBtn.setOnClickListener {
                saveState()
                val bundle = Bundle()
                addJobViewModel.latitude.observe(viewLifecycleOwner) {
                    bundle.putDouble("latitude", it)
                }
                addJobViewModel.longitude.observe(viewLifecycleOwner) {
                    bundle.putDouble("longitude", it)
                }
                findNavController().navigate(
                    resId = R.id.action_addJobFragment_to_selectJobLocationFragment,
                    args = bundle
                )
            }

            setFragmentResultListener("lat_lng") { _, bundle ->
                val lat = bundle.getDouble("latitude")
                val lng = bundle.getDouble("longitude")
                if (lat != 0.0 && lng != 0.0) {
                    addJobViewModel.setLatitude(bundle.getDouble("latitude"))
                    addJobViewModel.setLongitude(bundle.getDouble("longitude"))
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
                    gender = addJobViewModel.selectedGender,
                    instagramLink = "test",
                    latitude = addJobViewModel.latitude.value!!,
                    longitude = addJobViewModel.longitude.value!!,
                    maxAge = maxAgeEt.editText?.text.toString().toInt(),
                    minAge = minAgeEt.editText?.text.toString().toInt(),
                    phoneNumber = phoneNumberEt.editText?.text.toString()
                        .filter { !it.isWhitespace() },
                    requirement = requirementEt.editText?.text.toString(),
                    salary = salaryEt.editText?.text.toString().toInt(),
                    telegramLink = "test",
                    tgUserName = tgUserNameEt.editText?.text.toString(),
                    title = titleEt.editText?.text.toString(),
                    workingSchedule = workingScheduleEt.editText?.text.toString(),
                    workingTime = workingTimeEt.editText?.text.toString()
                )
            ).observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        error(it.error)
                    }

                    is ApiStatus.Loading -> {
                        loading()
                    }

                    is ApiStatus.Success -> {
                        success(it.response)
                    }
                }
            }
        }
    }

    private fun saveState() {
        binding.apply {
            addJobViewModel.setBenefit(benefitEt.editText?.text.toString())
            addJobViewModel.setDeadline(deadlineEt.editText?.text.toString())
            addJobViewModel.setMaxAge(
                if (maxAgeEt.editText?.text.toString()
                        .isNotEmpty()
                ) maxAgeEt.editText?.text.toString().toInt() else 0
            )
            addJobViewModel.setMinAge(
                if (minAgeEt.editText?.text.toString()
                        .isNotEmpty()
                ) minAgeEt.editText?.text.toString().toInt() else 0
            )
            addJobViewModel.setPhoneNumber(phoneNumberEt.editText?.text.toString())
            addJobViewModel.setRequirement(requirementEt.editText?.text.toString())
            addJobViewModel.setSalary(
                if (salaryEt.editText?.text.toString()
                        .isNotEmpty()
                ) salaryEt.editText?.text.toString().filter { !it.isWhitespace() }.toInt() else 0
            )
            addJobViewModel.setTgUsername(tgUserNameEt.editText?.text.toString())
            addJobViewModel.setTitle(titleEt.editText?.text.toString())
            addJobViewModel.setWorkingSchedule(workingScheduleEt.editText?.text.toString())
            addJobViewModel.setWorkingSchedule(workingTimeEt.editText?.text.toString())
        }
    }

    private fun error(error: Throwable) {
        Toast.makeText(requireContext(), "failed to create job", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "error: $error")
        Log.d(TAG, "error: ${error.message}")
        Log.d(TAG, "error: ${error.stackTrace}")
    }

    private fun loading() {
        loadingDialog = AlertDialog.Builder(requireContext()).create()
        val loadingBinding = LoadingDialogBinding.inflate(layoutInflater)
        loadingDialog.setView(loadingBinding.root)
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.setCancelable(false)
    }

    private fun success(jobCreateResponse: JobCreateResponse?) {
        loadingDialog.dismiss()
        Toast.makeText(requireContext(), "successfully created", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            addJobViewModel.benefit.observe(viewLifecycleOwner) {
                benefitEt.editText?.setText(it)
            }
            addJobViewModel.deadline.observe(viewLifecycleOwner) {
                deadlineEt.editText?.setText(it)
            }
            addJobViewModel.maxAge.observe(viewLifecycleOwner) {
                if (it == 0)
                    maxAgeEt.editText?.setText("")
                else
                    maxAgeEt.editText?.setText(it.toString())
            }
            addJobViewModel.minAge.observe(viewLifecycleOwner) {
                if (it == 0)
                    minAgeEt.editText?.setText("")
                else
                    minAgeEt.editText?.setText(it.toString())
            }
            addJobViewModel.phoneNumber.observe(viewLifecycleOwner) {
                phoneNumberEt.editText?.setText(it)
            }
            addJobViewModel.requirement.observe(viewLifecycleOwner) {
                requirementEt.editText?.setText(it)
            }
            addJobViewModel.salary.observe(viewLifecycleOwner) {
                if (it == 0)
                    salaryEt.editText?.setText("")
                else
                    salaryEt.editText?.setText(it.toString())
            }
            addJobViewModel.tgUserName.observe(viewLifecycleOwner) {
                tgUserNameEt.editText?.setText(it)
            }
            addJobViewModel.title.observe(viewLifecycleOwner) {
                titleEt.editText?.setText(it)
            }
            addJobViewModel.workingSchedule.observe(viewLifecycleOwner) {
                workingScheduleEt.editText?.setText(it)
            }
            addJobViewModel.workingTime.observe(viewLifecycleOwner) {
                workingTimeEt.editText?.setText(it)
            }

            addJobViewModel.gender.observe(viewLifecycleOwner) {
                if (it == GenderEnum.MALE.code) {
                    genderLayout.selectMale(resources)
                } else if (it == GenderEnum.FEMALE.code) {
                    genderLayout.selectFemale(resources)
                }
            }

            addJobViewModel.categoryIndex.observe(viewLifecycleOwner) {
                jobCategoryChoice.setSelection(it)
            }
            addJobViewModel.districtIndex.observe(viewLifecycleOwner) {
                districtChoice.setSelection(it)
            }
            addJobViewModel.regionIndex.observe(viewLifecycleOwner) {
                regionChoice.setSelection(it)
            }

            var isLocationAvailable = false
            addJobViewModel.latitude.observe(viewLifecycleOwner) {
                isLocationAvailable = it != 0.0
            }
            addJobViewModel.longitude.observe(viewLifecycleOwner) {
                isLocationAvailable = it != 0.0
            }
            if (isLocationAvailable) selectAddressTv.text =
                resources.getString(R.string.location_saved)
        }

    }

    private fun loadRegions() {
        lifecycleScope.launch {
            addressViewModel.regionLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "some error while loading regions",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(TAG, "loadRegions: ${it.error}")
                        Log.e(TAG, "loadRegions: ${it.error.message}")
                        Log.e(TAG, "loadRegions: ${it.error.printStackTrace()}")
                        Log.e(TAG, "loadRegions: ${it.error.stackTrace}")
                    }

                    is ApiStatus.Loading -> {

                    }

                    is ApiStatus.Success -> {
                        Log.d(TAG, "loadRegions: ${it.response?.size} regions got")
                        setRegions(it.response as List<RegionEntity>)
                    }
                }
            }
        }
    }

    private fun setRegions(regionList: List<RegionEntity>) {
        binding.apply {
            val regionAdapter = ArrayAdapter(
                requireContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                regionList.map { it.name }
            )
            regionChoice.setAdapter(regionAdapter)

            regionChoice.setOnItemClickListener { parent, view, position, id ->
                districtChoice.text.clear()
                districtChoice.hint = "Select district"
                setDistricts(regionList[position].id)
            }
        }
    }

    private fun setDistricts(regionId: String) {
        binding.apply {
            lifecycleScope.launch {
                val districtList = addressViewModel.listDistrictsByRegionId(regionId)
                val districtAdapter = ArrayAdapter(
                    requireContext(),
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                    districtList.map { it.name }
                )
                districtChoice.setAdapter(districtAdapter)
                districtChoice.setOnItemClickListener { parent, view, position, id ->
                    addJobViewModel.setDistrictId(districtList[position].id)
                }
            }
        }
    }

    private fun loadDistricts() {
        lifecycleScope.launch {
            addressViewModel.districtLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "some error while loading districts",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(TAG, "loadDistricts: ${it.error}")
                        Log.e(TAG, "loadDistricts: ${it.error.message}")
                        Log.e(TAG, "loadDistricts: ${it.error.printStackTrace()}")
                        Log.e(TAG, "loadDistricts: ${it.error.stackTrace}")
                    }

                    is ApiStatus.Loading -> {

                    }

                    is ApiStatus.Success -> {
                        (it.response as List<DistrictEntity>).forEach {
                            Log.d(TAG, "loadDistricts: succeeded $it")
                        }
                    }
                }
            }
        }
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            jobCategoryViewModel.jobCategoriesLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Some error on loading job categories",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(TAG, "loadCategories: ${it.error}")
                        Log.e(TAG, "loadCategories: ${it.error.message}")
                        Log.e(TAG, "loadCategories: ${it.error.printStackTrace()}")
                        Log.e(TAG, "loadCategories: ${it.error.stackTrace}")
                    }

                    is ApiStatus.Loading -> {

                    }

                    is ApiStatus.Success -> {
                        setJobCategories(it.response as List<JobCategoryEntity>)
                    }
                }
            }
        }
    }

    private fun setJobCategories(jobCategoryList: List<JobCategoryEntity>) {
        binding.apply {
            val jobCategoryAdapter = ArrayAdapter(
                requireContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                jobCategoryList.map { it.title }
            )
            jobCategoryChoice.setAdapter(jobCategoryAdapter)
            jobCategoryChoice.setOnItemClickListener { parent, view, position, id ->
                addJobViewModel.setCategoryId(jobCategoryList[position].id)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}