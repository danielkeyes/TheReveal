package dev.danielkeyes.thereveal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import dev.danielkeyes.thereveal.ui.ComposeWebView
import dev.danielkeyes.thereveal.ui.theme.TheRevealTheme

class AboutTheRevealFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        val siteUrl = this.resources.getString(R.string.site_url)
        val siteSubdirectory = this.resources.getString(R.string.site_subdirectory)

        return ComposeView(requireContext()).apply {
            setContent {
                TheRevealTheme() {
                    AboutTheRevealContent( aboutUrl = "$siteUrl/$siteSubdirectory")
                }
            }
        }
    }
}

@Composable
private fun AboutTheRevealContent(aboutUrl: String) {
    ComposeWebView(url = "https://$aboutUrl")
}
