package dev.goblingroup.uzworks.fragments.announcement

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapter.view_pager_adapters.AnnouncementPagerAdapter
import dev.goblingroup.uzworks.databinding.FragmentAnnouncementsBinding
import dev.goblingroup.uzworks.utils.AnnouncementEnum

@AndroidEntryPoint
class AnnouncementsFragment : Fragment() {

    private val TAG = "AnnouncementsFragment"

    private var _binding: FragmentAnnouncementsBinding? = null
    private val binding get() = _binding!!

    private var indicatorWidth: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: checking UzWorks")
        _binding = FragmentAnnouncementsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            Log.d(TAG, "onViewCreated: checking UzWorks")
            val adapter = AnnouncementPagerAdapter(
                fragment = this@AnnouncementsFragment,
                object : SavedAnnouncementsFragment.FindAnnouncementClickListener {
                    override fun onFindAnnouncementClick() {
                        viewPager.setCurrentItem(0, true)
                    }
                })

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

    private fun openAnnouncementDetails(announcementId: String, announcementType: String) {
        val direction =
            if (announcementType == AnnouncementEnum.JOB.announcementType)
                R.id.action_startFragment_to_jobDetailsFragment
            else
                R.id.action_startFragment_to_workerDetailsFragment
        val bundle = Bundle()
        bundle.putString("announcement_id", announcementId)
        findNavController().navigate(
            resId = direction,
            args = bundle
        )
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: checking UzWorks")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach: checking UzWorks")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: checking UzWorks")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: checking UzWorks")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: checking UzWorks")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: checking UzWorks")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: checking UzWorks")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: checking UzWorks")
        _binding = null
    }

    companion object {

        fun newInstance() =
            AnnouncementsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}