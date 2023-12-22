package dev.goblingroup.uzworks.adapters.view_pager_adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.goblingroup.uzworks.fragments.main.jobs.job_list.JobListFragment

class JobsViewPagerAdapter(
    fragment: Fragment,
    private val tabList: List<String>,
    private val jobClickListener: JobListFragment.OnJobClickListener
) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = tabList.size

    override fun createFragment(position: Int): Fragment {
        return JobListFragment.newInstance(tabList[position]).apply {
            setOnJobClickListener(jobClickListener)
        }
    }
}