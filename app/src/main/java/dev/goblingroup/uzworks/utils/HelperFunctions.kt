package dev.goblingroup.uzworks.utils

import androidx.navigation.NavOptions
import dev.goblingroup.uzworks.R

fun getNavOptions(): NavOptions {
    return NavOptions.Builder()
        .setEnterAnim(R.anim.enter)
        .setExitAnim(R.anim.exit)
        .setPopEnterAnim(R.anim.pop_enter)
        .setPopExitAnim(R.anim.pop_exit)
        .build()
}

