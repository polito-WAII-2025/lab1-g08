[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/vlo9idtn)
# lab1-wa2025

## Group Members
- [Arbore Giuseppe](https://github.com/GiuseppeArbore)
- [Bordino Luca](https://github.com/lbordino)
- [Di Leo Stefano](https://github.com/dlste)
- [Santoro Alice](https://github.com/AliceSantoro)

# Project Setup

## Description
This project mounts a volume in the `assets` directory, which contains the following required files:
- `waypoints.csv`
- `custom-parameters.yml`

The generated outputs are stored in the `output.js` file.

## Installation & Usage
1. Ensure that the `assets` directory contains `waypoints.csv` and `custom-parameters.yml` before running the script.
2. The `custom-parameters.yml` file must include the following parameters:
- `earthRadiusKm` 
- `geofenceCenterLatitude`
- `geofenceCenterLongitude`
- `geofenceRadiusKm`
- *(Optional)* `mostFrequentedAreaRadiusKm`
3. The computation results will be available in the output.js file

## Docker command
``docker build -f Dockerfile -t <image_tag> . && docker run -v <path_to_local_folder>:/app/assets <image_tag> ``