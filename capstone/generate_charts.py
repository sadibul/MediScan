"""
Generate Diagram #10 and Diagram #11 for MediScan AI Capstone Report
"""
import matplotlib
matplotlib.use('Agg')  # Non-interactive backend
import matplotlib.pyplot as plt
import numpy as np
import os

output_dir = os.path.join(os.path.dirname(__file__), 'data', 'results')
os.makedirs(output_dir, exist_ok=True)

# ═══════════════════════════════════════════════════════════════════
# DIAGRAM #10 — Per-Class mAP@50 Bar Chart (YOLOv8s v4 Best Model)
# ═══════════════════════════════════════════════════════════════════

classes_10 = [
    'HOSPITAL', 'DIAGNOSIS', 'MEDICINE', 'DATE', 'DURATION',
    'DOSE_STRENGTH', 'DOCTOR_NAME', 'DOSAGE_SCHEDULE', 'PATIENT_NAME',
    'TEST', 'AGE', 'DEGREE'
]
map50_10 = [99.5, 99.5, 99.2, 99.1, 99.0, 98.8, 98.5, 98.4, 98.4, 98.2, 97.7, 90.8]

fig1, ax1 = plt.subplots(figsize=(12, 7))

# Reverse so highest is at top
classes_reversed = classes_10[::-1]
values_reversed = map50_10[::-1]

bars = ax1.barh(classes_reversed, values_reversed, color='#4A90D9', edgecolor='white', height=0.65)

# Add percentage labels on each bar
for bar, val in zip(bars, values_reversed):
    ax1.text(bar.get_width() - 0.5, bar.get_y() + bar.get_height() / 2,
             f'{val:.1f}%', ha='right', va='center', fontweight='bold',
             fontsize=11, color='white')

ax1.set_xlabel('mAP@50 (%)', fontsize=13, fontweight='bold')
ax1.set_title('Per-Class mAP@50 — YOLOv8s v4 (Best Model)', fontsize=16, fontweight='bold', pad=15)
ax1.set_xlim(88, 101)
ax1.tick_params(axis='y', labelsize=11)
ax1.tick_params(axis='x', labelsize=10)
ax1.spines['top'].set_visible(False)
ax1.spines['right'].set_visible(False)
ax1.grid(axis='x', alpha=0.3, linestyle='--')

plt.tight_layout()
path_10 = os.path.join(output_dir, 'diagram_10_per_class_map50.png')
fig1.savefig(path_10, dpi=200, bbox_inches='tight', facecolor='white')
plt.close(fig1)
print(f"✅ Diagram #10 saved → {path_10}")


# ═══════════════════════════════════════════════════════════════════
# DIAGRAM #11 — v1 vs v4 Comparison Chart (Grouped Bar)
# ═══════════════════════════════════════════════════════════════════

# Sorted by biggest improvement first
classes_11 = [
    'HOSPITAL', 'DIAGNOSIS', 'TEST', 'DOSE_STRENGTH', 'AGE',
    'DOCTOR_NAME', 'PATIENT_NAME', 'DATE', 'DURATION',
    'DOSAGE_SCHEDULE', 'MEDICINE'
]
v1_map = [14.2, 11.0, 25.7, 51.5, 54.4, 59.4, 63.5, 65.2, 68.1, 78.7, 84.3]
v4_map = [99.5, 99.5, 98.2, 98.8, 97.7, 98.5, 98.4, 99.1, 99.0, 98.4, 99.2]

fig2, ax2 = plt.subplots(figsize=(14, 8))

x = np.arange(len(classes_11))
bar_width = 0.35

bars_v1 = ax2.bar(x - bar_width/2, v1_map, bar_width, label='v1 (Baseline)',
                   color='#E74C3C', edgecolor='white', alpha=0.9)
bars_v4 = ax2.bar(x + bar_width/2, v4_map, bar_width, label='v4 (Final Model)',
                   color='#2ECC71', edgecolor='white', alpha=0.9)

# Add value labels on top of each bar
for bar in bars_v1:
    height = bar.get_height()
    ax2.text(bar.get_x() + bar.get_width() / 2, height + 0.8,
             f'{height:.1f}%', ha='center', va='bottom', fontsize=8.5,
             fontweight='bold', color='#C0392B')

for bar in bars_v4:
    height = bar.get_height()
    ax2.text(bar.get_x() + bar.get_width() / 2, height + 0.8,
             f'{height:.1f}%', ha='center', va='bottom', fontsize=8.5,
             fontweight='bold', color='#27AE60')

# Add improvement arrows/annotations for the top 3 biggest jumps
improvements = [v4 - v1 for v1, v4 in zip(v1_map, v4_map)]
for i in range(3):  # Top 3 improvements
    mid_x = x[i]
    ax2.annotate(f'+{improvements[i]:.1f}%',
                 xy=(mid_x, v4_map[i] + 4), ha='center', fontsize=9,
                 fontweight='bold', color='#8E44AD',
                 bbox=dict(boxstyle='round,pad=0.3', facecolor='#F4ECF7', edgecolor='#8E44AD', alpha=0.8))

ax2.set_xlabel('Class', fontsize=13, fontweight='bold')
ax2.set_ylabel('mAP@50 (%)', fontsize=13, fontweight='bold')
ax2.set_title('YOLOv8s mAP@50 — v1 (Baseline) vs v4 (Final Model)', fontsize=16, fontweight='bold', pad=15)
ax2.set_xticks(x)
ax2.set_xticklabels(classes_11, rotation=30, ha='right', fontsize=10)
ax2.set_ylim(0, 115)
ax2.legend(fontsize=12, loc='upper right')
ax2.spines['top'].set_visible(False)
ax2.spines['right'].set_visible(False)
ax2.grid(axis='y', alpha=0.3, linestyle='--')

plt.tight_layout()
path_11 = os.path.join(output_dir, 'diagram_11_v1_vs_v4_comparison.png')
fig2.savefig(path_11, dpi=200, bbox_inches='tight', facecolor='white')
plt.close(fig2)
print(f"✅ Diagram #11 saved → {path_11}")

print(f"\n🎉 Both diagrams generated in: {output_dir}")
