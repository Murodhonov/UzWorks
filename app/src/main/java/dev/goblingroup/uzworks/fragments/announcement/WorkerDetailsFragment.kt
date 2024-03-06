package dev.goblingroup.uzworks.fragments.announcement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentWorkerDetailsBinding
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.LanguageEnum
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import dev.goblingroup.uzworks.vm.WorkerDetailsViewModel

@AndroidEntryPoint
class WorkerDetailsFragment : Fragment() {

    private var _binding: FragmentWorkerDetailsBinding? = null
    private val binding get() = _binding!!

    private val workerDetailsViewModel: WorkerDetailsViewModel by viewModels()
    private val addressViewModel: AddressViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkerDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            loadWorker()
        }
    }

    private fun loadWorker() {
        workerDetailsViewModel.fetchWorker(arguments?.getString("announcement_id").toString())
            .observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "failed to load worker",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is ApiStatus.Loading -> {
                        Toast.makeText(
                            requireContext(),
                            "loading worker details",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is ApiStatus.Success -> {
                        Toast.makeText(requireContext(), "succeeded", Toast.LENGTH_SHORT).show()
                        setWorkerDetails(it.response)
                    }
                }
            }
    }

    private fun setWorkerDetails(workerResponse: WorkerResponse?) {
        binding.apply {
            titleTv.text = workerResponse?.title
            jobCategoryTv.text =
                jobCategoryViewModel.findJobCategory(workerResponse?.categoryId.toString()).title
            genderTv.text =
                if (workerResponse?.gender == GenderEnum.MALE.label) resources.getString(R.string.male) else resources.getString(
                    R.string.female
                )
            fullNameTv.text = workerResponse?.fullName
            birthdateTv.text = workerResponse?.birthDate
            addressTv.text =
                "${addressViewModel.findRegion(addressViewModel.findDistrict(workerResponse?.districtId.toString()).regionId).name}, ${
                    addressViewModel.findDistrict(workerResponse?.districtId.toString()).name
                }"
            salaryTv.text = workerResponse?.salary.toString()
            workingTimeTv.text = workerResponse?.workingTime
            workingScheduleTv.text = workerResponse?.workingSchedule
            tgIv.setOnClickListener {
                openLink(workerResponse?.telegramLink)
            }

            tgLinkTv.setOnClickListener {
                openLink(workerResponse?.telegramLink)
            }

            igIv.setOnClickListener {
                openLink(workerResponse?.instagramLink)
            }

            igLinkTv.setOnClickListener {
                openLink(workerResponse?.instagramLink)
            }

            contactTgBtn.setOnClickListener {
                openLink(workerResponse?.tgUserName)
            }
        }
    }

    private fun openLink(link: String?) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}