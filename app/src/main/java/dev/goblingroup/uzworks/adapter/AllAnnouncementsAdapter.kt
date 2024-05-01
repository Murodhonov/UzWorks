package dev.goblingroup.uzworks.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.AnnouncementEntity
import dev.goblingroup.uzworks.databinding.AnnouncementItemBinding
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.utils.AnnouncementEnum
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.getImage
import dev.goblingroup.uzworks.vm.AllAnnouncementsViewModel
import dev.goblingroup.uzworks.vm.HomeViewModel

class AllAnnouncementsAdapter(
    private val allAnnouncementsViewModel: AllAnnouncementsViewModel,
    private val announcementList: List<Any>,
    private val resources: Resources,
    private val onItemClick: (String, String) -> Unit,
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
        fun bindAnnouncement(announcement: Any) {
            announcementItemBinding.apply {
                when (announcement) {
                    is JobResponse -> {
                        titleTv.text = announcement.title
                        costTv.text = "${announcement.salary} ${resources.getString(R.string.money_unit)}"
                        categoryTv.text = announcement.categoryName
                        addressTv.text = "${announcement.regionName}, ${announcement.districtName}"
                        iv.setImageResource(getImage(AnnouncementEnum.JOB.announcementType, announcement.gender))
                        if (announcement.isTop) {
                            badgeIv.visibility = View.VISIBLE
                        }

                        genderTv.text = when (announcement.gender) {
                            GenderEnum.MALE.code -> {
                                resources.getString(R.string.male)
                            }

                            GenderEnum.FEMALE.code -> {
                                resources.getString(R.string.female)
                            }

                            else -> {
                                ""
                            }
                        }

                        if (allAnnouncementsViewModel.isSaved(announcement.id)) {
                            saveIv.setImageResource(R.drawable.ic_saved)
                        } else {
                            saveIv.setImageResource(R.drawable.ic_unsaved)
                        }
                    }
                    is WorkerResponse -> {
                        titleTv.text = announcement.title
                        costTv.text = "${announcement.salary} ${resources.getString(R.string.money_unit)}"
                        categoryTv.text = announcement.categoryName
                        addressTv.text = "${announcement.regionName}, ${announcement.districtName}"
                        iv.setImageResource(getImage(AnnouncementEnum.JOB.announcementType, announcement.gender))

                        genderTv.text = when (announcement.gender) {
                            GenderEnum.MALE.code -> {
                                resources.getString(R.string.male)
                            }

                            GenderEnum.FEMALE.code -> {
                                resources.getString(R.string.female)
                            }

                            else -> {
                                ""
                            }
                        }

                        if (allAnnouncementsViewModel.isSaved(announcement.id)) {
                            saveIv.setImageResource(R.drawable.ic_saved)
                        } else {
                            saveIv.setImageResource(R.drawable.ic_unsaved)
                        }
                    }
                }

                saveIv.setOnClickListener {
                    when (announcement) {
                        is JobResponse -> {
                            if (allAnnouncementsViewModel.isSaved(announcement.id)) {
                                allAnnouncementsViewModel.unSave(announcement.id)
                                saveIv.setImageResource(R.drawable.ic_unsaved)
                                onSaveClick.invoke(false, announcement.id)
                            } else {
                                allAnnouncementsViewModel.save(announcement.mapToEntity())
                                saveIv.setImageResource(R.drawable.ic_saved)
                                onSaveClick.invoke(true, announcement.id)
                            }
                        }
                        is WorkerResponse -> {
                            if (allAnnouncementsViewModel.isSaved(announcement.id)) {
                                allAnnouncementsViewModel.unSave(announcement.id)
                                saveIv.setImageResource(R.drawable.ic_unsaved)
                                onSaveClick.invoke(false, announcement.id)
                            } else {
                                allAnnouncementsViewModel.save(announcement.mapToEntity())
                                saveIv.setImageResource(R.drawable.ic_saved)
                                onSaveClick.invoke(true, announcement.id)
                            }
                        }
                    }
                }

                root.setOnClickListener {
                    when (announcement) {
                        is JobResponse -> {
                            onItemClick.invoke(announcement.id, AnnouncementEnum.JOB.announcementType)
                        }
                        is WorkerResponse -> {
                            onItemClick.invoke(announcement.id, AnnouncementEnum.WORKER.announcementType)
                        }
                    }
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

    override fun getItemCount(): Int = announcementList.size

    override fun onBindViewHolder(holder: AnnouncementsViewHolder, position: Int) {
        holder.bindAnnouncement(announcementList[position])
    }
}