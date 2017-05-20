package swearbot

import junit.framework.TestCase
import org.junit.Test

/**
 * Created by calx on 20/05/17.
 */

class TestSwearBot: TestCase() {

    @Test
    fun testCommandInChannel() {
        val name = "swearbot"
        val swearWords = setOf<String>("thing")
        val bot = SwearBot(name, swearWords)
        val result = bot.parseAndExecuteCommand(", scores and what  not")
        assertEquals("no scores available", result)
    }
}