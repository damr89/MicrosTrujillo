import urllib.request
import urllib.parse

query = """
[out:json][timeout:25];
relation["route"="bus"]["operator"~"California"];
out geom;
"""
url = "https://overpass-api.de/api/interpreter"
data = urllib.parse.urlencode({'data': query}).encode('utf-8')
req = urllib.request.Request(url, data=data, headers={'User-Agent': 'MicrosTrujilloApp/1.0', 'Accept': 'application/json'})
try:
    with urllib.request.urlopen(req) as response:
        with open('app/src/main/assets/routes/overpass.json', 'w', encoding='utf-8') as f:
            f.write(response.read().decode('utf-8'))
    print("Success")
except Exception as e:
    print(e)
