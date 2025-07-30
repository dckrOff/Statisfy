package uz.dckroff.statisfy.presentation.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.R
import uz.dckroff.statisfy.databinding.FragmentFavoritesBinding
import uz.dckroff.statisfy.domain.model.*
import uz.dckroff.statisfy.presentation.adapter.FavoritesAdapter
import uz.dckroff.statisfy.presentation.viewmodel.FavoritesViewModel
import uz.dckroff.statisfy.utils.UiState
import uz.dckroff.statisfy.utils.Logger

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupRecyclerView()
        setupSearch()
        setupFilters()
        setupMenu()
        observeViewModel()
        
        Logger.d("FavoritesFragment: onViewCreated completed")
    }

    private fun setupUI() {
        binding.apply {
            // Настройка тулбара
            toolbar.setOnMenuItemClickListener { menuItem ->
                handleMenuClick(menuItem)
            }
            
            // Обработка действий
            btnRetry.setOnClickListener {
                viewModel.refresh()
            }
            
            btnSelectAll.setOnClickListener {
                viewModel.selectAllItems()
            }
            
            fabActions.setOnClickListener {
                showSelectedItemsDialog()
            }
        }
    }

    private fun setupRecyclerView() {
        favoritesAdapter = FavoritesAdapter(
            onItemClick = { item ->
                handleItemClick(item)
            },
            onRemoveClick = { item ->
                viewModel.removeFromFavorites(item.id)
            },
            onShareClick = { item ->
                shareItem(item)
            },
            onLongClick = { item ->
                if (!viewModel.isSelectionMode.value) {
                    viewModel.enableSelectionMode()
                }
                viewModel.toggleItemSelection(item.id)
                true
            }
        )
        
        binding.rvFavorites.apply {
            adapter = favoritesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSearch() {
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.updateSearchQuery(text?.toString() ?: "")
        }
    }

    private fun setupFilters() {
        binding.chipGroupFilters.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedTypes = mutableSetOf<ContentType>()
            
            checkedIds.forEach { chipId ->
                when (chipId) {
                    R.id.chip_facts -> selectedTypes.add(ContentType.FACT)
                    R.id.chip_news -> selectedTypes.add(ContentType.NEWS)
                    R.id.chip_statistics -> selectedTypes.add(ContentType.STATISTIC)
                }
            }
            
            // Если выбрано "Все" или ничего не выбрано, очищаем фильтры
            if (binding.chipAll.isChecked || selectedTypes.isEmpty()) {
                viewModel.clearContentTypeFilters()
            } else {
                selectedTypes.forEach { type ->
                    viewModel.toggleContentTypeFilter(type)
                }
            }
        }
    }

    private fun setupMenu() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            handleMenuClick(item)
        }
    }

    private fun observeViewModel() {
        // Основное состояние данных
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                handleUiState(state)
            }
        }
        
        // Режим выбора
        lifecycleScope.launch {
            viewModel.isSelectionMode.collectLatest { isSelectionMode ->
                updateSelectionMode(isSelectionMode)
            }
        }
        
        // Выбранные элементы
        lifecycleScope.launch {
            viewModel.selectedItems.collectLatest { selectedItems ->
                favoritesAdapter.selectedItems = selectedItems
                updateSelectedItemsUI(selectedItems)
            }
        }
        
        // Сообщения
        lifecycleScope.launch {
            viewModel.message.collectLatest { message ->
                showMessage(message)
            }
        }
        
        // Поисковый запрос
        lifecycleScope.launch {
            viewModel.searchQuery.collectLatest { query ->
                if (binding.etSearch.text.toString() != query) {
                    binding.etSearch.setText(query)
                }
            }
        }
        
        // Выбранные типы контента
        lifecycleScope.launch {
            viewModel.selectedContentTypes.collectLatest { types ->
                updateFilterChips(types)
            }
        }
        
        // Сортировка
        lifecycleScope.launch {
            viewModel.sortBy.collectLatest { sortBy ->
                updateSortMenu(sortBy)
            }
        }
        
        // Режим просмотра
        lifecycleScope.launch {
            viewModel.viewMode.collectLatest { viewMode ->
                updateViewMode(viewMode)
            }
        }
    }

    private fun handleUiState(state: UiState<FavoritesData>) {
        Logger.d("FavoritesFragment: handleUiState $state")
        
        when (state) {
            is UiState.Loading -> {
                showLoading(true)
                showError(false)
                showEmpty(false)
            }
            
            is UiState.Success -> {
                showLoading(false)
                showError(false)
                
                val data = state.data
                if (data.totalCount == 0) {
                    showEmpty(true)
                } else {
                    showEmpty(false)
                    updateContent(data)
                }
            }
            
            is UiState.Error -> {
                showLoading(false)
                showError(true, state.message)
                showEmpty(false)
            }
            
            UiState.Idle -> {
                showLoading(false)
            }
        }
    }

    private fun updateContent(data: FavoritesData) {
        // Обновляем счетчик элементов
        val totalCount = data.totalCount
        binding.tvItemsCount.text = "Найдено: $totalCount элементов"
        
        // Обновляем список
        val allItems = data.groupedContent.values.flatten()
        favoritesAdapter.submitList(allItems)
        
        Logger.d("FavoritesFragment: Updated content with ${allItems.size} items")
    }

    private fun showLoading(show: Boolean) {
        binding.progressLoading.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvFavorites.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showError(show: Boolean, message: String = "") {
        binding.layoutErrorState.visibility = if (show) View.VISIBLE else View.GONE
        if (show && message.isNotEmpty()) {
            binding.tvErrorMessage.text = message
        }
    }

    private fun showEmpty(show: Boolean) {
        binding.layoutEmptyState.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvFavorites.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun updateSelectionMode(isSelectionMode: Boolean) {
        favoritesAdapter.isSelectionMode = isSelectionMode
        
        // Показываем/скрываем элементы UI для режима выбора
        binding.btnSelectAll.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
        
        // Обновляем заголовок тулбара
        if (isSelectionMode) {
            binding.toolbar.title = "Режим выбора"
        } else {
            binding.toolbar.title = getString(R.string.favorites)
        }
    }

    private fun updateSelectedItemsUI(selectedItems: Set<String>) {
        val count = selectedItems.size
        
        if (count > 0) {
            binding.fabActions.show()
            binding.fabActions.text = "Удалить выбранные ($count)"
        } else {
            binding.fabActions.hide()
        }
    }

    private fun updateFilterChips(selectedTypes: Set<ContentType>) {
        // Обновляем состояние чипов без вызова слушателя
        binding.chipGroupFilters.setOnCheckedStateChangeListener(null)
        
        binding.chipFacts.isChecked = selectedTypes.contains(ContentType.FACT)
        binding.chipNews.isChecked = selectedTypes.contains(ContentType.NEWS)
        binding.chipStatistics.isChecked = selectedTypes.contains(ContentType.STATISTIC)
        binding.chipAll.isChecked = selectedTypes.isEmpty()
        
        // Восстанавливаем слушатель
        setupFilters()
    }

    private fun updateSortMenu(sortBy: FavoritesSortBy) {
        // TODO: Обновить состояние пунктов меню сортировки
    }

    private fun updateViewMode(viewMode: FavoritesViewMode) {
        // TODO: Обновить менеджер лейаута в зависимости от режима просмотра
        when (viewMode) {
            FavoritesViewMode.LIST -> {
                binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
            }
            FavoritesViewMode.GRID -> {
                // TODO: Установить GridLayoutManager
            }
            FavoritesViewMode.COMPACT -> {
                // TODO: Установить компактный режим
            }
        }
    }

    private fun handleMenuClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Сортировка
            R.id.sort_by_recent -> {
                viewModel.changeSortBy(FavoritesSortBy.RECENT)
                true
            }
            R.id.sort_by_alphabetical -> {
                viewModel.changeSortBy(FavoritesSortBy.ALPHABETICAL)
                true
            }
            R.id.sort_by_category -> {
                viewModel.changeSortBy(FavoritesSortBy.CATEGORY)
                true
            }
            R.id.sort_by_content_type -> {
                viewModel.changeSortBy(FavoritesSortBy.CONTENT_TYPE)
                true
            }
            
            // Режим просмотра
            R.id.view_mode_list -> {
                viewModel.changeViewMode(FavoritesViewMode.LIST)
                true
            }
            R.id.view_mode_grid -> {
                viewModel.changeViewMode(FavoritesViewMode.GRID)
                true
            }
            R.id.view_mode_compact -> {
                viewModel.changeViewMode(FavoritesViewMode.COMPACT)
                true
            }
            
            // Действия
            R.id.action_select_all -> {
                viewModel.enableSelectionMode()
                viewModel.selectAllItems()
                true
            }
            R.id.action_export -> {
                viewModel.exportFavorites()
                true
            }
            R.id.action_sync -> {
                viewModel.syncWithServer()
                true
            }
            R.id.action_create_folder -> {
                viewModel.showCreateFolderDialog()
                true
            }
            
            else -> false
        }
    }

    private fun handleItemClick(item: FavoriteItem) {
        viewModel.recordItemView(item.id)
        
        // Навигация к детальному экрану в зависимости от типа контента
        when (item.contentType) {
            ContentType.FACT -> {
                // Навигация к экрану деталей факта
                try {
                    findNavController().navigate(
                        R.id.action_favoritesFragment_to_factDetailFragment,
                        Bundle().apply {
                            putString("factId", item.contentId)
                        }
                    )
                } catch (e: Exception) {
                    Logger.e("FavoritesFragment: Navigation error - ${e.message}")
                    showMessage("Ошибка навигации")
                }
            }
            ContentType.NEWS -> {
                // Навигация к экрану деталей новости
                try {
                    findNavController().navigate(R.id.action_favoritesFragment_to_newsDetailFragment)
                } catch (e: Exception) {
                    Logger.e("FavoritesFragment: Navigation error - ${e.message}")
                    showMessage("Ошибка навигации")
                }
            }
            ContentType.STATISTIC -> {
                // Для статистики можно показать детали или просто отметить просмотр
                showMessage("Статистика: ${item.title}")
            }
        }
    }

    private fun shareItem(item: FavoriteItem) {
        // TODO: Реализовать функцию поделиться
        val shareText = "Посмотрите это: ${item.title}\n${item.summary ?: ""}"
        
        try {
            val intent = android.content.Intent().apply {
                action = android.content.Intent.ACTION_SEND
                type = "text/plain"
                putExtra(android.content.Intent.EXTRA_TEXT, shareText)
            }
            startActivity(android.content.Intent.createChooser(intent, "Поделиться"))
        } catch (e: Exception) {
            showMessage("Ошибка при попытке поделиться")
        }
    }

    private fun showSelectedItemsDialog() {
        val selectedCount = viewModel.selectedItems.value.size
        if (selectedCount > 0) {
            // Показываем диалог подтверждения удаления
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Удалить из избранного")
                .setMessage("Удалить $selectedCount элементов из избранного?")
                .setPositiveButton("Удалить") { _, _ ->
                    viewModel.deleteSelectedItems()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 