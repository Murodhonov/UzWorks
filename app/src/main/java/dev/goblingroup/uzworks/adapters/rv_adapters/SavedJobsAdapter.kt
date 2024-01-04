package dev.goblingroup.uzworks.adapters.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.WorkAnnouncementsLayoutBinding
import dev.goblingroup.uzworks.vm.JobsViewModel

class SavedJobsAdapter(
    private val jobsViewModel: JobsViewModel,
    private val onItemClick: (Int) -> Unit,
) : RecyclerView.Adapter<SavedJobsAdapter.JobViewHolder>() {

    inner class JobViewHolder(val workBinding: WorkAnnouncementsLayoutBinding) :
        ViewHolder(workBinding.root) {
        fun onBind(position: Int) {
            workBinding.apply {
                val job = jobsViewModel.listSavedJobs()[position]

                titleTv.text = job.title
                costTv.text = "${job.salary} so'm"

                if (job.isSaved) {
                    saveIv.setImageResource(R.drawable.ic_saved)
                } else {
                    saveIv.setImageResource(R.drawable.ic_unsaved)
                }

                saveIv.setOnClickListener {
                    val itemCount = jobsViewModel.listSavedJobs().size
                    if (job.isSaved) {
                        /**
                         * image should change to un saved
                         */
                        saveIv.setImageResource(R.drawable.ic_unsaved)
                    } else {
                        /**
                         * image should change to saved
                         */
                        saveIv.setImageResource(R.drawable.ic_saved)
                    }
                    jobsViewModel.updateJobSaved(job.id, !job.isSaved)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount - position)
                }


                root.setOnClickListener {
                    onItemClick.invoke(position)
                }
            }
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

    override fun getItemCount(): Int = jobsViewModel.listSavedJobs().size

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.onBind(position)
    }

}