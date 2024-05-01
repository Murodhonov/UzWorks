package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.database.dao.AnnouncementDao
import dev.goblingroup.uzworks.database.entity.AnnouncementEntity
import dev.goblingroup.uzworks.models.request.JobCreateRequest
import dev.goblingroup.uzworks.models.request.JobEditRequest
import dev.goblingroup.uzworks.models.request.WorkerCreateRequest
import dev.goblingroup.uzworks.models.request.WorkerEditRequest
import dev.goblingroup.uzworks.networking.JobService
import dev.goblingroup.uzworks.networking.WorkerService
import javax.inject.Inject

class AnnouncementRepository @Inject constructor(
    private val jobService: JobService,
    private val workerService: WorkerService,
    private val securityRepository: SecurityRepository,
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

    suspend fun createJob(jobCreateRequest: JobCreateRequest) =
        jobService.createJob(securityRepository.getToken(), jobCreateRequest)

    suspend fun editJob(jobEditRequest: JobEditRequest) =
        jobService.editJob(securityRepository.getToken(), jobEditRequest)

    suspend fun deleteJob(jobId: String) =
        jobService.deleteJob(securityRepository.getToken(), jobId)

    suspend fun jobsByUserId(userId: String) =
        jobService.getJobsByUserId(securityRepository.getToken(), userId)

    suspend fun createWorker(workerCreateRequest: WorkerCreateRequest) = workerService.createWorker(securityRepository.getToken(), workerCreateRequest)

    suspend fun editWorker(workerEditRequest: WorkerEditRequest) = workerService.editWorker(securityRepository.getToken(), workerEditRequest)

    suspend fun deleteWorker(workerId: String) = workerService.deleteWorker(securityRepository.getToken(), workerId)

    suspend fun workersByUserId(userId: String) = workerService.getWorkersByUserId(securityRepository.getToken(), userId)
}