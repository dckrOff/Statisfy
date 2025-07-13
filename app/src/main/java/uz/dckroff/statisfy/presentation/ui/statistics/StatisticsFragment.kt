package uz.dckroff.statisfy.presentation.ui.statistics

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.R
import uz.dckroff.statisfy.databinding.FragmentStatisticsBinding
import uz.dckroff.statisfy.domain.model.UserStats
import uz.dckroff.statisfy.presentation.adapter.StatisticsAdapter
import uz.dckroff.statisfy.presentation.viewmodel.StatisticsEvent
import uz.dckroff.statisfy.presentation.viewmodel.StatisticsViewModel
import uz.dckroff.statisfy.presentation.viewmodel.StatisticsEffect
import uz.dckroff.statisfy.presentation.viewmodel.TimePeriod
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.gone
import uz.dckroff.statisfy.utils.visible

/**
 * Фрагмент для отображения статистики пользователя
 */
@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: StatisticsViewModel by viewModels()
    private lateinit var statisticsAdapter: StatisticsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Logger.d("StatisticsFragment: onCreateView")
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.d("StatisticsFragment: onViewCreated")
        
        setupViews()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }
    
    /**
     * Настройка views
     */
    private fun setupViews() {
        Logger.d("StatisticsFragment: Setting up views")
        
        // Настройка chip группы для выбора периода
        setupTimePeriodChips()
        
        // Настройка SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            Logger.d("StatisticsFragment: Pull to refresh triggered")
            viewModel.handleEvent(StatisticsEvent.RefreshStats)
        }
    }
    
    /**
     * Настройка RecyclerView
     */
    private fun setupRecyclerView() {
        Logger.d("StatisticsFragment: Setting up RecyclerView")
        
        statisticsAdapter = StatisticsAdapter { statisticItem ->
            Logger.d("StatisticsFragment: Statistic item clicked: ${statisticItem.title}")
            // Можно добавить обработку клика на элемент статистики
        }
        
        binding.recyclerViewStatistics.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = statisticsAdapter
        }
    }
    
    /**
     * Настройка наблюдателей
     */
    private fun setupObservers() {
        Logger.d("StatisticsFragment: Setting up observers")
        
        // Наблюдаем за состоянием UI
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                Logger.d("StatisticsFragment: UI state updated - loading: ${state.isLoading}, error: ${state.error}")
                updateUI(state)
            }
        }
        
        // Наблюдаем за эффектами
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.effect.collectLatest { effect ->
                effect?.let { 
                    Logger.d("StatisticsFragment: Effect received: ${effect::class.simpleName}")
                    handleEffect(it)
                    viewModel.clearEffect()
                }
            }
        }
    }
    
    /**
     * Настройка обработчиков кликов
     */
    private fun setupClickListeners() {
        Logger.d("StatisticsFragment: Setting up click listeners")
        
        // Кнопка повторить
        binding.buttonRetry.setOnClickListener {
            Logger.d("StatisticsFragment: Retry button clicked")
            viewModel.handleEvent(StatisticsEvent.RetryLoad)
        }
        
        // Кнопка поделиться
        binding.buttonShare.setOnClickListener {
            Logger.d("StatisticsFragment: Share button clicked")
            viewModel.handleEvent(StatisticsEvent.ShareStats)
        }
        
        // Кнопка экспорта
        binding.buttonExport.setOnClickListener {
            Logger.d("StatisticsFragment: Export button clicked")
            viewModel.handleEvent(StatisticsEvent.ExportStats)
        }
    }
    
    /**
     * Настройка чипов для выбора периода времени
     */
    private fun setupTimePeriodChips() {
        Logger.d("StatisticsFragment: Setting up time period chips")
        
        TimePeriod.values().forEach { period ->
            val chip = Chip(requireContext()).apply {
                text = period.displayName
                isCheckable = true
                isChecked = period == TimePeriod.ALL_TIME
                
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        Logger.d("StatisticsFragment: Time period selected: $period")
                        viewModel.handleEvent(StatisticsEvent.SelectTimePeriod(period))
                    }
                }
            }
            binding.chipGroupTimePeriod.addView(chip)
        }
    }
    
    /**
     * Обновление UI на основе состояния
     */
    private fun updateUI(state: uz.dckroff.statisfy.presentation.viewmodel.StatisticsUiState) {
        Logger.d("StatisticsFragment: Updating UI")
        
        // Обновляем SwipeRefreshLayout
        binding.swipeRefreshLayout.isRefreshing = state.isLoading
        
        when {
            state.isLoading -> {
                showLoading()
            }
            state.error != null -> {
                showError(state.error)
            }
            state.userStats != null -> {
                showStats(state.userStats)
            }
            else -> {
                showEmpty()
            }
        }
    }
    
    /**
     * Отображение состояния загрузки
     */
    private fun showLoading() {
        Logger.d("StatisticsFragment: Showing loading state")
        
        binding.apply {
            progressBar.visible()
            layoutError.gone()
            layoutEmpty.gone()
            recyclerViewStatistics.gone()
            layoutStats.gone()
        }
    }
    
    /**
     * Отображение ошибки
     */
    private fun showError(error: String) {
        Logger.e("StatisticsFragment: Showing error state: $error")
        
        binding.apply {
            progressBar.gone()
            layoutError.visible()
            layoutEmpty.gone()
            recyclerViewStatistics.gone()
            layoutStats.gone()
            
            textViewError.text = error
        }
    }
    
    /**
     * Отображение пустого состояния
     */
    private fun showEmpty() {
        Logger.d("StatisticsFragment: Showing empty state")
        
        binding.apply {
            progressBar.gone()
            layoutError.gone()
            layoutEmpty.visible()
            recyclerViewStatistics.gone()
            layoutStats.gone()
        }
    }
    
    /**
     * Отображение статистики
     */
    private fun showStats(userStats: UserStats) {
        Logger.d("StatisticsFragment: Showing stats")
        
        binding.apply {
            progressBar.gone()
            layoutError.gone()
            layoutEmpty.gone()
            recyclerViewStatistics.visible()
            layoutStats.visible()
        }
        
        // Обновляем основные карточки статистики
        updateStatsCards(userStats)
        
        // Обновляем список детальной статистики
        updateStatsList(userStats)
    }
    
    /**
     * Обновление карточек статистики
     */
    private fun updateStatsCards(userStats: UserStats) {
        Logger.d("StatisticsFragment: Updating stats cards")
        
        binding.apply {
            // Общая статистика
            textViewTotalItemsRead.text = userStats.overallStats.totalItemsRead.toString()
            textViewTotalTimeSpent.text = getString(R.string.stats_minutes, userStats.overallStats.totalTimeSpent)
            textViewCurrentLevel.text = userStats.overallStats.currentLevel.toString()
            textViewDaysActive.text = userStats.overallStats.daysActive.toString()
            
            // Статистика чтения
            textViewFactsRead.text = userStats.readingStats.factsRead.toString()
            textViewNewsRead.text = userStats.readingStats.newsRead.toString()
            textViewAverageReadingTime.text = getString(R.string.stats_minutes, userStats.readingStats.averageReadingTime.toInt())
            
            // Статистика серий
            textViewCurrentStreak.text = userStats.streakStats.currentStreak.toString()
            textViewLongestStreak.text = userStats.streakStats.longestStreak.toString()
            
            // Избранное
            textViewTotalFavorites.text = userStats.favoriteStats.totalFavorites.toString()
            
            // Достижения
            textViewAchievements.text = userStats.achievements.size.toString()
            
            // Прогресс опыта
            progressBarExperience.progress = userStats.overallStats.experiencePoints
            progressBarExperience.max = userStats.overallStats.nextLevelXp
            textViewExperienceProgress.text = "${userStats.overallStats.experiencePoints}/${userStats.overallStats.nextLevelXp}"
        }
    }
    
    /**
     * Обновление списка детальной статистики
     */
    private fun updateStatsList(userStats: UserStats) {
        Logger.d("StatisticsFragment: Updating stats list")
        
        val statisticsItems = buildStatisticsItems(userStats)
        statisticsAdapter.updateData(statisticsItems)
    }
    
    /**
     * Создание элементов для списка статистики
     */
    private fun buildStatisticsItems(userStats: UserStats): List<StatisticsItem> {
        Logger.d("StatisticsFragment: Building statistics items")
        
        val items = mutableListOf<StatisticsItem>()
        
        // Категории
        if (userStats.categoryStats.isNotEmpty()) {
            items.add(StatisticsItem.Header(getString(R.string.stats_categories)))
            userStats.categoryStats.forEach { categoryStats ->
                items.add(
                    StatisticsItem.Category(
                        id = categoryStats.categoryId,
                        title = categoryStats.categoryName,
                        itemsRead = categoryStats.itemsRead,
                        timeSpent = categoryStats.timeSpent,
                        progressPercentage = categoryStats.progressPercentage
                    )
                )
            }
        }
        
        // Месячная активность
        if (userStats.monthlyActivity.isNotEmpty()) {
            items.add(StatisticsItem.Header(getString(R.string.stats_monthly)))
            userStats.monthlyActivity.take(6).forEach { monthlyActivity ->
                items.add(
                    StatisticsItem.Monthly(
                        month = monthlyActivity.month,
                        itemsRead = monthlyActivity.itemsRead,
                        timeSpent = monthlyActivity.timeSpent,
                        daysActive = monthlyActivity.daysActive
                    )
                )
            }
        }
        
        // Достижения
        if (userStats.achievements.isNotEmpty()) {
            items.add(StatisticsItem.Header(getString(R.string.stats_achievements)))
            userStats.achievements.take(10).forEach { achievement ->
                items.add(
                    StatisticsItem.Achievement(
                        id = achievement.id,
                        title = achievement.title,
                        description = achievement.description,
                        iconUrl = achievement.iconUrl,
                        unlockedAt = achievement.unlockedAt,
                        rarity = achievement.rarity
                    )
                )
            }
        }
        
        return items
    }
    
    /**
     * Обработка эффектов
     */
    private fun handleEffect(effect: StatisticsEffect) {
        Logger.d("StatisticsFragment: Handling effect: ${effect::class.simpleName}")
        
        when (effect) {
            is StatisticsEffect.ShareStats -> {
                shareStats(effect.text)
            }
            is StatisticsEffect.ExportStats -> {
                exportStats(effect.stats)
            }
            is StatisticsEffect.ShowMessage -> {
                // Показать сообщение (можно использовать Snackbar)
            }
        }
    }
    
    /**
     * Поделиться статистикой
     */
    private fun shareStats(text: String) {
        Logger.d("StatisticsFragment: Sharing stats")
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.stats_title))
        }
        
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
    }
    
    /**
     * Экспорт статистики
     */
    private fun exportStats(stats: UserStats) {
        Logger.d("StatisticsFragment: Exporting stats")
        // Здесь можно реализовать экспорт в файл
        // Пока что просто показываем, что функция вызвана
    }
    
    override fun onStart() {
        super.onStart()
        Logger.lifecycle("StatisticsFragment", "onStart")
    }
    
    override fun onResume() {
        super.onResume()
        Logger.lifecycle("StatisticsFragment", "onResume")
    }
    
    override fun onPause() {
        super.onPause()
        Logger.lifecycle("StatisticsFragment", "onPause")
    }
    
    override fun onStop() {
        super.onStop()
        Logger.lifecycle("StatisticsFragment", "onStop")
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        Logger.lifecycle("StatisticsFragment", "onDestroyView")
        _binding = null
    }
}