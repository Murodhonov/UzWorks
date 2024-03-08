package dev.goblingroup.uzworks.fragments.announcement

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.databinding.FragmentAddJobBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogItemBinding
import dev.goblingroup.uzworks.models.request.JobCreateRequest
import dev.goblingroup.uzworks.models.response.JobCreateResponse
import dev.goblingroup.uzworks.utils.ConstValues.DEFAULT_LATITUDE
import dev.goblingroup.uzworks.utils.ConstValues.DEFAULT_LONGITUDE
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.DateEnum
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.clear
import dev.goblingroup.uzworks.utils.dmyToIso
import dev.goblingroup.uzworks.utils.extractDateValue
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.vm.AddJobViewModel
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import dev.goblingroup.uzworks.vm.SecuredJobViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddJobFragment : Fragment() {

    private var _binding: FragmentAddJobBinding? = null
    private val binding get() = _binding!!

    private val addressViewModel: AddressViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()
    private val securedJobViewModel: SecuredJobViewModel by viewModels()
    private val addJobViewModel by viewModels<AddJobViewModel>()

    private var selectedDistrictId = ""
    private var selectedCategoryId = ""
    private var selectedGender = ""

    private var selectedLocation: LatLng? = null

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
            topTv.isSelected = true
            loadRegions()
            loadDistricts()
            loadCategories()

            back.setOnClickListener {
                findNavController().popBackStack()
            }

            deadlineEt.clear()

            deadlineEt.editText?.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
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
                                deadlineEt.isErrorEnabled = false
                                deadlineEt.editText?.setText(formatter.format(selectedCalendar.time))
                            }
                        },
                        deadlineEt.editText?.text.toString()
                            .extractDateValue(DateEnum.YEAR.dateLabel),
                        deadlineEt.editText?.text.toString()
                            .extractDateValue(DateEnum.MONTH.dateLabel),
                        deadlineEt.editText?.text.toString()
                            .extractDateValue(DateEnum.DATE.dateLabel)
                    )

                    datePickerDialog.show()
                }
                true
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
                    if (selectedGender == GenderEnum.FEMALE.label || selectedGender.isEmpty()) {
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
                    if (selectedGender == GenderEnum.MALE.label || selectedGender.isEmpty()) {
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

            selectAddressBtn.setOnClickListener {
                saveState()
                val bundle = Bundle()
                bundle.putDouble("latitude", selectedLocation?.latitude ?: DEFAULT_LATITUDE)
                bundle.putDouble("longitude", selectedLocation?.longitude ?: DEFAULT_LONGITUDE)
                if (selectedLocation == null)
                    bundle.putBoolean("job_creating", true)
                Log.d(
                    TAG,
                    "onViewCreated: map testing $selectedLocation passed from ${this@AddJobFragment::class.java.simpleName}"
                )
                findNavController().navigate(
                    resId = R.id.jobAddressFragment,
                    args = bundle,
                    navOptions = getNavOptions()
                )
            }

            setFragmentResultListener("lat_lng") { _, bundle ->
                selectedLocation =
                    LatLng(bundle.getDouble("latitude"), bundle.getDouble("longitude"))
                Log.d(
                    TAG,
                    "onViewCreated: map testing $selectedLocation received in ${this@AddJobFragment::class.java.simpleName}"
                )
                selectAddressTv.text = resources.getString(R.string.location_saved)
            }
        }
    }

    private fun saveState() {
        binding.apply {
            addJobViewModel.setBenefit(benefitEt.editText?.text.toString())
            addJobViewModel.setCategoryId(selectedCategoryId)
            addJobViewModel.setDeadline(deadlineEt.editText?.text.toString())
            addJobViewModel.setDistrictId(selectedDistrictId)
            addJobViewModel.setGender(selectedGender)
            addJobViewModel.setInstagramLink("")
            addJobViewModel.setMaxAge(if (maxAgeEt.editText?.text.toString().isNotEmpty()) maxAgeEt.editText?.text.toString().toInt() else 0)
            addJobViewModel.setMinAge(if (minAgeEt.editText?.text.toString().isNotEmpty()) minAgeEt.editText?.text.toString().toInt() else 0)
            addJobViewModel.setPhoneNumber(phoneNumberEt.editText?.text.toString())
            addJobViewModel.setRequirement(requirementEt.editText?.text.toString())
            addJobViewModel.setSalary(if (salaryEt.editText?.text.toString().isNotEmpty()) salaryEt.editText?.text.toString().toInt() else 0)
            addJobViewModel.setTelegramLink("")
            addJobViewModel.setTgUsername(tgUserNameEt.editText?.text.toString())
            addJobViewModel.setTitle(titleEt.editText?.text.toString())
            addJobViewModel.setWorkingSchedule(workingScheduleEt.editText?.text.toString())
            addJobViewModel.setWorkingSchedule(workingTimeEt.editText?.text.toString())
            findNavController().navigate(
                resId = R.id.jobAddressFragment,
                args = null,
                navOptions = getNavOptions()
            )
        }
    }

    private fun createJob() {
        binding.apply {
            lifecycleScope.launch {
                securedJobViewModel.createJob(
                    jobCreateRequest = JobCreateRequest(
                        benefit = benefitEt.editText?.text.toString(),
                        categoryId = selectedCategoryId,
                        deadline = deadlineEt.editText?.text.toString().dmyToIso().toString(),
                        districtId = selectedDistrictId,
                        gender = selectedGender,
                        instagramLink = "",
                        latitude = selectedLocation?.latitude ?: DEFAULT_LATITUDE,
                        longitude = selectedLocation?.longitude ?: DEFAULT_LONGITUDE,
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
    }

    private fun error(error: Throwable) {
        Toast.makeText(requireContext(), "failed to create job", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "error: $error")
        Log.d(TAG, "error: ${error.message}")
        Log.d(TAG, "error: ${error.stackTrace}")
    }

    private fun loading() {
        loadingDialog = AlertDialog.Builder(requireContext()).create()
        val loadingBinding = LoadingDialogItemBinding.inflate(layoutInflater)
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
                Log.d(TAG, "onResume: checking lifecycle benefit: $it")
            }

        }

    }

    private fun isFormValid(): Boolean {
        binding.apply {
            var isValid = true
            if (deadlineEt.editText?.text.toString().isEmpty()) {
                deadlineEt.isErrorEnabled = true
                deadlineEt.error = resources.getString(R.string.deadline_error)
                isValid = false
            }
            if (titleEt.editText?.text.toString().isEmpty()) {
                titleEt.isErrorEnabled = true
                titleEt.error = resources.getString(R.string.title_error)
                isValid = false
            }
            if (salaryEt.editText?.text.toString().isEmpty()) {
                salaryEt.isErrorEnabled = true
                salaryEt.error = resources.getString(R.string.salary_error)
                isValid = false
            }
            if (workingTimeEt.editText?.text.toString().isEmpty()) {
                workingTimeEt.isErrorEnabled = true
                workingTimeEt.error = resources.getString(R.string.working_time_error)
                isValid = false
            }
            if (tgUserNameEt.editText?.text.toString().isEmpty()) {
                tgUserNameEt.isErrorEnabled = true
                tgUserNameEt.error = resources.getString(R.string.tg_username_error)
                isValid = false
            }
            if (phoneNumberEt.editText?.text.toString().isEmpty()) {
                phoneNumberEt.isErrorEnabled = true
                phoneNumberEt.error = resources.getString(R.string.phone_number_error)
                isValid = false
            }
            if (orientationEt.editText?.text.toString().isEmpty()) {
                orientationEt.isErrorEnabled = true
                orientationEt.error = resources.getString(R.string.orientation_error)
                isValid = false
            }
            if (selectedDistrictId == "") {
                districtLayout.endIconMode = TextInputLayout.END_ICON_NONE
                districtChoice.error = resources.getString(R.string.district_error)
                isValid = false
            }
            if (selectedCategoryId == "") {
                jobCategoryLayout.endIconMode = TextInputLayout.END_ICON_NONE
                jobCategoryChoice.error = resources.getString(R.string.job_category_error)
                isValid = false
            }
            return isValid
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
                    selectedDistrictId = districtList[position].id
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
                selectedCategoryId = jobCategoryList[position].id
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}