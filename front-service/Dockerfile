# --- Stage 1 : Dependencies (Installation) ---
FROM node:18-alpine AS builder
WORKDIR /app

COPY package*.json ./
RUN npm install --production

# --- Stage 2 : Run (Image finale épurée) ---
FROM node:18-alpine
WORKDIR /app

USER node

COPY --from=builder /app/node_modules ./node_modules
COPY . .

EXPOSE 3000
CMD ["node", "server.js"]