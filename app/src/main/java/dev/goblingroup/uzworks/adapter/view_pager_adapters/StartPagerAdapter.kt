package dev.goblingroup.uzworks.adapter.view_pager_adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.goblingroup.uzworks.fragments.HomeFragment
import dev.goblingroup.uzworks.fragments.announcement.AnnouncementsFragment
import dev.goblingroup.uzworks.fragments.chat.ChatsListFragment
import dev.goblingroup.uzworks.fragments.profile.ProfileFragment

class StartPagerAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                HomeFragment.newInstance()
            }
            1 -> {
                AnnouncementsFragment.newInstance()
            }
            2 -> {
                ChatsListFragment.newInstance()
            }
            else -> {
                ProfileFragment.newInstance()
            }
        }
    }
}