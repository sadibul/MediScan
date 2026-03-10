# Fix: Remove Quality Check Gate from Extraction Pipeline

## File To Edit

```
src/pipeline/structured_extractor.py
```

## Exact Change

In the `process_structured()` method, find this block (around line 170):

```python
        # ── Quality Check ──
        quality_result = None
        if self.use_quality_check and not skip_quality_check:
            quality_result = self.check_quality(image)
            
            if not quality_result['is_acceptable']:
                return {
                    'prescription_id': datetime.now().strftime('%Y%m%d_%H%M%S'),
                    'extraction_timestamp': datetime.now().isoformat(),
                    'model_version': 'v6_9class_english',
                    'ocr_engine': 'paddleocr',
                    'medications': [],
                    'medication_count': 0,
                    'prescription_info': None,
                    'doctor': None,
                    'stats': {'total_fields_detected': 0},
                    'raw_extractions': [],
                    'annotated_image': image,
                    'quality_check': quality_result,
                    'status': 'rejected',
                    'message': quality_result['recommendation'],
                }
```

Replace it with this (remove the early return, keep quality check running):

```python
        # ── Quality Check ──
        quality_result = None
        if self.use_quality_check and not skip_quality_check:
            quality_result = self.check_quality(image)
            # Quality result is included in the response below,
            # but we never block extraction regardless of score.
```

## That's It

Just delete the `if not quality_result['is_acceptable']:` block and its entire
`return { ... }` statement. The quality check still runs and its result still
appears in the final response — it just never blocks extraction anymore.

## Expected Behavior After Fix

- Image with quality score **0.17** (bad) → still runs full YOLO + PaddleOCR → returns extracted medications
- Image with quality score **0.91** (good) → runs full extraction → same as before
- The `quality_check` field **still appears** in the JSON response
- The `status` field will always be `"completed"` never `"rejected"`

## What NOT To Change

- Do **not** change `fastapi_app.py`
- Do **not** remove the `/check-quality-base64` endpoint
- Do **not** remove `quality_result = self.check_quality(image)` — keep that line
- Only remove the `if not quality_result['is_acceptable']: return { ... }` block
