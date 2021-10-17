package com.quanticheart.secret

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val file by lazy { file() }

    private val encKey = "ENCRYPT_KEY"
    private val fileUrl =
        ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        securePrefsSave.setOnClickListener { encryptPrefsString(securePrefsInput.text.toString()) }

        securePrefsLoad.setOnClickListener {
            result.text = decryptPrefsString() ?: getString(R.string.no_saved_value)
        }

        fileDownload.setOnClickListener { downloadAndEncryptFile() }

        fileDelete.setOnClickListener {
            deleteFile(file) { success ->
                result.text =
                    getString(if (success) R.string.file_deleted else R.string.file_delete_error)
            }
        }

        fileLoad.setOnClickListener {
            openFileInputStream({
                file.inputStream()
            }, {
                result.text
            })
        }

        fileDecrypt.setOnClickListener {
            openFileInputStream({
                encryptFile(file).openFileInput()
            }, {
                result.text
            })
        }
    }

    private fun encryptPrefsString(value: String) {
        preferences.putString(encKey, value)
        result.text = getString(R.string.value_encrypted)
    }

    private fun decryptPrefsString(): String? = preferences.getString(encKey)

    private fun downloadAndEncryptFile() {
        if (file.exists()) {
            Log.i("TAG", "Encrypted file already exists!")
            result.text = getString(R.string.file_exists)
        } else {
            Log.e("TAG", "Encrypted file does not exist exists! Downloading...")
            downloadAndEncrypt(fileUrl) { _, msg ->
                result.text = msg
                Log.e("TAG", "Error occurred! :: $msg")
            }
        }
    }
}