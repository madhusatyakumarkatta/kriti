package com.krithi.domain.usecase

import android.net.Uri
import android.content.Context
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class ExtractLyricsUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            // For jaudiotagger to work easily, it usually expects a File path.
            // Since we are dealing with MediaStore URIs, we need the real path.
            val path = getRealPathFromURI(context, uri) ?: return@withContext null
            val file = File(path)
            if (!file.exists()) return@withContext null

            val audioFile = AudioFileIO.read(file)
            val tag = audioFile.tag ?: return@withContext null

            val lyrics = tag.getFirst(FieldKey.LYRICS)
            if (lyrics.isNullOrBlank()) null else lyrics
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        var path: String? = null
        val proj = arrayOf(MediaStore.Audio.Media.DATA)
        context.contentResolver.query(contentUri, proj, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                path = cursor.getString(columnIndex)
            }
        }
        return path
    }
}
