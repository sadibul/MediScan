# System Architecture Diagram — AI Prompt

> **Purpose:** Copy the prompt below and paste it into an AI image generation tool (e.g., ChatGPT with DALL·E, Eraser.io, or any AI diagram tool) to generate the MediScan System Architecture diagram for Chapter 5 of the academic report.
>
> **Caption for Report:** *Figure 5.X: MediScan System Architecture*

---

## ✅ Prompt to Generate the Diagram

```
Create a professional system architecture diagram for a project called "MediScan — AI-Powered Prescription Digitization System". The diagram must follow a clean, academic style similar to a university capstone report. Use a white background with a dark-blue header banner at the top.

═══════════════════════════════════════════════════════════
HEADER
═══════════════════════════════════════════════════════════
- At the very top, place a wide dark-blue (navy) rectangular banner spanning the full width.
- Inside the banner, center the text "System Architecture" in large, bold, white font.

═══════════════════════════════════════════════════════════
LAYOUT — LEFT TO RIGHT FLOW
═══════════════════════════════════════════════════════════
The diagram flows from LEFT to RIGHT, with the following major zones:

ZONE 1 (Far Left) — Users
ZONE 2 — Android App (Mobile Device)
ZONE 3 — Role-Based Authentication
ZONE 4 — Role Split (Patient / Doctor)
ZONE 5 — Feature Modules for each role
ZONE 6 (Far Right) — Backend Services (Firebase + FastAPI + AI Model)

═══════════════════════════════════════════════════════════
ZONE 1 — USERS (Far Left)
═══════════════════════════════════════════════════════════
- Draw a group of user stick figures (2-3 people icon) labeled "Users" below.
- From Users, draw an arrow pointing right to a smartphone icon.

═══════════════════════════════════════════════════════════
ZONE 2 — ANDROID APP (Mobile Device)
═══════════════════════════════════════════════════════════
- Draw a smartphone/mobile device icon labeled "Android App" below it.
- Subtitle text: "Kotlin + Jetpack Compose"
- From the smartphone, draw an arrow pointing right to "Role Based Authentication".

═══════════════════════════════════════════════════════════
ZONE 3 — ROLE-BASED AUTHENTICATION
═══════════════════════════════════════════════════════════
- Draw a shield or lock icon with a checkmark, labeled "Role Based Authentication" below.
- Subtitle: "Firebase Auth"
- From this node, draw TWO arrows splitting upward and downward:
  - Upper arrow → goes to "Patient" (top half of diagram)
  - Lower arrow → goes to "Doctor" (bottom half of diagram)

═══════════════════════════════════════════════════════════
ZONE 4 & 5 — PATIENT ROLE (Upper Half)
═══════════════════════════════════════════════════════════
- Draw a patient icon (person with a medical cross or clipboard) labeled "Patient" below.
- From Patient, draw arrows to the right connecting to these feature modules (each as a separate box/node with an appropriate icon):

  1. 📷 "Scan Prescription" — camera icon
     → This connects with an arrow to the FastAPI + AI Model box (far right)
  2. 💊 "My Prescriptions" — document/file icon
     → Connects to Firebase Firestore
  3. 📅 "Book Appointment" — calendar icon
     → Connects to Firebase Firestore
  4. ⏰ "Medicine Reminders" — alarm/bell icon
     → Connects to Firebase Firestore
  5. 🗺️ "Nearby Hospitals" — map pin icon
     → Connects to Google Maps API
  6. 🔔 "Notifications" — bell icon
     → Connects to Firebase Firestore (Real-time Listener)
  7. 👤 "Profile" — person icon

═══════════════════════════════════════════════════════════
ZONE 4 & 5 — DOCTOR ROLE (Lower Half)
═══════════════════════════════════════════════════════════
- Draw a doctor icon (person with stethoscope or medical coat) labeled "Doctor" below.
- From Doctor, draw arrows to the right connecting to these feature modules:

  1. 📋 "Patient List" — people/group icon
     → Connects to Firebase Firestore
  2. 📄 "Patient Records" — medical record/chart icon
     → Connects to Firebase Firestore
  3. 📊 "Analytics Dashboard" — bar chart icon
     → Connects to Firebase Firestore
  4. ✅ "Appointment Management" — calendar with checkmark icon
     → Connects to Firebase Firestore
  5. 📝 "Doctor Orders" — prescription pad/write icon
     → Connects to Firebase Firestore
  6. 🔔 "Notifications" — bell icon
     → Connects to Firebase Firestore (Real-time Listener)
  7. 👤 "Profile" — person icon

═══════════════════════════════════════════════════════════
ZONE 6 — BACKEND SERVICES (Right Side)
═══════════════════════════════════════════════════════════
Place these 3 backend service blocks on the right side, stacked vertically:

─── TOP: Firebase Cloud ───
Draw a rounded rectangle or cloud shape containing:
  - Firebase icon (flame icon or cloud icon with fire)
  - Label: "Firebase Cloud"
  - Inside, list these 3 sub-services vertically:
    1. 🔐 "Authentication" (Email + Google Sign-In)
    2. 🗄️ "Firestore Database" (6 Collections: users, prescriptions, appointments, notifications, reminders, doctor_orders)
    3. 📦 "Cloud Storage" (Prescription Images)
  - Multiple arrows come into this box from Patient and Doctor feature modules.

─── MIDDLE: FastAPI Backend ───
Draw a separate rounded rectangle:
  - Server/gear icon
  - Label: "FastAPI Backend"
  - Subtitle: "Railway Cloud (Docker)"
  - Inside, list these sub-components vertically:
    1. "Uvicorn ASGI Server"
    2. "/extract API endpoint"
    3. "/check-quality endpoint"
  - An arrow goes from this box to the "AI Model" box.
  - An arrow comes into this box from the Patient's "Scan Prescription" feature.

─── BOTTOM: AI Model ───
Draw a separate rounded rectangle:
  - Brain/AI icon
  - Label: "AI Model"
  - Inside, list these sub-components:
    1. "YOLOv8s — Field Detection (12 classes)"
    2. "PaddleOCR — Text Extraction"
    3. "Spatial Grouping — Medication Matching"
    4. "Quality Checker — Blur Detection"
  - An arrow connects from FastAPI to this box.
  - An arrow returns from this box back through FastAPI to the Patient's Scan feature (showing the response flow).

═══════════════════════════════════════════════════════════
CONNECTION ARROWS & FLOW
═══════════════════════════════════════════════════════════
- All arrows should be dark gray or black, thin, with small arrowheads.
- Use straight lines with 90-degree bends (not curved) for a clean look.
- The main data flow arrows should be:
  1. Users → Android App → Authentication → Patient / Doctor (role split)
  2. Patient → Scan Prescription → FastAPI Backend → AI Model → (response back)
  3. Patient features → Firebase Cloud (Firestore + Storage)
  4. Doctor features → Firebase Cloud (Firestore)
  5. Patient → Nearby Hospitals → Google Maps API
  6. Authentication ↔ Firebase Auth (bidirectional)

═══════════════════════════════════════════════════════════
SPECIAL FLOW — AI PRESCRIPTION EXTRACTION (Highlight this)
═══════════════════════════════════════════════════════════
Show a distinctive arrow (slightly thicker or colored blue) for this path:
  Patient → "Scan Prescription" → FastAPI Backend → AI Model
  AI Model → FastAPI Backend → "Scan Prescription" → Patient sees results
This is the core AI pipeline and should be visually prominent.

═══════════════════════════════════════════════════════════
STYLE & DESIGN RULES
═══════════════════════════════════════════════════════════
1. WHITE background — clean and minimal.
2. DARK BLUE header banner at top with white text "System Architecture".
3. Use FLAT, modern icons (not 3D). Similar to Material Design or Lucide icons.
4. Color palette:
   - Dark Blue (#1A237E or #283593) — header, primary accents
   - Light Blue (#E3F2FD) — Patient feature boxes background
   - Light Green (#E8F5E9) — Doctor feature boxes background
   - Light Gray (#F5F5F5) — Backend service box backgrounds
   - Orange/Amber (#FFF3E0) — AI Model box background
   - White — overall background
5. Each feature module should be a rounded-corner rectangle with:
   - A small icon on the left or top
   - The feature name as text
   - Light colored background matching its group
6. Arrows: thin (1-2px), dark gray, with small solid arrowheads.
7. Font: clean sans-serif (like Roboto, Inter, or Open Sans).
8. All text should be easily readable — not too small.
9. The diagram should look academic and professional — suitable for a university capstone report.
10. NO decorative elements, no gradients, no shadows — keep it flat and clean.

═══════════════════════════════════════════════════════════
CAPTION
═══════════════════════════════════════════════════════════
At the bottom center of the diagram, add the caption in bold:
"Figure : MediScan System Architecture"

═══════════════════════════════════════════════════════════
SIZE & ASPECT RATIO
═══════════════════════════════════════════════════════════
- Landscape orientation (wider than tall).
- Aspect ratio approximately 16:10 or 3:2.
- The diagram should be high resolution and clear when printed on A4 paper.
```

---

## 📐 Reference Layout (ASCII Blueprint)

Use this as a spatial reference when drawing:

```
┌──────────────────────────────────────────────────────────────────────────────────────────────┐
│                                    System Architecture                                        │
│                                  (dark blue banner, white text)                               │
└──────────────────────────────────────────────────────────────────────────────────────────────┘

                                        ┌─── PATIENT (upper half) ───────────────────────────┐
                                        │                                                     │
                                        │  👤 Patient                                         │
                                        │     │                                               │
                                        │     ├──► 📷 Scan Prescription ─────────► FastAPI ──► AI Model
                                        │     ├──► 💊 My Prescriptions ──────────► Firebase Firestore
  👥        📱         🔐                │     ├──► 📅 Book Appointment ──────────► Firebase Firestore
 Users ──► Android ──► Role-Based ──────│     ├──► ⏰ Medicine Reminders ─────────► Firebase Firestore
           App         Auth             │     ├──► 🗺️ Nearby Hospitals ──────────► Google Maps API
                                        │     ├──► 🔔 Notifications ─────────────► Firebase Firestore
                                        │     └──► 👤 Profile ───────────────────► Firebase Firestore
                                        └────────────────────────────────────────────────────────┘
                                        ┌─── DOCTOR (lower half) ────────────────────────────┐
                                        │                                                     │
                                        │  👨‍⚕️ Doctor                                         │
                                        │     │                                               │
                                        │     ├──► 📋 Patient List ──────────────► Firebase Firestore
                                        │     ├──► 📄 Patient Records ───────────► Firebase Firestore
                                        │     ├──► 📊 Analytics Dashboard ───────► Firebase Firestore
                                        │     ├──► ✅ Appointment Mgmt ──────────► Firebase Firestore
                                        │     ├──► 📝 Doctor Orders ─────────────► Firebase Firestore
                                        │     ├──► 🔔 Notifications ─────────────► Firebase Firestore
                                        │     └──► 👤 Profile ───────────────────► Firebase Firestore
                                        └────────────────────────────────────────────────────────┘

                                                            BACKEND SERVICES (right side)
                                                    ┌─────────────────────────────────────┐
                                                    │        ☁️ Firebase Cloud              │
                                                    │  ┌───────────────────────────────┐  │
                                                    │  │ 🔐 Authentication             │  │
                                                    │  │ 🗄️ Firestore (6 collections)  │  │
                                                    │  │ 📦 Cloud Storage (images)      │  │
                                                    │  └───────────────────────────────┘  │
                                                    ├─────────────────────────────────────┤
                                                    │        ⚡ FastAPI Backend            │
                                                    │     Railway Cloud (Docker)          │
                                                    │  ┌───────────────────────────────┐  │
                                                    │  │ Uvicorn → /extract, /quality  │  │
                                                    │  └───────────────────────────────┘  │
                                                    ├─────────────────────────────────────┤
                                                    │        🧠 AI Model                  │
                                                    │  ┌───────────────────────────────┐  │
                                                    │  │ YOLOv8s (12 classes)          │  │
                                                    │  │ PaddleOCR (English)           │  │
                                                    │  │ Spatial Grouping              │  │
                                                    │  │ Quality Checker               │  │
                                                    │  └───────────────────────────────┘  │
                                                    └─────────────────────────────────────┘
```

---

## 🛠️ Recommended Tools to Generate This

| Tool | How to Use | Best For |
|------|-----------|----------|
| **ChatGPT (DALL·E)** | Paste the prompt above directly | Quick image generation |
| **Eraser.io** | Paste the prompt, it creates editable diagrams | Best for clean editable diagrams |
| **draw.io / diagrams.net** | Use the ASCII blueprint as reference, draw manually | Full control, export as PNG/SVG |
| **Mermaid.js** | Convert to Mermaid syntax, render online | Code-based diagrams |
| **Canva** | Use the layout guide, drag-and-drop icons | Poster-quality output |
| **Excalidraw** | Hand-drawn style with the layout reference | Whiteboard aesthetic |
| **Figma** | Recreate using the blueprint as a template | Professional vector output |

---

## 📝 Quick Checklist Before Submitting

- [ ] Header banner says "System Architecture" in dark blue with white text
- [ ] Users → Android App → Role-Based Auth → Patient / Doctor split is clear
- [ ] Patient has 7 feature modules with icons
- [ ] Doctor has 7 feature modules with icons
- [ ] Firebase Cloud box shows Auth + Firestore + Storage
- [ ] FastAPI Backend box shows Railway Cloud + endpoints
- [ ] AI Model box shows YOLOv8s + PaddleOCR + Spatial Grouping + Quality Checker
- [ ] The AI pipeline path (Patient → Scan → FastAPI → AI Model → response) is visually prominent
- [ ] Google Maps API is connected to "Nearby Hospitals"
- [ ] All arrows are clean with arrowheads
- [ ] Caption at bottom: "Figure : MediScan System Architecture"
- [ ] Color scheme is clean and consistent (blue patient, green doctor, gray backend, orange AI)
- [ ] Landscape orientation, readable when printed on A4
