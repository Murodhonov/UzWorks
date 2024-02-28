package dev.goblingroup.uzworks.adapter.rv_adapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.AnnouncementEntity
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.databinding.AnnouncementItemBinding
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.getImage
import dev.goblingroup.uzworks.vm.AddressViewModel
import dev.goblingroup.uzworks.vm.AnnouncementViewModel
import dev.goblingroup.uzworks.vm.JobCategoryViewModel

class AllAnnouncementsAdapter(
    private val announcementViewModel: AnnouncementViewModel,
    private val jobCategoryViewModel: JobCategoryViewModel,
    private val addressViewModel: AddressViewModel,
    private val resources: Resources,
    private val onItemClick: (String) -> Unit,
    private val onSaveClick: (Boolean, String) -> Unit
    /**
     * boolean parameter:
     * true -> just saved
     * false -> just unsaved
     * this parameter for notifying saving/un_saving event among all and saved announcements
     */
) : RecyclerView.Adapter<AllAnnouncementsAdapter.AnnouncementsViewHolder>() {

    inner class AnnouncementsViewHolder(private val announcementItemBinding: AnnouncementItemBinding) :
        RecyclerView.ViewHolder(announcementItemBinding.root) {
        fun bindAnnouncement(announcement: AnnouncementEntity) {
            announcementItemBinding.apply {
                titleTv.text = announcement.title
                costTv.text = "${announcement.salary} so'm"
                categoryTv.text = getJobCategory(announcement.categoryId.toString())
                addressTv.text = getAddress(announcement.districtId.toString())
                iv.setImageResource(announcement.getImage())

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

                if (announcement.isSaved) {
                    saveIv.setImageResource(R.drawable.ic_saved)
                } else {
                    saveIv.setImageResource(R.drawable.ic_unsaved)
                }

                saveIv.setOnClickListener {
                    if (announcementViewModel.isAnnouncementSaved(announcement.id)) {
                        announcementViewModel.unSaveAnnouncement(announcement.id)
                        saveIv.setImageResource(R.drawable.ic_unsaved)
                        onSaveClick.invoke(false, announcement.id)
                    } else {
                        announcementViewModel.saveAnnouncement(announcement.id)
                        saveIv.setImageResource(R.drawable.ic_saved)
                        onSaveClick.invoke(true, announcement.id)
                    }
                }

                root.setOnClickListener {
                    onItemClick.invoke(announcement.id)
                }
            }
        }

        private fun getAddress(districtId: String): String {
            return "${addressViewModel.findDistrict(districtId).name}, ${addressViewModel.findRegionByDistrictId(districtId).name}"
        }

        private fun getJobCategory(categoryId: String): String {
            return jobCategoryViewModel.findJobCategory(categoryId).title
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

    override fun getItemCount(): Int = announcementViewModel.listAnnouncements().size

    override fun onBindViewHolder(holder: AnnouncementsViewHolder, position: Int) {
        holder.bindAnnouncement(announcementViewModel.listAnnouncements()[position])
    }
}