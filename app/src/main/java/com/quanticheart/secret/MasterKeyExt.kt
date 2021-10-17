package com.quanticheart.secret

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.MasterKey

/**
 * Create a e s256g c m key gen parameter spec
 *
 * @param keyAlias
 * @return
 */
private fun createAES256GCMKeyGenParameterSpec(
    keyAlias: String
): KeyGenParameterSpec {
    val builder = KeyGenParameterSpec.Builder(
        keyAlias,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setKeySize(256)
    return builder.build()
}

/**
 * Create master key
 *
 * @param context
 * @param keyAlias
 */
internal fun createMasterKey(
    context: Context,
    keyAlias: String = MasterKey.DEFAULT_MASTER_KEY_ALIAS
) =
    MasterKey.Builder(context)
        .setKeyGenParameterSpec(createAES256GCMKeyGenParameterSpec(keyAlias))
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
