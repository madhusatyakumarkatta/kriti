package com.krithi.lyric.provider.network

import com.krithi.data.model.audio.Song
import com.krithi.lyric.LrcParser
import com.krithi.lyric.provider.ILyricsProvider
import com.krithi.lyric.provider.ILyricsProvider.Companion.CANDIDATE_KEY_NUMBER
import com.krithi.lyric.provider.LyricsResult
import com.krithi.lyric.provider.SearchScorer
import com.krithi.util.SearchKeyUtil.getSearchKeys

abstract class NetworkProvider<T> : ILyricsProvider {

  protected data class CandidateSong<T>(
    val raw: T,
    val title: String?,
    val artist: String?,
    val album: String?,
    val duration: Long?
  )

  final override suspend fun getLyrics(song: Song): LyricsResult {
    val searchKeys = getSearchKeys(song)

    for (key in searchKeys.take(CANDIDATE_KEY_NUMBER)) {
      val candidates = searchCandidates(key.value)
      if (candidates.isEmpty()) {
        continue
      }
      val best = candidates
        .map {
          it to SearchScorer.calculateSongScoreWithKeyKind(
            song,
            it.title,
            it.artist,
            it.album,
            it.duration,
            key.value,
            key.kind
          )
        }
        .filter { it.second.isValid }
        .maxByOrNull { it.second.score }
        ?.first

      if (best != null) {
        val (lyric, tlyric) = searchLyric(best)
        if (!lyric.isNullOrEmpty()) {
          val combined = if (!tlyric.isNullOrEmpty()) {
            lyric.trimEnd() + "\n" + tlyric.trimStart()
          } else {
            lyric
          }
          return LyricsResult(LrcParser.parse(combined), id)
        }
      }
    }

    throw Exception("no lyric found by $id")
  }

  protected abstract suspend fun searchCandidates(searchKey: String): List<CandidateSong<T>>

  protected abstract suspend fun searchLyric(candidateSong: CandidateSong<T>): Pair<String?, String?>
}