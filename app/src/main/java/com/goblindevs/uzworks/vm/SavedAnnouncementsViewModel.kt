package com.goblindevs.uzworks.vm

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.goblindevs.uzworks.database.entity.AnnouncementEntity
import com.goblindevs.uzworks.repository.AnnouncementRepository
import javax.inject.Inject

@HiltViewModel
class SavedAnnouncementsViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository
) : ViewModel() {

    lateinit var savedAnnouncements: ArrayList<AnnouncementEntity>

    fun getSavedAnnouncements() {
        savedAnnouncements = ArrayList(announcementRepository.listSavedAnnouncements())
    }

    fun countAnnouncements() = announcementRepository.countSavedAnnouncements()

    fun unSave(announcementId: String) = announcementRepository.unSaveAnnouncement(announcementId)

}