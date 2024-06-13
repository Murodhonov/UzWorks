package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.repository.AddressRepository
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.utils.extractErrorMessage
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "AddressViewModel"

    private val _regionLiveData =
        MutableLiveData<ApiStatus<List<RegionEntity>>>()
    val regionLiveData get() = _regionLiveData

    private val _districtLiveData =
        MutableLiveData<ApiStatus<List<DistrictEntity>>>()

    init {
        fetchRegions()
    }

    private fun fetchRegions() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                if (addressRepository.listRegions().isEmpty()) {
                    Log.d(TAG, "fetchRegions: room is empty")
                    Log.d(TAG, "fetchRegions: requesting to server")
                    _regionLiveData.postValue(ApiStatus.Loading())
                    val regionResponse = addressRepository.getAllRegions()
                    if (regionResponse.isSuccessful) {
                        Log.d(TAG, "fetchRegions: ${regionResponse.body()} response has came successfully")
                        if (addressRepository.addRegions(regionResponse.body()!!)) {
                            Log.d(TAG, "fetchRegions: regions added to room")
                            _regionLiveData.postValue(ApiStatus.Success(addressRepository.listRegions()))
                        } else {
                            Log.d(TAG, "fetchRegions: regions couldn't be added to room")
                        }
                    } else {
                        _regionLiveData.postValue(ApiStatus.Error(Throwable(regionResponse.message())))
                        Log.e(TAG, "fetchRegions: Failed to fetch regions with code ${regionResponse.code()}")
                        Log.e(TAG, "fetchRegions: ${regionResponse.message()}")
                        Log.e(TAG, "fetchRegions: ${regionResponse.errorBody()}")
                    }
                } else {
                    Log.d(TAG, "fetchRegions: room isn't empty")
                    _regionLiveData.postValue(ApiStatus.Success(addressRepository.listRegions()))
                }
            }
        }
    }

    fun districtsByRegionId(regionId: String): LiveData<ApiStatus<List<DistrictEntity>>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                Log.d(TAG, "districtsByRegionId: searching districts by regionId: $regionId")
                if (addressRepository.listDistrictsByRegionId(regionId).isNotEmpty()) {
                    Log.d(TAG, "districtsByRegionId: found in room")
                    _districtLiveData.postValue(
                        ApiStatus.Success(
                            addressRepository.listDistrictsByRegionId(
                                regionId
                            )
                        )
                    )
                } else {
                    Log.d(TAG, "districtsByRegionId: not found in room fetching from server")
                    _districtLiveData.postValue(ApiStatus.Loading())
                    val response = addressRepository.getDistrictsByRegionId(regionId)
                    if (response.isSuccessful) {
                        Log.d(TAG, "districtsByRegionId: ${response.body()!!} response has came successfully")
                        if (addressRepository.addDistricts(response.body()!!, regionId)) {
                            Log.d(TAG, "districtsByRegionId: districts added to room")
                            _districtLiveData.postValue(
                                ApiStatus.Success(
                                    addressRepository.listDistrictsByRegionId(
                                        regionId
                                    )
                                )
                            )
                        } else {
                            Log.d(TAG, "districtsByRegionId: districts couldn't be added to room")
                        }
                    } else {
                        _districtLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                        Log.e(
                            TAG,
                            "districtsByRegionId: Failed to fetch districts with code ${response.errorBody()?.extractErrorMessage()}",
                        )
                        Log.e(TAG, "districtsByRegionId: ${response.code()}")
                    }
                }
            }
        }
        return _districtLiveData
    }

    fun listDistrictsByRegionId(regionId: String) =
        addressRepository.listDistrictsByRegionId(regionId)

    fun districtByRegionName(regionName: String): LiveData<ApiStatus<List<DistrictEntity>>> {
        viewModelScope.launch {
            if (addressRepository.districtsByRegionName(regionName).isNotEmpty()) {
                _districtLiveData.postValue(
                    ApiStatus.Success(
                        addressRepository.districtsByRegionName(
                            regionName
                        )
                    )
                )
            } else {
                if (networkHelper.isNetworkConnected()) {
                    _districtLiveData.postValue(ApiStatus.Loading())
                    val regionResponse = addressRepository.getAllRegions()
                    if (regionResponse.isSuccessful) {
                        addressRepository.addRegions(regionResponse.body()!!)
                    }
                    if (addressRepository.districtsByRegionName(regionName).isNotEmpty()) {
                        _districtLiveData.postValue(
                            ApiStatus.Success(
                                addressRepository.districtsByRegionName(
                                    regionName
                                )
                            )
                        )
                    } else {
                        val regionId =
                            addressRepository.listRegions().find { it.name == regionName }?.id
                        val districtsByRegionIdResponse =
                            addressRepository.getDistrictsByRegionId(regionId.toString())

                        if (districtsByRegionIdResponse.isSuccessful) {
                            if (addressRepository.addDistricts(
                                    districtsByRegionIdResponse.body()!!,
                                    regionId.toString()
                                )
                            ) {
                                _districtLiveData.postValue(
                                    ApiStatus.Success(
                                        addressRepository.listDistrictsByRegionId(
                                            regionId.toString()
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        return _districtLiveData
    }

    fun districtsByRegionName(regionName: String) = addressRepository.districtsByRegionName(regionName)

    fun listRegions() = addressRepository.listRegions()

    fun listDistricts() = addressRepository.listDistricts()

}