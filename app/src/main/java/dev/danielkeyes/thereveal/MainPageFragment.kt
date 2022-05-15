package dev.danielkeyes.thereveal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import dev.danielkeyes.thereveal.dataobject.validate
import dev.danielkeyes.thereveal.ui.MyScaffold
import dev.danielkeyes.thereveal.ui.theme.TheRevealTheme
import dev.danielkeyes.thereveal.util.RevealCodeTranslator

class MainPageFragment : Fragment() {

    private val args: MainPageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        return ComposeView(requireContext()).apply {
            setContent {
                TheRevealTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        MyScaffold(findNavController()) {
                            MainPage(
                                revealCode = args.revealCode,
                                navigateReveal = { code ->
                                    val bundle = bundleOf("revealCode" to code)
                                    findNavController().navigate(
                                        R.id.action_mainPageFragment_to_revealFragment, bundle
                                    )
                                }
                            ) {
                                findNavController().navigate(
                                    R.id.action_mainPageFragment_to_generateRevealCodeFragment
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainPage(revealCode: String = "", navigateReveal: (String) -> Unit, navigateGenerateRevealCode: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(1f)
                .wrapContentWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var revealCode by rememberSaveable { mutableStateOf(revealCode) }

            TextField(
                modifier = Modifier.padding(8.dp),
                value = revealCode,
                onValueChange = {
                    revealCode = it
                },
                label = { Text(text = "Reveal Code") },
                placeholder = { Text(text = "Paste your reveal code here") }
            )

            var context = LocalContext.current
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = {
                    val(invalidCode, _ ) = RevealCodeTranslator.decode(revealCode).validate()

                    if(invalidCode){
                        Toast.makeText(context, "Invalid reveal code provided", Toast.LENGTH_LONG).show()
                    } else {
                        navigateReveal(revealCode)
                    }
                }) {
                Text(text = "Reveal!")
            }
        }
        Button(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentWidth(),
            onClick = { navigateGenerateRevealCode() }) {
            Text(text = "Generate a code")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TheRevealTheme {
        MainPage(revealCode = "", navigateReveal = { }) {}
    }
}
