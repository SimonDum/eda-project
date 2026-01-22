# Projet EDA & Kubernetes

**Auteur : Simon Dumoulin**

Ce projet implémente une **Architecture Orientée Événements (EDA)** pour la gestion d'étudiants via un déploiement **Kubernetes (minikube)**. Cela a été réalisé dans un but purement pédagogique et dans le cadre du module d'Architecture Orientée Service en formation FISE ICY 5A à l'INSA Hauts-de-France.

> [!NOTE]
> Les réponses aux questions d'introduction à Kubernetes ainsi que des remarques approfondies et des captures d'écran sont fournies dans le rapport PDF.

---

## Architecture de la solution

Le système est composé de 5 services :

### Services d'infrastructure
* **Bus de messages (Kafka)** : Gère le topic de requête `etudiants-requests` et le topic de réponse `etudiants-responses`.
* **Base de données (PostgreSQL)** : Possède une simple table pour les étudiants.

### Microservices applicatifs
*  **API Service (Java Spring Boot)** :
    * Reçoit les requêtes HTTP (REST).
    * Agit comme *Producteur* : Pousse les demandes dans le topic `etudiants-requests`.
*  **Integration Service (Java Spring Boot)** :
    * Agit comme *Consommateur* : Écoute les demandes sur le topic `etudiants-requests`.
    * Effectue les opérations demandées en base de données.
    * Agit comme *Producteur* : Pousse les résultats sur le topic `etudiants-responses`.
*  **Front Service (Node.js + HTML/JS)** :
    * Fournit une interface à l'utilisateur.
    * Envoie des requêtes HTTP à l'API.
    * Agit comme *Consommateur* : Écoute les résultats sur le topic `etudiants-responses`.
    * Utilise les websockets pour diffuser les mises à jour en temps réel à tous les clients connectés.

---

## Structure du dépôt

Le projet est organisé de la manière suivante :

* [api-service/](api-service) : Code source Java Spring Boot de l'API. Contient le [Dockerfile](api-service/Dockerfile) multi-stage associé.
* [integration-service/](integration-service) : Code source Java Spring Boot du Worker. Contient le [Dockerfile](integration-service/Dockerfile) multi-stage associé.
* [front-service/](front-service) : Code source Node.js et Interface Web. Contient le [Dockerfile](front-service/Dockerfile) associé.
* [k8s/](k8s) : Contient tous les manifestes Kubernetes (`.yaml`) pour le déploiement.
* [deploy.sh](deploy.sh) : Script principal pour automatiser le déploiement sur le cluster.
* [cleanup.sh](cleanup.sh) : Script pour supprimer l'architecture et nettoyer les données.
* [build_and_push.sh](build_and_push.sh) : Script utilitaire ayant servi à la construction et au push des images sur DockerHub.

---

## Prérequis

* **Docker**
* **Minikube**
* **Kubectl**

---

## Déploiement de l'architecture

> [!NOTE]
> Le déploiement est extrêmement simple grâce à Kubernetes et au fait que nos images ont été préalablement push sur DockerHub. Le script [build_and_push.sh](build_and_push.sh) est fourni à titre purement indicatif mais il n'est pas à exécuter.

### 1. Démarrer l'environnement
Lancez Minikube :
```bash
minikube start
```
### 2. Exécuter le script de déploiement
> [!NOTE]
> Assurez vous que le script a les droits d'exécution.
```bash
./deploy.sh
```

---

## Suppression de l'architecture
> [!NOTE]
> Assurez vous que le script a les droits d'exécution.

> [!WARNING]
> Le script supprime non seulement les ressources Kubernetes déployées (Services, Deployments, StatefulSets), mais également les volumes de données persistants (PVC).
```bash
./cleanup.sh
```
