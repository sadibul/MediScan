package com.mediscan.app.core.constants

/**
 * Predefined medical suggestions for autocomplete fields
 * in the Prescription Information sheet.
 *
 * Covers common diagnoses, medical tests, and dose schedule
 * patterns relevant to Bangladesh medical practice.
 */
object MedicalSuggestions {

    // ═══════════════════════════════════════════════════
    // Dose Schedule Combinations (Morning + Afternoon + Night)
    // Format: M+A+N where 1 = take, 0 = skip
    // ═══════════════════════════════════════════════════
    val doseSchedules = listOf(
        "1+1+1" to "Morning + Afternoon + Night",
        "1+0+1" to "Morning + Night",
        "1+1+0" to "Morning + Afternoon",
        "0+1+1" to "Afternoon + Night",
        "1+0+0" to "Morning only",
        "0+1+0" to "Afternoon only",
        "0+0+1" to "Night only",
        "1+1+1+1" to "Morning + Noon + Afternoon + Night",
        "1+0+0+1" to "Morning + Night (4-dose)",
        "½+0+½" to "Half Morning + Half Night",
        "½+½+½" to "Half × 3 daily",
        "2+2+2" to "2 tabs × 3 daily",
        "2+0+2" to "2 tabs Morning + 2 tabs Night",
        "1+0+0+0" to "Once daily (Morning)",
        "0+0+0+1" to "Once daily (Bedtime)",
    )

    // ═══════════════════════════════════════════════════
    // Comprehensive Diagnosis List (Bangladesh context)
    // Sorted alphabetically, covering general + tropical
    // ═══════════════════════════════════════════════════
    val diagnoses = listOf(
        // A
        "Abscess", "Acid Reflux", "Acne", "Acute Bronchitis", "Acute Gastritis",
        "Acute Kidney Injury", "Acute Otitis Media", "Acute Pharyngitis",
        "Acute Respiratory Infection", "Acute Sinusitis", "Adenoiditis",
        "Allergic Conjunctivitis", "Allergic Dermatitis", "Allergic Rhinitis",
        "Alopecia", "Amoebiasis", "Anaemia", "Anal Fissure", "Angina Pectoris",
        "Ankylosing Spondylitis", "Anxiety Disorder", "Appendicitis",
        "Arrhythmia", "Arsenic Poisoning", "Arthritis", "Asthma", "Atherosclerosis",
        "Atopic Dermatitis", "Atrial Fibrillation",
        // B
        "Back Pain", "Bacterial Vaginosis", "Bell's Palsy", "Benign Prostatic Hyperplasia",
        "Bipolar Disorder", "Bleeding Disorder", "Blepharitis", "Bone Fracture",
        "Brain Tumor", "Breast Cancer", "Bronchiectasis", "Bronchiolitis",
        "Bronchitis", "Brucellosis", "Burn Injury", "Bursitis",
        // C
        "Calculus of Kidney", "Carcinoma", "Cardiac Arrest", "Cardiac Failure",
        "Carpal Tunnel Syndrome", "Cataract", "Cellulitis", "Cerebral Palsy",
        "Cerebrovascular Accident", "Cervical Pain", "Cervical Spondylosis",
        "Chickenpox", "Chikungunya", "Cholecystitis", "Cholelithiasis",
        "Cholera", "Chronic Bronchitis", "Chronic Cough", "Chronic Gastritis",
        "Chronic Kidney Disease", "Chronic Liver Disease", "Chronic Obstructive Pulmonary Disease",
        "Cirrhosis of Liver", "Cluster Headache", "Cold", "Colitis",
        "Colon Cancer", "Conjunctivitis", "Constipation", "Contact Dermatitis",
        "Convulsion", "Corneal Ulcer", "Coronary Artery Disease", "COVID-19",
        "Crohn's Disease", "Croup", "Cystitis",
        // D
        "Deafness", "Deep Vein Thrombosis", "Dehydration", "Dengue Fever",
        "Depression", "Dermatitis", "Dermatophytosis", "Deviated Nasal Septum",
        "Diabetes Mellitus Type 1", "Diabetes Mellitus Type 2", "Diabetic Foot",
        "Diabetic Nephropathy", "Diabetic Neuropathy", "Diabetic Retinopathy",
        "Diarrhoea", "Diphtheria", "Disc Prolapse", "Diverticulitis",
        "Drug Allergy", "Dry Eye Syndrome", "Duodenal Ulcer", "Dysentery",
        "Dyslipidemia", "Dyspepsia", "Dysphagia",
        // E
        "Ear Infection", "Ectopic Pregnancy", "Eczema", "Elephantiasis",
        "Emphysema", "Encephalitis", "Endometriosis", "Enteric Fever",
        "Epilepsy", "Epistaxis", "Erectile Dysfunction", "Erythema",
        "Essential Hypertension", "Eye Infection",
        // F
        "Fatty Liver Disease", "Febrile Seizure", "Fever", "Fibroid Uterus",
        "Fibromyalgia", "Filariasis", "Fistula", "Flatulence",
        "Food Allergy", "Food Poisoning", "Frozen Shoulder", "Fungal Infection",
        // G
        "Gallstone", "Gangrene", "Gastric Ulcer", "Gastritis",
        "Gastroenteritis", "Gastroesophageal Reflux Disease",
        "Generalized Anxiety Disorder", "Genital Herpes", "Giardiasis",
        "Gingivitis", "Glaucoma", "Goitre", "Gonorrhoea", "Gout",
        "Grave's Disease", "Guillain-Barre Syndrome",
        // H
        "Haemorrhoids", "Hay Fever", "Head Injury", "Headache",
        "Heart Attack", "Heart Failure", "Helicobacter Pylori Infection",
        "Helminthiasis", "Hemiplegia", "Hepatitis A", "Hepatitis B",
        "Hepatitis C", "Hepatitis E", "Hernia", "Herpes Simplex",
        "Herpes Zoster", "High Blood Pressure", "High Cholesterol",
        "Hip Pain", "Hives", "Hookworm Infection", "Hydrocele",
        "Hypercholesterolemia", "Hyperglycemia", "Hyperlipidemia",
        "Hyperthyroidism", "Hypertension", "Hypoglycemia", "Hypothyroidism",
        // I
        "IBS", "Ichthyosis", "Impetigo", "Incontinence",
        "Infertility", "Inflammatory Bowel Disease", "Influenza",
        "Insomnia", "Interstitial Lung Disease", "Iron Deficiency Anaemia",
        "Irritable Bowel Syndrome", "Ischaemic Heart Disease",
        // J-K
        "Jaundice", "Joint Pain", "Juvenile Arthritis",
        "Kala-azar", "Keloid", "Keratitis", "Kidney Infection",
        "Kidney Stone", "Knee Pain",
        // L
        "Laryngitis", "Leishmaniasis", "Leprosy", "Leptospirosis",
        "Leukaemia", "Lichen Planus", "Liver Abscess", "Liver Cancer",
        "Low Back Pain", "Lower Respiratory Tract Infection",
        "Lumbar Spondylosis", "Lung Cancer", "Lupus", "Lymphadenitis",
        "Lymphoma",
        // M
        "Macular Degeneration", "Malaria", "Malnutrition", "Measles",
        "Meningitis", "Meniscus Tear", "Menorrhagia", "Metabolic Syndrome",
        "Migraine", "Miscarriage", "Mitral Valve Disease", "Molluscum Contagiosum",
        "Mumps", "Muscle Spasm", "Muscular Dystrophy", "Myalgia",
        "Myasthenia Gravis", "Myocardial Infarction",
        // N
        "Nasal Polyp", "Nausea", "Neck Pain", "Nephritic Syndrome",
        "Nephrotic Syndrome", "Nerve Injury", "Neuralgia", "Neuropathy",
        "Nipah Virus", "Non-Alcoholic Fatty Liver Disease",
        // O
        "Obesity", "Obstructive Sleep Apnea", "Oesophagitis",
        "Oral Candidiasis", "Oral Ulcer", "Orchitis", "Osteoarthritis",
        "Osteomyelitis", "Osteoporosis", "Otitis Externa", "Otitis Media",
        "Ovarian Cyst",
        // P
        "Pancreatitis", "Panic Disorder", "Paralysis", "Parkinson's Disease",
        "PCOS", "Pelvic Inflammatory Disease", "Peptic Ulcer Disease",
        "Pericarditis", "Peritonitis", "Pharyngitis", "Piles",
        "Placenta Previa", "Plantar Fasciitis", "Pleural Effusion",
        "Pleurisy", "Pneumonia", "Pneumothorax", "Polycystic Ovary Syndrome",
        "Polyp", "Post-Traumatic Stress Disorder", "Pre-eclampsia",
        "Prostate Cancer", "Prostatitis", "Psoriasis", "Pterygium",
        "Pulmonary Embolism", "Pulmonary Fibrosis", "Pulmonary Tuberculosis",
        "Pyelonephritis",
        // R
        "Rabies", "Rash", "Reactive Arthritis", "Rectal Prolapse",
        "Renal Failure", "Respiratory Tract Infection", "Restless Leg Syndrome",
        "Retinal Detachment", "Rheumatic Fever", "Rheumatic Heart Disease",
        "Rheumatoid Arthritis", "Rhinitis", "Rickets", "Ringworm",
        "Rotavirus Infection", "Rubella",
        // S
        "Scabies", "Schizophrenia", "Sciatica", "Scoliosis",
        "Seizure", "Sepsis", "Sinusitis", "Skin Allergy",
        "Skin Cancer", "Skin Infection", "Slipped Disc", "Snake Bite",
        "Sore Throat", "Spinal Cord Injury", "Splenomegaly",
        "Spondylitis", "Spondylosis", "Staphylococcal Infection",
        "Stomach Cancer", "Stomach Ulcer", "Stomatitis", "Stroke",
        "Stye", "Syphilis",
        // T
        "Tachycardia", "Tendinitis", "Tennis Elbow", "Tetanus",
        "Thalassemia", "Throat Infection", "Thrombocytopenia",
        "Thyroid Nodule", "Tinnitus", "Tonsilitis", "Toothache",
        "Toxic Hepatitis", "Trachoma", "Trigeminal Neuralgia",
        "Tuberculosis", "Tumour", "Typhoid Fever",
        // U
        "Ulcerative Colitis", "Upper Respiratory Tract Infection",
        "Urethritis", "Urinary Incontinence", "Urinary Tract Infection",
        "Urticaria", "Uterine Fibroid", "Uterine Prolapse",
        // V
        "Vaginal Discharge", "Varicella", "Varicose Veins",
        "Vasculitis", "Vertigo", "Viral Fever", "Viral Hepatitis",
        "Vitamin B12 Deficiency", "Vitamin D Deficiency", "Vitiligo", "Vomiting",
        // W-Z
        "Warts", "Wheezing", "Whooping Cough", "Worm Infestation",
    )

    // ═══════════════════════════════════════════════════
    // Medical Tests List (Bangladesh context)
    // ═══════════════════════════════════════════════════
    val medicalTests = listOf(
        // Blood Tests
        "CBC (Complete Blood Count)", "Blood Sugar Fasting", "Blood Sugar PP (2hr ABF)",
        "Blood Sugar Random", "HbA1c", "Blood Grouping & Rh Typing",
        "ESR (Erythrocyte Sedimentation Rate)", "CRP (C-Reactive Protein)",
        "Lipid Profile", "Total Cholesterol", "Triglycerides", "HDL Cholesterol",
        "LDL Cholesterol", "VLDL", "Serum Creatinine", "Blood Urea",
        "BUN (Blood Urea Nitrogen)", "Serum Uric Acid", "Serum Electrolytes",
        "Serum Sodium", "Serum Potassium", "Serum Calcium", "Serum Magnesium",
        "Serum Iron", "TIBC", "Ferritin", "Serum Bilirubin (Total)",
        "Serum Bilirubin (Direct)", "SGPT (ALT)", "SGOT (AST)",
        "Alkaline Phosphatase", "GGT (Gamma GT)", "Serum Protein",
        "Serum Albumin", "Prothrombin Time (PT)", "INR", "APTT",
        "D-Dimer", "Fibrinogen", "Peripheral Blood Film",
        "Reticulocyte Count", "Platelet Count", "Hemoglobin Electrophoresis",
        "G6PD", "Direct Coombs Test", "Indirect Coombs Test",
        // Thyroid
        "TSH", "Free T3", "Free T4", "T3", "T4",
        "Anti-TPO Antibody", "Thyroglobulin",
        // Diabetes & Hormone
        "Fasting Insulin", "C-Peptide", "OGTT (Oral Glucose Tolerance Test)",
        "Cortisol", "Prolactin", "FSH", "LH", "Estradiol",
        "Progesterone", "Testosterone", "DHEA-S", "Growth Hormone",
        "Vitamin D (25-OH)", "Vitamin B12", "Folic Acid",
        // Liver & Hepatitis
        "Liver Function Test (LFT)", "HBsAg", "Anti-HBs", "Anti-HBc (Total)",
        "Anti-HBc (IgM)", "HBeAg", "Anti-HBe", "HBV DNA (Quantitative)",
        "Anti-HCV", "HCV RNA (Quantitative)", "HAV IgM", "HEV IgM",
        "AFP (Alpha Fetoprotein)",
        // Kidney
        "Kidney Function Test (KFT)", "eGFR", "Urine R/M/E",
        "Urine for Albumin", "Urine for Microalbumin", "Urine ACR",
        "24-Hour Urine Protein", "24-Hour Urine Creatinine",
        "Urine Culture & Sensitivity",
        // Cardiac
        "ECG (Electrocardiogram)", "Echocardiography", "Troponin I",
        "Troponin T", "CK-MB", "NT-proBNP", "BNP",
        "Stress Test (TMT/ETT)", "Holter Monitoring", "Coronary Angiography",
        // Infection & Immunity
        "Widal Test", "Blood Culture & Sensitivity", "Dengue NS1 Antigen",
        "Dengue IgM", "Dengue IgG", "Malaria (ICT)", "Malaria (MP)",
        "Chikungunya IgM", "RA Factor (Rheumatoid Factor)", "ANA (Antinuclear Antibody)",
        "Anti-dsDNA", "Anti-CCP", "ASO Titre", "VDRL", "TPHA",
        "HIV 1 & 2 Antibody", "Mantoux Test (Tuberculin Test)",
        "Sputum for AFB", "GeneXpert (MTB/RIF)", "TB Gold (IGRA)",
        "Stool R/M/E", "Stool for OBT", "Stool Culture",
        "Pus Culture & Sensitivity", "Wound Swab C/S",
        // Tumor Markers
        "CEA", "CA 19-9", "CA 125", "CA 15-3", "PSA (Total)",
        "PSA (Free)", "Beta-hCG",
        // Imaging
        "X-Ray Chest PA View", "X-Ray KUB", "X-Ray Cervical Spine",
        "X-Ray Lumbar Spine", "X-Ray Pelvis", "X-Ray Knee Joint",
        "X-Ray Hand", "X-Ray Shoulder", "X-Ray Ankle",
        "USG Whole Abdomen", "USG Lower Abdomen", "USG Thyroid",
        "USG Breast", "USG Pregnancy Profile", "USG KUB",
        "CT Scan Brain", "CT Scan Chest", "CT Scan Abdomen",
        "CT Scan Spine", "CT Angiography",
        "MRI Brain", "MRI Spine (Cervical)", "MRI Spine (Lumbar)",
        "MRI Knee", "MRI Shoulder", "MRI Abdomen",
        "MRCP", "MRA",
        "DEXA Scan (Bone Density)", "Mammography",
        "Doppler USG (Lower Limb Venous)", "Doppler USG (Carotid)",
        "Doppler USG (Renal)",
        // ENT & Eye
        "PTA (Pure Tone Audiometry)", "Tympanometry",
        "Visual Acuity Test", "Fundoscopy", "Slit Lamp Examination",
        "Intraocular Pressure (IOP)", "OCT (Retina)",
        // Pulmonary
        "Spirometry (PFT)", "Peak Flow Meter", "Sputum C/S",
        "ABG (Arterial Blood Gas)",
        // Endoscopy
        "Upper GI Endoscopy (OGD)", "Colonoscopy", "Sigmoidoscopy",
        "ERCP", "Bronchoscopy",
        // Biopsy / Histopathology
        "FNAC", "Biopsy", "Histopathology", "Pap Smear",
        // Miscellaneous
        "EEG (Electroencephalogram)", "EMG/NCV", "Bone Marrow Examination",
        "Skin Biopsy", "Allergy Panel (IgE)", "ABO Incompatibility Test",
        "Coagulation Profile", "ICT for Pregnancy",
    )
}
