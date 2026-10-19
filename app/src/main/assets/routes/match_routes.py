import json
import urllib.request
import time

def pt(lat, lon):
    return [lon, lat]

# The block nodes
larcoCuadras = [pt(-8.1460, -79.0525), pt(-8.1435, -79.0500), pt(-8.1410, -79.0475), pt(-8.1385, -79.0450), pt(-8.1355, -79.0420), pt(-8.1330, -79.0395), pt(-8.1310, -79.0375), pt(-8.1295, -79.0362), pt(-8.1285, -79.0353)]
americaSurGrauCuadras = [pt(-8.1285, -79.0353), pt(-8.1255, -79.0322), pt(-8.1225, -79.0289), pt(-8.1195, -79.0255), pt(-8.1165, -79.0195)]
americaSurPapalCuadras = [pt(-8.1285, -79.0353), pt(-8.1265, -79.0365), pt(-8.1245, -79.0378), pt(-8.1225, -79.0392), pt(-8.1205, -79.0405), pt(-8.1189, -79.0416)]
juanPabloUNTCuadras = [pt(-8.1189, -79.0416), pt(-8.1175, -79.0425), pt(-8.1160, -79.0435), pt(-8.1148, -79.0445), pt(-8.1135, -79.0455)]
jesusNazarethCuadras = [pt(-8.1135, -79.0455), pt(-8.1118, -79.0430), pt(-8.1098, -79.0395), pt(-8.1082, -79.0370), pt(-8.1070, -79.0350)]
mansicheMetropolitanaCuadras = [pt(-8.1070, -79.0350), pt(-8.1055, -79.0365), pt(-8.1040, -79.0380), pt(-8.1025, -79.0395), pt(-8.1010, -79.0410), pt(-8.0995, -79.0430), pt(-8.0980, -79.0450), pt(-8.0955, -79.0462), pt(-8.0930, -79.0480), pt(-8.0905, -79.0495), pt(-8.0870, -79.0510), pt(-8.0850, -79.0520)]
esperanzaCuadras = [pt(-8.0850, -79.0520), pt(-8.0825, -79.0530), pt(-8.0800, -79.0540), pt(-8.0770, -79.0548), pt(-8.0750, -79.0560), pt(-8.0732, -79.0578), pt(-8.0710, -79.0590), pt(-8.0680, -79.0602), pt(-8.0650, -79.0620), pt(-8.0625, -79.0640), pt(-8.0600, -79.0660)]
americaNorteMochicaCuadras = [pt(-8.1165, -79.0195), pt(-8.1115, -79.0180), pt(-8.1065, -79.0175), pt(-8.1015, -79.0177), pt(-8.0965, -79.0185), pt(-8.0921, -79.0211)]
americaOesteMallCuadras = [pt(-8.1285, -79.0353), pt(-8.1255, -79.0375), pt(-8.1225, -79.0400), pt(-8.1195, -79.0420), pt(-8.1160, -79.0438), pt(-8.1130, -79.0450), pt(-8.1080, -79.0440), pt(-8.1040, -79.0425)]
pabloCasalsCuadras = [pt(-8.1040, -79.0425), pt(-8.1015, -79.0398), pt(-8.0980, -79.0360), pt(-8.0960, -79.0295), pt(-8.0935, -79.0245), pt(-8.0921, -79.0211)]
tupacTahuantinsuyoCuadras = [pt(-8.0921, -79.0211), pt(-8.0900, -79.0238), pt(-8.0885, -79.0245), pt(-8.0875, -79.0260), pt(-8.0860, -79.0272), pt(-8.0845, -79.0285), pt(-8.0830, -79.0298), pt(-8.0795, -79.0315)]
milagroCuadras = [pt(-8.0580, -79.0470), pt(-8.0500, -79.0520), pt(-8.0440, -79.0555), pt(-8.0330, -79.0635), pt(-8.0280, -79.0660)]
laredoPorvenirCuadras = [pt(-8.0890, -78.9620), pt(-8.0925, -78.9770), pt(-8.0960, -78.9920), pt(-8.1015, -79.0050), pt(-8.1050, -79.0100), pt(-8.1100, -79.0150), pt(-8.1165, -79.0195)]

routes = {
    "v_ida": larcoCuadras + americaSurPapalCuadras[1:] + juanPabloUNTCuadras[1:] + jesusNazarethCuadras[1:] + mansicheMetropolitanaCuadras[1:] + esperanzaCuadras[1:],
    "b1_ida": larcoCuadras + americaSurGrauCuadras[1:] + americaNorteMochicaCuadras[1:] + tupacTahuantinsuyoCuadras[1:] + esperanzaCuadras[1:],
    "c_ida": larcoCuadras + americaOesteMallCuadras[1:] + pabloCasalsCuadras[1:] + tupacTahuantinsuyoCuadras[1:] + esperanzaCuadras[1:] + milagroCuadras[1:],
    "a_ida": larcoCuadras + americaSurGrauCuadras[1:] + americaNorteMochicaCuadras[1:] + tupacTahuantinsuyoCuadras[1:] + esperanzaCuadras[1:] + milagroCuadras[1:],
    "bc_ida": larcoCuadras + americaSurGrauCuadras[1:] + americaNorteMochicaCuadras[1:] + pabloCasalsCuadras[1:] + esperanzaCuadras[1:],
    "lv_ida": laredoPorvenirCuadras + americaSurGrauCuadras[::-1][1:] + larcoCuadras[::-1][1:]
}

def fetch_osrm_match(points):
    """ Utiliza la API de OSRM Match para encajar perfectamente los waypoints en la carretera """
    res = []
    chunk_size = 90 # OSRM Match supports up to 100 coordinates
    i = 0
    while i < len(points) - 1:
        chunk = points[i:min(i+chunk_size, len(points))]
        coords = ";".join([f"{p[0]},{p[1]}" for p in chunk])
        radiuses = ";".join(["50"] * len(chunk)) # 50m search radius
        url = f"https://router.project-osrm.org/match/v1/driving/{coords}?overview=full&geometries=geojson&radiuses={radiuses}"
        try:
            req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
            with urllib.request.urlopen(req) as response:
                data = json.loads(response.read().decode('utf-8'))
                if data['code'] == 'Ok':
                    # Extract coordinates from all matchings
                    for match in data['matchings']:
                        geom = match['geometry']['coordinates']
                        if len(res) == 0:
                            res.extend(geom)
                        else:
                            res.extend(geom[1:])
                else:
                    print(f"OSRM Match returned {data['code']}")
                    res.extend(chunk if i == 0 else chunk[1:])
        except Exception as e:
            print(f"Error fetching match chunk: {e}")
            res.extend(chunk if i == 0 else chunk[1:])
        time.sleep(0.3)
        i += chunk_size - 1
    return res

for name, points in routes.items():
    print(f"Matching {name}...")
    shape = fetch_osrm_match(points)

    geojson = {
        "company": "Nuevo California S.A.",
        "code": "M-XX",
        "letter": name.split('_')[0].upper(),
        "direction": "IDA",
        "name": "Route",
        "color": "#003399",
        "stops": [{"order": i+1, "name": f"Stop {i}", "lat": p[1], "lng": p[0]} for i,p in enumerate(points)],
        "shape": [{"lat": p[1], "lng": p[0]} for p in shape]
    }
    with open(f"C:/Users/WinterOS/Downloads/MicrosTrujillo/app/src/main/assets/routes/nuevo_california_{name}.json", 'w') as f:
        json.dump(geojson, f)

    print(f"Matching {name}_vuelta...")
    shape_vuelta = fetch_osrm_match(points[::-1])
    geojson_vuelta = {
        "company": "Nuevo California S.A.",
        "code": "M-XX",
        "letter": name.split('_')[0].upper(),
        "direction": "VUELTA",
        "name": "Route",
        "color": "#003399",
        "stops": [{"order": i+1, "name": f"Stop {i}", "lat": p[1], "lng": p[0]} for i,p in enumerate(points[::-1])],
        "shape": [{"lat": p[1], "lng": p[0]} for p in shape_vuelta]
    }
    with open(f"C:/Users/WinterOS/Downloads/MicrosTrujillo/app/src/main/assets/routes/nuevo_california_{name.split('_')[0]}_vuelta.json", 'w') as f:
        json.dump(geojson_vuelta, f)
    print(f"Done {name}.")
