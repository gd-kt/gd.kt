package client.clients.versions

/**
 * A class representing a Geometry Dash version
 * @property strVersion the version formatted as a string, example: `2.209`
 * @property gameVersion the **game version** used in requests, example `22`
 * @property binaryVersion the **binary version** used in requests, example `51`
 */
data class GDPlatformVersion(val strVersion: String, val gameVersion: UInt, val binaryVersion: UInt)
