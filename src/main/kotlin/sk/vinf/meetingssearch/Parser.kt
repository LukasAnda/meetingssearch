package sk.vinf.meetingssearch

import java.io.File
import javax.xml.stream.XMLStreamReader

object Parser {
    @JvmStatic
    fun main(args: Array<String>) {
        val file = File(DATA_PATH)
        file.listFiles()?.forEach {
            parseFile(it)
        }

    }
}

fun parseFile(file: File) {
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
                println(matcher.group().substring(1..10))
            }
        }
        onFinish {}
    }
}