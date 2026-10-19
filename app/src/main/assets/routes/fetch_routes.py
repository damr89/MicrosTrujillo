import json
import urllib.request
import urllib.parse

def get_overpass(query):
    url = "http://overpass-api.de/api/interpreter"
    data = urllib.parse.urlencode({'data': query}).encode('utf-8')
    req = urllib.request.Request(url, data=data, headers={'User-Agent': 'Mozilla/5.0'})
    with urllib.request.urlopen(req) as response:
        return json.loads(response.read().decode('utf-8'))

# We want the bus routes for "Nuevo California"
query = """
[out:json];
relation["route"="bus"]["operator"~"California",i](-8.15,-79.08,-8.05,-78.95);
out geom;
"""
try:
    data = get_overpass(query)
    for el in data.get('elements', []):
        tags = el.get('tags', {})
        name = tags.get('name', 'Unknown')
        ref = tags.get('ref', 'XX')
        direction = tags.get('direction', 'unknown')
        print(f"Found route: {name} ({ref}) {direction}")

        # Extract geometry
        coords = []
        for member in el.get('members', []):
            if member['type'] == 'way' and member['role'] in ['', 'forward', 'backward']:
                for node in member.get('geometry', []):
                    coords.append([node['lon'], node['lat']])

        if not coords:
            continue

        filename = f"nuevo_california_{ref}_{direction}.geojson".replace(" ", "_").replace("/", "_").lower()
        geojson = {
            "type": "Feature",
            "properties": {
                "name": name,
                "line": ref
            },
            "geometry": {
                "type": "LineString",
                "coordinates": coords
            }
        }
        with open("C:/Users/WinterOS/Downloads/MicrosTrujillo/app/src/main/assets/routes/" + filename, 'w') as f:
            json.dump(geojson, f)
        print(f"Saved {filename} with {len(coords)} points.")
except Exception as e:
    print(e)
