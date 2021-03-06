
package swearbot

import org.jibble.pircbot.PircBot
import java.util.*

class SwearBot(name: String, val swearWords: Collection<String>, scores: Map<String, Int>): PircBot() {
    var op = false
    var lastSpeaker = ""
    val userScores = mutableMapOf<String, Int>()
    @Volatile var scoresUpdated = false

    init {
        this.name = name
        userScores.putAll(scores)
    }

    override fun onOp(channel: String?, sourceNick: String?, sourceLogin: String?, sourceHostname: String?, recipient: String?) {
        super.onOp(channel, sourceNick, sourceLogin, sourceHostname, recipient)
        if(this.name == recipient) {
            op = true
        }
    }

    override fun onDeop(channel: String?, sourceNick: String?, sourceLogin: String?, sourceHostname: String?, recipient: String?) {
        super.onDeop(channel, sourceNick, sourceLogin, sourceHostname, recipient)
        if(this.name == recipient) {
            op = false
        }
    }

    override fun onKick(channel: String?, kickerNick: String?, kickerLogin: String?, kickerHostname: String?, recipientNick: String, reason: String?) {
        super.onKick(channel, kickerNick, kickerLogin, kickerHostname, recipientNick, reason)
        if (recipientNick.toLowerCase().contentEquals(getNick().toLowerCase())) {
            joinChannel(channel);
        }
    }

    override fun onDisconnect() {
        super.onDisconnect()
        reconnect()
    }

    override fun onMessage(channel: String, sender: String, login: String, hostname: String, message: String) {

        val lowerMessage = message.toLowerCase()

        if (lowerMessage.startsWith(name.toLowerCase())) {
            sendMessage(channel, parseAndExecuteCommand(lowerMessage.substring(name.length, lowerMessage.length)))
        }

        var score = userScores.getOrDefault(sender, 0)
        if (isSwear(message)) {
            if(lastSpeaker != sender) {
                score++
                userScores[sender] = score
                scoresUpdated = true
                sendMessage(channel, "$sender swore! score: $score ${getSwears(message)}")
            }
        } else {
            score-=10
            userScores[sender] = score
            scoresUpdated = true
            if(op) {
                kick(channel, sender, "$sender didn't swear. What a ${randomSwear()}ing ${randomSwear()}. score: $score")
            } else {
                sendMessage(channel, "$sender didn't swear. What a ${randomSwear()}ing ${randomSwear()}. score: $score")
            }
        }
        lastSpeaker = sender
    }

    override fun onPrivateMessage(sender: String, login: String, hostname: String, message: String) {
        sendMessage(sender, parseAndExecuteCommand(message))
    }

    fun isSwear(message: String): Boolean {
        val lowerMessage = message.toLowerCase()
        for (swear in swearWords) {
            if (lowerMessage.contains(swear)) {
                return true
            }
        }
        return false
    }

    fun getSwears(message: String): Collection<String> {
        val lowerMessage = message.toLowerCase()
        val used = mutableListOf<String>()
        swearWords.forEach { if(lowerMessage.contains(it)) { used.add(it) } }
        return used
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
        userScores.forEach { user, score -> msg += "$user:$score " }
        return msg
    }

    fun randomSwear(): String {
        val random = Random()
        return swearWords.elementAt(random.nextInt(swearWords.size))
    }

    fun isScoresUpdated(): Boolean {
        val tmp = scoresUpdated
        scoresUpdated = false
        return tmp
    }
}