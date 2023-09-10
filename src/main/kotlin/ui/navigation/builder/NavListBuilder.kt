package ui.navigation.builder

class NavListBuilder(
    builder: NavBuilderScope.() -> Unit
) {
    private val _targetList: MutableList<NavTarget> = mutableListOf()
    val targetList: Map<String, List<NavTarget>>
        get() = _targetList.groupBy { it.groupName }

    private var _defaultTarget: NavTarget? = null

    val defaultTarget: NavTarget
        get() = _defaultTarget ?: _targetList.first()

    init {
        builder(
            NavBuilderScope { title, icon, groupName, isDefaultTarget, screen ->
                val target = NavTarget(
                    id = _targetList.size,
                    title = title,
                    icon = icon,
                    groupName = groupName,
                    screen = screen
                )

                _targetList.add(target)

                if (isDefaultTarget) _defaultTarget = target
            }
        )
    }
}
