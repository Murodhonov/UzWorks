package dev.goblingroup.uzworks.vm

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.databinding.ConfirmDeleteBinding
import dev.goblingroup.uzworks.databinding.MyAnnouncementBottomBinding
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.repository.AnnouncementRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyAnnouncementsViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val securityRepository: SecurityRepository
) : ViewModel() {

    private val _jobLiveData = MutableLiveData<ApiStatus<List<JobResponse>>>(ApiStatus.Loading())
    val jobLiveData get() = _jobLiveData

    private val _workerLiveData = MutableLiveData<ApiStatus<List<WorkerResponse>>>(ApiStatus.Loading())
    val workerLiveData get() = _workerLiveData

    private lateinit var bottomSheet: BottomSheetDialog
    private lateinit var bottomBinding: MyAnnouncementBottomBinding
    private lateinit var confirmDeleteBottomSheet: BottomSheetDialog
    private lateinit var confirmDeleteBinding: ConfirmDeleteBinding

    init {
        if (securityRepository.isEmployer()) {
            loadJobs()
        } else if (securityRepository.isEmployee()) {
            loadWorkers()
        }
    }

    private fun loadJobs() {
        viewModelScope.launch {
            val jobsByUserId = announcementRepository.jobsByUserId(securityRepository.getUserId())
            if (jobsByUserId.isSuccessful) {
                _jobLiveData.postValue(ApiStatus.Success(jobsByUserId.body()))
            } else {
                Log.e(TAG, "loadJobs: ${jobsByUserId.code()}")
                Log.e(TAG, "loadJobs: ${jobsByUserId.errorBody()}")
                Log.e(TAG, "loadJobs: ${jobsByUserId.message()}")
                _jobLiveData.postValue(ApiStatus.Error(Throwable(jobsByUserId.message())))
            }
        }
    }

    private fun loadWorkers() {
        viewModelScope.launch {
                val workersByUserId =
                    announcementRepository.workersByUserId(securityRepository.getUserId())
                if (workersByUserId.isSuccessful) {
                    _workerLiveData.postValue(ApiStatus.Success(workersByUserId.body()))
                } else {
                    Log.e(TAG, "loadWorkers: ${workersByUserId.code()}")
                    Log.e(TAG, "loadWorkers: ${workersByUserId.errorBody()}")
                    Log.e(TAG, "loadWorkers: ${workersByUserId.message()}")
                    _workerLiveData.postValue(ApiStatus.Error(Throwable(workersByUserId.message())))
                }
        }
    }

    fun isEmployee() = securityRepository.isEmployee()

    fun isEmployer() = securityRepository.isEmployer()

    fun showBottom(
        context: Context,
        onMoreClick: () -> Unit,
        onEditClick: () -> Unit,
        onDeleteClick: () -> Unit
    ) {
        try {
            if (!bottomSheet.isShowing) {
                bottomSheet.show()
            }
        } catch (e: Exception) {
            bottomSheet = BottomSheetDialog(context)
            bottomBinding = MyAnnouncementBottomBinding.inflate(LayoutInflater.from(context))
            bottomBinding.apply {
                bottomSheet.setContentView(root)
                seeMoreBtn.setOnClickListener {
                    onMoreClick.invoke()
                    bottomSheet.dismiss()
                }

                editBtn.setOnClickListener {
                    onEditClick.invoke()
                    bottomSheet.dismiss()
                }
                deleteBtn.setOnClickListener {
                    confirmDelete(context) {
                        onDeleteClick.invoke()
                        bottomSheet.dismiss()
                    }
                }
            }
            if (!bottomSheet.isShowing) {
                bottomSheet.show()
            }
        }
    }

    private fun confirmDelete(
        context: Context,
        onDeleteClick: () -> Unit
    ) {
        try {
            if (!confirmDeleteBottomSheet.isShowing) {
                confirmDeleteBottomSheet.show()
            }
        } catch (e: Exception) {
            confirmDeleteBottomSheet = BottomSheetDialog(context)
            confirmDeleteBinding = ConfirmDeleteBinding.inflate(LayoutInflater.from(context))
            confirmDeleteBinding.apply {
                confirmDeleteBottomSheet.setContentView(root)
                yesBtn.setOnClickListener {
                    onDeleteClick.invoke()
                    confirmDeleteBottomSheet.dismiss()
                }
                cancelBtn.setOnClickListener {
                    confirmDeleteBottomSheet.dismiss()
                    bottomSheet.show()
                }
            }
            if (!confirmDeleteBottomSheet.isShowing) {
                confirmDeleteBottomSheet.show()
            }
        }
    }

}