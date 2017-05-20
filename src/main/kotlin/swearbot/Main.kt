package swearbot

import java.io.File

/**
 * Created by calx on 20/05/17.
 */
fun main(args: Array<String>) {
    val name = args[0]
    val server = args[1]
    val channel = args[2]
    val swearWords = readSwearWords("data/swear_words.txt")
    println("$name is alive!")
    val swearBot = SwearBot(name, swearWords)
    swearBot.setVerbose(true)
    println("connecting to $server")
    swearBot.connect(server)
    swearBot.joinChannel(channel)
}

fun readSwearWords(filename: String) : Set<String> {
    val swearWords = mutableSetOf<String>()
    swearWords.addAll(File(filename).readLines())
    return swearWords
}
