package dev.goblingroup.uzworks.adapter.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.databinding.JobAnnouncementsLayoutBinding

class JobAdapter(
    private val jobList: List<JobEntity>,
    private val jobCategoryList: List<JobCategoryEntity>,
    private val onItemClick: (String) -> Unit,
    private val onSaveClick: (Boolean, String) -> Unit
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    inner class JobViewHolder(private val jobBinding: JobAnnouncementsLayoutBinding) :
        ViewHolder(jobBinding.root) {
        fun onBind(position: Int) {
            jobBinding.apply {
                val job = jobList[position]
                titleTv.text = job.title
                costTv.text = "${job.salary} so'm"
                genderTv.text = job.gender
                categoryTv.text = getJobCategory(position)

                if (jobList[position].isSaved) {
                    saveIv.setImageResource(R.drawable.ic_saved)
                } else {
                    saveIv.setImageResource(R.drawable.ic_unsaved)
                }

                saveIv.setOnClickListener {
                    if (jobList[position].isSaved) {
                        saveIv.setImageResource(R.drawable.ic_unsaved)
                        onSaveClick.invoke(false, jobList[position].id)
                    } else {
                        saveIv.setImageResource(R.drawable.ic_saved)
                        onSaveClick.invoke(true, jobList[position].id)
                    }
                }


                root.setOnClickListener {
                    onItemClick.invoke(jobList[position].id)
                }
            }
        }

        private fun getJobCategory(position: Int): String {
            return jobCategoryList.find { it.id == jobList[position].categoryId }?.title.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        return JobViewHolder(
            JobAnnouncementsLayoutBinding.inflate(
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