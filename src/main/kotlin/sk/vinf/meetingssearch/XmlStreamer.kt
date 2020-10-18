package sk.vinf.meetingssearch

import java.io.Reader
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamReader
import javax.xml.stream.events.XMLEvent

/**
 * When using the underlying [XMLStreamReader] be aware of common streaming pitfalls.
 *
 *  - retrieving the tag content the attributes are skipped, later asking for attributes throws [IllegalStateException]
 *  - retrieving the tag content consumes the [XMLEvent.END_ELEMENT] event.
 */
inline fun <reified T : Any> Reader.streamXml(init: (XMLStreamer.() -> Unit)) =
    XMLStreamer(XMLInputFactory.newFactory().createXMLStreamReader(this)).run {
        init()
        stream()
        result?.invoke() as? T ?: throw IllegalStateException(
            "'${T::class}' expected to return. Make sure to end streaming with an 'onFinish()' call."
        )
    }

private const val FIRST_XML_EVENT_NUMBER = 1
private const val NUMBER_OF_XML_EVENTS = 15

class XMLStreamer(
    private val reader: XMLStreamReader
) {

    var result: (() -> Any)? = null
    private val anyEventsToActions: MutableMap<Int, MutableList<XMLStreamReader.() -> Unit>> = mutableMapOf()
    private val tagsToActions: MutableMap<Int, MutableList<Event>> = mutableMapOf()

    data class Event(val tagName: String, val action: XMLStreamReader.() -> Unit)

    fun onTag(name: String, action: XMLStreamReader.() -> Unit) {
        tagsToActions.getOrPut(XMLEvent.START_ELEMENT) { ArrayList() }.add(Event(name, action))
    }

    fun onTagEnd(name: String, action: XMLStreamReader.() -> Unit) {
        tagsToActions.getOrPut(XMLEvent.END_ELEMENT) { ArrayList() }.add(Event(name, action))
    }

    /**
     * See [XMLEvent] to register to specific events.
     */
    fun onAny(eventType: Int, action: XMLStreamReader.() -> Unit) {
        require(eventType in FIRST_XML_EVENT_NUMBER..NUMBER_OF_XML_EVENTS)
        anyEventsToActions.getOrPut(eventType) { ArrayList() }.add(action)
    }

    fun onFinish(returns: () -> Any) {
        result = returns
    }

    fun stream() {
        reader.apply {
            check(eventType == XMLEvent.START_DOCUMENT)
            while (hasNext()) {
                val eventType = next()
                anyEventsToActions[eventType]?.forEach { it.invoke(this) }
                tagsToActions[eventType]
                    ?.asSequence()
                    ?.filter { it.tagName == localName }
                    ?.forEach { it.action.invoke(this) }
            }
            check(eventType == XMLEvent.END_DOCUMENT)
        }
    }
}