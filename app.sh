#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

COMMAND=$1

if [ "$COMMAND" == "start" ]; then
  if [ "$2" == "--build" ]; then
    echo "Building images and starting services..."
    docker-compose up -d --build
  else
    echo "Starting services with existing images..."
    echo "(Use './app.sh start --build' to force a rebuild)"
    docker-compose up -d
  fi
  echo "All services started."
elif [ "$COMMAND" == "stop" ]; then
  echo "Stopping all services and removing volumes..."
  docker-compose down -v
  echo "All services and volumes removed."
else
  echo "Usage: $0 {start|stop}"
  echo "  start:         Start services using existing images."
  echo "  start --build: Rebuild images and then start services."
  echo "  stop:          Stop and remove all services and data volumes."
  exit 1
fi
