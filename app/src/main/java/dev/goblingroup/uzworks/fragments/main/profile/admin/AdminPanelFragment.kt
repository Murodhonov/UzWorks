package dev.goblingroup.uzworks.fragments.main.profile.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import dev.goblingroup.uzworks.adapters.view_pager_adapters.AdminPanelAdapter
import dev.goblingroup.uzworks.databinding.FragmentAdminPanelBinding
import dev.goblingroup.uzworks.utils.ConstValues.DISTRICT
import dev.goblingroup.uzworks.utils.ConstValues.JOB
import dev.goblingroup.uzworks.utils.ConstValues.JOB_CATEGORY
import dev.goblingroup.uzworks.utils.ConstValues.REGION
import dev.goblingroup.uzworks.utils.ConstValues.WORKER

class AdminPanelFragment : Fragment() {

    private var _binding: FragmentAdminPanelBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminPanelBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            val tabList = arrayListOf(DISTRICT, JOB, JOB_CATEGORY, REGION, WORKER)
            val adminPanelAdapter = AdminPanelAdapter(this@AdminPanelFragment, tabList)
            viewPager.adapter = adminPanelAdapter

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabList[position]
            }.attach()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}