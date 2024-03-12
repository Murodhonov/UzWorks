package dev.goblingroup.uzworks.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddJobViewModel @Inject constructor() : ViewModel() {

    private val _benefit = MutableLiveData("")
    val benefit: LiveData<String> get() = _benefit

    private val _categoryId = MutableLiveData("")
    val categoryId: LiveData<String> get() = _categoryId

    private val _latitude = MutableLiveData(0.0)

    val latitude: LiveData<Double> get() = _latitude

    private val _longitude = MutableLiveData(0.0)

    val longitude: LiveData<Double> get() = _longitude

    private val _deadline = MutableLiveData("")
    val deadline: LiveData<String> get() = _deadline

    private val _districtId = MutableLiveData("")
    val districtId: LiveData<String> get() = _districtId

    private val _gender = MutableLiveData("")
    val gender: LiveData<String> get() = _gender

    private val _instagramLink = MutableLiveData("")
    val instagramLink: LiveData<String> get() = _instagramLink

    private val _maxAge = MutableLiveData(0)
    val maxAge: LiveData<Int> get() = _maxAge

    private val _minAge = MutableLiveData(0)
    val minAge: LiveData<Int> get() = _minAge

    private val _phoneNumber = MutableLiveData("")
    val phoneNumber: LiveData<String> get() = _phoneNumber

    private val _requirement = MutableLiveData("")
    val requirement: LiveData<String> get() = _requirement

    private val _salary = MutableLiveData(0)
    val salary: LiveData<Int> get() = _salary

    private val _telegramLink = MutableLiveData("")
    val telegramLink: LiveData<String> get() = _telegramLink

    private val _tgUserName = MutableLiveData("")
    val tgUserName: LiveData<String> get() = _tgUserName

    private val _title = MutableLiveData("")
    val title: LiveData<String> get() = _title

    private val _workingSchedule = MutableLiveData("")
    val workingSchedule: LiveData<String> get() = _workingSchedule

    private val _workingTime = MutableLiveData("")
    val workingTime: LiveData<String> get() = _workingTime

    fun setBenefit(value: String) {
        _benefit.value = value
    }

    fun setCategoryId(value: String) {
        _categoryId.value = value
    }


    fun setLatitude(value: Double) {
        _latitude.value = value
    }

    fun setLongitude(value: Double) {
        _longitude.value = value
    }

    fun setDeadline(value: String) {
        _deadline.value = value
    }

    fun setDistrictId(value: String) {
        _districtId.value = value
    }

    fun setGender(value: String) {
        _gender.value = value
    }

    fun setInstagramLink(value: String) {
        _instagramLink.value = value
    }

    fun setMaxAge(value: Int) {
        _maxAge.value = value
    }

    fun setMinAge(value: Int) {
        _minAge.value = value
    }

    fun setPhoneNumber(value: String) {
        _phoneNumber.value = value
    }

    fun setRequirement(value: String) {
        _requirement.value = value
    }

    fun setSalary(value: Int) {
        _salary.value = value
    }

    fun setTelegramLink(value: String) {
        _telegramLink.value = value
    }

    fun setTgUsername(value: String) {
        _tgUserName.value = value
    }

    fun setTitle(value: String) {
        _title.value = value
    }

    fun setWorkingSchedule(value: String) {
        _workingSchedule.value = value
    }

    fun setWorkingTime(value: String) {
        _workingTime.value = value
    }

}