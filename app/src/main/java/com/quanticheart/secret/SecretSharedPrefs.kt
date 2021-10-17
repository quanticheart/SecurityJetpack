@file:Suppress("unused")

package com.quanticheart.secret

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import com.google.gson.Gson

/**
 * P r e f s_f i l e n a m e
 */
private const val PREFS_FILENAME = "SecurePrefsApp"

/**
 * Preferences
 */
val Context.preferences: CryptSharedPrefs
    get() = CryptSharedPrefs(this)


/**
 * Try catch return null
 *
 * @param T
 * @param R
 * @param block
 * @receiver
 * @return
 */
inline fun <T, R> T.tryCatchReturnNull(block: (T) -> R): R? {
    return try {
        block(this)
    } catch (e: Exception) {
        null
    }
}

/**
 * Prefs
 */
private val Context.prefs: SharedPreferences
    get() = EncryptedSharedPreferences.create(
        this,
        PREFS_FILENAME,
        createMasterKey(this),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

class CryptSharedPrefs(context: Context) {
    val pref = context.prefs
    private val editor: SharedPreferences.Editor = pref.edit()

    /**
     * put String to SharedPreference
     *
     * @param[key] key of preference
     * @param[value] value to input
     */
    fun putString(key: String, value: String) =
        editor.putString(key, value.trim()).apply()

    /**
     * get String value from SharedPreference
     *
     * @param[key] key of preference
     * if key is not presented or some unexpected problem happened, it will be return
     * @return String object
     */
    fun getString(key: String): String? = tryCatchReturnNull { pref.getString(key, null) }

    /**
     * Get string and clear
     *
     * @param key
     * @return
     */
    fun getStringAndClear(key: String): String? {
        val k = tryCatchReturnNull { pref.getString(key, null) }
        k?.let {
            delete(key)
        }
        return k
    }

    /**
     * Put model
     *
     * @param T
     * @param key
     * @param value
     */
    fun <T> putModel(key: String, value: T) =
        editor.putString(key, Gson().toJson(value).trim()).commit()

    /**
     * Get model
     *
     * @param T
     * @param key
     * @return
     */
    inline fun <reified T> getModel(key: String): T {
        val k = tryCatchReturnNull { pref.getString(key, null) }
        return Gson().fromJson(k, T::class.java)
    }

    /**
     * delete key-value from SharedPreference
     * @param[key] key to delete
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun delete(key: String) = editor.remove(key).commit()

    /**
     * clear all of preferences
     */
    fun clear() = editor.clear().commit()

}