# VeroHealth Demo

A fully offline, mock-data healthcare management Android app, built as a demo/test
target for **VeroFlow** (AI-powered automated Android testing). Kotlin + Jetpack
Compose + Material 3, implementing the full 36-screen SRS spec.

## Status: all 36 screens implemented

1. Splash · 2. Onboarding · 3. Welcome · 4. Patient Registration · 5. Login ·
6. Forgot Password · 7. Dashboard · 8. Find Doctor · 9. Doctor Details ·
10. Appointment Calendar · 11. Time Slot Selection · 12. Appointment Summary ·
13. Payment · 14. Appointment Confirmation · 15. Upcoming Appointments ·
16. Appointment Details · 17. Medical Records · 18. Laboratory Reports ·
19. Laboratory Report Viewer · 20. Digital Prescription · 21. Medication Reminder ·
22. Health Dashboard · 23. Add Health Reading · 24. Emergency Services ·
25. Doctor Chat (list + window) · 26. Video Consultation · 27. Medical Document
Center · 28. Insurance Information · 29. Notifications · 30. Health Insights ·
31. Patient Profile · 32. Edit Profile · 33. Settings · 34. Help & Support ·
35. About · 36. Logout Dialog

Every screen navigates from the Dashboard or another in-flow screen — nothing
is orphaned. Bottom navigation (Home / Appointments / Records / Messages /
Profile) and a Settings icon are on the Dashboard top bar.

## Deterministic test hooks for VeroFlow

- **Demo login** (always works): `demo.patient@verohealth.test` / `Passw0rd!`
- **Locked account**: any login email containing `"locked"` always fails
  ("account disabled").
- **Duplicate registration**: registering with an email already in the mock
  store always fails ("account already exists").
- **Fully booked dates / disabled time slots**: deterministic per
  doctor+date (seeded hash), so the same doctor/date always shows the same
  availability across runs.
- **Payment decline**: any card number ending in `0000` always fails
  ("declined by issuing bank"); any other card, or any non-card method,
  succeeds.
- **120 doctors, 5 hospitals**: generated with a fixed random seed, so the
  catalog (names, specialties, fees, ratings, availability) is identical
  across app runs.

## Project structure

```
app/src/main/java/com/veroflow/verohealth/
  MainActivity.kt, VeroHealthApp.kt
  data/
    model/          Patient, Doctor, Appointment, ClinicalRecords, HealthMetrics, SupportModels
    mock/           MockDataProvider (doctors/hospitals), ClinicalMockData (records/labs/rx)
    repository/
      AuthRepository        — mock auth "backend"
      AppointmentRepository — booking calendar/slots/payment simulation
      HealthDataRepository  — medications, readings, contacts, chat, docs, insurance,
                              notifications, insights (all in-memory, mutableStateListOf)
      Session               — logged-in patient, theme mode, first-launch flag
      BookingFlowState      — in-progress booking selections (doctor/date/time/type)
      SelectionState        — currently-viewed Appointment / LabReport for detail screens
      Validators            — shared field validation rules
  ui/
    navigation/    Routes.kt, NavGraph.kt — full NavHost, all 36 destinations wired
    theme/         Material3 theme, color palette, typography, light/dark/system modes
    screens/       splash, onboarding, welcome, auth, dashboard, doctor, appointment,
                   records, health, emergency, chat, video, documents, notifications,
                   profile, settings — one folder per module
```

### Design choices worth knowing about

- **No ViewModel layer.** State lives in a handful of plain Kotlin `object`
  repositories using `mutableStateOf`/`mutableStateListOf`, read directly by
  Composables. For a demo app this size it keeps every module readable in
  isolation — trade real for simplicity if this ever needs to scale past a
  test fixture.
- **Complex objects cross screens via state holders, not nav arguments.**
  `BookingFlowState` and `SelectionState` hold the "currently selected"
  Doctor/Appointment/LabReport; the previous screen sets it right before
  navigating. Only Doctor Chat uses a real NavHost string argument
  (`conversationId`), since chat needs a stable ID across process state.
- **Cross-screen state propagation** is real, not simulated: a health
  reading added in Add Health Reading immediately shows up on the Health
  Dashboard's charts; marking a medication taken updates its status
  wherever it's shown (Dashboard card, Medication Reminder list); adding a
  reminder from a Prescription creates a real entry in Medication Reminder.

## Building

This environment cannot download the Android SDK or Gradle distribution
(no network access to `dl.google.com` / Maven Google), so the project
**cannot be compiled here**. To build it yourself:

1. Open the project root in Android Studio (Koala or newer).
2. Let Gradle sync — it will fetch AGP, Kotlin, Compose, Navigation, and
   Coil dependencies from Google/Maven Central.
3. Run on an emulator or device with API 26+.

Every file was written and hand-reviewed for Compose/Kotlin correctness
against Compose BOM 2024.06, but since none of it has gone through an
actual Gradle build in this environment, please do a first sync/build on
your end and flag anything that doesn't compile — I'll fix it immediately.
