package dev.goblingroup.uzworks.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.adapters.view_pager_adapters.AdminPanelAdapter
import dev.goblingroup.uzworks.databinding.FragmentAdminPanelBinding
import dev.goblingroup.uzworks.utils.AdminTabsEnum

@AndroidEntryPoint
class AdminPanelFragment : Fragment() {

    private var _binding: FragmentAdminPanelBinding? = null
    private val binding get() = _binding!!

    private lateinit var adminPanelAdapter: AdminPanelAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminPanelBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            val tabList = arrayListOf(
                AdminTabsEnum.DISTRICT.tabTitle,
                AdminTabsEnum.JOB.tabTitle,
                AdminTabsEnum.JOB_CATEGORY.tabTitle,
                AdminTabsEnum.REGION.tabTitle,
                AdminTabsEnum.WORKER.tabTitle
            )
            adminPanelAdapter = AdminPanelAdapter(this@AdminPanelFragment, tabList)
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