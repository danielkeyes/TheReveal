package dev.danielkeyes.thereveal

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import dev.danielkeyes.thereveal.dataobject.Gender
import dev.danielkeyes.thereveal.dataobject.RevealDO
import dev.danielkeyes.thereveal.dataobject.RevealType
import dev.danielkeyes.thereveal.dataobject.validate
import dev.danielkeyes.thereveal.util.RevealCodeTranslator
import java.time.LocalDateTime


class GenerateRevealViewModel : ViewModel() {

    private var _revealCode = MutableLiveData<String>("")
    val revealCode: LiveData<String>
        get() = _revealCode

    private var _revealDO = MutableLiveData<RevealDO>(RevealDO())
    val revealDO: LiveData<RevealDO>
        get() = _revealDO

    private var _errorState = MutableLiveData(false)
    val errorState: LiveData<Boolean>
        get() = _errorState

    private var _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String>
        get() = _errorMessage

    init {
        // If the revealDO changes, we want to validate and generate a new code, error message
        // and error state
        this.revealDO.observeForever {
            val (errorState, errorMessage) = it.validate()
            _errorState.value = errorState
            _errorMessage.value = errorMessage

            if (errorState) {
                _revealCode.value = ""
            } else {
                _revealCode.value = RevealCodeTranslator.encode(revealDO = _revealDO.value!!)
            }
        }
    }

    fun updateRevealType(revealType: RevealType) {
        _revealDO.value = _revealDO.value?.copy(revealType = revealType)
    }

    fun updateFirstName(firstName: String) {
        _revealDO.value = _revealDO.value?.copy(firstName = firstName)
    }

    fun updateMiddleName(middleName: String) {
        _revealDO.value = _revealDO.value?.copy(middleName = middleName)
    }

    fun updateLastName(lastName: String) {
        _revealDO.value = _revealDO.value?.copy(lastName = lastName)
    }

    fun updateGender(gender: Gender) {
        _revealDO.value = _revealDO.value?.copy(gender = gender)
    }

    fun updateDob(dob: LocalDateTime) {
        _revealDO.value = _revealDO.value?.copy(dob = dob)
    }

    fun updateWeightLbs(weightLbs: Int) {
        _revealDO.value = _revealDO.value?.copy(weightLbs = weightLbs)
    }

    fun updateWeightOunces(weightOunces: Int) {
        _revealDO.value = _revealDO.value?.copy(weightOunces = weightOunces)
    }

    fun updateLengthInches(lengthInches: Int) {
        _revealDO.value = _revealDO.value?.copy(lengthInches = lengthInches)
    }

    fun previewRevealCode(
        navController: NavController
    ) {
        val bundle = bundleOf("revealCode" to _revealCode.value)
        navController.navigate(R.id.revealFragment, bundle)
    }

    fun shareRevealCode(context: Context) {
        // Keeping just as an example
//        val htmlMessage = HtmlCompat.fromHtml("""Get <a href=\"https://play.google
//        .com/store/apps/details?id=${BuildConfig.APPLICATION_ID}">The Reveal</a> app for
//        Android and enter the code:
//                        |${_revealCode.value}""".trimMargin("|"), HtmlCompat
//                        .FROM_HTML_MODE_LEGACY).toString()

        val siteUrl = context.resources.getString(R.string.site_url)
        val siteSubdirectory = context.resources.getString(R.string.site_subdirectory)

        val messageTry =
            """Follow the link to see the reveal 
                |https://$siteUrl/$siteSubdirectory?revealcode=${_revealCode.value}""".trimMargin()
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                messageTry,
            )
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(context, shareIntent, null)
    }
}
