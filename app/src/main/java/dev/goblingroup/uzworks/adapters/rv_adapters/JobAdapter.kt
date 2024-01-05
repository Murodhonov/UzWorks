package dev.goblingroup.uzworks.adapters.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.WorkAnnouncementsLayoutBinding
import dev.goblingroup.uzworks.vm.JobsViewModel

class JobAdapter(
    private val jobsViewModel: JobsViewModel,
    private val onItemClick: (String) -> Unit,
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    inner class JobViewHolder(val workBinding: WorkAnnouncementsLayoutBinding) :
        ViewHolder(workBinding.root) {
        fun onBind(position: Int) {
            workBinding.apply {
                titleTv.text = jobsViewModel.listDatabaseJobs()[position].tgUserName
                costTv.text = "${jobsViewModel.listDatabaseJobs()[position].salary} so'm"

                if (jobsViewModel.listDatabaseJobs()[position].isSaved) {
                    saveIv.setImageResource(R.drawable.ic_saved)
                } else {
                    saveIv.setImageResource(R.drawable.ic_unsaved)
                }

                saveIv.setOnClickListener {
                    if (jobsViewModel.listDatabaseJobs()[position].isSaved) {
                        /**
                         * should unSave
                         */
                        saveIv.setImageResource(R.drawable.ic_unsaved)
                        jobsViewModel.unSaveJob(jobsViewModel.listDatabaseJobs()[position].id)
                    } else {
                        /**
                         * should save
                         */
                        saveIv.setImageResource(R.drawable.ic_saved)
                        jobsViewModel.saveJob(jobsViewModel.listDatabaseJobs()[position].id)
                    }
                }


                root.setOnClickListener {
                    onItemClick.invoke(jobsViewModel.listDatabaseJobs()[position].id)
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

    override fun getItemCount(): Int = jobsViewModel.listDatabaseJobs().size

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.onBind(position)
    }

}