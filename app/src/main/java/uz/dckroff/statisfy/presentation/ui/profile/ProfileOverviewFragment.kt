package uz.dckroff.statisfy.presentation.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.databinding.FragmentProfileOverviewBinding
import uz.dckroff.statisfy.domain.model.Achievement
import uz.dckroff.statisfy.domain.model.UserStats
import uz.dckroff.statisfy.presentation.adapter.AchievementsAdapter
import uz.dckroff.statisfy.presentation.viewmodel.ProfileViewModel
import uz.dckroff.statisfy.utils.UiState

/**
 * Фрагмент обзора профиля пользователя
 */
class ProfileOverviewFragment : Fragment() {

    private var _binding: FragmentProfileOverviewBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var achievementsAdapter: AchievementsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        achievementsAdapter = AchievementsAdapter { achievement ->
            // TODO: Показать детали достижения
        }

        binding.rvRecentAchievements.apply {
            adapter = achievementsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupClickListeners() {
        binding.tvViewAllAchievements.setOnClickListener {
            // TODO: Открыть экран всех достижений
        }
    }

    private fun observeViewModel() {
        // Наблюдение за статистикой пользователя
        lifecycleScope.launch {
            viewModel.statsState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> {
                        // Показываем загрузку
                    }
                    is UiState.Success -> {
                        updateStatsUI(state.data)
                    }
                    is UiState.Error -> {
                        // Обрабатываем ошибку
                    }
                    UiState.Idle -> {
                        // Ничего не делаем
                    }
                }
            }
        }
    }

    private fun updateStatsUI(userStats: UserStats) {
        binding.apply {
            // Обновляем прогресс целей
            val dailyGoal = userStats.readingStats.averageItemsPerDay.toInt()
            val dailyProgress = minOf(dailyGoal, 5) // Максимум 5 для дневной цели
            tvDailyGoalProgress.text = "$dailyProgress/5"
            progressDailyGoal.progress = (dailyProgress * 100 / 5)

            val weeklyGoal = userStats.readingStats.averageItemsPerDay.toInt() * 7
            val weeklyProgress = minOf(weeklyGoal, 25)
            tvWeeklyGoalProgress.text = "$weeklyProgress/25"
            progressWeeklyGoal.progress = (weeklyProgress * 100 / 25)

            // Обновляем предпочтения чтения
            tvFavoriteReadingTime.text = when (userStats.readingStats.favoriteReadingTime) {
                "morning" -> "Утром"
                "afternoon" -> "Днем"
                "evening" -> "Вечером"
                else -> "Не определено"
            }

            tvAverageReadingTime.text = String.format("%.1f мин", userStats.readingStats.averageReadingTime)

            // Обновляем достижения
            val recentAchievements = userStats.achievements.take(5)
            achievementsAdapter.submitList(recentAchievements)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}