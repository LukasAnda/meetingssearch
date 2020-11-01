package sk.vinf.meetingssearch

import com.florianingerl.util.regex.Matcher
import org.junit.Test
import org.junit.Assert.*

class RegexTests {
    @Test
    fun `xml comment matches exactly`() {
        val text = "<!-- test -->"
        val matcher = commentsRegex.matcher(text)
        assertTrue(matcher?.find() == true)
        assertEquals(matcher?.group(), text)
    }

    @Test
    fun `xml comment with string after finds correct group`() {
        val text = "<!-- test --> string after"
        val matcher = commentsRegex.matcher(text)
        assertTrue(matcher?.find() == true)
        assertEquals(matcher?.group(), "<!-- test -->")
    }

    @Test
    fun `xml comment with string before finds correct group`() {
        val text = "String before <!-- test -->"
        val matcher = commentsRegex.matcher(text)
        assertTrue(matcher?.find() == true)
        assertEquals(matcher?.group(), "<!-- test -->")
    }

    @Test
    fun `xml comment with string around finds correct group`() {
        val text = "String before <!-- test --> string after"
        val matcher = commentsRegex.matcher(text)
        assertTrue(matcher?.find() == true)
        assertEquals(matcher?.group(), "<!-- test -->")
    }
}