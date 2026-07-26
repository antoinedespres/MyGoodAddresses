package com.despreschen.mygoodaddresses.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Photos of restaurants, in the app's own files directory.
 *
 * The previous version asked for WRITE_EXTERNAL_STORAGE and then never wrote
 * anything; the captured bitmap was shown and discarded. Keeping photos inside
 * app storage needs no storage permission at all, and they are removed with the
 * app.
 */
class PhotoStorage(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
) {

    private val directory: File
        get() = File(context.filesDir, DIRECTORY).apply { mkdirs() }

    /**
     * Creates an empty file for the camera to write into, and the content URI
     * to hand it. The camera app cannot be given a raw `file://` path.
     */
    fun newPhotoTarget(): Pair<File, Uri> {
        val file = File(directory, "${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.photos", file)
        return file to uri
    }

    /** Removes a photo whose restaurant is being deleted, or that was replaced. */
    suspend fun delete(path: String?) = withContext(ioDispatcher) {
        path?.let { runCatching { File(it).delete() } }
        Unit
    }

    /** Discards a capture target the user abandoned, so it does not accumulate. */
    suspend fun discardIfEmpty(file: File) = withContext(ioDispatcher) {
        if (file.exists() && file.length() == 0L) file.delete()
        Unit
    }

    private companion object {
        const val DIRECTORY = "photos"
    }
}
