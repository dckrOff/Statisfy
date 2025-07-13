package uz.dckroff.statisfy.presentation.ui.facts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.R
import uz.dckroff.statisfy.databinding.FragmentFactsBinding
import uz.dckroff.statisfy.domain.model.Category
import uz.dckroff.statisfy.presentation.adapter.FactAdapter
import uz.dckroff.statisfy.presentation.viewmodel.FactsViewModel
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.UiState
import uz.dckroff.statisfy.utils.gone
import uz.dckroff.statisfy.utils.visible

/**
 * Фрагмент для отображения списка фактов
 */
@AndroidEntryPoint
class FactsFragment : Fragment() {

    private var _binding: FragmentFactsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FactsViewModel by viewModels()

    private lateinit var factsAdapter: FactAdapter

    // Текущая выбранная категория
    private var currentCategoryId: String? = null

    // Текущая страница пагинации
    private var currentPage = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupRecyclerView()
        observeViewModel()
        loadData()
    }

    private fun setupUI() {
        // Настройка тулбара
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Настройка поисковой панели
        binding.searchBar.setOnClickListener {
            // TODO: Реализовать поиск
            Toast.makeText(requireContext(), "Поиск будет реализован в этапе 8", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setupRecyclerView() {
        factsAdapter = FactAdapter(
            onFactClick = { fact ->
                navigateToFactDetail(fact.id)
            },
            onShareClick = { fact ->
                shareFactContent(fact.id)
            },
            onFavoriteClick = { fact ->
                viewModel.toggleFavorite()
            }
        )

        binding.factsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = factsAdapter
            setHasFixedSize(true)

            // Добавляем обработку прокрутки для пагинации
            addOnScrollListener(object :
                androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: androidx.recyclerview.widget.RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    // Если пользователь прокрутил до конца списка, загружаем следующую страницу
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= 20
                    ) {
                        loadMoreFacts()
                    }
                }
            })
        }
    }

    private fun observeViewModel() {
        // Наблюдение за состоянием фактов
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.factsState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> {
                        if (currentPage == 0) {
                            showLoading()
                        }
                    }

                    is UiState.Success -> {
                        showContent(state.data)
                    }

                    is UiState.Error -> {
                        showError(state.message)
                    }

                    else -> {
                        TODO()
                    }
                }
            }
        }

        // Наблюдение за категориями
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categoriesState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> {
                        // Обрабатываем загрузку категорий
                    }

                    is UiState.Success -> {
                        setupCategories(state.data)
                    }

                    is UiState.Error -> {
                        // Показываем сообщение об ошибке загрузки категорий
                    }

                    UiState.Idle -> TODO()
                }
            }
        }
    }

    private fun setupCategories(categories: List<Category>) {
        // Очищаем контейнер категорий
        binding.categoriesContainer.removeAllViews()

        // Добавляем чип "Все" для отображения всех фактов
        val allChip = createCategoryChip(null, "Все", true)
        binding.categoriesContainer.addView(allChip)

        // Добавляем чипы для каждой категории
        categories.forEach { category ->
            val chip = createCategoryChip(category.id, category.name, false)
            binding.categoriesContainer.addView(chip)
        }
    }

    private fun createCategoryChip(
        categoryId: String?,
        categoryName: String,
        isChecked: Boolean
    ): Chip {
        val chip = layoutInflater.inflate(
            R.layout.item_category_chip, binding.categoriesContainer, false
        ) as Chip

        chip.text = categoryName
        chip.isChecked = isChecked
        chip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Сбрасываем выбор других чипов
                for (i in 0 until binding.categoriesContainer.childCount) {
                    val otherChip = binding.categoriesContainer.getChildAt(i) as? Chip
                    if (otherChip != buttonView) {
                        otherChip?.isChecked = false
                    }
                }

                // Устанавливаем текущую категорию и загружаем факты
                currentCategoryId = categoryId
                currentPage = 0
                loadData()
            }
        }

        return chip
    }

    private fun loadData() {
        viewModel.getFacts(page = currentPage, categoryId = currentCategoryId)
        if (currentPage == 0) {
            viewModel.getCategories()
        }
    }

    private fun loadMoreFacts() {
        currentPage++
        loadData()
    }

    private fun showLoading() {
        binding.progressBar.visible()
    }

    private fun showContent(facts: List<uz.dckroff.statisfy.domain.model.Fact>) {
        binding.progressBar.gone()
        factsAdapter.submitList(facts)
    }

    private fun showError(message: String) {
        binding.progressBar.gone()
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun navigateToFactDetail(factId: String) {
        try {
            val directions = FactsFragmentDirections.actionFactsFragmentToFactDetailFragment(factId)
            findNavController().navigate(directions)
        } catch (e: Exception) {
            Logger.e("FactsFragment: Error navigating to fact detail", e)
            Toast.makeText(
                requireContext(),
                "Ошибка при открытии деталей факта",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun shareFactContent(factId: String) {
        viewModel.shareFact(factId, requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}