package dev.goblingroup.uzworks.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.AnnouncementEntity
import dev.goblingroup.uzworks.databinding.AnnouncementItemBinding
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.vm.SavedAnnouncementsViewModel

class SavedAnnouncementsAdapter(
    private val savedAnnouncements: List<AnnouncementEntity>,
    private val resources: Resources,
    private val onItemClick: (String, String, Int) -> Unit,
    private val onSaveClick: (String, Int) -> Unit
    /**
     * boolean parameter:
     * true -> just saved
     * false -> just unsaved
     * this parameter for notifying saving/un_saving event among all and saved announcements
     */
) : RecyclerView.Adapter<SavedAnnouncementsAdapter.AnnouncementsViewHolder>() {

    inner class AnnouncementsViewHolder(private val announcementItemBinding: AnnouncementItemBinding) :
        RecyclerView.ViewHolder(announcementItemBinding.root) {
        fun bindAnnouncement(announcement: AnnouncementEntity, position: Int) {
            announcementItemBinding.apply {
                titleTv.isSelected = true
                addressTv.isSelected = true

                titleTv.text = announcement.title
                costTv.text = "${announcement.salary} ${resources.getString(R.string.money_unit)}"
                saveIv.setImageResource(R.drawable.ic_saved)
                categoryTv.text = announcement.categoryName
                addressTv.text = "${announcement.regionName}, ${announcement.districtName}"
                iv.setImageResource(announcement.pictureResId)
                if (announcement.isTop) {
                    badgeIv.visibility = View.VISIBLE
                }

                genderTv.text = when (announcement.gender) {
                    GenderEnum.MALE.label -> {
                        resources.getString(R.string.male)
                    }

                    GenderEnum.FEMALE.label -> {
                        resources.getString(R.string.female)
                    }

                    GenderEnum.UNKNOWN.label -> {
                        resources.getString(R.string.unknown)
                    }

                    else -> {
                        ""
                    }
                }

                saveIv.setOnClickListener {
                    onSaveClick.invoke(announcement.id, position)
                }

                root.setOnClickListener {
                    onItemClick.invoke(announcement.id, announcement.announcementType, position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementsViewHolder {
        return AnnouncementsViewHolder(
            AnnouncementItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = savedAnnouncements.size

    override fun onBindViewHolder(holder: AnnouncementsViewHolder, position: Int) {
        holder.bindAnnouncement(savedAnnouncements[position], position)
    }
}