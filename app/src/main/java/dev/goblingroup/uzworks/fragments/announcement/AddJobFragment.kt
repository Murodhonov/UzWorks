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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.databinding.FragmentAddJobBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogItemBinding
import dev.goblingroup.uzworks.models.request.JobCreateRequest
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.convertPhoneNumber
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

    private val addJobViewModel: AddJobViewModel by activityViewModels()

    private val addressViewModel: AddressViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingDialogItemBinding: LoadingDialogItemBinding

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

            phoneNumberEt.editText?.setText(
                addJobViewModel.phoneNumber.value.toString().convertPhoneNumber()
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
                findNavController().navigate(
                    resId = R.id.action_addJobFragment_to_selectJobLocationFragment,
                    args = null
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.apply {
            addJobViewModel.setTitle(titleEt.editText?.text.toString())
            addJobViewModel.setSalary(
                if (salaryEt.editText?.text.toString()
                        .isNotEmpty()
                ) salaryEt.editText?.text.toString().filter { !it.isWhitespace() }.toInt() else 0
            )
            addJobViewModel.setWorkingTime(workingTimeEt.editText?.text.toString())
            addJobViewModel.setWorkingSchedule(workingScheduleEt.editText?.text.toString())
            addJobViewModel.setDeadline(deadlineEt.editText?.text.toString())
            addJobViewModel.setTgUsername(tgUserNameEt.editText?.text.toString())
            addJobViewModel.setBenefit(benefitEt.editText?.text.toString())
            addJobViewModel.setRequirement(requirementEt.editText?.text.toString())
            addJobViewModel.setMinAge(
                if (minAgeEt.editText?.text.toString()
                        .isNotEmpty()
                ) minAgeEt.editText?.text.toString().toInt() else 0
            )
            addJobViewModel.setMaxAge(
                if (maxAgeEt.editText?.text.toString()
                        .isNotEmpty()
                ) maxAgeEt.editText?.text.toString().toInt() else 0
            )
        }
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            addJobViewModel.title.observe(viewLifecycleOwner) {
                titleEt.editText?.setText(it)
            }
            addJobViewModel.salary.observe(viewLifecycleOwner) {
                if (it == 0)
                    salaryEt.editText?.setText("")
                else
                    salaryEt.editText?.setText(it.toString())
            }
            addJobViewModel.gender.observe(viewLifecycleOwner) {
                if (it == GenderEnum.MALE.code) {
                    genderLayout.selectMale(resources)
                } else if (it == GenderEnum.FEMALE.code) {
                    genderLayout.selectFemale(resources)
                }
            }
            addJobViewModel.workingTime.observe(viewLifecycleOwner) {
                workingTimeEt.editText?.setText(it)
            }
            addJobViewModel.workingSchedule.observe(viewLifecycleOwner) {
                workingScheduleEt.editText?.setText(it)
            }
            addJobViewModel.deadline.observe(viewLifecycleOwner) {
                deadlineEt.editText?.setText(it)
            }
            addJobViewModel.tgUserName.observe(viewLifecycleOwner) {
                tgUserNameEt.editText?.setText(it)
            }
            addJobViewModel.benefit.observe(viewLifecycleOwner) {
                benefitEt.editText?.setText(it)
            }
            addJobViewModel.requirement.observe(viewLifecycleOwner) {
                requirementEt.editText?.setText(it)
            }
            addJobViewModel.minAge.observe(viewLifecycleOwner) {
                if (it == 0)
                    minAgeEt.editText?.setText("")
                else
                    minAgeEt.editText?.setText(it.toString())
            }
            addJobViewModel.maxAge.observe(viewLifecycleOwner) {
                if (it == 0)
                    maxAgeEt.editText?.setText("")
                else
                    maxAgeEt.editText?.setText(it.toString())
            }
            if (addJobViewModel.latitude.value != 0.0 && addJobViewModel.longitude.value != 0.0) {
                selectAddressTv.text = resources.getString(R.string.location_saved)
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
        Toast.makeText(requireContext(), "failed to create job", Toast.LENGTH_SHORT).show()
    }

    private fun loading() {
        try {
            loadingDialog.show()
        } catch (e: Exception) {
            loadingDialog = AlertDialog.Builder(requireContext()).create()
            loadingDialogItemBinding = LoadingDialogItemBinding.inflate(layoutInflater)
            loadingDialog.setView(loadingDialogItemBinding.root)
            loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loadingDialog.setCancelable(false)
            loadingDialog.show()
        }
    }

    private fun success() {
        loadingDialog.dismiss()
        Toast.makeText(requireContext(), "successfully created", Toast.LENGTH_SHORT).show()
        addJobViewModel.clearLiveData()
        findNavController().popBackStack()
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
                addJobViewModel.setRegionId(regionList[position].id)
                addJobViewModel.setRegionIndex(position)
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