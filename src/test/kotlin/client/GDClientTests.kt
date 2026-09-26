package client

import client.clients.AbstractGDClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag(TestTags.CLIENT)
@OptIn(GDClientApi::class)
class GDClientTests {
    @Test
    fun chkTest() {
        // from: https://boomlings.dev/topics/encryption/chk#python
        //
        // python:
        // >>> generate_chk(key="29481", values=["devexit", "SGVsbG8gZnJvbSB0aGUgR0REb2NzIQ==", 62687277, 69, 0], salt="xPT6iUrtws0J")
        // 'VAtSCFMEWwIOAFEKAA4DCgFRDFdQAAwJVwdbAA0DUVpQCgYEXQ0LCQ=='

        val expected = "VAtSCFMEWwIOAFEKAA4DCgFRDFdQAAwJVwdbAA0DUVpQCgYEXQ0LCQ=="
        val result = AbstractGDClient.createCHK(
            "devexit",
            "SGVsbG8gZnJvbSB0aGUgR0REb2NzIQ==",
            62687277,
            69,
            0,
            key = "29481",
            salt = "xPT6iUrtws0J"
        )

        Assertions.assertEquals(expected, result)

        val resultXorKey = AbstractGDClient.createCHK(
            "devexit",
            "SGVsbG8gZnJvbSB0aGUgR0REb2NzIQ==",
            62687277,
            69,
            0,
            key = XorKey.COMMENT_INTEGRITY
        )

        Assertions.assertEquals(expected, resultXorKey)
    }
}