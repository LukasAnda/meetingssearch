package sk.vinf.meetingssearch

import com.florianingerl.util.regex.Pattern

const val DATA_PATH = "./data"
val infoBoxRegex: Pattern = Pattern.compile("(?=\\{Infobox)(\\{([^{}]|(?1))*+\\})")
val commentsRegex: Pattern = Pattern.compile("\\<!--(.|\\n)*?-->")
val referencesRegex: Pattern = Pattern.compile("(\\<ref(.|\\n)*?\\/ref>|\\<ref(.|\\n)*?\\/>)")