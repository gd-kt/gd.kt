package client

import kotlinx.serialization.Serializable
import org.apache.commons.codec.digest.DigestUtils

/**
 * A hashed password used to log in to a geometry dash account.
 * It uses `sha1` to hash the password.
 *
 * This was added in 2.2
 */
@JvmInline
@Serializable
@GDClientApi
// The constructor is private to not pass an unencrypted password
// in the class's constructor
value class GJP2 private constructor(val encryptedPassword: String) {
    companion object {
        const val SALT = "mI29fmAnxgTs"

        fun encryptPassword(password: String): String =
            DigestUtils.sha1Hex(password + SALT)

        fun create(password: String): GJP2 =
            GJP2(encryptPassword(password))

        fun fromGJP2(gjp2: String): GJP2 =
            GJP2(gjp2)
    }
}