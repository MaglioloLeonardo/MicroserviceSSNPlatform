# login → ottieni il JWT
TOKEN=$(curl -s -X POST localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"magliololeonardo@hotmail.it","password":"Bonin1997!?"}' | jq -r .accessToken)

# prova lo stesso token direttamente contro l’auth‑service
curl -i http://localhost:8081/api/v1/users/magliololeonardo@hotmail.it/roles \
     -H "Authorization: Bearer $TOKEN"
