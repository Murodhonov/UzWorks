package dev.goblingroup.uzworks.adapter.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.database.entity.WorkerEntity
import dev.goblingroup.uzworks.databinding.AnnouncementItemBinding
import dev.goblingroup.uzworks.models.CombinedData
import dev.goblingroup.uzworks.vm.AddressViewModel

class AnnouncementsAdapter(
    private val combinedData: CombinedData,
    private val jobCategoryList: List<JobCategoryEntity>,
    private val addressViewModel: AddressViewModel,
    private val onItemClick: (String) -> Unit,
    private val onSaveClick: (Boolean, String, Int) -> Unit
) : RecyclerView.Adapter<AnnouncementsAdapter.AnnouncementsViewHolder>() {

    inner class AnnouncementsViewHolder(private val announcementItemBinding: AnnouncementItemBinding) :
        RecyclerView.ViewHolder(announcementItemBinding.root) {
        fun bindJob(announcement: JobEntity, position: Int) {
            announcementItemBinding.apply {
                titleTv.text = announcement.title
                costTv.text = "${announcement.salary} so'm"
                genderTv.text = announcement.gender
                categoryTv.text = getJobCategory(announcement.categoryId.toString())
                addressTv.text = getAddress(announcement.districtId.toString())

                if (announcement.isSaved) {
                    saveIv.setImageResource(R.drawable.ic_saved)
                } else {
                    saveIv.setImageResource(R.drawable.ic_unsaved)
                }

                saveIv.setOnClickListener {
                    if (announcement.isSaved) {
                        saveIv.setImageResource(R.drawable.ic_unsaved)
                        onSaveClick.invoke(false, announcement.id, position)
                    } else {
                        saveIv.setImageResource(R.drawable.ic_saved)
                        onSaveClick.invoke(true, announcement.id, position)
                    }
                }

                root.setOnClickListener {
                    onItemClick.invoke(announcement.id)
                }
            }
        }

        private fun getAddress(districtId: String): String {
            return "${addressViewModel.findDistrict(districtId)}, ${addressViewModel.findRegionByDistrictId(districtId)}"
        }

        fun bindWorker(announcement: WorkerEntity, position: Int) {
            announcementItemBinding.apply {
                titleTv.text = announcement.title
                costTv.text = "${announcement.salary} so'm"
                genderTv.text = announcement.gender
                categoryTv.text = getJobCategory(announcement.categoryId.toString())

                if (announcement.isSaved) {
                    saveIv.setImageResource(R.drawable.ic_saved)
                } else {
                    saveIv.setImageResource(R.drawable.ic_unsaved)
                }

                saveIv.setOnClickListener {
                    if (announcement.isSaved) {
                        saveIv.setImageResource(R.drawable.ic_unsaved)
                        onSaveClick.invoke(false, announcement.id, position)
                    } else {
                        saveIv.setImageResource(R.drawable.ic_saved)
                        onSaveClick.invoke(true, announcement.id, position)
                    }
                }

                root.setOnClickListener {
                    onItemClick.invoke(announcement.id)
                }
            }
        }

        private fun getJobCategory(categoryId: String): String {
            return jobCategoryList.find { it.id == categoryId }?.title.toString()
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

    override fun getItemCount(): Int = combinedData.workers?.size ?: combinedData.jobs?.size ?: 0

    override fun onBindViewHolder(holder: AnnouncementsViewHolder, position: Int) {
        if (combinedData.jobs != null) {
            holder.bindJob(combinedData.jobs[position], position)
        } else if (combinedData.workers != null) {
            holder.bindWorker(combinedData.workers[position], position)
        }
    }
}