package dev.goblingroup.uzworks.fragments.announcement

import android.annotation.SuppressLint
import android.os.Bundle
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
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.databinding.FragmentAddEditWorkerBinding
import dev.goblingroup.uzworks.models.request.WorkerCreateRequest
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.dmyToIso
import dev.goblingroup.uzworks.vm.AddWorkerViewModel
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditWorkerFragment : Fragment() {

    private var _binding: FragmentAddEditWorkerBinding? = null
    private val binding get() = _binding!!

    private val addWorkerViewModel: AddWorkerViewModel by viewModels()

    private val addressViewModel: AddressViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditWorkerBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            back.setOnClickListener {
                findNavController().popBackStack()
            }

            topTv.isSelected = true

            loadRegions()
            loadDistricts()
            loadCategories()

            addWorkerViewModel.controlInput(
                requireContext(),
                resources,
                deadlineEt,
                birthdayEt,
                titleEt,
                salaryEt,
                genderLayout,
                workingTimeEt,
                tgUserNameEt,
                phoneNumberEt,
            )

            saveBtn.setOnClickListener {
                if (addWorkerViewModel.isFormValid(
                        requireContext(),
                        resources,
                        deadlineEt,
                        birthdayEt,
                        titleEt,
                        salaryEt,
                        workingTimeEt,
                        tgUserNameEt,
                        phoneNumberEt,
                        districtLayout,
                        jobCategoryLayout
                    )
                ) {
                    createWorker()
                }
            }
            cancelBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            districtLayout.setOnClickListener {
                Toast.makeText(requireContext(), "click", Toast.LENGTH_SHORT).show()
                if (districtLayout.isErrorEnabled) {
                    districtLayout.isErrorEnabled = false
                }
            }
        }
    }

    private fun createWorker() {
        binding.apply {
            lifecycleScope.launch {
                addWorkerViewModel.addWorker(
                    workerCreateRequest = WorkerCreateRequest(
                        birthDate = birthdayEt.editText?.text.toString().dmyToIso().toString(),
                        categoryId = addWorkerViewModel.selectedCategoryId,
                        deadline = deadlineEt.editText?.text.toString().dmyToIso().toString(),
                        districtId = addWorkerViewModel.selectedDistrictId,
                        gender = addWorkerViewModel.selectedGender,
                        instagramLink = "link to post on ig",
                        location = "kajsdjashd",
                        phoneNumber = phoneNumberEt.editText?.text.toString(),
                        salary = salaryEt.editText?.text.toString()
                            .substring(0, salaryEt.editText?.text.toString().length - 5)
                            .toInt(),
                        telegramLink = "link to post on tg channel",
                        tgUserName = tgUserNameEt.editText?.text.toString(),
                        title = titleEt.editText?.text.toString(),
                        workingSchedule = "some working schedule",
                        workingTime = workingTimeEt.editText?.text.toString()
                    )
                ).observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "some error on creating worker",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(TAG, "createWorker: ${it.error}")
                            Log.e(TAG, "createWorker: ${it.error.stackTrace.joinToString()}")
                            Log.e(TAG, "createWorker: ${it.error.message}")
                        }

                        is ApiStatus.Loading -> {
                            Toast.makeText(requireContext(), "loading", Toast.LENGTH_SHORT)
                                .show()
                        }

                        is ApiStatus.Success -> {
                            Toast.makeText(
                                requireContext(),
                                "successfully created worker",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d(TAG, "createWorker: ${it.response as WorkerResponse}")
                            findNavController().popBackStack()
                        }
                    }
                }
            }
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
            regionChoice.threshold = 1
            regionChoice.setAdapter(regionAdapter)

            regionChoice.setOnItemClickListener { parent, view, position, id ->
                districtChoice.text.clear()
                districtChoice.hint = getString(R.string.select_district)
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
                districtChoice.threshold = 1
                districtChoice.setAdapter(districtAdapter)
                districtChoice.setOnItemClickListener { parent, view, position, id ->
                    if (districtLayout.isErrorEnabled) districtLayout.isErrorEnabled = false
                    addWorkerViewModel.selectedDistrictId = districtList[position].id
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
            val jobCategoryAdapter =
                ArrayAdapter(
                    requireContext(),
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                    jobCategoryList.map { it.title }
                )
            jobCategoryChoice.threshold = 1
            jobCategoryChoice.setAdapter(jobCategoryAdapter)
            jobCategoryChoice.setOnItemClickListener { parent, view, position, id ->
                if (jobCategoryLayout.isErrorEnabled) {
                    jobCategoryLayout.isErrorEnabled = false
                }
                addWorkerViewModel.selectedCategoryId = jobCategoryList[position].id
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}