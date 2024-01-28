package dev.goblingroup.uzworks.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import dev.goblingroup.uzworks.database.dao.UserDao
import java.lang.reflect.Type
import javax.inject.Inject

class SecurityRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val type: Type,
    private val userDao: UserDao
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
        val tokenDeleted = deleteToken()
        val userRolesDeleted = deleteUserRoles()
        return tokenDeleted && userRolesDeleted
    }

    private fun listToJson(list: List<String>): String {
        return gson.toJson(list)
    }

    private fun jsonToList(jsonString: String): List<String> {
        return gson.fromJson(jsonString, type)
    }

    private fun deleteToken(): Boolean {
        return sharedPreferences.edit().putString("token", null).commit()
    }

    private fun deleteUserRoles(): Boolean {
        return sharedPreferences.edit().putString("user_roles", listToJson(emptyList())).commit()
    }
}