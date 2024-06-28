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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapter.CategoryAdapter
import dev.goblingroup.uzworks.adapter.DistrictAdapter
import dev.goblingroup.uzworks.adapter.RegionAdapter
import dev.goblingroup.uzworks.databinding.BirthdayGenderExplanationBinding
import dev.goblingroup.uzworks.databinding.BottomSelectionBinding
import dev.goblingroup.uzworks.databinding.FragmentEditWorkerBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogBinding
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

@AndroidEntryPoint
class EditWorkerFragment : Fragment() {

    private var _binding: FragmentEditWorkerBinding? = null
    private val binding get() = _binding!!

    private val editWorkerViewModel: EditWorkerViewModel by viewModels()

    private val addressViewModel: AddressViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingDialogBinding: LoadingDialogBinding

    private lateinit var birthdayGenderExplanationDialog: AlertDialog
    private lateinit var birthdayGenderExplanationBinding: BirthdayGenderExplanationBinding

    private lateinit var regionDialog: BottomSheetDialog
    private lateinit var regionDialogItemBinding: BottomSelectionBinding

    private lateinit var districtDialog: BottomSheetDialog
    private lateinit var districtDialogItemBinding: BottomSelectionBinding

    private lateinit var categoryDialog: BottomSheetDialog
    private lateinit var categoryDialogItemBinding: BottomSelectionBinding

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

            toolbar.setNavigationOnClickListener {
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

            region.setOnClickListener {
                showRegion()
            }

            district.setOnClickListener {
                showDistrict()
            }

            category.setOnClickListener {
                showCategory()
            }
        }
    }

    private fun showRegion() {
        try {
            regionDialog.show()
        } catch (e: Exception) {
            regionDialog = BottomSheetDialog(requireContext())
            regionDialogItemBinding = BottomSelectionBinding.inflate(layoutInflater)
            regionDialog.setContentView(regionDialogItemBinding.root)
            regionDialog.show()
        }
        regionDialogItemBinding.apply {

            addressViewModel.regionLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "failed to load regions",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    is ApiStatus.Loading -> {
                        progress.visibility = View.VISIBLE
                    }

                    is ApiStatus.Success -> {
                        progress.visibility = View.GONE
                        val regionAdapter = RegionAdapter(
                            it.response!!
                        ) { regionId, regionName ->
                            if (regionName != binding.region.text.toString()) {
                                binding.region.text = regionName
                                editWorkerViewModel.regionId = regionId
                                editWorkerViewModel.districtId = ""
                                binding.district.text = resources.getString(R.string.select_region)
                            }
                            regionDialog.dismiss()
                        }
                        selectionRv.adapter = regionAdapter
                    }
                }
            }
        }
    }

    private fun showDistrict() {
        try {
            districtDialog.show()
        } catch (e: Exception) {
            districtDialog = BottomSheetDialog(requireContext())
            districtDialogItemBinding = BottomSelectionBinding.inflate(layoutInflater)
            districtDialog.setContentView(districtDialogItemBinding.root)
            districtDialog.show()
        }
        districtDialogItemBinding.apply {

            addressViewModel.districtsByRegionId(editWorkerViewModel.regionId.toString())
                .observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "failed to load districts",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                        is ApiStatus.Loading -> {
                            progress.visibility = View.VISIBLE
                            selectionRv.visibility = View.GONE
                        }

                        is ApiStatus.Success -> {
                            progress.visibility = View.GONE
                            selectionRv.visibility = View.VISIBLE
                            val districtAdapter = DistrictAdapter(
                                it.response!!
                            ) { districtId, districtName ->
                                binding.district.text = districtName
                                binding.district.setBackgroundResource(R.drawable.enabled_tv_background)
                                editWorkerViewModel.districtId = districtId
                                districtDialog.dismiss()
                            }
                            selectionRv.adapter = districtAdapter
                        }
                    }
                }
        }
    }

    private fun showCategory() {
        try {
            categoryDialog.show()
        } catch (e: Exception) {
            categoryDialog = BottomSheetDialog(requireContext())
            categoryDialogItemBinding = BottomSelectionBinding.inflate(layoutInflater)
            categoryDialog.setContentView(categoryDialogItemBinding.root)
            categoryDialog.show()
        }
        categoryDialogItemBinding.apply {
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
                        progress.visibility = View.VISIBLE
                    }

                    is ApiStatus.Success -> {
                        progress.visibility = View.GONE
                        Log.d("TAG", "showCategory: ${it.response!!.size}")
                        Log.d("TAG", "showCategory: ${it.response.map { it.title }}")
                        val categoryAdapter =
                            CategoryAdapter(it.response) { categoryId, categoryName ->
                                binding.category.text = categoryName
                                binding.category.setBackgroundResource(R.drawable.enabled_tv_background)
                                editWorkerViewModel.categoryId = categoryId
                                categoryDialog.dismiss()
                            }
                        selectionRv.adapter = categoryAdapter
                    }
                }
            }
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
            loadingDialogBinding = LoadingDialogBinding.inflate(layoutInflater)
            loadingDialog.setView(loadingDialogBinding.root)
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
            phoneNumber.text = editWorkerViewModel.phoneNumber.convertPhoneNumber()
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
            region.text = response.district.region.name
            district.text = response.district.name
            category.text = response.jobCategory.title

            editWorkerViewModel.controlInput(
                requireActivity(),
                titleEt,
                salaryEt,
                workingTimeEt,
                workingScheduleEt,
                tgUserNameEt,
                deadlineEt
            )

            phoneNumber.setOnClickListener {
                val clipboardManager =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData =
                    ClipData.newPlainText("label", phoneNumber.text.toString())
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(
                    requireContext(),
                    requireContext().resources.getString(R.string.phone_number_copied),
                    Toast.LENGTH_SHORT
                ).show()
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
                district = district,
                category = categoryTv
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