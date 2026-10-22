package client.clients.versions

object GDPlatformVersions {
    inline val CURRENT
        get() = VER_2_2091

    val VER_2_208 = GDPlatformVersion("2.208", 22u, 47u)
    val VER_2_209 = GDPlatformVersion("2.209", 22u, 51u)
    val VER_2_2091 = GDPlatformVersion("2.2091", 22u, 51u) // Claude probably didn't change the binary version
}