package dev.goblingroup.uzworks.adapters.rv_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.LoadItemBinding
import dev.goblingroup.uzworks.databinding.WorkAnnouncementsLayoutBinding
import dev.goblingroup.uzworks.models.response.JobResponse

class JobAdapter(
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    private val LOADING = 0
    private val ITEM = 1
    private var isLoadingAdded = false
    private var jobList = ArrayList<JobResponse>()

    inner class JobViewHolder(val workBinding: WorkAnnouncementsLayoutBinding) :
        ViewHolder(workBinding.root) {
        fun onBind(position: Int) {
            workBinding.apply {
                titleTv.text = jobList[position].title
                costTv.text = "${jobList[position].salary} so'm"

                val random = (0 until 10).random()
                if (random % 2 == 0) {
                    saveIv.setImageResource(R.drawable.ic_saved)
                } else {
                    saveIv.setImageResource(R.drawable.ic_unsaved)
                }
                root.setOnClickListener {
                    onItemClick.invoke(position)
                }
            }
        }

    }

    inner class LoadViewHolder(val loadItemBinding: LoadItemBinding) :
        ViewHolder(loadItemBinding.root) {
        fun onBind() {
            loadItemBinding.progress.visibility = View.VISIBLE
        }

    }

    fun addAll(list: List<JobResponse>) {
        list.forEach {
            add(it)
        }
    }

    private fun add(jobResponse: JobResponse) {
        jobList.add(jobResponse)
        notifyItemInserted(jobList.size - 1)
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(JobResponse())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val lastPosition = jobList.size - 1
        jobList.removeAt(lastPosition)
        notifyItemRemoved(lastPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == LOADING) LoadViewHolder(
            LoadItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
        else JobViewHolder(
            WorkAnnouncementsLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = jobList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is JobViewHolder) {
            holder.onBind(position)
        } else if (holder is LoadViewHolder) {
            holder.onBind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == jobList.size - 1 && isLoadingAdded) return LOADING
        return ITEM
    }

}