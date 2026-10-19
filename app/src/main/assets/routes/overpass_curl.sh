#!/bin/bash
curl -g 'http://overpass-api.de/api/interpreter?data=[out:json];relation["route"="bus"]["operator"~"California",i](-8.15,-79.08,-8.05,-78.95);out%20geom;' > app/src/main/assets/routes/overpass_routes.json
