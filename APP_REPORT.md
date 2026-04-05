# 📱 MediScan — Detailed App Documentation

## Comprehensive Feature & Screen Documentation

> **Package:** `com.mediscan.app`  
> **Architecture:** MVVM + Clean Architecture  
> **UI Framework:** Jetpack Compose (Material 3)  
> **Build System:** Gradle (Kotlin DSL) with Version Catalog

---

## 📖 Table of Contents

1. [Project Structure](#1-project-structure)
2. [Dependencies & Library Catalog](#2-dependencies--library-catalog)
3. [Core Infrastructure](#3-core-infrastructure)
4. [Authentication System](#4-authentication-system)
5. [Patient Module](#5-patient-module)
6. [Doctor Module](#6-doctor-module)
7. [Booking System](#7-booking-system)
8. [Notification System](#8-notification-system)
9. [Nearby Hospitals (Maps)](#9-nearby-hospitals-maps)
10. [Camera & Scan System](#10-camera--scan-system)
11. [AI Extraction Pipeline](#11-ai-extraction-pipeline)
12. [Data Layer](#12-data-layer)
13. [UI Components Library](#13-ui-components-library)
14. [Navigation Architecture](#14-navigation-architecture)
15. [Firebase Setup & Configuration](#15-firebase-setup--configuration)

---

## 1. Project Structure

```
app/src/main/java/com/mediscan/app/
│
├── MediScanApp.kt                          # @HiltAndroidApp Application class
├── MainActivity.kt                          # Single Activity (setContent → NavGraph)
│
├── core/
│   ├── constants/
│   │   └── ApiEndpoints.kt                 # Dynamic base URL (emulator vs physical)
│   ├── navigation/
│   │   ├── NavGraph.kt                     # Full navigation graph (22 routes)
│   │   └── Routes.kt                       # Route constants object
│   ├── theme/
│   │   ├── Color.kt                        # Material 3 color palette
│   │   ├── Theme.kt                        # MediScanTheme composable
│   │   └── Type.kt                         # Typography (Poppins + Roboto)
│   └── utils/
│       ├── DateUtils.kt                    # Date formatting utilities
│       ├── MedicalSuggestions.kt           # Autocomplete data (diagnoses, tests)
│       ├── NetworkResult.kt                # Sealed class (Idle/Loading/Success/Error)
│       └── PreferencesManager.kt           # EncryptedSharedPreferences with fallback
│
├── data/
│   ├── model/
│   │   ├── Appointment.kt                  # Appointment data class
│   │   ├── DoctorOrder.kt                  # Doctor-written prescription model
│   │   ├── ExtractionResult.kt             # AI extraction response model
│   │   ├── Medication.kt                   # Single medication data class
│   │   ├── Notification.kt                 # Notification data class
│   │   ├── Prescription.kt                 # Prescription data class
│   │   └── User.kt                         # User data class (patient + doctor fields)
│   ├── remote/
│   │   └── FastApiService.kt               # Retrofit interface (health, quality, extract)
│   └── repository/
│       ├── AppointmentRepository.kt         # Firestore appointment CRUD
│       ├── AuthRepository.kt                # Firebase Auth operations
│       ├── NotificationRepository.kt        # Firestore notification CRUD
│       ├── PrescriptionRepository.kt        # Firestore + FastAPI + Storage operations
│       └── UserRepository.kt                # Firestore user profile CRUD
│
├── di/
│   ├── AppModule.kt                         # Hilt module: Firebase instances
│   └── NetworkModule.kt                     # Hilt module: Retrofit + OkHttp
│
└── ui/
    ├── components/
    │   ├── AppointmentCard.kt               # Reusable appointment card
    │   ├── ExtractionResultSheet.kt         # Bottom sheet for AI results
    │   ├── QuickActionCard.kt               # Home screen action button cards
    │   └── common/
    │       ├── MediButton.kt                # Styled button component
    │       ├── MediCard.kt                  # Styled card component
    │       ├── MediTextField.kt             # Styled text field component
    │       └── ShimmerEffects.kt            # Shimmer loading placeholders
    │
    ├── viewmodel/
    │   ├── AuthViewModel.kt                 # Login/signup/Google sign-in state
    │   ├── BookingViewModel.kt              # Doctor search + appointment booking
    │   ├── DocsViewModel.kt                 # Prescription list loading
    │   ├── DoctorViewModel.kt               # Doctor dashboard state
    │   ├── NotificationViewModel.kt         # Real-time notification listener
    │   ├── PatientViewModel.kt              # Patient dashboard state
    │   └── ScanViewModel.kt                 # Camera/gallery capture + AI extraction
    │
    └── screens/
        ├── splash/
        │   └── SplashScreen.kt              # Animated splash → auth check → navigate
        ├── auth/
        │   ├── LoginScreen.kt               # Email/password + Google Sign-In
        │   └── SignUpScreen.kt              # Registration with role selection
        ├── patient/
        │   ├── PatientMainScreen.kt         # Bottom nav scaffold (Home/Scan/Docs/Profile)
        │   ├── home/
        │   │   └── PatientHomeScreen.kt     # Dashboard with quick actions + recent Rx
        │   ├── scan/
        │   │   ├── ScanScreen.kt            # Camera + Gallery capture (741 lines)
        │   │   └── CameraPreviewScreen.kt   # CameraX preview composable
        │   ├── docs/
        │   │   ├── DocsScreen.kt            # Prescription history list
        │   │   ├── PrescriptionDetailScreen.kt  # Single Rx detail view
        │   │   └── PrescriptionDetailViewModel.kt
        │   ├── medicine/
        │   │   └── BuyMedicineScreen.kt     # Medicine browsing/purchase UI
        │   ├── orders/
        │   │   └── DoctorOrdersScreen.kt    # Prescriptions from doctors
        │   └── profile/
        │       ├── PatientProfileScreen.kt  # Profile view
        │       ├── EditProfileScreen.kt     # Edit personal info
        │       └── ChangePasswordScreen.kt  # Password change
        ├── doctor/
        │   ├── DoctorMainScreen.kt          # Bottom nav scaffold (Appointments/Records/Profile)
        │   ├── appointments/
        │   │   ├── DoctorAppointmentsScreen.kt  # Appointment list + management
        │   │   ├── PatientDetailSheet.kt    # Patient info bottom sheet
        │   │   └── PatientRecordsScreen.kt  # View patient's Rx history
        │   ├── records/
        │   │   └── DoctorRecordsScreen.kt   # Analytics dashboard with Vico charts
        │   └── profile/
        │       ├── DoctorProfileScreen.kt   # Profile view
        │       ├── DoctorEditProfileScreen.kt  # Edit doctor info
        │       └── DoctorChangePasswordScreen.kt  # Password change
        ├── booking/
        │   ├── DoctorSearchScreen.kt        # Search/filter doctors
        │   ├── DoctorDetailScreen.kt        # Doctor info + book button
        │   └── PatientAppointmentsScreen.kt # Patient's appointments list
        ├── hospitals/
        │   └── NearbyHospitalsScreen.kt     # Google Maps with location markers
        └── notifications/
            └── NotificationsScreen.kt       # Notification list + mark read
```

---

## 2. Dependencies & Library Catalog

### Build Configuration

| Property | Value |
|----------|-------|
| **AGP** | 8.13.2 |
| **Kotlin** | 2.0.21 |
| **KSP** | 2.0.21-1.0.27 |
| **Compose BOM** | 2024.12.01 |
| **Min SDK** | 24 (Android 7.0) |
| **Target SDK** | 35 (Android 15) |
| **Compile SDK** | 35 |

### Library Versions

| Library | Version | Purpose | Used In |
|---------|---------|---------|---------|
| **Hilt** | 2.53.1 | Dependency Injection | AppModule, NetworkModule, all ViewModels |
| **Retrofit** | 2.11.0 | HTTP client | FastApiService, ScanViewModel |
| **OkHttp** | 4.12.0 | HTTP interceptor/logging | NetworkModule |
| **Gson** | 2.11.0 | JSON serialization | Retrofit converter |
| **Firebase BOM** | 33.7.0 | Firebase version management | — |
| **Firebase Auth** | (BOM managed) | Authentication | AuthRepository, AuthViewModel |
| **Firebase Firestore** | (BOM managed) | Cloud database | All repositories |
| **Firebase Storage** | (BOM managed) | Image storage | PrescriptionRepository |
| **Firebase Messaging** | (BOM managed) | Push notifications | (Available, not fully used) |
| **Play Services Auth** | 21.3.0 | Google Sign-In | LoginScreen, AuthViewModel |
| **CameraX** | 1.4.1 | Camera capture | ScanScreen, CameraPreviewScreen |
| **ExifInterface** | 1.3.7 | Image rotation metadata | ScanViewModel (gallery path) |
| **Coil** | 2.7.0 | Async image loading | Avatars, profile pictures |
| **Vico** | 2.0.0-beta.2 | Compose charts | DoctorRecordsScreen |
| **Maps Compose** | 6.2.1 | Google Maps | NearbyHospitalsScreen |
| **Play Services Maps** | 19.0.0 | Maps SDK | NearbyHospitalsScreen |
| **Play Services Location** | 21.3.0 | Location services | NearbyHospitalsScreen |
| **Navigation Compose** | 2.8.5 | Screen routing | NavGraph |
| **Room** | 2.6.1 | Local SQLite | (Declared, not implemented) |
| **WorkManager** | 2.10.0 | Background tasks | (Available for reminders) |
| **Security Crypto** | 1.1.0-alpha06 | Encrypted storage | PreferencesManager |
| **Accompanist Permissions** | 0.36.0 | Runtime permissions | ScanScreen, NearbyHospitalsScreen |
| **Shimmer** | 1.3.2 | Loading animations | DocsScreen, Appointments, etc. |
| **Material Icons Extended** | 1.7.6 | Extended icon set | All screens |

---

## 3. Core Infrastructure

### 3.1 Application Entry Point

**`MediScanApp.kt`** — Annotated with `@HiltAndroidApp` to enable Hilt dependency injection across the entire app.

**`MainActivity.kt`** — Single Activity architecture. Sets up `MediScanTheme` and renders the `NavGraph` composable. All screens are Compose destinations within the single Activity.

### 3.2 Theme System

| File | Purpose |
|------|---------|
| **Color.kt** | Defines the Material 3 color palette. Primary gradient: `Color(0xFF1A237E)` → `Color(0xFF3F51B5)` → `Color(0xFF5C6BC0)`. Background: `Color(0xFFF4F6FB)` |
| **Theme.kt** | `MediScanTheme` composable wrapping `MaterialTheme` with custom color scheme |
| **Type.kt** | Typography using **Poppins** (headings) and **Roboto** (body text) |

### 3.3 API Endpoints

**`ApiEndpoints.kt`** dynamically selects the base URL using a three-way selection system:
- **☁️ Cloud (Railway):** `https://capstone-production-59e8.up.railway.app/` — used when `USE_CLOUD = true` (default, recommended)
- **Emulator:** `http://10.0.2.2:8000/` (Android emulator → host loopback) — used when `USE_CLOUD = false`
- **Physical device:** `http://10.136.147.203:8000/` (current WiFi IP) — used when `USE_CLOUD = false`

Detection uses `Build.FINGERPRINT`, `Build.MODEL`, and `Build.MANUFACTURER` to identify emulators when not using cloud mode.

Also defines Firestore collection names (`users`, `prescriptions`, `appointments`, `notifications`, `reminders`) and Firebase Storage paths.

### 3.4 Preferences Manager

**`PreferencesManager.kt`** provides secure token/session storage:
- **Primary:** `EncryptedSharedPreferences` with AES256 keys
- **Fallback:** Regular `SharedPreferences` if encryption fails
- **Auto-recovery:** Detects corruption, deletes corrupted files, recreates fresh

### 3.5 Network Result

**`NetworkResult.kt`** — Sealed class for API state management:
```kotlin
sealed class NetworkResult<out T> {
    object Idle : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String) : NetworkResult<Nothing>()
}
```
Used by all ViewModels to represent async operation states. UI observes via `collectAsState()`.

### 3.6 Medical Suggestions

**`MedicalSuggestions.kt`** — Static lists of common:
- **Diagnoses** (Fever, Gastritis, Hypertension, Diabetes, etc.)
- **Medical tests** (CBC, Urine R/E, Blood Sugar, Lipid Profile, etc.)
- **Dose schedules** (1+0+0, 1+0+1, 1+1+1, etc.)

Used in `ExtractionResultSheet` for autocomplete suggestions when editing extraction results.

### 3.7 Dependency Injection

**`AppModule.kt`** (Hilt `@Module`):
- Provides `FirebaseAuth` singleton
- Provides `FirebaseFirestore` singleton
- Provides `FirebaseStorage` singleton

**`NetworkModule.kt`** (Hilt `@Module`):
- Provides `OkHttpClient` with logging interceptor and custom timeouts (30s connect, 90s read/write)
- Provides `Retrofit` instance with Gson converter and dynamic base URL from `ApiEndpoints`
- Provides `FastApiService` interface implementation

---

## 4. Authentication System

### 4.1 Overview

| Feature | Implementation |
|---------|---------------|
| **Email/Password Login** | `FirebaseAuth.signInWithEmailAndPassword()` |
| **Email/Password Registration** | `FirebaseAuth.createUserWithEmailAndPassword()` |
| **Google Sign-In** | `GoogleSignIn` SDK → `FirebaseAuth.signInWithCredential()` |
| **Role Selection** | Patient or Doctor — stored in Firestore `users/{uid}.userType` |
| **Session Persistence** | Firebase Auth automatically persists sessions |
| **Password Reset** | `FirebaseAuth.sendPasswordResetEmail()` |

### 4.2 Screens

#### SplashScreen.kt
- Animated MediScan logo and branding
- Checks `FirebaseAuth.currentUser` on launch
- If authenticated → reads `userType` from Firestore → navigates to Patient or Doctor home
- If not authenticated → navigates to Login

#### LoginScreen.kt
- Email + password text fields with validation
- "Sign in with Google" button (Google One-Tap)
- "Forgot Password?" link
- "Don't have an account? Sign Up" link
- Gradient header with app branding
- Error messages displayed as Snackbar

#### SignUpScreen.kt
- Full name, email, phone, password fields
- **Role selector** — Patient or Doctor radio buttons
- Doctor-specific fields: license number, specialization, hospital
- Validation: email format, password length (6+), required fields
- On success → creates Firebase Auth account + Firestore user document

### 4.3 AuthViewModel

**Key State:**
```kotlin
val loginState: StateFlow<NetworkResult<FirebaseUser>>
val signUpState: StateFlow<NetworkResult<FirebaseUser>>
val googleSignInState: StateFlow<NetworkResult<FirebaseUser>>
```

**Key Functions:**
- `login(email, password)` — email/password auth
- `signUp(email, password, name, phone, userType, ...)` — registration + Firestore profile creation
- `signInWithGoogle(idToken)` — Google credential auth
- `logout()` — sign out + clear preferences
- `resetPassword(email)` — send password reset email

### 4.4 Firebase Configuration Requirements

To enable Google Sign-In, the following must be configured:
1. **SHA-1 and SHA-256 fingerprints** added to Firebase Console → Project Settings → Android app
2. **`google-services.json`** downloaded and placed in `app/` directory
3. **Google Sign-In provider** enabled in Firebase Console → Authentication → Sign-in method
4. **Web Client ID** from `google-services.json` → `oauth_client` with type 3

---

## 5. Patient Module

### 5.1 PatientMainScreen.kt

Bottom navigation scaffold with 4 tabs:
| Tab | Icon | Route | Screen |
|-----|------|-------|--------|
| **Home** | 🏠 | `patient_home` | PatientHomeScreen |
| **Scan** | 📷 | `patient_scan` | ScanScreen |
| **Docs** | 📄 | `patient_docs` | DocsScreen |
| **Profile** | 👤 | `patient_profile` | PatientProfileScreen |

Notification bell icon in top app bar with real-time unread count badge.

### 5.2 PatientHomeScreen.kt

Dashboard with:
- **Welcome header** with user's name and avatar
- **Quick Action cards** (grid layout):
  - Scan Prescription → navigates to ScanScreen
  - My Documents → navigates to DocsScreen
  - Book Appointment → navigates to DoctorSearchScreen
  - Nearby Hospitals → navigates to NearbyHospitalsScreen
  - Buy Medicine → navigates to BuyMedicineScreen
  - Doctor Orders → navigates to DoctorOrdersScreen
- **Recent Prescriptions** section — last 3 prescriptions from Firestore
- **Upcoming Appointments** section — next appointments with view details

### 5.3 ScanScreen.kt (741 lines)

The most complex screen in the app. Two capture modes:

**Camera Mode:**
1. Requests camera permission via Accompanist Permissions
2. Shows CameraX preview with flash toggle and camera flip buttons
3. Capture button takes photo → converts to JPEG bytes
4. Sends to `ScanViewModel.processImage(bytes)`

**Gallery Mode:**
1. "Choose from Gallery" button at bottom
2. Uses `ActivityResultContracts.GetContent("image/*")`
3. Selected URI → `ScanViewModel.processGalleryImage(uri, context)`
4. Processing on `Dispatchers.IO`:
   - `BitmapFactory.decodeStream()` — handles all formats (HEIC, WebP, PNG, JPEG)
   - `ExifInterface` — reads rotation metadata
   - `Matrix.postRotate()` — applies correct orientation
   - `Bitmap.compress(JPEG, 95)` — re-encodes to JPEG

**After capture (both paths):**
1. Shows loading overlay ("Analyzing prescription...")
2. Receives `ExtractionResult` from ViewModel
3. Opens `ExtractionResultSheet` modal bottom sheet
4. User edits/confirms extracted data
5. Saves to Firestore + uploads image to Firebase Storage

### 5.4 DocsScreen.kt

- Lists all prescriptions for the current patient from Firestore
- **Shimmer loading** effect while data loads
- Each card shows: doctor name, date, medication count, diagnosis
- Tap → navigates to `PrescriptionDetailScreen`
- Pull-to-refresh support

### 5.5 PrescriptionDetailScreen.kt

- Full prescription view with scanned image (loaded from Firebase Storage via SDK)
- Doctor information section
- List of medications with dose, schedule, duration
- Diagnosis and test information
- Date and metadata
- **LRU cached images** — `PrescriptionImageCache` stores downloaded bitmaps

### 5.6 BuyMedicineScreen.kt

- Medicine browsing interface
- Product cards with medicine names, prices, descriptions
- Search/filter functionality
- Category-based browsing

### 5.7 DoctorOrdersScreen.kt

- Lists prescriptions written by doctors for this patient
- Shows doctor name, date, medications prescribed
- Enables patients to view doctor-prescribed medications

### 5.8 Profile Screens

**PatientProfileScreen.kt:**
- Displays user info: name, email, phone, blood group, date of birth, address
- Edit Profile button → `EditProfileScreen`
- Change Password button → `ChangePasswordScreen`
- Logout button (with confirmation dialog)

**EditProfileScreen.kt:**
- Editable fields for all patient-specific info
- Saves to Firestore `users/{uid}` document
- Validation on required fields

**ChangePasswordScreen.kt:**
- Current password verification
- New password + confirmation
- Uses `FirebaseAuth.currentUser.updatePassword()`

---

## 6. Doctor Module

### 6.1 DoctorMainScreen.kt

Bottom navigation scaffold with 3 tabs:
| Tab | Icon | Route | Screen |
|-----|------|-------|--------|
| **Appointments** | 📅 | `doctor_appointments` | DoctorAppointmentsScreen |
| **Records** | 📊 | `doctor_records` | DoctorRecordsScreen |
| **Profile** | 👤 | `doctor_profile` | DoctorProfileScreen |

Notification bell icon with unread count badge.

### 6.2 DoctorAppointmentsScreen.kt

- Lists all appointments for the doctor from Firestore
- **Shimmer loading** while fetching
- Each appointment card shows: patient name, date/time, complaint, status
- Status management: Pending → Confirmed → Completed / Cancelled
- Tap patient name → `PatientDetailSheet` (bottom sheet with patient info)
- View Records button → `PatientRecordsScreen`

### 6.3 PatientDetailSheet.kt

- Modal bottom sheet showing patient details
- Patient name, email, phone, medical info
- Quick actions: view records, write prescription

### 6.4 PatientRecordsScreen.kt

- Views a specific patient's prescription history
- Similar layout to patient's DocsScreen but from doctor's perspective
- Allows doctor to review previous prescriptions and medications

### 6.5 DoctorRecordsScreen.kt (Charts & Analytics)

**Library:** Vico (`com.patrykandpatrick.vico:compose-m3:2.0.0-beta.2`)

Features:
- **Patient statistics** — bar charts showing patient count by month
- **Appointment analytics** — completed vs cancelled trends
- **Prescription counts** — monthly prescription volume
- Uses `CartesianChartHost` and `ColumnCartesianLayer` from Vico
- Data sourced from Firestore aggregation queries

### 6.6 Doctor Profile Screens

**DoctorProfileScreen.kt:**
- Displays: name, email, specialization, hospital, license number, consultation fee, available days, time range
- Edit Profile → `DoctorEditProfileScreen`
- Change Password → `DoctorChangePasswordScreen`
- Logout

**DoctorEditProfileScreen.kt:**
- Editable fields for doctor-specific info (specialization, hospital, license, fee, availability)
- Saves to Firestore

**DoctorChangePasswordScreen.kt:**
- Same pattern as patient's password change

---

## 7. Booking System

### 7.1 DoctorSearchScreen.kt

- Search bar to find doctors by name or specialization
- Filters: specialization category
- Results from Firestore query on `users` collection (where `userType == "doctor"`)
- Each result card: doctor name, specialization, hospital, consultation fee
- Tap → `DoctorDetailScreen`

### 7.2 DoctorDetailScreen.kt

- Full doctor profile view
- Available days and time range
- Consultation fee
- **Book Appointment** button → navigates to booking flow with date/time picker
- If navigated from an existing appointment card, pre-fills appointment data

### 7.3 PatientAppointmentsScreen.kt

- Lists all appointments for the current patient
- Status color coding: Pending (yellow), Confirmed (green), Completed (blue), Cancelled (red)
- Each card shows: doctor name, specialization, date/time, status, complaint
- Tap for details or to view the doctor's profile

### 7.4 BookingViewModel.kt

**Key Functions:**
- `searchDoctors(query)` — Firestore query with name/specialization filter
- `loadDoctorDetails(doctorId)` — fetch single doctor profile
- `bookAppointment(...)` — create appointment document in Firestore
- `loadPatientAppointments()` — fetch patient's appointments
- `cancelAppointment(appointmentId)` — update status to "cancelled"

---

## 8. Notification System

### 8.1 Architecture

```
Firestore Collection: notifications/{notificationId}
├── userId: String          (recipient)
├── title: String
├── message: String
├── type: String            (appointment, prescription, system)
├── isRead: Boolean
├── createdAt: Timestamp
└── relatedId: String?      (appointmentId, prescriptionId)
```

### 8.2 Real-Time Listener

**`NotificationViewModel.kt`** sets up a Firestore snapshot listener:
```kotlin
fun startObserving(userId: String, userType: String) {
    firestore.collection("notifications")
        .whereEqualTo("userId", userId)
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .addSnapshotListener { snapshot, error -> ... }
}
```

**Unread count** is computed as a `StateFlow<Int>` and displayed as a red badge on the notification bell icon in both `PatientMainScreen` and `DoctorMainScreen`.

### 8.3 NotificationsScreen.kt

- Lists all notifications for the current user
- Unread notifications highlighted with accent background
- Tap to mark as read
- "Mark All as Read" button in top bar
- Notification types: appointment confirmations, new prescriptions, system messages

### 8.4 Notification Triggers

Notifications are created in Firestore when:
- Patient books an appointment → doctor gets notified
- Doctor confirms/cancels appointment → patient gets notified
- Doctor writes a prescription → patient gets notified

---

## 9. Nearby Hospitals (Maps)

### 9.1 NearbyHospitalsScreen.kt

**Libraries:**
- `com.google.maps.android:maps-compose:6.2.1` — Google Maps Compose
- `com.google.android.gms:play-services-maps:19.0.0` — Maps SDK
- `com.google.android.gms:play-services-location:21.3.0` — Location services
- `com.google.accompanist:accompanist-permissions:0.36.0` — Permission handling

**Features:**
1. **Location permission** requested via Accompanist Permissions
2. **Current location** obtained via `FusedLocationProviderClient`
3. **Google Maps** displayed with `GoogleMap` composable
4. **Markers** placed for nearby hospitals/pharmacies
5. **Map controls:** zoom, my-location button, map type toggle
6. **Info windows** on markers showing hospital name and distance

### 9.2 Configuration Requirements

- **Google Maps API key** must be set in `AndroidManifest.xml`:
  ```xml
  <meta-data
      android:name="com.google.android.geo.API_KEY"
      android:value="YOUR_MAPS_API_KEY" />
  ```
- Maps API must be enabled in Google Cloud Console
- Location permission: `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION`

---

## 10. Camera & Scan System

### 10.1 Architecture

```
ScanScreen.kt (UI Layer)
    ├── Camera Mode
    │   └── CameraPreviewScreen.kt (CameraX)
    │       ├── PreviewView (camera feed)
    │       ├── Flash toggle button
    │       ├── Camera flip button (front/back)
    │       └── Capture button → byteArray
    │
    └── Gallery Mode
        └── ActivityResultContracts.GetContent
            └── URI → ScanViewModel.processGalleryImage()

ScanViewModel.kt (Business Logic)
    ├── processImage(bytes) — camera path
    │   └── base64 encode → FastApiService.extractPrescription()
    │
    ├── processGalleryImage(uri, context) — gallery path (on IO thread)
    │   ├── BitmapFactory.decodeStream()
    │   ├── ExifInterface rotation correction
    │   ├── Bitmap.compress(JPEG, 95%)
    │   └── processImage(jpegBytes)
    │
    └── savePrescription(...)
        ├── Upload image → Firebase Storage
        └── Save document → Firestore
```

### 10.2 CameraX Configuration

```kotlin
val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
val preview = Preview.Builder().build()
val imageCapture = ImageCapture.Builder()
    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
    .build()
val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
```

### 10.3 Gallery Image Processing Pipeline

```
User selects image from gallery
    ↓
ContentResolver.openInputStream(uri)
    ↓
BitmapFactory.decodeStream(inputStream) — handles HEIC, WebP, PNG, JPEG
    ↓
ExifInterface(inputStream) → read TAG_ORIENTATION
    ↓
Matrix.postRotate(degrees) → correct orientation
    ↓
Bitmap.createBitmap(original, 0, 0, w, h, matrix, true)
    ↓
Bitmap.compress(CompressFormat.JPEG, 95, outputStream)
    ↓
ByteArray → Base64 → FastApiService.extractPrescription()
```

### 10.4 ExtractionResultSheet.kt

Modal bottom sheet that displays AI extraction results:

**Sections:**
1. **Quality Status** — shows quality score and pass/fail
2. **Doctor Info** — extracted doctor name and hospital (editable)
3. **Prescription Info** — date, diagnoses (with autocomplete), tests (with autocomplete)
4. **Medications List** — each medication in a card:
   - Medicine name (editable `OutlinedTextField`)
   - Dose strength (editable)
   - Schedule — **FlowRow chip grid** with common schedules (1+0+0, 1+0+1, 1+1+1, etc.)
   - Duration (editable)
   - Confidence indicators per field
5. **Save Button** — saves to Firebase

**Autocomplete Features:**
- Diagnosis field shows suggestions from `MedicalSuggestions.commonDiagnoses`
- Test field shows suggestions from `MedicalSuggestions.commonTests`
- Schedule uses FlowRow chip selector from `MedicalSuggestions.commonSchedules`
- Suggestions dismiss on focus change or selection

---

## 11. AI Extraction Pipeline

### 11.1 Backend Architecture (FastAPI — Railway Cloud)

```
FastAPI Server (Railway Cloud — HTTPS)
URL: https://capstone-production-59e8.up.railway.app/
    │
    ├── /health — Health check endpoint
    ├── /check-quality-base64 — Quick quality check
    ├── /extract-base64 — Full extraction (main endpoint)
    ├── /extract — File upload extraction
    ├── /results/{task_id} — Retrieve saved results
    └── /task/{task_id} — Delete task data
```

### 11.2 Extraction Pipeline Flow

```
Input Image (JPEG base64)
    ↓
[Stage 1] Quality Check (ResNet18 + Laplacian Blur Detection)
    ├── Good → Continue
    └── Bad → Return "rejected" with issues list
    ↓
[Stage 2] YOLOv8s Detection (9 classes)
    ├── Detects bounding boxes for: medicine, dose_strength,
    │   schedule, duration, doctor_name, hospital, date,
    │   diagnosis, test
    └── Returns: class, bbox coordinates, confidence
    ↓
[Stage 3] PaddleOCR 2.9.1 (English)
    ├── Crops each detected region
    ├── 3-attempt strategy per region:
    │   1. Raw crop → OCR
    │   2. Preprocessed (contrast enhancement) → OCR
    │   3. Enhanced (adaptive threshold) → OCR
    └── Returns best text result
    ↓
[Stage 4] Spatial Medication Grouping
    ├── Groups medicine + dose + schedule + duration
    │   by Y-coordinate proximity
    ├── Matches medicine names against 48,014-entry database
    └── Returns structured JSON
```

### 11.3 YOLO Model Details

| Property | Value |
|----------|-------|
| **Architecture** | YOLOv8s (small variant) |
| **Training** | Custom-trained on prescription images |
| **Classes** | 9 (medicine, dose_strength, schedule, duration, doctor_name, hospital, date, diagnosis, test) |
| **mAP50** | 98.6% |
| **Model Size** | ~64 MB |
| **Input Size** | 640×640 (auto-resized) |
| **Framework** | Ultralytics (PyTorch) — CPU on Railway, MPS/CUDA on local |

### 11.4 Android ↔ Backend Communication

```kotlin
// FastApiService.kt (Retrofit interface)
interface FastApiService {
    @GET("health")
    suspend fun healthCheck(): Map<String, Any>

    @POST("check-quality-base64")
    suspend fun checkQuality(@Body request: Map<String, String>): QualityCheckResponse

    @POST("extract-base64")
    suspend fun extractPrescription(@Body request: Map<String, String>): ExtractionResult
}
```

**Request format:** `{"image": "<base64_jpeg_string>"}`

**Response format:** Structured JSON with `medications[]`, `doctor{}`, `prescription_info{}`, `quality_check{}`, `stats{}`

---

## 12. Data Layer

### 12.1 Models

#### User.kt
```kotlin
data class User(
    val uid: String,
    val email: String,
    val fullName: String,
    val phone: String,
    val userType: String,           // "patient" or "doctor"
    val profileImageUrl: String?,
    // Patient fields
    val dateOfBirth: String?,
    val bloodGroup: String?,
    val address: String?,
    val emergencyContact: String?,
    // Doctor fields
    val licenseNumber: String?,
    val specialization: String?,
    val hospital: String?,
    val consultationFee: String?,
    val availableDays: List<String>?,
    val availableTimeRange: String?
)
```

#### Prescription.kt
```kotlin
data class Prescription(
    val id: String,
    val patientId: String,
    val doctorName: String?,
    val hospital: String?,
    val visitDate: Timestamp,
    val diagnosis: String?,
    val medications: List<Medication>,
    val imageUrl: String,
    val tests: List<String>?,
    val createdAt: Timestamp
)
```

#### Medication.kt
```kotlin
data class Medication(
    val medicine: String,
    val doseStrength: String?,
    val schedule: String?,
    val duration: String?,
    val confidence: Map<String, Double>?
)
```

#### Appointment.kt
```kotlin
data class Appointment(
    val id: String,
    val patientId: String,
    val patientName: String,
    val doctorId: String,
    val doctorName: String,
    val specialization: String?,
    val dateTime: Timestamp,
    val status: String,          // pending, confirmed, completed, cancelled
    val complaint: String?,
    val createdAt: Timestamp
)
```

#### Notification.kt
```kotlin
data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String,            // appointment, prescription, system
    val isRead: Boolean,
    val createdAt: Timestamp,
    val relatedId: String?
)
```

#### ExtractionResult.kt
Maps the FastAPI response JSON — includes `medications`, `doctor`, `prescription_info`, `quality_check`, `stats`, `status`, `message`.

#### DoctorOrder.kt
Represents prescriptions written by doctors for patients.

### 12.2 Repositories

| Repository | Responsibilities |
|------------|-----------------|
| **AuthRepository** | Firebase Auth operations: login, signup, Google sign-in, logout, password reset |
| **UserRepository** | Firestore `users` collection: create, read, update user profiles |
| **PrescriptionRepository** | Firestore `prescriptions` + FastAPI extraction + Firebase Storage image upload/download |
| **AppointmentRepository** | Firestore `appointments`: create, read, update status, query by patient/doctor |
| **NotificationRepository** | Firestore `notifications`: create, read, mark as read, real-time listener |

### 12.3 Remote Service

**`FastApiService.kt`** — Retrofit2 interface for communicating with the FastAPI backend:
- `healthCheck()` — GET /health
- `checkQuality(request)` — POST /check-quality-base64
- `extractPrescription(request)` — POST /extract-base64

---

## 13. UI Components Library

### 13.1 Common Components

| Component | File | Purpose |
|-----------|------|---------|
| **MediButton** | `common/MediButton.kt` | Styled gradient button with loading state |
| **MediTextField** | `common/MediTextField.kt` | Styled outlined text field with label and error state |
| **MediCard** | `common/MediCard.kt` | Elevated card with consistent styling |
| **ShimmerEffects** | `common/ShimmerEffects.kt` | Shimmer loading placeholders (prescription card, appointment card shapes) |

### 13.2 Feature Components

| Component | File | Purpose |
|-----------|------|---------|
| **AppointmentCard** | `AppointmentCard.kt` | Displays appointment info with status badge, used in both patient and doctor views |
| **QuickActionCard** | `QuickActionCard.kt` | Home screen grid action buttons (icon + label + gradient accent) |
| **ExtractionResultSheet** | `ExtractionResultSheet.kt` | Modal bottom sheet for viewing/editing AI extraction results |

### 13.3 Design System

**Consistent across all 28 screens:**
- **Header:** Horizontal gradient `Color(0xFF1A237E)` → `Color(0xFF3F51B5)` → `Color(0xFF5C6BC0)` with white text
- **Background:** `Color(0xFFF4F6FB)` (light blue-grey)
- **Cards:** White with subtle elevation and rounded corners
- **Accent bars:** Left-side colored strips on cards for visual hierarchy
- **Typography:** Poppins for headings, Roboto for body text
- **Icons:** Material Icons Extended set

---

## 14. Navigation Architecture

### 14.1 NavGraph.kt

Single `NavHost` with 22 routes organized by feature:

```
NavGraph
├── splash                              → SplashScreen
├── login                               → LoginScreen
├── sign_up                             → SignUpScreen
│
├── patient_main                        → PatientMainScreen (nested bottom nav)
│   ├── patient_home                    → PatientHomeScreen
│   ├── patient_scan                    → ScanScreen
│   ├── patient_docs                    → DocsScreen
│   └── patient_profile                 → PatientProfileScreen
│
├── doctor_main                         → DoctorMainScreen (nested bottom nav)
│   ├── doctor_appointments             → DoctorAppointmentsScreen
│   ├── doctor_records                  → DoctorRecordsScreen
│   └── doctor_profile                  → DoctorProfileScreen
│
├── prescription_detail/{id}            → PrescriptionDetailScreen
├── patient_edit_profile                → EditProfileScreen
├── doctor_search                       → DoctorSearchScreen
├── doctor_detail/{id}                  → DoctorDetailScreen
├── book_appointment/{id}               → BookAppointmentScreen
├── patient_appointments                → PatientAppointmentsScreen
├── patient_records/{patientId}         → PatientRecordsScreen
├── doctor_orders                       → DoctorOrdersScreen
├── nearby_hospitals                    → NearbyHospitalsScreen
├── notifications                       → NotificationsScreen
└── (profile edit/change password screens for both roles)
```

### 14.2 Transition Animations

All navigation uses custom animated transitions:
- **Enter:** Slide in from right + fade in
- **Exit:** Slide out to left + fade out
- **Pop enter:** Slide in from left + fade in
- **Pop exit:** Slide out to right + fade out

---

## 15. Firebase Setup & Configuration

### 15.1 Required Firebase Services

| Service | Purpose | Free Tier |
|---------|---------|-----------|
| **Authentication** | User login/registration | Unlimited email/Google users |
| **Firestore** | Cloud NoSQL database | 1GB storage, 50K reads/day, 20K writes/day |
| **Storage** | Image file storage | 5GB storage, 1GB/day download |
| **Messaging** | Push notifications (FCM) | Unlimited |

### 15.2 Firebase Console Setup Steps

1. Create project at [console.firebase.google.com](https://console.firebase.google.com)
2. Add Android app with package name `com.mediscan.app`
3. Add **SHA-1** and **SHA-256** fingerprints (required for Google Sign-In):
   ```bash
   # Debug key
   keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore
   # Password: android
   ```
4. Download `google-services.json` → place in `app/` directory
5. Enable Authentication providers: Email/Password + Google
6. Create Firestore database in test mode
7. Enable Firebase Storage

### 15.3 Firestore Collections Schema

```
users/{userId}
    ├── email, fullName, phone, userType, profileImageUrl, createdAt
    ├── (patient): dateOfBirth, bloodGroup, address, emergencyContact
    └── (doctor): licenseNumber, specialization, hospital, consultationFee, availableDays, availableTimeRange

prescriptions/{prescriptionId}
    ├── patientId, doctorName, hospital, visitDate, diagnosis
    ├── medications: [{medicine, doseStrength, schedule, duration, confidence}]
    ├── imageUrl, tests, createdAt
    └── rawExtractionJson

appointments/{appointmentId}
    ├── patientId, patientName, doctorId, doctorName
    ├── specialization, dateTime, status, complaint
    └── createdAt

notifications/{notificationId}
    ├── userId, title, message, type
    ├── isRead, createdAt
    └── relatedId
```

### 15.4 Firebase Storage Structure

```
prescription_images/{userId}/{prescriptionId}.jpg
profile_images/{userId}.jpg
```

### 15.5 Security Rules (Development)

```javascript
// Firestore Rules
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}

// Storage Rules
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

> ⚠️ **Note:** These are development rules. Production should have more restrictive per-collection rules.

---

## 📊 Summary Statistics

| Category | Count |
|----------|-------|
| **Total Kotlin files** | 58 |
| **Screen files** | 28 |
| **ViewModels** | 8 |
| **UI Components** | 7 |
| **Data Models** | 7 |
| **Repositories** | 5 |
| **DI Modules** | 2 |
| **Navigation Routes** | 22 |
| **Third-party libraries** | 25+ |
| **Lines of code (largest file)** | 741 (ScanScreen.kt) |
| **Firebase services used** | 4 (Auth, Firestore, Storage, Messaging) |
| **AI model classes** | 9 (YOLO) |
| **Master medicine database** | 48,014 entries |

---

*Documentation Generated: April 2026*  
*Project: MediScan — AI-Powered Prescription Digitization*  
*AI Backend: Railway Cloud — `https://capstone-production-59e8.up.railway.app/`*  
*Repository: [https://github.com/sadibul/MediScan.git](https://github.com/sadibul/MediScan.git)*
