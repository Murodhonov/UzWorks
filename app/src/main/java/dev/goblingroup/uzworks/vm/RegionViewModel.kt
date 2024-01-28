package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.repository.RegionRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegionViewModel @Inject constructor(
    private val regionRepository: RegionRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _regionStateFlow = MutableStateFlow<ApiStatus<List<RegionEntity>>>(ApiStatus.Loading())
    val regionStateFlow get() = _regionStateFlow

    init {
        fetchRegions()
    }

    private fun fetchRegions() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                if (regionRepository.listRegions().isNotEmpty()) {
                    _regionStateFlow.emit(ApiStatus.Success(regionRepository.listRegions()))
                } else {
                    regionRepository.getAllRegions()
                        .catch {
                            _regionStateFlow.emit(ApiStatus.Error(it))
                        }
                        .flatMapConcat { regionList ->
                            val emptyRegionList = ArrayList<RegionEntity>()
                            regionList.forEach { regionResponse ->
                                emptyRegionList.add(regionResponse.mapToEntity())
                            }
                            regionRepository.addRegions(emptyRegionList)
                        }
                        .collect {
                            _regionStateFlow.emit(ApiStatus.Success(regionRepository.listRegions()))
                        }
                }
            } else {
                if (regionRepository.listRegions().isNotEmpty()) {
                    _regionStateFlow.emit(ApiStatus.Success(regionRepository.listRegions()))
                } else {
                    _regionStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
                }
            }
        }
    }

    fun findRegionById(regionId: String) = regionRepository.findRegionById(regionId)

    fun listRegions() = regionRepository.listRegions()

}