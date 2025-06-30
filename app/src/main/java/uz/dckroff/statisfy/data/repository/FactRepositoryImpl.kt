package uz.dckroff.statisfy.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uz.dckroff.statisfy.data.local.dao.CategoryDao
import uz.dckroff.statisfy.data.local.dao.FactDao
import uz.dckroff.statisfy.data.local.entities.FactEntity
import uz.dckroff.statisfy.data.remote.api.FactApi
import uz.dckroff.statisfy.domain.model.Fact
import uz.dckroff.statisfy.domain.repository.FactRepository
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.NetworkChecker
import uz.dckroff.statisfy.utils.Result
import javax.inject.Inject

/**
 * Реализация репозитория для фактов
 */
class FactRepositoryImpl @Inject constructor(
    private val factApi: FactApi,
    private val factDao: FactDao,
    private val categoryDao: CategoryDao,
    private val networkChecker: NetworkChecker
) : FactRepository {
    
    override suspend fun getFacts(page: Int, size: Int, categoryId: String?): Result<List<Fact>> {
        Logger.methodCall("FactRepositoryImpl", "getFacts")
        
        return try {
            // Проверяем наличие интернета
            if (networkChecker.isNetworkAvailable()) {
                Logger.d("FactRepositoryImpl: Network available, fetching facts from API")
                
                val response = factApi.getFacts(page, size, categoryId)
                
                if (response.isSuccessful) {
                    response.body()?.let { factsResponse ->
                        Logger.d("FactRepositoryImpl: Successfully fetched ${factsResponse.content.size} facts")
                        
                        // Сохраняем категории в базу данных
                        val categories = factsResponse.content.map { it.category }
                        categoryDao.insertCategories(categories.map { 
                            uz.dckroff.statisfy.data.local.entities.CategoryEntity.fromDomainModel(it.toDomainModel()) 
                        })
                        
                        // Сохраняем факты в базу данных
                        val facts = factsResponse.content.map { factDto ->
                            FactEntity.fromDomainModel(factDto.toDomainModel())
                        }
                        factDao.insertFacts(facts)
                        
                        // Возвращаем доменные модели
                        val domainFacts = factsResponse.content.map { it.toDomainModel() }
                        Result.Success(domainFacts)
                    } ?: run {
                        Logger.e("FactRepositoryImpl: Empty response body")
                        Result.Error("Не удалось получить факты: пустой ответ")
                    }
                } else {
                    Logger.e("FactRepositoryImpl: Error fetching facts: ${response.code()} ${response.message()}")
                    Result.Error("Не удалось получить факты: ${response.code()} ${response.message()}")
                }
            } else {
                Logger.w("FactRepositoryImpl: No network, returning cached facts")
                // Если нет интернета, возвращаем кэшированные данные
                val cachedFacts = getCachedFacts()
                if (cachedFacts.isNotEmpty()) {
                    Result.Success(cachedFacts)
                } else {
                    Result.Error("Нет доступа к интернету и нет кэшированных данных")
                }
            }
        } catch (e: Exception) {
            Logger.e("FactRepositoryImpl: Exception while fetching facts", e)
            Result.Error("Ошибка при получении фактов: ${e.message}")
        }
    }
    
    override suspend fun getFactById(id: String): Result<Fact> {
        Logger.methodCall("FactRepositoryImpl", "getFactById")
        
        return try {
            // Сначала проверяем в кэше
            val cachedFact = factDao.getFactById(id)
            
            if (cachedFact != null) {
                Logger.d("FactRepositoryImpl: Found fact in cache")
                val category = categoryDao.getCategoryById(cachedFact.categoryId)
                
                if (category != null) {
                    Result.Success(cachedFact.toDomainModel(category.toDomainModel()))
                } else {
                    Logger.e("FactRepositoryImpl: Category not found for fact")
                    Result.Error("Категория факта не найдена")
                }
            } else if (networkChecker.isNetworkAvailable()) {
                Logger.d("FactRepositoryImpl: Fact not in cache, fetching from API")
                
                val response = factApi.getFactById(id)
                
                if (response.isSuccessful) {
                    response.body()?.let { factDto ->
                        Logger.d("FactRepositoryImpl: Successfully fetched fact from API")
                        
                        // Сохраняем категорию в базу данных
                        categoryDao.insertCategory(
                            uz.dckroff.statisfy.data.local.entities.CategoryEntity.fromDomainModel(
                                factDto.category.toDomainModel()
                            )
                        )
                        
                        // Сохраняем факт в базу данных
                        factDao.insertFact(FactEntity.fromDomainModel(factDto.toDomainModel()))
                        
                        // Возвращаем доменную модель
                        Result.Success(factDto.toDomainModel())
                    } ?: run {
                        Logger.e("FactRepositoryImpl: Empty response body")
                        Result.Error("Не удалось получить факт: пустой ответ")
                    }
                } else {
                    Logger.e("FactRepositoryImpl: Error fetching fact: ${response.code()} ${response.message()}")
                    Result.Error("Не удалось получить факт: ${response.code()} ${response.message()}")
                }
            } else {
                Logger.e("FactRepositoryImpl: No network and fact not in cache")
                Result.Error("Нет доступа к интернету и факт не найден в кэше")
            }
        } catch (e: Exception) {
            Logger.e("FactRepositoryImpl: Exception while fetching fact", e)
            Result.Error("Ошибка при получении факта: ${e.message}")
        }
    }
    
    override suspend fun getRecentFacts(): Result<List<Fact>> {
        Logger.methodCall("FactRepositoryImpl", "getRecentFacts")
        
        return try {
            if (networkChecker.isNetworkAvailable()) {
                Logger.d("FactRepositoryImpl: Network available, fetching recent facts from API")
                
                val response = factApi.getRecentFacts()
                
                if (response.isSuccessful) {
                    response.body()?.let { factDtos ->
                        Logger.d("FactRepositoryImpl: Successfully fetched ${factDtos.size} recent facts")
                        
                        // Сохраняем категории в базу данных
                        val categories = factDtos.map { it.category }
                        categoryDao.insertCategories(categories.map { 
                            uz.dckroff.statisfy.data.local.entities.CategoryEntity.fromDomainModel(it.toDomainModel()) 
                        })
                        
                        // Сохраняем факты в базу данных
                        val facts = factDtos.map { factDto ->
                            FactEntity.fromDomainModel(factDto.toDomainModel())
                        }
                        factDao.insertFacts(facts)
                        
                        // Возвращаем доменные модели
                        val domainFacts = factDtos.map { it.toDomainModel() }
                        Result.Success(domainFacts)
                    } ?: run {
                        Logger.e("FactRepositoryImpl: Empty response body")
                        Result.Error("Не удалось получить недавние факты: пустой ответ")
                    }
                } else {
                    Logger.e("FactRepositoryImpl: Error fetching recent facts: ${response.code()} ${response.message()}")
                    Result.Error("Не удалось получить недавние факты: ${response.code()} ${response.message()}")
                }
            } else {
                Logger.w("FactRepositoryImpl: No network, returning cached recent facts")
                // Если нет интернета, возвращаем кэшированные данные
                val cachedFacts = getCachedRecentFacts()
                if (cachedFacts.isNotEmpty()) {
                    Result.Success(cachedFacts)
                } else {
                    Result.Error("Нет доступа к интернету и нет кэшированных данных")
                }
            }
        } catch (e: Exception) {
            Logger.e("FactRepositoryImpl: Exception while fetching recent facts", e)
            Result.Error("Ошибка при получении недавних фактов: ${e.message}")
        }
    }
    
    override suspend fun getDailyFact(): Result<Fact> {
        Logger.methodCall("FactRepositoryImpl", "getDailyFact")
        
        return try {
            // Сначала проверяем в кэше
            val cachedDailyFact = factDao.getDailyFact()
            
            if (cachedDailyFact != null) {
                Logger.d("FactRepositoryImpl: Found daily fact in cache")
                val category = categoryDao.getCategoryById(cachedDailyFact.categoryId)
                
                if (category != null) {
                    Result.Success(cachedDailyFact.toDomainModel(category.toDomainModel()))
                } else {
                    Logger.e("FactRepositoryImpl: Category not found for daily fact")
                    Result.Error("Категория ежедневного факта не найдена")
                }
            } else if (networkChecker.isNetworkAvailable()) {
                Logger.d("FactRepositoryImpl: Daily fact not in cache, fetching from API")
                
                val response = factApi.getDailyFact()
                
                if (response.isSuccessful) {
                    response.body()?.let { factDto ->
                        Logger.d("FactRepositoryImpl: Successfully fetched daily fact from API")
                        
                        // Сохраняем категорию в базу данных
                        categoryDao.insertCategory(
                            uz.dckroff.statisfy.data.local.entities.CategoryEntity.fromDomainModel(
                                factDto.category.toDomainModel()
                            )
                        )
                        
                        // Сохраняем факт в базу данных как ежедневный
                        val factEntity = FactEntity.fromDomainModel(factDto.toDomainModel(), true)
                        factDao.setDailyFact(factEntity)
                        
                        // Возвращаем доменную модель
                        Result.Success(factDto.toDomainModel())
                    } ?: run {
                        Logger.e("FactRepositoryImpl: Empty response body")
                        Result.Error("Не удалось получить ежедневный факт: пустой ответ")
                    }
                } else {
                    Logger.e("FactRepositoryImpl: Error fetching daily fact: ${response.code()} ${response.message()}")
                    Result.Error("Не удалось получить ежедневный факт: ${response.code()} ${response.message()}")
                }
            } else {
                Logger.e("FactRepositoryImpl: No network and daily fact not in cache")
                Result.Error("Нет доступа к интернету и ежедневный факт не найден в кэше")
            }
        } catch (e: Exception) {
            Logger.e("FactRepositoryImpl: Exception while fetching daily fact", e)
            Result.Error("Ошибка при получении ежедневного факта: ${e.message}")
        }
    }
    
    override fun getCachedFactsFlow(): Flow<List<Fact>> {
        Logger.methodCall("FactRepositoryImpl", "getCachedFactsFlow")
        
        return factDao.getAllFactsFlow().map { factEntities ->
            factEntities.mapNotNull { factEntity ->
                val category = categoryDao.getCategoryById(factEntity.categoryId)
                category?.let { factEntity.toDomainModel(it.toDomainModel()) }
            }
        }
    }
    
    /**
     * Получение кэшированных фактов
     */
    private suspend fun getCachedFacts(): List<Fact> {
        Logger.methodCall("FactRepositoryImpl", "getCachedFacts")
        
        val factEntities = factDao.getRecentFacts(20)
        return factEntities.mapNotNull { factEntity ->
            val category = categoryDao.getCategoryById(factEntity.categoryId)
            category?.let { factEntity.toDomainModel(it.toDomainModel()) }
        }
    }
    
    /**
     * Получение кэшированных недавних фактов
     */
    private suspend fun getCachedRecentFacts(): List<Fact> {
        Logger.methodCall("FactRepositoryImpl", "getCachedRecentFacts")
        
        val factEntities = factDao.getRecentFacts(10)
        return factEntities.mapNotNull { factEntity ->
            val category = categoryDao.getCategoryById(factEntity.categoryId)
            category?.let { factEntity.toDomainModel(it.toDomainModel()) }
        }
    }
} 