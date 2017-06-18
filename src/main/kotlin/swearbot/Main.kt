package swearbot

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.FileWriter
import kotlin.concurrent.fixedRateTimer

/**
 * Created by calx on 20/05/17.
 */
val scoresFileName = "data/scores.csv"

fun main(args: Array<String>) {
    println(listOf(*args))
    val name = args[0]
    val server = args[1]
    val channel = args[2]
    val swearWords = readSwearWords("data/swear_words.txt")
    val scores = loadScores(scoresFileName)
    println("$name is alive!")
    val swearBot = SwearBot(name, swearWords, scores)
    val fixedRateTimer = fixedRateTimer(name = "scores-updater", daemon = true, initialDelay = 5000, period = 5000) {
        if(swearBot.isScoresUpdated()) {
            saveScores(scoresFileName, swearBot.userScores)
        }
    }
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

fun loadScores(fileName: String): Map<String,Int> {
    val scores = mutableMapOf<String,Int>()
    val csvFileFormat = CSVFormat.DEFAULT
    var fileReader: FileReader? = null
    var csvFileParser: CSVParser? = null
    try {
        fileReader = FileReader(fileName)
        csvFileParser = CSVParser(fileReader, csvFileFormat)
        val records = csvFileParser.getRecords()
        records.forEach {scores.put(it.get(0), it.get(1).toInt())}
    } catch(ex: FileNotFoundException) {
        println(ex)
    } finally {
        fileReader?.close()
        csvFileParser?.close()
    }
    return scores
}

fun saveScores(fileName: String, scores: Map<String,Int>) {
    println("save scores. file = $fileName")
    val csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator("\n")

    var fileWriter: FileWriter? = null
    var csvFilePrinter: CSVPrinter? = null
    try {
        fileWriter = FileWriter(fileName)
        csvFilePrinter = CSVPrinter(fileWriter, csvFileFormat)

        scores.forEach { csvFilePrinter?.printRecord(it.key, it.value) }
    } finally {
        fileWriter?.flush()
        fileWriter?.close()
        csvFilePrinter?.close()
    }
    println("Scores saved")

}