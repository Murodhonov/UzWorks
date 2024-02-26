package dev.goblingroup.uzworks.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.database.dao.UserDao
import dev.goblingroup.uzworks.repository.HomeRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val userDao: UserDao,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _workerLivedata = MutableLiveData<ApiStatus<Int>>(ApiStatus.Loading())
    val workerLivedata = _workerLivedata

    private val _jobLiveData = MutableLiveData<ApiStatus<Int>>(ApiStatus.Loading())
    val jobLiveData = _jobLiveData

    init {
        countJobs()
        countWorkers()
    }

    private fun countJobs() {
        viewModelScope.launch {
            if (networkHelper.isConnected()) {
                val countJobsResponse = homeRepository.countJobs()
                if (countJobsResponse.isSuccessful) {
                    _jobLiveData.postValue(ApiStatus.Success(countJobsResponse.body()))
                } else {
                    _jobLiveData.postValue(ApiStatus.Error(Throwable(countJobsResponse.message())))
                }
            } else {
                _jobLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
    }

    private fun countWorkers() {
        viewModelScope.launch {
            if (networkHelper.isConnected()) {
                val countWorkersResponse = homeRepository.countWorkers()
                if (countWorkersResponse.isSuccessful) {
                    _workerLivedata.postValue(ApiStatus.Success(countWorkersResponse.body()))
                } else {
                    _workerLivedata.postValue(ApiStatus.Error(Throwable(countWorkersResponse.message())))
                }
            } else {
                _workerLivedata.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
    }

    fun getFullName(): String {
        return "${userDao.getUser()?.firstname} ${userDao.getUser()?.lastName}"
    }

}