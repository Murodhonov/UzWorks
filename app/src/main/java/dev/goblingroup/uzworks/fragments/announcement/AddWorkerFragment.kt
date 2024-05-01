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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.databinding.FragmentAddWorkerBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogItemBinding
import dev.goblingroup.uzworks.models.request.WorkerCreateRequest
import dev.goblingroup.uzworks.utils.ConstValues.DEFAULT_BIRTHDAY
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.convertPhoneNumber
import dev.goblingroup.uzworks.utils.dmyToIso
import dev.goblingroup.uzworks.vm.AddWorkerViewModel
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddWorkerFragment : Fragment() {

    private var _binding: FragmentAddWorkerBinding? = null
    private val binding get() = _binding!!

    private val addWorkerViewModel: AddWorkerViewModel by viewModels()

    private val addressViewModel: AddressViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingDialogItemBinding: LoadingDialogItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddWorkerBinding.inflate(layoutInflater)
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
                fragmentActivity = requireActivity(),
                deadlineEt = deadlineEt,
                birthdayEt = birthdayEt,
                titleEt = titleEt,
                salaryEt = salaryEt,
                genderLayout = genderLayout,
                workingTimeEt = workingTimeEt,
                workingScheduleEt = workingScheduleEt,
                tgUserNameEt = tgUserNameEt,
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
                val isValid = addWorkerViewModel.isFormValid(
                    resources,
                    deadlineEt,
                    birthdayEt,
                    titleEt,
                    salaryEt,
                    workingTimeEt,
                    workingScheduleEt,
                    tgUserNameEt,
                    districtLayout,
                    jobCategoryLayout
                )
                Log.d(TAG, "onViewCreated: adding worker $isValid")
                if (isValid) {
                    createWorker()
                }
            }

            cancelBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            phoneNumberEt.editText?.setText(addWorkerViewModel.phoneNumber.convertPhoneNumber())
        }
    }

    private fun createWorker() {
        binding.apply {
            lifecycleScope.launch {
                Toast.makeText(requireContext(), "creating worker", Toast.LENGTH_SHORT).show()
                addWorkerViewModel.addWorker(
                    workerCreateRequest = WorkerCreateRequest(
                        birthDate = if (birthdayEt.editText?.text.toString()
                                .isEmpty()
                        ) DEFAULT_BIRTHDAY else birthdayEt.editText?.text.toString().dmyToIso()
                            .toString(),
                        categoryId = addWorkerViewModel.jobCategoryId,
                        deadline = deadlineEt.editText?.text.toString().dmyToIso().toString(),
                        districtId = addWorkerViewModel.districtId,
                        gender = addWorkerViewModel.gender,
                        instagramLink = "",
                        phoneNumber = addWorkerViewModel.phoneNumber,
                        salary = salaryEt.editText?.text.toString().trim()
                            .filter { !it.isWhitespace() }.toInt(),
                        telegramLink = "",
                        tgUserName = tgUserNameEt.editText?.text.toString().substring(1),
                        title = titleEt.editText?.text.toString(),
                        workingSchedule = workingScheduleEt.editText?.text.toString(),
                        workingTime = workingTimeEt.editText?.text.toString()
                    )
                ).observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            failed()
                        }

                        is ApiStatus.Loading -> {
                            loading()
                        }

                        is ApiStatus.Success -> {
                            succeeded()
                        }
                    }
                }
            }
        }
    }

    private fun failed() {
        loadingDialog.dismiss()
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

    private fun succeeded() {
        loadingDialog.dismiss()
        Toast.makeText(requireContext(), "succeeded", Toast.LENGTH_SHORT).show()
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
                districtLayout.isErrorEnabled = false
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
                    districtLayout.isErrorEnabled = false
                    addWorkerViewModel.districtId = districtList[position].id
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
                addWorkerViewModel.jobCategoryId = jobCategoryList[position].id
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}