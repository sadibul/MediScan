 

 

**MediScan: An AI-Powered Prescription Digitization and Smart Medicine Management System**

 

**Name                                            	   ID**

   Sadibul Islam Sadib                                           2021-1-60-141

   Md Touhidul Islam Alif                                        2021-1-60-142

   Md. Ashikul Islam                                             2021-1-60-048

 

                                            	**Supervised By:**  
                                 	[Supervisor Name],

          	[Designation], 

          	Department of Computer Science and Engineering

                                	East West University

 

 

 

 

 

 

**Department of Computer Science and Engineering**

 **East  West  University**

**Dhaka-1212,  Bangladesh**

 

 

**April, 2026**

 

 

# **Declaration**

 

 

We, Sadibul Islam Sadib, Md Touhidul Islam Alif, and Md. Ashikul Islam, hereby declare that the work presented in this capstone project report is the outcome of the investigation performed by us under the supervision of [Supervisor Name], [Designation], Department of Computer Science and Engineering, East West University. We also declare that no part of this project has been or is being submitted elsewhere for the award of any degree or diploma, except for publication.  
 

                	Countersigned                                                                	Signature

 

. . . . . . . . . . . . . . . . . . . . . . . .                                    	. . . . . . . . . . . . . . . . . . . . . . . .  
 

	[Supervisor Name]                                       	       	Sadibul Islam Sadib

     	**Supervisor**                                                        	 	2021-1-60-141  

     	                                                                    	 	Md Touhidul Islam Alif

     	                                                                    	 	2021-1-60-142  

     	                                                                    	 	Md. Ashikul Islam

     	                                                                    	 	2021-1-60-048  

 

 

# **Letter of Acceptance**

 

 

 

 

 

The capstone project report entitled " **MediScan: An AI-Powered Prescription Digitization and Smart Medicine Management System**" is submitted by Sadibul Islam Sadib, Md Touhidul Islam Alif, and Md. Ashikul Islam to the Department of Computer Science and Engineering, East West University, Dhaka, Bangladesh and is accepted for the partial fulfillment of the requirement for the degree of Bachelor of Science in Computer Science and Engineering on ( 	/     /     	).

 

Board of Examiners

1\.                                                                                     	

|  |  |  |  |
| :---- | :---- | :---- | :---- |
|  |  |  |  |
|  |  |  |  |

[Designation]  
Department of Computer Science and Engineering  
East West University  
   
2\.                                                                                     	

|  |  |
| :---- | :---- |
|  |  |

 

 

#                                                                               	  **Abstract**

 

 

Prescriptions remain the primary communication medium between physicians and patients in Bangladesh's healthcare system. However, handwritten prescriptions are frequently illegible, leading to medication errors, incorrect dosages, and potential health risks. MediScan addresses this critical problem by developing an AI-powered Android application that digitizes prescription images into structured, editable medication data. The system employs a multi-stage AI pipeline consisting of image quality assessment (ResNet18), field detection (YOLOv8s trained on 12 prescription field classes), and optical character recognition (PaddleOCR). The YOLOv8s detection model was trained on 1,464 real prescription images collected from patients and pharmacies in Bangladesh, augmented to 1,806 training samples, achieving a mean Average Precision (mAP@50) of 98.1% and an F1-Score of 96.0% across all 12 classes. The Android application, built with Kotlin and Jetpack Compose following MVVM architecture, provides role-based interfaces for patients and doctors, supporting prescription scanning, appointment booking, medicine reminders, and real-time notifications via Firebase. The AI backend, deployed on Railway Cloud using FastAPI, processes prescription images and returns structured JSON data. Although the image quality classifier was disabled due to insufficient training data causing false rejections, the core detection and OCR pipeline demonstrates production-ready accuracy. This project demonstrates that deep learning-based prescription digitization is both feasible and highly accurate for the Bangladeshi healthcare context.

 

 

## **Acknowledgments**

 

   
 

As it is true for everyone, we have also arrived at this point of achieving a goal in our lives through various interactions with and help from other people. However, written words are often elusive and harbour diverse interpretations even in one's mother language. Therefore, we would not like to make efforts to find the best words to express our thankfulness other than simply listing those people who have contributed to this project in an essential way. This work was carried out in the Department of Computer Science and Engineering at East West University, Bangladesh.  
We would first like to thank God Almighty from the bottom of our hearts for all of His blessings. We also want to express our gratitude to [Supervisor Name], our supervisor, who provided us with this opportunity and introduced us to the field of AI-powered healthcare systems. Without them, this work would not have been feasible. Their inspirational words, perceptive advice, and unwavering support throughout our B.Sc. program were both greatly appreciated and indispensable.  
We would also like to acknowledge the patients and pharmacies in Bangladesh who allowed us to collect real prescription images for training the AI model. Their contribution was fundamental to the success of this project.  
Finally, we would like to thank all those who have shown their constant support and friendship in various ways, directly or indirectly related to our academic life. We will remember them in our hearts and hope to find a more appropriate place to acknowledge them in the future.

 

 

Acknowledgments                                                                                                              	iii

 

 

                                                                                                                      	Sadibul Islam Sadib

                                                                                                                      	Md Touhidul Islam Alif

                                                                                                                      	Md. Ashikul Islam

 

 

 

 

**Table of Contents**

 

**Table of Contents                                                                                                                                        	        	 i**

**List of Figures                                                                                                                                             	        	iii**

Declaration. 4

Letter of Acceptance. 5

Abstract 6

Acknowledgments. 7

Chapter 1. 10

1.1 Motivation. 11

1.2 Business Model Canvas. 12

1.3 Organization of this Capstone Report 13

Chapter 2. 14

2.1 Background. 14

2.2 Related Works. 15

Chapter 3. 17

Chapter 4. 20

Chapter 5. 21

5.1     	Use Case Diagram. 21

5.2     	Activity Diagram (Swimlane). 22

5.3     	Class Diagram. 23

5.4     	Component Diagram. 24

5.5     	Sequence Diagram. 25

5.6     	Data Flow Diagram. 26

5.7     	Deployment Diagram. 27

5.8     	User Interface. 28

Chapter 6. 29

6.1 Materials. 29

6.1.1 Data Collection. 29

6.1.2 Dataset Exploration. 30

6.1.3 Data Augmentation and Preprocessing. 31

6.2 Method. 32

6.2.1 Proposed Model (YOLOv8s). 32

6.2.2 Design/Framework (AI Pipeline). 34

6.2.3 Algorithm/Model Formulation. 35

Chapter 7. 37

7.1 Obtained Results. 37

7.2 In-depth Result Analysis. 40

7.3 Software Cost Analysis. 44

Chapter 8. 47

References. 49

Appendix. 51

 

 

 

 

 

**List of Figures**

 

 

 

 

**1.2   	Business Model Canvas                                                                                                    	12**

**5.1   	Use Case Diagram                                                                                                             	21**

**5.2   	Activity Diagram                                                                                                              	22**

**5.3   	Class Diagram                                                                                                                   	23**

**5.4   	Component Diagram                                                                                                        	24**

**5.5   	Sequence Diagram                                                                                                            	25**

**5.6   	Data Flow Diagram                                                                                                          	26**

**5.7   	Deployment Diagram                                                                                                       	27**

**5.8   	User Interface                                                                                                                    	28**

**6.1   	Sample Prescription Images from Dataset                                                                     	29**

**6.2   	Dataset Class Distribution (Bounding Box Counts)                                                      	30**

**6.3   	YOLO Training Loss Curves                                                                                            	37**

**6.4   	Box Precision Curve                                                                                                          	38**

**6.5   	Box Recall Curve                                                                                                               	38**

**6.6   	Box F1-Score Curve                                                                                                          	38**

**6.7   	Precision-Recall Curve                                                                                                      	39**

**6.8   	Confusion Matrix                                                                                                              	39**

**6.9   	Normalized Confusion Matrix                                                                                          	39**

**6.10  	Validation Ground Truth vs Predictions                                                                       	40**

**7.1   	Per-Class mAP@50 Chart                                                                                               	42**

**7.2   	Model Version Comparison (v1 → v4)                                                                           	43**

 

 

 

 

 

 

 

 

 

# **Chapter 1**

 

**Introduction**

 

In Bangladesh, where approximately 170 million people depend on a healthcare system with limited digital infrastructure, the prescription remains the primary medium of communication between physicians and patients. Over 90% of prescriptions in Bangladesh are handwritten, and studies have shown that a significant portion of these are partially or fully illegible to patients and pharmacists alike. This illegibility leads to medication errors, incorrect dosages, drug interactions, and delayed treatments — problems that are particularly acute in rural areas where patients may travel hours to reach a pharmacy and cannot easily return to clarify a prescription.

The World Health Organization (WHO) has identified medication errors as a leading cause of preventable harm in healthcare globally, estimating that medication-related errors cost approximately $42 billion USD annually worldwide. In Bangladesh, where healthcare literacy is lower and the doctor-to-patient ratio stands at approximately 1:1,581, the impact of illegible prescriptions is amplified. Patients often rely on the pharmacist's interpretation of what was written, which introduces a dangerous layer of guesswork into the medication process.

Despite rapid advancements in Optical Character Recognition (OCR) and object detection technologies, prescription digitization remains a challenging and relatively underexplored problem — particularly for the South Asian context where prescriptions follow non-standardised formats, contain mixed languages (English medical terms written in varied handwriting styles), and include complex multi-field layouts with medicines, dosages, schedules, durations, diagnoses, and doctor information all coexisting on a single page without clear delimiters.

MediScan addresses this challenge by developing an end-to-end AI-powered Android application that captures prescription images via a smartphone camera (or gallery selection), processes them through a multi-stage AI pipeline, and returns structured, editable medication data to the user. The system serves two user roles — patients and doctors — within a single application, enabling patients to digitize and manage their prescriptions while allowing doctors to view patient records and manage appointments.

This Capstone C report focuses on the mobile application development, cloud deployment, and the final results of the system. The AI model training (YOLOv8s, PaddleOCR, image quality classifier) was completed and reported in Capstone B. This phase covers the integration of the trained models into a production backend, the development of the Android application with 28 screens, and the deployment of the entire system.

 

 

 

## **1.1 Motivation**

 

The motivation behind MediScan arises from a deeply personal and widely shared frustration: the inability to read one's own prescription after leaving a doctor's office. In Bangladesh, it is common for patients to receive a handwritten prescription and then struggle to decipher it at the pharmacy — sometimes purchasing the wrong medicine as a result. This is not merely an inconvenience; it is a public health hazard.

Several factors motivated this project:

**1. Patient Safety:** Medication errors arising from illegible prescriptions can have severe consequences ranging from allergic reactions to organ damage. The WHO estimates that 1 in every 10 patients is harmed while receiving hospital care, with medication errors being a significant contributor.

**2. Digital Health Gap in Bangladesh:** While developed nations have largely transitioned to Electronic Health Records (EHR) and e-prescriptions, Bangladesh's healthcare system remains predominantly paper-based. MediScan provides a bridge technology that can digitize paper prescriptions without requiring hospitals to overhaul their existing systems.

**3. Smartphone Penetration:** Bangladesh has over 180 million mobile subscribers with rapidly growing smartphone adoption, making a mobile-first solution both practical and scalable.

**4. Advancing AI Capabilities:** Recent breakthroughs in object detection (YOLO series) and OCR (PaddleOCR, Tesseract) have made it feasible to build accurate field detection systems even with relatively small training datasets. The release of YOLOv8 by Ultralytics in 2023 provided a particularly efficient architecture for this task.

**5. Medicine Management:** Beyond digitization, there is a need for comprehensive medicine management — reminders for medication schedules, tracking of medical history, and a communication bridge between patients and their physicians.

> **📸 SCREENSHOT PLACEHOLDER — Figure 1.1:** *Place a screenshot of a typical illegible handwritten prescription from Bangladesh here to illustrate the problem.*

 

 

 

## **1.2 Business Model Canvas**

> **📸 SCREENSHOT PLACEHOLDER — Figure 1.2:** *Place the Business Model Canvas diagram here (created in diagram.md as Diagram #1).*

| Segment | Details |
|---------|---------|
| **Customer Segments** | Patients (all ages), Doctors/Clinics, Pharmacies |
| **Value Propositions** | AI prescription digitization, medicine reminders, appointment booking, patient-doctor connection |
| **Channels** | Google Play Store, direct APK distribution |
| **Customer Relationships** | Self-service via app, push notifications, appointment system |
| **Revenue Streams** | Freemium model, premium features (advanced analytics, unlimited scans), pharmacy partnerships |
| **Key Resources** | AI models (YOLO + PaddleOCR), Firebase infrastructure, Railway cloud server |
| **Key Activities** | AI model training, app development, data collection, cloud deployment |
| **Key Partnerships** | Pharmacies, hospitals/clinics, Firebase (Google), Railway (cloud) |
| **Cost Structure** | Cloud hosting (Railway ~$5-20/month), Firebase (free tier), Google Play ($25 one-time), development time |

 

 

## **1.3 Organization of this Capstone Report**

This report is organized into eight chapters. **Chapter 1** introduces the problem of illegible prescriptions in Bangladesh and presents the motivation for building MediScan. **Chapter 2** provides a literature review covering existing work in prescription digitization, object detection, and OCR. **Chapter 3** addresses the Programme Outcomes (POs) and maps them to the project's contributions. **Chapter 4** presents the research questions and objectives that guided the development. **Chapter 5** details the system design through UML diagrams including use case, activity, class, component, sequence, data flow, and deployment diagrams, along with the user interface. **Chapter 6** describes the materials and methods, covering data collection, dataset preparation, the YOLO model architecture, and the AI pipeline design. **Chapter 7** presents the results including model performance metrics, per-class analysis, and software cost estimation using COCOMO. **Chapter 8** concludes the report with a summary of achievements and future directions.

 

 

 

 

# **Chapter 2**

 

**Literature Review**

## **2.1 Background**

Prescription digitization lies at the intersection of Computer Vision, Optical Character Recognition (OCR), and Healthcare Informatics. The challenge is fundamentally different from standard document digitization because prescriptions exhibit enormous variability in layout, handwriting style, language usage, and content density. Unlike structured forms or typed documents, prescriptions have no universal template — each physician has their own writing style, abbreviation conventions, and layout preferences.

Traditional approaches to handling illegible prescriptions have relied on pharmacist expertise and, in some cases, callbacks to the prescribing physician. However, these approaches do not scale and introduce dangerous delays in medication access. The emergence of deep learning-based object detection models — particularly the YOLO (You Only Look Once) family — has opened new possibilities for automated field detection in unstructured documents. Simultaneously, advances in OCR engines, especially PaddleOCR by Baidu, have significantly improved text recognition accuracy even on degraded or handwritten text.

The specific challenge addressed by MediScan requires a two-stage approach: first, detecting and localizing individual fields on a prescription (medicine names, dosages, schedules, etc.), and second, reading the text within each detected field. This is more complex than standard OCR because the system must understand the spatial layout and semantic relationships between fields — for example, that a dosage written to the right of a medicine name belongs to that specific medicine.

 

## **2.2 Related Works**

Prescription digitization and medical document analysis have attracted growing research attention in recent years. Several approaches have been proposed that are relevant to MediScan's design:

**Object Detection for Document Analysis:** Redmon et al. \[1\] introduced the YOLO (You Only Look Once) architecture, which revolutionized real-time object detection by framing detection as a single regression problem. Subsequent versions — YOLOv5, YOLOv7, and YOLOv8 \[2\] — progressively improved accuracy and inference speed. Jocher et al. \[2\] developed YOLOv8 with Ultralytics, introducing anchor-free detection heads and mosaic augmentation that proved particularly effective for small object detection — a critical requirement for prescription fields.

**OCR for Medical Documents:** PaddleOCR \[3\], developed by Baidu, provides a lightweight yet accurate OCR pipeline supporting 80+ languages. Its DB (Differentiable Binarization) text detection combined with CRNN-based recognition has shown state-of-the-art performance on scene text benchmarks. Li et al. \[4\] demonstrated that PaddleOCR achieves competitive accuracy even on degraded and handwritten text when combined with appropriate preprocessing.

**Prescription and Handwriting Recognition:** Patel and Patel \[5\] developed a CNN-based system for recognizing handwritten medical prescriptions, achieving 87% character-level accuracy on an English prescription dataset. Their work highlighted the difficulty of segmenting individual characters in cursive medical handwriting, motivating the whole-word recognition approach used in PaddleOCR.

**Medical Document Layout Analysis:** Xu et al. \[6\] proposed LayoutLM, a pre-trained model for document understanding that jointly models text and layout information. While powerful, LayoutLM requires significant computational resources and fine-tuning data, making it less practical for mobile deployment compared to the YOLO + OCR pipeline approach.

**Spatial Grouping in Document Analysis:** Yang et al. \[7\] introduced methods for spatial relationship extraction in forms and receipts, where fields must be associated based on proximity and alignment. MediScan adapts this approach specifically for prescriptions, using Y-coordinate proximity to group detected fields into medication rows — a medicine name, its dose, schedule, and duration are typically written on the same horizontal line.

**Mobile Health Applications:** Istepanian et al. \[8\] surveyed m-Health applications in developing countries, noting that smartphone-based health solutions can bypass traditional healthcare infrastructure limitations. Their findings support MediScan's mobile-first approach for the Bangladeshi market where smartphone penetration far exceeds access to digital health systems.

**Image Quality Assessment:** Mittal et al. \[9\] developed BRISQUE (Blind/Referenceless Image Spatial Quality Evaluator) for no-reference image quality assessment. MediScan adapts this concept using a trained ResNet18 classifier combined with Laplacian variance for blur detection, though the classifier was ultimately disabled due to insufficient training data.

 

 

 

# **Chapter 3**

 

                     	      	**Addressing the Program Outcomes (PO's)**

Our Capstone Project's primary research questions are provided below.

**RQ1.** How to apply and integrate new and previously learned mathematics, physics, and engineering knowledge to handle the Capstone Project's challenges? (PO1)

⮚ The MediScan project extensively applies mathematical foundations including: (a) **Convolutional Neural Networks (CNNs)** — which rely on matrix convolution operations, gradient descent optimisation, and backpropagation; (b) **YOLOv8 detection** — involving bounding box regression (IoU computation), non-maximum suppression, and the hinge loss function; (c) **Performance metrics** — including precision, recall, F1-score, and mean Average Precision (mAP), all of which are grounded in probability and statistics; and (d) **Image preprocessing** — using Laplacian operators for blur detection (variance computation) and ResNet18 architecture for quality classification.

 

 

**RQ2.** What relevant topics needed to be investigated, and how should the Capstone Project's issues and objectives be defined? (PO4)

To address the objective of building an AI-powered prescription digitization system, comprehensive investigation of several domains was necessary. This included studying the YOLO architecture evolution (v1 through v8), understanding PaddleOCR's detection-recognition pipeline, analysing prescription formats in Bangladesh, and surveying existing mobile health solutions. By reviewing literature on object detection for document analysis, OCR for degraded text, and spatial relationship extraction, the issues and objectives were clearly defined: build an end-to-end system that detects 12 prescription field classes with >95% accuracy and delivers structured data to a mobile application.

 

 

**RQ3.** How do you assess the numerous components of the Capstone Project objectives in order to build an efficient solution? (PO2)

The components were assessed through rigorous quantitative evaluation. The YOLO detection model was evaluated using standard COCO metrics: mAP@50 (98.1%), mAP@50-95 (86.5%), Precision (97.1%), Recall (95.0%), and F1-Score (96.0%). The OCR pipeline was assessed through a 3-attempt strategy (raw, preprocessed, enhanced) to maximise extraction accuracy. The mobile application was assessed through functional testing across 28 screens, user role verification (patient vs doctor), and end-to-end pipeline testing from image capture to data display.

**RQ4.** How to design and build capstone project solutions that address public health and safety, cultural, societal, and environmental concerns? (PO3)

MediScan directly addresses public health concerns by reducing medication errors caused by illegible prescriptions. The system is designed with cultural sensitivity for the Bangladeshi context — supporting English medical terminology as used in local prescriptions. Patient data privacy is maintained through Firebase Authentication with encrypted storage. The application requires no harmful materials or components, and as a software-based solution, has minimal environmental impact. The system is designed to be accessible to users of all ages and backgrounds.

**RQ5.** Which modern engineering and IT technologies are necessary, and how should they be used to design and develop the Capstone Project solution? (PO5)

⮚ The project utilises a comprehensive technology stack:

 ⮚  **Android Development:** Kotlin, Jetpack Compose (Material 3), CameraX, Hilt (Dagger) dependency injection

 ⮚  **AI/ML:** Python, PyTorch, Ultralytics YOLOv8, PaddleOCR, OpenCV, ResNet18

 ⮚  **Backend:** FastAPI (Python), Uvicorn ASGI server, Railway cloud deployment

 ⮚  **Cloud Services:** Firebase Authentication, Firestore NoSQL database, Firebase Storage, Railway PaaS

 ⮚  **Development Tools:** Android Studio, VS Code, Git/GitHub, Docker

 

 

**RQ6.** How should the societal, health, safety, legal, and cultural components of the capstone project be evaluated and resolved? (PO6)

The project was evaluated against societal impact criteria throughout development. All prescription images used for training were collected with consent. The system does not store prescription images permanently on any server — they are processed and discarded. Patient medical data is stored in Firebase with authentication-based access control. The application promotes health safety by reducing the probability of dispensing errors caused by illegible handwriting.

 

 

**RQ7.** How to assess and deliver sustainability impact of the capstone project in societal and environmental perspectives? (PO7)

MediScan contributes to sustainability by: (a) reducing paper wastage through digital prescription storage; (b) minimising unnecessary pharmacy visits when prescriptions are misread; (c) using efficient AI models (YOLOv8s with 11.1M parameters) that require minimal GPU resources (2.62 GB during inference); and (d) deploying on Railway Cloud which uses shared infrastructure, reducing the per-application carbon footprint compared to dedicated servers.

 

**RQ8.** Which professional and engineering professional standards and practices should be observed during the capstone project's implementation? (PO8)

The project follows industry-standard software engineering practices: MVVM architecture pattern, Clean Architecture principles, version control with Git/GitHub, code documentation, modular design with Hilt dependency injection, and CI/CD-ready deployment with Docker containerization. All code is structured for testability and maintainability.

 

**RQ9.** Which practices should be followed in order to function well as an individual and a team member in order to achieve the Capstone Project's objectives? (PO9)

This capstone project was developed as a collaborative team of three members: Sadibul Islam Sadib (lead developer — AI pipeline, Android application, backend deployment), Md Touhidul Islam Alif, and Md. Ashikul Islam. The team divided responsibilities across the multiple domains of the project — AI model training, backend development, mobile application development, cloud deployment, and documentation. Regular team communication, task tracking, and code integration were essential to maintaining project coherence. Communication with the supervisor for feedback and guidance was maintained throughout the process.

 

**RQ10.** Which procedures should be followed in order to provide optimal deliverables? (PO10)

The project employed an iterative development methodology: the AI pipeline was developed first (Capstone B), followed by backend deployment and mobile application development (Capstone C). Each component was tested independently before integration. Version control with Git tracked all changes, and documentation was maintained throughout to ensure reproducibility.

 

**RQ11.** How to apply software engineering concepts and techniques to the Capstone Project's development life cycle and conduct economic analysis and cost estimation in the event of a real-world deployment of the Capstone Project's solution? (PO11)

Software engineering principles were applied throughout: requirements analysis, system design (UML diagrams), implementation with MVVM architecture, testing, and deployment. COCOMO-based cost estimation was performed for real-world deployment scenarios, detailed in Chapter 7.3.

 

 

**RQ12.** Which independent and life-long learning skills are gained during the Capstone Project design and development process? (PO12)

Through this capstone project, I gained extensive independent learning skills including: training object detection models with custom datasets, deploying Python backends on cloud platforms, building production Android applications with Jetpack Compose, managing Firebase services at scale, debugging complex multi-system pipelines (Android ↔ FastAPI ↔ Firebase), and containerizing applications with Docker. These skills are directly transferable to industry roles in mobile development, AI engineering, and cloud computing.

 

**The present phase of the Capstone Project (Capstone C) is primarily concerned with the mobile application development, cloud deployment, and final system integration.**

 

 

 

# **Chapter 4**

 

**Research Questions & Objectives**

**Research Questions:**

●       How can AI-based object detection accurately identify and extract individual fields from handwritten prescription images?  
●       How can spatial grouping algorithms correctly associate detected fields (medicine, dose, schedule, duration) into coherent medication entries?  
●       How can a mobile application be designed to provide real-time prescription digitization with a seamless user experience?  
●       What is the achievable accuracy for prescription field detection using YOLOv8 with limited training data?  
●       How can a cloud-deployed AI backend serve prescription extraction requests reliably for a mobile application?

The main goal of this research is to create an end-to-end AI-powered system that can digitize handwritten and printed prescriptions into structured, editable medication data accessible through a mobile application. The system must: (1) detect 12 distinct field classes on prescription images with >95% mAP accuracy; (2) extract text from detected fields using OCR; (3) group extracted fields into coherent medication entries using spatial analysis; (4) deliver the results to an Android application in real-time; and (5) provide additional healthcare features including appointment booking, medicine reminders, and patient-doctor communication.

The system targets two user roles: **Patients** — who scan prescriptions, manage their medication history, set reminders, and book appointments; and **Doctors** — who view patient records, manage appointments, and access prescription histories. By digitizing prescriptions at the point of receipt, MediScan aims to eliminate the dangerous gap between what the doctor writes and what the patient (or pharmacist) reads.

 

 

 

# **Chapter 5**

 

      	                       	                                                      **System Design**

## **5.1   Use Case Diagram**

The MediScan system involves three primary actors: Patient, Doctor, and the AI System. The Patient can register/login, capture prescription images, view digitized prescriptions, edit extracted data, book appointments with doctors, set medicine reminders, manage their profile, and view nearby hospitals. The Doctor can register/login, view their patient list, access patient prescription records, manage appointments (accept/reject/cancel), write orders, and manage their profile. The AI System processes prescription images through quality assessment, field detection, and OCR extraction.

> **📸 SCREENSHOT PLACEHOLDER — Figure 5.1:** *Place the Use Case Diagram here (created in diagram.md as Diagram #2).*

## **5.2    Activity Diagram (Swimlane)**

The activity flow begins when a Patient opens the application and authenticates via Firebase Auth. Upon successful login, the patient navigates to the scan screen, selects a prescription image (via camera or gallery), and submits it to the AI backend. The system performs quality assessment, YOLO detection, and PaddleOCR extraction in sequence. If extraction succeeds, the results are displayed in a bottom sheet for review and editing. The patient confirms the data, which is then saved to Firebase Firestore. In parallel, the Doctor swimlane shows the process of viewing patient lists, accessing records, and managing appointments with accept/reject/cancel decisions that trigger notification creation.

> **📸 SCREENSHOT PLACEHOLDER — Figure 5.2:** *Place the Activity Diagram (Swimlane) here (created in diagram.md as Diagram #3).*

 

##  **5.3  Class Diagram**

The class diagram for MediScan consists of 8 primary data classes and 6 repository classes following Clean Architecture. The core data classes are: **User** (uid, name, email, role, phone, specialization, hospitalName, degree, experience), **Prescription** (prescriptionId, userId, imageUrl, medications, doctorName, hospital, diagnosis, date), **Medication** (name, doseStrength, dosageSchedule, duration), **Appointment** (appointmentId, patientId, doctorId, date, time, status, type), **Notification** (notificationId, userId, title, message, isRead, createdAt), **Reminder** (reminderId, userId, medicineName, times, startDate, isActive), **ExtractionResult** (status, medications, doctorInfo, rawDetections), and **DoctorOrder** (orderId, doctorId, patientId, medications, notes, date). Each data class maps to a Firestore collection, and each repository provides CRUD operations.

> **📸 SCREENSHOT PLACEHOLDER — Figure 5.3:** *Place the Class Diagram here (created in diagram.md as Diagram #4).*

 

 

 

## **5.4   Component Diagram**

The MediScan system comprises four major components: (1) **Android Application** — containing the UI layer (Jetpack Compose screens), ViewModel layer (8 ViewModels), and Repository layer (6 repositories); (2) **Firebase Services** — providing Authentication, Firestore database, and Cloud Storage; (3) **FastAPI Backend** — housing the AI pipeline with quality checker, YOLO detector, PaddleOCR engine, and spatial grouping module; and (4) **Railway Cloud** — providing the hosting infrastructure with Docker containerization. The Android app communicates with Firebase via the Firebase SDK and with the FastAPI backend via Retrofit2 over HTTPS.

> **📸 SCREENSHOT PLACEHOLDER — Figure 5.4:** *Place the Component Diagram here (created in diagram.md as Diagram #5).*

 

 

##  

## **5.5   Sequence Diagram**

The sequence diagram illustrates the prescription extraction workflow. The Patient initiates by capturing an image, which is sent from ScanScreen to ScanViewModel. The ViewModel calls PrescriptionRepository, which uploads the image to Firebase Storage and obtains a URL. The image is then sent via Retrofit2 to the FastAPI backend. On the server side, the pipeline executes sequentially: QualityChecker assesses image quality, YOLODetector performs field detection, PaddleOCR extracts text from each detected region, and SpatialGrouper associates fields into medication entries. The structured JSON response flows back through the chain: FastAPI → Retrofit → Repository → ViewModel → UI (ExtractionResultSheet). The Patient reviews, edits if necessary, and confirms — triggering a Firestore save.

> **📸 SCREENSHOT PLACEHOLDER — Figure 5.5:** *Place the Sequence Diagram here (created in diagram.md as Diagram #6).*

 

 

## **5.6   Data Flow Diagram**

The data flow in MediScan involves three main processes and multiple data stores. **Process 1 (Prescription Extraction):** Patient uploads image → AI pipeline processes → structured data returned → saved to Firestore prescriptions collection. **Process 2 (Appointment Management):** Patient selects doctor → creates appointment → notification created for doctor → doctor accepts/rejects → status update triggers patient notification. **Process 3 (Medicine Reminders):** Patient creates reminder → alarm scheduled via AlarmManager → BroadcastReceiver triggers at scheduled time → notification displayed. Data stores include: Firestore (users, prescriptions, appointments, notifications, reminders collections), Firebase Storage (prescription images), and SharedPreferences (user session).

> **📸 SCREENSHOT PLACEHOLDER — Figure 5.6:** *Place the Data Flow Diagram here (created in diagram.md as Diagram #7).*

 

 

## **5.7   Deployment Diagram**

The deployment architecture consists of three nodes: (1) **Android Device** — running the MediScan APK with CameraX for image capture, local SharedPreferences for session management, and the Firebase SDK for cloud communication; (2) **Firebase Cloud (Google)** — hosting Authentication services, Firestore NoSQL database (6 collections), and Cloud Storage (prescription images); (3) **Railway Cloud** — hosting a Docker container running the FastAPI application with Uvicorn ASGI server, YOLO model weights, and PaddleOCR engine. Communication between the Android device and Firebase uses the Firebase SDK (HTTPS/WebSocket). Communication between the Android device and Railway uses Retrofit2 over HTTPS with multipart file upload.

> **📸 SCREENSHOT PLACEHOLDER — Figure 5.7:** *Place the Deployment Diagram here (created in diagram.md as Diagram #8).*

 

 

##  **5.8  User Interface**

The MediScan application features 28 screens organised across two user roles. Key interfaces include:

**Patient Screens:** Login/Signup, Home Dashboard (with quick actions for scan, appointments, reminders), Scan Screen (camera + gallery), Extraction Results Bottom Sheet, Prescription List, Prescription Detail, Book Appointment (doctor search + ModalBottomSheet booking), Appointments List, Medicine Reminders, Buy Medicine, Nearby Hospitals (Google Maps), Profile, and Notifications.

**Doctor Screens:** Doctor Home Dashboard, Patient List with Search, Patient Detail Bottom Sheet, Patient Prescription Records, Write Order, Doctor Appointments (with accept/reject/cancel actions), Doctor Profile, and Notifications.

All screens follow a consistent Material 3 design language with gradient headers (blue-to-purple), white cards with elevation, and a navigation bar with icons.

> **📸 SCREENSHOT PLACEHOLDER — Figure 5.8:** *Place 4-6 key screenshots of the app here — showing Patient Home, Scan Result, Doctor Dashboard, and Appointment screens (created in diagram.md as Diagram #9).*

 

 

 

# **Chapter 6**

 

**Materials and Method**

## **6.1 Materials:**

### **6.1.1 Data Collection:**

 

The dataset for training the YOLOv8 detection model was collected entirely from real-world prescription images in Bangladesh. A total of **1,464 prescription images** were collected from patients, pharmacies, and clinics across Dhaka. The prescriptions include both handwritten and printed varieties from different physicians with varying handwriting styles, layouts, and content complexity.

The collection process involved:
- Photographing prescriptions using smartphone cameras at various angles and lighting conditions
- Ensuring diversity in prescription formats (different clinics, hospitals, and private chambers)
- Obtaining verbal consent from patients for research use
- Capturing prescriptions in both portrait and landscape orientations

Each prescription was manually annotated with bounding boxes for 12 field classes using a labelling tool. The annotations were saved in YOLO format (class_id, x_center, y_center, width, height — normalised to image dimensions).

> **📸 SCREENSHOT PLACEHOLDER — Figure 6.1:** *Place 3-4 sample prescription images from the dataset here to show variety (handwritten, printed, different layouts).*

 

### **6.1.2 Dataset Exploration:**

 

The annotated dataset consists of 12 field classes, each representing a distinct region on a prescription:

| Class ID | Class Name | Total Bounding Boxes | Description |
|----------|-----------|---------------------|-------------|
| 0 | MEDICINE | 789 | Medicine/drug names |
| 1 | DOSE_STRENGTH | 548 | Dosage amounts (e.g., 500mg) |
| 2 | DOSAGE_SCHEDULE | 636 | Frequency (e.g., 1+0+1) |
| 3 | DURATION | 585 | Duration (e.g., 7 days) |
| 4 | DOCTOR_NAME | 93 | Prescribing physician name |
| 5 | DEGREE | 9 | Doctor's degree (e.g., MBBS) |
| 6 | HOSPITAL | 44 | Hospital/clinic name |
| 7 | PATIENT_NAME | 100 | Patient name |
| 8 | AGE | 83 | Patient age |
| 9 | DATE | 101 | Prescription date |
| 10 | TEST | 102 | Recommended tests |
| 11 | DIAGNOSIS | 38 | Diagnosis/disease |
| **Total** | | **3,128** | |

The dataset exhibits significant class imbalance — MEDICINE (789 instances) has over 87× more samples than DEGREE (9 instances). This imbalance was addressed through augmentation and class-weighted loss during training.

> **📸 SCREENSHOT PLACEHOLDER — Figure 6.2:** *Place the labels distribution chart here — use `Report /yolo_prescription_v4/labels.jpg`*

 

###  

### **6.1.3 Data Augmentation and Preprocessing:**

 

To increase the effective training set size and improve model generalization, a comprehensive augmentation pipeline was applied:

**Augmentation Techniques:**
- **Mosaic Augmentation (100%):** Combines 4 training images into a single composite, forcing the model to detect fields at different scales and positions
- **Random Augmentation (randaugment):** Applies random colour, contrast, and brightness transformations
- **Random Erasing (40%):** Randomly erases portions of the image, forcing the model to learn from partial views
- **HSV Augmentation:** Hue (±0.015), Saturation (±0.7), Value (±0.4) — simulates different lighting conditions
- **Scale (±0.3):** Random scaling to handle prescriptions photographed at different distances
- **Translation (±0.1):** Random spatial translation for position invariance
- **Noise Injection:** Custom noise augmentation applied to simulate camera noise in low-light conditions

**Dataset Split (after augmentation):**

| Split | Images | Percentage |
|-------|--------|------------|
| Training | 1,444 | 80% |
| Validation | 180 | 10% |
| Test | 182 | 10% |
| **Total** | **1,806** | **100%** |

The total bounding box count across the augmented dataset is **31,788** — approximately 10× the original annotations.

**Image Quality Classifier (Disabled):**

An image quality classifier was initially developed using ResNet18 to filter out poor-quality prescription images before processing. The classifier was trained on the 1,464 collected images, split into "good" and "bad" quality categories. However, due to the limited dataset size, the classifier exhibited unacceptable false-positive rates — it would frequently reject good-quality images captured by mobile phones, declining even clear, well-lit prescriptions. Since the classifier was degrading user experience by blocking legitimate scans rather than improving accuracy, the feature was **disabled in the production system**. The quality assessment now relies solely on the Laplacian variance blur detection threshold, which is more reliable as a rule-based approach.

 

## **6.2 Method:**

 

### **6.2.1 Proposed Model (YOLOv8s):**

MediScan employs **YOLOv8s** (small variant, 11.1M parameters) as the core detection model. YOLOv8, released by Ultralytics in January 2023, is an anchor-free, single-stage object detection model that predicts bounding boxes and class probabilities directly from full images in a single forward pass.

**Architecture Overview:**

The YOLOv8s architecture consists of three main components:

**Backbone (CSPDarknet53):**
The backbone extracts multi-scale feature maps from the input image using Cross-Stage Partial (CSP) connections. The input image (640×640×3) is processed through a series of convolutional layers with batch normalisation and SiLU activation:

$$
\text{Conv}(x) = \text{SiLU}(\text{BN}(\text{Conv2D}(x, W)))
$$

Where SiLU (Sigmoid Linear Unit) is defined as:

$$
\text{SiLU}(x) = x \cdot \sigma(x) = \frac{x}{1 + e^{-x}}
$$

**Neck (PANet — Path Aggregation Network):**
The neck fuses features from different backbone levels using top-down and bottom-up pathways, producing multi-scale feature maps (P3, P4, P5) that enable detection of fields at various sizes — from small text labels (AGE, DATE) to large regions (HOSPITAL, DIAGNOSIS).

**Head (Decoupled Head):**
Unlike earlier YOLO versions, YOLOv8 uses decoupled heads for classification and regression, processing them independently:

$$
\hat{y}_{cls} = \sigma(W_{cls} \cdot F + b_{cls})
$$
$$
\hat{y}_{box} = W_{box} \cdot F + b_{box}
$$

Where $F$ is the feature map, $\sigma$ is the sigmoid function, and $W_{cls}$, $W_{box}$ are the learned weights for classification and box regression respectively.

**Loss Function:**

YOLOv8 uses a composite loss function combining three components:

$$
\mathcal{L}_{total} = \lambda_{box} \cdot \mathcal{L}_{box} + \lambda_{cls} \cdot \mathcal{L}_{cls} + \lambda_{dfl} \cdot \mathcal{L}_{dfl}
$$

Where:
- $\mathcal{L}_{box}$ = CIoU (Complete Intersection over Union) loss for bounding box regression, weighted by $\lambda_{box} = 5.0$
- $\mathcal{L}_{cls}$ = Binary Cross-Entropy loss for classification, weighted by $\lambda_{cls} = 2.0$
- $\mathcal{L}_{dfl}$ = Distribution Focal Loss for fine-grained box regression, weighted by $\lambda_{dfl} = 1.5$

**CIoU Loss** is defined as:

$$
\mathcal{L}_{CIoU} = 1 - IoU + \frac{\rho^2(b, b^{gt})}{c^2} + \alpha v
$$

Where $IoU$ is the Intersection over Union, $\rho$ is the Euclidean distance between centres of predicted and ground truth boxes, $c$ is the diagonal length of the smallest enclosing box, $\alpha$ is a positive trade-off parameter, and $v$ measures the consistency of aspect ratio:

$$
v = \frac{4}{\pi^2}\left(\arctan\frac{w^{gt}}{h^{gt}} - \arctan\frac{w}{h}\right)^2
$$

**Training Configuration:**

| Parameter | Value |
|-----------|-------|
| Architecture | YOLOv8s (11.1M parameters) |
| Input Size | 640 × 640 pixels |
| Batch Size | 6 (optimised for 6GB VRAM) |
| Epochs | 150 (all completed) |
| Optimiser | AdamW |
| Initial Learning Rate ($lr_0$) | 0.001 |
| Final Learning Rate ($lr_f$) | 0.01 × $lr_0$ |
| Momentum | 0.937 |
| Weight Decay | 0.0005 |
| Warmup Epochs | 5 |
| Patience (Early Stopping) | 30 epochs |
| Cache | Disk-based |
| GPU Memory Used | 2.62 GB |
| Training Time | 3.178 hours |

 

### **6.2.2 Design/Framework (AI Pipeline):**

The MediScan AI pipeline follows a sequential three-stage architecture:

**Stage 1 — Image Quality Assessment:**

Input Layer: Raw prescription image from mobile camera or gallery.

Processing Layer: Laplacian variance computation for blur detection:

$$
\text{Blur Score} = \text{Var}(\nabla^2 I)
$$

Where $\nabla^2 I$ is the Laplacian of the grayscale image $I$. If the variance falls below the threshold (100), the image is flagged as blurry.

Output Layer: Binary decision — accept or reject the image.

*(Note: The ResNet18-based quality classifier was trained but disabled in production due to high false-positive rates with limited training data.)*

**Stage 2 — Field Detection (YOLOv8s):**

Input Layer: Accepted prescription image, resized to 640×640.

Processing Layer: YOLOv8s forward pass with confidence threshold 0.25 and IoU threshold 0.7 for Non-Maximum Suppression (NMS).

Output Layer: List of detected bounding boxes with class labels and confidence scores.

**Stage 3 — Text Extraction (PaddleOCR) and Spatial Grouping:**

Input Layer: Cropped regions for each detected bounding box.

Processing Layer: 3-attempt OCR strategy — (a) raw crop, (b) preprocessed (grayscale + contrast), (c) enhanced (adaptive threshold + denoising). Each attempt uses PaddleOCR's English recognition model.

Grouping Layer: Spatial medication grouping algorithm that associates fields by Y-coordinate proximity — if two detected fields have vertical centres within a threshold distance, they are grouped as belonging to the same medication entry.

Output Layer: Structured JSON with medications array, doctor info, and metadata.

 

### **6.2.3 Algorithm/Model Formulation**

**BEGIN**

**Algorithm: MediScan Prescription Extraction Pipeline**

1. Input: Prescription image $I$ (from camera or gallery)

2. Output: Structured JSON containing medications, doctor info, diagnosis, tests

3. **// Stage 1: Quality Assessment**
   a. Convert $I$ to grayscale: $I_g = \text{cvtColor}(I, \text{GRAY})$
   b. Compute Laplacian variance: $v = \text{Var}(\nabla^2 I_g)$
   c. If $v < 100$: Return error ("Image too blurry")

4. **// Stage 2: Field Detection**
   a. Resize: $I_r = \text{resize}(I, 640 \times 640)$
   b. Run YOLO inference: $\text{detections} = \text{YOLOv8s}(I_r, \text{conf}=0.25)$
   c. Apply NMS with $\text{IoU}_{threshold} = 0.7$
   d. For each detection $d_i$: extract class $c_i$, confidence $p_i$, bounding box $[x_1, y_1, x_2, y_2]$

5. **// Stage 3: OCR Extraction**
   a. For each detection $d_i$:
      i. Crop region: $R_i = I[y_1:y_2, x_1:x_2]$
      ii. Attempt 1: $\text{text}_i = \text{PaddleOCR}(R_i)$
      iii. If empty → Attempt 2: $\text{text}_i = \text{PaddleOCR}(\text{preprocess}(R_i))$
      iv. If empty → Attempt 3: $\text{text}_i = \text{PaddleOCR}(\text{enhance}(R_i))$

6. **// Stage 4: Spatial Grouping**
   a. Sort medicine detections by Y-coordinate
   b. For each medicine $m_j$:
      i. Find DOSE_STRENGTH, DOSAGE_SCHEDULE, DURATION within Y-proximity threshold
      ii. Group as: $\text{med}_j = \{m_j, \text{dose}_j, \text{schedule}_j, \text{duration}_j\}$

7. **// Stage 5: Post-processing**
   a. Match medicine names against 48,014-entry master medicine list
   b. Format output as structured JSON

8. Return: JSON response with medications array, doctor info, metadata

**END**

 

 

# **Chapter 7**

 

**Results and Discussions**

## **7.1 Obtained Results**

 

The YOLOv8s model was trained for 150 epochs on the augmented prescription dataset (1,806 images, 31,788 bounding boxes) and evaluated on the held-out test set (182 images). The model demonstrated excellent performance across all 12 field classes, achieving production-ready accuracy.

**Overall Performance Metrics:**

| Metric | Value |
|--------|-------|
| **mAP@50** | 98.1% |
| **mAP@50-95** | 86.5% |
| **Precision** | 97.1% |
| **Recall** | 95.0% |
| **F1-Score** | 96.0% |

**Training Progression:**

The model showed rapid convergence during training. The box loss decreased from 1.279 (epoch 1) to 0.324 (epoch 150), the classification loss decreased from 9.096 to 1.123, and the DFL loss decreased from 1.520 to 0.844. Validation metrics stabilised around epoch 100, with marginal improvements through epoch 150.

> **📸 SCREENSHOT PLACEHOLDER — Figure 6.3:** *Place the training results chart here — use `Report /yolo_prescription_v4/results.png`*

> **📸 SCREENSHOT PLACEHOLDER — Figure 6.4:** *Place the Box Precision curve here — use `Report /yolo_prescription_v4/BoxP_curve.png`*

> **📸 SCREENSHOT PLACEHOLDER — Figure 6.5:** *Place the Box Recall curve here — use `Report /yolo_prescription_v4/BoxR_curve.png`*

> **📸 SCREENSHOT PLACEHOLDER — Figure 6.6:** *Place the Box F1-Score curve here — use `Report /yolo_prescription_v4/BoxF1_curve.png`*

> **📸 SCREENSHOT PLACEHOLDER — Figure 6.7:** *Place the Precision-Recall curve here — use `Report /yolo_prescription_v4/BoxPR_curve.png`*

**Confusion Matrix Analysis:**

The confusion matrix reveals that the model has extremely low inter-class confusion. The dominant diagonal indicates correct classification for the vast majority of detections. The most common misclassification occurs between DOSE_STRENGTH and DOSAGE_SCHEDULE, which is expected as both contain numerical values adjacent to medicine names.

> **📸 SCREENSHOT PLACEHOLDER — Figure 6.8:** *Place the confusion matrix here — use `Report /yolo_prescription_v4/confusion_matrix.png`*

> **📸 SCREENSHOT PLACEHOLDER — Figure 6.9:** *Place the normalised confusion matrix here — use `Report /yolo_prescription_v4/confusion_matrix_normalized.png`*

**Validation Predictions vs Ground Truth:**

Visual comparison of model predictions against ground truth labels on validation batches demonstrates accurate localisation and classification of all field types. The model correctly identifies medicine names, dosages, schedules, and durations even in densely written prescriptions.

> **📸 SCREENSHOT PLACEHOLDER — Figure 6.10a:** *Place validation ground truth here — use `Report /yolo_prescription_v4/val_batch0_labels.jpg`*

> **📸 SCREENSHOT PLACEHOLDER — Figure 6.10b:** *Place validation predictions here — use `Report /yolo_prescription_v4/val_batch0_pred.jpg`*

 

 

## **7.2 In-depth Result Analysis**

 

**Per-Class Performance:**

| Class | mAP@50 | mAP@50-95 | Precision | Recall | F1-Score | Instances |
|-------|--------|-----------|-----------|--------|----------|-----------|
| MEDICINE | 99.2% | 87.5% | 95.7% | 98.6% | 97.1% | 789 |
| DOSE_STRENGTH | 98.8% | 85.3% | 97.8% | 97.9% | 97.9% | 548 |
| DOSAGE_SCHEDULE | 98.4% | 84.2% | 92.2% | 97.1% | 94.6% | 636 |
| DURATION | 99.0% | 86.8% | 94.3% | 98.3% | 96.3% | 585 |
| DOCTOR_NAME | 98.5% | 83.1% | 93.2% | 95.7% | 94.4% | 93 |
| DEGREE | 90.8% | 75.2% | 100.0% | 73.4% | 84.7% | 9 |
| HOSPITAL | 99.5% | 89.1% | 98.4% | 100.0% | 99.2% | 44 |
| PATIENT_NAME | 98.4% | 84.7% | 98.3% | 97.0% | 97.6% | 100 |
| AGE | 97.7% | 82.9% | 98.7% | 92.4% | 95.4% | 83 |
| DATE | 99.1% | 87.2% | 97.5% | 96.0% | 96.7% | 101 |
| TEST | 98.2% | 84.6% | 100.0% | 93.2% | 96.5% | 102 |
| DIAGNOSIS | 99.5% | 88.4% | 99.4% | 100.0% | 99.7% | 38 |

**Key Observations:**

1. **Highest-performing classes:** DIAGNOSIS (99.5% mAP@50, 99.7% F1) and HOSPITAL (99.5% mAP@50, 99.2% F1) achieved near-perfect detection. These fields typically occupy distinct, isolated regions on prescriptions with consistent formatting.

2. **Lowest-performing class:** DEGREE (90.8% mAP@50, 84.7% F1) had the weakest performance due to having only 9 training instances. Despite this severe data scarcity, the model still achieved 100% precision — it never falsely detected a degree where none existed, but missed 26.6% of actual degree fields (73.4% recall).

3. **Core medication fields** (MEDICINE, DOSE_STRENGTH, DOSAGE_SCHEDULE, DURATION) all exceed 94% F1-Score, which is critical since these are the primary output of the system.

4. **Precision vs Recall trade-off:** The model generally favours higher precision over recall, meaning it produces very few false positives. This is desirable for a medical application where incorrect field detection is more harmful than missing a field.

> **📸 SCREENSHOT PLACEHOLDER — Figure 7.1:** *Create a bar chart showing per-class mAP@50 values (created in diagram.md as Diagram #10).*

**Understanding the Evaluation Metrics:**

**Precision** measures the accuracy of positive predictions — of all detections the model made, what fraction were correct:

$$
\text{Precision} = \frac{TP}{TP + FP}
$$

Where $TP$ = True Positives (correctly detected fields) and $FP$ = False Positives (incorrectly detected fields).

**Recall (Sensitivity)** measures the completeness of detection — of all actual fields present, what fraction were found:

$$
\text{Recall} = \frac{TP}{TP + FN}
$$

Where $FN$ = False Negatives (fields that exist but were missed by the model).

**F1-Score** is the harmonic mean of Precision and Recall, providing a single balanced metric:

$$
F1 = 2 \cdot \frac{\text{Precision} \cdot \text{Recall}}{\text{Precision} + \text{Recall}}
$$

The harmonic mean is preferred over the arithmetic mean because it penalises extreme imbalance between precision and recall. For example, a model with 100% precision but 1% recall would have an arithmetic mean of 50.5% but an F1-Score of only 1.98% — correctly reflecting its poor overall performance.

**Mean Average Precision (mAP)** is the primary metric for object detection:

$$
\text{AP} = \int_0^1 P(r) \, dr
$$

Where $P(r)$ is the precision at recall level $r$. The AP is computed for each class individually, and the mean across all classes gives mAP:

$$
\text{mAP} = \frac{1}{N} \sum_{i=1}^{N} AP_i
$$

**mAP@50** uses an IoU threshold of 0.50 to determine whether a detection matches a ground truth box:

$$
IoU = \frac{|B_{pred} \cap B_{gt}|}{|B_{pred} \cup B_{gt}|}
$$

**mAP@50-95** averages mAP across IoU thresholds from 0.50 to 0.95 (in steps of 0.05), providing a stricter evaluation of localisation accuracy.

**Model Evolution — v1 to v4:**

The final model (v4) represents a dramatic improvement over earlier training runs:

| Metric | v1 | v2 | v3 | v4 (Final) | Improvement |
|--------|-----|-----|-----|-----|-------------|
| mAP@50 | 52.4% | 52.3% | 52.8% | **98.1%** | +45.7% |
| mAP@50-95 | 26.9% | 24.6% | 26.1% | **86.5%** | +59.6% |
| Precision | 56.9% | 54.9% | 57.2% | **97.1%** | +40.2% |
| Recall | 53.1% | 53.9% | 53.3% | **95.0%** | +41.9% |
| F1-Score | 54.9% | 54.4% | 55.2% | **96.0%** | +41.1% |

The breakthrough from v3 to v4 was achieved through three key changes: (1) improved augmentation pipeline with noise injection to simulate real-world camera conditions; (2) increased class loss weight ($\lambda_{cls} = 2.0$) to force the model to better distinguish between similar field types; and (3) higher initial learning rate (0.001 vs 0.000625) for faster and more decisive convergence.

The most dramatic per-class improvements were in previously underperforming classes:
- HOSPITAL: 14.2% → 99.5% (+85.3%)
- DIAGNOSIS: 11.0% → 99.5% (+88.5%)
- TEST: 25.7% → 98.2% (+72.5%)

> **📸 SCREENSHOT PLACEHOLDER — Figure 7.2:** *Create a grouped bar chart comparing v1 vs v4 mAP@50 per class (created in diagram.md as Diagram #11).*

 

## **7.3 Software Cost Analysis**

To estimate the cost of deploying MediScan as a real-world product, we consider the following factors:

▪ **Project size:** The project spans AI model training (Python), backend development (FastAPI), and mobile application development (Kotlin/Android) — approximately 15,000+ lines of code across 65+ source files.

▪ **Project complexity:** Rated 4 out of 5 — the system involves multi-domain engineering (AI, mobile, cloud), real-time image processing, and complex spatial algorithms.

▪ **Development team experience:** Team of three developers with intermediate experience in AI/ML, Android development, and backend engineering.

**Direct Cost Analysis:**

(Following the salary structure of Bangladeshi software development companies)

The project required approximately 8 months of development across Capstone B and C.

Android Developer (x1): 40,000 BDT per month
AI/ML Engineer (x1): 45,000 BDT per month
Backend Developer (x1): 35,000 BDT per month

Total Monthly Salary Cost:

1 × 40,000 + 1 × 45,000 + 1 × 35,000 = 1,20,000

Total Salary Cost for 8 months:

8 × 1,20,000 = 9,60,000

Other Costs:

●   	Software/Tools Licenses (Android Studio — free, VS Code — free): 0
●   	Cloud Hosting (Railway): ~5,000/month × 8 = 40,000
●   	Firebase (free tier): 0
●   	Google Play Store Fee: 2,500 (one-time, ~$25)
●   	GPU for Training (Google Colab Pro): ~1,500/month × 3 = 4,500
●   	Miscellaneous Expenses: 10,000

Total Other Costs:

0 + 40,000 + 0 + 2,500 + 4,500 + 10,000 = 57,000

Total Estimated Budget:

9,60,000 + 57,000 = **10,17,000 BDT** (approximately $8,475 USD)

 

**COCOMO 2 Estimation:**

To validate our cost estimate using the COCOMO (Constructive Cost Model) methodology:

The COCOMO model estimates effort based on the size and complexity of the software:

$$
\text{Effort (Person-Months)} = a \times (\text{KLOC})^b
$$

Where:
- $a$ = coefficient based on project type (2.4 for semi-detached projects)
- $b$ = exponent based on project complexity (1.05 for semi-detached)
- KLOC = Kilo Lines of Code

For MediScan:
- Total source lines ≈ 15,000 → KLOC = 15
- Project type: Semi-detached (mix of experienced and novel aspects)

$$
\text{Effort} = 2.4 \times (15)^{1.05} = 2.4 \times 16.89 = 40.54 \text{ Person-Months}
$$

Development time estimation:

$$
\text{Time (Months)} = 2.5 \times (\text{Effort})^{0.38}
$$

$$
\text{Time} = 2.5 \times (40.54)^{0.38} = 2.5 \times 4.38 = 10.95 \text{ Months} \approx 11 \text{ Months}
$$

Average team size:

$$
\text{Team Size} = \frac{\text{Effort}}{\text{Time}} = \frac{40.54}{10.95} = 3.7 \approx 4 \text{ people}
$$

COCOMO Cost Estimation:

Using average salary of 40,000 BDT/month per developer:

$$
\text{Cost} = \text{Effort} \times \text{Salary} = 40.54 \times 40,000 = 16,21,600 \text{ BDT}
$$

Including other costs:

$$
\text{Total COCOMO Cost} = 16,21,600 + 57,000 = \textbf{16,78,600 BDT} \approx \$13,988 \text{ USD}
$$

The COCOMO estimate is higher than our direct estimate because it accounts for the full complexity of a semi-detached project, including rework, testing, and integration overhead. The actual development was performed by a single individual across 8 months, which kept direct costs lower.

 

 

 

# **Chapter 8**

 

**Conclusion**

 

MediScan demonstrates that AI-powered prescription digitization is both technically feasible and practically effective for the Bangladeshi healthcare context. The system successfully addresses the critical problem of illegible prescriptions by providing an end-to-end solution that spans from image capture to structured medication data.

The YOLOv8s detection model, trained on 1,464 real prescription images from Bangladesh and augmented to 1,806 samples, achieved remarkable performance: **98.1% mAP@50**, **97.1% Precision**, **95.0% Recall**, and **96.0% F1-Score** across all 12 field classes. These results significantly exceed the initial target of 95% mAP and demonstrate that accurate prescription field detection is achievable even with a relatively small, domain-specific dataset. The model's journey from 52.4% mAP (v1) to 98.1% mAP (v4) — a +45.7% improvement — highlights the importance of appropriate augmentation strategies and hyperparameter tuning over simply increasing dataset size.

The Android application, comprising 28 screens and 65+ source files, provides a comprehensive healthcare management platform beyond just prescription scanning. The integration of appointment booking, medicine reminders with local alarm scheduling, real-time notifications via Firebase, and role-based interfaces for both patients and doctors creates a cohesive ecosystem that addresses multiple pain points in the patient-doctor workflow.

However, the project also revealed important limitations. The image quality classifier trained on the 1,464-image dataset was insufficiently generalised — it exhibited high false-positive rates that degraded user experience, necessitating its disablement. This underscores that while object detection models can achieve high accuracy with augmented small datasets, classification tasks (binary quality assessment) may require significantly larger and more diverse training data to avoid over-fitting to specific image characteristics.

The deployment of the AI backend on Railway Cloud demonstrates the viability of serving a mobile AI application using affordable cloud infrastructure (under $20/month), making the solution economically accessible for the Bangladeshi market.

**Future Directions:**

1. **Bangla OCR Support:** Extending PaddleOCR to recognise Bangla script, which would cover the growing number of prescriptions written in Bangla.
2. **Larger Dataset for Quality Classifier:** Collecting 10,000+ images with diverse quality levels to re-enable the image quality filter.
3. **Offline Mode:** Packaging a lightweight YOLO model for on-device inference using ONNX runtime, enabling prescription scanning without internet connectivity.
4. **Pharmacy Integration:** Connecting with pharmacy inventory systems to verify medicine availability and enable direct ordering.
5. **Cross-Platform Expansion:** Developing an iOS version and a web portal for clinics and hospitals.
6. **Edge AI:** Deploying quantised models (INT8) on mobile devices for faster, privacy-preserving inference.

MediScan represents a meaningful step toward digitizing Bangladesh's paper-based healthcare system, demonstrating that modern deep learning techniques, when combined with thoughtful mobile application design and affordable cloud infrastructure, can create accessible healthcare technology solutions for developing nations.

 

 

 

 

# **References**

 

1. Redmon, J., Divvala, S., Girshick, R., & Farhadi, A. (2016). You Only Look Once: Unified, Real-Time Object Detection. *2016 IEEE Conference on Computer Vision and Pattern Recognition (CVPR)*, 779–788. https://doi.org/10.1109/CVPR.2016.91  
2. Jocher, G., Chaurasia, A., & Qiu, J. (2023). Ultralytics YOLOv8. *Ultralytics*. https://github.com/ultralytics/ultralytics  
3. Du, Y., Li, C., Guo, R., Yin, X., Liu, W., Zhou, J., Bai, Y., Yu, Z., Yang, Y., Dang, Q., & Wang, H. (2020). PP-OCR: A Practical Ultra Lightweight OCR System. *arXiv preprint arXiv:2009.09941*. https://arxiv.org/abs/2009.09941  
4. Li, C., Liu, W., Guo, R., Yin, X., Jiang, K., Du, Y., Du, Y., Zhu, L., Lai, B., Hu, X., Yu, D., & Wang, H. (2022). PP-OCRv3: More Attempts for the Improvement of Ultra Lightweight OCR System. *arXiv preprint arXiv:2206.03001*. https://arxiv.org/abs/2206.03001  
5. Patel, O. P., & Patel, A. (2021). Handwritten Medical Prescription Recognition using CNN. *International Journal of Engineering Research & Technology*, *10*(5), 445–451.  
6. Xu, Y., Li, M., Cui, L., Huang, S., Wei, F., & Zhou, M. (2020). LayoutLM: Pre-training of Text and Layout for Document Image Understanding. *Proceedings of the 26th ACM SIGKDD International Conference on Knowledge Discovery & Data Mining*, 1192–1200. https://doi.org/10.1145/3394486.3403172  
7. Yang, X., Yumer, E., Asente, P., Kraley, M., Kifer, D., & Giles, C. L. (2017). Learning to Extract Semantic Structure from Documents Using Multimodal Fully Convolutional Neural Networks. *2017 IEEE Conference on Computer Vision and Pattern Recognition (CVPR)*, 4342–4351. https://doi.org/10.1109/CVPR.2017.462  
8. Istepanian, R. S. H., Laxminarayan, S., & Pattichis, C. S. (2006). *M-Health: Emerging Mobile Health Systems*. Springer. https://doi.org/10.1007/b137697  
9. Mittal, A., Moorthy, A. K., & Bovik, A. C. (2012). No-Reference Image Quality Assessment in the Spatial Domain. *IEEE Transactions on Image Processing*, *21*(12), 4695–4708. https://doi.org/10.1109/TIP.2012.2214050  
10. He, K., Zhang, X., Ren, S., & Sun, J. (2016). Deep Residual Learning for Image Recognition. *2016 IEEE Conference on Computer Vision and Pattern Recognition (CVPR)*, 770–778. https://doi.org/10.1109/CVPR.2016.90  
11. Lin, T.-Y., Dollár, P., Girshick, R., He, K., Hariharan, B., & Belongie, S. (2017). Feature Pyramid Networks for Object Detection. *2017 IEEE Conference on Computer Vision and Pattern Recognition (CVPR)*, 936–944. https://doi.org/10.1109/CVPR.2017.106  
12. Zheng, Z., Wang, P., Liu, W., Li, J., Ye, R., & Ren, D. (2020). Distance-IoU Loss: Faster and Better Learning for Bounding Box Regression. *Proceedings of the AAAI Conference on Artificial Intelligence*, *34*(07), 12993–13000. https://doi.org/10.1609/aaai.v34i07.6999  
13. Liu, S., Qi, L., Qin, H., Shi, J., & Jia, J. (2018). Path Aggregation Network for Instance Segmentation. *2018 IEEE/CVF Conference on Computer Vision and Pattern Recognition*, 8759–8768. https://doi.org/10.1109/CVPR.2018.00913  
14. Boehm, B. W., Abts, C., Brown, A. W., Chulani, S., Clark, B. K., Horowitz, E., Madachy, R., Reifer, D. J., & Steece, B. (2009). *Software Cost Estimation with COCOMO II*. Prentice Hall.

 

 

 

# **Appendix**

 

 

| CO | Details | Knowledge Profile (K) | Engineering Problem (EP) |
| :---- | :---- | :---- | :---- |
| CO3 | MediScan processes prescription images using AI-based object detection and optical character recognition. The system detects 12 distinct field classes from prescription images and extracts structured text data. It employs YOLOv8s (deep learning), PaddleOCR (NLP/vision), and spatial grouping algorithms to digitize prescriptions. The Android application uses MVVM architecture with Jetpack Compose for the UI layer and Firebase for cloud storage and real-time data synchronisation. | **(i) Problem Analysis \[K1, K2, K3, K4\]   K1: Theory-based natural Sciences:** Gained knowledge about medical prescription formats, healthcare information systems, and the challenges of handwriting recognition in the medical domain.   **K2: Conceptually based mathematics, numerical analysis, statistics, and formal aspects of computer and information science:** Statistical metrics (precision, recall, F1, mAP) and numerical analysis (IoU computation, loss functions, gradient descent optimisation) have been extensively used.   **K3: Theory-based engineering fundamentals:** Deep learning (CNNs, YOLOv8), computer vision (object detection, OCR), and mobile engineering (Android architecture, Firebase integration) form the engineering foundation.   **K4: Forefront engineering specialist knowledge for practice:** Knowledge of state-of-the-art object detection (anchor-free YOLOv8), modern OCR (PaddleOCR), cloud deployment (Docker, Railway), and mobile development (Jetpack Compose, Hilt DI) has been applied. | **(i) Problem Analysis \[EP1, EP2, EP3, EP6, EP7\]   EP1: Depth of knowledge required:** Deep understanding of object detection architectures, OCR pipelines, spatial analysis algorithms, mobile application architecture (MVVM, Clean Architecture), and cloud deployment (Docker, Railway) was required.   **EP2: Range of conflicting requirements:** Balancing model accuracy vs inference speed, image quality filtering vs user experience (false rejections), cloud dependency vs offline capability, and feature richness vs app complexity.   **EP3: Depth of analysis required:** Extensive analysis of YOLO architectures, augmentation strategies (4 model versions tested), OCR preprocessing techniques (3-attempt strategy), and spatial grouping algorithms was performed to achieve 98.1% mAP accuracy.   **EP6: Extent of stakeholder involvement:** Patient needs (simple scanning, readable output), doctor needs (patient management, prescription viewing), and pharmacist needs (accurate medication data) were all considered.   **EP7: Interdependence:** The system integrates AI (Python), mobile (Kotlin), and cloud (Firebase + Railway) components that must work together seamlessly. |
| :---- | :---- | :---- | :---- |
| CO4 | MediScan is a healthcare-focused application that directly addresses patient safety by reducing medication errors. The project processes prescription images — no harmful materials are used. The system respects patient privacy through Firebase Authentication and does not permanently store images on processing servers. The application is accessible to users of all ages, genders, and backgrounds. No negative cultural, societal, or environmental impact exists. | **(i) Design and Implementation \[K5\] K5: Engineering design:** The system was designed following MVVM + Clean Architecture patterns. UML diagrams (use case, activity, class, component, sequence, data flow, deployment) were created. The AI pipeline was designed as a modular three-stage system (quality → detection → OCR) allowing independent testing and improvement of each stage. | **(i) Design and Implementation \[EP1, EP2, EP4, EP5, EP6, EP7\]   EP1: Depth of knowledge required:** Android architecture patterns, Firebase integration, REST API design, Docker containerisation, and AI pipeline engineering.   **EP2: Range of conflicting requirements:** Image quality classifier accuracy vs usability (ultimately disabled), real-time processing vs accuracy (3-attempt OCR), and cloud cost vs performance.   **EP4: Familiarity of issues:** Resolved novel challenges including Firestore Boolean field naming convention bugs (@field:JvmField), alarm scheduling across device reboots, and PaddleOCR version compatibility issues on different platforms.   **EP5: Extent of applicable codes:** Clean, documented Kotlin code following Android best practices. Python backend following PEP 8 standards.   **EP7: Interdependence:** Multi-platform system requiring Android ↔ Firebase ↔ FastAPI integration. |
| :---- | :---- | :---- | :---- |
| CO5 | MediScan uses modern tools and technologies: Kotlin with Jetpack Compose for Android development, Python with PyTorch and Ultralytics for AI model training, FastAPI for the REST backend, Firebase (Auth, Firestore, Storage) for cloud services, Docker for containerisation, and Railway for cloud deployment. Development tools include Android Studio, VS Code, and Git/GitHub. | **(i) Materials and Devices \[K6\]   K6: Engineering Practice (technology):** Practical knowledge of YOLOv8 training with Ultralytics, PaddleOCR configuration, FastAPI development, Firebase integration, Jetpack Compose UI development, Hilt dependency injection, and Docker deployment has been applied. | **(i) Materials and Devices \[EP1, EP2, EP4, EP5\]   EP1: Depth of knowledge required:** Knowledge of multiple frameworks (YOLO, PaddleOCR, Compose, Firebase, FastAPI) and their integration points.   **EP2: Range of conflicting requirements:** GPU limitations (6GB VRAM requiring batch size 6), Railway free tier limitations, Firebase free tier quotas.   **EP4: Familiarity of issues:** PaddleOCR Python import order bugs, Gradle dependency conflicts, Firestore serialisation issues — all resolved through systematic debugging.   **EP5: Extent of applicable codes:** All modern tools used under their respective licenses (Apache 2.0, MIT). Standard development practices followed throughout. |
| :---- | :---- | :---- | :---- |
| CO6 | MediScan has a positive societal impact by improving healthcare safety through prescription digitization. There is no gender discrimination, age limitation, or cultural restriction in the application. The system is entirely software-based with no hardware manufacturing or environmental pollutants. Patient data privacy is protected through encrypted storage and authentication. The application is free to use for basic features, ensuring accessibility regardless of economic background. | **(i) Social and Environmental Impact of Engineering \[K7\]   K7: Comprehension of engineering in society:** MediScan addresses a real public health challenge — medication errors from illegible prescriptions. The system contributes positively to society by improving healthcare accessibility. No illegal software is used; all frameworks are open-source or properly licensed. The project follows ethical guidelines for medical data handling. | **(i) Social and Environmental Impact of Engineering \[EP2, EP5, EP6\]   EP2: Range of conflicting requirements:** Tools and technologies selected considering data privacy, accessibility, cost (free tier services), and environmental impact (cloud efficiency).   **EP5: Extent of applicable codes:** All development follows standard ethical and professional practices. Medical data handling follows privacy-by-design principles.   **EP6: Extent of stakeholder involvement and conflicting requirements:** Patient accessibility, doctor workflow efficiency, and pharmacy accuracy requirements were all balanced in the system design. |

 

 

