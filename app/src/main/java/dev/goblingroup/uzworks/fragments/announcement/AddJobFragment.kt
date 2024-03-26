package dev.goblingroup.uzworks.fragments.announcement

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.google.android.material.textfield.TextInputLayout
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
import dev.goblingroup.uzworks.utils.DateEnum
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.clear
import dev.goblingroup.uzworks.utils.dmyToIso
import dev.goblingroup.uzworks.utils.extractDateValue
import dev.goblingroup.uzworks.utils.formatPhoneNumber
import dev.goblingroup.uzworks.utils.formatSalary
import dev.goblingroup.uzworks.utils.formatTgUsername
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.utils.selectFemale
import dev.goblingroup.uzworks.utils.selectMale
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

            minAgeEt.editText?.addTextChangedListener(object : TextWatcher {
                var isFormatting = false

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (isFormatting)
                        return

                    if (s.toString() == "0")
                        return

                    isFormatting = true
                    if (s.toString().length > 2) {
                        minAgeEt.editText?.setText(s.toString().substring(0, 2))
                        minAgeEt.editText?.setSelection(2)
                    }

                    isFormatting = false
                }

            })

            maxAgeEt.editText?.addTextChangedListener(object : TextWatcher {
                var isFormatting = false

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (isFormatting)
                        return

                    if (s.toString() == "0")
                        return

                    isFormatting = true
                    if (s.toString().length > 2) {
                        maxAgeEt.editText?.setText(s.toString().substring(0, 2))
                        maxAgeEt.editText?.setSelection(2)
                    }

                    isFormatting = false
                }

            })

            salaryEt.editText?.addTextChangedListener(object : TextWatcher {
                private var isFormatting = false

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (isFormatting)
                        return

                    isFormatting = true
                    val formattedSalary = s?.filter { !it.isWhitespace() }.toString().formatSalary()
                    salaryEt.editText?.setText(formattedSalary)
                    salaryEt.editText?.setSelection(formattedSalary.length)

                    isFormatting = false
                }

            })

            phoneNumberEt.editText?.setText(resources.getString(R.string.phone_number_prefix))
            phoneNumberEt.editText?.addTextChangedListener(object : TextWatcher {
                private var isFormatting = false

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (isFormatting)
                        return

                    isFormatting = true
                    val newText = s.toString().filter { !it.isWhitespace() }
                    val oldText =
                        phoneNumberEt.editText?.tag.toString().filter { !it.isWhitespace() }
                    val formattedPhone =
                        s?.filter { !it.isWhitespace() }.toString()
                            .formatPhoneNumber(newText.length < oldText.length)
                    phoneNumberEt.editText?.setText(formattedPhone)
                    phoneNumberEt.editText?.setSelection(formattedPhone.length)
                    phoneNumberEt.tag = formattedPhone

                    isFormatting = false
                }
            })

            tgUserNameEt.editText?.addTextChangedListener(object : TextWatcher {
                var isFormatting = false

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (isFormatting)
                        return
                    isFormatting = true
                    val formattedTgUsername = s.toString().formatTgUsername()
                    tgUserNameEt.editText?.setText(formattedTgUsername)
                    tgUserNameEt.editText?.setSelection(formattedTgUsername.length)

                    isFormatting = false
                }

            })

            genderLayout.apply {
                var selectedGender = ""

                maleBtn.setOnClickListener {
                    addJobViewModel.gender.observe(viewLifecycleOwner) {
                        selectedGender = it
                    }
                    if (selectedGender == GenderEnum.FEMALE.label || selectedGender.isEmpty()) {
                        addJobViewModel.setGender(GenderEnum.MALE.label)
                        selectMale(resources)
                    }
                }
                femaleBtn.setOnClickListener {
                    addJobViewModel.gender.observe(viewLifecycleOwner) {
                        selectedGender = it
                    }
                    if (selectedGender == GenderEnum.MALE.label || selectedGender.isEmpty()) {
                        addJobViewModel.setGender(GenderEnum.FEMALE.label)
                        selectFemale(resources)
                    }
                }
            }

            saveBtn.setOnClickListener {
                if (isFormValid()) {
                    createJob()
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
                    resId = R.id.selectJobLocationFragment,
                    args = bundle,
                    navOptions = getNavOptions()
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

            jobCategoryChoice.setOnClickListener {
                jobCategoryLayout.isErrorEnabled = false
            }
        }
    }

    private fun saveState() {
        binding.apply {
            addJobViewModel.setBenefit(benefitEt.editText?.text.toString())
            addJobViewModel.setDeadline(deadlineEt.editText?.text.toString())
            addJobViewModel.setMaxAge(if (maxAgeEt.editText?.text.toString().isNotEmpty()) maxAgeEt.editText?.text.toString().toInt() else 0)
            addJobViewModel.setMinAge(if (minAgeEt.editText?.text.toString().isNotEmpty()) minAgeEt.editText?.text.toString().toInt() else 0)
            addJobViewModel.setPhoneNumber(phoneNumberEt.editText?.text.toString())
            addJobViewModel.setRequirement(requirementEt.editText?.text.toString())
            addJobViewModel.setSalary(if (salaryEt.editText?.text.toString().isNotEmpty()) salaryEt.editText?.text.toString().filter { !it.isWhitespace() }.toInt() else 0)
            addJobViewModel.setTgUsername(tgUserNameEt.editText?.text.toString())
            addJobViewModel.setTitle(titleEt.editText?.text.toString())
            addJobViewModel.setWorkingSchedule(workingScheduleEt.editText?.text.toString())
            addJobViewModel.setWorkingSchedule(workingTimeEt.editText?.text.toString())
        }
    }

    private fun createJob() {
        binding.apply {
            lifecycleScope.launch {
                var categoryId = ""
                addJobViewModel.categoryId.observe(viewLifecycleOwner) {
                    categoryId = it
                }
                var districtId = ""
                addJobViewModel.districtId.observe(viewLifecycleOwner) {
                    districtId = it
                }
                var gender = ""
                addJobViewModel.gender.observe(viewLifecycleOwner) {
                    gender = it
                }
                var latitude = 0.0
                addJobViewModel.latitude.observe(viewLifecycleOwner) {
                    latitude = it
                }
                var longitude = 0.0
                addJobViewModel.longitude.observe(viewLifecycleOwner) {
                    longitude = it
                }
                securedJobViewModel.createJob(
                    jobCreateRequest = JobCreateRequest(
                        benefit = benefitEt.editText?.text.toString(),
                        categoryId = categoryId,
                        deadline = deadlineEt.editText?.text.toString().dmyToIso().toString(),
                        districtId = districtId,
                        gender = gender,
                        instagramLink = "link of post on instagram",
                        latitude = latitude,
                        longitude = longitude,
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
                if (it == GenderEnum.MALE.label) {
                    genderLayout.selectMale(resources)
                } else if (it == GenderEnum.FEMALE.label) {
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
            addJobViewModel.gender.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.confirm_gender),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            if (workingTimeEt.editText?.text.toString().isEmpty()) {
                workingTimeEt.isErrorEnabled = true
                workingTimeEt.error = resources.getString(R.string.working_time_error)
                isValid = false
            }
            if (workingScheduleEt.editText?.text.toString().isEmpty()) {
                workingScheduleEt.isErrorEnabled = true
                workingScheduleEt.error = resources.getString(R.string.working_schedule_error)
            }
            if (tgUserNameEt.editText?.text.toString() == "@") {
                tgUserNameEt.isErrorEnabled = true
                tgUserNameEt.error = resources.getString(R.string.tg_username_error)
                isValid = false
            }
            if (phoneNumberEt.editText?.text.toString()
                    .filter { !it.isWhitespace() }.length != 13
            ) {
                phoneNumberEt.isErrorEnabled = true
                phoneNumberEt.error = resources.getString(R.string.phone_number_error)
                isValid = false
            }
            addJobViewModel.districtId.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    districtLayout.endIconMode = TextInputLayout.END_ICON_NONE
                    districtChoice.error = resources.getString(R.string.district_error)
                    isValid = false
                }
            }
            addJobViewModel.categoryId.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    jobCategoryLayout.endIconMode = TextInputLayout.END_ICON_NONE
                    jobCategoryChoice.error = resources.getString(R.string.job_category_error)
                    isValid = false
                }
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