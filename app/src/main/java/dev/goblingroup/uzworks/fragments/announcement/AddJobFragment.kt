package dev.goblingroup.uzworks.fragments.announcement

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentAddJobBinding
import dev.goblingroup.uzworks.models.request.JobCreateRequest
import dev.goblingroup.uzworks.models.response.JobCreateResponse
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.DateEnum
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.stringDateToString
import dev.goblingroup.uzworks.utils.stringToDate
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.DistrictViewModel
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import dev.goblingroup.uzworks.vm.RegionViewModel
import dev.goblingroup.uzworks.vm.SecuredJobViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddJobFragment : Fragment() {

    private var _binding: FragmentAddJobBinding? = null
    private val binding get() = _binding!!

    private val regionViewModel: RegionViewModel by viewModels()
    private val districtViewModel: DistrictViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()
    private val securedJobViewModel: SecuredJobViewModel by viewModels()

    private var selectedDistrictId = ""
    private var selectedCategoryId = ""
    private var selectedGender = GenderEnum.MALE.label

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddJobBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            topTv.isSelected = true
            loadRegions()
            loadDistricts()
            loadCategories()

            back.setOnClickListener {
                findNavController().popBackStack()
            }

            deadlineBtn.setOnClickListener {
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        val selectedCalendar = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }

                        val currentCalendar = Calendar.getInstance()

                        if (selectedCalendar.before(currentCalendar)) {
                            Toast.makeText(
                                requireContext(),
                                "Cannot select date before current date",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val formatter = SimpleDateFormat(
                                "dd.MM.yyyy", Locale.getDefault()
                            )
                            deadlineBtn.strokeColor = resources.getColor(R.color.black_blue)
                            deadlineTv.text = formatter.format(selectedCalendar.time)
                        }
                    },
                    deadlineTv.stringToDate(DateEnum.YEAR.dateLabel),
                    deadlineTv.stringToDate(DateEnum.MONTH.dateLabel),
                    deadlineTv.stringToDate(DateEnum.DATE.dateLabel)
                )

                datePickerDialog.show()
            }

            salaryEt.editText?.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    // When losing focus, append " so'm" to the salary
                    val currentText = salaryEt.editText?.text.toString().trim()
                    if (currentText.isNotEmpty()) {
                        salaryEt.editText?.setText("$currentText so'm")
                    }
                } else {
                    // When gaining focus, remove " so'm" suffix
                    val currentText = salaryEt.editText?.text.toString().trim()
                    if (currentText.endsWith(" so'm")) {
                        val newSalary = currentText.substring(0, currentText.length - 5)
                        salaryEt.editText?.setText(newSalary)
                        salaryEt.editText?.setSelection(salaryEt.editText?.text.toString().length)
                    }
                }
            }

            genderLayout.apply {
                maleBtn.setOnClickListener {
                    if (selectedGender == GenderEnum.FEMALE.label) {
                        selectedGender = GenderEnum.MALE.label
                        maleStroke.setBackgroundResource(R.drawable.gender_stroke_selected)
                        femaleStroke.setBackgroundResource(R.drawable.gender_stroke_unselected)
                        maleCircle.visibility = View.VISIBLE
                        femaleCircle.visibility = View.GONE
                        maleTv.setTextColor(resources.getColor(R.color.black_blue))
                        femaleTv.setTextColor(resources.getColor(R.color.text_color))
                        maleBtn.strokeColor = resources.getColor(R.color.black_blue)
                        femaleBtn.strokeColor = resources.getColor(R.color.text_color)
                    }
                }
                femaleBtn.setOnClickListener {
                    if (selectedGender == GenderEnum.MALE.label) {
                        selectedGender = GenderEnum.FEMALE.label
                        femaleStroke.setBackgroundResource(R.drawable.gender_stroke_selected)
                        maleStroke.setBackgroundResource(R.drawable.gender_stroke_unselected)
                        femaleCircle.visibility = View.VISIBLE
                        maleCircle.visibility = View.GONE
                        femaleTv.setTextColor(resources.getColor(R.color.black_blue))
                        maleTv.setTextColor(resources.getColor(R.color.text_color))
                        femaleBtn.strokeColor = resources.getColor(R.color.black_blue)
                        maleBtn.strokeColor = resources.getColor(R.color.text_color)
                    }
                }
            }

            saveBtn.setOnClickListener {
                if (isFormValid()) {
                    createJob()
                } else {
                    Toast.makeText(requireContext(), "fill forms", Toast.LENGTH_SHORT).show()
                }
            }
            cancelBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            titleEt.editText?.doAfterTextChanged {
                if (titleEt.isErrorEnabled && it.toString().isNotEmpty()) {
                    titleEt.isErrorEnabled = false
                }
            }

            salaryEt.editText?.doAfterTextChanged {
                if (salaryEt.isErrorEnabled && it.toString().isNotEmpty()) {
                    salaryEt.isErrorEnabled = false
                }
            }

            workingTimeEt.editText?.doAfterTextChanged {
                if (workingTimeEt.isErrorEnabled && it.toString().isNotEmpty()) {
                    workingTimeEt.isErrorEnabled = false
                }
            }

            tgUserNameEt.editText?.doAfterTextChanged {
                if (tgUserNameEt.isErrorEnabled && it.toString().isNotEmpty()) {
                    tgUserNameEt.isErrorEnabled = false
                }
            }

            instagramUsernameEt.editText?.doAfterTextChanged {
                if (instagramUsernameEt.isErrorEnabled && it.toString().isNotEmpty()) {
                    instagramUsernameEt.isErrorEnabled = false
                }
            }

            phoneNumberEt.editText?.doAfterTextChanged {
                if (phoneNumberEt.isErrorEnabled && it.toString().isNotEmpty()) {
                    phoneNumberEt.isErrorEnabled = false
                }
            }

            orientationEt.editText?.doAfterTextChanged {
                if (orientationEt.isErrorEnabled && it.toString().isNotEmpty()) {
                    orientationEt.isErrorEnabled = false
                }
            }
        }
    }

    private fun createJob() {
        binding.apply {
            lifecycleScope.launch {
                securedJobViewModel.createJob(
                    jobCreateRequest = JobCreateRequest(
                        benefit = benefitEt.editText?.text.toString(),
                        categoryId = selectedCategoryId,
                        deadline = deadlineTv.stringDateToString(),
                        districtId = selectedDistrictId,
                        gender = selectedGender,
                        instagramLink = instagramUsernameEt.editText?.text.toString(),
                        latitude = 41.3409,
                        longitude = 69.2867,
                        maxAge = maxAgeEt.editText?.text.toString().toInt(),
                        minAge = minAgeEt.editText?.text.toString().toInt(),
                        phoneNumber = phoneNumberEt.editText?.text.toString(),
                        requirement = requirementEt.editText?.text.toString(),
                        salary = salaryEt.editText?.text.toString()
                            .substring(0, salaryEt.editText?.text.toString().length - 5)
                            .toInt(),
                        telegramLink = "link of post on telegram channel",
                        tgUserName = tgUserNameEt.editText?.text.toString(),
                        title = titleEt.editText?.text.toString(),
                        workingSchedule = workingScheduleEt.editText?.text.toString(),
                        workingTime = workingTimeEt.editText?.text.toString()
                    )
                ).observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "some error on creating job",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(TAG, "createJob: ${it.error}")
                            Log.e(
                                TAG,
                                "createJob: ${it.error.stackTrace.joinToString()}"
                            )
                            Log.e(TAG, "createJob: ${it.error.message}")
                        }

                        is ApiStatus.Loading -> {
                            Toast.makeText(requireContext(), "loading", Toast.LENGTH_SHORT)
                                .show()
                        }

                        is ApiStatus.Success -> {
                            Toast.makeText(
                                requireContext(),
                                "successfully created job",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d(
                                TAG,
                                "createJob: ${it.response as JobCreateResponse}"
                            )
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun isFormValid(): Boolean {
        binding.apply {
            var isValid = true
            if (deadlineTv.text == "Deadline") {
                deadlineBtn.strokeColor = resources.getColor(R.color.red)
                deadlineTv.text = "Select deadline"
                isValid = false
            }
            if (titleEt.editText?.text.toString().isEmpty()) {
                titleEt.isErrorEnabled = true
                titleEt.error = "Enter title"
                isValid = false
            }
            if (salaryEt.editText?.text.toString().isEmpty()) {
                salaryEt.isErrorEnabled = true
                salaryEt.error = "Enter salary"
                isValid = false
            }
            if (workingTimeEt.editText?.text.toString().isEmpty()) {
                workingTimeEt.isErrorEnabled = true
                workingTimeEt.error = "Enter working time"
                isValid = false
            }
            if (tgUserNameEt.editText?.text.toString().isEmpty()) {
                tgUserNameEt.isErrorEnabled = true
                tgUserNameEt.error = "Enter your telegram username"
                isValid = false
            }
            if (instagramUsernameEt.editText?.text.toString().isEmpty()) {
                instagramUsernameEt.isErrorEnabled = true
                instagramUsernameEt.error = "Enter your instagram username"
                isValid = false
            }
            if (phoneNumberEt.editText?.text.toString().isEmpty()) {
                phoneNumberEt.isErrorEnabled = true
                phoneNumberEt.error = "Enter your phone number"
                isValid = false
            }
            if (orientationEt.editText?.text.toString().isEmpty()) {
                orientationEt.isErrorEnabled = true
                orientationEt.error = "Enter orientation"
                isValid = false
            }
            if (selectedDistrictId == "") {
                districtLayout.endIconMode = TextInputLayout.END_ICON_NONE
                districtChoice.error = "Select your district"
                isValid = false
            }
            if (selectedCategoryId == "") {
                jobCategoryLayout.endIconMode = TextInputLayout.END_ICON_NONE
                jobCategoryChoice.error = "Select your job category"
                isValid = false
            }
            return isValid
        }
    }

    private fun loadRegions() {
        lifecycleScope.launch {
            regionViewModel.regionLiveData.observe(viewLifecycleOwner) {
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
                val districtList = districtViewModel.listDistrictsByRegionId(regionId)
                val districtAdapter = ArrayAdapter(
                    requireContext(),
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                    districtList.map { it.name }
                )
                districtChoice.setAdapter(districtAdapter)
                districtChoice.setOnItemClickListener { parent, view, position, id ->
                    selectedDistrictId = districtList[position].id
                }
            }
        }
    }

    private fun loadDistricts() {
        lifecycleScope.launch {
            districtViewModel.districtLiveData.observe(viewLifecycleOwner) {
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
                selectedCategoryId = jobCategoryList[position].id
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}