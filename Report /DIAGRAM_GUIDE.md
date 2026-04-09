# 📐 Diagram Guide for MediScan Academic Report

> This document lists ALL diagrams and screenshots required for the academic report.  
> Each entry specifies: **what to create**, **where to place it** in the report, and **how to create it**.

---

## 📋 Summary Table

| # | Diagram / Screenshot | Report Location | Type | Source |
|---|---------------------|-----------------|------|--------|
| 1 | Business Model Canvas | Chapter 1, Figure 1.2 | Create manually | Draw in draw.io or Canva |
| 2 | Use Case Diagram | Chapter 5, Figure 5.1 | Create manually | Draw in draw.io / StarUML |
| 3 | Activity Diagram (Swimlane) | Chapter 5, Figure 5.2 | Create manually | Draw in draw.io |
| 4 | Class Diagram | Chapter 5, Figure 5.3 | Create manually | Draw in draw.io / StarUML |
| 5 | Component Diagram | Chapter 5, Figure 5.4 | Create manually | Draw in draw.io |
| 6 | Sequence Diagram | Chapter 5, Figure 5.5 | Create manually | Draw in draw.io / StarUML |
| 7 | Data Flow Diagram | Chapter 5, Figure 5.6 | Create manually | Draw in draw.io |
| 8 | Deployment Diagram | Chapter 5, Figure 5.7 | Create manually | Draw in draw.io |
| 9 | User Interface Screenshots | Chapter 5, Figure 5.8 | Take from phone | Screenshots from running app |
| 10 | Per-Class mAP@50 Bar Chart | Chapter 7, Figure 7.1 | Create manually | Use data from report |
| 11 | v1 vs v4 Comparison Chart | Chapter 7, Figure 7.2 | Create manually | Use data from report |
| 12 | Illegible Prescription Example | Chapter 1, Figure 1.1 | Photo | Use from dataset |
| 13 | Sample Dataset Images | Chapter 6, Figure 6.1 | From dataset | Select 3-4 diverse prescriptions |
| 14 | Labels Distribution | Chapter 6, Figure 6.2 | From YOLO output | `Report /yolo_prescription_v4/labels.jpg` |
| 15 | Training Results | Chapter 7, Figure 6.3 | From YOLO output | `Report /yolo_prescription_v4/results.png` |
| 16 | Box Precision Curve | Chapter 7, Figure 6.4 | From YOLO output | `Report /yolo_prescription_v4/BoxP_curve.png` |
| 17 | Box Recall Curve | Chapter 7, Figure 6.5 | From YOLO output | `Report /yolo_prescription_v4/BoxR_curve.png` |
| 18 | Box F1-Score Curve | Chapter 7, Figure 6.6 | From YOLO output | `Report /yolo_prescription_v4/BoxF1_curve.png` |
| 19 | Precision-Recall Curve | Chapter 7, Figure 6.7 | From YOLO output | `Report /yolo_prescription_v4/BoxPR_curve.png` |
| 20 | Confusion Matrix | Chapter 7, Figure 6.8 | From YOLO output | `Report /yolo_prescription_v4/confusion_matrix.png` |
| 21 | Normalised Confusion Matrix | Chapter 7, Figure 6.9 | From YOLO output | `Report /yolo_prescription_v4/confusion_matrix_normalized.png` |
| 22 | Validation Labels | Chapter 7, Figure 6.10a | From YOLO output | `Report /yolo_prescription_v4/val_batch0_labels.jpg` |
| 23 | Validation Predictions | Chapter 7, Figure 6.10b | From YOLO output | `Report /yolo_prescription_v4/val_batch0_pred.jpg` |

---

## 📸 Diagram #1 — Business Model Canvas
**Report Location:** Chapter 1, Section 1.2, Figure 1.2  
**Type:** Create manually in draw.io, Canva, or PowerPoint

### What to draw:
Create a standard Business Model Canvas with 9 blocks:

```
┌──────────────────┬──────────────────┬──────────────────┬──────────────────┬──────────────────┐
│   Key Partners   │  Key Activities  │ Value Proposition│ Customer Relat.  │ Customer Segments│
│                  │                  │                  │                  │                  │
│ • Pharmacies     │ • AI model       │ • AI prescription│ • Self-service   │ • Patients       │
│ • Hospitals      │   training       │   digitization   │   via app        │   (all ages)     │
│ • Firebase       │ • App            │ • Medicine       │ • Push           │ • Doctors/       │
│   (Google)       │   development    │   reminders      │   notifications  │   Clinics        │
│ • Railway        │ • Data           │ • Appointment    │ • Appointment    │ • Pharmacies     │
│   (Cloud)        │   collection     │   booking        │   system         │                  │
│                  │ • Cloud          │ • Patient-doctor │                  │                  │
│                  │   deployment     │   connection     │                  │                  │
├──────────────────┴──────────────────┤                  ├──────────────────┴──────────────────┤
│          Key Resources              │                  │              Channels               │
│                                     │                  │                                     │
│ • AI models (YOLO + PaddleOCR)      │                  │ • Google Play Store                 │
│ • Firebase infrastructure           │                  │ • Direct APK distribution           │
│ • Railway cloud server              │                  │                                     │
│ • 48,014-entry medicine list        │                  │                                     │
├─────────────────────────────────────┴──────────────────┼─────────────────────────────────────┤
│              Cost Structure                             │           Revenue Streams            │
│                                                         │                                     │
│ • Cloud hosting (Railway ~$5-20/month)                  │ • Freemium model                    │
│ • Firebase (free tier)                                  │ • Premium features                  │
│ • Google Play ($25 one-time)                            │ • Pharmacy partnerships             │
│ • Development time                                      │                                     │
└─────────────────────────────────────────────────────────┴─────────────────────────────────────┘
```

---

## 📸 Diagram #2 — Use Case Diagram
**Report Location:** Chapter 5, Section 5.1, Figure 5.1  
**Type:** UML Use Case Diagram (draw in draw.io or StarUML)

### Actors:
1. **Patient** (stick figure, left side)
2. **Doctor** (stick figure, right side)  
3. **AI System** (box/actor, bottom)

### Patient Use Cases (ovals):
- Register / Login
- Capture Prescription (Camera)
- Select Prescription (Gallery)
- View Digitized Prescriptions
- Edit Extracted Data
- Save Prescription to Cloud
- Book Appointment
- Set Medicine Reminder
- View Nearby Hospitals
- View Notifications
- Manage Profile

### Doctor Use Cases (ovals):
- Register / Login
- View Patient List
- View Patient Prescriptions
- Accept / Reject Appointment
- Cancel Appointment (with confirmation)
- Write Doctor Order
- View Notifications
- Manage Profile

### AI System Use Cases (ovals):
- Assess Image Quality
- Detect Prescription Fields (YOLO)
- Extract Text (PaddleOCR)
- Group Medications (Spatial)

### Relationships:
- Patient —→ Capture Prescription —→ «include» —→ AI System: Detect Fields
- Patient —→ Book Appointment —→ «extend» —→ Doctor: Accept/Reject
- Doctor —→ View Patient Prescriptions —→ «include» —→ AI System processed data

---

## 📸 Diagram #3 — Activity Diagram (Swimlane)
**Report Location:** Chapter 5, Section 5.2, Figure 5.2  
**Type:** UML Activity Diagram with 3 swimlanes

### Swimlanes:
1. **Patient** (left)
2. **System / App** (middle)
3. **Doctor** (right)

### Flow:

```
PATIENT                          SYSTEM/APP                         DOCTOR
────────                         ──────────                         ──────
  │                                  │                                 │
  ● Start                            │                                 │
  │                                  │                                 │
  ▼                                  │                                 │
[Open App]                           │                                 │
  │                                  │                                 │
  ▼                                  │                                 │
[Login / Register] ───────────►  [Firebase Auth]                       │
  │                              [Verify Credentials]                  │
  ◄──────────────────────────── [Return Auth Result]                   │
  │                                  │                                 │
  ▼                                  │                                 │
<Patient or Doctor?>                 │                                 │
  │                                  │                                 │
  ├── Patient Role ──►               │                                 │
  │                                  │                                 │
  ▼                                  │                                 │
[Navigate to Scan]                   │                                 │
  │                                  │                                 │
  ▼                                  │                                 │
<Camera or Gallery?>                 │                                 │
  │                                  │                                 │
  ├── Camera ──► [Capture Image]     │                                 │
  ├── Gallery ──► [Select Image]     │                                 │
  │                                  │                                 │
  ▼                                  │                                 │
[Submit Image] ──────────────►  [Upload to Firebase Storage]           │
  │                              [Send to FastAPI Backend]             │
  │                                  │                                 │
  │                              [Quality Check (Blur)]                │
  │                              [YOLO Detection (12 classes)]         │
  │                              [PaddleOCR (3 attempts)]              │
  │                              [Spatial Grouping]                    │
  │                                  │                                 │
  ◄──────────────────────────── [Return JSON Result]                   │
  │                                  │                                 │
  ▼                                  │                                 │
[Review Results in Sheet]            │                                 │
  │                                  │                                 │
  ▼                                  │                                 │
[Edit if needed]                     │                                 │
  │                                  │                                 │
  ▼                                  │                                 │
[Confirm & Save] ────────────►  [Save to Firestore]                    │
  │                                  │                                 │
  │                                  │              Doctor Role ───►   │
  │                                  │                                 │
  │                                  │                                 ▼
  │                                  │                          [View Patient List]
  │                                  │                                 │
  │                                  │                                 ▼
  │                                  │                          [Select Patient]
  │                                  │                                 │
  │                                  │                                 ▼
  │                                  │                          [View Prescriptions]
  │                                  │                                 │
  ▼                                  │                                 │
[Book Appointment] ──────────►  [Create Appointment]                   │
  │                              [Create Notification] ──────────►[Receive Notification]
  │                                  │                                 │
  │                                  │                                 ▼
  │                                  │                          [Accept/Reject/Cancel]
  │                                  │                                 │
  ◄──────────────────────────── [Update Status + Notify]◄──────────────┘
  │                                  │
  ● End                              │
```

---

## 📸 Diagram #4 — Class Diagram
**Report Location:** Chapter 5, Section 5.3, Figure 5.3  
**Type:** UML Class Diagram

### Classes to draw:

```
┌─────────────────────────┐         ┌─────────────────────────┐
│         User            │         │      Prescription       │
├─────────────────────────┤         ├─────────────────────────┤
│ - uid: String           │         │ - prescriptionId: String│
│ - name: String          │         │ - userId: String        │
│ - email: String         │    1    │ - imageUrl: String      │
│ - role: String          │────────*│ - medications: List     │
│ - phone: String         │         │ - doctorName: String    │
│ - profilePicUrl: String │         │ - hospital: String      │
│ - specialization: String│         │ - diagnosis: String     │
│ - hospitalName: String  │         │ - date: String          │
│ - degree: String        │         │ - createdAt: Timestamp  │
│ - experience: String    │         ├─────────────────────────┤
├─────────────────────────┤         │ + toMap(): Map          │
│ + toMap(): Map          │         └─────────────────────────┘
└─────────────────────────┘                    │ contains *
                                               ▼
┌─────────────────────────┐         ┌─────────────────────────┐
│      Appointment        │         │       Medication        │
├─────────────────────────┤         ├─────────────────────────┤
│ - appointmentId: String │         │ - name: String          │
│ - patientId: String     │         │ - doseStrength: String  │
│ - doctorId: String      │         │ - dosageSchedule: String│
│ - date: String          │         │ - duration: String      │
│ - time: String          │         ├─────────────────────────┤
│ - status: String        │         │ + toMap(): Map          │
│ - type: String          │         └─────────────────────────┘
│ - notes: String         │
│ - createdAt: Timestamp  │
├─────────────────────────┤
│ + toMap(): Map          │         ┌─────────────────────────┐
└─────────────────────────┘         │       Notification      │
                                    ├─────────────────────────┤
┌─────────────────────────┐         │ - notificationId: String│
│        Reminder         │         │ - userId: String        │
├─────────────────────────┤         │ - title: String         │
│ - reminderId: String    │         │ - message: String       │
│ - userId: String        │         │ - isRead: Boolean       │
│ - medicineName: String  │         │ - type: String          │
│ - times: List<String>   │         │ - createdAt: Timestamp  │
│ - startDate: String     │         ├─────────────────────────┤
│ - isActive: Boolean     │         │ + toMap(): Map          │
│ - createdAt: Timestamp  │         └─────────────────────────┘
├─────────────────────────┤
│ + toMap(): Map          │
└─────────────────────────┘

REPOSITORY CLASSES:
┌──────────────────────────────┐    ┌──────────────────────────────┐
│    AuthRepository            │    │  PrescriptionRepository      │
├──────────────────────────────┤    ├──────────────────────────────┤
│ + login()                    │    │ + uploadImage()              │
│ + register()                 │    │ + extractPrescription()      │
│ + googleSignIn()             │    │ + savePrescription()         │
│ + logout()                   │    │ + getPrescriptions()         │
└──────────────────────────────┘    └──────────────────────────────┘
┌──────────────────────────────┐    ┌──────────────────────────────┐
│  AppointmentRepository       │    │  NotificationRepository      │
├──────────────────────────────┤    ├──────────────────────────────┤
│ + createAppointment()        │    │ + observeUnreadCount()       │
│ + getAppointments()          │    │ + getNotifications()         │
│ + updateStatus()             │    │ + markAsRead()               │
└──────────────────────────────┘    └──────────────────────────────┘
┌──────────────────────────────┐    ┌──────────────────────────────┐
│    UserRepository            │    │    ReminderRepository        │
├──────────────────────────────┤    ├──────────────────────────────┤
│ + getUserProfile()           │    │ + addReminder()              │
│ + updateProfile()            │    │ + getReminders()             │
│ + searchDoctors()            │    │ + deleteReminder()           │
└──────────────────────────────┘    └──────────────────────────────┘
```

### Relationships:
- User **1** ──────* Prescription (one user has many prescriptions)
- User **1** ──────* Appointment (one user has many appointments, as patient OR doctor)
- User **1** ──────* Notification (one user has many notifications)
- User **1** ──────* Reminder (one user has many reminders)
- Prescription **1** ──────* Medication (one prescription has many medications)

---

## 📸 Diagram #5 — Component Diagram
**Report Location:** Chapter 5, Section 5.4, Figure 5.4  
**Type:** UML Component Diagram

### Components to draw:

```
┌─────────────────────────────────────────────────────────────────────┐
│                     📱 Android Application                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                │
│  │  UI Layer   │  │  ViewModel  │  │ Repository  │                │
│  │ (Compose    │──│   Layer     │──│   Layer     │                │
│  │  Screens)   │  │ (8 VMs)     │  │ (6 Repos)   │                │
│  └─────────────┘  └─────────────┘  └──────┬──────┘                │
│                                            │                        │
│  ┌──────────────────────┐  ┌──────────────┴──────────────┐        │
│  │  Hilt DI Module      │  │  Navigation (NavGraph)       │        │
│  │  (AppModule +        │  │  (22 Routes)                 │        │
│  │   NetworkModule)     │  │                              │        │
│  └──────────────────────┘  └─────────────────────────────┘        │
└────────────────┬───────────────────────────┬────────────────────────┘
                 │ Firebase SDK              │ Retrofit2 (HTTPS)
                 ▼                           ▼
┌────────────────────────────┐   ┌──────────────────────────────────┐
│    ☁️ Firebase Cloud        │   │    ⚡ Railway Cloud                │
│  ┌──────────┐              │   │  ┌────────────────────────────┐  │
│  │   Auth   │              │   │  │    FastAPI Backend          │  │
│  └──────────┘              │   │  │  ┌──────────────────────┐  │  │
│  ┌──────────┐              │   │  │  │ Quality Checker      │  │  │
│  │Firestore │              │   │  │  │ (Blur Detection)     │  │  │
│  │(6 colls) │              │   │  │  └──────────────────────┘  │  │
│  └──────────┘              │   │  │  ┌──────────────────────┐  │  │
│  ┌──────────┐              │   │  │  │ YOLOv8s Detector     │  │  │
│  │ Storage  │              │   │  │  │ (12 classes)         │  │  │
│  │ (Images) │              │   │  │  └──────────────────────┘  │  │
│  └──────────┘              │   │  │  ┌──────────────────────┐  │  │
└────────────────────────────┘   │  │  │ PaddleOCR Engine     │  │  │
                                 │  │  │ (English)            │  │  │
                                 │  │  └──────────────────────┘  │  │
                                 │  │  ┌──────────────────────┐  │  │
                                 │  │  │ Spatial Grouper      │  │  │
                                 │  │  └──────────────────────┘  │  │
                                 │  └────────────────────────────┘  │
                                 └──────────────────────────────────┘
```

---

## 📸 Diagram #6 — Sequence Diagram
**Report Location:** Chapter 5, Section 5.5, Figure 5.5  
**Type:** UML Sequence Diagram

### Actors/Objects (left to right):
1. **Patient** (actor)
2. **ScanScreen** (lifeline)
3. **ScanViewModel** (lifeline)
4. **PrescriptionRepository** (lifeline)
5. **Firebase Storage** (lifeline)
6. **FastAPI Backend** (lifeline)
7. **Firestore** (lifeline)

### Messages (in order):

```
Patient    ScanScreen    ScanViewModel    PrescriptionRepo    Firebase Storage    FastAPI    Firestore
  │            │              │                  │                   │               │           │
  │──capture──►│              │                  │                   │               │           │
  │  image     │──process────►│                  │                   │               │           │
  │            │   Image      │──uploadImage────►│                   │               │           │
  │            │              │                  │──putFile─────────►│               │           │
  │            │              │                  │◄──imageUrl────────│               │           │
  │            │              │                  │                   │               │           │
  │            │              │──extractFields──►│                   │               │           │
  │            │              │                  │──POST /extract───────────────────►│           │
  │            │              │                  │                   │               │           │
  │            │              │                  │              [Quality Check]      │           │
  │            │              │                  │              [YOLO Detection]     │           │
  │            │              │                  │              [PaddleOCR]          │           │
  │            │              │                  │              [Spatial Grouping]   │           │
  │            │              │                  │                   │               │           │
  │            │              │                  │◄──JSON Response──────────────────│           │
  │            │              │◄─ExtractionResult│                   │               │           │
  │            │◄─show Sheet──│                  │                   │               │           │
  │◄─review───│              │                  │                   │               │           │
  │            │              │                  │                   │               │           │
  │──confirm──►│              │                  │                   │               │           │
  │  & save    │──save───────►│                  │                   │               │           │
  │            │              │──savePrescription►│                  │               │           │
  │            │              │                  │──set document────────────────────────────────►│
  │            │              │                  │◄─success─────────────────────────────────────│
  │            │              │◄─saved───────────│                   │               │           │
  │            │◄─success─────│                  │                   │               │           │
  │◄─done──────│              │                  │                   │               │           │
```

---

## 📸 Diagram #7 — Data Flow Diagram (DFD)
**Report Location:** Chapter 5, Section 5.6, Figure 5.6  
**Type:** Data Flow Diagram (Level 0 and Level 1)

### Level 0 (Context Diagram):
```
                    ┌─────────────┐
   Patient ────────►│             │────────► Doctor
 (prescription     │   MediScan  │  (patient records,
  image, data)     │   System    │   notifications)
   Patient ◄────────│             │◄──────── Doctor
 (digitized data,  │             │  (appointments,
  reminders)       └─────────────┘   orders)
```

### Level 1:
```
                        ┌─────────────────────┐
   Patient ──image─────►│ P1: Prescription    │──structured──► D1: Firestore
                        │     Extraction       │    data         (prescriptions)
                        └─────────────────────┘
                                │
                                ▼
                        ┌─────────────────────┐
   Patient ──request───►│ P2: Appointment     │──notification─► Doctor
                        │     Management       │
   Patient ◄──status────│                     │◄──response───── Doctor
                        └─────────────────────┘
                                │
                                ▼
                        D2: Firestore (appointments, notifications)

                        ┌─────────────────────┐
   Patient ──set───────►│ P3: Medicine        │──alarm────► Android AlarmManager
                        │     Reminders        │
   Patient ◄──alert─────│                     │
                        └─────────────────────┘
                                │
                                ▼
                        D3: Firestore (reminders)
```

---

## 📸 Diagram #8 — Deployment Diagram
**Report Location:** Chapter 5, Section 5.7, Figure 5.7  
**Type:** UML Deployment Diagram

### Nodes to draw:

```
┌──────────────────────────────────────────────────────────────────────┐
│                        📱 Android Device                              │
│  ┌────────────────────────────────────────────────────────────────┐  │
│  │                    MediScan APK                                 │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────────┐  │  │
│  │  │ CameraX  │ │ Compose  │ │ Hilt DI  │ │ Firebase SDK     │  │  │
│  │  └──────────┘ │ UI (28   │ └──────────┘ └──────────────────┘  │  │
│  │               │ screens) │ ┌──────────┐ ┌──────────────────┐  │  │
│  │               └──────────┘ │ Retrofit │ │ AlarmManager     │  │  │
│  │                            └──────────┘ └──────────────────┘  │  │
│  └────────────────────────────────────────────────────────────────┘  │
└──────────────────┬────────────────────────────┬──────────────────────┘
                   │ Firebase SDK               │ HTTPS (Retrofit2)
                   │ (HTTPS/WebSocket)          │ Multipart Upload
                   ▼                            ▼
┌──────────────────────────────┐    ┌──────────────────────────────────┐
│   ☁️ Firebase Cloud (Google)  │    │   ⚡ Railway Cloud (Docker)       │
│  ┌──────────────────────┐    │    │  ┌────────────────────────────┐  │
│  │ Authentication       │    │    │  │ Docker Container           │  │
│  │ (Email + Google)     │    │    │  │  ┌──────────────────────┐  │  │
│  └──────────────────────┘    │    │  │  │ Uvicorn ASGI Server  │  │  │
│  ┌──────────────────────┐    │    │  │  │  ┌────────────────┐  │  │  │
│  │ Firestore NoSQL DB   │    │    │  │  │  │ FastAPI App    │  │  │  │
│  │ 6 Collections:       │    │    │  │  │  │                │  │  │  │
│  │ • users              │    │    │  │  │  │ • /health      │  │  │  │
│  │ • prescriptions      │    │    │  │  │  │ • /check-qual  │  │  │  │
│  │ • appointments       │    │    │  │  │  │ • /extract     │  │  │  │
│  │ • notifications      │    │    │  │  │  └────────────────┘  │  │  │
│  │ • reminders          │    │    │  │  └──────────────────────┘  │  │
│  │ • doctor_orders      │    │    │  │  ┌──────────────────────┐  │  │
│  └──────────────────────┘    │    │  │  │ YOLO Model Weights   │  │  │
│  ┌──────────────────────┐    │    │  │  │ PaddleOCR Engine     │  │  │
│  │ Cloud Storage        │    │    │  │  │ Medicine List (48K)  │  │  │
│  │ (Prescription Images)│    │    │  │  └──────────────────────┘  │  │
│  └──────────────────────┘    │    │  └────────────────────────────┘  │
└──────────────────────────────┘    └──────────────────────────────────┘
```

---

## 📸 Diagram #9 — User Interface Screenshots
**Report Location:** Chapter 5, Section 5.8, Figure 5.8  
**Type:** App screenshots (take from actual running app on phone/emulator)

### Screenshots needed (6 total):

| # | Screen | What to show |
|---|--------|-------------|
| 9a | **Patient Home** | Dashboard with quick action cards (Scan, Appointments, Reminders, etc.) |
| 9b | **Scan Screen** | Camera preview with the capture button and gallery option |
| 9c | **Extraction Result** | Bottom sheet showing extracted medications, doctor info |
| 9d | **Doctor Dashboard** | Doctor home with patient count, appointment stats |
| 9e | **Doctor Appointments** | List of appointments with accept/reject/cancel buttons |
| 9f | **Medicine Reminders** | Patient reminder list with add reminder dialog |

> **How:** Run the app on your phone or emulator, navigate to each screen, and take a screenshot.

---

## 📸 Diagram #10 — Per-Class mAP@50 Bar Chart
**Report Location:** Chapter 7, Section 7.2, Figure 7.1  
**Type:** Create in Excel, Google Sheets, or Python matplotlib

### Data for the chart:

| Class | mAP@50 |
|-------|--------|
| HOSPITAL | 99.5% |
| DIAGNOSIS | 99.5% |
| MEDICINE | 99.2% |
| DATE | 99.1% |
| DURATION | 99.0% |
| DOSE_STRENGTH | 98.8% |
| DOCTOR_NAME | 98.5% |
| DOSAGE_SCHEDULE | 98.4% |
| PATIENT_NAME | 98.4% |
| TEST | 98.2% |
| AGE | 97.7% |
| DEGREE | 90.8% |

> **Style:** Horizontal bar chart, sorted descending by mAP@50. Use blue bars. Add percentage labels on each bar. Title: "Per-Class mAP@50 — YOLOv8s v4 (Best Model)"

---

## 📸 Diagram #11 — v1 vs v4 Comparison Chart
**Report Location:** Chapter 7, Section 7.2, Figure 7.2  
**Type:** Grouped bar chart (create in Excel or Python)

### Data:

| Class | v1 mAP@50 | v4 mAP@50 |
|-------|-----------|-----------|
| HOSPITAL | 14.2% | 99.5% |
| DIAGNOSIS | 11.0% | 99.5% |
| TEST | 25.7% | 98.2% |
| DOSE_STRENGTH | 51.5% | 98.8% |
| AGE | 54.4% | 97.7% |
| DOCTOR_NAME | 59.4% | 98.5% |
| PATIENT_NAME | 63.5% | 98.4% |
| DATE | 65.2% | 99.1% |
| DURATION | 68.1% | 99.0% |
| DOSAGE_SCHEDULE | 78.7% | 98.4% |
| MEDICINE | 84.3% | 99.2% |

> **Style:** Grouped bar chart with two colours (red = v1, green = v4). Title: "YOLOv8s mAP@50 — v1 (Baseline) vs v4 (Final Model)". Sort by improvement (biggest improvement first).

---

## 📸 Diagram #12 — Illegible Prescription Example
**Report Location:** Chapter 1, Figure 1.1  
**Type:** Photo from your dataset

> **What to use:** Select one of the hardest-to-read prescriptions from your collected 1,464 images. Choose one that clearly shows illegible handwriting to illustrate the problem MediScan solves.

---

## 📸 Diagram #13 — Sample Dataset Images
**Report Location:** Chapter 6, Section 6.1.1, Figure 6.1  
**Type:** 3-4 photos from your dataset

> **What to use:** Select 3-4 diverse prescriptions:
> 1. A handwritten prescription (hard to read)
> 2. A printed/typed prescription (clear format)
> 3. A mixed prescription (part handwritten, part printed)
> 4. A prescription with many fields (medicine, dose, schedule, duration, doctor name, hospital, etc.)

---

## 📸 Images from `Report /yolo_prescription_v4/` folder

These images are already generated from your YOLO training and should be **directly inserted** into the report at the indicated locations:

| # | File | Report Figure | Description |
|---|------|--------------|-------------|
| 14 | `Report /yolo_prescription_v4/labels.jpg` | Figure 6.2 | Dataset class distribution and bounding box statistics |
| 15 | `Report /yolo_prescription_v4/results.png` | Figure 6.3 | All training curves (loss, metrics, LR) across 150 epochs |
| 16 | `Report /yolo_prescription_v4/BoxP_curve.png` | Figure 6.4 | Precision vs confidence threshold per class |
| 17 | `Report /yolo_prescription_v4/BoxR_curve.png` | Figure 6.5 | Recall vs confidence threshold per class |
| 18 | `Report /yolo_prescription_v4/BoxF1_curve.png` | Figure 6.6 | F1-Score vs confidence threshold per class |
| 19 | `Report /yolo_prescription_v4/BoxPR_curve.png` | Figure 6.7 | Precision-Recall curve per class |
| 20 | `Report /yolo_prescription_v4/confusion_matrix.png` | Figure 6.8 | Raw confusion matrix (count-based) |
| 21 | `Report /yolo_prescription_v4/confusion_matrix_normalized.png` | Figure 6.9 | Normalised confusion matrix (percentage-based) |
| 22 | `Report /yolo_prescription_v4/val_batch0_labels.jpg` | Figure 6.10a | Validation batch — ground truth labels |
| 23 | `Report /yolo_prescription_v4/val_batch0_pred.jpg` | Figure 6.10b | Validation batch — model predictions |

---

## ✅ Checklist Before Submitting

- [ ] All 11 diagrams created (Diagrams #1–#8, #10, #11, #12)
- [ ] 6 app screenshots taken (Diagram #9a–#9f)
- [ ] 3-4 dataset sample images selected (Diagram #13)
- [ ] 10 YOLO training images placed from `yolo_prescription_v4/` folder (Diagrams #14–#23)
- [ ] All figures numbered correctly matching ACADEMIC_REPORT.md
- [ ] All `📸 SCREENSHOT PLACEHOLDER` markers in report replaced with actual images
- [ ] Business Model Canvas is clean and professional
- [ ] All UML diagrams use standard notation (draw.io recommended)
- [ ] Charts (#10, #11) have proper labels, titles, and legends

---

> **Tools Recommendation:**
> - **UML Diagrams (#2-#8):** draw.io (free, web-based) — https://app.diagrams.net/
> - **Business Model Canvas (#1):** Canva or draw.io
> - **Charts (#10, #11):** Google Sheets, Excel, or Python matplotlib
> - **Screenshots (#9):** Android phone or Android Studio emulator
