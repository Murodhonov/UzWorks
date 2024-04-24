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
    private val savedAnnouncementsViewModel: SavedAnnouncementsViewModel,
    private val savedAnnouncements: List<AnnouncementEntity>,
    private val resources: Resources,
    private val onItemClick: (String, String) -> Unit,
    private val onSaveClick: (Boolean, String) -> Unit
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

                    else -> {
                        ""
                    }
                }

                saveIv.setOnClickListener {
                    savedAnnouncementsViewModel.unSave(announcement.id)
                    onSaveClick.invoke(savedAnnouncementsViewModel.countAnnouncements() == 0, announcement.id)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, savedAnnouncementsViewModel.countAnnouncements() - position)
                }

                root.setOnClickListener {
                    onItemClick.invoke(announcement.id, announcement.announcementType)
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