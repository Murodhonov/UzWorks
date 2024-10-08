package com.goblindevs.uzworks.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.goblindevs.uzworks.database.dao.AnnouncementDao
import com.goblindevs.uzworks.database.dao.DistrictDao
import com.goblindevs.uzworks.database.dao.JobCategoryDao
import com.goblindevs.uzworks.database.dao.RegionDao
import com.goblindevs.uzworks.networking.UserService
import com.goblindevs.uzworks.utils.ConstValues.BEARER
import com.goblindevs.uzworks.utils.GenderEnum
import com.goblindevs.uzworks.utils.UserRole
import java.lang.reflect.Type
import javax.inject.Inject

class SecurityRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val type: Type,
    private val announcementDao: AnnouncementDao,
    private val districtDao: DistrictDao,
    private val regionDao: RegionDao,
    private val jobCategoryDao: JobCategoryDao,
    private val userService: UserService
) {

    suspend fun getUser() = userService.getUserById(getToken(), getUserId())

    fun getToken() = "$BEARER ${sharedPreferences.getString("token", null).toString()}"

    fun getUserRoles(): List<String> {
        val userRolesJson = sharedPreferences.getString("user_roles", null)
        return if (userRolesJson?.isEmpty() == true) ArrayList() else jsonToList(userRolesJson.toString())
    }

    fun setUserRoles(userRoleList: List<String>): Boolean {
        return sharedPreferences.edit().putString("user_roles", listToJson(userRoleList)).commit()
    }

    fun setToken(token: String): Boolean {
        return sharedPreferences.edit().putString("token", token).commit()
    }

    fun setUserId(userId: String): Boolean {
        return sharedPreferences.edit().putString("user_id", userId).commit()
    }

    fun getUserId(): String {
        return sharedPreferences.getString("user_id", null).toString()
    }

    fun setExpirationDate(expiration: String): Boolean {
        return sharedPreferences.edit().putString("expiration", expiration).commit()
    }

    fun getExpirationDate(): String {
        return sharedPreferences.getString("expiration", null).toString()
    }

    fun logout(): Boolean {
        announcementDao.clearTable()
        districtDao.clearTable()
        regionDao.clearTable()
        jobCategoryDao.clearTable()
        return sharedPreferences.edit().clear().commit()
    }

    fun isEmployee(): Boolean {
        return getUserRoles().contains(UserRole.EMPLOYEE.roleName)
    }

    fun isEmployer(): Boolean {
        return getUserRoles().contains(UserRole.EMPLOYER.roleName)
    }

    fun setLanguageCode(languageCode: String): Boolean {
        return sharedPreferences.edit().putString("language_code", languageCode).commit()
    }

    fun getLanguageCode(): String? {
        return sharedPreferences.getString("language_code", null)
    }

    fun setGender(gender: Int) = sharedPreferences.edit().putInt("gender", gender).commit()

    fun getGender() = sharedPreferences.getInt("gender", GenderEnum.UNKNOWN.code)

    fun setBirthdate(birthDate: String) =
        sharedPreferences.edit().putString("birthdate", birthDate).commit()

    fun getBirthdate(): String? = sharedPreferences.getString("birthdate", null)

    fun setPhoneNumber(phoneNumber: String) =
        sharedPreferences.edit().putString("phone_number", phoneNumber).commit()

    fun getPhoneNumber() = sharedPreferences.getString("phone_number", null)

    private fun listToJson(list: List<String>) = gson.toJson(list)

    private fun jsonToList(jsonString: String): List<String> {
        return gson.fromJson(jsonString, type)
    }
}