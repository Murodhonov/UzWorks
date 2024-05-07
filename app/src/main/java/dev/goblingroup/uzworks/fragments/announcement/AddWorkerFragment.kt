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
import dev.goblingroup.uzworks.databinding.BirthdayGenderExplanationBinding
import dev.goblingroup.uzworks.databinding.FragmentAddWorkerBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogItemBinding
import dev.goblingroup.uzworks.models.request.WorkerCreateRequest
import dev.goblingroup.uzworks.utils.ConstValues.DEFAULT_BIRTHDAY
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.convertPhoneNumber
import dev.goblingroup.uzworks.utils.dmyToIso
import dev.goblingroup.uzworks.utils.isoToDmy
import dev.goblingroup.uzworks.utils.selectFemale
import dev.goblingroup.uzworks.utils.selectMale
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

    private lateinit var birthdayGenderExplanationDialog: AlertDialog
    private lateinit var birthdayGenderExplanationBinding: BirthdayGenderExplanationBinding

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

            setRegions(addressViewModel.listRegions())
            setJobCategories(jobCategoryViewModel.listJobCategories())

            addWorkerViewModel.controlInput(
                fragmentActivity = requireActivity(),
                deadlineEt = deadlineEt,
                titleEt = titleEt,
                salaryEt = salaryEt,
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

            genderLayout.root.setOnClickListener {
                birthdayGenderExplanation(resources.getString(R.string.gender_restriction_explanation))
            }

            birthdayEt.editText?.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    birthdayGenderExplanation(resources.getString(R.string.birthday_restriction_explanation))
                }
            }

            saveBtn.setOnClickListener {
                val isValid = addWorkerViewModel.isFormValid(
                    resources,
                    deadlineEt,
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
            if (addWorkerViewModel.birthdate != DEFAULT_BIRTHDAY) {
                birthdayEt.editText?.setText(addWorkerViewModel.birthdate?.isoToDmy())
            }

            genderLayout.apply {
                when (addWorkerViewModel.gender) {
                    GenderEnum.MALE.code -> {
                        selectMale(resources)
                    }

                    GenderEnum.FEMALE.code -> {
                        selectFemale(resources)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun birthdayGenderExplanation(explanationMessage: String) {
        try {
            birthdayGenderExplanationDialog.show()
        } catch (e: Exception) {
            birthdayGenderExplanationDialog = AlertDialog.Builder(requireContext()).create()
            birthdayGenderExplanationBinding = BirthdayGenderExplanationBinding.inflate(layoutInflater)
            birthdayGenderExplanationDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            birthdayGenderExplanationDialog.setView(birthdayGenderExplanationBinding.root)
            birthdayGenderExplanationDialog.show()
        }
        birthdayGenderExplanationBinding.apply {
            explanationTv.text = explanationMessage

            close.setOnClickListener {
                birthdayGenderExplanationDialog.dismiss()
            }
        }
    }

    private fun createWorker() {
        binding.apply {
            lifecycleScope.launch {
                addWorkerViewModel.addWorker(
                    workerCreateRequest = WorkerCreateRequest(
                        birthDate = addWorkerViewModel.birthdate.toString(),
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
        Toast.makeText(requireContext(), resources.getString(R.string.worker_announcement_created), Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_addWorkerFragment_to_myAnnouncementsFragment)
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