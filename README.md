[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/vlo9idtn)
# lab1-wa2025

## Group Members
- [Arbore Giuseppe](https://github.com/GiuseppeArbore)
- [Bordino Luca](https://github.com/lbordino)
- [Di Leo Stefano](https://github.com/dlste)
- [Santoro Alice](https://github.com/AliceSantoro)

# Project Setup

## Description
This project mounts a volume in the `evaluation` directory, which contains the following required files:
- `waypoints.csv`
- `custom-parameters.yml`

The generated outputs are stored in the `output.js` and  `output_advanced.js` file in the same directory.

## Installation & Usage
1. Ensure that the `evaluation` directory contains `waypoints.csv` and `custom-parameters.yml` before running the application.
2. The `custom-parameters.yml` file must include the following parameters:
- `earthRadiusKm` 
- `geofenceCenterLatitude`
- `geofenceCenterLongitude`
- `geofenceRadiusKm`
- *(Optional)* `mostFrequentedAreaRadiusKm`

## Build Docker Image
Run the following from the [directory](./RouteAnalyzer) containing the Dockerfile.
`<image_tag>` is an arbitrary name (i.e. `analyzer`).

`docker build -f Dockerfile -t <image_tag> .`

## Run Docker Image
When running the following command, ensure that the chosen directory (`<path_to_local_folder>`) contains the `waypoints.csv` file:

`<image_tag>` is the same as the one in [Build Docker Image](#build-docker-image).

`<path_to_local_folder>` is the absolute path to the target directory.

`docker run -v "<path_to_local_folder>:/app/evaluation" <image_tag>`
