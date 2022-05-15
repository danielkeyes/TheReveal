package dev.danielkeyes.thereveal.dataobject

import java.time.LocalDateTime

//TODO: Length should have been a float
//TODO: DOB needs to have time as well
data class RevealDO(
    val revealType: RevealType = RevealType.NONE,
    val firstName: String? = null,
    val middleName: String? = null,
    val lastName: String? = null,
    val gender: Gender? = Gender.BOY,
    val dob: LocalDateTime? = LocalDateTime.now(),
    val weightLbs: Int? = 7,
    val weightOunces: Int? = 11,
    val lengthInches: Int? = 19,
)

enum class Gender{
    BOY, GIRL
}

enum class RevealType{
    GENDER, BIRTH, EXPECTING, NONE
}

/**
 * Checks is RevealDO is valid and returns errorState and errorMessage
 * @return Pair<Boolean, String> errorState and error message
 */
fun RevealDO.validate(): Pair<Boolean, String>{
    return when (this.revealType) {
        RevealType.GENDER -> { // Gender Reveal - requires gender
            if( gender == null) {
                Pair(true, "Gender required for gender reveal")
            } else {
                Pair(false, "")
            }
        }
        RevealType.BIRTH -> { // Birth Reveal - requires all but middle and last name
            if(this.firstName == null|| this.firstName.isEmpty() ) {
                Pair(true, "Firstname required for birth reveal")
            } else if (this.gender == null) {
                Pair(true, "Gender required for birth reveal")
            } else if (this.dob == null) {
                Pair(true, "DOB required for birth reveal")
            } else if (this.weightLbs == null) {
                Pair(true, "Weight in lbs required for birth reveal")
            } else if (this.weightOunces == null) {
                Pair(true, "Weight in ounces required for birth reveal")
            } else if (this.lengthInches == null) {
                Pair(true, "Length in inches required for birth reveal")
            } else {
                Pair(false, "")
            }
        }
        else -> {
            Pair(true, "Reveal type required")
        }
    }
}

/**
 * Used for testing
 */
fun getSampleRevealDO(): RevealDO {
    return RevealDO(
        revealType = RevealType.BIRTH,
        firstName = "firstName",
        middleName = "middleName",
        lastName = "lastName",
        gender = Gender.BOY,
        dob = LocalDateTime.now(),
        weightLbs = 7,
        weightOunces = 15,
        lengthInches = 18,
    )
}
