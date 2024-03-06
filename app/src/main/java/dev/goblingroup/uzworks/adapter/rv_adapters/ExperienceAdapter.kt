package dev.goblingroup.uzworks.adapter.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.databinding.ExperienceItemBinding
import dev.goblingroup.uzworks.models.response.ExperienceResponse

class ExperienceAdapter(
    private val experienceList: List<ExperienceResponse>,
    private val editClick: (ExperienceResponse) -> Unit
) : RecyclerView.Adapter<ExperienceAdapter.ExperienceViewHolder>() {

    inner class ExperienceViewHolder(private val itemBinding: ExperienceItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(experienceResponse: ExperienceResponse, position: Int) {
            itemBinding.apply {
                positionTv.text = experienceResponse.position
                companyNameTv.text = experienceResponse.companyName
                durationTv.text = "${experienceResponse.startDate} ${experienceResponse.endDate}"
                editExperienceBtn.setOnClickListener {
                    editClick.invoke(experienceResponse)
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