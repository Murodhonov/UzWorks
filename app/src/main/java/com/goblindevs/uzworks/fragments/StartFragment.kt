package com.goblindevs.uzworks.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import dagger.hilt.android.AndroidEntryPoint
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.adapter.view_pager_adapters.StartPagerAdapter
import com.goblindevs.uzworks.databinding.FragmentStartBinding
import com.goblindevs.uzworks.utils.GenderEnum
import com.goblindevs.uzworks.vm.ApiStatus
import com.goblindevs.uzworks.vm.StartViewModel

@AndroidEntryPoint
class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    private val startViewModel: StartViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            startViewModel.startPagerAdapter = StartPagerAdapter(this@StartFragment)
            viewPager.adapter = startViewModel.startPagerAdapter
            viewPager.isUserInputEnabled = false

            bottomBar.setOnItemSelectedListener {
                viewPager.setCurrentItem(it, true)
            }

            viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (position == 3) {
                        root.transitionToEnd()
                    } else {
                        root.transitionToStart()
                    }
                    if (bottomBar.itemActiveIndex != position) bottomBar.itemActiveIndex = position
                }
            })

            startViewModel.viewPagerPositionLiveData.observe(viewLifecycleOwner) {
                bottomBar.itemActiveIndex = it
            }

            profile.setOnClickListener {
                viewPager.setCurrentItem(3, true)
            }

            startViewModel.userLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {

                    }
                    is ApiStatus.Loading -> {

                    }
                    is ApiStatus.Success -> {
                        when (it.response?.gender) {
                            GenderEnum.MALE.code -> profile.setImageResource(R.drawable.ic_male)
                            GenderEnum.FEMALE.code -> profile.setImageResource(R.drawable.ic_female)
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        startViewModel.setViewPagerPosition(binding.viewPager.currentItem)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}