package dev.danielkeyes.thereveal

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dev.danielkeyes.thereveal.ui.theme.Black
import dev.danielkeyes.thereveal.ui.theme.LightBlue400
import dev.danielkeyes.thereveal.ui.theme.LightBlue400Dark
import dev.danielkeyes.thereveal.ui.theme.TheRevealTheme
import dev.danielkeyes.thereveal.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Unused for now
class SplashScreenFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch{
            delay(2000L)
            findNavController().apply {
                popBackStack()
                navigate(R.id.mainPageFragment)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                TheRevealTheme() {
                    SplashContent()
                }
            }
        }
    }
}

@Composable
private fun SplashContent() {
    val splashBackgroundColor = if (isSystemInDarkTheme()) LightBlue400Dark else LightBlue400
    val splashIconTint = if (isSystemInDarkTheme()) White else Black

    Box(modifier = Modifier
        .background(splashBackgroundColor)
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Icon(
            modifier = Modifier.size(120.dp),
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "App Logo",
            tint = splashIconTint
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSplashScreen() {
    TheRevealTheme {
        SplashContent()
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewSplashScreenDark() {
    TheRevealTheme {
        SplashContent()
    }
}
