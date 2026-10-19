import urllib.request
import urllib.parse
import json

urls = [
    'https://overpass-api.de/api/interpreter',
    'https://lz4.overpass-api.de/api/interpreter',
    'https://overpass.kumi.systems/api/interpreter'
]

query = '[out:json];relation["route"="bus"]["operator"~"California",i];out geom;'

for url in urls:
    try:
        data = urllib.parse.urlencode({'data': query}).encode('utf-8')
        req = urllib.request.Request(url, data=data, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req) as r:
            res = json.loads(r.read().decode('utf-8'))
            print('Success with', url, 'Found:', len(res.get('elements', [])))
            with open('app/src/main/assets/routes/osm_dump.json', 'w') as f:
                json.dump(res, f)
            break
    except Exception as e:
        print('Failed', url, e)
