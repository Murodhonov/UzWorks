package dev.goblingroup.uzworks.fragments.main.jobs

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapters.view_pager_adapters.JobsViewPagerAdapter
import dev.goblingroup.uzworks.databinding.FragmentJobsBinding
import dev.goblingroup.uzworks.databinding.SavedJobsPropertiesBinding
import dev.goblingroup.uzworks.utils.getNavOptions

class JobsFragment : Fragment() {

    private var _binding: FragmentJobsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            val adapter =
                JobsViewPagerAdapter(
                    this@JobsFragment,
                    arrayListOf("Barcha", "Saqlanganlar"),
                    object : JobListFragment.OnJobClickListener {
                        override fun onJobClick(position: Int) {
                            jobClick()
                        }

                    })
            viewPager.adapter = adapter

            TabLayoutMediator(
                tabLayout, viewPager
            ) { tab, position ->

            }.attach()

            allTv.setOnClickListener {
                allTv.setTextColor(resources.getColor(R.color.black_blue))
                savedTv.setTextColor(resources.getColor(R.color.text_color))
                viewPager.setCurrentItem(0, true)
            }

            savedTv.setOnClickListener {
                allTv.setTextColor(resources.getColor(R.color.text_color))
                savedTv.setTextColor(resources.getColor(R.color.black_blue))
                viewPager.setCurrentItem(1, true)
            }

            viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    when (position) {
                        0 -> {
                            allTv.setTextColor(resources.getColor(R.color.black_blue))
                            savedTv.setTextColor(resources.getColor(R.color.text_color))
                        }
                        1 -> {
                            allTv.setTextColor(resources.getColor(R.color.text_color))
                            savedTv.setTextColor(resources.getColor(R.color.black_blue))
                        }
                    }
                }
            })
        }
    }

    private fun jobClick() {
        binding.apply {
            /*if (viewPager.currentItem == 0) {
                findNavController().navigate(R.id.action_jobsFragment_to_jobDetailsFragment)
            } else {
                val bottomSheetDialog = BottomSheetDialog(requireContext())
                val jobPropertiesBinding = SavedJobsPropertiesBinding.inflate(layoutInflater)
                jobPropertiesBinding.apply {
                    bottomSheetDialog.setContentView(root)
                    contactChatBtn.setOnClickListener {
                        findNavController().navigate(R.id.action_jobsFragment_to_chatFragment)
                    }
                    shareBtn.setOnClickListener {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "Public link to job"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "public link is in progress")
                    }
                    seeMoreBtn.setOnClickListener {
                        findNavController().navigate(R.id.action_jobsFragment_to_jobDetailsFragment)
                    }
                    deleteBtn.setOnClickListener {
                        Toast.makeText(
                            requireContext(),
                            "delete job from saved is in progress",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }*/
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
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "There should be a public link to job announcement"
                        )
                        type = "text/plain"
                    }
                    if (shareIntent.resolveActivity(requireContext().packageManager) != null) {
                        startActivity(Intent.createChooser(shareIntent, "Share using"))
                    }
                    bottomSheetDialog.dismiss()
                }
                seeMoreBtn.setOnClickListener {
                    bottomSheetDialog.dismiss()
                    findNavController().navigate(
                        resId = R.id.jobDetailsFragment,
                        args = null,
                        navOptions = getNavOptions()
                    )
                }
                deleteBtn.setOnClickListener {
                    Toast.makeText(
                        requireContext(),
                        "delete job from saved is in progress",
                        Toast.LENGTH_SHORT
                    ).show()
                    bottomSheetDialog.dismiss()
                }
            }
            bottomSheetDialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}