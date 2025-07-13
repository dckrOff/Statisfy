package uz.dckroff.statisfy.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.dckroff.statisfy.R
import uz.dckroff.statisfy.databinding.ItemStatisticsAchievementBinding
import uz.dckroff.statisfy.databinding.ItemStatisticsCategoryBinding
import uz.dckroff.statisfy.databinding.ItemStatisticsHeaderBinding
import uz.dckroff.statisfy.databinding.ItemStatisticsMonthlyBinding
import uz.dckroff.statisfy.presentation.ui.statistics.StatisticsItem
import uz.dckroff.statisfy.utils.Logger

/**
 * Адаптер для отображения статистики
 */
class StatisticsAdapter(
    private val onItemClick: (StatisticsItem) -> Unit = {}
) : ListAdapter<StatisticsItem, StatisticsAdapter.StatisticsViewHolder>(StatisticsDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_CATEGORY = 1
        private const val VIEW_TYPE_MONTHLY = 2
        private const val VIEW_TYPE_ACHIEVEMENT = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is StatisticsItem.Header -> VIEW_TYPE_HEADER
            is StatisticsItem.Category -> VIEW_TYPE_CATEGORY
            is StatisticsItem.Monthly -> VIEW_TYPE_MONTHLY
            is StatisticsItem.Achievement -> VIEW_TYPE_ACHIEVEMENT
            else -> throw IllegalArgumentException("Unknown item type at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemStatisticsHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding)
            }
            VIEW_TYPE_CATEGORY -> {
                val binding = ItemStatisticsCategoryBinding.inflate(inflater, parent, false)
                CategoryViewHolder(binding, onItemClick)
            }
            VIEW_TYPE_MONTHLY -> {
                val binding = ItemStatisticsMonthlyBinding.inflate(inflater, parent, false)
                MonthlyViewHolder(binding, onItemClick)
            }
            VIEW_TYPE_ACHIEVEMENT -> {
                val binding = ItemStatisticsAchievementBinding.inflate(inflater, parent, false)
                AchievementViewHolder(binding, onItemClick)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    /**
     * Обновление данных
     */
    fun updateData(newItems: List<StatisticsItem>) {
        Logger.d("StatisticsAdapter: Updating data with ${newItems.size} items")
        submitList(newItems)
    }

    /**
     * Базовый ViewHolder для статистики
     */
    abstract class StatisticsViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: StatisticsItem)
    }

    /**
     * ViewHolder для заголовка
     */
    class HeaderViewHolder(
        private val binding: ItemStatisticsHeaderBinding
    ) : StatisticsViewHolder(binding.root) {
        
        override fun bind(item: StatisticsItem) {
            if (item is StatisticsItem.Header) {
                binding.textViewHeader.text = item.title
            }
        }
    }

    /**
     * ViewHolder для категории
     */
    class CategoryViewHolder(
        private val binding: ItemStatisticsCategoryBinding,
        private val onItemClick: (StatisticsItem) -> Unit
    ) : StatisticsViewHolder(binding.root) {
        
        override fun bind(item: StatisticsItem) {
            if (item is StatisticsItem.Category) {
                binding.apply {
                    textViewCategoryName.text = item.title
                    textViewItemsRead.text = "${item.itemsRead}"
                    textViewTimeSpent.text = "${item.timeSpent} мин"
                    progressBarCategory.progress = item.progressPercentage.toInt()
                    textViewProgress.text = "${item.progressPercentage.toInt()}%"
                    
                    root.setOnClickListener {
                        onItemClick(item)
                    }
                }
            }
        }
    }

    /**
     * ViewHolder для месячной активности
     */
    class MonthlyViewHolder(
        private val binding: ItemStatisticsMonthlyBinding,
        private val onItemClick: (StatisticsItem) -> Unit
    ) : StatisticsViewHolder(binding.root) {
        
        override fun bind(item: StatisticsItem) {
            if (item is StatisticsItem.Monthly) {
                binding.apply {
                    textViewMonth.text = formatMonth(item.month)
                    textViewItemsRead.text = "${item.itemsRead}"
                    textViewTimeSpent.text = "${item.timeSpent} мин"
                    textViewDaysActive.text = "${item.daysActive}"
                    
                    root.setOnClickListener {
                        onItemClick(item)
                    }
                }
            }
        }
        
        private fun formatMonth(month: String): String {
            // Преобразуем YYYY-MM в более читаемый формат
            val parts = month.split("-")
            if (parts.size == 2) {
                val year = parts[0]
                val monthNum = parts[1].toIntOrNull() ?: 1
                val monthNames = arrayOf(
                    "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
                )
                return "${monthNames[monthNum - 1]} $year"
            }
            return month
        }
    }

    /**
     * ViewHolder для достижения
     */
    class AchievementViewHolder(
        private val binding: ItemStatisticsAchievementBinding,
        private val onItemClick: (StatisticsItem) -> Unit
    ) : StatisticsViewHolder(binding.root) {
        
        override fun bind(item: StatisticsItem) {
            if (item is StatisticsItem.Achievement) {
                binding.apply {
                    textViewTitle.text = item.title
                    textViewDescription.text = item.description
                    textViewUnlockedAt.text = "Получено: ${formatDate(item.unlockedAt)}"
                    
                    // Устанавливаем цвет рамки в зависимости от редкости
                    val rarityColor = android.graphics.Color.parseColor(item.rarity.color)
                    cardViewAchievement.strokeColor = rarityColor
                    
                    // Устанавливаем иконку (если есть)
                    // TODO: Загрузить иконку из iconUrl если она есть
                    
                    root.setOnClickListener {
                        onItemClick(item)
                    }
                }
            }
        }
        
        private fun formatDate(dateString: String): String {
            // Здесь можно добавить форматирование даты
            return dateString
        }
    }
}

/**
 * DiffCallback для оптимизации обновлений списка
 */
class StatisticsDiffCallback : DiffUtil.ItemCallback<StatisticsItem>() {
    
    override fun areItemsTheSame(oldItem: StatisticsItem, newItem: StatisticsItem): Boolean {
        return when {
            oldItem is StatisticsItem.Header && newItem is StatisticsItem.Header -> {
                oldItem.title == newItem.title
            }
            oldItem is StatisticsItem.Category && newItem is StatisticsItem.Category -> {
                oldItem.id == newItem.id
            }
            oldItem is StatisticsItem.Monthly && newItem is StatisticsItem.Monthly -> {
                oldItem.month == newItem.month
            }
            oldItem is StatisticsItem.Achievement && newItem is StatisticsItem.Achievement -> {
                oldItem.id == newItem.id
            }
            else -> false
        }
    }
    
    override fun areContentsTheSame(oldItem: StatisticsItem, newItem: StatisticsItem): Boolean {
        return oldItem == newItem
    }
}