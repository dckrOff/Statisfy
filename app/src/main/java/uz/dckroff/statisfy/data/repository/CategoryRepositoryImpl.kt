package uz.dckroff.statisfy.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import uz.dckroff.statisfy.data.local.dao.CategoryDao
import uz.dckroff.statisfy.data.local.entities.CategoryEntity
import uz.dckroff.statisfy.data.remote.api.CategoryApi
import uz.dckroff.statisfy.domain.model.Category
import uz.dckroff.statisfy.domain.repository.CategoryRepository
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.NetworkChecker
import uz.dckroff.statisfy.utils.Result
import javax.inject.Inject

/**
 * Реализация репозитория для категорий
 */
class CategoryRepositoryImpl @Inject constructor(
    private val categoryApi: CategoryApi,
    private val categoryDao: CategoryDao,
    private val networkChecker: NetworkChecker
) : CategoryRepository {

    override suspend fun getCategories(): Result<List<Category>> {
        Logger.methodCall("CategoryRepositoryImpl", "getCategories")

        return try {
            // Проверяем наличие интернета
            if (networkChecker.isNetworkAvailable()) {
                Logger.d("CategoryRepositoryImpl: Network available, fetching categories from API")

                val response = categoryApi.getCategories()

                if (response.isSuccessful) {
                    response.body()?.let { categoryDtos ->
                        Logger.d("CategoryRepositoryImpl: Successfully fetched ${categoryDtos.size} categories")

                        // Сохраняем категории в базу данных
                        val categories = categoryDtos.map { categoryDto ->
                            CategoryEntity.fromDomainModel(categoryDto.toDomainModel())
                        }
                        categoryDao.insertCategories(categories)

                        // Возвращаем доменные модели
                        val domainCategories = categoryDtos.map { it.toDomainModel() }
                        Result.Success(domainCategories)
                    } ?: run {
                        Logger.e("CategoryRepositoryImpl: Empty response body")
                        Result.Error("Не удалось получить категории: пустой ответ")
                    }
                } else {
                    Logger.e("CategoryRepositoryImpl: Error fetching categories: ${response.code()} ${response.message()}")
                    Result.Error("Не удалось получить категории: ${response.code()} ${response.message()}")
                }
            } else {
                Logger.w("CategoryRepositoryImpl: No network, returning cached categories")
                // Если нет интернета, возвращаем кэшированные данные
                val cachedCategories = getCachedCategories()
                if (cachedCategories.isNotEmpty()) {
                    Result.Success(cachedCategories)
                } else {
                    Result.Error("Нет доступа к интернету и нет кэшированных данных")
                }
            }
        } catch (e: Exception) {
            Logger.e("CategoryRepositoryImpl: Exception while fetching categories", e)
            Result.Error("Ошибка при получении категорий: ${e.message}")
        }
    }

    override fun getCachedCategoriesFlow(): Flow<List<Category>> {
        Logger.methodCall("CategoryRepositoryImpl", "getCachedCategoriesFlow")

        return categoryDao.getAllCategoriesFlow().map { categoryEntities ->
            categoryEntities.map { it.toDomainModel() }
        }
    }

    /**
     * Получение кэшированных категорий
     */
    private suspend fun getCachedCategories(): List<Category> {
        Logger.methodCall("CategoryRepositoryImpl", "getCachedCategories")

        return categoryDao.getAllCategoriesFlow().first().map { it.toDomainModel() }
    }
} 