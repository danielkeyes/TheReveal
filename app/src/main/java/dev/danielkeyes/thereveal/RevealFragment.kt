package dev.danielkeyes.thereveal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import dev.danielkeyes.thereveal.dataobject.Gender
import dev.danielkeyes.thereveal.dataobject.RevealDO
import dev.danielkeyes.thereveal.dataobject.RevealType
import dev.danielkeyes.thereveal.ui.MyScaffold
import dev.danielkeyes.thereveal.ui.theme.BabyBlue
import dev.danielkeyes.thereveal.ui.theme.BabyPink
import dev.danielkeyes.thereveal.ui.theme.LightBlue400
import dev.danielkeyes.thereveal.ui.theme.LightPink300
import dev.danielkeyes.thereveal.ui.theme.TheRevealTheme
import dev.danielkeyes.thereveal.util.DateHelperUtil
import kotlinx.coroutines.delay
import java.time.LocalDateTime

class RevealFragment : Fragment() {

    private val revealViewModel: RevealViewModel by viewModels()

    private val args: RevealFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val code = args.revealCode
        revealViewModel.setRevealCode(code)

        super.onCreate(savedInstanceState)

        return ComposeView(requireContext()).apply {
            setContent {

                val revealDO by revealViewModel.revealDO.observeAsState()

                TheRevealTheme() {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        MyScaffold(findNavController()) {
                            RevealContent(revealDO)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RevealContent(revealDO: RevealDO?) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        if (revealDO == null) {
            InvalidRevealCode()
        } else {
            if ( // Gender Reveal - requires gender
                revealDO.revealType == RevealType.GENDER &&
                revealDO.gender != null
            ) {
                GenderReveal(gender = revealDO.gender)
            } else if ( // Birth Reveal - requires all but middle and last name
                revealDO.revealType == RevealType.BIRTH &&
                revealDO.firstName != null &&
                revealDO.firstName.isNotEmpty() &&
                revealDO.gender != null &&
                revealDO.dob != null &&
                revealDO.weightLbs != null &&
                revealDO.weightOunces != null &&
                revealDO.lengthInches != null
            ) {
                BirthReveal(
                    firstName = revealDO.firstName,
                    middleName = revealDO.middleName,
                    lastName = revealDO.lastName,
                    gender = revealDO.gender,
                    dob = revealDO.dob,
                    weightLbs = revealDO.weightLbs,
                    weightOunces = revealDO.weightOunces,
                    lengthInches = revealDO.lengthInches,
                )
            } else { //UnsupportedRevealType
                InvalidRevealCode()
            }
        }
    }
}

@Composable
private fun BirthReveal(
    firstName: String,
    middleName: String? = null,
    lastName: String? = null,
    gender: Gender,
    dob: LocalDateTime,
    weightLbs: Int,
    weightOunces: Int,
    lengthInches: Int,
    countDownEnabled: Boolean = true,
) {
    val cardColor = getCardColor(gender, isSystemInDarkTheme())

    val countDownTime = if (countDownEnabled) 3 else 0
    RevealCountDown(countDownTime) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Name
            MyCard(backgroundColor = cardColor) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Baby", style = MaterialTheme.typography.h5)
                    Text(text = "$firstName", style = MaterialTheme.typography.h3)
                    if (middleName != null && middleName.isNotEmpty()) {
                        Text(text = "$middleName", style = MaterialTheme.typography.h4)
                    }
                    if (lastName != null && lastName.isNotEmpty()) {
                        Text(text = "$lastName", style = MaterialTheme.typography.h3)
                    }
                }
            }

            // Born date
            val monthDisplay = DateHelperUtil.getLongMonth(dob)
            val dayDisplay = DateHelperUtil.getDayWithOrdinal(dob.dayOfMonth)
            val yearDisplay = dob.year
            MyCard(backgroundColor = cardColor) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {                    Text(text = "Born on", style = MaterialTheme.typography.h5)
                    Text(text = "$monthDisplay $dayDisplay", style = MaterialTheme.typography.h4)
                    Text(text = "$yearDisplay", style = MaterialTheme.typography.h5)
                }
            }

            // Details
            MyCard(backgroundColor = cardColor) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Birth size", style = MaterialTheme.typography.h5)
                    Text(text = "$weightLbs lbs $weightOunces oz", style = MaterialTheme.typography.h4)
                    Text(text = "$lengthInches inches long", style = MaterialTheme.typography.h4)
                }
            }
        }
    }
}

@Composable
private fun RevealCountDown(countDownSeconds: Int = 3, content: @Composable () -> Unit) {
    var revealContent by remember { mutableStateOf(false) }
    var countDown by remember { mutableStateOf(countDownSeconds) }

    if(countDownSeconds > 0) {
        LaunchedEffect(Unit) {
            while (countDown > 0) {
                delay(1000)
                countDown--
            }
            revealContent = true
        }
    } else {
        revealContent = true
    }

    if (revealContent) {
        content()
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "$countDown", fontSize = 24.sp)
        }
    }
}


@Composable
private fun GenderReveal(
    gender: Gender, countDownEnabled: Boolean = true,
) {
    val cardColor = getCardColor(gender, isSystemInDarkTheme())

    val countDownTime = if (countDownEnabled) 3 else 0
    RevealCountDown(countDownTime) {
        MyCard(backgroundColor = cardColor) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(text = "It's a", fontSize = 24.sp)
                Text(text = "${gender?.name}!", fontSize = 48.sp)
            }
        }
    }
}

@Composable
private fun InvalidRevealCode() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Invalid Reveal Code provided",
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Please double check your entered code",
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MyCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color?,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        backgroundColor = backgroundColor ?: MaterialTheme.colors.surface,
        content = content
    )
}

fun getCardColor(gender: Gender, darkMode: Boolean): Color {
    return if (gender == Gender.BOY) {
        if(darkMode) {
            LightBlue400
        } else {
            BabyBlue
        }
    } else{
        if(darkMode) {
            LightPink300
        } else {
            BabyPink
        }
    }
}

@Preview
@Composable
private fun PreviewGenderReveal() {
    TheRevealTheme() {
        GenderReveal(gender = Gender.BOY, countDownEnabled = false)
    }
}

@Preview
@Composable
private fun PreviewBirthReveal() {
    TheRevealTheme() {
        BirthReveal(
            firstName = "Dean",
            middleName = "Kurt",
            lastName = "Keyes",
            gender = Gender.BOY,
            dob = LocalDateTime.now().withMonth(6).withDayOfMonth(27).withYear(2022),
            weightLbs = 8,
            weightOunces = 0,
            lengthInches = 21,
            countDownEnabled = false,
        )
    }
}
