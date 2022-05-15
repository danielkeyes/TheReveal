package dev.danielkeyes.thereveal

import android.app.DatePickerDialog
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.chargemap.compose.numberpicker.NumberPicker
import dev.danielkeyes.thereveal.dataobject.Gender
import dev.danielkeyes.thereveal.dataobject.RevealDO
import dev.danielkeyes.thereveal.dataobject.RevealType
import dev.danielkeyes.thereveal.ui.MyScaffold
import dev.danielkeyes.thereveal.ui.theme.TheRevealTheme
import dev.danielkeyes.thereveal.util.DateHelperUtil
import java.time.LocalDateTime
import java.util.*

class GenerateRevealCodeFragment : Fragment() {

    private val vm: GenerateRevealViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        val navController = findNavController()

        return ComposeView(requireContext()).apply {
            setContent {
                val revealDO by vm.revealDO.observeAsState(RevealDO())

                TheRevealTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                    ) {
                        // TODO: future, pull vm out of GenerateRevealCodeContent
                        MyScaffold(findNavController()) {
                            GenerateRevealCodeContent(
                                vm = vm, revealDO = revealDO, navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GenerateRevealCodeContent(
    vm: GenerateRevealViewModel,
    revealDO: RevealDO,
    navController: NavController,
) {
    val revealCode by vm.revealCode.observeAsState("")
    val errorState by vm.errorState.observeAsState(false)
    val errorMessage by vm.errorMessage.observeAsState("")

    Column(modifier = Modifier.padding(16.dp)) {
        // Only Birth and Gender are selectable Reveal Types
        val selectableRevealTypes = listOf(RevealType.GENDER, RevealType.BIRTH)
        var selected by remember { mutableStateOf(revealDO.revealType) }

        // If selected
        if (!selectableRevealTypes.contains(selected)) {
            selected = selectableRevealTypes[0]
            vm.updateRevealType(selectableRevealTypes[0])
        }

        Column() {
            TitleText(text = "What type of reveal do you want?")
            LeftAlignedRow {
                selectableRevealTypes.forEach { revealType ->
                    val displayName = revealType.name.lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault
                            ()) else it.toString() }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selected == revealType, onClick = {
                            selected = revealType
                            vm.updateRevealType(revealType)
                        })
                        Text(
                            text = "$displayName", modifier = Modifier
                                .clickable(onClick = {
                                    selected = revealType
                                    vm.updateRevealType(revealType)
                                })
                                .padding(start = 4.dp)
                        )
                    }
                }
            }
        }

        LargeSeparator()

        // Content of Reveal Generator based on reveal type
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .weight(1f)
        ) {
            when (selected) {
                RevealType.GENDER -> {
                    GenderRevealGenerator(
                        revealDO = revealDO,
                        updateGender = { gender: Gender -> vm.updateGender(gender) },
                        modifier = Modifier.weight(1f),
                    )
                }
                RevealType.BIRTH -> {
                    BirthRevealGenerator(revealDO = revealDO,
                        updateFirstName = { vm.updateFirstName(it) },
                        updateMiddleName = { vm.updateMiddleName(it) },
                        updateLastName = { vm.updateLastName(it) },
                        updateGender = { vm.updateGender(it) },
                        updateDob = { vm.updateDob(it) },
                        updateWeightLbs = { vm.updateWeightLbs(it) },
                        updateWeightOunces = { vm.updateWeightOunces(it) },
                        updateLengthInches = { vm.updateLengthInches(it) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }


        LargeSeparator()

        val context = LocalContext.current

        GeneratePreviewAndShareCodeContent(
            code = revealCode,
            errorState = errorState,
            previewRevealCode = { vm.previewRevealCode(navController = navController) },
            shareRevealCode = { vm.shareRevealCode(context = context) },
        )
    }
}

@Composable
fun GenderRevealGenerator(
    revealDO: RevealDO,
    updateGender: (Gender) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SubTitleText(text = "Gender")
        var selected by remember { mutableStateOf(revealDO.gender) }
        Column(modifier = Modifier.weight(1f)) {
            Gender.values().forEach { gender ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selected == gender, onClick = {
                        selected = gender
                        updateGender(gender)
                    })
                    Text(
                        text = gender.name.lowercase(Locale.US)
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US)
                            else it.toString() },
                        modifier = Modifier
                            .clickable(onClick = {
                                selected = gender
                                updateGender(gender)
                            })
                            .padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BirthRevealGenerator(
    revealDO: RevealDO,
    updateFirstName: (String) -> Unit,
    updateMiddleName: (String) -> Unit,
    updateLastName: (String) -> Unit,
    updateGender: (Gender) -> Unit,
    updateDob: (LocalDateTime) -> Unit,
    updateWeightLbs: (Int) -> Unit,
    updateWeightOunces: (Int) -> Unit,
    updateLengthInches: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .weight(1f)
        ) {

            // Name
            SubTitleText(text = "Name")
            // First Name -------------------------------
            OutlinedTextField(
                modifier = Modifier.padding(8.dp),
                value = revealDO.firstName ?: "",
                onValueChange = {
                    updateFirstName(it)
                },
                label = {
                    Row {
                        Text(text = "First Name")
                        if (revealDO.firstName.isNullOrEmpty()) {
                            Text(text = " - Required")
                        }
                    }
                },
                isError = revealDO.firstName.isNullOrEmpty(),
            )

            // Middle Name -------------------------------
            OutlinedTextField(modifier = Modifier.padding(8.dp),
                value = revealDO.middleName ?: "",
                onValueChange = {
                    updateMiddleName(it)
                },
                label = {
                    Text(text = "Middle Name")
                })
            // Last Name -------------------------------
            OutlinedTextField(modifier = Modifier.padding(8.dp),
                value = revealDO.lastName ?: "",
                onValueChange = {
                    updateLastName(it)
                },
                label = {
                    Text(text = "Last Name")
                })

            // Gender -------------------------------
            SubTitleText(text = "Gender")
            LeftAlignedRow() {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val boy = Gender.BOY
                    RadioButton(selected = revealDO.gender == boy, onClick = {
                        updateGender(boy)
                    })
                    Text(
                        text = Gender.BOY.name.lowercase()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale
                                .getDefault()) else it.toString() },
                        modifier = Modifier
                            .clickable(onClick = { updateGender(boy) })
                            .padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val girl = Gender.GIRL
                    RadioButton(selected = revealDO.gender == girl, onClick = {
                        updateGender(girl)
                    })
                    Text(
                        text = Gender.GIRL.name.lowercase()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale
                                .getDefault()) else it.toString() },
                        modifier = Modifier
                            .clickable(onClick = { updateGender(girl) })
                            .padding(start = 4.dp)
                    )
                }
            }

            // DatePicker -------------------------------
            SubTitleText(text = "Born")
            val mContext = LocalContext.current

            // NOTE month does not work the same ...increment/decrement
            val mDatePickerDialog = DatePickerDialog(
                mContext,
                { _: DatePicker, mYear: Int, mMonth: Int, mDay: Int ->
                    updateDob(LocalDateTime.of(mYear, mMonth + 1, mDay, 0, 0))
                },
                revealDO.dob?.year ?: LocalDateTime.now().year,
                (revealDO.dob?.monthValue ?: LocalDateTime.now().monthValue) - 1,
                revealDO.dob?.dayOfMonth ?: LocalDateTime.now().dayOfMonth
            )

            Button(onClick = {
                mDatePickerDialog.show()
            }, colors = ButtonDefaults.buttonColors()) {
                Text(text = formatDisplayDate(revealDO.dob ?: LocalDateTime.now()))
            }

            // Weight Lbs -------------------------------
            SubTitleText(text = "Weight")
            if (revealDO.weightOunces == null) {
                updateWeightLbs(7)
            }
            if (revealDO.weightOunces == null) {
                updateWeightOunces(11)
            }
            LeftAlignedRow() {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NumberPicker(modifier = Modifier
                        .width(84.dp)
                        .padding(end = 8.dp),
                        value = revealDO.weightLbs ?: 0,
                        range = 0..14,
                        onValueChange = {
                            updateWeightLbs(it)
                        })
                    Text(text = "lbs")
                }
                Spacer(modifier = Modifier.width(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NumberPicker(modifier = Modifier
                        .width(84.dp)
                        .padding(end = 8.dp),
                        value = revealDO.weightOunces ?: 0,
                        range = 0..15,
                        onValueChange = {
                            updateWeightOunces(it)
                        })
                    Text(text = "ounces")
                }
            }

            // Length
            SubTitleText(text = "Length")
            if (revealDO.lengthInches == null) {
                updateLengthInches(19)
            }
            LeftAlignedRow() {
                NumberPicker(modifier = Modifier
                    .width(72.dp)
                    .padding(end = 8.dp),
                    value = revealDO.lengthInches ?: 0,
                    range = 0..30,
                    onValueChange = {
                        updateLengthInches(it)
                    })
                Text(text = "inches")
            }
//            Text(text = "* Required", color = MaterialTheme.colors.error)
        }
    }
}

fun formatDisplayDate(localDateTime: LocalDateTime): String {
    val monthShortName = DateHelperUtil.getShortMonth(localDateTime.monthValue)

    val ordinalDay = DateHelperUtil.getDayWithOrdinal(localDateTime.dayOfMonth)

    return "$monthShortName $ordinalDay, ${localDateTime.year}"
}

@Composable
fun GeneratePreviewAndShareCodeContent(
    code: String?,
    errorState: Boolean,
    previewRevealCode: () -> Unit,
    shareRevealCode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        Row(Modifier.fillMaxWidth()) {
            Text(text = "Code: ")
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = if (code != null && code.isNotEmpty()) code + "\n" else "Generate a code " +
                        "by filling in the necessary details above\n",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(enabled = !errorState, onClick = {
                previewRevealCode()
            }) {
                Text(text = "Preview", fontSize = 16.sp)
            }
            Button(enabled = !errorState, onClick = {
                if (code != null && code.isNotEmpty()) {
                    shareRevealCode()
                } else {
                    Toast.makeText(
                        context, "Please generate a code first", Toast.LENGTH_LONG
                    ).show()
                }
            }) {
                Icon(imageVector = Icons.Rounded.Share, contentDescription = "Share Code")
            }
        }
    }
}

@Composable
fun TitleText(text: String) {
    Text(modifier = Modifier.padding(vertical = 4.dp), text = text, fontSize = 20.sp)
}

@Composable
fun SubTitleText(text: String, isRequired: Boolean = false) {
    Text(
        modifier = Modifier.padding(horizontal = 2.dp),
        text = buildAnnotatedString {
            append(text)
            if (isRequired) {
                withStyle(style = SpanStyle(color = MaterialTheme.colors.error)) {
                    append("*")
                }
            }
        },
        fontSize = 16.sp,
        style = MaterialTheme.typography.h5,
    )
}

@Composable
fun SpacedRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        content = content
    )
}

@Composable
fun LeftAlignedRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
fun LargeSeparator() {
    Spacer(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .height(1.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colors.onSurface)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewGeneratePreviewOptions() {
    TheRevealTheme() {
        GenerateRevealCodeContent(
            vm = GenerateRevealViewModel(),
            revealDO = RevealDO(),
            navController = rememberNavController(),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGeneratePreviewOptions2() {
    TheRevealTheme() {
        GeneratePreviewAndShareCodeContent(
            "AD728D7D9SF8SD9F8H98FHSNDINV0S98D09JF09J4NF09JJMSJV9009J39J49JSDJFS",
            errorState = false,
            {},
            {},
        )
    }
}
