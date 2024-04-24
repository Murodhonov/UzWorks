package dev.goblingroup.uzworks.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.goblingroup.uzworks.database.entity.AnnouncementEntity

@Dao
interface AnnouncementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAnnouncement(announcementEntity: AnnouncementEntity)

    @Delete
    fun unSaveAnnouncement(announcementEntity: AnnouncementEntity)

    @Query("DELETE FROM announcement_table WHERE announcement_id = :announcementId")
    fun unSave(announcementId: String)

    @Query("SELECT * FROM announcement_table")
    fun listSavedAnnouncements(): List<AnnouncementEntity>

    @Query("SELECT COUNT(*) FROM announcement_table")
    fun countSavedAnnouncements(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM announcement_table WHERE announcement_id = :announcementId)")
    fun isAnnouncementSaved(announcementId: String): Boolean

    @Query("DELETE FROM announcement_table")
    fun clearTable()
}