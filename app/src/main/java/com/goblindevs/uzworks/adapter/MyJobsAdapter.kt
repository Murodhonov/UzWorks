package com.goblindevs.uzworks.adapter

import android.content.res.Resources
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.databinding.MyAnnouncementItemBinding
import com.goblindevs.uzworks.models.response.JobResponse
import com.goblindevs.uzworks.utils.ConstValues.TAG
import com.goblindevs.uzworks.utils.GenderEnum
import com.goblindevs.uzworks.utils.formatSalary

class MyJobsAdapter(
    private val jobList: List<JobResponse>,
    private val resources: Resources,
    private val onItemClick: (String, String) -> Unit
) : RecyclerView.Adapter<MyJobsAdapter.MyJobViewHolder>() {

    inner class MyJobViewHolder(private val itemBinding: MyAnnouncementItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(jobResponse: JobResponse, position: Int) {
            itemBinding.apply {
                titleTv.isSelected = true
                addressTv.isSelected = true

                titleTv.text = jobResponse.title
                costTv.text = "${jobResponse.salary.toString().formatSalary()} ${resources.getString(R.string.money_unit)}"
                addressTv.text = "${jobResponse.district.region.name}, ${jobResponse.district.name}"

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
                genderTv.post {
                    val availableWidth = genderTv.width
                    val textWidth = genderTv.paint.measureText(jobResponse.gender)

                    if (textWidth > availableWidth) {
                        genderTv.apply {
                            isSingleLine = true
                            ellipsize = TextUtils.TruncateAt.MARQUEE
                            marqueeRepeatLimit = -1
                            isFocusable = true
                            isFocusableInTouchMode = true
                            isSelected = true
                            gravity = Gravity.START
                        }
                    } else {
                        genderTv.apply {
                            isSingleLine = true
                            ellipsize = null
                            gravity = Gravity.CENTER
                        }
                    }
                }

                categoryTv.text = jobResponse.jobCategory.title
                categoryTv.post {
                    val availableWidth = categoryTv.width
                    val textWidth = categoryTv.paint.measureText(jobResponse.jobCategory.title)

                    if (textWidth > availableWidth) {
                        categoryTv.apply {
                            isSingleLine = true
                            ellipsize = TextUtils.TruncateAt.MARQUEE
                            marqueeRepeatLimit = -1
                            isFocusable = true
                            isFocusableInTouchMode = true
                            isSelected = true
                            gravity = Gravity.START
                        }
                    } else {
                        categoryTv.apply {
                            isSingleLine = true
                            ellipsize = null
                            gravity = Gravity.CENTER
                        }
                    }
                }

                if (jobResponse.status) {
                    statusIv.setImageResource(R.drawable.ic_done)
                } else {
                    statusIv.setImageResource(R.drawable.ic_pending)
                }

                root.setOnClickListener {
                    Log.d(TAG, "onBind: checking delete job progress ${jobResponse.id} clicked on position $position")
                    onItemClick.invoke(jobResponse.title, jobResponse.id)
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