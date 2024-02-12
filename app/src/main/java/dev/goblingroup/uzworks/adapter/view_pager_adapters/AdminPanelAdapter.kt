package dev.goblingroup.uzworks.adapter.view_pager_adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.goblingroup.uzworks.fragments.admin.tabs.DistrictControlFragment
import dev.goblingroup.uzworks.fragments.admin.tabs.JobCategoryControlFragment
import dev.goblingroup.uzworks.fragments.admin.tabs.JobControlFragment
import dev.goblingroup.uzworks.fragments.admin.tabs.RegionControlFragment
import dev.goblingroup.uzworks.fragments.admin.tabs.WorkerControlFragment
import dev.goblingroup.uzworks.utils.AdminTabsEnum

class AdminPanelAdapter(
    fragment: Fragment,
    private val tabList: List<String>
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = tabList.size

    override fun createFragment(position: Int): Fragment {
        when (tabList[position]) {
            AdminTabsEnum.DISTRICT.tabTitle -> {
                return DistrictControlFragment.newInstance()
            }

            AdminTabsEnum.JOB.tabTitle -> {
                return JobControlFragment.newInstance()
            }

            AdminTabsEnum.JOB_CATEGORY.tabTitle -> {
                return JobCategoryControlFragment.newInstance()
            }

            AdminTabsEnum.REGION.tabTitle -> {
                return RegionControlFragment.newInstance()
            }

            AdminTabsEnum.WORKER.tabTitle -> {
                return WorkerControlFragment.newInstance()
            }

            else -> {
                return WorkerControlFragment.newInstance()
            }
        }
    }
}