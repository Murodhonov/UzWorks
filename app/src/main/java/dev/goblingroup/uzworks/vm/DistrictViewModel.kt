package dev.goblingroup.uzworks.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.models.response.DistrictCreateResponse
import dev.goblingroup.uzworks.models.response.DistrictResponse
import dev.goblingroup.uzworks.repository.DistrictRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DistrictViewModel @Inject constructor(
    private val districtRepository: DistrictRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _districtLiveData =
        MutableLiveData<ApiStatus<List<DistrictEntity>>>(ApiStatus.Loading())
    val districtLiveData get() = _districtLiveData

    private val districtByIdLiveData =
        MutableLiveData<ApiStatus<DistrictResponse>>(ApiStatus.Loading())

    private val districtByRegionIdLiveData =
        MutableLiveData<ApiStatus<List<DistrictCreateResponse>>>(ApiStatus.Loading())

    init {
        fetchDistricts()
    }

    private fun fetchDistricts() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                if (districtRepository.listDistricts().isNotEmpty()) {
                    _districtLiveData.postValue(ApiStatus.Success(districtRepository.listDistricts()))
                } else {
                    val response = districtRepository.getAllDistricts()
                    if (response.isSuccessful) {
                        val emptyDistrictList = ArrayList<DistrictEntity>()
                        response.body()?.forEach { districtCreateResponse ->
                            emptyDistrictList.add(districtCreateResponse.mapToEntity())
                        }
                        districtRepository.addDistricts(emptyDistrictList)
                        _districtLiveData.postValue(ApiStatus.Success(districtRepository.listDistricts()))
                    } else {
                        _districtLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                    }
                }
            } else {
                if (districtRepository.listDistricts().isNotEmpty()) {
                    _districtLiveData.postValue(ApiStatus.Success(districtRepository.listDistricts()))
                } else {
                    _districtLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
                }
            }
        }
    }

    fun getDistrictById(districtId: String): LiveData<ApiStatus<DistrictResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = districtRepository.getDistrictById(districtId)
                if (response.isSuccessful) {
                    districtByIdLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    districtByIdLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                districtByIdLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return districtByIdLiveData
    }

    fun getDistrictByRegionId(regionId: String): LiveData<ApiStatus<List<DistrictCreateResponse>>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = districtRepository.getDistrictByRegionId(regionId)
                if (response.isSuccessful) {
                    districtByIdLiveData
                }
            } else {
                districtByRegionIdLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return districtByRegionIdLiveData
    }

    suspend fun findDistrict(districtId: String) = districtRepository.findDistrict(districtId)

    suspend fun listDistrictsByRegionId(regionId: String) =
        districtRepository.listDistrictsByRegionId(regionId)

    suspend fun getRegionByDistrictId(districtId: String) =
        districtRepository.getRegionByDistrictId(districtId)

}