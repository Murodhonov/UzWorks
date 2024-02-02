package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.database.entity.WorkerEntity
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.repository.WorkersRepository
import dev.goblingroup.uzworks.utils.ConstValues
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

class WorkersViewModel @Inject constructor(
    private val workersRepository: WorkersRepository,
    private val networkHelper: NetworkHelper
): ViewModel() {

    init {
        getAllWorkers()
    }

    private val workerByIdLiveData = MutableLiveData<ApiStatus<WorkerResponse>>(ApiStatus.Loading())

    private val _workersLiveData = MutableLiveData<ApiStatus<List<WorkerEntity>>>(ApiStatus.Loading())
    val workersLiveData get() = _workersLiveData

    private val countLiveData = MutableLiveData<ApiStatus<Int>>(ApiStatus.Loading())

    private val workersByIdLiveData =
        MutableLiveData<ApiStatus<List<WorkerResponse>>>(ApiStatus.Loading())

    fun getWorkerById(workerId: String): LiveData<ApiStatus<WorkerResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = workersRepository.getWorkerById(workerId)
                if (response.isSuccessful) {
                    workerByIdLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    workerByIdLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                workerByIdLiveData.postValue(ApiStatus.Error(Throwable(ConstValues.NO_INTERNET)))
            }
        }
        return workerByIdLiveData
    }

    private fun getAllWorkers() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = workersRepository.getAllWorkers()
                if (response.isSuccessful) {
                    Log.d(ConstValues.TAG, "getAllWorkers: ${response.body()?.size} workers got")
                    workersRepository.addWorkers(response.body()!!)
                    _workersLiveData.postValue(ApiStatus.Success(workersRepository.listDatabaseWorkers()))
                } else {
                    _workersLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                if (workersRepository.countDatabaseWorkers() > 0) {
                    _workersLiveData.postValue(ApiStatus.Success(workersRepository.listDatabaseWorkers()))
                } else {
                    _workersLiveData.postValue(ApiStatus.Error(Throwable(ConstValues.NO_INTERNET)))
                }
            }
        }
    }

    fun countWorkers(): LiveData<ApiStatus<Int>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = workersRepository.countWorkers()
                if (response.isSuccessful) {
                    countLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    countLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                countLiveData.postValue(ApiStatus.Error(Throwable(ConstValues.NO_INTERNET)))
            }
        }
        return countLiveData
    }

    fun getWorkersByUserId(userId: String): LiveData<ApiStatus<List<WorkerResponse>>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = workersRepository.getWorkerByUserId(userId)
                if (response.isSuccessful) {
                    workersByIdLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    workersByIdLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                workersByIdLiveData.postValue(ApiStatus.Error(Throwable(ConstValues.NO_INTERNET)))
            }
        }
        return workersByIdLiveData
    }

    suspend fun addWorker(workerEntity: WorkerEntity) = workersRepository.addWorker(workerEntity)

    fun saveWorker(workerId: String) = workersRepository.saveWorker(workerId)

    suspend fun unSaveWorker(workerId: String) = workersRepository.unSaveWorker(workerId)

    suspend fun isWorkerSaved(workerId: String) = workersRepository.isWorkerSaved(workerId)

    suspend fun getWorker(workerId: String) = workersRepository.getWorker(workerId)

    suspend fun listDatabaseWorkers() = workersRepository.listDatabaseWorkers()

    fun listSavedWorkers() = workersRepository.listSavedWorkers()

    suspend fun countDatabaseWorkers() = workersRepository.countDatabaseWorkers()

    suspend fun countSavedWorkers() = workersRepository.countSavedWorkers()

}