package com.goblindevs.uzworks.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.databinding.ExperienceItemBinding
import com.goblindevs.uzworks.models.response.ExperienceResponse

class ExperienceAdapter(
    private val experienceList: List<ExperienceResponse>,
    private val resources: Resources,
    private val editClick: (ExperienceResponse, Int) -> Unit,
    private val deleteClick: (String, Int) -> Unit
) : RecyclerView.Adapter<ExperienceAdapter.ExperienceViewHolder>() {

    inner class ExperienceViewHolder(private val itemBinding: ExperienceItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(experienceResponse: ExperienceResponse, position: Int) {
            itemBinding.apply {
                positionTv.isSelected = true
                companyNameTv.isSelected = true
                durationTv.isSelected = true

                positionTv.text = experienceResponse.position
                companyNameTv.text = experienceResponse.companyName
                durationTv.text = resources.getString(R.string.date_range_format, experienceResponse.startDate, experienceResponse.endDate)
                editExperienceBtn.setOnClickListener {
                    editClick.invoke(experienceResponse, position)
                }

                deleteExperienceBtn.setOnClickListener {
                    deleteClick.invoke(experienceResponse.id, position)
                }
            }
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

    override fun getItemCount(): Int = experienceList.size

    override fun onBindViewHolder(holder: ExperienceViewHolder, position: Int) {
        holder.onBind(experienceList[position], position)
    }
}