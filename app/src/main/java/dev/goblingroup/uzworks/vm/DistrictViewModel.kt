package dev.goblingroup.uzworks.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
            if (networkHelper.isConnected()) {
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

    fun findDistrict(districtId: String) = districtRepository.findDistrict(districtId)

    fun listDistrictsByRegionId(regionId: String) =
        districtRepository.listDistrictsByRegionId(regionId)

    fun getRegionByDistrictId(districtId: String) =
        districtRepository.getRegionByDistrictId(districtId)

    fun listDistricts() = districtRepository.listDistricts()

}