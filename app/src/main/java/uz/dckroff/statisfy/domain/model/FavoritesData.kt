package uz.dckroff.statisfy.domain.model

/**
 * Модель данных для экрана избранного
 */
data class FavoritesData(
    val totalCount: Int = 0,
    val factsCount: Int = 0,
    val newsCount: Int = 0,
    val statisticsCount: Int = 0,
    val folders: List<FavoriteFolder> = emptyList(),
    val recentItems: List<FavoriteItem> = emptyList(),
    val groupedContent: Map<ContentType, List<FavoriteItem>> = emptyMap()
)

/**
 * Универсальная модель избранного элемента
 */
data class FavoriteItem(
    val id: String,
    val contentId: String,
    val contentType: ContentType,
    val title: String,
    val summary: String?,
    val imageUrl: String?,
    val source: String?,
    val category: Category?,
    val folderId: String? = null,
    val tags: List<String> = emptyList(),
    val addedAt: String,
    val lastViewedAt: String? = null,
    val metadata: Map<String, String> = emptyMap()
) {
    fun toFact(): Fact? {
        return if (contentType == ContentType.FACT) {
            Fact(
                id = contentId,
                title = title,
                content = summary ?: "",
                category = category ?: Category("", ""),
                source = source ?: "",
                createdAt = addedAt
            )
        } else null
    }
    
    fun toNews(): News? {
        return if (contentType == ContentType.NEWS) {
            News(
                id = contentId,
                title = title,
                summary = summary ?: "",
                url = metadata["url"] ?: "",
                imageUrl = imageUrl,
                source = source ?: "",
                publishedAt = addedAt,
                category = category ?: Category("", ""),
                createdAt = addedAt
            )
        } else null
    }
}

/**
 * Папка для организации избранного контента
 */
data class FavoriteFolder(
    val id: String,
    val name: String,
    val description: String? = null,
    val color: String? = null,
    val icon: String? = null,
    val itemsCount: Int = 0,
    val contentTypes: Set<ContentType> = emptySet(),
    val createdAt: String,
    val updatedAt: String? = null,
    val isDefault: Boolean = false
)

/**
 * Типы контента
 */
enum class ContentType(val displayName: String) {
    FACT("Факты"),
    NEWS("Новости"),
    STATISTIC("Статистика");
    
    companion object {
        fun fromString(value: String): ContentType? {
            return values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}

/**
 * Настройки отображения избранного
 */
data class FavoritesDisplaySettings(
    val groupByType: Boolean = true,
    val showFolders: Boolean = true,
    val sortBy: FavoritesSortBy = FavoritesSortBy.RECENT,
    val viewMode: FavoritesViewMode = FavoritesViewMode.LIST
)

/**
 * Варианты сортировки избранного
 */
enum class FavoritesSortBy(val displayName: String) {
    RECENT("По дате добавления"),
    ALPHABETICAL("По алфавиту"),
    CATEGORY("По категориям"),
    CONTENT_TYPE("По типу контента"),
    LAST_VIEWED("По последнему просмотру")
}

/**
 * Режимы отображения избранного
 */
enum class FavoritesViewMode {
    LIST,    // Список
    GRID,    // Сетка
    COMPACT  // Компактный
} 