package uz.dckroff.statisfy.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.dckroff.statisfy.presentation.ui.profile.ProfileOverviewFragment
import uz.dckroff.statisfy.presentation.ui.profile.ProfileStatsFragment
import uz.dckroff.statisfy.presentation.ui.profile.ProfilePreferencesFragment
import uz.dckroff.statisfy.presentation.ui.profile.ProfileAccountFragment

/**
 * Адаптер для ViewPager2 в профиле пользователя
 */
class ProfilePagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfileOverviewFragment()
            1 -> ProfileStatsFragment()
            2 -> ProfilePreferencesFragment()
            3 -> ProfileAccountFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}