package uz.dckroff.statisfy.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.dckroff.statisfy.data.local.dao.FavoriteDao
import uz.dckroff.statisfy.data.local.entities.FavoriteEntity
import uz.dckroff.statisfy.data.remote.api.FavoriteApi
import uz.dckroff.statisfy.domain.model.Category
import uz.dckroff.statisfy.domain.model.Fact
import uz.dckroff.statisfy.domain.model.News
import uz.dckroff.statisfy.domain.model.Statistic
import uz.dckroff.statisfy.domain.repository.FavoritesRepository
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.NetworkChecker
import uz.dckroff.statisfy.utils.Result
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация репозитория для избранного
 */
@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val favoriteApi: FavoriteApi,
    private val networkChecker: NetworkChecker
) : FavoritesRepository {

    // Временное хранилище для избранного, пока не будет полной реализации с базой данных
    private val favoriteFacts = mutableSetOf<String>()
    private val favoriteNews = mutableSetOf<String>()
    private val favoriteStatistics = mutableSetOf<String>()

    override suspend fun addToFavorites(fact: Fact): Result<Unit> {
        Logger.d("FavoritesRepositoryImpl: addToFavorites() called with fact: ${fact.id}")
        
        try {
            // Сохраняем в локальную базу
            val favoriteEntity = FavoriteEntity(
                itemId = fact.id,
                itemType = FavoriteEntity.TYPE_FACT,
                title = fact.title,
                content = fact.content,
                source = fact.source,
                imageUrl = null,
                categoryId = fact.category.id,
                categoryName = fact.category.name,
                createdAt = fact.createdAt
            )
            
            favoriteDao.insertFavorite(favoriteEntity)
            favoriteFacts.add(fact.id)
            
            // Отправляем на сервер, если есть сеть
            if (networkChecker.isNetworkAvailable()) {
                val response = favoriteApi.addToFavorites(
                    mapOf(
                        "itemId" to fact.id,
                        "type" to FavoriteEntity.TYPE_FACT
                    )
                )
                
                if (!response.isSuccessful) {
                    Logger.e("FavoritesRepositoryImpl: Error syncing favorite to server: ${response.code()}")
                }
            }
            
            return Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e("FavoritesRepositoryImpl: Error adding fact to favorites", e)
            return Result.Error("Не удалось добавить факт в избранное: ${e.message}")
        }
    }

    override suspend fun addToFavorites(news: News): Result<Unit> {
        Logger.d("FavoritesRepositoryImpl: addToFavorites() called with news: ${news.id}")
        
        try {
            // Сохраняем в локальную базу
            val favoriteEntity = FavoriteEntity(
                itemId = news.id,
                itemType = FavoriteEntity.TYPE_NEWS,
                title = news.title,
                content = news.summary,
                source = news.source,
                imageUrl = null,
                categoryId = news.category.id,
                categoryName = news.category.name,
                createdAt = news.publishedAt
            )
            
            favoriteDao.insertFavorite(favoriteEntity)
            favoriteNews.add(news.id)
            
            // Отправляем на сервер, если есть сеть
            if (networkChecker.isNetworkAvailable()) {
                val response = favoriteApi.addToFavorites(
                    mapOf(
                        "itemId" to news.id,
                        "type" to FavoriteEntity.TYPE_NEWS
                    )
                )
                
                if (!response.isSuccessful) {
                    Logger.e("FavoritesRepositoryImpl: Error syncing favorite to server: ${response.code()}")
                }
            }
            
            return Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e("FavoritesRepositoryImpl: Error adding news to favorites", e)
            return Result.Error("Не удалось добавить новость в избранное: ${e.message}")
        }
    }

    override suspend fun addToFavorites(statistic: Statistic): Result<Unit> {
        Logger.d("FavoritesRepositoryImpl: addToFavorites() called with statistic: ${statistic.id}")
        
        try {
            // Сохраняем в локальную базу
            val favoriteEntity = FavoriteEntity(
                itemId = statistic.id,
                itemType = FavoriteEntity.TYPE_STATISTIC,
                title = statistic.title,
                content = "${statistic.value} ${statistic.unit}",
                source = statistic.source,
                imageUrl = null,
                categoryId = statistic.category.id,
                categoryName = statistic.category.name,
                createdAt = statistic.date.toString()
            )
            
            favoriteDao.insertFavorite(favoriteEntity)
            favoriteStatistics.add(statistic.id)
            
            // Отправляем на сервер, если есть сеть
            if (networkChecker.isNetworkAvailable()) {
                val response = favoriteApi.addToFavorites(
                    mapOf(
                        "itemId" to statistic.id,
                        "type" to FavoriteEntity.TYPE_STATISTIC
                    )
                )
                
                if (!response.isSuccessful) {
                    Logger.e("FavoritesRepositoryImpl: Error syncing favorite to server: ${response.code()}")
                }
            }
            
            return Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e("FavoritesRepositoryImpl: Error adding statistic to favorites", e)
            return Result.Error("Не удалось добавить статистику в избранное: ${e.message}")
        }
    }

    override suspend fun removeFromFavorites(itemId: String): Result<Unit> {
        Logger.d("FavoritesRepositoryImpl: removeFromFavorites() called with itemId: $itemId")
        
        try {
            // Получаем элемент из БД
            val favoriteEntity = favoriteDao.getFavoriteById(itemId)
            
            if (favoriteEntity != null) {
                // Удаляем из локальной базы
                favoriteDao.deleteFavorite(favoriteEntity)
                
                // Удаляем из временного хранилища
                favoriteFacts.remove(itemId)
                favoriteNews.remove(itemId)
                favoriteStatistics.remove(itemId)
                
                // Удаляем с сервера, если есть сеть
                if (networkChecker.isNetworkAvailable()) {
                    val response = favoriteApi.removeFromFavorites(itemId)
                    
                    if (!response.isSuccessful) {
                        Logger.e("FavoritesRepositoryImpl: Error syncing favorite removal to server: ${response.code()}")
                    }
                }
            }
            
            return Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e("FavoritesRepositoryImpl: Error removing from favorites", e)
            return Result.Error("Не удалось удалить элемент из избранного: ${e.message}")
        }
    }

    override suspend fun isFavorite(itemId: String): Boolean {
        try {
            return favoriteDao.isFavorite(itemId)
        } catch (e: Exception) {
            Logger.e("FavoritesRepositoryImpl: Error checking favorite status", e)
            // Используем временное хранилище как запасной вариант
            return itemId in favoriteFacts || itemId in favoriteNews || itemId in favoriteStatistics
        }
    }

    override fun getFavoriteFacts(): Flow<List<Fact>> {
        return favoriteDao.getFavoritesByType(FavoriteEntity.TYPE_FACT).map { entities ->
            entities.map { entity ->
                Fact(
                    id = entity.itemId,
                    title = entity.title,
                    content = entity.content,
                    category = Category(
                        id = entity.categoryId,
                        name = entity.categoryName,
                    ),
                    source = entity.source,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    override fun getFavoriteNews(): Flow<List<News>> {
        return favoriteDao.getFavoritesByType(FavoriteEntity.TYPE_NEWS).map { entities ->
            entities.map { entity ->
                News(
                    id = entity.itemId,
                    title = entity.title,
                    summary = entity.content,
                    url = "",
                    source = entity.source,
                    publishedAt = entity.createdAt,
                    isRelevant = true,
                    category = Category(
                        id = entity.categoryId,
                        name = entity.categoryName,
                    )
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getFavoriteStatistics(): Flow<List<Statistic>> {
        return favoriteDao.getFavoritesByType(FavoriteEntity.TYPE_STATISTIC).map { entities ->
            entities.map { entity ->
                // Парсим строку вида "12.5 кг" в значение и единицу измерения
                val contentParts = entity.content.split(" ", limit = 2)
                val value = contentParts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
                val unit = contentParts.getOrElse(1) { "" }
                
                Statistic(
                    id = entity.itemId,
                    title = entity.title,
                    value = value,
                    unit = unit,
                    source = entity.source,
                    category = Category(
                        id = entity.categoryId,
                        name = entity.categoryName,
                    ),
                    date = try {
                        LocalDate.parse(entity.createdAt)
                    } catch (e: Exception) {
                        LocalDate.now()
                    }
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAllFavorites(): Flow<Map<String, List<Any>>> {
        return favoriteDao.getAllFavorites().map { entities ->
            val groupedEntities = entities.groupBy { it.itemType }
            
            val facts = groupedEntities[FavoriteEntity.TYPE_FACT]?.map { entity ->
                Fact(
                    id = entity.itemId,
                    title = entity.title,
                    content = entity.content,
                    category = Category(
                        id = entity.categoryId,
                        name = entity.categoryName,
                    ),
                    source = entity.source,
                    createdAt = entity.createdAt
                )
            } ?: emptyList()
            
            val news = groupedEntities[FavoriteEntity.TYPE_NEWS]?.map { entity ->
                News(
                    id = entity.itemId,
                    title = entity.title,
                    summary = entity.content,
                    url = "",
                    source = entity.source,
                    publishedAt = entity.createdAt,
                    isRelevant = true,
                    category = Category(
                        id = entity.categoryId,
                        name = entity.categoryName,
                    )
                )
            } ?: emptyList()
            
            val statistics = groupedEntities[FavoriteEntity.TYPE_STATISTIC]?.map { entity ->
                val contentParts = entity.content.split(" ", limit = 2)
                val value = contentParts.getOrNull(0)?.toDoubleOrNull() ?: 0.0
                val unit = contentParts.getOrElse(1) { "" }
                
                Statistic(
                    id = entity.itemId,
                    title = entity.title,
                    value = value,
                    unit = unit,
                    source = entity.source,
                    category = Category(
                        id = entity.categoryId,
                        name = entity.categoryName,
                    ),
                    date = try {
                        LocalDate.parse(entity.createdAt)
                    } catch (e: Exception) {
                        LocalDate.now()
                    }
                )
            } ?: emptyList()
            
            mapOf(
                "facts" to facts,
                "news" to news,
                "statistics" to statistics
            ) as Map<String, List<Any>>
        }
    }

    override suspend fun syncFavorites(): Result<Unit> {
        Logger.d("FavoritesRepositoryImpl: syncFavorites() called")
        
        if (!networkChecker.isNetworkAvailable()) {
            return Result.Error("Нет подключения к сети")
        }
        
        try {
            // Получаем избранное с сервера
            val response = favoriteApi.getFavorites()
            
            if (response.isSuccessful && response.body() != null) {
                val favoriteResponse = response.body()!!
                
                // Синхронизируем данные с локальной базой
                // Для полноценной имплементации потребуется более сложная логика
                // синхронизации, обработка конфликтов и т.д.
                
                return Result.Success(Unit)
            } else {
                return Result.Error("Ошибка синхронизации: ${response.code()}")
            }
        } catch (e: Exception) {
            Logger.e("FavoritesRepositoryImpl: Error syncing favorites", e)
            return Result.Error("Ошибка при синхронизации избранного: ${e.message}")
        }
    }
} 