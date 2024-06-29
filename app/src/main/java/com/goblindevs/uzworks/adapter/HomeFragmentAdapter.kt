package com.goblindevs.uzworks.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.databinding.HomeAnnouncementBinding
import com.goblindevs.uzworks.databinding.HomeHeaderBinding
import com.goblindevs.uzworks.databinding.HomeStatisticsBinding
import com.goblindevs.uzworks.vm.ApiStatus
import com.goblindevs.uzworks.vm.HomeViewModel

class HomeFragmentAdapter(
    private val fragmentLifecycleOwner: LifecycleOwner,
    private val homeViewModel: HomeViewModel,
    private val resources: Resources,
    private val onItemClick: (String, String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class HeaderViewHolder(private val homeHeaderBinding: HomeHeaderBinding) :
        RecyclerView.ViewHolder(homeHeaderBinding.root) {
        fun onBind() {
            homeHeaderBinding.apply {
                homeViewModel.fullNameLiveData.observe(fragmentLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {

                        }

                        is ApiStatus.Loading -> {

                        }

                        is ApiStatus.Success -> {
                            greetingTv.text =
                                "${resources.getString(R.string.greeting)}\n${it.response}"
                        }
                    }
                }
            }
        }
    }

    inner class StatisticsViewHolder(private val homeStatisticsBinding: HomeStatisticsBinding) :
        RecyclerView.ViewHolder(homeStatisticsBinding.root) {
        fun onBind() {
            homeStatisticsBinding.apply {
                homeViewModel.jobCountLiveData.observe(fragmentLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {

                        }

                        is ApiStatus.Loading -> {
                            jobsCountProgress.visibility = View.VISIBLE
                            jobsCountTv.visibility = View.INVISIBLE
                        }

                        is ApiStatus.Success -> {
                            jobsCountProgress.visibility = View.INVISIBLE
                            jobsCountTv.visibility = View.VISIBLE
                            jobsCountTv.text = it.response.toString()
                        }
                    }
                }

                homeViewModel.workerCountLivedata.observe(fragmentLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {

                        }

                        is ApiStatus.Loading -> {
                            workersCountProgress.visibility = View.VISIBLE
                            workersCountTv.visibility = View.INVISIBLE
                        }

                        is ApiStatus.Success -> {
                            workersCountProgress.visibility = View.INVISIBLE
                            workersCountTv.visibility = View.VISIBLE
                            workersCountTv.text = it.response.toString()
                        }
                    }
                }

                homeViewModel.userCountLiveData.observe(fragmentLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {

                        }
                        is ApiStatus.Loading -> {
                           usersCountProgress.visibility = View.VISIBLE
                           usersCountTv.visibility = View.INVISIBLE
                        }
                        is ApiStatus.Success -> {
                            usersCountProgress.visibility = View.INVISIBLE
                            usersCountTv.visibility = View.VISIBLE
                            usersCountTv.text = it.response.toString()
                        }
                    }
                }
            }
        }

    }

    inner class HomeAnnouncementViewHolder(private val homeAnnouncementBinding: HomeAnnouncementBinding) :
        RecyclerView.ViewHolder(homeAnnouncementBinding.root) {
        fun onBind() {
            homeAnnouncementBinding.apply {
                homeViewModel.announcementLiveData.observe(fragmentLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            homeAnnouncementProgress.visibility = View.INVISIBLE
                            noAnnouncementsTv.visibility = View.VISIBLE
                        }

                        is ApiStatus.Loading -> {
                            homeAnnouncementProgress.visibility = View.VISIBLE
                            noAnnouncementsTv.visibility = View.INVISIBLE
                        }

                        is ApiStatus.Success -> {
                            homeAnnouncementProgress.visibility = View.INVISIBLE
                            noAnnouncementsTv.visibility = View.INVISIBLE
                            val adapter = HomeAdapter(
                                homeViewModel,
                                it.response!!,
                                resources
                            ) { announcementId, announcementType ->
                                onItemClick.invoke(announcementId, announcementType)
                            }
                            announcementRv.adapter = adapter
                            if (adapter.itemCount == 0) {
                                noAnnouncementsTv.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                HeaderViewHolder(
                    HomeHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            1 -> {
                StatisticsViewHolder(
                    HomeStatisticsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                HomeAnnouncementViewHolder(
                    HomeAnnouncementBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int = 3

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                holder.onBind()
            }

            is StatisticsViewHolder -> {
                holder.onBind()
            }

            is HomeAnnouncementViewHolder -> {
                holder.onBind()
            }
        }
    }

}