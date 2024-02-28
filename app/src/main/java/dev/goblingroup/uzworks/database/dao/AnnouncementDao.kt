package dev.goblingroup.uzworks.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.goblingroup.uzworks.database.entity.AnnouncementEntity

@Dao
interface AnnouncementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAnnouncements(announcementList: List<AnnouncementEntity>)

    @Query("UPDATE announcement_table SET is_saved = 1 WHERE announcement_id = :announcementId")
    fun saveAnnouncement(announcementId: String)

    @Query("UPDATE announcement_table SET is_saved = 0 WHERE announcement_id = :announcementId")
    fun unSaveAnnouncement(announcementId: String)

    @Query("SELECT is_saved FROM announcement_table WHERE announcement_id = :announcementId")
    fun isAnnouncementSaved(announcementId: String): Boolean

    @Query("SELECT * FROM announcement_table")
    fun listAllAnnouncements(): List<AnnouncementEntity>

    @Query("SELECT * FROM announcement_table WHERE is_saved = 1")
    fun listSavedAnnouncements(): List<AnnouncementEntity>

    @Query("SELECT COUNT(*) FROM announcement_table")
    fun countAnnouncements(): Int

    @Query("SELECT COUNT(*) FROM announcement_table WHERE is_saved = 1")
    fun countSavedAnnouncements(): Int

    @Query("DELETE FROM announcement_table")
    fun deleteAnnouncements()

}