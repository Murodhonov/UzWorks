package dev.goblingroup.uzworks.adapters.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.WorkerEntity
import dev.goblingroup.uzworks.databinding.WorkerAnnouncementsLayoutBinding

class SavedWorkersAdapter(
    private val workerList: List<WorkerEntity>,
    private val jobCategoryList: List<JobCategoryEntity>,
    private val onItemClick: (String) -> Unit,
    private val unSaveWorker: (String, Int) -> Unit
) : RecyclerView.Adapter<SavedWorkersAdapter.WorkerViewHolder>() {

    inner class WorkerViewHolder(private val workBinding: WorkerAnnouncementsLayoutBinding) :
        ViewHolder(workBinding.root) {
        fun onBind(position: Int) {
            workBinding.apply {
                val worker = workerList[position]

                titleTv.text = worker.tgUserName
                costTv.text = "${worker.salary} so'm"
                genderTv.text = worker.gender
                categoryTv.text = getWorkerCategory(position)
                saveIv.setImageResource(R.drawable.ic_saved)

                saveIv.setOnClickListener {
                    unSaveWorker.invoke(workerList[position].id, position)
                }


                root.setOnClickListener {
                    onItemClick.invoke(worker.id)
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