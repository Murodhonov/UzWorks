package dev.goblingroup.uzworks.adapters.view_pager_adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.goblingroup.uzworks.fragments.main.jobs.job_list.AllJobsFragment
import dev.goblingroup.uzworks.fragments.main.jobs.job_list.SavedJobsFragment

class JobsViewPagerAdapter(
    fragment: Fragment,
    private val tabList: List<String>,
    private val allJobsClickListener: AllJobsFragment.OnAllJobClickListener,
    private val savedJobsClickListener: SavedJobsFragment.OnSavedJobClickListener
) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = tabList.size

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) AllJobsFragment.newInstance(tabList[position])
            .apply { setOnJobClickListener(allJobsClickListener) }
        else SavedJobsFragment.newInstance(tabList[position])
            .apply { setOnJobClickListener(savedJobsClickListener) }
    }
}