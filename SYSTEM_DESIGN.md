# 🏗️ System Design: MediScan
## AI-Powered Prescription Digitization & Smart Medicine Management System

> **Last Updated:** April 2026 — Final architecture (Kotlin/Compose + Firebase + FastAPI) — **ALL COMPONENTS IMPLEMENTED ✅** — **AI Backend deployed to Railway Cloud ☁️**

---

## 📐 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                      MEDISCAN                                        │
│                         Smart Prescription Management System                         │
│                              (English Extraction Only)                               │
└─────────────────────────────────────────────────────────────────────────────────────┘

                                         │
                    ┌────────────────────┴────────────────────┐
                    │                                         │
                    ▼                                         ▼
            ┌──────────────┐                          ┌──────────────┐
            │   PATIENT    │                          │    DOCTOR    │
            │ Android App  │                          │ Android App  │
            │ Kotlin/Compose│                         │ Kotlin/Compose│
            └──────┬───────┘                          └──────┬───────┘
                    │                                         │
                    │         (Same APK, role-based UI)       │
                    └────────────────────┬────────────────────┘
                                         │
                          ┌──────────────┼──────────────┐
                          │              │              │
                          ▼              ▼              ▼
                   ┌───────────┐  ┌───────────┐  ┌───────────┐
                   │ Firebase  │  │ Firebase  │  │ Firebase  │
                   │   Auth    │  │ Firestore │  │  Storage  │
                   │(login/reg)│  │ (database)│  │ (images)  │
                   └───────────┘  └───────────┘  └───────────┘
                                         │
                                         │ (Prescription extraction only)
                                         ▼
                          ┌─────────────────────────────┐
                          │     ⚡ FASTAPI BACKEND       │
                          │   (AI Extraction Server)     │
                          │ ☁️ Railway Cloud (HTTPS)     │
                          ├─────────────────────────────┤
                          │  🔍 Quality Checker          │
                          │  🎯 YOLOv8s (9 classes)      │
                          │  📝 PaddleOCR (English)      │
                          │  🧩 Spatial Medication Grouping│
                          └─────────────────────────────┘
```

### Architecture Decisions

| Decision | Choice | Reason |
|----------|--------|--------|
| Single app or separate apps? | **Single APK** with role-based UI | Simpler to maintain, shared auth logic |
| Where to store user data? | **Firebase Firestore** (cloud) | Real-time sync, offline cache, free tier, no server setup |
| Where to store images? | **Firebase Storage** | Secured by Firebase Auth UID, 5GB free |
| Where to run AI? | **FastAPI server** (Railway Cloud or local) | GPU needed for YOLO + PaddleOCR, too heavy for mobile. Deployed to Railway for universal access. |
| Auth system? | **Firebase Auth** only | Native Android SDK, Google Sign-In built-in, no custom JWT needed |
| Image input? | **CameraX + Gallery picker** | Dual input for flexibility — camera for new photos, gallery for existing |
| Image caching? | **LRU memory cache** | PrescriptionImageCache (50 images, 100MB) avoids redundant Firebase downloads |

---

## 🎯 Component Details

### 1️⃣ CLIENT LAYER — Android App (Kotlin + Jetpack Compose)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        📱 ANDROID APPLICATION                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  One APK — Two Roles:                                                   │
│                                                                         │
│  📱 PATIENT ROLE                    👨‍⚕️ DOCTOR ROLE                      │
│  ─────────────                     ─────────────                        │
│  • Camera capture (CameraX)    ✅  • View patients list             ✅ │
│  • Gallery image picker        ✅  • Search patients                ✅ │
│  • Upload prescription image   ✅  • View patient Rx history        ✅ │
│  • View extracted medications  ✅  • View all medications           ✅ │
│  • Prescription history        ✅  • Digital prescription writing   ✅ │
│  • Book appointments           ✅  • Patient diagnosis history      ✅ │
│  • Buy medicines (browse)      ✅  • Analytics dashboard (Vico)     ✅ │
│  • Nearby hospitals (Maps)     ✅  • Appointment management         ✅ │
│  • Profile management          ✅  • Profile management             ✅ │
│  • Doctor orders (view)        ✅  • Notifications                  ✅ │
│  • Notifications               ✅                                      │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

#### Tech Stack (Android) — All Implemented ✅:

| Category | Library | Version | Purpose | Status |
|----------|---------|---------|---------|--------|
| **Language** | Kotlin | 2.0.21 | Primary language | ✅ |
| **UI Framework** | Jetpack Compose | BOM 2024.12.01 | Declarative UI (Material 3) | ✅ |
| **Architecture** | MVVM + Clean Architecture | — | Separation of concerns | ✅ |
| **DI** | Hilt (Dagger) | 2.53.1 | Dependency injection | ✅ |
| **HTTP Client** | Retrofit2 + OkHttp | 2.11.0 / 4.12.0 | API calls to FastAPI | ✅ |
| **Auth** | Firebase Auth | BOM 33.7.0 | Email/password + Google Sign-In | ✅ |
| **Cloud DB** | Firebase Firestore | BOM 33.7.0 | User data, prescriptions, appointments | ✅ |
| **Cloud Storage** | Firebase Storage | BOM 33.7.0 | Prescription images | ✅ |
| **Camera** | CameraX | 1.4.1 | Prescription photo capture | ✅ |
| **Gallery** | ActivityResultContracts | AndroidX | Image picker with EXIF rotation | ✅ |
| **Image Loading** | Coil + Firebase SDK | 2.7.0 | Avatars (Coil) + Rx images (SDK direct) | ✅ |
| **Image Cache** | Custom LRU Cache | — | 50-image, 100MB prescription cache | ✅ |
| **Navigation** | Navigation Compose | 2.8.5 | Screen routing with animations | ✅ |
| **Charts** | Vico | 2.0.0-beta.2 | Doctor analytics (bar charts) | ✅ |
| **Maps** | Google Maps Compose | 6.2.1 | Hospital/pharmacy finder | ✅ |
| **Notifications** | Firestore Listeners | — | Real-time in-app notifications | ✅ |
| **Token Storage** | EncryptedSharedPreferences | 1.1.0-alpha06 | Secure local storage (with fallback) | ✅ |
| **Serialization** | Gson | 2.11.0 | JSON parsing | ✅ |
| **Permissions** | Accompanist Permissions | 0.36.0 | Camera + location permissions | ✅ |
| **Loading UI** | Shimmer | 1.3.2 | Loading placeholder animations | ✅ |
| **EXIF** | ExifInterface | 1.3.7 | Gallery image rotation correction | ✅ |

#### Android Project Structure (Actual — 58 Kotlin files):

```
app/src/main/java/com/mediscan/app/
├── MediScanApp.kt                      # Application class (@HiltAndroidApp)
├── MainActivity.kt                      # Single Activity (Compose)
│
├── core/
│   ├── constants/
│   │   └── ApiEndpoints.kt             # Dynamic base URL (cloud/emulator/physical, USE_CLOUD flag)
│   ├── theme/
│   │   ├── Theme.kt                    # Material 3 Theme (MediScanTheme)
│   │   ├── Color.kt                    # Color palette (gradient indigo/blue)
│   │   └── Type.kt                     # Typography (Poppins + Roboto)
│   ├── navigation/
│   │   ├── NavGraph.kt                 # 22 routes with animated transitions
│   │   └── Routes.kt                   # Route constants + helper functions
│   └── utils/
│       ├── DateUtils.kt                # Date formatting utilities
│       ├── MedicalSuggestions.kt        # Autocomplete data (diagnoses, tests, schedules)
│       ├── NetworkResult.kt            # Sealed class (Idle/Loading/Success/Error)
│       └── PreferencesManager.kt       # EncryptedSharedPreferences + fallback
│
├── data/
│   ├── model/
│   │   ├── User.kt                     # User (patient + doctor fields)
│   │   ├── Prescription.kt             # Prescription with medications list
│   │   ├── Medication.kt               # Single medication data class
│   │   ├── Appointment.kt              # Appointment data class
│   │   ├── ExtractionResult.kt         # AI extraction response model
│   │   ├── DoctorOrder.kt              # Doctor-written prescription
│   │   └── Notification.kt             # Notification data class
│   ├── remote/
│   │   └── FastApiService.kt           # Retrofit interface (health, quality, extract)
│   └── repository/
│       ├── AuthRepository.kt           # Firebase Auth operations
│       ├── PrescriptionRepository.kt   # Firestore + FastAPI + Storage
│       ├── AppointmentRepository.kt    # Firestore appointments
│       ├── UserRepository.kt           # Firestore user profiles
│       └── NotificationRepository.kt   # Firestore notifications + listener
│
├── di/
│   ├── AppModule.kt                    # Hilt: Firebase singletons
│   └── NetworkModule.kt                # Hilt: Retrofit + OkHttp (30s/90s/90s timeouts)
│
└── ui/
    ├── viewmodel/
    │   ├── AuthViewModel.kt            # Login/signup/Google sign-in state
    │   ├── PatientViewModel.kt         # Patient dashboard state
    │   ├── ScanViewModel.kt            # Camera + gallery + AI extraction
    │   ├── DocsViewModel.kt            # Prescription list loading
    │   ├── DoctorViewModel.kt          # Doctor dashboard state
    │   ├── BookingViewModel.kt         # Doctor search + appointment booking
    │   └── NotificationViewModel.kt    # Real-time notification listener
    ├── screens/
    │   ├── splash/
    │   │   └── SplashScreen.kt
    │   ├── auth/
    │   │   ├── LoginScreen.kt          # Email/password + Google Sign-In
    │   │   └── SignUpScreen.kt         # Registration with role selection
    │   ├── patient/
    │   │   ├── PatientMainScreen.kt    # Bottom nav (Home/Scan/Docs/Profile)
    │   │   ├── home/PatientHomeScreen.kt
    │   │   ├── scan/ScanScreen.kt      # CameraX + Gallery (741 lines)
    │   │   ├── scan/CameraPreviewScreen.kt
    │   │   ├── docs/DocsScreen.kt      # Prescription history
    │   │   ├── docs/PrescriptionDetailScreen.kt
    │   │   ├── docs/PrescriptionDetailViewModel.kt
    │   │   ├── medicine/BuyMedicineScreen.kt
    │   │   ├── orders/DoctorOrdersScreen.kt
    │   │   └── profile/ (PatientProfile, EditProfile, ChangePassword)
    │   ├── doctor/
    │   │   ├── DoctorMainScreen.kt     # Bottom nav (Appointments/Records/Profile)
    │   │   ├── appointments/DoctorAppointmentsScreen.kt
    │   │   ├── appointments/PatientDetailSheet.kt
    │   │   ├── appointments/PatientRecordsScreen.kt
    │   │   ├── records/DoctorRecordsScreen.kt  # Vico charts
    │   │   └── profile/ (DoctorProfile, DoctorEditProfile, DoctorChangePassword)
    │   ├── booking/
    │   │   ├── DoctorSearchScreen.kt
    │   │   ├── DoctorDetailScreen.kt
    │   │   └── PatientAppointmentsScreen.kt
    │   ├── hospitals/
    │   │   └── NearbyHospitalsScreen.kt  # Google Maps Compose
    │   └── notifications/
    │       └── NotificationsScreen.kt
    └── components/
        ├── AppointmentCard.kt
        ├── ExtractionResultSheet.kt    # AI results bottom sheet
        ├── QuickActionCard.kt
        └── common/
            ├── MediButton.kt
            ├── MediCard.kt
            ├── MediTextField.kt
            └── ShimmerEffects.kt
```

---

### 2️⃣ AUTHENTICATION — Firebase Auth (Only)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        🔐 AUTHENTICATION FLOW                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  📱 Android App                 🔥 Firebase Auth                        │
│       │                              │                                  │
│       │  1. User enters email/       │                                  │
│       │     password OR taps         │                                  │
│       │     "Sign in with Google"    │                                  │
│       │                              │                                  │
│       │  2. Firebase SDK call:       │                                  │
│       │     auth.signInWith...()     │                                  │
│       │ ────────────────────────────▶│                                  │
│       │                              │                                  │
│       │  3. Firebase returns:        │                                  │
│       │     FirebaseUser object      │                                  │
│       │     + ID Token (JWT)         │                                  │
│       │ ◀────────────────────────────│                                  │
│       │                              │                                  │
│       │  4. Store user profile       │                                  │
│       │     in Firestore             │                                  │
│       │     (name, role, phone...)   │                                  │
│       │ ────────────────────────────▶│  Firestore                       │
│       │                              │                                  │
│       │  5. Navigate to Home         │                                  │
│       │     (Patient or Doctor       │                                  │
│       │      based on role field)    │                                  │
│       │                              │                                  │
│       │  ─── For AI API calls ───    │                                  │
│       │                              │                                  │
│       │  6. Get Firebase ID Token:   │                                  │
│       │     user.getIdToken()        │                                  │
│       │                              │                                  │
│       │  7. Send to FastAPI:         │                                  │
│       │     Authorization: Bearer    │                                  │
│       │     <firebase_id_token>      │                                  │
│       │ ──────────────────────────▶  FastAPI verifies with              │
│       │                              firebase-admin SDK                 │
│       │                              │                                  │
└─────────────────────────────────────────────────────────────────────────┘

  ✅ What Firebase Auth gives you for FREE:
  ─────────────────────────────────────────
  • Email/Password registration & login
  • Google Sign-In (one-tap)
  • Password reset emails
  • Email verification
  • Automatic token management (refresh tokens)
  • 10,000 phone verifications/month FREE
  • Unlimited email/password and Google Sign-In users

  ❌ What you DON'T need (removed from old design):
  ──────────────────────────────────────────────────
  • PyJWT / python-jose (Firebase handles tokens)
  • passlib / bcrypt (Firebase handles password hashing)
  • slowapi / Redis rate limiting (not needed for capstone)
  • Custom JWT generation on FastAPI
  • Custom /auth/login, /auth/register endpoints on FastAPI
```

---

### 3️⃣ BACKEND — FastAPI (AI Extraction Server Only)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        ⚡ FASTAPI BACKEND                               │
│                   (AI Prescription Extraction Only)                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Purpose: Run AI models (YOLO + PaddleOCR) that are too heavy          │
│  for mobile devices. This server does ONE thing: extract data          │
│  from prescription images.                                              │
│                                                                         │
│  📂 Current Structure (already built & working):                        │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  prescription_ai/                                                │   │
│  │  ├── backend/                                                   │   │
│  │  │   └── fastapi_app.py          # API server V6.1 (READY)     │   │
│  │  │                                                               │   │
│  │  ├── src/                                                       │   │
│  │  │   ├── pipeline/                                              │   │
│  │  │   │   ├── extractor.py         # YOLOv8 + PaddleOCR         │   │
│  │  │   │   └── structured_extractor.py  # Medication grouping    │   │
│  │  │   ├── ocr/                                                   │   │
│  │  │   │   └── paddle_ocr_engine.py # PaddleOCR wrapper          │   │
│  │  │   └── preprocessing/                                         │   │
│  │  │       └── quality_checker.py   # ResNet18 + Laplacian       │   │
│  │  │                                                               │   │
│  │  ├── experiments/v6_9class_english/                              │   │
│  │  │   └── weights/best.pt         # YOLO model (64MB)           │   │
│  │  │                                                               │   │
│  │  └── medicine library/                                          │   │
│  │      └── master_medicine_list.csv # 48,014 medicines           │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  🔌 API Endpoints (already implemented in fastapi_app.py):             │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                                                                  │   │
│  │  GET  /                       # Server info & version           │   │
│  │  GET  /health                 # Health check                    │   │
│  │  POST /check-quality          # Quality check (file upload)     │   │
│  │  POST /check-quality-base64   # Quality check (base64 string)  │   │
│  │  POST /extract                # Full extraction (file upload)   │   │
│  │  POST /extract-base64         # Full extraction (base64 string)│   │
│  │  GET  /results/{task_id}      # Retrieve saved results          │   │
│  │  DELETE /task/{task_id}       # Delete task data                │   │
│  │                                                                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  Technologies:                                                          │
│  ┌────────────────────────────────────────────────────────────────┐    │
│  │  • FastAPI 0.109.x          - Web framework                    │    │
│  │  • Uvicorn                  - ASGI server                      │    │
│  │  • python-multipart         - File uploads                     │    │
│  │  • firebase-admin           - Token verification (to add)      │    │
│  │  • CORS enabled             - Android app can connect          │    │
│  └────────────────────────────────────────────────────────────────┘    │
│                                                                         │
│  ⚠️  Note: User data (prescriptions, appointments, profiles) is        │
│  NOT stored in FastAPI. It's all in Firebase Firestore directly         │
│  from the Android app. FastAPI only does AI extraction.                 │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

### 4️⃣ AI/ML ENGINE (Already Trained & Working)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        🤖 AI/ML PROCESSING ENGINE                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                      EXTRACTION PIPELINE V6                      │   │
│  │                                                                  │   │
│  │  ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────────┐ │   │
│  │  │  IMAGE   │   │ QUALITY  │   │   YOLO   │   │  PADDLEOCR   │ │   │
│  │  │  INPUT   │──▶│  CHECK   │──▶│ DETECTION│──▶│  (English)   │ │   │
│  │  │          │   │          │   │          │   │              │ │   │
│  │  │ • Photo  │   │• ResNet18│   │ • 9      │   │• Field-spec  │ │   │
│  │  │ • Base64 │   │• Laplacian│  │  classes │   │  preprocess  │ │   │
│  │  │          │   │• 80% acc │   │ • 98.6%  │   │• 3-attempt   │ │   │
│  │  │          │   │          │   │  mAP50   │   │  strategy    │ │   │
│  │  └──────────┘   └──────────┘   └──────────┘   └──────┬───────┘ │   │
│  │                                                       │         │   │
│  │                  ┌────────────────────────────────────┘         │   │
│  │                  ▼                                              │   │
│  │  ┌──────────────────────────────────────┐                       │   │
│  │  │          SPATIAL GROUPING             │                       │   │
│  │  │  Group medications with their doses  │                       │   │
│  │  │  by Y-coordinate proximity           │                       │   │
│  │  └─────────────────────┬────────────────┘                       │   │
│  │                                    ▼                            │   │
│  │                    ┌──────────────────────────┐                 │   │
│  │                    │   STRUCTURED JSON OUTPUT  │                │   │
│  │                    └──────────────────────────┘                 │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  9 YOLO Classes:                                                        │
│  ┌────────────────────────────────────────────────────────────────┐    │
│  │  MEDICINE  │  DOSE_STRENGTH  │  DOSAGE_SCHEDULE  │  DURATION  │    │
│  │  DOCTOR_NAME  │  HOSPITAL  │  DATE  │  TEST  │  DIAGNOSIS    │    │
│  └────────────────────────────────────────────────────────────────┘    │
│                                                                         │
│  Model Files:                                                           │
│  ┌────────────────────────────────────────────────────────────────┐    │
│  │  experiments/v6_9class_english/weights/best.pt   (64 MB)       │    │
│  │  models/image_quality_classifier.pt              (128 MB)      │    │
│  │  medicine library/master_medicine_list.csv        (48K meds)   │    │
│  └────────────────────────────────────────────────────────────────┘    │
│                                                                         │
│  AI Stack:                                                              │
│  ┌────────────────────────────────────────────────────────────────┐    │
│  │  • YOLOv8s (Ultralytics)    - Field detection (9 classes)      │    │
│  │  • PaddleOCR 2.9.1          - Text recognition (English)       │    │
│  │  • PaddlePaddle 2.6.2       - PaddleOCR backend (CPU build)    │    │
│  │  • ResNet18                 - Image quality classification      │    │
│  │  • OpenCV 4.x               - Image preprocessing              │    │
│  │  • PyTorch 2.5.1            - Deep learning framework           │    │
│  │  • GPU: MPS (Apple Silicon) / CUDA (NVIDIA) / CPU (Railway)    │    │
│  └────────────────────────────────────────────────────────────────┘    │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

### 5️⃣ DATABASE — Firebase Firestore (Cloud) + Room (Local)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        🗄️ DATABASE: FIREBASE FIRESTORE                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Why Firestore (not PostgreSQL):                                        │
│  • No server to install/maintain                                       │
│  • Native Android SDK with offline caching built-in                    │
│  • Real-time listeners (data syncs automatically)                      │
│  • Free tier: 1GB storage, 50K reads/day, 20K writes/day              │
│  • Document-based = perfect for prescriptions & user profiles          │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  FIRESTORE COLLECTIONS                                           │   │
│  │                                                                  │   │
│  │  users/{userId}                                                  │   │
│  │  ├── email: String                                              │   │
│  │  ├── fullName: String                                           │   │
│  │  ├── phone: String                                              │   │
│  │  ├── userType: "patient" | "doctor"                             │   │
│  │  ├── profileImageUrl: String?                                   │   │
│  │  ├── createdAt: Timestamp                                       │   │
│  │  │  (Patient-specific)                                           │   │
│  │  ├── dateOfBirth: String?                                       │   │
│  │  ├── bloodGroup: String?                                        │   │
│  │  ├── address: String?                                           │   │
│  │  ├── emergencyContact: String?                                  │   │
│  │  │  (Doctor-specific)                                            │   │
│  │  ├── licenseNumber: String?                                     │   │
│  │  ├── specialization: String?                                    │   │
│  │  ├── hospital: String?                                          │   │
│  │  ├── consultationFee: String?                                   │   │
│  │  ├── availableDays: List<String>?                               │   │
│  │  └── availableTimeRange: String?                                │   │
│  │                                                                  │   │
│  │  prescriptions/{prescriptionId}                                  │   │
│  │  ├── patientId: String (Firebase UID)                           │   │
│  │  ├── doctorName: String?                                        │   │
│  │  ├── hospital: String?                                          │   │
│  │  ├── visitDate: Timestamp                                       │   │
│  │  ├── diagnosis: String?                                         │   │
│  │  ├── medications: List<Map>                                     │   │
│  │  │   └── {medicine, doseStrength, schedule, duration, conf}     │   │
│  │  ├── imageUrl: String (Firebase Storage path)                   │   │
│  │  ├── rawExtractionJson: String?                                 │   │
│  │  └── createdAt: Timestamp                                       │   │
│  │                                                                  │   │
│  │  appointments/{appointmentId}                                    │   │
│  │  ├── patientId, patientName, doctorId, doctorName               │   │
│  │  ├── specialization, dateTime, status, complaint                │   │
│  │  └── createdAt: Timestamp                                       │   │
│  │                                                                  │   │
│  │  notifications/{notificationId}                                  │   │
│  │  ├── userId, title, message, type                               │   │
│  │  ├── isRead: Boolean                                            │   │
│  │  ├── createdAt: Timestamp                                       │   │
│  │  └── relatedId: String? (appointmentId/prescriptionId)          │   │
│  │                                                                  │   │
│  │  reminders/{reminderId}                                          │   │
│  │  ├── patientId, prescriptionId, medicineName                    │   │
│  │  ├── schedule, reminderTimes, isActive                          │   │
│  │  └── createdAt: Timestamp                                       │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  FIREBASE STORAGE (Images)                                       │   │
│  │  prescription_images/{userId}/{prescriptionId}.jpg               │   │
│  │  profile_images/{userId}.jpg                                     │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  LOCAL CACHE: Room (SQLite) — optional, for offline             │   │
│  │  Firestore SDK already has built-in offline cache.              │   │
│  │  Room is extra for heavier offline needs.                       │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  Entity Relationships:                                                  │
│                                                                         │
│  USERS ──1:N──▶ PRESCRIPTIONS ──1:N──▶ MEDICATIONS                     │
│    │                                         │                          │
│    └──1:N──▶ APPOINTMENTS                    └──1:N──▶ REMINDERS       │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Data Flow Diagrams

### Prescription Upload & Extraction Flow:

```
  ANDROID APP                    FASTAPI BACKEND           FIREBASE
  (Kotlin/Compose)               (Railway Cloud / Local)
      │                             │                         │
      │  1. User captures photo     │                         │
      │     using CameraX           │                         │
      │     OR picks from Gallery   │                         │
      │                             │                         │
      │  2. Gallery path:           │                         │
      │     - Decode (BitmapFactory)│                         │
      │     - EXIF rotation fix     │                         │
      │     - Re-encode as JPEG     │                         │
      │     (all on IO thread)      │                         │
      │                             │                         │
      │  3. Convert to base64       │                         │
      │                             │                         │
      │  4. POST /extract-base64    │                         │
      │     via Retrofit2           │                         │
      │     {"image": "<base64>"}   │                         │
      │ ──────────────────────────▶ │                         │
      │                             │                         │
      │                             │  5. AI Pipeline:        │
      │                             │     Quality → YOLO →    │
      │                             │     OCR → Matching →    │
      │                             │     Spatial Grouping    │
      │                             │                         │
      │  6. JSON response           │                         │
      │     {medications: [...]}    │                         │
      │ ◀────────────────────────── │                         │
      │                             │                         │
      │  7. Show in bottom sheet    │                         │
      │     (ExtractionResultSheet) │                         │
      │     (editable by user)      │                         │
      │                             │                         │
      │  8. User taps "Save" →      │                         │
      │     Upload image to Storage ──────────────────────▶   │
      │     Save Rx to Firestore   ───────────────────────▶   │
      │                             │                         │
```

### Authentication Flow:

```
  ANDROID APP                    FIREBASE AUTH
      │                             │
      │  1. Email/password OR       │
      │     Google Sign-In          │
      │     (Firebase SDK)          │
      │ ──────────────────────────▶ │
      │                             │
      │  2. FirebaseUser returned   │
      │     (uid, email, token)     │
      │ ◀────────────────────────── │
      │                             │
      │  3. Check Firestore:        │
      │     users/{uid} exists?     │
      │                             │
      │  YES → Read userType →      │
      │     Navigate to Home        │
      │                             │
      │  NO → Show role picker →    │
      │     Save to Firestore →     │
      │     Navigate to Home        │
      │                             │
```

---

## 🚀 Deployment Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    PRODUCTION SETUP (Railway Cloud) ✅                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Railway Cloud (AI Backend — Hobby Plan $5/month)                       │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  • FastAPI + Uvicorn (Docker container, Python 3.11-slim)       │   │
│  │  • YOLOv8s + ResNet18 (CPU inference)                           │   │
│  │  • PaddleOCR 2.9.1 + PaddlePaddle 2.6.2 (CPU)                  │   │
│  │  • URL: https://capstone-production-59e8.up.railway.app/        │   │
│  │  • Resources: Up to 48 vCPU, 48 GB RAM, 5 GB storage           │   │
│  │  • Auto-deploy on git push to main branch                       │   │
│  │  • HTTPS with automatic SSL/TLS certificates                    │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↕ HTTPS (any network / anywhere)          │
│  Android Device / Emulator                                              │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  • MediScan Kotlin app                                          │   │
│  │  • ApiEndpoints.kt: USE_CLOUD = true (default)                  │   │
│  │  • CLOUD_URL: https://capstone-production-59e8.up.railway.app/  │   │
│  │  • OkHttp timeouts: 30s connect, 90s read, 90s write           │   │
│  │  • Works on ANY network (no same-WiFi requirement)              │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↕ HTTPS                                   │
│  Firebase (Cloud — Free Tier)                                           │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  • Auth: unlimited email/Google users                    ✅     │   │
│  │  • Firestore: 1GB storage, 50K reads/day                ✅     │   │
│  │  • Storage: 5GB, 1GB/day download                        ✅     │   │
│  │  • Messaging (FCM): available                            🔜     │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
├─────────────────────────────────────────────────────────────────────────┤
│                    LOCAL DEVELOPMENT SETUP (Optional)                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Developer Machine (Apple Silicon Mac with MPS, or NVIDIA with CUDA)    │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  • FastAPI + Uvicorn                    Port: 8000              │   │
│  │  • YOLOv8s + ResNet18 (MPS/CUDA GPU)   Faster inference         │   │
│  │  • PaddleOCR (CPU)                     PaddlePaddle limitation  │   │
│  │  • Set USE_CLOUD = false in ApiEndpoints.kt                     │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↕ HTTP (WiFi / same network)              │
│  Android Device / Emulator                                              │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  • Emulator: http://10.0.2.2:8000 (auto-detected)              │   │
│  │  • Physical: http://<local_ip>:8000 (configure in ApiEndpoints) │   │
│  │  • Requires same WiFi network as dev machine                    │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 📋 API Response Format

### POST /extract-base64 — Main extraction endpoint

**Request:** `{"image": "<base64_jpg_string>"}`

**Response (accepted):**
```json
{
  "prescription_id": "20260224_153000",
  "extraction_timestamp": "2026-02-24T15:30:00",
  "model_version": "v6_9class_english",
  "ocr_engine": "paddleocr",
  "status": "completed",
  "task_id": "a1b2c3d4",
  "medications": [
    {
      "medicine": "Napa Extra",
      "dose_strength": "500mg",
      "schedule": "3 times daily",
      "duration": "5 days",
      "confidence": { "medicine": 0.95, "dose_strength": 0.88, "schedule": 0.82, "duration": 0.79 }
    }
  ],
  "medication_count": 1,
  "doctor": { "name": "Dr. Ahmed", "hospital": "Dhaka Medical College" },
  "prescription_info": { "date": "14/01/2026", "diagnoses": ["Fever"], "tests": ["CBC"] },
  "quality_check": { "is_acceptable": true, "quality_score": 0.92, "issues": [] },
  "stats": { "total_fields_detected": 8, "medicines_found": 1 }
}
```

**Response (rejected):**
```json
{
  "status": "rejected",
  "message": "The image quality is poor. Please retake the photo.",
  "medications": [],
  "medication_count": 0,
  "quality_check": { "is_acceptable": false, "quality_score": 0.15 }
}
```

---

## 🔧 Complete Technology Stack

| Layer | Technology | Status |
|-------|------------|--------|
| **Android App** | Kotlin 2.0.21 + Jetpack Compose (Material 3) | ✅ **Done** (58 files, 28 screens) |
| **Auth** | Firebase Auth (email + Google Sign-In) | ✅ **Done** |
| **Cloud DB** | Firebase Firestore | ✅ **Done** (5 collections) |
| **Image Storage** | Firebase Storage + LRU Cache | ✅ **Done** |
| **HTTP Client** | Retrofit 2.11.0 + OkHttp 4.12.0 | ✅ **Done** |
| **DI** | Hilt 2.53.1 (Dagger) | ✅ **Done** (2 modules) |
| **Camera** | CameraX 1.4.1 + Gallery Picker | ✅ **Done** (dual input) |
| **Charts** | Vico 2.0.0-beta.2 | ✅ **Done** (doctor analytics) |
| **Maps** | Google Maps Compose 6.2.1 | ✅ **Done** (nearby hospitals) |
| **Notifications** | Firestore Real-time Listeners | ✅ **Done** (in-app) |
| **Permissions** | Accompanist Permissions 0.36.0 | ✅ **Done** (camera, location) |
| **Loading UI** | Shimmer 1.3.2 | ✅ **Done** |
| **EXIF Handling** | ExifInterface 1.3.7 | ✅ **Done** (gallery rotation) |
| **Secure Storage** | EncryptedSharedPreferences + fallback | ✅ **Done** |
| **AI Backend** | FastAPI v6.1 (Railway Cloud / CPU) | ✅ **Done** — Deployed to Railway |
| **YOLO v6** | YOLOv8s (9 classes, 98.6% mAP50) | ✅ **Done** |
| **OCR** | PaddleOCR 2.9.1 + PaddlePaddle 2.6.2 (English) | ✅ **Done** |
| **Quality Checker** | ResNet18 + Laplacian (80%) | ✅ **Done** |
| **Local DB** | Room (SQLite) | ⏸️ Not implemented (Firestore offline cache used instead) |
| **Push Notifications** | FCM (Firebase Cloud Messaging) | ⏸️ Available, not implemented |
| **Medication Reminders** | WorkManager | ⏸️ Available, not implemented |

---

*Document Created: January 14, 2026*
*Last Updated: April 2026 — Railway cloud deployment, PaddleOCR 2.9.1, USE_CLOUD flag, HTTPS connection*
*Project: MediScan - AI-Powered Prescription Digitization*
*Repository: [https://github.com/sadibul/MediScan.git](https://github.com/sadibul/MediScan.git)*
