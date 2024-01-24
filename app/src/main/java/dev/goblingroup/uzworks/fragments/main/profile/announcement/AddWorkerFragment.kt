package dev.goblingroup.uzworks.fragments.main.profile.announcement

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapters.DistrictAdapter
import dev.goblingroup.uzworks.adapters.JobCategoryAdapter
import dev.goblingroup.uzworks.adapters.RegionAdapter
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.databinding.FragmentAddWorkerBinding
import dev.goblingroup.uzworks.models.request.WorkerRequest
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.networking.ApiClient
import dev.goblingroup.uzworks.networking.SecuredWorkerService
import dev.goblingroup.uzworks.singleton.MySharedPreference
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.DateEnum
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.utils.convertDateFormat
import dev.goblingroup.uzworks.utils.stringToDate
import dev.goblingroup.uzworks.vm.DistrictViewModel
import dev.goblingroup.uzworks.vm.DistrictViewModelFactory
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import dev.goblingroup.uzworks.vm.JobCategoryViewModelFactory
import dev.goblingroup.uzworks.vm.RegionViewModel
import dev.goblingroup.uzworks.vm.RegionViewModelFactory
import dev.goblingroup.uzworks.vm.SecuredWorkerViewModel
import dev.goblingroup.uzworks.vm.SecuredWorkerViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.coroutines.CoroutineContext

class AddWorkerFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentAddWorkerBinding? = null
    private val binding get() = _binding!!

    private lateinit var appDatabase: AppDatabase
    private lateinit var networkHelper: NetworkHelper

    private lateinit var regionViewModel: RegionViewModel
    private lateinit var regionViewModelFactory: RegionViewModelFactory

    private lateinit var districtViewModel: DistrictViewModel
    private lateinit var districtViewModelFactory: DistrictViewModelFactory

    private lateinit var jobCategoryViewModel: JobCategoryViewModel
    private lateinit var jobCategoryViewModelFactory: JobCategoryViewModelFactory

    private lateinit var securedWorkerViewModel: SecuredWorkerViewModel
    private lateinit var securedWorkerViewModelFactory: SecuredWorkerViewModelFactory

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            topTv.isSelected = true
            appDatabase = AppDatabase.getInstance(requireContext())
            networkHelper = NetworkHelper(requireContext())

            Log.d(TAG, "onViewCreated: creating securedWorkerViewModelFactory instance")
            securedWorkerViewModelFactory = SecuredWorkerViewModelFactory(
                ApiClient.securedWorkerService,
                networkHelper
            )

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
                    stringToDate(deadlineTv.text.toString(), DateEnum.YEAR.dateLabel),
                    stringToDate(deadlineTv.text.toString(), DateEnum.MONTH.dateLabel),
                    stringToDate(deadlineTv.text.toString(), DateEnum.DATE.dateLabel)
                )

                datePickerDialog.show()
            }

            birthdayBtn.setOnClickListener {
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        val selectedCalendar = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }

                        val currentCalendar = Calendar.getInstance()

                        if (selectedCalendar.after(currentCalendar)) {
                            Toast.makeText(
                                requireContext(),
                                "Cannot select date after current date",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val formatter = SimpleDateFormat(
                                "dd.MM.yyyy", Locale.getDefault()
                            )
                            birthdayBtn.strokeColor = resources.getColor(R.color.black_blue)
                            birthdayTv.text = formatter.format(selectedCalendar.time)
                        }
                    },
                    stringToDate(birthdayTv.text.toString(), DateEnum.YEAR.dateLabel),
                    stringToDate(birthdayTv.text.toString(), DateEnum.MONTH.dateLabel),
                    stringToDate(birthdayTv.text.toString(), DateEnum.DATE.dateLabel)
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

    private fun isFormValid(): Boolean {
        binding.apply {
            var isValid = true
            if (deadlineTv.text == "Deadline") {
                deadlineBtn.strokeColor = resources.getColor(R.color.red)
                deadlineTv.text = "Select deadline"
                isValid = false
            }
            if (birthdayTv.text == "Birthday") {
                birthdayBtn.strokeColor = resources.getColor(R.color.red)
                birthdayTv.text = "Select your birthday"
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

    private fun createWorker() {
        binding.apply {
            securedWorkerViewModel = ViewModelProvider(
                owner = this@AddWorkerFragment,
                factory = securedWorkerViewModelFactory
            )[SecuredWorkerViewModel::class.java]

            launch {
                securedWorkerViewModel.createWorker(
                    workerRequest = WorkerRequest(
                        birthDate = convertDateFormat(birthdayTv.text.toString()),
                        categoryId = selectedCategoryId,
                        deadline = convertDateFormat(deadlineTv.text.toString()),
                        districtId = selectedDistrictId,
                        gender = selectedGender,
                        instagramLink = instagramUsernameEt.editText?.text.toString(),
                        location = orientationEt.editText?.text.toString(),
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
                )
                    .collect {
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
                            }
                        }
                    }
            }
        }
    }

    private fun loadRegions() {
        launch {
            regionViewModelFactory = RegionViewModelFactory(
                appDatabase = appDatabase,
                regionService = ApiClient.regionService,
                regionId = "",
                districtId = "",
                networkHelper = networkHelper
            )
            regionViewModel = ViewModelProvider(
                owner = this@AddWorkerFragment,
                factory = regionViewModelFactory
            )[RegionViewModel::class.java]

            regionViewModel.getRegionStateFlow()
                .collect {
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
            val regionAdapter = RegionAdapter(
                requireContext(),
                R.layout.drop_down_item,
                regionList
            )
            regionChoice.threshold = 1
            regionChoice.setAdapter(regionAdapter)

            regionChoice.setOnItemClickListener { parent, view, position, id ->
                districtChoice.text.clear()
                districtChoice.hint = "Select district"
                setDistricts(regionAdapter.getItem(position)?.id.toString())
            }
        }
    }

    private fun setDistricts(regionId: String) {
        binding.apply {
            val districtAdapter = DistrictAdapter(
                requireContext(),
                R.layout.drop_down_item,
                districtViewModel.listDistrictsByRegionId(regionId)
            )
            districtChoice.threshold = 1
            districtChoice.setAdapter(districtAdapter)
            districtChoice.setOnItemClickListener { parent, view, position, id ->
                selectedDistrictId = districtAdapter.getItem(position).id
            }
        }
    }

    private fun loadDistricts() {
        launch {
            districtViewModelFactory = DistrictViewModelFactory(
                appDatabase = appDatabase,
                districtService = ApiClient.districtService,
                networkHelper = networkHelper,
                districtId = "",
                regionId = ""
            )

            districtViewModel = ViewModelProvider(
                owner = this@AddWorkerFragment,
                factory = districtViewModelFactory
            )[DistrictViewModel::class.java]

            districtViewModel.getDistrictStateFlow()
                .collect {
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
        launch {
            jobCategoryViewModelFactory = JobCategoryViewModelFactory(
                appDatabase = appDatabase,
                ApiClient.jobCategoryService,
                networkHelper = networkHelper
            )

            jobCategoryViewModel = ViewModelProvider(
                owner = this@AddWorkerFragment,
                factory = jobCategoryViewModelFactory
            )[JobCategoryViewModel::class.java]

            jobCategoryViewModel.getJobCategories()
                .collect {
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
                JobCategoryAdapter(requireContext(), R.layout.drop_down_item, jobCategoryList)
            jobCategoryChoice.threshold = 1
            jobCategoryChoice.setAdapter(jobCategoryAdapter)
            jobCategoryChoice.setOnItemClickListener { parent, view, position, id ->
                selectedCategoryId = jobCategoryAdapter.getItem(position)?.id.toString()
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}