
package swearbot

import org.jibble.pircbot.PircBot
import java.util.*

class SwearBot(name: String, val swearWords: Collection<String>): PircBot() {
    val userScores = mutableMapOf<String, Int>()

    init {
        this.name = name
    }

    override fun onMessage(channel: String?, sender: String, login: String?, hostname: String?, message: String) {

        val lowerMessage = message.toLowerCase()
        var score = userScores.getOrDefault(sender, 0)
        for (swear in swearWords) {
            if (lowerMessage.contains(swear)) {
                score++
                userScores[sender] = score
                sendMessage(channel, "$sender swore! score: $score ($swear)")
                return
            }
        }

        score--
        userScores[sender] = score
        sendMessage(channel, "$sender didn't swear. What a ${randomSwear()}ing ${randomSwear()}. score: $score")

    }

    override fun onPrivateMessage(sender: String, login: String, hostname: String, message: String) {
        sendMessage(sender, parseAndExecuteCommand(message))

    }

    fun parseAndExecuteCommand(command: String): String {
        val parts = command.split("\\s+".toRegex())
        when (parts[0]) {
            "scores" -> return listScores()
        }
        return """command not found
available commands are:
  scores"""
    }

    fun listScores(): String {
        var msg = ""
        userScores.forEach { user, score -> msg += "${user}: ${score}\n" }
        return msg
    }

    fun randomSwear(): String {
        val random = Random()
        return swearWords.elementAt(random.nextInt(swearWords.size))
    }
}