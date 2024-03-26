package dev.goblingroup.uzworks.fragments.announcement

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.databinding.FragmentAddWorkerBinding
import dev.goblingroup.uzworks.models.request.WorkerCreateRequest
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.DateEnum
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.clear
import dev.goblingroup.uzworks.utils.dmyToIso
import dev.goblingroup.uzworks.utils.extractDateValue
import dev.goblingroup.uzworks.utils.formatPhoneNumber
import dev.goblingroup.uzworks.utils.formatSalary
import dev.goblingroup.uzworks.utils.formatTgUsername
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import dev.goblingroup.uzworks.vm.SecuredWorkerViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddWorkerFragment : Fragment() {

    private var _binding: FragmentAddWorkerBinding? = null
    private val binding get() = _binding!!

    private val addressViewModel: AddressViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()
    private val securedWorkerViewModel: SecuredWorkerViewModel by viewModels()

    private var selectedDistrictId = ""
    private var selectedCategoryId = ""
    private var selectedGender = GenderEnum.MALE.label

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
            topTv.isSelected = true

            loadRegions()
            loadDistricts()
            loadCategories()

            back.setOnClickListener {
                findNavController().popBackStack()
            }

            deadlineEt.clear()
            birthdayEt.clear()

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

            birthdayEt.editText?.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val datePickerDialog = DatePickerDialog(
                        requireContext(),
                        { _, year, month, dayOfMonth ->
                            val selectedCalendar = Calendar.getInstance().apply {
                                set(year, month, dayOfMonth)
                            }
                            if (checkBirthdate(selectedCalendar)) {
                                val formatter = SimpleDateFormat(
                                    "dd.MM.yyyy", Locale.getDefault()
                                )
                                birthdayEt.isErrorEnabled = false
                                birthdayEt.editText?.setText(formatter.format(selectedCalendar.time))
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    resources.getString(R.string.unavailable_date),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        birthdayEt.editText?.text.toString()
                            .extractDateValue(DateEnum.YEAR.dateLabel),
                        birthdayEt.editText?.text.toString()
                            .extractDateValue(DateEnum.MONTH.dateLabel),
                        birthdayEt.editText?.text.toString()
                            .extractDateValue(DateEnum.DATE.dateLabel)
                    )

                    datePickerDialog.show()
                }
                true
            }

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
                    createWorker()
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

        }
    }

    private fun checkBirthdate(selectedCalendar: Calendar): Boolean {
        val minimumAge = 16
        val minimumCalendar = Calendar.getInstance().clone() as Calendar
        minimumCalendar.add(Calendar.YEAR, -minimumAge)
        return selectedCalendar.after(minimumCalendar)
    }

    private fun isFormValid(): Boolean {
        binding.apply {
            var isValid = true
            if (deadlineEt.editText?.text.toString().isEmpty()) {
                deadlineEt.isErrorEnabled = true
                deadlineEt.error = resources.getString(R.string.deadline_error)
                isValid = false
            }
            if (birthdayEt.editText?.text.toString().isEmpty()) {
                birthdayEt.isErrorEnabled = true
                birthdayEt.error = resources.getString(R.string.birthday_error)
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

    private fun createWorker() {
        binding.apply {
            lifecycleScope.launch {
                securedWorkerViewModel.createWorker(
                    workerCreateRequest = WorkerCreateRequest(
                        birthDate = birthdayEt.editText?.text.toString().dmyToIso().toString(),
                        categoryId = selectedCategoryId,
                        deadline = deadlineEt.editText?.text.toString().dmyToIso().toString(),
                        districtId = selectedDistrictId,
                        gender = selectedGender,
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
                districtChoice.threshold = 1
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
            val jobCategoryAdapter =
                ArrayAdapter(
                    requireContext(),
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                    jobCategoryList.map { it.title }
                )
            jobCategoryChoice.threshold = 1
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