package dev.goblingroup.uzworks.adapters.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.databinding.ExperienceItemBinding

class ExperienceAdapter : RecyclerView.Adapter<ExperienceAdapter.ExperienceViewHolder>() {

    inner class ExperienceViewHolder(private val itemBinding: ExperienceItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind() {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceViewHolder {
        return ExperienceViewHolder(
            ExperienceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = 4

    override fun onBindViewHolder(holder: ExperienceViewHolder, position: Int) {
        holder.onBind()
    }
}