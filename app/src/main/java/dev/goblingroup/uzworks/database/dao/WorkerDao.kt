package dev.goblingroup.uzworks.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.goblingroup.uzworks.database.entity.WorkerEntity

@Dao
interface WorkerDao {

    @Insert(
        entity = WorkerEntity::class,
        onConflict = OnConflictStrategy.REPLACE
    )
    fun addWorkers(workerList: List<WorkerEntity>)

    @Query("UPDATE workers_table SET is_saved = 1 WHERE worker_id = :workerId")
    fun saveWorker(workerId: String)

    @Query("UPDATE workers_table SET is_saved = 0 WHERE worker_id = :workerId")
    fun unSaveWorker(workerId: String)

    @Query("SELECT is_saved FROM workers_table WHERE worker_id = :workerId")
    fun isWorkerSaved(workerId: String): Boolean

    @Query("SELECT * FROM workers_table WHERE worker_id = :workerId")
    fun getWorker(workerId: String): WorkerEntity

    @Query("SELECT * FROM workers_table")
    fun listAllWorkers(): List<WorkerEntity>

    @Query("SELECT * FROM workers_table WHERE is_saved = 1")
    fun listSavedWorkers(): List<WorkerEntity>

    @Query("SELECT COUNT(*) FROM workers_table")
    fun countWorkers(): Int

    @Query("SELECT COUNT(*) FROM workers_table WHERE is_saved = 1")
    fun countSavedWorkers(): Int

    @Query("DELETE FROM workers_table")
    fun deleteWorkers()

}