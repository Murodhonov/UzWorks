package com.goblindevs.uzworks.adapter

import android.content.res.Resources
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.databinding.MyAnnouncementItemBinding
import com.goblindevs.uzworks.models.response.WorkerResponse
import com.goblindevs.uzworks.utils.GenderEnum
import com.goblindevs.uzworks.utils.formatSalary

class MyWorkersAdapter(
    private val jobList: List<WorkerResponse>,
    private val resources: Resources,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<MyWorkersAdapter.MyJobViewHolder>() {

    inner class MyJobViewHolder(private val itemBinding: MyAnnouncementItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(workerResponse: WorkerResponse) {
            itemBinding.apply {
                titleTv.isSelected = true
                addressTv.isSelected = true

                titleTv.text = workerResponse.title
                titleTv.text = workerResponse.title
                costTv.text = "${workerResponse.salary.toString().formatSalary()} ${resources.getString(R.string.money_unit)}"
                addressTv.text =
                    "${workerResponse.district.region.name}, ${workerResponse.district.name}"

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
                genderTv.post {
                    val availableWidth = genderTv.width
                    val textWidth = genderTv.paint.measureText(workerResponse.jobCategory.title)

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


                categoryTv.text = workerResponse.jobCategory.title
                categoryTv.post {
                    val availableWidth = categoryTv.width
                    val textWidth = categoryTv.paint.measureText(workerResponse.jobCategory.title)

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
        holder.onBind(jobList[position])
    }

}