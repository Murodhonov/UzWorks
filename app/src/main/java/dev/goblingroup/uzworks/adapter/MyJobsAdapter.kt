package dev.goblingroup.uzworks.adapter

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.MyAnnouncementItemBinding
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.formatSalary

class MyJobsAdapter(
    private val jobList: List<JobResponse>,
    private val resources: Resources,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<MyJobsAdapter.MyJobViewHolder>() {

    inner class MyJobViewHolder(private val itemBinding: MyAnnouncementItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(jobResponse: JobResponse, position: Int) {
            itemBinding.apply {
                titleTv.isSelected = true
                addressTv.isSelected = true
                categoryTv.isSelected = true

                titleTv.text = jobResponse.title
                costTv.text = "${jobResponse.salary.toString().formatSalary()} ${resources.getString(R.string.money_unit)}"
                genderTv.text = when (jobResponse.gender) {
                    GenderEnum.MALE.label -> {
                        resources.getString(R.string.male)
                    }

                    GenderEnum.FEMALE.label -> {
                        resources.getString(R.string.female)
                    }

                    GenderEnum.UNKNOWN.label -> {
                        resources.getString(R.string.unknown)
                    }

                    else -> {
                        ""
                    }
                }
                categoryTv.text = jobResponse.categoryName
                addressTv.text = "${jobResponse.regionName}, ${jobResponse.districtName}"

                if (jobResponse.status) {
                    statusIv.setImageResource(R.drawable.ic_done)
                } else {
                    statusIv.setImageResource(R.drawable.ic_pending)
                }

                root.setOnClickListener {
                    Log.d(TAG, "onBind: checking delete job progress ${jobResponse.id} clicked on position $position")
                    onItemClick.invoke(jobResponse.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyJobViewHolder {
        return MyJobViewHolder(
            MyAnnouncementItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = jobList.size

    override fun onBindViewHolder(holder: MyJobViewHolder, position: Int) {
        holder.onBind(jobList[position], position)
    }

}