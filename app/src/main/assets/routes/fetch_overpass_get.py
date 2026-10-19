import urllib.request
import urllib.parse
import json

query = """
[out:json][timeout:25];
(
  relation["route"="bus"]["operator"~"California",i](-8.15,-79.08,-8.05,-78.95);
);
out geom;
"""

url = 'http://overpass-api.de/api/interpreter?data=' + urllib.parse.quote(query)
req = urllib.request.Request(url)
req.add_header('User-Agent', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)')

try:
    with urllib.request.urlopen(req) as response:
        content = response.read().decode('utf-8')
        with open('C:/Users/WinterOS/Downloads/MicrosTrujillo/app/src/main/assets/routes/overpass_routes.json', 'w', encoding='utf-8') as f:
            f.write(content)
        print("Success!")
except Exception as e:
    print("Error:", e)
