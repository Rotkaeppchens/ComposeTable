package data

interface LedModule {
    fun calc(ledNr: Int): LedColor
}
