package client.struct

import client.AbstractGDClient
import client.GDClientApi
import client.enums.Gamemode
import editor.objects.ObjectParser
import editor.rawstring.RawStringFactory
import editor.rawstring.Id.Companion.id
import editor.rawstring.property.BoolProperty
import editor.rawstring.property.EnumProperty
import editor.rawstring.property.GdEnum
import editor.rawstring.property.IntProperty
import editor.rawstring.property.UIntProperty
import editor.rawstring.property.UnencodedStringProperty
import editor.rawstring.serializing.Serializer
import exceptions.GdDotKtException

enum class MessageState(override val value: Int) : GdEnum {
    ALL(0),
    FRIENDS_ONLY(1),
    NONE(2),
}

enum class FriendsState(override val value: Int) : GdEnum {
    ALL(0),
    NONE(1),
}

enum class Special(override val value: Int) : GdEnum {
    GLOW_DISABLED(0),
    ENABLED(2),
}

enum class FriendState(override val value: Int) : GdEnum {
    UNFRIENDED(0),
    FRIENDED(1),
    FRIEND_REQUEST_SENT(3),
    FRIEND_REQUEST_RECEIVED(4),
}

enum class ModLevel(override val value: Int) : GdEnum {
    NONE(0),
    NORMAL_MOD(1),
    ELDER_MOD(2),
    LEADERBOARD_MOD(3),
}

enum class CommentHistoryState(override val value: Int) : GdEnum {
    ALL(0),
    FRIENDS_ONLY(1),
    NONE(2),
}

private const val BREAKDOWN_SEPARATOR: Char = ','

@GDClientApi
class ClassicLevelsBreakdown {
    var auto: Int
    var easy: Int
    var normal: Int
    var hard: Int
    var harder: Int
    var insane: Int

    var daily: Int
    var gauntlet: Int

    constructor(rawBreakdown: String) {
        val splittedBreakdown = rawBreakdown.split(BREAKDOWN_SEPARATOR)
        if (splittedBreakdown.size != 8)
            throw GdDotKtException("Size of collection $rawBreakdown is of size ${splittedBreakdown.size} but it was expected to by 8 elements long")

        this.auto = splittedBreakdown[0].toInt()
        this.easy = splittedBreakdown[1].toInt()
        this.normal = splittedBreakdown[2].toInt()
        this.hard = splittedBreakdown[3].toInt()
        this.harder = splittedBreakdown[4].toInt()
        this.insane = splittedBreakdown[5].toInt()

        this.daily = splittedBreakdown[6].toInt()
        this.gauntlet = splittedBreakdown[7].toInt()
    }
}

@GDClientApi
class PlatformerLevelsBreakdown {
    var auto: Int
    var easy: Int
    var normal: Int
    var hard: Int
    var harder: Int
    var insane: Int

    var theMap: Int

    constructor(rawBreakdown: String) {
        val splittedBreakdown = rawBreakdown.split(BREAKDOWN_SEPARATOR)
        if (splittedBreakdown.size != 7)
            throw GdDotKtException("Size of collection $rawBreakdown is of size ${splittedBreakdown.size} but it was expected to by 7 elements long")

        this.auto = splittedBreakdown[0].toInt()
        this.easy = splittedBreakdown[1].toInt()
        this.normal = splittedBreakdown[2].toInt()
        this.hard = splittedBreakdown[3].toInt()
        this.harder = splittedBreakdown[4].toInt()
        this.insane = splittedBreakdown[5].toInt()

        this.theMap = splittedBreakdown[6].toInt()
    }
}

@GDClientApi
class DemonLevelsBreakdown {
    var easyDemon: Int
    var mediumDemon: Int
    var hardDemon: Int
    var insaneDemon: Int
    var extremeDemon: Int

    var easyDemonPlatformer: Int
    var mediumDemonPlatformer: Int
    var hardDemonPlatformer: Int
    var insaneDemonPlatformer: Int
    var extremeDemonPlatformer: Int

    var weekly: Int
    var gauntlet: Int

    constructor(rawBreakdown: String) {
        val splittedBreakdown = rawBreakdown.split(BREAKDOWN_SEPARATOR)
        if (splittedBreakdown.size != 12)
            throw GdDotKtException("Size of collection $rawBreakdown is of size ${splittedBreakdown.size} but it was expected to by 12 elements long")

        this.easyDemon = splittedBreakdown[0].toInt()
        this.mediumDemon = splittedBreakdown[1].toInt()
        this.hardDemon = splittedBreakdown[2].toInt()
        this.insaneDemon = splittedBreakdown[3].toInt()
        this.extremeDemon = splittedBreakdown[4].toInt()

        this.easyDemonPlatformer = splittedBreakdown[5].toInt()
        this.mediumDemonPlatformer = splittedBreakdown[6].toInt()
        this.hardDemonPlatformer = splittedBreakdown[7].toInt()
        this.insaneDemonPlatformer = splittedBreakdown[8].toInt()
        this.extremeDemonPlatformer = splittedBreakdown[9].toInt()

        this.weekly = splittedBreakdown[10].toInt()
        this.gauntlet = splittedBreakdown[11].toInt()
    }
}

@GDClientApi
open class UserStructure(override val client: AbstractGDClient) : ServerStructure {
    companion object : ServerStructureCompanion<UserStructure> {
        override val separator: Char = ':'

        override fun parse(rawString: String, client: AbstractGDClient): UserStructure =
            ObjectParser.parse(rawString, UserStructure(client), separator)
    }

    override val rawStringFactory: RawStringFactory = RawStringFactory.createDynamic(this)

    val userName = UnencodedStringProperty(1.id, defaultValue = null)
    val userID = UIntProperty(2.id, defaultValue = null)
    val stars = UIntProperty(3.id, defaultValue = null)
    val demonCount = UIntProperty(4.id, defaultValue = null)
    val creatorPoints = UIntProperty(8.id, defaultValue = null)
    val color = UIntProperty(10.id, defaultValue = null)
    val color2 = UIntProperty(11.id, defaultValue = null)
    val shipID = UIntProperty(12.id, defaultValue = null)
    val secretCoins = UIntProperty(13.id, defaultValue = null)
    val accountID = UIntProperty(16.id, defaultValue = null)
    val userCoins = UIntProperty(17.id, defaultValue = null)
}

@GDClientApi
class UserScore(client: AbstractGDClient) : UserStructure(client) {
    companion object : ServerStructureCompanion<UserScore> {
        override val separator: Char = ':'

        override fun parse(rawString: String, client: AbstractGDClient): UserScore =
            ObjectParser.parse(rawString, UserScore(client), separator)
    }

    val ranking = IntProperty(6.id, defaultValue = null)
    /**
     * The player's account ID, or the device ID for unregistered players.
     * Used for highlighting unregistered players on the leaderboards.
     * For registered players, the game instead checks if the accountID field matches the current account.
     *
     * Only has a value when viewing yourself on a leaderboard
     * */
    val accountHighlight = UnencodedStringProperty(7.id, defaultValue = null)
    val displayIconType = EnumProperty(14.id, Serializer.enum(Gamemode.entries))
    val displayIconID = UIntProperty(9.id, defaultValue = null)
    val special = EnumProperty(15.id, Serializer.enum(Special.entries))
}

@GDClientApi
class UserInfo(client: AbstractGDClient) : UserStructure(client) {
    companion object : ServerStructureCompanion<UserInfo> {
        override val separator: Char = ':'

        override fun parse(rawString: String, client: AbstractGDClient): UserInfo =
            ObjectParser.parse(rawString, UserInfo(client), separator)
    }

    val ranking = IntProperty(6.id, defaultValue = null)
    val iconType = EnumProperty(14.id, Serializer.enum(Gamemode.entries))
    val iconID = UIntProperty(9.id, defaultValue = null)
    val special = EnumProperty(15.id, Serializer.enum(Special.entries))
    val messageState = EnumProperty(18.id, Serializer.enum(MessageState.entries))
    val friendsState = EnumProperty(19.id, Serializer.enum(FriendsState.entries))
    val youTube = UnencodedStringProperty(20.id, defaultValue = null)
    val cubeIconID = UIntProperty(21.id, defaultValue = null)
    val shipIconID = UIntProperty(22.id, defaultValue = null)
    val ballIconID = UIntProperty(23.id, defaultValue = null)
    val ufoIconID = UIntProperty(24.id, defaultValue = null)
    val waveIconID = UIntProperty(25.id, defaultValue = null)
    val robotIconID = UIntProperty(26.id, defaultValue = null)
    val spiderIconID = UIntProperty(43.id, defaultValue = null)
    val swingIconID = UIntProperty(53.id, defaultValue = null)
    val jetpackIconID = UIntProperty(54.id, defaultValue = null)
    val hasGlow = BoolProperty(28.id, defaultValue = null)
    val glowColor = UIntProperty(51.id, defaultValue = null)
    val isRegistered = BoolProperty(29.id, defaultValue = null)
    val globalRank = UIntProperty(30.id, defaultValue = null)
    val iconExplosionID = UIntProperty(48.id, defaultValue = null)
    val friendState = EnumProperty(31.id, Serializer.enum(FriendState.entries))
    /** Only has a value when the player sent you a friend request */
    val friendRequestID = UIntProperty(32.id, defaultValue = null)
    /** Only has a value when the player sent you a friend request */
    val friendRequestMessage = UnencodedStringProperty(35.id, defaultValue = null)
    /** Only has a value when the player sent you a friend request */
    val friendRequestAge = UnencodedStringProperty(37.id, defaultValue = null)
    /** Only has a value when logged in and when viewing your own profile */
    val messages = UIntProperty(38.id, defaultValue = null)
    val friendRequests = UIntProperty(39.id, defaultValue = null)
    val newFriends = UIntProperty(40.id, defaultValue = null)
    val twitter = UnencodedStringProperty(44.id, defaultValue = null)
    val twitch = UnencodedStringProperty(45.id, defaultValue = null)
    val diamonds = UIntProperty(46.id, defaultValue = null)
    val modLevel = EnumProperty(49.id, Serializer.enum(ModLevel.entries))
    val commentHistoryState = EnumProperty(50.id, Serializer.enum(CommentHistoryState.entries))
    val moons = UIntProperty(52.id, defaultValue = null)
    val rawDemonLevelsBreakdown = UnencodedStringProperty(55.id, defaultValue = null)
    val rawClassicLevelsBreakdown = UnencodedStringProperty(56.id, defaultValue = null)
    val rawPlatformerLevelsBreakdown = UnencodedStringProperty(57.id, defaultValue = null)
    val demonBreakdown
        get() = DemonLevelsBreakdown(this.rawDemonLevelsBreakdown.getOrThrow())
    val classicBreakdown
        get() = ClassicLevelsBreakdown(this.rawClassicLevelsBreakdown.getOrThrow())
    val platformerBreadown
        get() = PlatformerLevelsBreakdown(this.rawPlatformerLevelsBreakdown.getOrThrow())
    val discord = UnencodedStringProperty(58.id, defaultValue = null)
    val instagram = UnencodedStringProperty(59.id, defaultValue = null)
    val tiktok = UnencodedStringProperty(60.id, defaultValue = null)
    /**
     * A player provided info. Quoting from geometry dash:
     * > "This field can be used for a custom, one time authentication token generated by a third-party service (such as a mod).
     * Once saved here, the service can link and verify your account."
     */
    val customField = UnencodedStringProperty(61.id, defaultValue = null)
}

@GDClientApi
class LeaderboardUser(client: AbstractGDClient) : UserStructure(client) {
    companion object : ServerStructureCompanion<LeaderboardUser> {
        override val separator: Char = ':'

        override fun parse(rawString: String, client: AbstractGDClient): LeaderboardUser =
            ObjectParser.parse(rawString, LeaderboardUser(client), separator)
    }

    /** The time since you submitted a levelScore */
    val age = UnencodedStringProperty(42.id, defaultValue = null)
}

@GDClientApi
class FriendRequestUser(client: AbstractGDClient) : UserStructure(client) {
    companion object : ServerStructureCompanion<FriendRequestUser> {
        override val separator: Char = ':'

        override fun parse(rawString: String, client: AbstractGDClient): FriendRequestUser =
            ObjectParser.parse(rawString, FriendRequestUser(client), separator)
    }

    val newFriendRequest = BoolProperty(41.id, defaultValue = null)
}
