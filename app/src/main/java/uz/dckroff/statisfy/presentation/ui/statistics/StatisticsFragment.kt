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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.R
import uz.dckroff.statisfy.databinding.FragmentStatisticsBinding
import uz.dckroff.statisfy.domain.model.Statistic
import uz.dckroff.statisfy.domain.model.Category
import uz.dckroff.statisfy.presentation.adapter.StatisticsAdapter
import uz.dckroff.statisfy.presentation.viewmodel.StatisticsEvent
import uz.dckroff.statisfy.presentation.viewmodel.StatisticsViewModel
import uz.dckroff.statisfy.presentation.viewmodel.StatisticsEffect
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.gone
import uz.dckroff.statisfy.utils.visible

/**
 * Фрагмент для отображения глобальной статистики
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
        setupSearch()
    }
    
    /**
     * Настройка views
     */
    private fun setupViews() {
        Logger.d("StatisticsFragment: Setting up views")
        
        // Настройка SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            Logger.d("StatisticsFragment: Pull to refresh triggered")
            viewModel.handleEvent(StatisticsEvent.RefreshStatistics)
        }
    }
    
    /**
     * Настройка RecyclerView
     */
    private fun setupRecyclerView() {
        Logger.d("StatisticsFragment: Setting up RecyclerView")
        
        statisticsAdapter = StatisticsAdapter(
            onStatisticClick = { statistic ->
                Logger.d("StatisticsFragment: Statistic clicked: ${statistic.title}")
                viewModel.handleEvent(StatisticsEvent.SelectStatistic(statistic))
            },
            onCategoryFilterClick = { category ->
                Logger.d("StatisticsFragment: Category filter clicked: ${category?.name}")
                viewModel.handleEvent(StatisticsEvent.FilterByCategory(category))
            },
            onShareClick = { statistic ->
                Logger.d("StatisticsFragment: Share clicked: ${statistic.title}")
                viewModel.handleEvent(StatisticsEvent.ShareStatistic(statistic))
            }
        )
        
        binding.recyclerViewStatistics.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = statisticsAdapter
        }
    }
    
    /**
     * Настройка поиска
     */
    private fun setupSearch() {
        Logger.d("StatisticsFragment: Setting up search")
        
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { 
                    viewModel.handleEvent(StatisticsEvent.SearchStatistics(it))
                }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { 
                    viewModel.handleEvent(StatisticsEvent.SearchStatistics(it))
                }
                return true
            }
        })
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
        
        // Кнопка очистить фильтр
        binding.buttonClearFilter.setOnClickListener {
            Logger.d("StatisticsFragment: Clear filter button clicked")
            viewModel.handleEvent(StatisticsEvent.ClearFilter)
        }
    }
    
    /**
     * Обновление UI на основе состояния
     */
    private fun updateUI(state: uz.dckroff.statisfy.presentation.viewmodel.StatisticsUiState) {
        Logger.d("StatisticsFragment: Updating UI")
        
        // Обновляем SwipeRefreshLayout
        binding.swipeRefreshLayout.isRefreshing = state.isLoading
        
        // Обновляем видимость кнопки очистки фильтра
        binding.buttonClearFilter.visibility = if (state.selectedCategory != null || state.searchQuery.isNotEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
        
        // Обновляем заголовок в зависимости от фильтра
        binding.textViewTitle.text = when {
            state.selectedCategory != null -> "Статистика: ${state.selectedCategory.name}"
            state.searchQuery.isNotEmpty() -> "Поиск: ${state.searchQuery}"
            else -> getString(R.string.stats_title)
        }
        
        when {
            state.isLoading && state.filteredStatistics.isEmpty() -> {
                showLoading()
            }
            state.error != null -> {
                showError(state.error)
            }
            state.filteredStatistics.isEmpty() -> {
                showEmpty()
            }
            else -> {
                showStatistics(state)
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
        }
    }
    
    /**
     * Отображение статистики
     */
    private fun showStatistics(state: uz.dckroff.statisfy.presentation.viewmodel.StatisticsUiState) {
        Logger.d("StatisticsFragment: Showing statistics")
        
        binding.apply {
            progressBar.gone()
            layoutError.gone()
            layoutEmpty.gone()
            recyclerViewStatistics.visible()
        }
        
        // Создаем список элементов для отображения
        val items = buildStatisticsItems(state)
        statisticsAdapter.updateData(items)
    }
    
    /**
     * Создание элементов для отображения
     */
    private fun buildStatisticsItems(state: uz.dckroff.statisfy.presentation.viewmodel.StatisticsUiState): List<StatisticsItem> {
        Logger.d("StatisticsFragment: Building statistics items")
        
        val items = mutableListOf<StatisticsItem>()
        
        // Добавляем фильтры по категориям, если нет активного поиска
        if (state.searchQuery.isEmpty() && state.categories.isNotEmpty()) {
            items.add(StatisticsItem.Header("Фильтр по категориям"))
            
            // Добавляем кнопку "Все"
            items.add(StatisticsItem.CategoryFilter(
                category = Category("all", "Все"),
                statisticsCount = state.allStatistics.size,
                isSelected = state.selectedCategory == null
            ))
            
            state.categories.forEach { category ->
                val countInCategory = state.allStatistics.count { it.category.id == category.id }
                items.add(StatisticsItem.CategoryFilter(
                    category = category,
                    statisticsCount = countInCategory,
                    isSelected = state.selectedCategory?.id == category.id
                ))
            }
        }
        
        // Добавляем статистические данные
        if (state.filteredStatistics.isNotEmpty()) {
            val headerText = when {
                state.selectedCategory != null -> "Статистика по категории: ${state.selectedCategory.name}"
                state.searchQuery.isNotEmpty() -> "Результаты поиска"
                else -> "Глобальная статистика"
            }
            
            items.add(StatisticsItem.Header(headerText))
            
            state.filteredStatistics.forEach { statistic ->
                items.add(StatisticsItem.StatisticRecord(
                    id = statistic.id,
                    title = statistic.title,
                    value = statistic.value,
                    unit = statistic.unit,
                    category = statistic.category,
                    source = statistic.source,
                    date = statistic.date.toString()
                ))
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
            is StatisticsEffect.ShareStatistic -> {
                shareStatistic(effect.text)
            }
            is StatisticsEffect.ShowMessage -> {
                // Показать Toast или Snackbar
                android.widget.Toast.makeText(requireContext(), effect.message, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * Поделиться статистикой
     */
    private fun shareStatistic(text: String) {
        Logger.d("StatisticsFragment: Sharing statistic")
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.stats_title))
        }
        
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
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