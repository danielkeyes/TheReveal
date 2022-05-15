package dev.danielkeyes.thereveal.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import dev.danielkeyes.thereveal.BuildConfig
import dev.danielkeyes.thereveal.R
import dev.danielkeyes.thereveal.SCAFFOLD_ENABLED
import dev.danielkeyes.thereveal.ui.theme.TheRevealTheme

/**
 * Reusable composables
 */

/**
 * Reusable compose scaffold including icon, app name, and menu with about option
 * tailored for The Reveal app use
 */
@Composable
fun MyScaffold(navController: NavController, content: @Composable (PaddingValues) -> Unit,  ) {
    var expandMenu by remember { mutableStateOf(false) }

    if(SCAFFOLD_ENABLED) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(MaterialTheme.colors.primary)
                ) {
                    Row(
                        modifier = Modifier.height(IntrinsicSize.Max),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.fillMaxHeight()) {
                            var iconBackground = R.drawable.icon_background_light_mode
                            var iconFilterColor = Color.Black

                            // For dark mode we need to change the icon background and filter
                            if(isSystemInDarkTheme()){
                                iconBackground = R.drawable.icon_background_dark_mode
                                iconFilterColor = Color.White
                            }

                            Image(
                                modifier = Modifier.fillMaxHeight(),
                                painter = painterResource(id = iconBackground),
                                contentDescription = "app icon"
                            )
                            Image(
                                modifier = Modifier.fillMaxHeight(),
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "app icon",
                                colorFilter = ColorFilter.tint(iconFilterColor)
                            )
                        }
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "The Reveal",
                            style = MaterialTheme.typography.h6
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        // https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#DropdownMenu(kotlin.Boolean,kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.ui.unit.DpOffset,androidx.compose.ui.window.PopupProperties,kotlin.Function1)
                        Box(modifier = Modifier
                            .wrapContentSize(Alignment.TopStart)) {
                            IconButton(onClick = { expandMenu = true }) {
                                Icon(
                                    imageVector = Icons.Rounded.MoreVert,
                                    contentDescription = "Menu"
                                )
                            }
                            DropdownMenu(
                                expanded = expandMenu,
                                onDismissRequest = { expandMenu = false }
                            ) {
                                DropdownMenuItem(onClick = { navController.navigate(R.id.aboutTheRevealFragmnet) }) {
                                    Text("About")
                                }
                                if(BuildConfig.DEBUG) {
                                    // TODO future add any debug only items I want here for testing
                                }
                            }
                        }
                    }
                }
            },
            content = content
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            content
        }
    }
}

/**
 * Reusable compose webview
 */
@Composable
fun ComposeWebView(url: String) {
    AndroidView(
        factory = { context ->
                  WebView(context).apply {
                      layoutParams = ViewGroup.LayoutParams(
                          ViewGroup.LayoutParams.MATCH_PARENT,
                          ViewGroup.LayoutParams.MATCH_PARENT
                      )
                      webViewClient = WebViewClient()
                      loadUrl(url)
                  }
        },
        update = { webView ->
            webView.loadUrl(url)
        }
    )
}

@Preview()
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewMyScaffold() {
    TheRevealTheme {
        val context = LocalContext.current
        Surface {
            MyScaffold(navController = NavController(context)) {}
        }
    }
}
