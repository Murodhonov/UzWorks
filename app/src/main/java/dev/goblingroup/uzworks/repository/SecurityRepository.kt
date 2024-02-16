package dev.goblingroup.uzworks.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import dev.goblingroup.uzworks.database.dao.JobDao
import dev.goblingroup.uzworks.database.dao.UserDao
import dev.goblingroup.uzworks.database.dao.WorkerDao
import dev.goblingroup.uzworks.utils.UserRole
import java.lang.reflect.Type
import javax.inject.Inject

class SecurityRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val type: Type,
    private val userDao: UserDao,
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

    fun deleteUser(): Boolean {
        userDao.deleteUser()
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

    private fun listToJson(list: List<String>): String {
        return gson.toJson(list)
    }

    private fun jsonToList(jsonString: String): List<String> {
        return gson.fromJson(jsonString, type)
    }

}