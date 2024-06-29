package com.goblindevs.uzworks.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.databinding.AnnouncementItemBinding
import com.goblindevs.uzworks.mapper.mapToEntity
import com.goblindevs.uzworks.models.response.JobResponse
import com.goblindevs.uzworks.models.response.WorkerResponse
import com.goblindevs.uzworks.utils.AnnouncementEnum
import com.goblindevs.uzworks.utils.GenderEnum
import com.goblindevs.uzworks.utils.formatSalary
import com.goblindevs.uzworks.utils.getImage
import com.goblindevs.uzworks.vm.HomeViewModel

class HomeAdapter(
    private val homeViewModel: HomeViewModel,
    private val announcementList: List<Any>,
    private val resources: Resources,
    private val onItemClick: (String, String) -> Unit,
) : RecyclerView.Adapter<HomeAdapter.AnnouncementsViewHolder>() {

    inner class AnnouncementsViewHolder(private val announcementItemBinding: AnnouncementItemBinding) :
        RecyclerView.ViewHolder(announcementItemBinding.root) {
        fun bindAnnouncement(announcement: Any, position: Int) {
            announcementItemBinding.apply {
                titleTv.isSelected = true
                addressTv.isSelected = true
                categoryTv.isSelected = true

                when (announcement) {
                    is JobResponse -> {
                        titleTv.text = announcement.title
                        costTv.text = "${announcement.salary.toString().formatSalary()} ${
                            resources.getString(R.string.money_unit)
                        }"
                        categoryTv.text = announcement.jobCategory.title
                        addressTv.text = "${announcement.district.region.name}, ${announcement.district.name}"
                        iv.setImageResource(
                            getImage(
                                AnnouncementEnum.JOB.announcementType,
                                announcement.gender,
                                position
                            )
                        )
                        badgeIv.visibility = View.VISIBLE

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

                        if (homeViewModel.isSaved(announcement.id)) {
                            saveIv.setImageResource(R.drawable.ic_saved)
                        } else {
                            saveIv.setImageResource(R.drawable.ic_unsaved)
                        }
                    }
                    is WorkerResponse -> {
                        titleTv.text = announcement.title
                        costTv.text = "${announcement.salary.toString().formatSalary()} ${
                            resources.getString(R.string.money_unit)
                        }"
                        categoryTv.text = announcement.jobCategory.title
                        addressTv.text = "${announcement.district.region.name}, ${announcement.district.name}"
                        iv.setImageResource(
                            getImage(
                                AnnouncementEnum.WORKER.announcementType,
                                announcement.gender,
                                position
                            )
                        )
                        badgeIv.visibility = View.VISIBLE

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

                        if (homeViewModel.isSaved(announcement.id)) {
                            saveIv.setImageResource(R.drawable.ic_saved)
                        } else {
                            saveIv.setImageResource(R.drawable.ic_unsaved)
                        }
                    }
                    else -> {}
                }

                saveIv.setOnClickListener {
                    when (announcement) {
                        is JobResponse -> {
                            if (homeViewModel.isSaved(announcement.id)) {
                                homeViewModel.unSave(announcement.id)
                                saveIv.setImageResource(R.drawable.ic_unsaved)
                            } else {
                                homeViewModel.save(announcement.mapToEntity(position))
                                saveIv.setImageResource(R.drawable.ic_saved)
                            }
                        }
                        is WorkerResponse -> {
                            if (homeViewModel.isSaved(announcement.id)) {
                                homeViewModel.unSave(announcement.id)
                                saveIv.setImageResource(R.drawable.ic_unsaved)
                            } else {
                                homeViewModel.save(announcement.mapToEntity())
                                saveIv.setImageResource(R.drawable.ic_saved)
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
        holder.bindAnnouncement(announcementList[position], position)
    }
}