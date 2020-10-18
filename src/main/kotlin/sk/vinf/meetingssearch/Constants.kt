package sk.vinf.meetingssearch

import com.florianingerl.util.regex.Pattern

const val DATA_PATH = "./data"
val infoboxRegex = Pattern.compile("(?=\\{Infobox)(\\{([^{}]|(?1))*+\\})")