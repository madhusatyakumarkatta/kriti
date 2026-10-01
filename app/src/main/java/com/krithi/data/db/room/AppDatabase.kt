package com.krithi.data.db.room

import android.content.Context
import android.content.Intent
import androidx.room.Database
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.krithi.data.db.DbMigrations.migration3to4
import com.krithi.data.db.DbMigrations.migration4to5
import com.krithi.data.db.DbMigrations.migration5to6
import com.krithi.data.db.DbMigrations.migration6to7
import com.krithi.data.db.room.dao.HistoryDao
import com.krithi.data.db.room.dao.MetaDataCacheDao
import com.krithi.data.db.room.dao.PlayListDao
import com.krithi.data.db.room.dao.PlayQueueDao
import com.krithi.data.db.room.dao.SmbDao
import com.krithi.data.db.room.dao.WebDavDao
import com.krithi.data.db.room.entity.History
import com.krithi.data.db.room.entity.MetaDataCache
import com.krithi.data.db.room.entity.PlayList
import com.krithi.data.db.room.entity.PlayQueue
import com.krithi.data.db.room.entity.Smb
import com.krithi.data.db.room.entity.WebDav
import com.krithi.service.MusicService
import com.krithi.ui.activity.base.BaseMusicActivity.Companion.EXTRA_PLAYLIST
import com.krithi.util.Util.sendLocalBroadcast
import timber.log.Timber

/**
 * Created by remix on 2019/1/12
 */
@Database(
  entities = [
    PlayList::class,
    PlayQueue::class,
    History::class,
    WebDav::class,
    Smb::class,
    MetaDataCache::class
  ], version = AppDatabase.VERSION, exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

  abstract fun playListDao(): PlayListDao

  abstract fun playQueueDao(): PlayQueueDao

  abstract fun historyDao(): HistoryDao

  abstract fun webDavDao(): WebDavDao

  abstract fun smbDao(): SmbDao

  abstract fun metaDataCacheDao(): MetaDataCacheDao

  companion object {

    const val VERSION = 7

    @Volatile
    private var INSTANCE: AppDatabase? = null

    @JvmStatic
    fun getInstance(context: Context): AppDatabase =
      INSTANCE ?: synchronized(this) {
        INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
      }

    private fun buildDatabase(context: Context): AppDatabase {
      val migration1to3 = object : Migration(1, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
        }

      }

      val database =
        Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "aplayer.db")
          .addMigrations(migration1to3)
          .addMigrations(migration3to4)
          .addMigrations(migration4to5)
          .addMigrations(migration5to6)
          .addMigrations(migration6to7)
          .build()
      database.invalidationTracker.addObserver(object :
        InvalidationTracker.Observer(PlayList.TABLE_NAME, PlayQueue.TABLE_NAME) {
        override fun onInvalidated(tables: Set<String>) {
          Timber.v("onInvalidated: $tables")
          if (tables.contains(PlayList.TABLE_NAME)) {
            sendLocalBroadcast(
              Intent(MusicService.PLAYLIST_CHANGE)
                .putExtra(EXTRA_PLAYLIST, PlayList.TABLE_NAME)
            )
          } else if (tables.contains(PlayQueue.TABLE_NAME)) {
            sendLocalBroadcast(
              Intent(MusicService.PLAYLIST_CHANGE)
                .putExtra(EXTRA_PLAYLIST, PlayQueue.TABLE_NAME)
            )
          }
        }
      })
      return database
    }

  }
}
