package uz.dckroff.statisfy.presentation.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.R
import uz.dckroff.statisfy.databinding.FragmentProfileBinding
import uz.dckroff.statisfy.domain.model.User
import uz.dckroff.statisfy.domain.model.UserStats
import uz.dckroff.statisfy.presentation.adapter.ProfilePagerAdapter
import uz.dckroff.statisfy.presentation.viewmodel.ProfileTab
import uz.dckroff.statisfy.presentation.viewmodel.ProfileViewModel
import uz.dckroff.statisfy.utils.Logger
import uz.dckroff.statisfy.utils.UiState

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var pagerAdapter: ProfilePagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewPager()
        setupToolbar()
        setupClickListeners()
        observeViewModel()
        
        // Загружаем данные
        viewModel.loadProfileData()
        
        Logger.d("ProfileFragment: onViewCreated completed")
    }

    private fun setupViewPager() {
        pagerAdapter = ProfilePagerAdapter(requireActivity())
        
        binding.viewPager.adapter = pagerAdapter
        
        // Связываем TabLayout с ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Обзор"
                1 -> "Статистика"
                2 -> "Настройки"
                3 -> "Аккаунт"
                else -> "Tab $position"
            }
        }.attach()
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            handleMenuClick(menuItem)
        }
    }

    private fun setupClickListeners() {
        binding.btnRetry.setOnClickListener {
            viewModel.loadProfileData()
        }
        
        binding.fabEditProfile.setOnClickListener {
            // TODO: Открыть экран редактирования профиля
            showMessage("Редактирование профиля (в разработке)")
        }
    }

    private fun observeViewModel() {
        // Наблюдение за профилем пользователя
        lifecycleScope.launch {
            viewModel.profileState.collectLatest { state ->
                handleProfileState(state)
            }
        }
        
        // Наблюдение за статистикой
        lifecycleScope.launch {
            viewModel.statsState.collectLatest { state ->
                handleStatsState(state)
            }
        }
        
        // Наблюдение за выбранной вкладкой
        lifecycleScope.launch {
            viewModel.selectedTab.collectLatest { tab ->
                updateSelectedTab(tab)
            }
        }
        
        // Наблюдение за режимом редактирования
        lifecycleScope.launch {
            viewModel.isEditingProfile.collectLatest { isEditing ->
                updateEditingMode(isEditing)
            }
        }
    }

    private fun handleProfileState(state: UiState<User>) {
        Logger.d("ProfileFragment: handleProfileState $state")
        
        when (state) {
            is UiState.Loading -> {
                showLoading(true)
                showError(false)
            }
            
            is UiState.Success -> {
                showLoading(false)
                showError(false)
                updateProfileUI(state.data)
            }
            
            is UiState.Error -> {
                showLoading(false)
                showError(true, state.message)
            }
            
            UiState.Idle -> {
                showLoading(false)
            }
        }
    }

    private fun handleStatsState(state: UiState<UserStats>) {
        when (state) {
            is UiState.Success -> {
                updateStatsHeader(state.data)
            }
            else -> {
                // Остальные состояния обрабатываются в дочерних фрагментах
            }
        }
    }

    private fun updateProfileUI(user: User) {
        binding.apply {
            tvUsername.text = user.username
            tvEmail.text = user.email
            
            // TODO: Загрузить аватар пользователя
            // Glide.with(ivAvatar)
            //     .load(user.avatarUrl)
            //     .centerCrop()
            //     .placeholder(R.drawable.ic_profile)
            //     .into(ivAvatar)
        }
    }

    private fun updateStatsHeader(userStats: UserStats) {
        binding.apply {
            // Обновляем быстрые статистики в заголовке
            tvStreakCount.text = userStats.streakStats.currentStreak.toString()
            tvItemsRead.text = userStats.overallStats.totalItemsRead.toString()
            tvLevel.text = userStats.overallStats.currentLevel.toString()
        }
    }

    private fun updateSelectedTab(tab: ProfileTab) {
        val position = when (tab) {
            ProfileTab.OVERVIEW -> 0
            ProfileTab.STATISTICS -> 1
            ProfileTab.PREFERENCES -> 2
            ProfileTab.ACCOUNT -> 3
        }
        
        if (binding.viewPager.currentItem != position) {
            binding.viewPager.setCurrentItem(position, true)
        }
    }

    private fun updateEditingMode(isEditing: Boolean) {
        if (isEditing) {
            binding.fabEditProfile.setImageResource(R.drawable.ic_mark)
        } else {
            binding.fabEditProfile.setImageResource(R.drawable.ic_edit)
        }
    }

    private fun handleMenuClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_profile -> {
                viewModel.toggleEditingMode()
                true
            }
            R.id.action_settings -> {
                // Переключиться на вкладку настроек
                viewModel.selectTab(ProfileTab.PREFERENCES)
                true
            }
            R.id.action_export_data -> {
                viewModel.exportUserData()
                showMessage("Экспорт данных...")
                true
            }
            R.id.action_sync -> {
                viewModel.syncProfileData()
                showMessage("Синхронизация...")
                true
            }
            R.id.action_privacy -> {
                // TODO: Открыть настройки приватности
                showMessage("Настройки приватности (в разработке)")
                true
            }
            R.id.action_help -> {
                // TODO: Открыть справку
                showMessage("Справка (в разработке)")
                true
            }
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            else -> false
        }
    }

    private fun showLogoutDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Выход из аккаунта")
            .setMessage("Вы уверены, что хотите выйти из аккаунта?")
            .setPositiveButton("Выйти") { _, _ ->
                viewModel.logout()
                // TODO: Навигация к экрану входа
                showMessage("Выход из аккаунта...")
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showLoading(show: Boolean) {
        binding.progressLoading.visibility = if (show) View.VISIBLE else View.GONE
        binding.viewPager.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showError(show: Boolean, message: String = "") {
        binding.layoutErrorState.visibility = if (show) View.VISIBLE else View.GONE
        binding.viewPager.visibility = if (show) View.GONE else View.VISIBLE
        
        if (show && message.isNotEmpty()) {
            binding.tvErrorMessage.text = message
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