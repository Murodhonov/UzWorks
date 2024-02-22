package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.database.dao.UserDao
import dev.goblingroup.uzworks.networking.UserService
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val userService: UserService,
    private val userDao: UserDao
) {
}