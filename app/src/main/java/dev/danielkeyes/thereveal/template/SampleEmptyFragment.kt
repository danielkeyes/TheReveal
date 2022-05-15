package dev.danielkeyes.thereveal.template

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import dev.danielkeyes.thereveal.ui.theme.TheRevealTheme

/**
 * Development use
 */
class SampleEmptyFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        return ComposeView(requireContext()).apply {
            setContent {
                TheRevealTheme {
                    FragmentContent()
                }
            }
        }
    }
}

@Composable
private fun FragmentContent() {
    Text(text = "EmptyFragment")
}
