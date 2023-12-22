package dev.goblingroup.uzworks.adapters.view_pager_adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.goblingroup.uzworks.fragments.main.profile.admin.FieldsFragment

class AdminPanelAdapter(
    fragment: Fragment,
    private val tabList: List<String>
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = tabList.size

    override fun createFragment(position: Int): Fragment {
        return FieldsFragment.newInstance(tabList[position])
    }
}