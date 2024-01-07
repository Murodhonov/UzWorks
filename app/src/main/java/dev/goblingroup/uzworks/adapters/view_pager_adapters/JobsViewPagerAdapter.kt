package dev.goblingroup.uzworks.adapters.view_pager_adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.goblingroup.uzworks.fragments.main.jobs.job_list.AllJobsFragment
import dev.goblingroup.uzworks.fragments.main.jobs.job_list.SavedJobsFragment

class JobsViewPagerAdapter(
    fragment: Fragment,
    private val allJobsClickListener: AllJobsFragment.OnAllJobClickListener,
    private val savedJobsClickListener: SavedJobsFragment.OnSavedJobClickListener,
    private val onFindJobClickListener: SavedJobsFragment.OnFindJobClickListener
) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) AllJobsFragment.newInstance()
            .apply { setOnJobClickListener(allJobsClickListener) }
        else SavedJobsFragment.newInstance()
            .apply {
                setOnJobClickListener(savedJobsClickListener)
                setOnFindJobClickListener(onFindJobClickListener)
            }
    }
}