#!/bin/bash

echo "🧪 Testing Virtual Card Payment PoC"

# Run tests
./gradlew test

# Check test results
if [ $? -eq 0 ]; then
    echo "✅ All tests passed!"
else
    echo "❌ Some tests failed!"
    exit 1
fi
