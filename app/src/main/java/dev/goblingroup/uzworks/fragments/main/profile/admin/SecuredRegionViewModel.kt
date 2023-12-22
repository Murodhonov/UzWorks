package dev.goblingroup.uzworks.fragments.main.profile.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.models.request.RegionEditRequest
import dev.goblingroup.uzworks.networking.SecuredRegionService
import dev.goblingroup.uzworks.repository.secured.SecuredRegionRepository
import dev.goblingroup.uzworks.resource.secured_resource.region.CreateRegionResource
import dev.goblingroup.uzworks.resource.secured_resource.region.DeleteRegionResource
import dev.goblingroup.uzworks.resource.secured_resource.region.EditRegionResource
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SecuredRegionViewModel(
    securedRegionService: SecuredRegionService,
    regionName: String,
    regionId: String,
    regionEditRequest: RegionEditRequest,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "SecuredDistrictViewMode"

    private var securedRegionRepository =
        SecuredRegionRepository(
            securedRegionService = securedRegionService,
            regionName = regionName,
            regionId = regionId,
            regionEditRequest = regionEditRequest
        )

    private val createStateFlow =
        MutableStateFlow<CreateRegionResource<Unit>>(CreateRegionResource.CreateLoading())

    private val deleteStateFlow =
        MutableStateFlow<DeleteRegionResource<Unit>>(DeleteRegionResource.DeleteLoading())

    private val editStateFlow =
        MutableStateFlow<EditRegionResource<Unit>>(EditRegionResource.EditLoading())

    fun createDistrict(): StateFlow<CreateRegionResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedRegionRepository.createRegion()
                    .catch {
                        createStateFlow.emit(CreateRegionResource.CreateError(it))
                    }
                    .collect {
                        createStateFlow.emit(CreateRegionResource.CreateSuccess())
                    }
            } else {
                createStateFlow.emit(CreateRegionResource.CreateError(Throwable(NO_INTERNET)))
            }
        }
        return createStateFlow
    }

    fun deleteDistrict(): StateFlow<DeleteRegionResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedRegionRepository.deleteRegion()
                    .catch {
                        deleteStateFlow.emit(DeleteRegionResource.DeleteError(it))
                    }
                    .collect {
                        deleteStateFlow.emit(DeleteRegionResource.DeleteSuccess())
                    }
            } else {
                deleteStateFlow.emit(DeleteRegionResource.DeleteError(Throwable(NO_INTERNET)))
            }
        }
        return deleteStateFlow
    }

    fun editDistrict(): StateFlow<EditRegionResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedRegionRepository.editRegion()
                    .catch {
                        editStateFlow.emit(EditRegionResource.EditError(it))
                    }
                    .collect {
                        editStateFlow.emit(EditRegionResource.EditSuccess())
                    }
            } else {
                editStateFlow.emit(EditRegionResource.EditError(Throwable(NO_INTERNET)))
            }
        }
        return editStateFlow
    }

}