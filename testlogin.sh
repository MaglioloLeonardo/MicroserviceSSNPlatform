#!/usr/bin/env bash
set -euo pipefail

BASE_URL="http://localhost:8085/api/v1/auth"
HDR="Content-Type: application/json"

OK=0
FAIL=0

function report {
  if [ "$1" -eq "$2" ]; then
    echo "✅ $3 → returned $2 as expected"
    ((OK++))
  else
    echo "❌ $3 → returned $2 but expected $1"
    ((FAIL++))
  fi
}

echo "=== TESTING AUTH SERVICE at $BASE_URL ==="

# 1) Registration: valid user
HTTP=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/register" \
  -H "$HDR" \
  -d '{"email":"user1@example.com","password":"StrongPass123!","role":"PATIENT"}')
report 201 "$HTTP" "Register valid user"

# 2) Registration: duplicate user
HTTP=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/register" \
  -H "$HDR" \
  -d '{"email":"user1@example.com","password":"StrongPass123!","role":"PATIENT"}')
report 400 "$HTTP" "Register duplicate user"

# 3) Login: correct credentials
LOGIN_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/login" \
  -H "$HDR" \
  -d '{"email":"user1@example.com","password":"StrongPass123!"}')
# split body and status
BODY=$(echo "$LOGIN_RESPONSE" | sed '$d')
HTTP=$(echo "$LOGIN_RESPONSE" | tail -n1)
report 200 "$HTTP" "Login valid user"

# extract token for later (simple parsing)
TOKEN=$(echo "$BODY" | sed -E 's/.*"accessToken"\s*:\s*"([^"]+)".*/\1/')
if [[ -n "$TOKEN" ]]; then
  echo "🔑 Captured accessToken (length ${#TOKEN})"
else
  echo "⚠️  Failed to capture token"
fi

# 4) Login: wrong password
HTTP=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/login" \
  -H "$HDR" \
  -d '{"email":"user1@example.com","password":"WrongPass!"}')
report 400 "$HTTP" "Login wrong password"

# 5) Login: non-existent email
HTTP=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/login" \
  -H "$HDR" \
  -d '{"email":"noone@nowhere.com","password":"Anything1!"}')
report 400 "$HTTP" "Login non-existent email"

# 6) Validation: empty email
HTTP=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/login" \
  -H "$HDR" \
  -d '{"email":"","password":"x"}')
report 400 "$HTTP" "Login empty email"

# 7) Validation: bad email format
HTTP=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/login" \
  -H "$HDR" \
  -d '{"email":"not-an-email","password":"whatever"}')
report 400 "$HTTP" "Login invalid email format"

# 8) Validation: empty password
HTTP=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/login" \
  -H "$HDR" \
  -d '{"email":"user1@example.com","password":""}')
report 400 "$HTTP" "Login empty password"

echo
echo "=== SUMMARY: $OK passed, $FAIL failed ==="
if [ "$FAIL" -gt 0 ]; then
  exit 1
fi
