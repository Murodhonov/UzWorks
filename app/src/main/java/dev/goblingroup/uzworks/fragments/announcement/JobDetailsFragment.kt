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
import dev.goblingroup.uzworks.databinding.FragmentJobDetailsBinding
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import dev.goblingroup.uzworks.vm.JobDetailsViewModel

@AndroidEntryPoint
class JobDetailsFragment : Fragment() {

    private var _binding: FragmentJobDetailsBinding? = null

    private val binding get() = _binding!!

    private val jobDetailsViewModel: JobDetailsViewModel by viewModels()
    private val addressViewModel: AddressViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            loadJob()
        }
    }

    private fun loadJob() {
        jobDetailsViewModel.fetchJob(arguments?.getString("announcement_id").toString())
            .observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(requireContext(), "failed to load job", Toast.LENGTH_SHORT)
                            .show()
                    }

                    is ApiStatus.Loading -> {
                        Toast.makeText(requireContext(), "loading job", Toast.LENGTH_SHORT).show()
                    }

                    is ApiStatus.Success -> {
                        Toast.makeText(
                            requireContext(),
                            "job successfully loaded",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        setJobDetails(it.response)
                    }
                }
            }
    }

    private fun setJobDetails(response: JobResponse?) {
        binding.apply {
            announcementTitleTv.text = response?.title
            jobCategoryTv.text =
                jobCategoryViewModel.findJobCategory(response?.categoryId.toString()).toString()
            genderTv.text =
                if (response?.gender == GenderEnum.MALE.label) resources.getString(R.string.male) else resources.getString(
                    R.string.female
                )
            addressTv.text =
                "${addressViewModel.findRegionByDistrictId(response?.districtId.toString())}, ${
                    addressViewModel.findDistrict(response?.districtId.toString())
                }"
            salaryTv.text = response?.salary.toString()
            titleTv.text = response?.benefit
            workingTimeTv.text = response?.workingTime
            workingScheduleTv.text = response?.workingSchedule
            benefitTv.text = response?.benefit
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}