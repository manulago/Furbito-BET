#!/bin/bash

echo "Creating Event..."
curl -X POST http://localhost:8080/api/admin/events \
  -H "Content-Type: application/json" \
  -d '{"name": "Real Madrid vs Barcelona", "date": "2025-05-01T20:00:00"}'
echo -e "\n"

echo "Adding Outcome..."
curl -X POST http://localhost:8080/api/admin/events/1/outcomes \
  -H "Content-Type: application/json" \
  -d '{"description": "Real Madrid Wins", "odds": 1.5}'
echo -e "\n"

echo "Listing Events..."
curl http://localhost:8080/api/events
echo -e "\n"

echo "Placing Bet (User ID 2)..."
curl -X POST http://localhost:8080/api/bets \
  -H "Content-Type: application/json" \
  -d '{"userId": 2, "outcomeId": 1, "amount": 10}'
echo -e "\n"
