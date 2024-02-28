package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.repository.AddressRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _regionLiveData =
        MutableLiveData<ApiStatus<List<RegionEntity>>>(ApiStatus.Loading())
    val regionLiveData get() = _regionLiveData

    private val _districtLiveData =
        MutableLiveData<ApiStatus<List<DistrictEntity>>>(ApiStatus.Loading())
    val districtLiveData get() = _districtLiveData

    init {
        fetchRegions()
    }

    private fun fetchRegions() {
        viewModelScope.launch {
            if (networkHelper.isConnected()) {
                if (addressRepository.listRegions().isEmpty()) {
                    val regionResponse = addressRepository.getAllRegions()
                    if (regionResponse.isSuccessful) {
                        val regionIdList = ArrayList<String>()
                        val emptyRegionList = ArrayList<RegionEntity>()
                        regionResponse.body()?.forEach {
                            emptyRegionList.add(it.mapToEntity())
                            regionIdList.add(it.id)
                        }
                        if (addressRepository.addRegions(emptyRegionList)) {
                            _regionLiveData.postValue(ApiStatus.Success(addressRepository.listRegions()))
                            fetchDistricts(regionIdList)
                        }
                    } else {
                        _regionLiveData.postValue(ApiStatus.Error(Throwable(regionResponse.message())))
                        Log.e(TAG, "fetchRegions: ${regionResponse.code()}")
                        Log.e(TAG, "fetchRegions: ${regionResponse.message()}")
                        Log.e(TAG, "fetchRegions: ${regionResponse.errorBody()}")
                    }
                } else {
                    Log.d(TAG, "fetchRegions: there's something in region table")
                    _regionLiveData.postValue(ApiStatus.Success(addressRepository.listRegions()))
                    fetchDistricts(addressRepository.listDistricts().map { it.id })
                }
            } else {
                _regionLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
    }

    private fun fetchDistricts(regionIdList: List<String>) {
        viewModelScope.launch {
            if (networkHelper.isConnected()) {
                if (addressRepository.listDistricts().isEmpty()) {
                    val emptyDistrictList = ArrayList<DistrictEntity>()
                    var error = ""
                    regionIdList.forEach { regionId ->
                        var index = 0
                        val districtsByRegionIdResponse =
                            addressRepository.getDistrictsByRegionId(regionId)
                        if (districtsByRegionIdResponse.isSuccessful) {
                            districtsByRegionIdResponse.body()?.forEach { districtResponse ->
                                index++
                                emptyDistrictList.add(districtResponse.mapToEntity(regionId))
                            }
                            Log.d(TAG, "fetchDistricts: $index districts added by $regionId")
                        } else {
                            error = districtsByRegionIdResponse.message()
                            Log.e(TAG, "fetchDistricts: ${districtsByRegionIdResponse.code()}")
                            Log.e(TAG, "fetchDistricts: ${districtsByRegionIdResponse.message()}")
                            Log.e(TAG, "fetchDistricts: ${districtsByRegionIdResponse.errorBody()}")
                        }
                    }
                    Log.d(TAG, "fetchDistricts: error variable -> $error")
                    if (error.isEmpty() && addressRepository.addDistricts(emptyDistrictList)) {
                        _districtLiveData.postValue(ApiStatus.Success(addressRepository.listDistricts()))
                    } else {
                        _districtLiveData.postValue(ApiStatus.Error(Throwable(error)))
                    }
                } else {
                    Log.d(TAG, "fetchDistricts: there's something in district table")
                    _districtLiveData.postValue(ApiStatus.Success(addressRepository.listDistricts()))
                }
            } else {
                _districtLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
    }

    fun findRegion(regionId: String) = addressRepository.findRegion(regionId)

    fun findDistrict(districtId: String) = addressRepository.findDistrict(districtId)

    fun findRegionByDistrictId(districtId: String) =
        addressRepository.findRegionByDistrictId(districtId)

    fun listDistrictsByRegionId(regionId: String) =
        addressRepository.listDistrictsByRegionId(regionId)

}