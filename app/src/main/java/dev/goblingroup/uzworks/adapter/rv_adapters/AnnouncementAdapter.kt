package dev.goblingroup.uzworks.adapter.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.database.entity.WorkerEntity
import dev.goblingroup.uzworks.databinding.AnnouncementItemBinding

class AnnouncementAdapter(
    private val announcementList: List<Any>,
    private val jobCategoryList: List<JobCategoryEntity>,
    private val onItemClick: (String) -> Unit,
    private val onSaveClick: (Boolean, String) -> Unit
) : RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder>() {

    inner class AnnouncementViewHolder(private val itemBinding: AnnouncementItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindJob(announcement: JobEntity) {
            itemBinding.apply {
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
                        onSaveClick.invoke(false, announcement.id)
                    } else {
                        saveIv.setImageResource(R.drawable.ic_saved)
                        onSaveClick.invoke(true, announcement.id)
                    }
                }

                root.setOnClickListener {
                    onItemClick.invoke(announcement.id)
                }
            }
        }

        fun bindWorker(announcement: WorkerEntity) {
            itemBinding.apply {
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
                        onSaveClick.invoke(false, announcement.id)
                    } else {
                        saveIv.setImageResource(R.drawable.ic_saved)
                        onSaveClick.invoke(true, announcement.id)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        return AnnouncementViewHolder(
            AnnouncementItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = announcementList.size

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        when (val announcement = announcementList[position]) {
            is JobEntity -> {
                holder.bindJob(announcement)
            }

            is WorkerEntity -> {
                holder.bindWorker(announcement)
            }

            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

}