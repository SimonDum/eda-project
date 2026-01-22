#!/bin/bash

# Script pour supprimer l'architecture (services et données)

# --- 1. APPLICATIONS ---
echo "[1/3] Suppression des services applicatifs..."
kubectl delete -f k8s/front-service.yaml
kubectl delete -f k8s/api-service.yaml
kubectl delete -f k8s/integration-service.yaml

# --- 2. INFRASTRUCTURE ---
echo "[2/3] Suppression de l'infrastructure..."
kubectl delete -f k8s/kafka.yaml
kubectl delete -f k8s/postgres.yaml

# --- 3. DONNEES ---
echo "[3/3] Nettoyage des volumes persistants (PVC)..."
kubectl delete pvc -l app=postgres
kubectl delete pvc -l app=kafka

echo "Environnement nettoyé."