package dev.goblingroup.uzworks.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.MyAnnouncementItemBinding
import dev.goblingroup.uzworks.models.response.JobResponse

class MyJobsAdapter(
    private val jobList: List<JobResponse>,
    private val resources: Resources,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<MyJobsAdapter.MyJobViewHolder>() {

    inner class MyJobViewHolder(private val itemBinding: MyAnnouncementItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(jobResponse: JobResponse) {
            itemBinding.apply {
                titleTv.text = jobResponse.title
                costTv.text = "${jobResponse.salary} ${resources.getString(R.string.money_unit)}"
                genderTv.text = jobResponse.gender
                categoryTv.text = jobResponse.categoryName
                addressTv.text = "${jobResponse.regionName}, ${jobResponse.districtName}"

                root.setOnClickListener {
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
        holder.onBind(jobList[position])
    }

}