package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.database.dao.AnnouncementDao
import dev.goblingroup.uzworks.database.entity.AnnouncementEntity
import dev.goblingroup.uzworks.networking.JobService
import dev.goblingroup.uzworks.networking.WorkerService
import javax.inject.Inject

class AnnouncementRepository @Inject constructor(
    private val jobService: JobService,
    private val workerService: WorkerService,
    private val announcementDao: AnnouncementDao
){

    suspend fun getAllJobs() = jobService.getAllJobs()

    suspend fun getTopJobs() = jobService.getTopJobs()

    suspend fun countJobs() = jobService.countJobs()

    suspend fun getAllWorkers() = workerService.getAll()

    suspend fun getTopWorkers() = workerService.getTopWorkers()

    suspend fun countWorkers() = workerService.countWorkers()

    suspend fun getJobById(jobId: String) = jobService.getJobById(jobId)

    suspend fun getWorkerById(workerId: String) = workerService.getById(workerId)

    fun saveAnnouncement(announcementEntity: AnnouncementEntity) =
        announcementDao.saveAnnouncement(announcementEntity)

    fun unSaveAnnouncement(announcementId: String) = announcementDao.unSave(announcementId)

    fun listSavedAnnouncements() = announcementDao.listSavedAnnouncements()

    fun countSavedAnnouncements() = announcementDao.countSavedAnnouncements()

    fun isAnnouncementSaved(announcementId: String) = announcementDao.isAnnouncementSaved(announcementId)
}