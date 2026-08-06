package com.veroflow.verohealth.data.model

data class MedicalRecord(
    val id: String,
    val visitDate: String,
    val doctorName: String,
    val department: String,
    val hospitalName: String,
    val symptoms: String,
    val diagnosis: String,
    val treatment: String,
    val hasPrescription: Boolean,
    val hasAttachments: Boolean
)

enum class LabCategory(val label: String) {
    BLOOD("Blood Tests"), IMAGING("Imaging"), URINE("Urine Tests"), OTHER("Others")
}

enum class ReportStatus { READY, PENDING, REVIEWED }

data class LabResultRow(val testName: String, val result: String, val referenceRange: String, val flag: String)

data class LabReport(
    val id: String,
    val testName: String,
    val category: LabCategory,
    val date: String,
    val doctorName: String,
    val laboratoryName: String,
    val status: ReportStatus,
    val patientRemarks: String,
    val doctorRemarks: String,
    val results: List<LabResultRow>
)

data class PrescribedMedicine(
    val name: String,
    val dosage: String,
    val frequency: String,
    val duration: String,
    val instructions: String // e.g. "After Meals", "Morning"
)

data class Prescription(
    val id: String,
    val doctorName: String,
    val date: String,
    val medicines: List<PrescribedMedicine>
)
