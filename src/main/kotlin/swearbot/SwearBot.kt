
package swearbot

import org.jibble.pircbot.PircBot
import java.util.*

class SwearBot(name: String, val swearWords: Collection<String>): PircBot() {
    val userScores = mutableMapOf<String, Int>()

    init {
        this.name = name
    }

    override fun onMessage(channel: String, sender: String, login: String, hostname: String, message: String) {

        val lowerMessage = message.toLowerCase()

        if (lowerMessage.startsWith(name.toLowerCase())) {
            sendMessage(channel, parseAndExecuteCommand(lowerMessage.substring(name.length, lowerMessage.length)))
        }

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
        println("parse and execute: '$command'")
        val parts = command.split("\\s+".toRegex())
        println(parts)
        when (parts[1].trim()) {
            "scores" -> return listScores()
        }
        return "command not found. Availble commands: scores"
    }

    fun listScores(): String {
        if(userScores.isEmpty()) {
            return "no scores available"
        }

        var msg = ""
        userScores.forEach { user, score -> msg += "${user}:${score} " }
        return msg
    }

    fun randomSwear(): String {
        val random = Random()
        return swearWords.elementAt(random.nextInt(swearWords.size))
    }
}