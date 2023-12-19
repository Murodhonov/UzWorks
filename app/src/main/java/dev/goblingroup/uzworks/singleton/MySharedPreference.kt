package dev.goblingroup.uzworks.singleton

import android.content.Context
import android.content.SharedPreferences

class MySharedPreference private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_data", Context.MODE_PRIVATE)

    fun setToken(token: String): Boolean {
        return sharedPreferences.edit().putString("token", token).commit()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun setUserId(userId: String): Boolean {
        return sharedPreferences.edit().putString("user_id", userId).commit()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString("user_id", null)
    }

    companion object {
        private var mySharedPreferences: MySharedPreference? = null

        fun getInstance(context: Context): MySharedPreference {
            if (mySharedPreferences == null) {
                mySharedPreferences = MySharedPreference(context)
            }
            return mySharedPreferences as MySharedPreference
        }
    }

}