package uz.dckroff.statisfy.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.dckroff.statisfy.domain.model.NotificationSettings
import uz.dckroff.statisfy.services.NotificationData
import uz.dckroff.statisfy.services.NotificationType
import uz.dckroff.statisfy.utils.Result

/**
 * Репозиторий для управления уведомлениями
 */
interface NotificationRepository {

    /**
     * Регистрация устройства для получения push-уведомлений
     */
    suspend fun registerDevice(token: String): Result<Unit>

    /**
     * Обновление токена устройства
     */
    suspend fun updateDeviceToken(token: String): Result<Unit>

    /**
     * Получение настроек уведомлений
     */
    suspend fun getNotificationSettings(): Result<NotificationSettings>

    /**
     * Сохранение настроек уведомлений
     */
    suspend fun saveNotificationSettings(settings: NotificationSettings): Result<Unit>

    /**
     * Получение истории уведомлений
     */
    fun getNotificationHistory(): Flow<List<NotificationData>>

    /**
     * Сохранение уведомления в базу данных
     */
    suspend fun saveNotification(notification: NotificationData): Result<Unit>

    /**
     * Отметка уведомления как прочитанного
     */
    suspend fun markAsRead(notificationId: String): Result<Unit>

    /**
     * Удаление уведомления
     */
    suspend fun deleteNotification(notificationId: String): Result<Unit>

    /**
     * Очистка всех уведомлений
     */
    suspend fun clearAllNotifications(): Result<Unit>

    /**
     * Планирование локального уведомления
     */
    suspend fun scheduleLocalNotification(
        id: String,
        title: String,
        body: String,
        type: NotificationType,
        scheduledTime: Long,
        contentId: String? = null
    ): Result<Unit>

    /**
     * Отмена запланированного уведомления
     */
    suspend fun cancelScheduledNotification(id: String): Result<Unit>

    /**
     * Планирование ежедневного факта
     */
    suspend fun scheduleDailyFact(hourOfDay: Int, minute: Int): Result<Unit>

    /**
     * Отмена ежедневного факта
     */
    suspend fun cancelDailyFact(): Result<Unit>

    /**
     * Планирование напоминания о неактивности
     */
    suspend fun scheduleInactivityReminder(daysInactive: Int): Result<Unit>

    /**
     * Отмена напоминания о неактивности
     */
    suspend fun cancelInactivityReminder(): Result<Unit>

    /**
     * Проверка разрешений на уведомления
     */
    suspend fun checkNotificationPermission(): Boolean

    /**
     * Запрос разрешения на уведомления
     */
    suspend fun requestNotificationPermission(): Boolean
}