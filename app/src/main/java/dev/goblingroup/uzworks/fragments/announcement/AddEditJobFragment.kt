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
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.databinding.FragmentAddEditJobBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogBinding
import dev.goblingroup.uzworks.models.request.JobCreateRequest
import dev.goblingroup.uzworks.models.request.JobEditRequest
import dev.goblingroup.uzworks.models.response.JobCreateResponse
import dev.goblingroup.uzworks.utils.ConstValues.ANNOUNCEMENT_ADDING
import dev.goblingroup.uzworks.utils.ConstValues.ANNOUNCEMENT_EDITING
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.dmyToIso
import dev.goblingroup.uzworks.utils.selectFemale
import dev.goblingroup.uzworks.utils.selectMale
import dev.goblingroup.uzworks.vm.AddEditJobViewModel
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditJobFragment : Fragment() {

    private var _binding: FragmentAddEditJobBinding? = null
    private val binding get() = _binding!!

    private val addressViewModel: AddressViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()
    private val addEditJobViewModel: AddEditJobViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditJobBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
//            topTv.isSelected = true
            loadRegions()
            loadDistricts()
            loadCategories()

            back.setOnClickListener {
                findNavController().popBackStack()
            }


            addEditJobViewModel.controlInput(
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
                if (addEditJobViewModel.isFormValid(
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
                    addEdit()
                }
            }

            cancelBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            selectAddressBtn.setOnClickListener {
                saveState()
                val bundle = Bundle()
                addEditJobViewModel.latitude.observe(viewLifecycleOwner) {
                    bundle.putDouble("latitude", it)
                }
                addEditJobViewModel.longitude.observe(viewLifecycleOwner) {
                    bundle.putDouble("longitude", it)
                }
                findNavController().navigate(
                    resId = R.id.action_addEditJobFragment_to_selectJobLocationFragment,
                    args = bundle
                )
            }

            setFragmentResultListener("lat_lng") { _, bundle ->
                val lat = bundle.getDouble("latitude")
                val lng = bundle.getDouble("longitude")
                if (lat != 0.0 && lng != 0.0) {
                    addEditJobViewModel.setLatitude(bundle.getDouble("latitude"))
                    addEditJobViewModel.setLongitude(bundle.getDouble("longitude"))
                }
            }
        }
    }

    private fun addEdit() {
        when (addEditJobViewModel.status) {
            ANNOUNCEMENT_ADDING -> {
                add()
            }

            ANNOUNCEMENT_EDITING -> {
                edit()
            }

            else -> {}
        }
    }

    private fun add() {
        binding.apply {
            addEditJobViewModel.createJob(
                JobCreateRequest(
                    benefit = benefitEt.editText?.text.toString(),
                    categoryId = addEditJobViewModel.categoryId.value.toString(),
                    deadline = deadlineEt.editText?.text.toString().dmyToIso().toString(),
                    districtId = addEditJobViewModel.districtId.value.toString(),
                    gender = addEditJobViewModel.selectedGender,
                    instagramLink = "test",
                    latitude = addEditJobViewModel.latitude.value!!,
                    longitude = addEditJobViewModel.longitude.value!!,
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

    private fun edit() {
        binding.apply {
            addEditJobViewModel.editJob(
                JobEditRequest(
                    benefit = benefitEt.editText?.text.toString(),
                    categoryId = addEditJobViewModel.categoryId.value.toString(),
                    deadline = deadlineEt.editText?.text.toString().dmyToIso().toString(),
                    districtId = addEditJobViewModel.districtId.value.toString(),
                    gender = addEditJobViewModel.selectedGender,
                    instagramLink = "test",
                    latitude = addEditJobViewModel.latitude.value!!,
                    longitude = addEditJobViewModel.longitude.value!!,
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
                    workingTime = workingTimeEt.editText?.text.toString(),
                    id = addEditJobViewModel.jobId
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
                        loadingDialog.dismiss()
                        Toast.makeText(requireContext(), "successfully updated", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun saveState() {
        binding.apply {
            addEditJobViewModel.setBenefit(benefitEt.editText?.text.toString())
            addEditJobViewModel.setDeadline(deadlineEt.editText?.text.toString())
            addEditJobViewModel.setMaxAge(
                if (maxAgeEt.editText?.text.toString()
                        .isNotEmpty()
                ) maxAgeEt.editText?.text.toString().toInt() else 0
            )
            addEditJobViewModel.setMinAge(
                if (minAgeEt.editText?.text.toString()
                        .isNotEmpty()
                ) minAgeEt.editText?.text.toString().toInt() else 0
            )
            addEditJobViewModel.setPhoneNumber(phoneNumberEt.editText?.text.toString())
            addEditJobViewModel.setRequirement(requirementEt.editText?.text.toString())
            addEditJobViewModel.setSalary(
                if (salaryEt.editText?.text.toString()
                        .isNotEmpty()
                ) salaryEt.editText?.text.toString().filter { !it.isWhitespace() }.toInt() else 0
            )
            addEditJobViewModel.setTgUsername(tgUserNameEt.editText?.text.toString())
            addEditJobViewModel.setTitle(titleEt.editText?.text.toString())
            addEditJobViewModel.setWorkingSchedule(workingScheduleEt.editText?.text.toString())
            addEditJobViewModel.setWorkingSchedule(workingTimeEt.editText?.text.toString())
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
            addEditJobViewModel.benefit.observe(viewLifecycleOwner) {
                benefitEt.editText?.setText(it)
            }
            addEditJobViewModel.deadline.observe(viewLifecycleOwner) {
                deadlineEt.editText?.setText(it)
            }
            addEditJobViewModel.maxAge.observe(viewLifecycleOwner) {
                if (it == 0)
                    maxAgeEt.editText?.setText("")
                else
                    maxAgeEt.editText?.setText(it.toString())
            }
            addEditJobViewModel.minAge.observe(viewLifecycleOwner) {
                if (it == 0)
                    minAgeEt.editText?.setText("")
                else
                    minAgeEt.editText?.setText(it.toString())
            }
            addEditJobViewModel.phoneNumber.observe(viewLifecycleOwner) {
                phoneNumberEt.editText?.setText(it)
            }
            addEditJobViewModel.requirement.observe(viewLifecycleOwner) {
                requirementEt.editText?.setText(it)
            }
            addEditJobViewModel.salary.observe(viewLifecycleOwner) {
                if (it == 0)
                    salaryEt.editText?.setText("")
                else
                    salaryEt.editText?.setText(it.toString())
            }
            addEditJobViewModel.tgUserName.observe(viewLifecycleOwner) {
                tgUserNameEt.editText?.setText(it)
            }
            addEditJobViewModel.title.observe(viewLifecycleOwner) {
                titleEt.editText?.setText(it)
            }
            addEditJobViewModel.workingSchedule.observe(viewLifecycleOwner) {
                workingScheduleEt.editText?.setText(it)
            }
            addEditJobViewModel.workingTime.observe(viewLifecycleOwner) {
                workingTimeEt.editText?.setText(it)
            }

            addEditJobViewModel.gender.observe(viewLifecycleOwner) {
                if (it == GenderEnum.MALE.label) {
                    genderLayout.selectMale(resources)
                } else if (it == GenderEnum.FEMALE.label) {
                    genderLayout.selectFemale(resources)
                }
            }

            addEditJobViewModel.categoryIndex.observe(viewLifecycleOwner) {
                jobCategoryChoice.setSelection(it)
            }
            addEditJobViewModel.districtIndex.observe(viewLifecycleOwner) {
                districtChoice.setSelection(it)
            }
            addEditJobViewModel.regionIndex.observe(viewLifecycleOwner) {
                regionChoice.setSelection(it)
            }

            var isLocationAvailable = false
            addEditJobViewModel.latitude.observe(viewLifecycleOwner) {
                isLocationAvailable = it != 0.0
            }
            addEditJobViewModel.longitude.observe(viewLifecycleOwner) {
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
                    addEditJobViewModel.setDistrictId(districtList[position].id)
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
                addEditJobViewModel.setCategoryId(jobCategoryList[position].id)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}