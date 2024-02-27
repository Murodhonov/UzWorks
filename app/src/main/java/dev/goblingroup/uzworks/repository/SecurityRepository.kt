package dev.goblingroup.uzworks.repository

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import dev.goblingroup.uzworks.database.dao.JobDao
import dev.goblingroup.uzworks.database.dao.WorkerDao
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.UserRole
import java.lang.Exception
import java.lang.reflect.Type
import javax.inject.Inject

class SecurityRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val type: Type,
    private val jobDao: JobDao,
    private val workerDao: WorkerDao
) {

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

    fun deleteUser(): Boolean {
        jobDao.deleteJobs()
        workerDao.deleteWorkers()
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

    private fun listToJson(list: List<String>): String {
        return gson.toJson(list)
    }

    private fun jsonToList(jsonString: String): List<String> {
        return gson.fromJson(jsonString, type)
    }

}