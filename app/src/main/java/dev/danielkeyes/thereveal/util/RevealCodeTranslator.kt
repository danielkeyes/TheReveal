package dev.danielkeyes.thereveal.util

import android.util.Base64
import dev.danielkeyes.thereveal.dataobject.Gender
import dev.danielkeyes.thereveal.dataobject.RevealDO
import dev.danielkeyes.thereveal.dataobject.RevealType
import java.io.UnsupportedEncodingException
import java.time.LocalDateTime
import java.util.logging.*

// This is used just for easier debugging
private const val USE_ENCODING: Boolean = true

object RevealCodeTranslator {
    private val logger = Logger.getLogger(this::class.simpleName)
    private const val delimiter = '#'

    // Keeping as a reminder of old easier to read identifiers
    // switched to numbers to save on reveal code length
//        private const val revealTypeIdentifier = "revealType"
//        private const val firstNameIdentifier = "firstName"
//        private const val middleNameIdentifier = "middleName"
//        private const val lastNameIdentifier = "lastName"
//        private const val genderIdentifier = "gender"
//        private const val dobIdentifier = "dob"
//        private const val weightLbsIdentifier = "weightLbs"
//        private const val weightOuncesIdentifier = "weightOunces"
//        private const val lengthInchesIdentifier = "length"

    // Updated identifier to save code length, 0-9, then alphabet
    private const val revealTypeIdentifier = "0"
    private const val firstNameIdentifier = "1"
    private const val middleNameIdentifier = "2"
    private const val lastNameIdentifier = "3"
    private const val genderIdentifier = "4"
    private const val dobIdentifier = "5"
    private const val weightLbsIdentifier = "6"
    private const val weightOuncesIdentifier = "7"
    private const val lengthInchesIdentifier = "8"

    fun encode(revealDO: RevealDO): String {

        var fullString = ""
        var base64 = ""

        revealDO.revealType.let { fullString += delimiter + revealTypeIdentifier + it }
        revealDO.gender?.let { fullString += delimiter + genderIdentifier + it }

        // small tweak to not encode everything if just Gender reveal
        // saves on code length
        if (revealDO.revealType == RevealType.BIRTH) {
            revealDO.firstName?.let { fullString += delimiter + firstNameIdentifier + it }
            revealDO.middleName?.let { fullString += delimiter + middleNameIdentifier + it }
            revealDO.lastName?.let { fullString += delimiter + lastNameIdentifier + it }
            revealDO.dob?.let { fullString += delimiter + dobIdentifier + it.toEncodableString() }
            revealDO.weightLbs?.let { fullString += delimiter + weightLbsIdentifier + it }
            revealDO.weightOunces?.let { fullString += delimiter + weightOuncesIdentifier + it }
            revealDO.lengthInches?.let { fullString += delimiter + lengthInchesIdentifier + it }
        }

        return if (!USE_ENCODING) {
            fullString
        } else {
            var data: ByteArray?
            try {
                data = fullString.toByteArray(charset("UTF-8"))
                base64 = Base64.encodeToString(data, Base64.DEFAULT).filter { !it.isWhitespace() }
            } catch (e: UnsupportedEncodingException) {
                logger.log(Level.FINE, e.message)
            }
            base64
        }
    }

    fun decode(encodedString: String): RevealDO {

        var fullString = encodedString

        // Receiving side
        if (USE_ENCODING) {
            try {
                val data: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
                fullString = String(data, charset("UTF-8"))
            } catch (e: UnsupportedEncodingException) {
                logger.log(Level.FINE, e.message)
                return RevealDO(RevealType.NONE)
            } catch (e: IllegalArgumentException) {
                logger.log(Level.FINE, e.message)
                return RevealDO(RevealType.NONE)
            }
        }

        val values = fullString.split('#')

        val revealType = values.getValueFromArray(revealTypeIdentifier).let { revealTypeString ->
            RevealType.values().find { it.name == revealTypeString } ?: RevealType.NONE
        }
        val firstName = values.getValueFromArray(firstNameIdentifier)
        val middleName = values.getValueFromArray(middleNameIdentifier)
        val lastName = values.getValueFromArray(lastNameIdentifier)
        val sex = values.getValueFromArray(genderIdentifier).let { sexString ->
            Gender.values().find { it.name == sexString }
        }
        val dob = values.getValueFromArray(dobIdentifier)?.toLocalDateTime()
        val weightLbs = values.getValueFromArray(weightLbsIdentifier)?.toInt()
        val weightOunces = values.getValueFromArray(weightOuncesIdentifier)?.toInt()?.mod(16)
        val lengthInches = values.getValueFromArray(lengthInchesIdentifier)?.toInt()

        return RevealDO(
            revealType = revealType,
            firstName = firstName,
            middleName = middleName,
            lastName = lastName,
            gender = sex,
            dob = dob,
            weightLbs = weightLbs,
            weightOunces = weightOunces,
            lengthInches = lengthInches,
        )
    }

    /**
     * Takes LocalDateTime and returns date as encodable string
     * ie. jan 2nd, 2022 -> 01022022
     */
    private fun LocalDateTime.toEncodableString(): String {
        return this.monthValue.toString().padStart(2, '0') + this.dayOfMonth.toString()
            .padStart(2, '0') + this.year.toString()
    }

    /**
     * Takes custom coded date string to LocalDateTime
     * ie. 01022022 -> jan 2nd, 2022
     */
    private fun String.toLocalDateTime(): LocalDateTime {
        val month = this.substring(0, 2).toInt()
        val day = this.substring(2, 4).toInt()
        val year = this.substring(4, 8).toInt()

        return LocalDateTime.now().withMonth(month).withDayOfMonth(day).withYear(year)
    }
    
    private fun Collection<String>.getValueFromArray(identifier: String): String? {
        return find { it.startsWith(identifier) }?.removePrefix(identifier)
    }
}

