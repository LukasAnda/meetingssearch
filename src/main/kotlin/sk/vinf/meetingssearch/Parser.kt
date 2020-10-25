package sk.vinf.meetingssearch

import java.io.File
import javax.xml.stream.XMLStreamReader

object Parser {
    @JvmStatic
    fun main(args: Array<String>) {
        val file = File(DATA_PATH)
        file.listFiles()?.forEach {
            if (!it.isDirectory) {
                parseFile(it)
            }
        }

    }
}

fun parseFile(file: File) {
    val newFile = File(DATA_PATH + "/parsed/" + file.name.substringBeforeLast(".") + "-infoboxes.txt")
    var processedText = ""
    file.reader().streamXml<String> {
        onTag("text") {
            processedText = ""
        }
        onAny(XMLStreamReader.CHARACTERS) {
            processedText += text
        }
        onTagEnd("text") {

            val matcher = infoboxRegex.matcher(processedText)

            if (matcher.find()) {
                val infobox = matcher.group()
                newFile.appendText(parseInfoBox(infobox))
            }
        }
        onFinish {
            ""
        }
    }
}

fun parseInfoBox(infoBox: String) = StringBuilder().apply {
    val person = Person()

    infoBox.split("\n").forEach {
        when {
            it.contains(Regex("^\\|\\s*name")) && person.name.isEmpty() -> person.name = it
            it.contains(Regex("^\\|\\s*birth_date")) && person.birthDate.isEmpty() -> person.birthDate = it
            it.contains(Regex("^\\|\\s*death_date")) && person.deathDate.isEmpty() -> person.deathDate = it
        }
    }
    if (person.isValid()) {
        append(person)
        append("\n")
    }
}.toString()

class Person {
    var name: String = ""
    var birthDate: String = ""
    var deathDate: String = ""

    private fun getPersonName(): String {
        var parsedName = name
        parsedName = parsedName.substringAfter("=")
        return parsedName.trim()
    }

    private fun getBirthDay(): String {
        //TODO This will later be turned to timestamp
        var parsedBirthDay = birthDate
        parsedBirthDay = parsedBirthDay.substringAfter("=")
        return parsedBirthDay.trim()
    }

    private fun getDeathDay(): String {
        //TODO This will later be turned to timestamp
        var parsedDeathDate = deathDate
        parsedDeathDate = parsedDeathDate.substringAfter("=")
        return parsedDeathDate.trim()
    }

    fun isValid() = getPersonName().isNotEmpty() && getBirthDay().isNotEmpty()

    override fun toString() = StringBuilder().apply {
        append(getPersonName())

        val birthDay = getBirthDay()
        append(" | ")
        append(birthDay)


        val deathDay = getDeathDay()
        append(" | ")
        append(deathDay)
    }.toString()
}