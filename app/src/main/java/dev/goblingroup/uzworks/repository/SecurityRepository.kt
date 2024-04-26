package dev.goblingroup.uzworks.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import dev.goblingroup.uzworks.database.dao.AnnouncementDao
import dev.goblingroup.uzworks.networking.SecuredUserService
import dev.goblingroup.uzworks.utils.UserRole
import java.lang.reflect.Type
import javax.inject.Inject

class SecurityRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val type: Type,
    private val announcementDao: AnnouncementDao,
    private val securedUserService: SecuredUserService
) {

    suspend fun getUser() = securedUserService.getUserById(getUserId())

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

    fun setGender(gender: Int): Boolean {
        return sharedPreferences.edit().putInt("gender", gender).commit()
    }

    fun getGender(): String? {
        return sharedPreferences.getString("gender", null)
    }

    private fun listToJson(list: List<String>): String {
        return gson.toJson(list)
    }

    private fun jsonToList(jsonString: String): List<String> {
        return gson.fromJson(jsonString, type)
    }
}