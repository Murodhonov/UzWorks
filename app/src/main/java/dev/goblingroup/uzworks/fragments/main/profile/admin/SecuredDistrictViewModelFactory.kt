package dev.goblingroup.uzworks.fragments.main.profile.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.models.request.DistrictEditRequest
import dev.goblingroup.uzworks.models.request.DistrictRequest
import dev.goblingroup.uzworks.networking.SecuredDistrictService
import dev.goblingroup.uzworks.utils.NetworkHelper

class SecuredDistrictViewModelFactory(
    private val securedDistrictService: SecuredDistrictService,
    var districtRequest: DistrictRequest,
    private val districtId: String,
    var districtEditRequest: DistrictEditRequest,
    private val networkHelper: NetworkHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SecuredDistrictViewModel::class.java)) {
            return SecuredDistrictViewModel(
                securedDistrictService = securedDistrictService,
                districtRequest = districtRequest,
                districtId = districtId,
                districtEditRequest = districtEditRequest,
                networkHelper = networkHelper
            ) as T
        }
        return throw Exception("Some error in ${this::class.java.simpleName}")
    }

}