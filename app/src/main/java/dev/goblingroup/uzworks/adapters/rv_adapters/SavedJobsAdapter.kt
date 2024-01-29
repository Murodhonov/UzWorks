package dev.goblingroup.uzworks.adapters.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.databinding.WorkAnnouncementsLayoutBinding

class SavedJobsAdapter(
    private val jobList: List<JobEntity>,
    private val jobCategoryList: List<JobCategoryEntity>,
    private val onItemClick: (String) -> Unit,
    private val unSaveJob: (String, Int) -> Unit
) : RecyclerView.Adapter<SavedJobsAdapter.JobViewHolder>() {

    inner class JobViewHolder(val workBinding: WorkAnnouncementsLayoutBinding) :
        ViewHolder(workBinding.root) {
        fun onBind(position: Int) {
            workBinding.apply {
                val job = jobList[position]

                titleTv.text = job.tgUserName
                costTv.text = "${job.salary} so'm"
                genderTv.text = job.gender
                categoryTv.text = getJobCategory(position)
                saveIv.setImageResource(R.drawable.ic_saved)

                saveIv.setOnClickListener {
                    unSaveJob.invoke(jobList[position].id, position)
                }


                root.setOnClickListener {
                    onItemClick.invoke(job.id)
                }
            }
        }

        private fun getJobCategory(position: Int): String {
            return jobCategoryList.find { it.id == jobList[position].categoryId }?.title.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        return JobViewHolder(
            WorkAnnouncementsLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = jobList.size

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.onBind(position)
    }

}