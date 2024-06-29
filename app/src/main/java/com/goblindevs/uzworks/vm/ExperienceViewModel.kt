package com.goblindevs.uzworks.vm

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.models.request.ExperienceCreateRequest
import com.goblindevs.uzworks.models.request.ExperienceEditRequest
import com.goblindevs.uzworks.models.response.ExperienceCreateResponse
import com.goblindevs.uzworks.models.response.ExperienceEditResponse
import com.goblindevs.uzworks.models.response.ExperienceResponse
import com.goblindevs.uzworks.repository.ExperienceRepository
import com.goblindevs.uzworks.repository.SecurityRepository
import com.goblindevs.uzworks.utils.ConstValues.TAG
import com.goblindevs.uzworks.utils.DateEnum
import com.goblindevs.uzworks.utils.NetworkHelper
import com.goblindevs.uzworks.utils.extractDateValue
import com.goblindevs.uzworks.utils.extractErrorMessage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExperienceViewModel @Inject constructor(
    private val experienceRepository: ExperienceRepository,
    private val securityRepository: SecurityRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _experienceLiveData = MutableLiveData<ApiStatus<List<ExperienceResponse>>>(ApiStatus.Loading())
    val experienceLiveData get() = _experienceLiveData

    private val createExperienceLiveData = MutableLiveData<ApiStatus<ExperienceCreateResponse>>(
        ApiStatus.Loading()
    )

    private val editExperienceLiveData =
        MutableLiveData<ApiStatus<ExperienceEditResponse>>(ApiStatus.Loading())

    private val deleteExperienceLiveData = MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    lateinit var experienceList: ArrayList<ExperienceResponse>

    init {
        fetchExperiences()
    }

    fun fetchExperiences() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                _experienceLiveData.postValue(ApiStatus.Loading())
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
                Log.e(TAG, "createExperience: ${experienceCreateResponse.errorBody()?.extractErrorMessage()}")
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
                Log.e(TAG, "editExperience: ${editExperienceResponse.errorBody()?.extractErrorMessage()}")
                Log.e(TAG, "editExperience: ${editExperienceResponse.code()}")
            }
        }
        return editExperienceLiveData
    }

    fun deleteExperience(experienceId: String): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = experienceRepository.deleteExperience(experienceId)
                if (response.isSuccessful) {
                    deleteExperienceLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    deleteExperienceLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            }
        }
        return deleteExperienceLiveData
    }

    @SuppressLint("ClickableViewAccessibility")
    fun controlExperienceInput(
        context: Context,
        positionEt: TextInputLayout,
        companyNameEt: TextInputLayout,
        startDateEt: TextInputLayout,
        endDateEt: TextInputLayout
    ) {
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

        startDateEt.editText?.setOnFocusChangeListener { view, hasFocus ->
            Log.d(TAG, "controlExperienceInput: $hasFocus")
            if (hasFocus) {
                val datePickerDialog = DatePickerDialog(
                    context,
                    R.style.DatePickerDialogTheme,
                    { p0, year, month, dayOfMonth ->
                        val selectedDate = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }

                        val minimumCalendar =
                            Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }

                        if (selectedDate.before(minimumCalendar)) {
                            val formatter = SimpleDateFormat(
                                "dd.MM.yyyy", Locale.getDefault()
                            )
                            startDateEt.isErrorEnabled = false
                            startDateEt.editText?.setText(formatter.format(selectedDate.time))
                        } else {
                            Toast.makeText(
                                context,
                                context.resources.getString(R.string.invalid_start_date),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    startDateEt.editText?.text.toString().extractDateValue(DateEnum.YEAR.dateLabel),
                    startDateEt.editText?.text.toString().extractDateValue(DateEnum.MONTH.dateLabel) - 1,
                    startDateEt.editText?.text.toString().extractDateValue(DateEnum.DATE.dateLabel),
                )

                datePickerDialog.show()

                datePickerDialog.setOnDismissListener {
                    startDateEt.clearFocus()
                }
            }
        }

        endDateEt.editText?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                val datePickerDialog = DatePickerDialog(
                    context,
                    R.style.DatePickerDialogTheme,
                    { p0, year, month, dayOfMonth ->
                        val selectedDate = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }

                        if (selectedDate.before(Calendar.getInstance())) {
                            val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                            endDateEt.isErrorEnabled = false
                            endDateEt.editText?.setText(formatter.format(selectedDate.time))
                        } else {
                            Toast.makeText(
                                context,
                                context.resources.getString(R.string.invalid_end_date),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    endDateEt.editText?.text.toString().extractDateValue(DateEnum.YEAR.dateLabel),
                    endDateEt.editText?.text.toString()
                        .extractDateValue(DateEnum.MONTH.dateLabel) - 1,
                    endDateEt.editText?.text.toString().extractDateValue(DateEnum.DATE.dateLabel),
                )

                datePickerDialog.show()

                datePickerDialog.setOnDismissListener {
                    endDateEt.clearFocus()
                }
            }
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
        if (startDateEt.editText?.text.toString()
                .isNotEmpty() && endDateEt.editText?.text.toString().isNotEmpty()
        ) {
            val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val startDate = Calendar.getInstance().apply {
                time =
                    formatter.parse(startDateEt.editText?.text.toString())!!
            }
            val endDate = Calendar.getInstance().apply {
                time =
                    formatter.parse(endDateEt.editText?.text.toString())!!
            }
            if (startDate.after(endDate)) {
                result = false
                startDateEt.isErrorEnabled = true
                startDateEt.error = resources.getString(R.string.start_date_end_date_error)
            }
        }
        return result
    }

}