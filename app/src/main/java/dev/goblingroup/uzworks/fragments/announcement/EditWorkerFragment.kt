package dev.goblingroup.uzworks.fragments.announcement

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
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.databinding.BirthdayGenderExplanationBinding
import dev.goblingroup.uzworks.databinding.FragmentEditWorkerBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogItemBinding
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.utils.ConstValues.DEFAULT_BIRTHDAY
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.convertPhoneNumber
import dev.goblingroup.uzworks.utils.formatSalary
import dev.goblingroup.uzworks.utils.formatTgUsername
import dev.goblingroup.uzworks.utils.isoToDmy
import dev.goblingroup.uzworks.utils.selectFemale
import dev.goblingroup.uzworks.utils.selectMale
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.EditWorkerViewModel
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditWorkerFragment : Fragment() {

    private var _binding: FragmentEditWorkerBinding? = null
    private val binding get() = _binding!!

    private val editWorkerViewModel: EditWorkerViewModel by viewModels()

    private val addressViewModel: AddressViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingDialogItemBinding: LoadingDialogItemBinding

    private lateinit var birthdayGenderExplanationDialog: AlertDialog
    private lateinit var birthdayGenderExplanationBinding: BirthdayGenderExplanationBinding

    private lateinit var regionAdapter: ArrayAdapter<String>
    private lateinit var districtAdapter: ArrayAdapter<String>
    private lateinit var categoryAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditWorkerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            editWorkerViewModel.workerId = arguments?.getString("announcement_id").toString()

            back.setOnClickListener {
                findNavController().popBackStack()
            }

            editWorkerViewModel.fetchWorker().observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        failed()
                    }

                    is ApiStatus.Loading -> {
                        loading()
                    }

                    is ApiStatus.Success -> {
                        succeeded(it.response!!)
                    }
                }
            }

            topTv.isSelected = true
        }
    }

    private fun failed() {
        Toast.makeText(requireContext(), resources.getString(R.string.fetch_worker_failed), Toast.LENGTH_SHORT).show()
        hideLoading()
        findNavController().popBackStack()
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

    private fun succeeded(response: WorkerResponse) {
        hideLoading()
        binding.apply {
            titleEt.editText?.setText(response.title)
            salaryEt.editText?.setText(response.salary.toString().formatSalary())
            workingTimeEt.editText?.setText(response.workingTime)
            workingScheduleEt.editText?.setText(response.workingSchedule)
            tgUserNameEt.editText?.setText(response.tgUserName.formatTgUsername())
            phoneNumberEt.editText?.setText(editWorkerViewModel.phoneNumber.convertPhoneNumber())
            deadlineEt.editText?.setText(response.deadline.isoToDmy())
            if (editWorkerViewModel.birthdate != DEFAULT_BIRTHDAY)
                birthdayEt.editText?.setText(response.birthDate.isoToDmy())
            genderLayout.apply {
                when (response.gender) {
                    GenderEnum.MALE.label -> {
                        selectMale(resources)
                    }

                    GenderEnum.FEMALE.label -> {
                        selectFemale(resources)
                    }
                }
            }
            regionChoice.setText(response.regionName)
            districtChoice.setText(response.districtName)
            jobCategoryChoice.setText(response.categoryName)

            regionChoice.setOnClickListener {
                addressViewModel.regionLiveData.observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "failed to load regions",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is ApiStatus.Loading -> {
                            regionProgress.visibility = View.VISIBLE
                            regionLayout.endIconMode = TextInputLayout.END_ICON_NONE
                        }
                        is ApiStatus.Success -> {
                            regionProgress.visibility = View.GONE
                            regionLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU
                            setRegions(it.response!!)
                        }
                    }
                }
            }

            jobCategoryChoice.setOnClickListener {
                jobCategoryViewModel.jobCategoriesLiveData.observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "failed to load categories",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is ApiStatus.Loading -> {
                            categoryProgress.visibility = View.VISIBLE
                            jobCategoryLayout.endIconMode = TextInputLayout.END_ICON_NONE
                        }
                        is ApiStatus.Success -> {
                            categoryProgress.visibility = View.GONE
                            jobCategoryLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU
                            setJobCategories(it.response!!)
                        }
                    }
                }
            }

            districtChoice.setOnItemClickListener { parent, view, position, id ->
                if (districtLayout.isErrorEnabled) {
                    districtLayout.isErrorEnabled = false
                }
                editWorkerViewModel.districtId = addressViewModel.districtsByRegionName(regionChoice.text.toString())[position].id
            }

            jobCategoryChoice.setOnItemClickListener { parent, view, position, id ->
                if (jobCategoryLayout.isErrorEnabled) {
                    jobCategoryLayout.isErrorEnabled = false
                }
                editWorkerViewModel.categoryId =
                    jobCategoryViewModel.listJobCategories()[position].id
            }

            editWorkerViewModel.controlInput(
                requireActivity(),
                topTv,
                titleEt,
                salaryEt,
                workingTimeEt,
                workingScheduleEt,
                tgUserNameEt,
                deadlineEt
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


            cancelBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            saveBtn.setOnClickListener {
                edit()
            }
        }
    }

    private fun setJobCategories(jobCategoryList: List<JobCategoryEntity>) {
        binding.apply {
            categoryAdapter =
                ArrayAdapter(
                    requireContext(),
                    androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                    jobCategoryList.map { it.title }
                )
            jobCategoryChoice.threshold = 1
            jobCategoryChoice.setAdapter(categoryAdapter)
            jobCategoryChoice.setOnItemClickListener { parent, view, position, id ->
                if (jobCategoryLayout.isErrorEnabled) {
                    jobCategoryLayout.isErrorEnabled = false
                }
                editWorkerViewModel.categoryId = jobCategoryList[position].id
            }
        }
    }

    private fun setRegions(response: List<RegionEntity>) {
        binding.apply {
            regionAdapter = ArrayAdapter(
                requireContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                response.map { it.name }
            )
            regionChoice.threshold = 1
            regionChoice.setAdapter(regionAdapter)

            regionChoice.setOnItemClickListener { parent, view, position, id ->
                districtChoice.text.clear()
                districtChoice.hint = getString(R.string.select_district)
                setDistricts(response[position].id)
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

    private fun edit() {
        binding.apply {
            editWorkerViewModel.editWorker(
                context = requireContext(),
                titleEt = titleEt,
                salaryEt = salaryEt,
                workingTimeEt = workingTimeEt,
                workingScheduleEt = workingScheduleEt,
                tgUserNameEt = tgUserNameEt,
                deadlineEt = deadlineEt,
                birthdayEt = birthdayEt,
                districtLayout = districtLayout,
                categoryLayout = jobCategoryLayout
            ).observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(requireContext(), resources.getString(R.string.edit_worker_announcement_failed), Toast.LENGTH_SHORT).show()
                        hideLoading()
                    }

                    is ApiStatus.Loading -> {
                        loading()
                    }

                    is ApiStatus.Success -> {
                        hideLoading()
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.worker_announcement_edited),
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun setDistricts(regionId: String) {
        binding.apply {
            lifecycleScope.launch {
                addressViewModel.districtsByRegionId(regionId).observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "failed to load districts",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is ApiStatus.Loading -> {
                            districtProgress.visibility = View.VISIBLE
                            districtLayout.endIconMode = TextInputLayout.END_ICON_NONE
                        }
                        is ApiStatus.Success -> {
                            districtProgress.visibility = View.GONE
                            districtLayout.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU
                            districtAdapter = ArrayAdapter(
                                requireContext(),
                                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                                it.response!!.map { it.name }
                            )
                            districtChoice.setAdapter(districtAdapter)
                        }
                    }
                }
            }
        }
    }

    private fun hideLoading() {
        try {
            loadingDialog.dismiss()
        } catch (e: Exception) {
            Log.e(TAG, "hideLoading: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}