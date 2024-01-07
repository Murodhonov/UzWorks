package dev.goblingroup.uzworks.fragments.main.jobs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapters.view_pager_adapters.JobsViewPagerAdapter
import dev.goblingroup.uzworks.databinding.FragmentJobsBinding
import dev.goblingroup.uzworks.databinding.SavedJobsPropertiesBinding
import dev.goblingroup.uzworks.fragments.main.jobs.job_list.AllJobsFragment
import dev.goblingroup.uzworks.fragments.main.jobs.job_list.SavedJobsFragment
import dev.goblingroup.uzworks.utils.getNavOptions

class JobsFragment : Fragment() {

    private var _binding: FragmentJobsBinding? = null
    private val binding get() = _binding!!

    private var indicatorWidth: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            val adapter = JobsViewPagerAdapter(
                fragment = this@JobsFragment,
                object : AllJobsFragment.OnAllJobClickListener {
                    override fun onAllJobClick(jobId: String) {
                        openJobDetails(jobId)
                    }

                }, object : SavedJobsFragment.OnSavedJobClickListener {
                    override fun onSavedJobClick(jobId: String) {
                        savedJobClicked(jobId)
                    }

                }, object : SavedJobsFragment.OnFindJobClickListener {
                    override fun onFindJobClick() {
                        viewPager.setCurrentItem(0, true)
                    }

                }
            )

            viewPager.adapter = adapter

            TabLayoutMediator(
                tabLayout, viewPager
            ) { tab, position ->
                tab.text = arrayListOf("Barcha", "Saqlanganlar")[position]
            }.attach()

            tabLayout.post {
                indicatorWidth = tabLayout.width / tabLayout.tabCount
                val indicatorParams = indicator.layoutParams as FrameLayout.LayoutParams
                indicatorParams.width = indicatorWidth
                indicator.layoutParams = indicatorParams
            }

            viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    val params = indicator.layoutParams as FrameLayout.LayoutParams
                    val translationOffSet = (positionOffset + position) * indicatorWidth
                    params.leftMargin = translationOffSet.toInt()
                    indicator.layoutParams = params
                }
            })

        }
    }

    private fun savedJobClicked(jobId: String) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val jobPropertiesBinding = SavedJobsPropertiesBinding.inflate(layoutInflater)
        jobPropertiesBinding.apply {
            bottomSheetDialog.setContentView(root)
            contactChatBtn.setOnClickListener {
                bottomSheetDialog.dismiss()
                findNavController().navigate(
                    resId = R.id.chatFragment,
                    args = null,
                    navOptions = getNavOptions()
                )
            }
            shareBtn.setOnClickListener {
                bottomSheetDialog.dismiss()
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, "public link is in progress")
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            }
            seeMoreBtn.setOnClickListener {
                bottomSheetDialog.dismiss()
                openJobDetails(jobId)
            }
            deleteBtn.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "delete job from saved is in progress",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        bottomSheetDialog.show()
    }

    private fun openJobDetails(jobId: String) {
        val bundle = Bundle()
        bundle.putString("clicked_job_id", jobId)
        findNavController().navigate(
            resId = R.id.jobDetailsFragment,
            args = bundle,
            navOptions = getNavOptions()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}