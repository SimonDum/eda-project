/**
 * SERVEUR BFF (Backend For Frontend) - Node.js
 * --------------------------------------------
 * Ce serveur joue le rôle de "Pont" (Bridge) entre le navigateur et notre EDA
 * * Son rôle est double :
 * 1. COMMANDES (Sens Montant) : Transmettre les actions utilisateur vers l'API Java (via HTTP REST).
 * 2. EVENEMENTS (Sens Descendant) : Consommer les évènements sur le topic de réponse et notifier le navigateur en temps réel (via WebSocket).
 */

const express = require('express');
const http = require('http');
const { Server } = require("socket.io");
const { Kafka } = require('kafkajs');
const axios = require('axios');

const app = express();
const server = http.createServer(app);
const io = new Server(server);

app.use(express.static('public'));

// --- CONFIGURATION ---
const API_BASE_URL = process.env.API_URL || 'http://api-service:8080/service';
const API_HEADERS = { 
    headers: { 
        'Content-Type': 'application/json',
        'x-api-key': process.env.API_KEY || 'SecretKey2025' 
    }
};

const KAFKA_BROKER = process.env.KAFKA_BROKER || 'kafka-service:9092';
const TOPIC_RESPONSE = 'etudiants-responses';

// --- KAFKA SETUP ---
const kafka = new Kafka({ 
  clientId: 'front-service', 
  brokers: [KAFKA_BROKER],
  retry: { initialRetryTime: 3000, retries: 10 }
});

const consumer = kafka.consumer({ groupId: 'group-front' });

let isKafkaReady = false;

// --- BOUCLE DE CONNEXION (Retry Pattern) ---
async function startKafka() {
  let connected = false;
  
  while (!connected) {
    try {
      console.log("Tentative de connexion à Kafka...");
      
      await consumer.connect();
      // On s'abonne au topic de réponse
      await consumer.subscribe({ topic: TOPIC_RESPONSE, fromBeginning: false });
      
      // On lance l'écoute
      await consumer.run({
        eachMessage: async ({ topic, partition, message }) => {
          const textVal = message.value.toString();
          console.log(`📩 Reçu : ${textVal}`);
          try {
            io.emit('kafka_response', JSON.parse(textVal));
          } catch (e) { console.error("Erreur JSON", e); }
        },
      });

      console.log("Kafka est connecté et prêt !");
      connected = true;
      isKafkaReady = true;

      // On prévient tous les clients connectés que le système est opérationnel.
      // C'est ce signal qui va déclencher le GET_ALL automatique côté client.
      io.emit('system_status', { ready: true });

    } catch (error) {
      console.error("Kafka indisponible. Nouvelle tentative dans 5s...");
      isKafkaReady = false;
      io.emit('system_status', { ready: false }); // On prévient le front
      
      // On attend 5 secondes avant de réessayer
      await new Promise(resolve => setTimeout(resolve, 5000));
    }
  }
}

// On démarre la boucle de connexion à Kafka en arrière-plan
startKafka();

// --- GESTION SOCKET.IO ---
io.on('connection', (socket) => {
  console.log('Client Web connecté');

  // Synchronisation à l'arrivée : 
  // Si le client arrive et que Kafka est DÉJÀ prêt, on lui dit tout de suite.
  if (isKafkaReady) {
    socket.emit('system_status', { ready: true });
  } else {
    socket.emit('system_status', { ready: false });
  }

  // Gestion des actions
  socket.on('front_action', async (data) => {
    
    // On refuse l'action si Kafka n'est pas prêt
    if (!isKafkaReady) {
        socket.emit('kafka_response', { success: false, message: "Système en cours de démarrage, patientez..." });
        return;
    }

    console.log(`Action reçue : ${data.action}`);
    
    try {
      if (data.action === 'CREATE') {
        await axios.post(`${API_BASE_URL}/student/add`, data.student, API_HEADERS);
      } 
      else if (data.action === 'GET_ALL') {
        await axios.get(`${API_BASE_URL}/students`, API_HEADERS);
      } 
      else if (data.action === 'DELETE') {
        await axios.delete(`${API_BASE_URL}/student/delete/${data.targetId}`, API_HEADERS);
      } 
      else if (data.action === 'UPDATE') {
        const studentToUpdate = { ...data.student, id: data.targetId };
        await axios.put(`${API_BASE_URL}/student/update`, studentToUpdate, API_HEADERS);
      }
    } catch (err) {
      console.error("Erreur API :", err.message);
    }
  });
});

server.listen(3000, () => { console.log('Serveur HTTP écoute sur le port 3000'); });