#!/bin/bash

# Ce script n'est pas destiné à être exécuté par quelqu'un d'autre.
# Il illustre les commandes du build et de push effectuées par moi-même pendant le développement.
# Maintenant que c'est fait, vous pouvez simplement faire le déploiement kubernetes via le script "deploy.sh".

set -e

DOCKER_USER="simondum"

echo "Démarrage de la construction et de la publication des images..."

# --- 1. API ---
echo "[1/3] Construction de l'API (api-service)..."
docker build -t $DOCKER_USER/api-service:1.0 ./api-service
docker push $DOCKER_USER/api-service:1.0

# --- 2. INTEGRATION ---
echo "[2/3] Construction du Service d'Intégration (integration-service)..."
docker build -t $DOCKER_USER/integration-service:1.0 ./integration-service
docker push $DOCKER_USER/integration-service:1.0

# --- 3. FRONT ---
echo "[3/3] Construction du Frontend (front-service)..."
docker build -t $DOCKER_USER/front-service:1.0 ./front-service
docker push $DOCKER_USER/front-service:1.0

echo "Toutes les images ont été buildées et pushées avec succès."