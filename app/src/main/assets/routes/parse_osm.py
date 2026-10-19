import json
import re

with open('app/src/main/assets/routes/osm_dump.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

# Dictionary to store the longest shape for each route file
best_shapes = {}

for el in data.get('elements', []):
    tags = el.get('tags', {})
    name = tags.get('name', '')
    ref = tags.get('ref', '')

    if not ref:
        m = re.search(r'(M-\d+)', name)
        if m:
            ref = m.group(1)

    # Clean the ref from quotes and extra spaces
    ref = ref.replace('"', '').strip()

    # We want these specific references:
    valid_refs = ["M-07 V", "M-10 B1", "M-12 C", "M-08 A", "M-09 BC", "M-36 LV"]

    is_valid = False
    matched_ref = ""
    for v in valid_refs:
        if ref == v or ref == v.split(" ")[0] or v in ref:
            is_valid = True
            matched_ref = v
            break

    if not is_valid:
        continue

    letter = matched_ref.split(" ")[1]
    code = matched_ref.split(" ")[0]

    dir_str = "IDA"
    if "San Pedro \u2192" in name or "Virgen de la Puerta \u2192" in name or "Via Panamericana Norte \u2192" in name or "V\u00eda Panamericana Norte \u2192" in name or "Los Tulipanes \u2192" in name or "Manuel Seoane \u2192" in name:
        dir_str = "VUELTA"

    # Some relations are circular rings. Ignore them.
    if "\u2192" not in name:
        continue

    shape = []
    for member in el.get('members', []):
        if member['type'] == 'way' and member['role'] in ['', 'forward', 'backward']:
            way_geom = member.get('geometry', [])
            if member['role'] == 'backward':
                way_geom = list(reversed(way_geom))

            for pt in way_geom:
                lat = pt['lat']
                lon = pt['lon']
                if len(shape) == 0 or shape[-1] != [lon, lat]:
                    shape.append([lon, lat])

    if len(shape) < 2:
        continue

    filename = f"nuevo_california_{letter}_{dir_str}.json".lower()

    if filename not in best_shapes or len(shape) > len(best_shapes[filename]['shape']):
        stops = []
        step = max(1, len(shape) // 25)
        for i, pt in enumerate(shape):
            if i % step == 0 or i == len(shape) - 1:
                stops.append({
                    "order": len(stops) + 1,
                    "name": f"Paradero {len(stops) + 1}",
                    "lat": pt[1],
                    "lng": pt[0]
                })

        geojson = {
            "company": "Nuevo California S.A.",
            "code": code,
            "letter": letter,
            "direction": dir_str,
            "name": name,
            "color": "#003399",
            "stops": stops,
            "shape": [{"lat": p[1], "lng": p[0]} for p in shape]
        }
        best_shapes[filename] = geojson

for filename, geojson in best_shapes.items():
    with open(f"C:/Users/WinterOS/Downloads/MicrosTrujillo/app/src/main/assets/routes/{filename}", 'w', encoding='utf-8') as f:
        json.dump(geojson, f)
    print(f"Saved {filename} with {len(geojson['shape'])} points.")
