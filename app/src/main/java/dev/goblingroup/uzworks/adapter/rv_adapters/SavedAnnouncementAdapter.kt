package dev.goblingroup.uzworks.adapter.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.database.entity.WorkerEntity
import dev.goblingroup.uzworks.databinding.AnnouncementItemBinding

class SavedAnnouncementAdapter(
    private val savedAnnouncementList: List<Any>,
    private val jobCategoryList: List<JobCategoryEntity>,
    private val onItemClick: (String) -> Unit,
    private val unSaveAnnouncement: (String, Int) -> Unit
) : RecyclerView.Adapter<SavedAnnouncementAdapter.SavedAnnouncementViewHolder>() {

    inner class SavedAnnouncementViewHolder(private val announcementItemBinding: AnnouncementItemBinding) :
        RecyclerView.ViewHolder(announcementItemBinding.root) {

        fun bindJob(announcement: JobEntity, position: Int) {
            announcementItemBinding.apply {
                titleTv.text = announcement.tgUserName
                costTv.text = "${announcement.salary} so'm"
                genderTv.text = announcement.gender
                categoryTv.text = getJobCategory(announcement.categoryId.toString())
                saveIv.setImageResource(R.drawable.ic_saved)

                saveIv.setOnClickListener {
                    unSaveAnnouncement.invoke(announcement.id, position)
                }

                root.setOnClickListener {
                    onItemClick.invoke(announcement.id)
                }
            }
        }

        fun bindWorker(announcement: WorkerEntity) {
            announcementItemBinding.apply {
                titleTv.text = announcement.tgUserName
                costTv.text = "${announcement.salary} so'm"
                genderTv.text = announcement.gender
                categoryTv.text = getJobCategory(announcement.categoryId.toString())
                saveIv.setImageResource(R.drawable.ic_saved)

                saveIv.setOnClickListener {
                    unSaveAnnouncement.invoke(announcement.id, position)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedAnnouncementViewHolder {
        return SavedAnnouncementViewHolder(
            AnnouncementItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = savedAnnouncementList.size

    override fun onBindViewHolder(holder: SavedAnnouncementViewHolder, position: Int) {
        when (val announcement = savedAnnouncementList[position]) {
            is JobEntity -> {
                holder.bindJob(announcement, position)
            }

            is WorkerEntity -> {
                holder.bindWorker(announcement)
            }

            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

}