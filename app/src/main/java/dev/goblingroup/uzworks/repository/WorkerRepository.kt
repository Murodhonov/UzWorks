package dev.goblingroup.uzworks.repository

import android.util.Log
import dev.goblingroup.uzworks.database.dao.WorkerDao
import dev.goblingroup.uzworks.database.entity.WorkerEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.networking.WorkerService
import dev.goblingroup.uzworks.utils.ConstValues
import javax.inject.Inject

class WorkerRepository @Inject constructor(
    private val workerService: WorkerService,
    private val workerDao: WorkerDao
) {

    suspend fun getAllWorkers() = workerService.getAll()

    fun addWorkers(workerList: List<WorkerResponse>) {
        val existingWorkers = try {
            workerDao.listAllWorkers()
        } catch (e: Exception) {
            ArrayList()
        }
        Log.d(ConstValues.TAG, "addWorkers: existingWorkers -> $existingWorkers")
        val newWorkers = mutableListOf<WorkerEntity>()


        workerList.forEach { apiWorker ->
            val existingWorker = existingWorkers.find { it.id == apiWorker.id }
            if (existingWorker != null) {
                /**
                 * worker exists on table
                 */
                newWorkers.add(apiWorker.mapToEntity(existingWorker.isSaved))
            } else {
                /**
                 * it's new item, so it should be directly added
                 */
                newWorkers.add(apiWorker.mapToEntity(false))
            }
        }
        workerDao.addWorkers(newWorkers)
    }

    fun saveWorker(workerId: String) = workerDao.saveWorker(workerId)

    fun unSaveWorker(workerId: String): Boolean {
        workerDao.unSaveWorker(workerId)
        return workerDao.countSavedWorkers() != 0
    }

    fun listDatabaseWorkers() = workerDao.listAllWorkers()

    fun listSavedWorkers() = workerDao.listSavedWorkers()

    fun countDatabaseWorkers() = workerDao.countWorkers()

    fun countSavedWorkers() = workerDao.countSavedWorkers()

}