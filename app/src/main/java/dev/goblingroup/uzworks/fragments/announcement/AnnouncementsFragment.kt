package dev.goblingroup.uzworks.fragments.announcement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapter.view_pager_adapters.AnnouncementPagerAdapter
import dev.goblingroup.uzworks.databinding.FragmentAnnouncementsBinding
import dev.goblingroup.uzworks.databinding.SavedAnnouncementBottomBinding
import dev.goblingroup.uzworks.utils.AnnouncementEnum
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.getNavOptions

@AndroidEntryPoint
class AnnouncementsFragment : Fragment() {

    private var _binding: FragmentAnnouncementsBinding? = null
    private val binding get() = _binding!!

    private var indicatorWidth: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnnouncementsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            val adapter = AnnouncementPagerAdapter(
                fragment = this@AnnouncementsFragment,
                object : AllAnnouncementsFragment.AllAnnouncementClickListener {
                    override fun onAllAnnouncementClick(
                        announcementId: String,
                        announcementType: String
                    ) {
                        openAnnouncementDetails(announcementId, announcementType)
                    }

                }, object : SavedAnnouncementsFragment.SavedAnnouncementClickListener {
                    override fun onSavedAnnouncementClick(
                        announcementId: String,
                        announcementType: String
                    ) {
                        savedAnnouncementClicked(announcementId, announcementType)
                    }
                }, object : SavedAnnouncementsFragment.FindAnnouncementClickListener {
                    override fun onFindAnnouncementClick() {
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

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
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

    private fun savedAnnouncementClicked(announcementId: String, announcementType: String) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val savedAnnouncementBottomBinding = SavedAnnouncementBottomBinding.inflate(layoutInflater)
        savedAnnouncementBottomBinding.apply {
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
                openAnnouncementDetails(announcementId, announcementType)
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

    private fun openAnnouncementDetails(announcementId: String, announcementType: String) {
        val direction =
            if (announcementType == AnnouncementEnum.JOB.announcementType) R.id.jobDetailsFragment else R.id.workerDetailsFragment
        val bundle = Bundle()
        bundle.putString("announcement_id", announcementId)
        Log.d(TAG, "openAnnouncementDetails: bundle $bundle announcementType -> $announcementType")
        Log.d(
            TAG,
            "openAnnouncementDetails: navigating to ${if (announcementType == AnnouncementEnum.JOB.announcementType) "job details" else "worker details"}"
        )
        findNavController().navigate(
            resId = direction,
            args = bundle,
            navOptions = getNavOptions()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}