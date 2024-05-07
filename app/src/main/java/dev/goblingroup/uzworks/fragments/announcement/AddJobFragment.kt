package dev.goblingroup.uzworks.fragments.announcement

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
import android.view.inputmethod.InputMethodManager
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
import dev.goblingroup.uzworks.utils.ConstValues.JOB_ADDING
import dev.goblingroup.uzworks.utils.ConstValues.JOB_LOCATION_STATUS
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

            phoneNumberEt.editText?.setText(
                addJobViewModel.phoneNumber.value.toString().convertPhoneNumber()
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
                        districtLayout,
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
                val bundle = Bundle()
                bundle.putString(JOB_LOCATION_STATUS, JOB_ADDING)
                findNavController().navigate(
                    resId = R.id.action_addJobFragment_to_selectJobLocationFragment,
                    args = bundle
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
            titleEt.editText?.setText(addJobViewModel.title.value)
            if (addJobViewModel.salary.value == 0) {
                salaryEt.editText?.setText("")
            } else salaryEt.editText?.setText(addJobViewModel.salary.value.toString())
            when (addJobViewModel.gender.value) {
                GenderEnum.MALE.code -> genderLayout.selectMale(resources)
                GenderEnum.FEMALE.code -> genderLayout.selectFemale(resources)
            }
            workingTimeEt.editText?.setText(addJobViewModel.workingTime.value)
            workingScheduleEt.editText?.setText(addJobViewModel.workingSchedule.value)
            deadlineEt.editText?.setText(addJobViewModel.deadline.value)
            tgUserNameEt.editText?.setText(addJobViewModel.tgUserName.value)
            benefitEt.editText?.setText(addJobViewModel.benefit.value)
            requirementEt.editText?.setText(addJobViewModel.requirement.value)
            if (addJobViewModel.minAge.value == 0) {
                minAgeEt.editText?.setText("")
            } else minAgeEt.editText?.setText(addJobViewModel.minAge.value.toString())
            if (addJobViewModel.maxAge.value == 0) {
                maxAgeEt.editText?.setText("")
            } else maxAgeEt.editText?.setText(addJobViewModel.maxAge.value.toString())
            if (addJobViewModel.latitude.value != 0.0 && addJobViewModel.longitude.value != 0.0) {
                selectAddressTv.text = resources.getString(R.string.change_location)
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
        Toast.makeText(requireContext(), resources.getString(R.string.job_announcement_created), Toast.LENGTH_SHORT).show()
        addJobViewModel.clearLiveData()
        findNavController().navigate(R.id.action_addJobFragment_to_myAnnouncementsFragment)
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
                districtChoice.hint = resources.getString(R.string.select_district)
                addJobViewModel.setDistrictId("")
//                addJobViewModel.setDistrictIndex(-1)
                addJobViewModel.setRegionId(regionList[position].id)
//                addJobViewModel.setRegionIndex(position)
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
                    if (districtLayout.isErrorEnabled) districtLayout.isErrorEnabled = false
                    addJobViewModel.setDistrictId(districtList[position].id)
//                    addJobViewModel.setDistrictIndex(position)
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
                if (jobCategoryLayout.isErrorEnabled) jobCategoryLayout.isErrorEnabled = false
                addJobViewModel.setCategoryId(jobCategoryList[position].id)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}