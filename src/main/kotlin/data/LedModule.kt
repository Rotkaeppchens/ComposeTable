package data

interface LedModule {
    val moduleId: String

    fun calc(ledNr: Int): LedColor
}
