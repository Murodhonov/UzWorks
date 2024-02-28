package dev.goblingroup.uzworks.repository

import android.util.Log
import dev.goblingroup.uzworks.database.dao.AnnouncementDao
import dev.goblingroup.uzworks.database.entity.AnnouncementEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.networking.JobService
import dev.goblingroup.uzworks.networking.WorkerService
import dev.goblingroup.uzworks.utils.ConstValues
import javax.inject.Inject

class AnnouncementRepository @Inject constructor(
    private val jobService: JobService,
    private val workerService: WorkerService,
    private val announcementDao: AnnouncementDao
){

    suspend fun getAllJobs() = jobService.getAllJobs()

    suspend fun countJobs() = jobService.countJobs()

    suspend fun getAllWorkers() = workerService.getAll()

    suspend fun countWorkers() = workerService.countWorkers()

    suspend fun getJobById(jobId: String) = jobService.getJobById(jobId)

    suspend fun getWorkerById(workerId: String) = workerService.getById(workerId)

    fun addJobs(jobList: List<JobResponse>) {
        val existingJobs = try {
            announcementDao.listAllAnnouncements()
        } catch (e: Exception) {
            ArrayList()
        }
        Log.d(ConstValues.TAG, "addJobs: existingJobs -> $existingJobs")
        val newJobs = mutableListOf<AnnouncementEntity>()

        jobList.forEach { apiJob ->
            val existingJob = existingJobs.find { it.id == apiJob.id }
            if (existingJob != null) {
                newJobs.add(apiJob.mapToEntity(existingJob.isSaved))
            } else {
                newJobs.add(apiJob.mapToEntity(false))
            }
        }

        announcementDao.addAnnouncements(newJobs)
    }

    fun addWorkers(workerList: List<WorkerResponse>) {
        val existingWorkers = try {
            announcementDao.listAllAnnouncements()
        } catch (e: Exception) {
            ArrayList()
        }
        val newWorkers = mutableListOf<AnnouncementEntity>()

        workerList.forEach { apiWorker ->
            val existingWorker = existingWorkers.find { it.id == apiWorker.id }
            if (existingWorker != null) {
                newWorkers.add(apiWorker.mapToEntity(existingWorker.isSaved))
            } else {
                newWorkers.add(apiWorker.mapToEntity(false))
            }
        }

        announcementDao.addAnnouncements(newWorkers)
    }

    fun saveAnnouncement(announcementId: String) = announcementDao.saveAnnouncement(announcementId)

    fun unSaveAnnouncement(announcementId: String): Boolean {
        announcementDao.unSaveAnnouncement(announcementId)
        return announcementDao.countSavedAnnouncements() != 0
    }

    fun listDatabaseAnnouncements() = announcementDao.listAllAnnouncements()

    fun listSavedAnnouncements() = announcementDao.listSavedAnnouncements()

    fun countDatabaseAnnouncements() = announcementDao.countAnnouncements()

    fun countSavedAnnouncements() = announcementDao.countSavedAnnouncements()

    fun isAnnouncementSaved(announcementId: String) = announcementDao.isAnnouncementSaved(announcementId)

}