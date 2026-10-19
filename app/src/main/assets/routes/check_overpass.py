import urllib.request
import urllib.parse
import json

query = """
[out:json];
relation["route"="bus"]["operator"~"California",i](-8.15,-79.08,-8.05,-78.95);
out geom;
"""
url = 'http://overpass-api.de/api/interpreter'
data = urllib.parse.urlencode({'data': query}).encode('utf-8')
req = urllib.request.Request(url, data=data, headers={'User-Agent': 'Mozilla/5.0'})
try:
    with urllib.request.urlopen(req) as response:
        data = json.loads(response.read().decode('utf-8'))
        print('Found', len(data.get('elements', [])), 'relations')
        for el in data.get('elements', []):
            tags = el.get('tags', {})
            print(tags.get('name'), tags.get('ref'))
except Exception as e:
    print(e)
