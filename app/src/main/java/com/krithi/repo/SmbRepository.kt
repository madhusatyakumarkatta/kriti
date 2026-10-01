package com.krithi.repo

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import com.krithi.data.db.room.dao.SmbDao
import com.krithi.data.db.room.entity.Smb
import javax.inject.Inject

interface SmbRepository {
  fun allSmb(): Flow<List<Smb>>

  suspend fun insertOrReplace(smb: Smb): Long

  suspend fun delete(smb: Smb): Int
}

class SmbRepoImpl @Inject constructor(
  @param:ApplicationContext
  private val context: Context,
  private val smbDao: SmbDao
) : SmbRepository {

  override fun allSmb(): Flow<List<Smb>> = smbDao.selectAll()

  override suspend fun insertOrReplace(smb: Smb) = smbDao.insertOrReplace(smb)

  override suspend fun delete(smb: Smb) = smbDao.delete(smb)

}
