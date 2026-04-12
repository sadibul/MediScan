# MediScan — Capstone Project Poster Content

> **Instructions:** Use this content to design the poster in PowerPoint, Canva, or Figma.  
> Follow the same layout as the teacher's EyeCareAI sample poster.  
> The poster should be **landscape orientation**, single page, with a **dark-blue header banner**.

---

## ═══════════════════════════════════════════════════
## TOP BANNER (Dark Blue Background, White Text)
## ═══════════════════════════════════════════════════

### Title:
**MediScan: An AI-Powered Prescription Digitization and Smart Medicine Management System**

### University Info (Left side of banner):
East West University  
Department of Computer Science and Engineering

### Supervisor (Right side of banner):
**Supervised by:**  
[Supervisor Name]  
[Designation]  
Department of CSE, East West University

---

## ═══════════════════════════════════════════════════
## GROUP MEMBERS (Below the banner, left-aligned)
## ═══════════════════════════════════════════════════

| Name | ID |
|------|----|
| Sadibul Islam Sadib | 2021-1-60-141 |
| Md Touhidul Islam Alif | 2021-1-60-142 |
| Md. Ashikul Islam | 2021-1-60-048 |

---

## ═══════════════════════════════════════════════════
## ABSTRACT
## ═══════════════════════════════════════════════════

Illegible handwritten prescriptions are a major cause of medication errors in Bangladesh, where over 90% of prescriptions are still handwritten. MediScan is an AI-powered Android application that digitizes prescription images using a YOLOv8s object detection model and PaddleOCR text extraction engine. The system detects 12 distinct field classes — including medicine names, dosages, schedules, and durations — achieving 98.1% mAP@50 accuracy. Built with Kotlin and Jetpack Compose, the application provides role-based interfaces for patients and doctors, featuring prescription management, appointment booking, medicine reminders, and real-time notifications through Firebase cloud services.

---

## ═══════════════════════════════════════════════════
## INTRODUCTION
## ═══════════════════════════════════════════════════

In Bangladesh, approximately 170 million people rely on a healthcare system that remains predominantly paper-based. Handwritten prescriptions are frequently illegible, leading to medication errors, incorrect dosages, and adverse drug reactions. The WHO estimates that medication-related errors cost $42 billion USD annually worldwide. Despite advances in computer vision and OCR technologies, prescription digitization for the South Asian context remains underexplored due to non-standardized formats and complex multi-field layouts. MediScan addresses this gap with a smartphone-based solution combining deep learning detection, OCR extraction, and a comprehensive healthcare management platform.

---

## ═══════════════════════════════════════════════════
## OBJECTIVES
## ═══════════════════════════════════════════════════

The primary objective of this project is to develop a YOLOv8s model capable of detecting 12 prescription field classes with greater than 95% mAP accuracy, combined with a multi-stage AI pipeline integrating object detection, OCR text extraction, and spatial medication grouping. The project further aims to design a role-based Android application with dedicated patient and doctor interfaces using Kotlin and Jetpack Compose, while implementing cloud-based features including prescription storage, appointment booking, medicine reminders, and real-time notifications via Firebase. Finally, the AI backend is deployed on Railway Cloud to ensure scalable, cost-effective inference for real-world usage.

---

## ═══════════════════════════════════════════════════
## PROPOSED METHODOLOGY
## ═══════════════════════════════════════════════════

The system employs a four-stage AI pipeline: blur detection filters low-quality images, a custom YOLOv8s model (11.1M parameters) detects and localizes 12 prescription field classes, PaddleOCR extracts text from each detected region using a 3-attempt strategy for maximum accuracy, and a spatial grouping algorithm links each medicine with its dose, schedule, and duration. The pipeline runs on a FastAPI backend deployed via Docker on Railway Cloud, with all user data stored in Firebase Firestore.

---

## ═══════════════════════════════════════════════════
## SYSTEM ARCHITECTURE
## ═══════════════════════════════════════════════════

> **📸 INSERT IMAGE HERE:** Place the MediScan System Architecture diagram.  
> *(Use the diagram generated from `Report /system_architecture.md` prompt)*

---

## ═══════════════════════════════════════════════════
## EXPERIMENTS AND RESULT ANALYSIS
## ═══════════════════════════════════════════════════

The YOLOv8s model was trained for 150 epochs on 1,806 images (1,464 original + augmented) containing 31,788 bounding box annotations. The model achieved outstanding performance across all evaluation metrics:

| Metric | Value |
|--------|-------|
| mAP@50 | 98.1% |
| mAP@50-95 | 86.5% |
| Precision | 97.1% |
| Recall | 95.0% |
| F1-Score | 96.0% |

Through four model iterations, performance improved dramatically from 52.4% mAP (v1) to 98.1% mAP (v4) — a +45.7% gain. All 12 classes achieved above 90% mAP@50, with the highest being HOSPITAL and DIAGNOSIS at 99.5%. The core medication detection fields (Medicine, Dose, Schedule, Duration) all exceed 98% mAP@50, ensuring reliable extraction for clinical use.

### Images to include in this section (arrange in a grid):

> **Image 1 (Large — Main Result):** `Poster/poster diagram/1.jpeg`  
> *(This is the primary result visualization — place prominently)*

> **Image 2:** `Poster/poster diagram/Per-Class mAP@50 Chart.png`  
> *(Bar chart showing per-class mAP@50 values for all 12 classes)*

> **Image 3:** `Poster/poster diagram/Model Version Comparison.png`  
> *(Grouped bar chart comparing v1 vs v4 performance across all classes)*

> **Image 4:** Training Results Curves  
> *(Use `Report /yolo_prescription_v4/results.png` — shows loss curves and metrics across 150 epochs)*

> **Image 5:** Confusion Matrix  
> *(Use `Report /yolo_prescription_v4/confusion_matrix_normalized.png` — shows inter-class confusion)*

> **Image 6:** Precision-Recall Curve  
> *(Use `Report /yolo_prescription_v4/BoxPR_curve.png` — shows PR curves per class)*

> **Image 7:** F1-Score Curve  
> *(Use `Report /yolo_prescription_v4/BoxF1_curve.png` — shows F1 vs confidence threshold)*

---

## ═══════════════════════════════════════════════════
## CONCLUSION
## ═══════════════════════════════════════════════════

MediScan successfully demonstrates that AI-powered prescription digitization is technically feasible and practically effective for the Bangladeshi healthcare context. The YOLOv8s model achieved 98.1% mAP@50 across 12 field classes, significantly exceeding the 95% target. The Android application with 28 screens provides a comprehensive healthcare platform covering prescription scanning, appointment management, medicine reminders, and doctor-patient communication. Future work includes Bangla OCR support, on-device inference for offline scanning, and pharmacy integration for direct medicine ordering.

---

## ═══════════════════════════════════════════════════
## REFERENCES
## ═══════════════════════════════════════════════════

[1] J. Redmon, S. Divvala, R. Girshick, and A. Farhadi, "You Only Look Once: Unified, Real-Time Object Detection," *IEEE CVPR*, pp. 779–788, 2016.

[2] G. Jocher, A. Chaurasia, and J. Qiu, "Ultralytics YOLOv8," Ultralytics, 2023. [Online]. Available: https://github.com/ultralytics/ultralytics

[3] Y. Du et al., "PP-OCR: A Practical Ultra Lightweight OCR System," *arXiv:2009.09941*, 2020.

[4] Y. Xu et al., "LayoutLM: Pre-training of Text and Layout for Document Image Understanding," *ACM SIGKDD*, pp. 1192–1200, 2020.

[5] O. P. Patel and A. Patel, "Handwritten Medical Prescription Recognition using CNN," *Int. J. Eng. Res. & Tech.*, vol. 10, no. 5, pp. 445–451, 2021.

[6] K. He, X. Zhang, S. Ren, and J. Sun, "Deep Residual Learning for Image Recognition," *IEEE CVPR*, pp. 770–778, 2016.

[7] R. S. H. Istepanian, S. Laxminarayan, and C. S. Pattichis, *M-Health: Emerging Mobile Health Systems*, Springer, 2006.

---

## ═══════════════════════════════════════════════════
## POSTER DESIGN GUIDELINES
## ═══════════════════════════════════════════════════

### Layout (Match EyeCareAI sample):
```
┌──────────────────────────────────────────────────────────────────────────────────┐
│  EAST WEST        MediScan: An AI-Powered Prescription         Supervised by:   │
│  UNIVERSITY       Digitization and Smart Medicine               [Supervisor]     │
│  Dept. of CSE     Management System                             Dept. of CSE     │
│                                                                  EWU             │
├──────────────────────────────────────────────────────────────────────────────────┤
│  Group Members: Sadibul Islam Sadib (2021-1-60-141),                            │
│  Md Touhidul Islam Alif (2021-1-60-142), Md. Ashikul Islam (2021-1-60-048)     │
├────────────────────┬────────────────────┬────────────────────────────────────────┤
│                    │                    │                                         │
│     ABSTRACT       │   INTRODUCTION     │         OBJECTIVES                     │
│                    │                    │                                         │
│  (Text from above) │ (Text from above)  │  (Bullet points from above)            │
│                    │                    │                                         │
├────────────────────┴────────────────────┼────────────────────────────────────────┤
│                                         │                                         │
│     PROPOSED METHODOLOGY                │    SYSTEM ARCHITECTURE                 │
│                                         │                                         │
│  (Text from above)                      │    📸 [System Architecture Diagram]     │
│                                         │                                         │
├─────────────────────────────────────────┴────────────────────────────────────────┤
│                                                                                   │
│     EXPERIMENTS AND RESULT ANALYSIS                                              │
│                                                                                   │
│  [Metrics Table]    [Image 1]    [Image 2]    [Image 3]                          │
│                     [Image 4]    [Image 5]    [Image 6]    [Image 7]             │
│                                                                                   │
├──────────────────────┬───────────────────────────────────────────────────────────┤
│                      │                                                            │
│     CONCLUSION       │                    REFERENCES                             │
│                      │                                                            │
│  (Text from above)   │  [1] Redmon et al. ... [7] Istepanian et al.             │
│                      │                                                            │
└──────────────────────┴───────────────────────────────────────────────────────────┘
```

### Color Scheme:
- **Header Banner:** Dark Blue (#1A237E or #283593) with White text
- **Section Titles:** Dark Blue, Bold
- **Body Text:** Black or Dark Gray (#333333)
- **Background:** White
- **Accent/Highlights:** Light Blue (#E3F2FD) for section backgrounds if needed
- **Table Headers:** Dark Blue background, White text

### Fonts:
- **Title:** Bold, 28-36pt (Roboto or Arial)
- **Section Headers:** Bold, 16-20pt
- **Body Text:** Regular, 11-14pt
- **References:** Regular, 9-11pt

### Image Placement (7 images for Results section):
1. `Poster/poster diagram/1.jpeg` — **Largest**, center-top of results section
2. `Poster/poster diagram/Per-Class mAP@50 Chart.png` — Medium size
3. `Poster/poster diagram/Model Version Comparison.png` — Medium size
4. `Report /yolo_prescription_v4/results.png` — Training curves (small-medium)
5. `Report /yolo_prescription_v4/confusion_matrix_normalized.png` — Small-medium
6. `Report /yolo_prescription_v4/BoxPR_curve.png` — Small-medium
7. `Report /yolo_prescription_v4/BoxF1_curve.png` — Small-medium

### Quick Checklist:
- [ ] Dark blue header banner with project title, university, and supervisor
- [ ] Group members with IDs listed below the banner
- [ ] Abstract section (~80 words)
- [ ] Introduction section (~90 words)
- [ ] Objectives section (paragraph, ~75 words)
- [ ] Proposed Methodology section (~130 words, text-based, no diagram)
- [ ] System Architecture section with diagram image
- [ ] Experiments & Results section with metrics table + 7 images
- [ ] Conclusion section (~70 words)
- [ ] 7 References in IEEE format
- [ ] Clean, academic, professional look matching sample poster
