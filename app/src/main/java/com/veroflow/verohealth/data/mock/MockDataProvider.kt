package com.veroflow.verohealth.data.mock

import com.veroflow.verohealth.data.model.AvailabilityWindow
import com.veroflow.verohealth.data.model.Doctor
import com.veroflow.verohealth.data.model.Gender
import com.veroflow.verohealth.data.model.Hospital
import com.veroflow.verohealth.data.model.Specialty
import kotlin.random.Random

/**
 * All doctor/hospital data is generated once, deterministically (fixed seed),
 * so the catalog is identical across app runs — important for VeroFlow, which
 * needs stable ground truth to validate search/filter results against.
 */
object MockDataProvider {

    val hospitals: List<Hospital> by lazy {
        listOf(
            Hospital("H-01", "Lahore General Hospital", "Lahore", "Jail Road, Lahore", 4.5),
            Hospital("H-02", "Shifa International Hospital", "Islamabad", "Sector H-8/4, Islamabad", 4.7),
            Hospital("H-03", "Aga Khan University Hospital", "Karachi", "Stadium Road, Karachi", 4.8),
            Hospital("H-04", "Doctors Hospital", "Lahore", "Canal Bank Road, Lahore", 4.3),
            Hospital("H-05", "South City Hospital", "Karachi", "Clifton, Karachi", 4.2)
        )
    }

    private val firstNames = listOf(
        "Ahmed", "Sara", "Bilal", "Ayesha", "Hassan", "Fatima", "Usman", "Mariam",
        "Ali", "Sana", "Omar", "Hira", "Zeeshan", "Nadia", "Kamran", "Iqra",
        "Tariq", "Rabia", "Faisal", "Amna"
    )
    private val lastNames = listOf(
        "Khan", "Malik", "Iqbal", "Butt", "Sheikh", "Chaudhry", "Raza", "Farooq",
        "Aslam", "Javed", "Qureshi", "Baig"
    )
    private val languagePool = listOf("English", "Urdu", "Punjabi", "Arabic")

    val doctors: List<Doctor> by lazy {
        val rng = Random(42) // fixed seed -> deterministic roster
        (1..120).map { index ->
            val specialty = Specialty.entries[rng.nextInt(Specialty.entries.size)]
            val hospital = hospitals[rng.nextInt(hospitals.size)]
            val gender = if (rng.nextBoolean()) Gender.MALE else Gender.FEMALE
            val firstName = firstNames[rng.nextInt(firstNames.size)]
            val lastName = lastNames[rng.nextInt(lastNames.size)]
            val experience = rng.nextInt(2, 30)
            val rating = 3.5 + rng.nextInt(0, 16) / 10.0 // 3.5 - 5.0
            Doctor(
                id = "D-%04d".format(index),
                name = "Dr. $firstName $lastName",
                specialty = specialty,
                hospital = hospital,
                qualification = qualificationFor(specialty),
                experienceYears = experience,
                rating = rating,
                reviewCount = rng.nextInt(10, 500),
                consultationFee = (1500 + rng.nextInt(0, 20) * 250).toDouble(),
                gender = gender,
                biography = "Dr. $firstName $lastName is a ${specialty.label} specialist with " +
                    "$experience years of experience at ${hospital.name}, focused on " +
                    "patient-centered, evidence-based care.",
                languages = languagePool.shuffled(rng).take(rng.nextInt(1, 3)),
                weeklyAvailability = defaultAvailability(),
                isAvailableToday = rng.nextInt(0, 100) < 70
            )
        }
    }

    private fun qualificationFor(specialty: Specialty): String = when (specialty) {
        Specialty.CARDIOLOGY -> "MBBS, FCPS (Cardiology)"
        Specialty.DERMATOLOGY -> "MBBS, FCPS (Dermatology)"
        Specialty.PEDIATRICS -> "MBBS, FCPS (Pediatrics)"
        Specialty.ORTHOPEDICS -> "MBBS, FCPS (Orthopedic Surgery)"
        Specialty.NEUROLOGY -> "MBBS, FCPS (Neurology)"
        Specialty.GYNECOLOGY -> "MBBS, FCPS (Gynecology & Obstetrics)"
        Specialty.GENERAL_MEDICINE -> "MBBS, MRCP"
        Specialty.ENT -> "MBBS, FCPS (ENT)"
        Specialty.OPHTHALMOLOGY -> "MBBS, FCPS (Ophthalmology)"
        Specialty.PSYCHIATRY -> "MBBS, FCPS (Psychiatry)"
        Specialty.DENTISTRY -> "BDS, FCPS (Dentistry)"
        Specialty.PULMONOLOGY -> "MBBS, FCPS (Pulmonology)"
    }

    private fun defaultAvailability(): List<AvailabilityWindow> = listOf(
        AvailabilityWindow("Monday", "09:00", "13:00"),
        AvailabilityWindow("Wednesday", "14:00", "18:00"),
        AvailabilityWindow("Friday", "09:00", "13:00")
    )

    fun doctorById(id: String): Doctor? = doctors.firstOrNull { it.id == id }
}
