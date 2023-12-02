package dev.goblingroup.uzworks.adapters.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.WorkAnnouncementsLayoutBinding

class WorksAdapter(
    val onItemClick: (position: Int) -> Unit
) : RecyclerView.Adapter<WorksAdapter.RecommendedWorkViewHolder>() {

    inner class RecommendedWorkViewHolder(private val workAnnouncementBinding: WorkAnnouncementsLayoutBinding) :
        RecyclerView.ViewHolder(workAnnouncementBinding.root) {
        fun onBind(position: Int) {
            workAnnouncementBinding.apply {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendedWorkViewHolder {
        return RecommendedWorkViewHolder(
            WorkAnnouncementsLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int = 10

    override fun onBindViewHolder(holder: RecommendedWorkViewHolder, position: Int) {
        holder.onBind(position)
    }
}