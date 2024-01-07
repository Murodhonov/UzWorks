package dev.goblingroup.uzworks.adapters.view_pager_adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.goblingroup.uzworks.fragments.main.admin.district.DistrictControlFragment
import dev.goblingroup.uzworks.fragments.main.admin.job.JobControlFragment
import dev.goblingroup.uzworks.fragments.main.admin.job_category.JobCategoryControlFragment
import dev.goblingroup.uzworks.fragments.main.admin.region.RegionControlFragment
import dev.goblingroup.uzworks.fragments.main.admin.worker.WorkerControlFragment
import dev.goblingroup.uzworks.utils.ConstValues.DISTRICT
import dev.goblingroup.uzworks.utils.ConstValues.JOB
import dev.goblingroup.uzworks.utils.ConstValues.JOB_CATEGORY
import dev.goblingroup.uzworks.utils.ConstValues.REGION

class AdminPanelAdapter(
    fragment: Fragment,
    private val tabList: List<String>
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = tabList.size

    override fun createFragment(position: Int): Fragment {
        when (tabList[position]) {
            DISTRICT -> {
                return DistrictControlFragment.newInstance()
            }

            JOB -> {
                return JobControlFragment.newInstance()
            }

            JOB_CATEGORY -> {
                return JobCategoryControlFragment.newInstance()
            }

            REGION -> {
                return RegionControlFragment.newInstance()
            }

            else -> {
                return WorkerControlFragment.newInstance()
            }
        }
    }
}