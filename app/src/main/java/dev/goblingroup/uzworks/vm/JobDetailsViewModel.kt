package dev.goblingroup.uzworks.vm

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.repository.AnnouncementRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.PeriodEnum
import dev.goblingroup.uzworks.utils.timeAgo
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobDetailsViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val securityRepository: SecurityRepository
) : ViewModel() {

    private val jobLiveData = MutableLiveData<ApiStatus<JobResponse>>(ApiStatus.Loading())

    fun fetchJob(jobId: String): LiveData<ApiStatus<JobResponse>> {
        viewModelScope.launch {
                val jobByIdResponse = announcementRepository.getJobById(jobId)
                if (jobByIdResponse.isSuccessful) {
                    jobLiveData.postValue(ApiStatus.Success(jobByIdResponse.body()))
                } else {
                    jobLiveData.postValue(ApiStatus.Error(Throwable(jobByIdResponse.message())))
                    Log.e(TAG, "fetchJob: ${jobByIdResponse.errorBody()}", )
                    Log.e(TAG, "fetchJob: ${jobByIdResponse.code()}", )
                    Log.e(TAG, "fetchJob: ${jobByIdResponse.message()}", )
                }
        }
        return jobLiveData
    }

    fun getLanguageCode() = securityRepository.getLanguageCode()

    fun getTimeAgo(time: String, resources: Resources): String {
        val (period, timeUnit) = time.timeAgo()
        return when (timeUnit) {
            PeriodEnum.SECONDS.label -> {
                "$period ${if (period == 1) resources.getString(R.string.second) else resources.getString(R.string.seconds)} ago"
            }

            PeriodEnum.MINUTES.label -> {
                "$period ${if (period == 1) resources.getString(R.string.minute) else resources.getString(R.string.minutes)} ago"
            }

            PeriodEnum.HOURS.label -> {
                "$period ${if (period == 1) resources.getString(R.string.hour) else resources.getString(R.string.hours)} ago"
            }

            PeriodEnum.DAYS.label -> {
                "$period ${if (period == 1) resources.getString(R.string.day) else resources.getString(R.string.days)} ago"
            }

            PeriodEnum.WEEKS.label -> {
                "$period ${if (period == 1) resources.getString(R.string.week) else resources.getString(R.string.weeks)} ago"
            }

            PeriodEnum.MONTHS.label -> {
                "$period ${if (period == 1) resources.getString(R.string.month) else resources.getString(R.string.months)} ago"
            }

            PeriodEnum.YEARS.label -> {
                "$period ${if (period == 1) resources.getString(R.string.year) else resources.getString(R.string.years)} ago"
            }

            else -> ""
        }
    }

}