package dev.goblingroup.uzworks.adapters.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.WorkerEntity
import dev.goblingroup.uzworks.databinding.WorkerAnnouncementsLayoutBinding

class WorkerAdapter(
    private val workerList: List<WorkerEntity>,
    private val jobCategoryList: List<JobCategoryEntity>,
    private val onItemClick: (String) -> Unit,
    private val onSaveClick: (Boolean, String) -> Unit
) : RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder>(){

    inner class WorkerViewHolder(val workBinding: WorkerAnnouncementsLayoutBinding) :
        RecyclerView.ViewHolder(workBinding.root) {
        fun onBind(position: Int) {
            workBinding.apply {
                val job = workerList[position]
                titleTv.text = job.title
                costTv.text = "${job.salary} so'm"
                genderTv.text = job.gender
                categoryTv.text = getWorkerCategory(position)

                if (workerList[position].isSaved) {
                    saveIv.setImageResource(R.drawable.ic_saved)
                } else {
                    saveIv.setImageResource(R.drawable.ic_unsaved)
                }

                saveIv.setOnClickListener {
                    if (workerList[position].isSaved) {
                        saveIv.setImageResource(R.drawable.ic_unsaved)
                        onSaveClick.invoke(false, workerList[position].id)
                    } else {
                        saveIv.setImageResource(R.drawable.ic_saved)
                        onSaveClick.invoke(true, workerList[position].id)
                    }
                }


                root.setOnClickListener {
                    onItemClick.invoke(workerList[position].id)
                }
            }
        }

        private fun getWorkerCategory(position: Int): String {
            return jobCategoryList.find { it.id == workerList[position].categoryId }?.title.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkerViewHolder {
        return WorkerViewHolder(
            WorkerAnnouncementsLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = workerList.size

    override fun onBindViewHolder(holder: WorkerViewHolder, position: Int) {
        holder.onBind(position)
    }

}