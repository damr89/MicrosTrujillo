import json
import urllib.request
import time

def pt(lat, lon):
    return [lon, lat]

# Data from TrujilloCuadraShape.kt
larcoCuadras = [pt(-8.1408, -79.0552), pt(-8.1396, -79.0538), pt(-8.1379, -79.0515), pt(-8.1352, -79.0490), pt(-8.1340, -79.0475), pt(-8.1325, -79.0450), pt(-8.1311, -79.0435), pt(-8.1295, -79.0400), pt(-8.1285, -79.0390), pt(-8.1278, -79.0381)]
americaSurGrauCuadras = [pt(-8.1278, -79.0381), pt(-8.1265, -79.0360), pt(-8.1250, -79.0340), pt(-8.1235, -79.0320), pt(-8.1220, -79.0290), pt(-8.1205, -79.0260), pt(-8.1185, -79.0230), pt(-8.1175, -79.0210), pt(-8.1165, -79.0195)]
americaSurPapalCuadras = [pt(-8.1278, -79.0381), pt(-8.1260, -79.0385), pt(-8.1245, -79.0395), pt(-8.1235, -79.0398), pt(-8.1215, -79.0405), pt(-8.1189, -79.0416)]
juanPabloUNTCuadras = [pt(-8.1189, -79.0416), pt(-8.1170, -79.0405), pt(-8.1145, -79.0392), pt(-8.1135, -79.0388), pt(-8.1128, -79.0385)]
jesusNazarethCuadras = [pt(-8.1128, -79.0385), pt(-8.1110, -79.0375), pt(-8.1092, -79.0346), pt(-8.1080, -79.0355), pt(-8.1045, -79.0382)]
mansicheMetropolitanaCuadras = [pt(-8.1045, -79.0382), pt(-8.1030, -79.0400), pt(-8.1015, -79.0420), pt(-8.0980, -79.0460), pt(-8.0965, -79.0487), pt(-8.0872, -79.0520)]
esperanzaCuadras = [pt(-8.0872, -79.0520), pt(-8.0850, -79.0530), pt(-8.0810, -79.0540), pt(-8.0795, -79.0545), pt(-8.0770, -79.0560), pt(-8.0732, -79.0578), pt(-8.0710, -79.0590), pt(-8.0674, -79.0610), pt(-8.0600, -79.0660)]
americaNorteMochicaCuadras = [pt(-8.1165, -79.0195), pt(-8.1135, -79.0185), pt(-8.1105, -79.0180), pt(-8.1070, -79.0175), pt(-8.1040, -79.0175), pt(-8.1000, -79.0178), pt(-8.0960, -79.0190), pt(-8.0921, -79.0211)]
americaOesteMallCuadras = [pt(-8.1278, -79.0381), pt(-8.1250, -79.0410), pt(-8.1215, -79.0435), pt(-8.1180, -79.0460), pt(-8.1145, -79.0480), pt(-8.1090, -79.0455), pt(-8.1045, -79.0440)]
pabloCasalsCuadras = [pt(-8.1045, -79.0440), pt(-8.1015, -79.0400), pt(-8.0980, -79.0360), pt(-8.0960, -79.0295), pt(-8.0935, -79.0245), pt(-8.0921, -79.0211)]
tupacTahuantinsuyoCuadras = [pt(-8.0921, -79.0211), pt(-8.0900, -79.0238), pt(-8.0875, -79.0260), pt(-8.0860, -79.0272), pt(-8.0845, -79.0285), pt(-8.0830, -79.0298), pt(-8.0795, -79.0315)]
milagroCuadras = [pt(-8.0580, -79.0470), pt(-8.0500, -79.0520), pt(-8.0440, -79.0555), pt(-8.0330, -79.0635), pt(-8.0280, -79.0660)]
laredoPorvenirCuadras = [pt(-8.0890, -78.9620), pt(-8.0925, -78.9770), pt(-8.0960, -78.9920), pt(-8.1015, -79.0050), pt(-8.1050, -79.0100), pt(-8.1100, -79.0150), pt(-8.1165, -79.0195)]

def fetch_osrm(points):
    res = []
    for i in range(len(points)-1):
        p1 = points[i]
        p2 = points[i+1]
        coords = f"{p1[0]},{p1[1]};{p2[0]},{p2[1]}"
        url = f"https://router.project-osrm.org/route/v1/driving/{coords}?overview=full&geometries=geojson"
        try:
            req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla'})
            with urllib.request.urlopen(req) as response:
                data = json.loads(response.read().decode('utf-8'))
                geom = data['routes'][0]['geometry']['coordinates']
                if i == 0:
                    res.extend(geom)
                else:
                    res.extend(geom[1:])
        except Exception as e:
            print(f"Error for {coords}: {e}")
            if i == 0:
                res.extend([p1, p2])
            else:
                res.append(p2)
        time.sleep(0.1)
    return res

routes = {
    "v_ida": larcoCuadras + americaSurPapalCuadras[1:] + juanPabloUNTCuadras[1:] + jesusNazarethCuadras[1:] + mansicheMetropolitanaCuadras[1:] + esperanzaCuadras[1:],
    "b1_ida": larcoCuadras + americaSurGrauCuadras[1:] + americaNorteMochicaCuadras[1:] + tupacTahuantinsuyoCuadras[1:] + esperanzaCuadras[1:],
    "c_ida": larcoCuadras + americaOesteMallCuadras[1:] + pabloCasalsCuadras[1:] + tupacTahuantinsuyoCuadras[1:] + esperanzaCuadras[1:] + milagroCuadras[1:],
    "a_ida": larcoCuadras + americaSurGrauCuadras[1:] + americaNorteMochicaCuadras[1:] + tupacTahuantinsuyoCuadras[1:] + esperanzaCuadras[1:] + milagroCuadras[1:],
    "bc_ida": larcoCuadras + americaSurGrauCuadras[1:] + americaNorteMochicaCuadras[1:] + pabloCasalsCuadras[1:] + esperanzaCuadras[1:],
    "lv_ida": laredoPorvenirCuadras + americaSurGrauCuadras[::-1][1:] + larcoCuadras[::-1][1:]
}

for name, points in routes.items():
    print(f"Fetching {name}...")
    shape = fetch_osrm(points)

    # generate json
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

    # generate vuelta
    print(f"Fetching {name}_vuelta...")
    shape_vuelta = fetch_osrm(points[::-1])
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
