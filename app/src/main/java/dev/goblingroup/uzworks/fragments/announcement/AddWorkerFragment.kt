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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapter.CategoryAdapter
import dev.goblingroup.uzworks.adapter.DistrictAdapter
import dev.goblingroup.uzworks.adapter.RegionAdapter
import dev.goblingroup.uzworks.databinding.BirthdayGenderExplanationBinding
import dev.goblingroup.uzworks.databinding.BottomSelectionBinding
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
        _binding = FragmentAddWorkerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            addWorkerViewModel.controlInput(
                fragmentActivity = requireActivity(),
                deadlineEt = deadlineEt,
                titleEt = titleEt,
                salaryEt = salaryEt,
                workingTimeEt = workingTimeEt,
                workingScheduleEt = workingScheduleEt,
                tgUserNameEt = tgUserNameEt,
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

            saveBtn.setOnClickListener {
                val isValid = addWorkerViewModel.isFormValid(
                    resources,
                    deadlineEt,
                    titleEt,
                    salaryEt,
                    workingTimeEt,
                    workingScheduleEt,
                    tgUserNameEt,
                    district,
                    category
                )
                Log.d(TAG, "onViewCreated: adding worker $isValid")
                if (isValid) {
                    createWorker()
                }
            }

            cancelBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            phoneNumber.text = addWorkerViewModel.phoneNumber.convertPhoneNumber()
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
                                addWorkerViewModel.regionId = regionId
                                addWorkerViewModel.districtId = ""
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

            addressViewModel.districtsByRegionId(addWorkerViewModel.regionId)
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
                                addWorkerViewModel.districtId = districtId
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
                                addWorkerViewModel.jobCategoryId = categoryId
                                categoryDialog.dismiss()
                            }
                        selectionRv.adapter = categoryAdapter
                    }
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

                        else -> {}
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}