package com.krithi.lyric.provider.network

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.krithi.data.model.misc.LyricOrder
import com.krithi.request.kugou.KuGouClient
import com.krithi.request.kugou.KuGouSong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KuGouProvider @Inject constructor(
  @param:ApplicationContext
  private val context: Context,
  private val kuGouClient: KuGouClient
) : NetworkProvider<KuGouSong>() {

  override val id = LyricOrder.Kugou.toString()

  override val displayName = context.getString(LyricOrder.Kugou.stringRes)

  override suspend fun searchCandidates(searchKey: String): List<CandidateSong<KuGouSong>> {
    val list = kuGouClient.searchSongList(searchKey)
    return list.map { s ->
      CandidateSong(
        raw = s,
        title = s.title,
        artist = s.artists.joinToString(", "),
        album = s.album,
        duration = s.durationMs
      )
    }
  }

  override suspend fun searchLyric(candidateSong: CandidateSong<KuGouSong>): Pair<String?, String?> {
    return kuGouClient.getLyrics(candidateSong.raw)
  }
}
