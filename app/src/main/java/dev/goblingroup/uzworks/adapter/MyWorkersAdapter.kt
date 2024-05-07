package dev.goblingroup.uzworks.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.MyAnnouncementItemBinding
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.utils.GenderEnum

class MyWorkersAdapter(
    private val jobList: List<WorkerResponse>,
    private val resources: Resources,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<MyWorkersAdapter.MyJobViewHolder>() {

    inner class MyJobViewHolder(private val itemBinding: MyAnnouncementItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(workerResponse: WorkerResponse, position: Int) {
            itemBinding.apply {
                titleTv.isSelected = true
                addressTv.isSelected = true
                titleTv.text = workerResponse.title
                titleTv.text = workerResponse.title
                costTv.text = "${workerResponse.salary} ${resources.getString(R.string.money_unit)}"
                genderTv.text = when (workerResponse.gender) {
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
                categoryTv.text = workerResponse.categoryName
                addressTv.text = "${workerResponse.regionName}, ${workerResponse.districtName}"

                if (workerResponse.status) {
                    statusIv.setImageResource(R.drawable.ic_done)
                } else {
                    statusIv.setImageResource(R.drawable.ic_pending)
                }

                root.setOnClickListener {
                    onItemClick.invoke(workerResponse.id)
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