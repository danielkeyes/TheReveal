package dev.danielkeyes.thereveal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.danielkeyes.thereveal.dataobject.RevealDO
import dev.danielkeyes.thereveal.util.RevealCodeTranslator

class RevealViewModel: ViewModel() {
    // Reveal Code
    private var revealCode: String? = ""

    // RevealDo
    private var _revealDO = MutableLiveData<RevealDO>()

    val revealDO: LiveData<RevealDO>
        get() = _revealDO

    fun setRevealCode(revealCode: String?) {
        this.revealCode = revealCode

        if (revealCode != null && revealCode.isNotEmpty()){
            _revealDO.value = RevealCodeTranslator.decode(revealCode)
        }
    }
}
