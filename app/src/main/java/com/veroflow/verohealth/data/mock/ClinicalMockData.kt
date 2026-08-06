package com.veroflow.verohealth.data.mock

import com.veroflow.verohealth.data.model.LabCategory
import com.veroflow.verohealth.data.model.LabReport
import com.veroflow.verohealth.data.model.LabResultRow
import com.veroflow.verohealth.data.model.MedicalRecord
import com.veroflow.verohealth.data.model.PrescribedMedicine
import com.veroflow.verohealth.data.model.Prescription
import com.veroflow.verohealth.data.model.ReportStatus

object ClinicalMockData {

    val medicalRecords: List<MedicalRecord> by lazy {
        listOf(
            MedicalRecord(
                "MR-001", "2026-06-14", "Dr. Ahmed Khan", "Cardiology", "Shifa International Hospital",
                "Occasional chest tightness during exertion",
                "Mild hypertension", "Prescribed lifestyle changes and low-dose medication",
                hasPrescription = true, hasAttachments = true
            ),
            MedicalRecord(
                "MR-002", "2026-04-02", "Dr. Sana Malik", "Dermatology", "Aga Khan University Hospital",
                "Persistent skin rash on forearm",
                "Contact dermatitis", "Topical corticosteroid cream, 2 weeks",
                hasPrescription = true, hasAttachments = false
            ),
            MedicalRecord(
                "MR-003", "2025-12-20", "Dr. Bilal Iqbal", "General Medicine", "Lahore General Hospital",
                "Fever and sore throat",
                "Viral pharyngitis", "Rest, fluids, symptomatic treatment",
                hasPrescription = true, hasAttachments = false
            ),
            MedicalRecord(
                "MR-004", "2025-09-08", "Dr. Fatima Raza", "Orthopedics", "Doctors Hospital",
                "Lower back pain after lifting",
                "Lumbar muscle strain", "Physiotherapy referral, pain management",
                hasPrescription = false, hasAttachments = true
            )
        )
    }

    val labReports: List<LabReport> by lazy {
        listOf(
            LabReport(
                "LAB-001", "Complete Blood Count", LabCategory.BLOOD, "2026-06-15",
                "Dr. Ahmed Khan", "VeroLab Diagnostics", ReportStatus.REVIEWED,
                patientRemarks = "", doctorRemarks = "Values within acceptable range overall.",
                results = listOf(
                    LabResultRow("Hemoglobin", "13.8 g/dL", "13.0 - 17.0 g/dL", "Normal"),
                    LabResultRow("WBC Count", "7.2 x10^9/L", "4.0 - 11.0 x10^9/L", "Normal"),
                    LabResultRow("Platelets", "245 x10^9/L", "150 - 450 x10^9/L", "Normal")
                )
            ),
            LabReport(
                "LAB-002", "Lipid Profile", LabCategory.BLOOD, "2026-06-15",
                "Dr. Ahmed Khan", "VeroLab Diagnostics", ReportStatus.REVIEWED,
                patientRemarks = "", doctorRemarks = "LDL slightly elevated — recommend dietary changes.",
                results = listOf(
                    LabResultRow("Total Cholesterol", "215 mg/dL", "< 200 mg/dL", "High"),
                    LabResultRow("LDL", "142 mg/dL", "< 100 mg/dL", "High"),
                    LabResultRow("HDL", "48 mg/dL", "> 40 mg/dL", "Normal")
                )
            ),
            LabReport(
                "LAB-003", "Chest X-Ray", LabCategory.IMAGING, "2025-09-10",
                "Dr. Fatima Raza", "VeroImaging Center", ReportStatus.READY,
                patientRemarks = "", doctorRemarks = "No acute abnormality detected.",
                results = listOf(LabResultRow("Impression", "Normal study", "—", "Normal"))
            ),
            LabReport(
                "LAB-004", "Urinalysis", LabCategory.URINE, "2025-12-21",
                "Dr. Bilal Iqbal", "VeroLab Diagnostics", ReportStatus.PENDING,
                patientRemarks = "", doctorRemarks = "",
                results = emptyList()
            )
        )
    }

    val prescriptions: List<Prescription> by lazy {
        listOf(
            Prescription(
                "RX-001", "Dr. Ahmed Khan", "2026-06-14",
                listOf(
                    PrescribedMedicine("Amlodipine 5mg", "1 tablet", "Once daily", "30 days", "Morning, After Meals"),
                    PrescribedMedicine("Aspirin 75mg", "1 tablet", "Once daily", "30 days", "After Meals")
                )
            ),
            Prescription(
                "RX-002", "Dr. Sana Malik", "2026-04-02",
                listOf(
                    PrescribedMedicine("Betamethasone Cream", "Thin layer", "Twice daily", "14 days", "Morning and Evening")
                )
            ),
            Prescription(
                "RX-003", "Dr. Bilal Iqbal", "2025-12-20",
                listOf(
                    PrescribedMedicine("Paracetamol 500mg", "1-2 tablets", "Every 6 hours as needed", "5 days", "After Meals")
                )
            )
        )
    }
}
