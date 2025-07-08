package uz.dckroff.statisfy.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import uz.dckroff.statisfy.MainActivity
import uz.dckroff.statisfy.R
import uz.dckroff.statisfy.domain.repository.NotificationRepository
import uz.dckroff.statisfy.utils.Logger
import javax.inject.Inject

/**
 * Сервис для обработки Firebase Cloud Messaging уведомлений
 */
@AndroidEntryPoint
class StatisfyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationRepository: NotificationRepository

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Logger.d("FCM", "Message received from: ${remoteMessage.from}")
        
        // Получаем данные уведомления
        val notificationData = parseNotificationData(remoteMessage)
        
        // Показываем уведомление
        showNotification(notificationData)
        
        // Сохраняем уведомление в базу данных
        saveNotificationToDatabase(notificationData)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Logger.d("FCM", "New token: $token")
        
        // Отправляем новый токен на сервер
        sendTokenToServer(token)
    }

    /**
     * Парсинг данных уведомления
     */
    private fun parseNotificationData(remoteMessage: RemoteMessage): NotificationData {
        val data = remoteMessage.data
        val notification = remoteMessage.notification

        return NotificationData(
            id = data["id"] ?: System.currentTimeMillis().toString(),
            title = notification?.title ?: data["title"] ?: "Statisfy",
            body = notification?.body ?: data["body"] ?: "",
            imageUrl = notification?.imageUrl?.toString() ?: data["imageUrl"],
            type = NotificationType.fromString(data["type"] ?: "general"),
            contentId = data["contentId"],
            deepLink = data["deepLink"],
            largeIcon = data["largeIcon"],
            actions = parseActions(data["actions"])
        )
    }

    /**
     * Показ уведомления
     */
    private fun showNotification(notificationData: NotificationData) {
        val channelId = getNotificationChannelId(notificationData.type)
        
        // Создаем канал уведомлений (для Android 8.0+)
        createNotificationChannel(channelId, notificationData.type)
        
        // Создаем Intent для открытия приложения
        val intent = createNotificationIntent(notificationData)
        val pendingIntent = PendingIntent.getActivity(
            this, 
            notificationData.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Строим уведомление
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notificationData.title)
            .setContentText(notificationData.body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(getNotificationPriority(notificationData.type))
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationData.body))
        
        // Добавляем изображение если есть
        if (!notificationData.imageUrl.isNullOrEmpty()) {
            // TODO: Загрузить изображение и добавить в уведомление
        }
        
        // Добавляем действия
        notificationData.actions.forEachIndexed { index, action ->
            val actionIntent = createActionIntent(action, notificationData)
            val actionPendingIntent = PendingIntent.getBroadcast(
                this,
                (notificationData.id + action.key).hashCode(),
                actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            notificationBuilder.addAction(
                action.icon,
                action.title,
                actionPendingIntent
            )
        }
        
        // Показываем уведомление
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationData.id.hashCode(), notificationBuilder.build())
    }

    /**
     * Создание канала уведомлений
     */
    private fun createNotificationChannel(channelId: String, type: NotificationType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getChannelName(type),
                getChannelImportance(type)
            ).apply {
                description = getChannelDescription(type)
                enableLights(true)
                enableVibration(true)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Создание Intent для уведомления
     */
    private fun createNotificationIntent(notificationData: NotificationData): Intent {
        return Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notification_id", notificationData.id)
            putExtra("notification_type", notificationData.type.name)
            putExtra("content_id", notificationData.contentId)
            putExtra("deep_link", notificationData.deepLink)
        }
    }

    /**
     * Создание Intent для действий уведомления
     */
    private fun createActionIntent(action: NotificationAction, notificationData: NotificationData): Intent {
        return Intent("uz.dckroff.statisfy.NOTIFICATION_ACTION").apply {
            putExtra("action_key", action.key)
            putExtra("notification_id", notificationData.id)
            putExtra("content_id", notificationData.contentId)
        }
    }

    /**
     * Получение ID канала уведомлений
     */
    private fun getNotificationChannelId(type: NotificationType): String {
        return when (type) {
            NotificationType.DAILY_FACT -> "daily_facts"
            NotificationType.NEWS_UPDATE -> "news_updates"
            NotificationType.RECOMMENDATIONS -> "recommendations"
            NotificationType.REMINDER -> "reminders"
            NotificationType.GENERAL -> "general"
        }
    }

    /**
     * Получение имени канала
     */
    private fun getChannelName(type: NotificationType): String {
        return when (type) {
            NotificationType.DAILY_FACT -> "Факт дня"
            NotificationType.NEWS_UPDATE -> "Новости"
            NotificationType.RECOMMENDATIONS -> "Рекомендации"
            NotificationType.REMINDER -> "Напоминания"
            NotificationType.GENERAL -> "Общие уведомления"
        }
    }

    /**
     * Получение описания канала
     */
    private fun getChannelDescription(type: NotificationType): String {
        return when (type) {
            NotificationType.DAILY_FACT -> "Ежедневные интересные факты"
            NotificationType.NEWS_UPDATE -> "Новые интересные новости"
            NotificationType.RECOMMENDATIONS -> "Персонализированные рекомендации"
            NotificationType.REMINDER -> "Напоминания о чтении фактов"
            NotificationType.GENERAL -> "Общие уведомления приложения"
        }
    }

    /**
     * Получение важности канала
     */
    private fun getChannelImportance(type: NotificationType): Int {
        return when (type) {
            NotificationType.DAILY_FACT -> NotificationManager.IMPORTANCE_DEFAULT
            NotificationType.NEWS_UPDATE -> NotificationManager.IMPORTANCE_DEFAULT
            NotificationType.RECOMMENDATIONS -> NotificationManager.IMPORTANCE_LOW
            NotificationType.REMINDER -> NotificationManager.IMPORTANCE_HIGH
            NotificationType.GENERAL -> NotificationManager.IMPORTANCE_DEFAULT
        }
    }

    /**
     * Получение приоритета уведомления
     */
    private fun getNotificationPriority(type: NotificationType): Int {
        return when (type) {
            NotificationType.DAILY_FACT -> NotificationCompat.PRIORITY_DEFAULT
            NotificationType.NEWS_UPDATE -> NotificationCompat.PRIORITY_DEFAULT
            NotificationType.RECOMMENDATIONS -> NotificationCompat.PRIORITY_LOW
            NotificationType.REMINDER -> NotificationCompat.PRIORITY_HIGH
            NotificationType.GENERAL -> NotificationCompat.PRIORITY_DEFAULT
        }
    }

    /**
     * Парсинг действий уведомления
     */
    private fun parseActions(actionsJson: String?): List<NotificationAction> {
        if (actionsJson.isNullOrEmpty()) return emptyList()
        
        // TODO: Парсинг JSON с действиями
        return emptyList()
    }

    /**
     * Сохранение уведомления в базу данных
     */
    private fun saveNotificationToDatabase(notificationData: NotificationData) {
        // TODO: Реализовать сохранение через repository
    }

    /**
     * Отправка токена на сервер
     */
    private fun sendTokenToServer(token: String) {
        // TODO: Реализовать отправку токена через repository
    }
}

/**
 * Типы уведомлений
 */
enum class NotificationType {
    DAILY_FACT,
    NEWS_UPDATE,
    RECOMMENDATIONS,
    REMINDER,
    GENERAL;

    companion object {
        fun fromString(value: String): NotificationType {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: GENERAL
        }
    }
}

/**
 * Модель данных уведомления
 */
data class NotificationData(
    val id: String,
    val title: String,
    val body: String,
    val imageUrl: String? = null,
    val type: NotificationType = NotificationType.GENERAL,
    val contentId: String? = null,
    val deepLink: String? = null,
    val largeIcon: String? = null,
    val actions: List<NotificationAction> = emptyList()
)

/**
 * Действие в уведомлении
 */
data class NotificationAction(
    val key: String,
    val title: String,
    val icon: Int = 0
)