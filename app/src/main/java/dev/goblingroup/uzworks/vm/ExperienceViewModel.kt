package dev.goblingroup.uzworks.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.repository.secured.SecuredWorkerRepository
import dev.goblingroup.uzworks.utils.NetworkHelper
import javax.inject.Inject

@HiltViewModel
class ExperienceViewModel @Inject constructor(
    private val securedWorkerRepository: SecuredWorkerRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _experienceLiveData = MutableLiveData<ApiStatus<Experience>>

}