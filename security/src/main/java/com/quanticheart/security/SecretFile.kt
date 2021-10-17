package com.quanticheart.security

import android.content.Context
import androidx.security.crypto.EncryptedFile
import java.io.File

const val ENCRYPTED_FILE_NAME = "ENCRYPTED_FILE_NAME"

fun createFile(name: String = ENCRYPTED_FILE_NAME) = File("", name)

fun Context.encryptFile(file: File) = EncryptedFile.Builder(
    this,
    file,
    createMasterKey(this),
    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
).build()
