package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.models.response.DistrictResponse
import dev.goblingroup.uzworks.repository.DistrictRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DistrictViewModel @Inject constructor(
    private val districtRepository: DistrictRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _districtStateFlow =
        MutableStateFlow<ApiStatus<List<DistrictEntity>>>(ApiStatus.Loading())
    val districtStateFlow get() = _districtStateFlow

    private val districtByIdFlow =
        MutableStateFlow<ApiStatus<DistrictResponse>>(ApiStatus.Loading())

    private val districtByRegionIdFlow =
        MutableStateFlow<ApiStatus<List<DistrictResponse>>>(ApiStatus.Loading())

    init {
        fetchDistricts()
    }

    private fun fetchDistricts() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                if (districtRepository.listDistricts().isNotEmpty()) {
                    _districtStateFlow.emit(ApiStatus.Success(districtRepository.listDistricts()))
                } else {
                    districtRepository.getAllDistricts()
                        .catch {
                            _districtStateFlow.emit(ApiStatus.Error(it))
                        }
                        .flatMapConcat { districtResponseList ->
                            val emptyDistrictList = ArrayList<DistrictEntity>()
                            districtResponseList.forEach { districtResponse ->
                                emptyDistrictList.add(districtResponse.mapToEntity())
                            }
                            districtRepository.addDistricts(emptyDistrictList)
                        }
                        .collect {
                            _districtStateFlow.emit(ApiStatus.Success(districtRepository.listDistricts()))
                        }
                }
            } else {
                if (districtRepository.listDistricts().isNotEmpty()) {
                    _districtStateFlow.emit(ApiStatus.Success(districtRepository.listDistricts()))
                } else {
                    _districtStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
                }
            }
        }
    }

    fun getDistrictById(districtId: String): StateFlow<ApiStatus<DistrictResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                districtRepository.getDistrictById(districtId)
                    .catch {
                        districtByIdFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        districtByIdFlow.emit(ApiStatus.Success(it))
                    }
            } else {
                districtByIdFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return districtByIdFlow
    }

    fun getDistrictByRegionId(regionId: String): StateFlow<ApiStatus<List<DistrictResponse>>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                districtRepository.getDistrictByRegionId(regionId)
                    .catch {
                        districtByRegionIdFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        districtByRegionIdFlow.emit(ApiStatus.Success(it))
                    }
            } else {
                districtByRegionIdFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return districtByRegionIdFlow
    }

    fun findDistrict(districtId: String) = districtRepository.findDistrict(districtId)

    fun listDistrictsByRegionId(regionId: String) =
        districtRepository.listDistrictsByRegionId(regionId)

    fun getRegionByDistrictId(districtId: String) =
        districtRepository.getRegionByDistrictId(districtId)

}