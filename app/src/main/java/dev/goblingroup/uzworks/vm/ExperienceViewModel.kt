package dev.goblingroup.uzworks.vm

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.MotionEvent
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapter.ExperienceAdapter
import dev.goblingroup.uzworks.databinding.AddEditExperienceDialogItemBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogBinding
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

    private val editExperienceLiveData =
        MutableLiveData<ApiStatus<ExperienceEditResponse>>(ApiStatus.Loading())

    lateinit var experienceList: ArrayList<ExperienceResponse>

    init {
        fetchExperiences()
    }

    private fun fetchExperiences() {
        viewModelScope.launch {
            val experienceResponse =
                experienceRepository.getExperiencesByUserId(securityRepository.getUserId())
            if (experienceResponse.isSuccessful) {
                experienceList = ArrayList()
                experienceList.addAll(experienceResponse.body()!!)
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
                Log.e(TAG, "editExperience: ${editExperienceResponse.message()}")
                Log.e(TAG, "editExperience: ${editExperienceResponse.headers()}")
                Log.e(TAG, "editExperience: ${editExperienceResponse.raw()}")
            }
        }
        return editExperienceLiveData
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
                ).show()
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

    fun isFormValid(
        resources: Resources,
        positionEt: TextInputLayout,
        companyNameEt: TextInputLayout,
        startDateEt: TextInputLayout,
        endDateEt: TextInputLayout
    ): Boolean {
        var result = true
        if (positionEt.editText?.text.toString().isEmpty()) {
            result = false
            positionEt.isErrorEnabled = true
            positionEt.error = resources.getString(R.string.position_error)
        }
        if (companyNameEt.editText?.text.toString().isEmpty()) {
            result = false
            companyNameEt.isErrorEnabled = true
            companyNameEt.error = resources.getString(R.string.company_name_error)
        }
        if (startDateEt.editText?.text.toString().isEmpty()) {
            result = false
            startDateEt.isErrorEnabled = true
            startDateEt.error = resources.getString(R.string.start_date_error)
        }
        if (endDateEt.editText?.text.toString().isEmpty()) {
            result = false
            endDateEt.isErrorEnabled = true
            endDateEt.error = resources.getString(R.string.end_date_error)
        }
        return result
    }

}