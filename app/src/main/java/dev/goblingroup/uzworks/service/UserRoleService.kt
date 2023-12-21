package dev.goblingroup.uzworks.service

import android.content.Context

interface UserRoleService {

    fun getUserRoleList(context: Context): List<String>

    fun addUserRole(userRole: String, context: Context): Boolean

    fun deleteUserRole(userRole: String, context: Context): Boolean

    fun setUserRoles(userRoleList: List<String>, context: Context): Boolean

}