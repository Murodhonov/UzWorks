package dev.goblingroup.uzworks.repository

import android.util.Log
import dev.goblingroup.uzworks.database.dao.WorkerDao
import dev.goblingroup.uzworks.database.entity.WorkerEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.networking.WorkerService
import dev.goblingroup.uzworks.utils.ConstValues
import javax.inject.Inject

class WorkersRepository @Inject constructor(
    private val workerService: WorkerService,
    private val workerDao: WorkerDao
) {

    suspend fun getWorkerById(workerId: String) = workerService.getById(workerId)

    suspend fun getAllWorkers() = workerService.getAll()

    suspend fun countWorkers() = workerService.count()

    suspend fun getWorkerByUserId(userId: String) = workerService.getWorkersByUserId(userId = userId)

    suspend fun addWorker(workerEntity: WorkerEntity) = workerDao.addWorker(workerEntity)

    suspend fun addWorkers(workerList: List<WorkerResponse>) {
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

    suspend fun unSaveWorker(workerId: String): Boolean {
        workerDao.unSaveWorker(workerId)
        return workerDao.countSavedWorkers() != 0
    }

    suspend fun isWorkerSaved(workerId: String) = workerDao.isWorkerSaved(workerId)

    suspend fun getWorker(workerId: String) = workerDao.getWorker(workerId)

    suspend fun listDatabaseWorkers() = workerDao.listAllWorkers()

    fun listSavedWorkers() = workerDao.listSavedWorkers()

    suspend fun countDatabaseWorkers() = workerDao.countWorkers()

    suspend fun countSavedWorkers() = workerDao.countSavedWorkers()

}