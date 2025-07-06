package uz.dckroff.statisfy.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import uz.dckroff.statisfy.data.local.converters.StringSetConverter
import uz.dckroff.statisfy.domain.model.ContentType
import uz.dckroff.statisfy.domain.model.FavoriteFolder

/**
 * Сущность для папок избранного контента
 */
@Entity(tableName = "favorite_folders")
@TypeConverters(StringSetConverter::class)
data class FavoriteFolderEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String? = null,
    val color: String? = null,
    val icon: String? = null,
    val itemsCount: Int = 0,
    val contentTypes: Set<String> = emptySet(), // Set<ContentType.name>
    val createdAt: String,
    val updatedAt: String? = null,
    val isDefault: Boolean = false,
    val sortOrder: Int = 0
) {
    /**
     * Преобразование в доменную модель
     */
    fun toDomainModel(): FavoriteFolder {
        return FavoriteFolder(
            id = id,
            name = name,
            description = description,
            color = color,
            icon = icon,
            itemsCount = itemsCount,
            contentTypes = contentTypes.mapNotNull { 
                ContentType.fromString(it) 
            }.toSet(),
            createdAt = createdAt,
            updatedAt = updatedAt,
            isDefault = isDefault
        )
    }
    
    companion object {
        /**
         * Создание из доменной модели
         */
        fun fromDomainModel(folder: FavoriteFolder): FavoriteFolderEntity {
            return FavoriteFolderEntity(
                id = folder.id,
                name = folder.name,
                description = folder.description,
                color = folder.color,
                icon = folder.icon,
                itemsCount = folder.itemsCount,
                contentTypes = folder.contentTypes.map { it.name }.toSet(),
                createdAt = folder.createdAt,
                updatedAt = folder.updatedAt,
                isDefault = folder.isDefault
            )
        }
        
        /**
         * Создание папки по умолчанию
         */
        fun createDefault(type: ContentType): FavoriteFolderEntity {
            return FavoriteFolderEntity(
                id = "default_${type.name.lowercase()}",
                name = type.displayName,
                description = "Папка по умолчанию для ${type.displayName.lowercase()}",
                color = getDefaultColorForType(type),
                icon = getDefaultIconForType(type),
                contentTypes = setOf(type.name),
                createdAt = System.currentTimeMillis().toString(),
                isDefault = true
            )
        }
        
        private fun getDefaultColorForType(type: ContentType): String {
            return when (type) {
                ContentType.FACT -> "#2196F3"     // Синий
                ContentType.NEWS -> "#FF9800"     // Оранжевый
                ContentType.STATISTIC -> "#4CAF50" // Зеленый
            }
        }
        
        private fun getDefaultIconForType(type: ContentType): String {
            return when (type) {
                ContentType.FACT -> "lightbulb"
                ContentType.NEWS -> "newspaper"
                ContentType.STATISTIC -> "bar_chart"
            }
        }
    }
}