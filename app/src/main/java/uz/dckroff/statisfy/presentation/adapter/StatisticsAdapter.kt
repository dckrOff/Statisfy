package uz.dckroff.statisfy.presentation.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.dckroff.statisfy.R
import uz.dckroff.statisfy.databinding.ItemStatisticsHeaderBinding
import uz.dckroff.statisfy.databinding.ItemStatisticsRecordBinding
import uz.dckroff.statisfy.databinding.ItemStatisticsCategoryFilterBinding
import uz.dckroff.statisfy.databinding.ItemStatisticsEmptyBinding
import uz.dckroff.statisfy.databinding.ItemStatisticsErrorBinding
import uz.dckroff.statisfy.databinding.ItemStatisticsLoadingBinding
import uz.dckroff.statisfy.domain.model.Statistic
import uz.dckroff.statisfy.presentation.ui.statistics.StatisticsItem
import uz.dckroff.statisfy.utils.Logger

/**
 * Адаптер для отображения глобальной статистики
 */
class StatisticsAdapter(
    private val onStatisticClick: (Statistic) -> Unit = {},
    private val onCategoryFilterClick: (uz.dckroff.statisfy.domain.model.Category?) -> Unit = {},
    private val onShareClick: (Statistic) -> Unit = {}
) : ListAdapter<StatisticsItem, StatisticsAdapter.StatisticsViewHolder>(StatisticsDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_STATISTIC_RECORD = 1
        private const val VIEW_TYPE_CATEGORY_FILTER = 2
        private const val VIEW_TYPE_LOADING = 3
        private const val VIEW_TYPE_ERROR = 4
        private const val VIEW_TYPE_EMPTY = 5
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is StatisticsItem.Header -> VIEW_TYPE_HEADER
            is StatisticsItem.StatisticRecord -> VIEW_TYPE_STATISTIC_RECORD
            is StatisticsItem.CategoryFilter -> VIEW_TYPE_CATEGORY_FILTER
            is StatisticsItem.Loading -> VIEW_TYPE_LOADING
            is StatisticsItem.Error -> VIEW_TYPE_ERROR
            is StatisticsItem.Empty -> VIEW_TYPE_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemStatisticsHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding)
            }

            VIEW_TYPE_STATISTIC_RECORD -> {
                val binding = ItemStatisticsRecordBinding.inflate(inflater, parent, false)
                StatisticRecordViewHolder(binding, onStatisticClick, onShareClick)
            }

            VIEW_TYPE_CATEGORY_FILTER -> {
                val binding = ItemStatisticsCategoryFilterBinding.inflate(inflater, parent, false)
                CategoryFilterViewHolder(binding, onCategoryFilterClick)
            }

            VIEW_TYPE_LOADING -> {
                val binding = ItemStatisticsLoadingBinding.inflate(inflater, parent, false)
                LoadingViewHolder(binding)
            }

            VIEW_TYPE_ERROR -> {
                val binding = ItemStatisticsErrorBinding.inflate(inflater, parent, false)
                ErrorViewHolder(binding)
            }

            VIEW_TYPE_EMPTY -> {
                val binding = ItemStatisticsEmptyBinding.inflate(inflater, parent, false)
                EmptyViewHolder(binding)
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
    abstract class StatisticsViewHolder(itemView: android.view.View) :
        RecyclerView.ViewHolder(itemView) {
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
     * ViewHolder для статистической записи
     */
    class StatisticRecordViewHolder(
        private val binding: ItemStatisticsRecordBinding,
        private val onStatisticClick: (Statistic) -> Unit,
        private val onShareClick: (Statistic) -> Unit
    ) : StatisticsViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.O)
        override fun bind(item: StatisticsItem) {
            if (item is StatisticsItem.StatisticRecord) {
                binding.apply {
                    textViewTitle.text = item.title
                    textViewValue.text = "${item.value} ${item.unit}"
                    textViewCategory.text = item.category.name
                    textViewSource.text = item.source
                    textViewDate.text = item.date

                    val statistic = Statistic(
                        id = item.id,
                        title = item.title,
                        value = item.value,
                        unit = item.unit,
                        category = item.category,
                        source = item.source,
                        date = try {
                            java.time.LocalDate.parse(item.date)
                        } catch (e: Exception) {
                            java.time.LocalDate.now()
                        }
                    )

                    root.setOnClickListener {
                        onStatisticClick(statistic)
                    }

                    buttonShare.setOnClickListener {
                        onShareClick(statistic)
                    }
                }
            }
        }
    }

    /**
     * ViewHolder для фильтра категорий
     */
    class CategoryFilterViewHolder(
        private val binding: ItemStatisticsCategoryFilterBinding,
        private val onCategoryFilterClick: (uz.dckroff.statisfy.domain.model.Category?) -> Unit
    ) : StatisticsViewHolder(binding.root) {

        override fun bind(item: StatisticsItem) {
            if (item is StatisticsItem.CategoryFilter) {
                binding.apply {
                    textViewCategoryName.text = item.category.name
                    textViewCount.text = "(${item.statisticsCount})"
                    root.isSelected = item.isSelected

                    root.setOnClickListener {
                        onCategoryFilterClick(item.category)
                    }
                }
            }
        }
    }

    /**
     * ViewHolder для загрузки
     */
    class LoadingViewHolder(
        private val binding: ItemStatisticsLoadingBinding
    ) : StatisticsViewHolder(binding.root) {

        override fun bind(item: StatisticsItem) {
            // Анимация загрузки уже в layout
        }
    }

    /**
     * ViewHolder для ошибки
     */
    class ErrorViewHolder(
        private val binding: ItemStatisticsErrorBinding
    ) : StatisticsViewHolder(binding.root) {

        override fun bind(item: StatisticsItem) {
            if (item is StatisticsItem.Error) {
                binding.textViewError.text = item.message
            }
        }
    }

    /**
     * ViewHolder для пустого состояния
     */
    class EmptyViewHolder(
        private val binding: ItemStatisticsEmptyBinding
    ) : StatisticsViewHolder(binding.root) {

        override fun bind(item: StatisticsItem) {
            if (item is StatisticsItem.Empty) {
                binding.textViewEmpty.text = item.message
            }
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

            oldItem is StatisticsItem.StatisticRecord && newItem is StatisticsItem.StatisticRecord -> {
                oldItem.id == newItem.id
            }

            oldItem is StatisticsItem.CategoryFilter && newItem is StatisticsItem.CategoryFilter -> {
                oldItem.category.id == newItem.category.id
            }

            oldItem is StatisticsItem.Loading && newItem is StatisticsItem.Loading -> true
            oldItem is StatisticsItem.Error && newItem is StatisticsItem.Error -> true
            oldItem is StatisticsItem.Empty && newItem is StatisticsItem.Empty -> true
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: StatisticsItem, newItem: StatisticsItem): Boolean {
        return oldItem == newItem
    }
}