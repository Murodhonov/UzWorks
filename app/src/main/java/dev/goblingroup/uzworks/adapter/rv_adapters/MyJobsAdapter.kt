package dev.goblingroup.uzworks.adapter.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.databinding.MyAnnouncementItemBinding
import dev.goblingroup.uzworks.models.response.JobResponse

class MyJobsAdapter(
    private val jobList: List<JobResponse>,
    private val jobCategoryList: List<JobCategoryEntity>,
    private val districtList: List<DistrictEntity>,
    private val regionList: List<RegionEntity>,
    private val onItemClick: (String) -> Unit,
    private val onItemLongClick: (String) -> Unit
) : RecyclerView.Adapter<MyJobsAdapter.MyJobViewHolder>() {

    inner class MyJobViewHolder(private val itemBinding: MyAnnouncementItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(jobResponse: JobResponse) {
            itemBinding.apply {
                titleTv.text = jobResponse.title
                titleTv.text = jobResponse.title
                costTv.text = "${jobResponse.salary} so'm"
                genderTv.text = jobResponse.gender
                categoryTv.text = getJobCategory(jobResponse.categoryId)
                addressTv.text = getAddress(jobResponse.districtId)

                root.setOnClickListener {
                    onItemClick.invoke(jobResponse.id)
                }

                root.setOnLongClickListener {
                    onItemLongClick.invoke(jobResponse.id)
                    true
                }
            }
        }

        private fun getAddress(districtId: String): String {
            val district = districtList.find { it.id == districtId }
            val region = regionList.find { it.id == district?.regionId }
            return "${district?.name}, ${region?.name}"
        }

        private fun getJobCategory(categoryId: String): String {
            return jobCategoryList.find { it.id == categoryId }?.title.toString()
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