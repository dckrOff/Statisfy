package uz.dckroff.statisfy.presentation.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import uz.dckroff.statisfy.presentation.viewmodel.ProfileViewModel

/**
 * Фрагмент настроек и предпочтений пользователя
 */
class ProfilePreferencesFragment : Fragment() {

    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // TODO: Создать полноценный лейаут для настроек
        return inflater.inflate(android.R.layout.simple_list_item_1, container, false).apply {
            findViewById<android.widget.TextView>(android.R.id.text1).text = "Настройки и предпочтения\n(В разработке)"
        }
    }
}