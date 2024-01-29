package dev.goblingroup.uzworks.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.RegionEditRequest
import dev.goblingroup.uzworks.repository.secured.SecuredRegionRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuredRegionViewModel @Inject constructor(
    private val securedRegionRepository: SecuredRegionRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "SecuredDistrictViewMode"

    private val createLiveData =
        MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    private val deleteLiveData =
        MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    private val editLiveData =
        MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    fun createDistrict(regionName: String): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = securedRegionRepository.createRegion(regionName)
                if (response.isSuccessful) {
                    createLiveData.postValue(ApiStatus.Success(null))
                } else {
                    createLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                createLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return createLiveData
    }

    fun deleteDistrict(regionId: String): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = securedRegionRepository.deleteRegion(regionId)
                if (response.isSuccessful) {
                    deleteLiveData.postValue(ApiStatus.Success(null))
                } else {
                    deleteLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                deleteLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return deleteLiveData
    }

    fun editDistrict(regionEditRequest: RegionEditRequest): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = securedRegionRepository.editRegion(regionEditRequest)
                if (response.isSuccessful) {
                    editLiveData.postValue(ApiStatus.Success(null))
                } else {
                    editLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                editLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return editLiveData
    }

}