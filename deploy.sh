#!/bin/bash

# Il s'agit du seul script que vous avez besoin de lancer pour déployer toute l'architecture.
# (Veillez à ce que minikube soit démarré sur votre machine)

set -e

echo "Démarrage du déploiement de l'architecture Microservices..."

# --- 1. INFRASTRUCTURE ---
echo "[1/3] Déploiement de l'infrastructure (Postgres & Kafka)..."
kubectl apply -f k8s/postgres.yaml
kubectl apply -f k8s/kafka.yaml

echo "Attente de la disponibilité de la Base de données et du Broker..."
kubectl wait --for=condition=ready pod -l app=postgres --timeout=120s
kubectl wait --for=condition=ready pod -l app=kafka --timeout=120s

# --- 2. APPLICATIONS ---
echo "[2/3] Déploiement des services applicatifs..."
kubectl apply -f k8s/api-service.yaml
kubectl apply -f k8s/integration-service.yaml
kubectl apply -f k8s/front-service.yaml

# --- 3. FINALISATION ---
echo "[3/3] Vérification que tous les services sont prêts..."
echo "En attente du service API..."
kubectl wait --for=condition=available deployment/api-service --timeout=120s
echo "En attente du service d'intégration..."
kubectl wait --for=condition=ready pod -l app=integration-service --timeout=120s
echo "En attente du service de front..."
kubectl wait --for=condition=available deployment/front-service --timeout=120s
echo "Déploiement terminé avec succès."
echo "Lancement de l'application..."
minikube service front-service