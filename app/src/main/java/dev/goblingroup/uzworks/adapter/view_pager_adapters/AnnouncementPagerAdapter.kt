package dev.goblingroup.uzworks.adapter.view_pager_adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.goblingroup.uzworks.fragments.announcement.AllAnnouncementsFragment
import dev.goblingroup.uzworks.fragments.announcement.SavedAnnouncementsFragment

class AnnouncementPagerAdapter(
    fragment: Fragment,
    private val findAnnouncementClickListener: SavedAnnouncementsFragment.FindAnnouncementClickListener
) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) AllAnnouncementsFragment.newInstance()
        else SavedAnnouncementsFragment.newInstance().apply {
            setOnFindAnnouncementClickListener(findAnnouncementClickListener)
        }
    }
}