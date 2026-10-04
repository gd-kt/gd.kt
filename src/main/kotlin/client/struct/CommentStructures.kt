package client.struct

import client.GDClientApi
import client.clients.AbstractGDClient
import editor.objects.ObjectParser
import editor.rawstring.Id.Companion.id
import editor.rawstring.RawStringFactory
import editor.rawstring.property.BoolProperty
import editor.rawstring.property.IntProperty
import editor.rawstring.property.UIntProperty
import editor.rawstring.property.UnencodedStringProperty
import editor.rawstring.property.getOrThrow
import java.awt.Color

@GDClientApi
abstract class CommentStructure(override val client: AbstractGDClient, val userInfo: CommentUserInfo) : ServerStructure {
    companion object {
        const val SEPARATOR: Char = '~'
    }

    override val rawStringFactory: RawStringFactory = RawStringFactory.create(this)

    val comment = UnencodedStringProperty(2.id, defaultValue = null)
    val likes = IntProperty(4.id, defaultValue = null)
    val dislikes = IntProperty(5.id, defaultValue = null)
    val messageID = UIntProperty(6.id, defaultValue = null)
    val authorAccountID = UIntProperty(8.id, defaultValue = null)
    val age = UnencodedStringProperty(9.id, defaultValue = null)
    val timestamp = UIntProperty(15.id, defaultValue = null)
}

@GDClientApi
class CommentHistoryStructure(override val client: AbstractGDClient, userInfo: CommentUserInfo) : CommentStructure(client, userInfo) {
    companion object : ServerStructureCompanion<CommentHistoryStructure> {
        override val separator: Char = SEPARATOR

        override fun parse(rawString: String, client: AbstractGDClient): CommentHistoryStructure {
            val splitted = rawString.split(':')

            val userStructure = ObjectParser.parse(splitted[1], CommentUserInfo(client), CommentUserInfo.separator)
            val commentStructure = ObjectParser.parse(splitted[0], CommentHistoryStructure(client, userStructure), separator)

            return commentStructure
        }
    }

    override val rawStringFactory: RawStringFactory = RawStringFactory.create(this)

    val levelID = IntProperty(1.id, defaultValue = null)
    val userID = UIntProperty(3.id, defaultValue = null)
    val percent = UIntProperty(10.id, defaultValue = null)
    val modBadge = UIntProperty(11.id, defaultValue = null)
    val rawModeratorChatColor = UnencodedStringProperty(12.id, defaultValue = null)

    val isUserModerator
        get() = this.rawModeratorChatColor.value != null

    var moderatorChatColor: Color?
        get() {
            if (this.rawModeratorChatColor.value == null)
                return null

            val splitted = this.rawModeratorChatColor.getOrThrow().split(',')

            val r = splitted[0].toInt()
            val g = splitted[1].toInt()
            val b = splitted[2].toInt()

            return Color(r, g, b)
        }
        set(value) {
            if (value == null) {
                this.rawModeratorChatColor.value = null
            } else {
                this.rawModeratorChatColor.value = "${value.red},${value.green},${value.blue}"
            }
        }
}

@GDClientApi
class LevelCommentStructure(override val client: AbstractGDClient, userInfo: CommentUserInfo) : CommentStructure(client, userInfo) {
    companion object : ServerStructureCompanion<LevelCommentStructure> {
        override val separator: Char = SEPARATOR

        override fun parse(rawString: String, client: AbstractGDClient): LevelCommentStructure {
            val splitted = rawString.split(':')

            val userStructure = ObjectParser.parse(splitted[1], CommentUserInfo(client), CommentUserInfo.separator)
            val commentStructure = ObjectParser.parse(splitted[0], LevelCommentStructure(client, userStructure), separator)

            return commentStructure
        }
    }

    override val rawStringFactory: RawStringFactory = RawStringFactory.create(this)

    val isSpam = BoolProperty(7.id, defaultValue = null)
}