package com.quanticheart.security

import android.app.Activity
import android.util.Log
import okhttp3.*
import java.io.*

val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()

fun download(url: String, callback: (download: Download) -> Unit) {
    val request = Request.Builder().url(url).build()
    okHttpClient.newCall(request).enqueue(
        object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Download(msg = e.message))
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.bytes()?.let {
                    callback(Download(msg = "ok", true, it))
                } ?: run {
                    callback(Download(msg = "File Empty"))
                }
            }
        }
    )
}

fun Activity.downloadAndEncrypt(url: String, callback: (status: Boolean, msg: String) -> Unit) {
    download(url) {
        if (it.status) {
            runOnUiThread {
                var encryptedOutputStream: FileOutputStream? = null
                try {
                    encryptedOutputStream = encryptFile(createFile()).openFileOutput().apply {
                        write(it.file)
                    }
                    callback(true, "ok")
                } catch (e: Exception) {
                    callback(false, e.message ?: "Error")
                } finally {
                    encryptedOutputStream?.close()
                }
            }
        } else {
            callback(false, it.msg ?: "Error w")
        }
    }
}

fun deleteFile(file: File, callback: (status: Boolean) -> Unit) {
    var success = false
    if (file.exists()) {
        success = createFile().delete()
    }
    callback(success)
}

fun openFileInputStream(fileInput: () -> FileInputStream, callback: (String) -> Unit) {
    Log.i("TAG", "Loading file...")
    var fileInputStream: FileInputStream? = null
    try {
        fileInputStream = fileInput()
        val reader = BufferedReader(InputStreamReader(fileInputStream))
        val stringBuilder = StringBuilder()
        reader.forEachLine { line -> stringBuilder.appendLine(line) }
        callback(stringBuilder.toString())
    } catch (e: Exception) {
        callback(e.message ?: "Error occurred when reading file")
    } finally {
        fileInputStream?.close()
    }
}

data class Download(
    val msg: String?,
    val status: Boolean = false,
    val file: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Download

        if (status != other.status) return false
        if (msg != other.msg) return false
        if (!file.contentEquals(other.file)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + msg.hashCode()
        result = 31 * result + file.contentHashCode()
        return result
    }
}