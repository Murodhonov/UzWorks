package dev.goblingroup.uzworks.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.repository.RegionRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegionViewModel @Inject constructor(
    private val regionRepository: RegionRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _regionLiveData = MutableLiveData<ApiStatus<List<RegionEntity>>>(ApiStatus.Loading())
    val regionLiveData get() = _regionLiveData

    init {
        fetchRegions()
    }

    private fun fetchRegions() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                if (regionRepository.listRegions().isNotEmpty()) {
                    _regionLiveData.postValue(ApiStatus.Success(regionRepository.listRegions()))
                } else {
                    val response = regionRepository.getAllRegions()
                    if (response.isSuccessful) {
                        val emptyRegionList = ArrayList<RegionEntity>()
                        response.body()?.forEach { regionResponse ->
                            emptyRegionList.add(regionResponse.mapToEntity())
                        }
                        regionRepository.addRegions(emptyRegionList)
                        _regionLiveData.postValue(ApiStatus.Success(regionRepository.listRegions()))
                    } else {
                        _regionLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                    }
                }
            } else {
                if (regionRepository.listRegions().isNotEmpty()) {
                    _regionLiveData.postValue(ApiStatus.Success(regionRepository.listRegions()))
                } else {
                    _regionLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
                }
            }
        }
    }

    suspend fun findRegionById(regionId: String) = regionRepository.findRegionById(regionId)

    suspend fun listRegions() = regionRepository.listRegions()

}