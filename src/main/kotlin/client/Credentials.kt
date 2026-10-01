package client

import kotlinx.serialization.Serializable

@Serializable
@GDClientApi
data class Credentials(
    val username: String,
    val gjp2: GJP2
)
