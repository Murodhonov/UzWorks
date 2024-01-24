package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.networking.SecuredJobService
import dev.goblingroup.uzworks.utils.NetworkHelper

class SecuredJobViewModelFactory(
    private val securedJobService: SecuredJobService,
    private val networkHelper: NetworkHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SecuredJobViewModel::class.java)) {
            return SecuredJobViewModel(
                securedJobService = securedJobService,
                networkHelper = networkHelper
            ) as T
        }
        return throw Exception("Some error in ${this::class.java.simpleName}")
    }

}