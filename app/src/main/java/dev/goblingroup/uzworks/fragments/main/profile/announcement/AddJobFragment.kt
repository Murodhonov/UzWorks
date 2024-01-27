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
import dev.goblingroup.uzworks.databinding.FragmentAddJobBinding
import dev.goblingroup.uzworks.models.request.JobRequest
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.networking.ApiClient
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.ConstValues
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
import dev.goblingroup.uzworks.vm.SecuredJobViewModel
import dev.goblingroup.uzworks.vm.SecuredJobViewModelFactory
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

class AddJobFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentAddJobBinding? = null
    private val binding get() = _binding!!

    private lateinit var appDatabase: AppDatabase
    private lateinit var networkHelper: NetworkHelper

    private lateinit var regionViewModel: RegionViewModel
    private lateinit var regionViewModelFactory: RegionViewModelFactory

    private lateinit var districtViewModel: DistrictViewModel
    private lateinit var districtViewModelFactory: DistrictViewModelFactory

    private lateinit var jobCategoryViewModel: JobCategoryViewModel
    private lateinit var jobCategoryViewModelFactory: JobCategoryViewModelFactory

    private lateinit var securedJobViewModel: SecuredJobViewModel
    private lateinit var securedJobViewModelFactory: SecuredJobViewModelFactory

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
            appDatabase = AppDatabase.getInstance(requireContext())
            networkHelper = NetworkHelper(requireContext())


            securedJobViewModelFactory = SecuredJobViewModelFactory(
                ApiClient.securedJobService,
                networkHelper = networkHelper
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
//                if (isFormValid()) {
                    createJob()
//                } else {
//                    Toast.makeText(requireContext(), "fill forms", Toast.LENGTH_SHORT).show()
//                }
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
        val jobRequest = JobRequest(
            benefit = "benefit",
            categoryId = "abd37ade-8749-4d7e-8d90-042e53d43954",
            deadline = "2024-01-26T13:11:26.743Z",
            districtId = "a7f85890-07c3-4541-b3ca-e07fc820db5f",
            gender = "Male",
            instagramLink = "sjfkdsf",
            latitude = 41.3409,
            longitude = 69.2867,
            maxAge = 20,
            minAge = 30,
            phoneNumber = "+998931895305",
            requirement = "sdfsgags",
            salary = 50000,
            telegramLink = "link of post on telegram channel",
            tgUserName = "fsdfsdf",
            title = "frggsgf",
            workingSchedule = "dfghfdjsghsjfg",
            workingTime = "hasgujhersgisgf"
        )
        Log.d(ConstValues.TAG, "createJob: requesting for create job $jobRequest")
        ApiClient.initialize(requireContext())

        ApiClient.securedJobService.createJob(
            jobRequest = jobRequest
        ).enqueue(object : Callback<JobResponse> {
            override fun onResponse(call: Call<JobResponse>, response: Response<JobResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "success", Toast.LENGTH_SHORT).show()
                    Log.d(ConstValues.TAG, "onResponse: ${response.body()}")
                } else {
                    Toast.makeText(requireContext(), "unsuccessful", Toast.LENGTH_SHORT).show()
                    Log.e(ConstValues.TAG, "onResponse: ${response.code()}")
                    Log.e(ConstValues.TAG, "onResponse: ${response.message()}")
                    Log.e(ConstValues.TAG, "onResponse: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<JobResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "failed", Toast.LENGTH_SHORT).show()
                Log.e(ConstValues.TAG, "onFailure: $t")
                Log.e(ConstValues.TAG, "onFailure: ${t.message}")
                Log.e(ConstValues.TAG, "onFailure: ${t.printStackTrace()}")
                Log.e(ConstValues.TAG, "onFailure: ${t.stackTrace}")
            }

        })
    }

//    private fun createJob() {
//        binding.apply {
//            securedJobViewModel = ViewModelProvider(
//                owner = this@AddJobFragment,
//                factory = securedJobViewModelFactory
//            )[SecuredJobViewModel::class.java]
//
//            launch {
//                securedJobViewModel.createJob(jobRequest = JobRequest(
//                    benefit = benefitEt.editText?.text.toString(),
//                    categoryId = selectedCategoryId,
//                    deadline = convertDateFormat(deadlineTv.text.toString()),
//                    districtId = selectedDistrictId,
//                    gender = selectedGender,
//                    instagramLink = instagramUsernameEt.editText?.text.toString(),
//                    latitude = 41.3409,
//                    longitude = 69.2867,
//                    maxAge = maxAgeEt.editText?.text.toString().toInt(),
//                    minAge = minAgeEt.editText?.text.toString().toInt(),
//                    phoneNumber = phoneNumberEt.editText?.text.toString(),
//                    requirement = requirementEt.editText?.text.toString(),
//                    salary = salaryEt.editText?.text.toString()
//                        .substring(0, salaryEt.editText?.text.toString().length - 5)
//                        .toInt(),
//                    telegramLink = "link of post on telegram channel",
//                    tgUserName = tgUserNameEt.editText?.text.toString(),
//                    title = titleEt.editText?.text.toString(),
//                    workingSchedule = workingScheduleEt.editText?.text.toString(),
//                    workingTime = workingTimeEt.editText?.text.toString()
//                ))
//                    .collect {
//                        when (it) {
//                            is ApiStatus.Error -> {
//                                Toast.makeText(
//                                    requireContext(),
//                                    "some error on creating job",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                Log.e(ConstValues.TAG, "createWorker: ${it.error}")
//                                Log.e(
//                                    ConstValues.TAG,
//                                    "createJob: ${it.error.stackTrace.joinToString()}"
//                                )
//                                Log.e(ConstValues.TAG, "createJob: ${it.error.message}")
//                            }
//
//                            is ApiStatus.Loading -> {
//                                Toast.makeText(requireContext(), "loading", Toast.LENGTH_SHORT)
//                                    .show()
//                            }
//
//                            is ApiStatus.Success -> {
//                                Toast.makeText(
//                                    requireContext(),
//                                    "successfully created worker",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                Log.d(
//                                    ConstValues.TAG,
//                                    "createJob: ${it.response as JobResponse}"
//                                )
//                            }
//                        }
//                    }
//            }
//        }
//    }

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
        launch {
            regionViewModelFactory = RegionViewModelFactory(
                appDatabase = appDatabase,
                regionService = ApiClient.regionService,
                regionId = "",
                districtId = "",
                networkHelper = networkHelper
            )
            regionViewModel = ViewModelProvider(
                owner = this@AddJobFragment,
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
                            Log.e(ConstValues.TAG, "loadRegions: ${it.error}")
                            Log.e(ConstValues.TAG, "loadRegions: ${it.error.message}")
                            Log.e(ConstValues.TAG, "loadRegions: ${it.error.printStackTrace()}")
                            Log.e(ConstValues.TAG, "loadRegions: ${it.error.stackTrace}")
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
                owner = this@AddJobFragment,
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
                            Log.e(ConstValues.TAG, "loadDistricts: ${it.error}")
                            Log.e(ConstValues.TAG, "loadDistricts: ${it.error.message}")
                            Log.e(ConstValues.TAG, "loadDistricts: ${it.error.printStackTrace()}")
                            Log.e(ConstValues.TAG, "loadDistricts: ${it.error.stackTrace}")
                        }

                        is ApiStatus.Loading -> {

                        }

                        is ApiStatus.Success -> {
                            (it.response as List<DistrictEntity>).forEach {
                                Log.d(ConstValues.TAG, "loadDistricts: succeeded $it")
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
                owner = this@AddJobFragment,
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
                            Log.e(ConstValues.TAG, "loadCategories: ${it.error}")
                            Log.e(ConstValues.TAG, "loadCategories: ${it.error.message}")
                            Log.e(ConstValues.TAG, "loadCategories: ${it.error.printStackTrace()}")
                            Log.e(ConstValues.TAG, "loadCategories: ${it.error.stackTrace}")
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