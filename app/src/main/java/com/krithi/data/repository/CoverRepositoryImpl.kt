package com.krithi.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.krithi.domain.repository.CoverRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoverRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CoverRepository {
    private val prefs: SharedPreferences = context.getSharedPreferences("covers_prefs", Context.MODE_PRIVATE)

    override fun getCustomCoverUri(songId: Long): String? {
        return prefs.getString(songId.toString(), null)
    }

    override fun observeCustomCoverUri(songId: Long): Flow<String?> = callbackFlow {
        val key = songId.toString()
        // Send initial value
        trySend(prefs.getString(key, null))

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, changedKey ->
            if (changedKey == key) {
                trySend(sharedPreferences.getString(key, null))
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    override fun setCustomCoverUri(songId: Long, uri: String?) {
        if (uri == null) {
            prefs.edit().remove(songId.toString()).apply()
        } else {
            prefs.edit().putString(songId.toString(), uri).apply()
        }
    }
}
