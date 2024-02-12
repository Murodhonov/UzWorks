package dev.goblingroup.uzworks.adapter.view_pager_adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.goblingroup.uzworks.fragments.workers.AllWorkersFragment
import dev.goblingroup.uzworks.fragments.workers.SavedWorkersFragment

class WorkersPagerAdapter(
    fragment: Fragment,
    private val allWorkersClickListener: AllWorkersFragment.OnAllWorkerClickListener,
    private val savedWorkersClickListener: SavedWorkersFragment.OnSavedWorkerClickListener,
    private val onFindWorkerClickListener: SavedWorkersFragment.OnFindWorkerClickListener
) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) AllWorkersFragment.newInstance()
            .apply { setOnWorkerClickListener(allWorkersClickListener) }
        else SavedWorkersFragment.newInstance()
            .apply {
                setOnWorkerClickListener(savedWorkersClickListener)
                setOnFindWorkerClickListener(onFindWorkerClickListener)
            }
    }
}