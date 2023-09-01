package data

interface LedModule {
    val moduleId: String

    fun onUpdate(nanoTime: Long): List<LedColor>
}
