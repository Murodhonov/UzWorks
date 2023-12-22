package dev.goblingroup.uzworks.fragments.main.profile.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.models.request.DistrictEditRequest
import dev.goblingroup.uzworks.models.request.DistrictRequest
import dev.goblingroup.uzworks.networking.SecuredDistrictService
import dev.goblingroup.uzworks.repository.secured.SecuredDistrictRepository
import dev.goblingroup.uzworks.resource.secured_resource.district.CreateDistrictResource
import dev.goblingroup.uzworks.resource.secured_resource.district.DeleteDistrictResource
import dev.goblingroup.uzworks.resource.secured_resource.district.EditDistrictResource
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SecuredDistrictViewModel(
    securedDistrictService: SecuredDistrictService,
    districtRequest: DistrictRequest,
    districtId: String,
    districtEditRequest: DistrictEditRequest,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "SecuredDistrictViewMode"

    private var securedDistrictRepository =
        SecuredDistrictRepository(
            securedDistrictService = securedDistrictService,
            districtRequest = districtRequest,
            districtId = districtId,
            districtEditRequest = districtEditRequest
        )

    private val createStateFlow =
        MutableStateFlow<CreateDistrictResource<Unit>>(CreateDistrictResource.CreateLoading())

    private val deleteStateFlow =
        MutableStateFlow<DeleteDistrictResource<Unit>>(DeleteDistrictResource.DeleteLoading())

    private val editStateFlow =
        MutableStateFlow<EditDistrictResource<Unit>>(EditDistrictResource.EditLoading())

    fun createDistrict(): StateFlow<CreateDistrictResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedDistrictRepository.createDistrict()
                    .catch {
                        createStateFlow.emit(CreateDistrictResource.CreateError(it))
                    }
                    .collect {
                        createStateFlow.emit(CreateDistrictResource.CreateSuccess())
                    }
            } else {
                createStateFlow.emit(CreateDistrictResource.CreateError(Throwable(NO_INTERNET)))
            }
        }
        return createStateFlow
    }

    fun deleteDistrict(): StateFlow<DeleteDistrictResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedDistrictRepository.deleteDistrict()
                    .catch {
                        deleteStateFlow.emit(DeleteDistrictResource.DeleteError(it))
                    }
                    .collect {
                        deleteStateFlow.emit(DeleteDistrictResource.DeleteSuccess())
                    }
            } else {
                deleteStateFlow.emit(DeleteDistrictResource.DeleteError(Throwable(NO_INTERNET)))
            }
        }
        return deleteStateFlow
    }

    fun editDistrict(): StateFlow<EditDistrictResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedDistrictRepository.editDistrict()
                    .catch {
                        editStateFlow.emit(EditDistrictResource.EditError(it))
                    }
                    .collect {
                        editStateFlow.emit(EditDistrictResource.EditSuccess())
                    }
            } else {
                editStateFlow.emit(EditDistrictResource.EditError(Throwable(NO_INTERNET)))
            }
        }
        return editStateFlow
    }

}