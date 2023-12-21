package dev.goblingroup.uzworks.service

import android.content.Context
import com.google.gson.reflect.TypeToken
import dev.goblingroup.uzworks.singleton.MyGson
import dev.goblingroup.uzworks.singleton.MySharedPreference


class UserRoleImpl : UserRoleService {

    override fun getUserRoleList(context: Context): List<String> {
        val contactList: List<String>
        val type = object : TypeToken<List<String?>?>() {}.type
        val userRolesJson: String? = MySharedPreference.getInstance(context).getUserRoles()
        contactList = if (userRolesJson!!.isEmpty()) {
            ArrayList()
        } else {
            MyGson.instance.gson!!.fromJson(userRolesJson, type)
        }
        return contactList
    }

    override fun addUserRole(userRole: String, context: Context): Boolean {
        return MySharedPreference.getInstance(context)
            .setUserRoles(changedJson(userRole, context, false))
    }

    override fun deleteUserRole(userRole: String, context: Context): Boolean {
        return MySharedPreference.getInstance(context)
            .setUserRoles(changedJson(userRole, context, true))
    }

    override fun setUserRoles(userRoleList: List<String>, context: Context): Boolean {
        return MySharedPreference.getInstance(context)
            .setUserRoles(MyGson.instance.gson!!.toJson(userRoleList))
    }

    private fun changedJson(userRole: String, context: Context, isDelete: Boolean): String {
        val userRolesList = ArrayList(getUserRoleList(context))
        if (isDelete) userRolesList.remove(userRole)
        else userRolesList.add(userRole)
        return MyGson.instance.gson?.toJson(userRolesList).toString()
    }
}