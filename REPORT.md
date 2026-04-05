# 📋 MediScan — Project Report

## AI-Powered Prescription Digitization & Smart Medicine Management System

> **Project Type:** Capstone Project  
> **Platform:** Android (Kotlin / Jetpack Compose)  
> **AI Backend:** FastAPI (Python) with YOLOv8s + PaddleOCR — Deployed to **Railway Cloud**  
> **Cloud Services:** Firebase (Auth, Firestore, Storage) + Railway (AI Backend)  
> **GitHub:** [https://github.com/sadibul/MediScan.git](https://github.com/sadibul/MediScan.git)

---

## 📖 Table of Contents

1. [Project Overview](#1-project-overview)
2. [Strengths](#2-strengths)
3. [Weaknesses](#3-weaknesses)
4. [Challenges Faced & Solutions](#4-challenges-faced--solutions)
5. [Limitations](#5-limitations)
6. [AI & GPU Constraints](#6-ai--gpu-constraints)
7. [Performance Analysis](#7-performance-analysis)
8. [Technology Decisions & Trade-offs](#8-technology-decisions--trade-offs)
9. [Future Improvements](#9-future-improvements)
10. [Conclusion](#10-conclusion)

---

## 1. Project Overview

MediScan is an AI-powered Android application that digitizes handwritten/printed prescription images into structured, editable medication data. The system serves two user roles — **Patients** and **Doctors** — within a single APK using role-based navigation.

### Core Workflow

```
Patient captures/selects prescription image
        ↓
Image sent to FastAPI backend (via Retrofit2)
        ↓
AI Pipeline: Quality Check (ResNet18) → Detection (YOLOv8s) → OCR (PaddleOCR)
        ↓
Structured JSON returned (medications, doctor info, diagnosis, tests)
        ↓
Patient reviews/edits in bottom sheet → Saves to Firebase
```

### Project Scale

| Metric | Count |
|--------|-------|
| **Kotlin Source Files** | 58 |
| **Screens** | 28 |
| **ViewModels** | 8 |
| **UI Components** | 7 |
| **Data Models** | 7 |
| **Repositories** | 5 |
| **DI Modules** | 2 |
| **Navigation Routes** | 22 |
| **Third-Party Libraries** | 25+ |

---

## 2. Strengths

### 2.1 High-Accuracy AI Pipeline

- **YOLOv8s** achieves **98.6% mAP50** on 9 detection classes (medicine, dose_strength, schedule, duration, doctor_name, hospital, date, diagnosis, test)
- **3-attempt OCR strategy** with PaddleOCR: raw → preprocessed → enhanced — maximizes text extraction
- **Spatial medication grouping** by Y-coordinate proximity correctly associates medicines with their doses, schedules, and durations
- **Medicine name matching** against a 48,014-entry master medicine list improves accuracy of extracted names

### 2.2 Modern Android Architecture

- **MVVM + Clean Architecture** with clear separation: UI (Compose) → ViewModel → Repository → Data Source
- **Hilt (Dagger)** for dependency injection — fully decoupled, testable components
- **Kotlin Coroutines + StateFlow** for reactive, non-blocking UI state management
- **Navigation Compose** with animated transitions (slide + fade) for smooth screen navigation
- **Material 3 Design** with a consistent design system: gradient headers, white cards, colored accent bars across all 28 screens

### 2.3 Dual Image Input (Camera + Gallery)

- **CameraX** integration for real-time prescription capture with flash toggle and camera flip
- **Gallery picker** (`ActivityResultContracts.GetContent`) for selecting existing images
- **Automatic EXIF rotation** correction ensures images are properly oriented regardless of device
- **JPEG re-encoding** (95% quality) on all gallery images guarantees backend compatibility (handles HEIC/WebP/PNG conversion)
- **Background processing** — all gallery image decoding/rotation runs on `Dispatchers.IO` to keep UI responsive

### 2.4 Comprehensive Firebase Integration

- **Firebase Auth** with both email/password AND Google Sign-In (one-tap)
- **Firebase Firestore** for real-time cloud database — prescriptions, appointments, user profiles, notifications
- **Firebase Storage** for prescription image storage with SDK-based direct download (bypassing URL security issues)
- **LRU Image Cache** (`PrescriptionImageCache`) prevents redundant downloads — 50-image capacity with 100MB size limit
- **Real-time Firestore listeners** for notifications (unread count badge updates instantly)

### 2.5 Rich Feature Set

| Feature | Implementation |
|---------|---------------|
| **Prescription Scanning** | CameraX + Gallery → FastAPI AI → Extraction results bottom sheet |
| **Prescription History** | Firestore-backed list with shimmer loading, pull-to-refresh |
| **Appointment Booking** | Doctor search → detail → date/time selection → Firestore save |
| **Doctor Analytics** | Vico charting library — bar charts for patient statistics |
| **Nearby Hospitals** | Google Maps Compose with real-time location + markers |
| **Notifications** | Firestore real-time listener with unread count badge |
| **Buy Medicine** | Product browsing UI for medicines |
| **Doctor Orders** | Prescriptions written by doctors for their patients |
| **Profile Management** | Edit profile, change password, view details for both roles |
| **Autocomplete Suggestions** | Diagnosis and test name suggestions from medical dictionary |
| **Extraction Editing** | FlowRow chip selector for dose schedules + editable fields |

### 2.6 User Experience Polish

- **Shimmer loading effects** (via `compose-shimmer`) on data-loading screens
- **Animated transitions** between screens (slide-in/fade for navigation)
- **Consistent gradient headers** (indigo → blue) across all screens
- **Role-based navigation** — patients and doctors see completely different dashboards
- **Error recovery** — EncryptedSharedPreferences auto-recovers from corruption with fallback to regular SharedPreferences
- **Smart emulator detection** — automatically switches API base URL between emulator (`10.0.2.2`) and physical device IP

---

## 3. Weaknesses

### 3.1 No Offline AI Capability

The AI extraction pipeline requires a network connection to the FastAPI backend server. If the server is unreachable, prescription scanning is completely unavailable. There is no on-device ML model for even basic text extraction.

### 3.2 Cloud Backend — CPU-Only Inference

The FastAPI server is deployed to **Railway Cloud** (`https://capstone-production-59e8.up.railway.app/`), eliminating the previous local-only limitation. However, Railway runs on CPU-only infrastructure (no GPU), which means:
- AI extraction takes longer (~8-12 seconds) compared to GPU inference (~2-4 seconds)
- The $5/month Hobby Plan provides generous resources (48 GB RAM) but no GPU acceleration
- `ApiEndpoints.kt` supports switching between cloud and local via the `USE_CLOUD` flag for development flexibility

### 3.3 English-Only OCR

PaddleOCR is configured for **English text only**. Prescriptions written in Bengali, Arabic, or other non-Latin scripts will not be processed correctly. This limits usability in multilingual regions.

### 3.4 No Room Database (Offline Cache) Implementation

While Room (SQLite) was planned in the original architecture, it was not implemented. The app relies entirely on Firestore's built-in offline cache, which has limitations:
- Cache is temporary and size-limited
- No explicit control over what data is cached locally
- Full offline-first experience is not supported

### 3.5 No Push Notifications (FCM)

Firebase Cloud Messaging was planned but not implemented. Notifications are implemented via Firestore real-time listeners, which only work while the app is in the foreground. Users don't receive push notifications when the app is closed.

### 3.6 No Automated Testing

The project does not include unit tests, integration tests, or UI tests. All testing was done manually during development. This makes refactoring risky and regression detection difficult.

---

## 4. Challenges Faced & Solutions

### 4.1 Gallery Image Format Incompatibility

**Problem:** When users selected images from the gallery, the backend returned HTTP 500 ("Could not decode image"). Gallery images can be in HEIC, WebP, or PNG format, but the backend's OpenCV `cv2.imdecode()` works best with JPEG.

**Solution:** Added a preprocessing step in `ScanViewModel.processGalleryImage()`:
1. Decode the raw bytes with `BitmapFactory.decodeStream()` (handles all Android-supported formats)
2. Apply EXIF rotation correction using `ExifInterface`
3. Re-encode as JPEG at 95% quality using `Bitmap.compress(JPEG, 95, stream)`
4. Send the JPEG bytes to the backend

### 4.2 EXIF Rotation Not Applied → Empty OCR Results

**Problem:** After fixing the format issue, gallery images sometimes returned empty extraction results. The AI could detect fields but PaddleOCR extracted no text.

**Root Cause:** Many phone cameras embed rotation in EXIF metadata instead of physically rotating pixels. The image looked correct in gallery apps (which read EXIF) but was sideways/upside-down to OpenCV.

**Solution:** Added `ExifInterface` dependency and a rotation correction function:
```kotlin
val exif = ExifInterface(inputStream)
val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
val rotationDegrees = when (orientation) {
    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
    else -> 0f
}
// Apply rotation via Matrix before encoding
```

### 4.3 Gallery Processing Blocking UI Thread

**Problem:** After adding format conversion + EXIF rotation, selecting a gallery image caused the UI to freeze for 1-3 seconds (ANR risk on slow devices).

**Solution:** Moved all image processing to `ScanViewModel` on `Dispatchers.IO`:
```kotlin
fun processGalleryImage(uri: Uri, context: Context) {
    viewModelScope.launch(Dispatchers.IO) {
        // Decode, rotate, encode — all on background thread
        val jpegBytes = processAndEncode(uri, context)
        processImage(jpegBytes) // Then send to API
    }
}
```

### 4.4 Scanned Prescription Image Not Displaying

**Problem:** After saving a prescription, the scanned image wouldn't display on the prescription detail screen. Firebase Storage download URLs were failing silently.

**Root Cause:** Firebase Storage security rules and signed URL expiration caused `Coil.AsyncImage` to fail loading images.

**Solution:** Replaced URL-based loading with Firebase SDK direct download:
```kotlin
val storageRef = Firebase.storage.reference.child(imageUrl)
val bytes = storageRef.getBytes(10 * 1024 * 1024).await() // 10MB max
val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
```
Added an LRU cache (`PrescriptionImageCache`) to avoid redundant downloads — 50 images, 100MB max.

### 4.5 EncryptedSharedPreferences Crash on Logout

**Problem:** Logging out caused a persistent crash. The app could not recover even after reinstalling.

**Root Cause:** `EncryptedSharedPreferences` master key corruption in the Android Keystore. Once corrupted, any attempt to access the encrypted file throws an unrecoverable exception.

**Solution:** Three-layer defense:
1. **Try-catch** around all EncryptedSharedPreferences access
2. **Auto-recovery** — if encryption fails, delete the corrupted file and recreate
3. **Fallback** to regular `SharedPreferences` if encryption is completely broken

### 4.6 Notification Observer Not Updating Badge

**Problem:** The unread notification count (red badge on bell icon) wasn't updating in real-time for both patient and doctor views.

**Root Cause:** The Firestore snapshot listener was initialized in the ViewModel constructor but the query filters (patientId/doctorId) weren't available yet, causing silent failures.

**Solution:** Made `startObserving(userId, userType)` a public method called explicitly from `LaunchedEffect` when the screen is composed, ensuring user context is available:
```kotlin
LaunchedEffect(currentUser) {
    currentUser?.uid?.let { uid ->
        notificationViewModel.startObserving(uid, userType)
    }
}
```

### 4.7 LazyColumn Inside ScrollableColumn Crash

**Problem:** Adding autocomplete suggestion dropdowns (LazyColumn) inside the ExtractionResultSheet (which uses verticalScroll) caused a crash: "Nesting scrollable in the same direction is not allowed."

**Solution:** Replaced the suggestion LazyColumn with a regular `Column` with `take(5)` to limit items. Since suggestions are always ≤5 items, LazyColumn's recycling benefit was unnecessary.

### 4.8 MPS (Metal) GPU Acceleration for macOS

**Problem:** The FastAPI backend was originally configured for NVIDIA CUDA GPUs. Running on a Mac (Apple Silicon) meant all AI inference ran on CPU, making extraction slow (~8-12 seconds per image).

**Solution:** Modified the backend startup to detect and use Apple's Metal Performance Shaders (MPS):
```python
if torch.backends.mps.is_available():
    device = torch.device("mps")
elif torch.cuda.is_available():
    device = torch.device("cuda")
else:
    device = torch.device("cpu")
```
This reduced inference time to ~3-5 seconds on MPS.

### 4.9 PaddleOCR Silent Failure on Railway (PIR Compiler Bug)

**Problem:** After deploying the FastAPI backend to Railway Cloud, YOLO detection worked perfectly (detecting 3+ fields), but PaddleOCR returned empty text (`"text": ""`) and `ocr_confidence: 0.0` for ALL detected fields. The app showed empty medication fields despite successful detection.

**Root Cause:** PaddlePaddle 3.3.x introduced a new **PIR (Paddle Intermediate Representation) compiler** that throws a `NotImplementedError` on Intel CPUs with oneDNN (MKL-DNN) enabled in Docker containers. Railway uses Intel Xeon CPUs. The error was silently caught by a try/except block in PaddleOCR's inference engine, causing it to return an empty list instead of OCR results.

**Solution:** Downgraded to **PaddlePaddle 2.6.2** (CPU build) + **PaddleOCR 2.9.1**, which use the older stable inference engine without PIR. Also added v2/v3 API auto-detection:
```python
# v3 API (PaddleOCR 3.x): result = engine.predict(image)
# v2 API (PaddleOCR 2.x): result = engine.ocr(image, cls=True)
# Auto-detect which API version is available
try:
    result = engine.predict(image)  # v3
except AttributeError:
    result = engine.ocr(image, cls=True)  # v2 fallback
```
**Impact:** Zero accuracy loss — same PP-OCRv4 models, same results, just a different inference engine version. The downgrade only affected the PaddlePaddle framework, not the OCR models themselves.

### 4.10 Railway Cloud Deployment

**Problem:** The university WiFi network was too restrictive for running the FastAPI server locally — Android devices couldn't connect to the local machine, making the app unusable outside the development environment.

**Solution:** Deployed the entire AI backend to **Railway Cloud** (Hobby Plan, $5/month):
1. Created a `Dockerfile` (Python 3.11-slim, CPU-only PyTorch and PaddlePaddle)
2. Created `requirements.txt` with CPU-specific packages (torch+cpu, paddlepaddle)
3. Created `railway.toml` for deployment configuration
4. Created `.dockerignore` to exclude unnecessary files
5. Connected GitHub repo to Railway for auto-deploy on push

**Result:** The AI backend is now accessible from anywhere over HTTPS at `https://capstone-production-59e8.up.railway.app/`. The Android app connects automatically when `USE_CLOUD = true` in `ApiEndpoints.kt`.

---

## 5. Limitations

### 5.1 Network Dependency

| Aspect | Limitation |
|--------|----------|
| **AI Extraction** | Requires internet connection to Railway Cloud server (works on any network) |
| **Data Sync** | Requires internet for Firestore read/write |
| **Image Upload** | Requires internet for Firebase Storage |
| **Notifications** | Only work while app is in foreground (no FCM push) |

### 5.2 AI Accuracy Constraints

| Aspect | Limitation |
|--------|-----------|
| **Handwriting** | Heavily stylized handwriting may not be recognized by PaddleOCR |
| **Languages** | English only — Bengali, Hindi, Arabic scripts not supported |
| **Image Quality** | Blurry, dark, or angled photos reduce extraction accuracy |
| **Prescription Formats** | Trained primarily on Bangladeshi prescription formats; may not generalize to other countries |
| **Medicine Names** | Master list contains 48,014 Bangladeshi medicines; foreign medicine names may not match |

### 5.3 Scalability Constraints

| Aspect | Limitation |
|--------|----------|
| **Backend** | Single Railway container, no load balancing, no horizontal scaling |
| **GPU** | Railway runs CPU-only — no GPU acceleration for AI inference |
| **Concurrent Users** | One FastAPI instance can handle ~5-10 concurrent extractions |
| **Storage** | Firebase free tier: 5GB storage, 1GB/day download |
| **Database** | Firestore free tier: 50K reads/day, 20K writes/day |
| **No CDN** | Images served directly from Firebase Storage (no CDN optimization) |

### 5.4 Security Limitations

- FastAPI endpoints are **unauthenticated** — anyone with the URL can send extraction requests
- Firebase Security Rules need tightening for production
- No rate limiting on the AI extraction endpoint
- `usesCleartextTraffic=true` still enabled for local development fallback (Railway uses HTTPS, so cloud traffic is encrypted)

---

## 6. AI & GPU Constraints

### 6.1 Model Architecture

| Model | Size | Purpose | Inference Device |
|-------|------|---------|-----------------|
| **YOLOv8s** | ~64 MB | 9-class field detection | GPU (MPS/CUDA) or CPU |
| **ResNet18** | ~128 MB | Image quality classification | GPU (MPS/CUDA) or CPU |
| **PaddleOCR** | ~30 MB | English text recognition | CPU only (PaddlePaddle) |

### 6.2 GPU Support Matrix

| Platform | GPU Type | Support | Performance |
|----------|----------|---------|-------------|
| **Windows/Linux + NVIDIA** | CUDA GPU | ✅ Full | Best (~2-4 sec/image) |
| **macOS Apple Silicon** | MPS (Metal) | ✅ Partial | Good (~3-5 sec/image) |
| **Railway Cloud (Intel Xeon)** | CPU only | ✅ Deployed | Moderate (~8-12 sec/image) |
| **macOS Intel** | CPU only | ⚠️ Slow | Slow (~8-12 sec/image) |
| **Any (no GPU)** | CPU | ⚠️ Slow | Slow (~8-12 sec/image) |

### 6.3 MPS (Metal Performance Shaders) Notes

- MPS is Apple's GPU acceleration framework for PyTorch on macOS
- **YOLOv8s and ResNet18** work on MPS — significant speedup over CPU
- **PaddleOCR** does NOT support MPS — always runs on CPU (PaddlePaddle framework limitation)
- Some PyTorch operations fall back to CPU silently on MPS (e.g., certain tensor operations not yet implemented)
- MPS requires macOS 12.3+ and PyTorch 1.13+

### 6.4 Memory Usage

- Peak GPU memory: ~2-3 GB during extraction (YOLOv8s + quality checker loaded simultaneously)
- Peak RAM: ~4-5 GB (all three models + image buffers + FastAPI overhead)
- Model loading time on startup: 10-20 seconds

---

## 7. Performance Analysis

### 7.1 AI Pipeline Timing Breakdown

| Stage | Time (GPU/MPS) | Time (CPU) |
|-------|----------------|------------|
| Image decode + preprocessing | ~50ms | ~50ms |
| Quality check (ResNet18) | ~50ms | ~200ms |
| YOLO detection (9 classes) | ~300ms | ~2000ms |
| PaddleOCR (per field) | ~100ms/field | ~100ms/field |
| Spatial grouping + matching | ~20ms | ~20ms |
| **Total (3 medications)** | **~2-4 sec** | **~8-12 sec** |

### 7.2 Network Timing

| Operation | Typical Time |
|-----------|-------------|
| Image upload (base64, ~500KB) | ~200-500ms (WiFi) |
| API response parsing | ~10ms |
| Firebase Firestore write | ~100-300ms |
| Firebase Storage upload (~500KB) | ~500ms-1s |
| **End-to-end (capture → saved)** | **~5-8 sec (GPU)** |

### 7.3 Android App Performance

| Metric | Value |
|--------|-------|
| APK size (debug) | ~25-30 MB |
| Cold start time | ~2-3 seconds |
| Screen transition | ~300ms (animated) |
| Camera preview FPS | 30 FPS (CameraX) |
| Memory usage (runtime) | ~150-200 MB |
| Gallery image processing | ~200-500ms (on IO thread) |

### 7.4 OkHttp Timeout Configuration

```
Connect timeout: 30 seconds
Read timeout:    90 seconds
Write timeout:   90 seconds
```

The generous read timeout (90s) accommodates slow CPU-only extraction.

---

## 8. Technology Decisions & Trade-offs

### 8.1 Why Jetpack Compose over XML?

| Factor | Compose | XML |
|--------|---------|-----|
| UI code | Declarative, less boilerplate | Imperative, verbose |
| State management | Built-in with `remember`, `StateFlow` | Manual with LiveData/ViewBinding |
| Preview | `@Preview` in IDE | Layout preview |
| Learning curve | Steeper (newer) | Well-documented |
| **Decision** | ✅ **Chosen** — modern, less code, better DX | — |

### 8.2 Why Firebase over Custom Backend?

| Factor | Firebase | Custom (PostgreSQL + JWT) |
|--------|----------|--------------------------|
| Setup time | Minutes (SDK integration) | Days (server + DB setup) |
| Auth | Built-in (email, Google, phone) | Must build from scratch |
| Real-time sync | Built-in listeners | WebSocket implementation needed |
| Offline cache | Automatic | Must implement manually |
| Cost | Free tier generous | Server hosting costs |
| **Decision** | ✅ **Chosen** — faster development, free hosting | — |

### 8.3 Why Hilt over Manual DI?

| Factor | Hilt | Manual DI |
|--------|------|-----------|
| Boilerplate | Annotations (`@Inject`, `@Module`) | Factory classes |
| Scoping | Automatic (ViewModel, Activity) | Manual lifecycle management |
| Testing | Easy to swap implementations | Custom test configuration |
| **Decision** | ✅ **Chosen** — industry standard, compile-time safety | — |

### 8.4 Why PaddleOCR over Tesseract?

| Factor | PaddleOCR | Tesseract |
|--------|-----------|-----------|
| Accuracy (English) | Higher (deep learning based) | Good but lower |
| Handwriting | Better recognition | Poor |
| Speed | Fast with GPU | Moderate |
| Setup | pip install | System library needed |
| **Decision** | ✅ **Chosen** — better accuracy on prescriptions | — |

---

## 9. Future Improvements

### 9.1 Short-Term (Next Release)

- [x] **Cloud deployment** — ✅ **DONE** — Deployed to Railway Cloud (Hobby Plan, $5/month) at `https://capstone-production-59e8.up.railway.app/`
- [ ] **GPU cloud instance** — Upgrade to a GPU-enabled cloud provider (AWS/GCP) for faster inference (~2-4 sec vs ~8-12 sec)
- [ ] **FCM push notifications** — Notify patients of appointment confirmations, medication reminders even when app is closed
- [ ] **Room database** — Implement full offline-first with Room + sync strategy
- [ ] **Unit tests** — Add ViewModel, Repository, and utility function tests
- [ ] **Firebase Auth token verification** on FastAPI — Protect AI endpoints from unauthorized access

### 9.2 Medium-Term (v2.0)

- [ ] **Multi-language OCR** — Add Bengali, Hindi, Arabic script support via PaddleOCR language packs
- [ ] **On-device ML** — Port a lightweight extraction model (TFLite/ONNX) for basic offline scanning
- [ ] **Medication reminders** — WorkManager-based local alarms for dose schedules
- [ ] **Doctor digital prescribing** — Let doctors write prescriptions digitally within the app
- [ ] **Appointment video calls** — Integrate WebRTC or Twilio for telemedicine

### 9.3 Long-Term (v3.0)

- [ ] **Drug interaction checker** — Cross-reference extracted medications against interaction databases
- [ ] **Insurance integration** — Connect with health insurance APIs for coverage verification
- [ ] **Multi-platform** — iOS version using KMM (Kotlin Multiplatform Mobile)
- [ ] **Medical records export** — Generate PDF/FHIR-compliant medical records
- [ ] **Voice-to-prescription** — Let doctors dictate prescriptions via speech-to-text

---

## 10. Conclusion

MediScan successfully demonstrates the feasibility of AI-powered prescription digitization on a mobile platform. The system achieves **98.6% mAP50** in field detection and provides a smooth end-to-end workflow from image capture to structured data storage.

**Key achievements:**
- Complete Android app with 28 screens serving both patients and doctors
- Real-time AI extraction with sub-5-second response times on GPU (8-12 sec on Railway CPU)
- **Cloud-deployed AI backend** on Railway — accessible from anywhere over HTTPS
- Comprehensive Firebase integration for auth, database, and storage
- Modern Compose UI with consistent design language
- Dual image input (camera + gallery) with robust format handling
- Successfully diagnosed and resolved a silent PaddleOCR PIR compiler bug in Docker

**Key areas for improvement:**
- GPU cloud instance for faster AI inference
- Multi-language OCR support for broader accessibility
- Offline capabilities with on-device ML
- Automated testing for long-term maintainability

The project demonstrates strong full-stack mobile development skills, AI/ML integration, cloud service management, and modern Android architecture patterns.

---

*Report Generated: April 2026*  
*Project: MediScan — AI-Powered Prescription Digitization*  
*Deployment: Railway Cloud — `https://capstone-production-59e8.up.railway.app/`*  
*Repository: [https://github.com/sadibul/MediScan.git](https://github.com/sadibul/MediScan.git)*
