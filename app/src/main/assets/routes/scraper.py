import urllib.request
import re
import json

url = 'https://smartbus.app/pe/trujillo/nuevo-california'
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'})
try:
    with urllib.request.urlopen(req) as response:
        content = response.read().decode('utf-8')
        # The data is inside the next.js __next_f arrays, usually json-encoded strings
        blocks = re.findall(r'self\.__next_f\.push\(\[1,"(.*?)"]\)</script>', content, re.DOTALL)
        with open('app/src/main/assets/routes/dump.txt', 'w', encoding='utf-8') as f:
            for b in blocks:
                f.write(b + '\n\n---\n\n')
        print("Dumped to dump.txt")
except Exception as e:
    print(e)
