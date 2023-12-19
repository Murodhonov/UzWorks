package dev.goblingroup.uzworks.singleton

import com.google.gson.Gson

class MyGson {
    val gson: Gson?
        get() = Companion.gson

    companion object {
        private val myGson = MyGson()
        private var gson: Gson? = null
        val instance: MyGson
            get() {
                if (gson == null) {
                    gson = Gson()
                }
                return myGson
            }
    }
}
