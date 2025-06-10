#!/bin/bash

echo "🚀 Starting Virtual Card Payment PoC Demo"

# Build the project
echo "📦 Building project..."
./gradlew build -x test

# Run the application
echo "🏃‍♂️ Starting application..."
./gradlew bootRun &

# Wait for application to start
echo "⏳ Waiting for application to start..."
sleep 10

# Test Vietnam user payment
echo "🧪 Testing Vietnam user payment..."
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "vietnam_user_1",
    "amount": 100.00,
    "ipAddress": "203.113.123.45"
  }'

echo -e "\n"

# Test EU user geo-blocking
echo "🧪 Testing EU user geo-blocking..."
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "france_user_1",
    "amount": 50.00,
    "ipAddress": "91.185.123.45"
  }'

echo -e "\n"

# Check transaction status
echo "🔍 Checking transaction status..."
curl http://localhost:8080/api/payments/1

echo -e "\n"

# View all transactions
echo "📋 Viewing all transactions..."
curl http://localhost:8080/api/payments

echo -e "\n"

# Check user balance
echo "💰 Checking user balance..."
curl http://localhost:8080/api/balance/vietnam_user_1

echo -e "\n"

# Show H2 Console info
echo "📊 H2 Console Information:"
echo "  URL: http://localhost:8080/h2-console"
echo "  JDBC URL: jdbc:h2:mem:payments"
echo "  Username: sa"
echo "  Password: password"

echo -e "\n"
echo "✅ Demo completed!"
