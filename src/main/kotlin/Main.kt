import client.Credentials
import client.GDClientApi
import client.GJP2
import client.clients.AsyncGDClient
import client.clients.GDClient
import editor.objects.triggers.objects.ToggleTrigger

// TODO: Once this is actually more closer to being finished delete this temporary main function
@OptIn(GDClientApi::class)
private fun main() {
    val username = System.getenv("USERNAME")!!
    val password = System.getenv("PASSWORD")!!

    val client = GDClient(Credentials(username, GJP2.create(password)))
    val asyncClient = AsyncGDClient(Credentials(username, GJP2.create(password)))
//    val commentRes = client.postAccountComment("Hi hello")
//    println(commentRes)

//    val res = client.getUserInfo(14350205)
//    val accountInfo = client.accountInfo
//    println(accountInfo)

//    val str = WSLiveEditor.getLevelString()
//    println("Level String: $str")

//    client.postComment("Hi Hello", 146305033)
//    asyncClient.postComment("Hi Hello", 146305033) { call, response, data ->
//        println("Async comment: $data")
//    }

    val label = ToggleTrigger(50f, 50f)
    println(label.asRawString())
}