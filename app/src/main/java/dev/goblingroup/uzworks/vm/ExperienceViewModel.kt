package dev.goblingroup.uzworks.vm

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.ExperienceCreateRequest
import dev.goblingroup.uzworks.models.request.ExperienceEditRequest
import dev.goblingroup.uzworks.models.response.ExperienceCreateResponse
import dev.goblingroup.uzworks.models.response.ExperienceEditResponse
import dev.goblingroup.uzworks.models.response.ExperienceResponse
import dev.goblingroup.uzworks.repository.ExperienceRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.DateEnum
import dev.goblingroup.uzworks.utils.clear
import dev.goblingroup.uzworks.utils.extractDateValue
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExperienceViewModel @Inject constructor(
    private val experienceRepository: ExperienceRepository,
    private val securityRepository: SecurityRepository
) : ViewModel() {

    private val _experienceLiveData = MutableLiveData<ApiStatus<List<ExperienceResponse>>>(ApiStatus.Loading())
    val experienceLiveData get() = _experienceLiveData

    private val createExperienceLiveData = MutableLiveData<ApiStatus<ExperienceCreateResponse>>(
        ApiStatus.Loading()
    )

    private val editExperienceLiveData = MutableLiveData<ApiStatus<ExperienceEditResponse>>(
        ApiStatus.Loading()
    )

    private val _positionLiveData = MutableLiveData("")
    val positionLiveData get() = _positionLiveData

    private val _companyNameLiveData = MutableLiveData("")
    val companyNameLiveData get() = _companyNameLiveData

    private val _startDateLiveData = MutableLiveData("")
    val startDateLiveData get() = _startDateLiveData

    private val _endDateLiveData = MutableLiveData("")
    val endDateLiveData get() = _endDateLiveData

    var adding = false
    var editing = false



    init {
        fetchExperiences()
    }

    private fun fetchExperiences() {
        viewModelScope.launch {
                val experienceResponse =
                    experienceRepository.getExperiencesByUserId(securityRepository.getUserId())
                if (experienceResponse.isSuccessful) {
                    _experienceLiveData.postValue(ApiStatus.Success(experienceResponse.body()))
                } else {
                    _experienceLiveData.postValue(ApiStatus.Error(Throwable(experienceResponse.message())))
                }
        }
    }

    fun createExperience(experienceCreateRequest: ExperienceCreateRequest): LiveData<ApiStatus<ExperienceCreateResponse>> {
        viewModelScope.launch {
            Log.d(TAG, "createExperience: creating $experienceCreateRequest")
            val experienceCreateResponse =
                experienceRepository.createExperience(experienceCreateRequest)
            if (experienceCreateResponse.isSuccessful) {
                createExperienceLiveData.postValue(ApiStatus.Success(experienceCreateResponse.body()))
            } else {
                createExperienceLiveData.postValue(
                    ApiStatus.Error(
                        Throwable(
                            experienceCreateResponse.message()
                        )
                    )
                )
                Log.e(TAG, "createExperience: ${experienceCreateResponse.errorBody()}")
                Log.e(TAG, "createExperience: ${experienceCreateResponse.code()}")
            }
        }
        return createExperienceLiveData
    }

    fun editExperience(experienceEditRequest: ExperienceEditRequest): LiveData<ApiStatus<ExperienceEditResponse>> {
        viewModelScope.launch {
                val editExperienceResponse = experienceRepository.editExperience(experienceEditRequest)
                if (editExperienceResponse.isSuccessful) {
                    editExperienceLiveData.postValue(ApiStatus.Success(editExperienceResponse.body()))
                } else {
                    editExperienceLiveData.postValue(
                        ApiStatus.Error(
                            Throwable(
                                editExperienceResponse.message()
                            )
                        )
                    )
                    Log.e(TAG, "editExperience: ${editExperienceResponse.errorBody()}")
                    Log.e(TAG, "editExperience: ${editExperienceResponse.code()}")
                }
        }
        return editExperienceLiveData
    }

    fun setPosition(position: String) {
        _positionLiveData.value = position
    }

    fun setCompanyName(companyName: String) {
        _companyNameLiveData.value = companyName
    }

    fun setStartDate(startDate: String) {
        _startDateLiveData.value = startDate
    }

    fun setEndDate(endDate: String) {
        _endDateLiveData.value = endDate
    }

    @SuppressLint("ClickableViewAccessibility")
    fun controlExperienceInput(
        context: Context,
        positionEt: TextInputLayout,
        companyNameEt: TextInputLayout,
        startDateEt: TextInputLayout,
        endDateEt: TextInputLayout
    ) {
        startDateEt.clear()
        endDateEt.clear()

        positionEt.editText?.doAfterTextChanged {
            if (it.toString().isNotEmpty() && positionEt.isErrorEnabled) {
                positionEt.isErrorEnabled = false
            }
        }

        companyNameEt.editText?.doAfterTextChanged {
            if (it.toString().isNotEmpty() && companyNameEt.isErrorEnabled) {
                companyNameEt.isErrorEnabled = false
            }
        }

        startDateEt.editText?.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                DatePickerDialog(
                    context,
                    { p0, year, month, dayOfMonth ->
                        val selectedDate = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }

                        val formatter = SimpleDateFormat(
                            "dd.MM.yyyy", Locale.getDefault()
                        )
                        startDateEt.isErrorEnabled = false
                        startDateEt.editText?.setText(formatter.format(selectedDate.time))
                    },
                    startDateEt.editText?.text.toString().extractDateValue(DateEnum.YEAR.dateLabel),
                    startDateEt.editText?.text.toString()
                        .extractDateValue(DateEnum.MONTH.dateLabel),
                    startDateEt.editText?.text.toString().extractDateValue(DateEnum.DATE.dateLabel),
                )
            }
            true
        }

        endDateEt.editText?.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                DatePickerDialog(
                    context,
                    { p0, year, month, dayOfMonth ->
                        val selectedDate = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }
                        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        endDateEt.isErrorEnabled = false
                        endDateEt.editText?.setText(formatter.format(selectedDate.time))
                    },
                    endDateEt.editText?.text.toString().extractDateValue(DateEnum.YEAR.dateLabel),
                    endDateEt.editText?.text.toString()
                        .extractDateValue(DateEnum.MONTH.dateLabel),
                    endDateEt.editText?.text.toString().extractDateValue(DateEnum.DATE.dateLabel),
                ).show()
            }
            true
        }
    }

}