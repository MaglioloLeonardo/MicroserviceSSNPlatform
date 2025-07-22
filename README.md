# TASSProject – Backend

Monorepo containing every backend component for **TASSProject**, a micro-service platform built with Spring Boot, PostgreSQL and RabbitMQ.
Use `docker-compose` for local hacking or deploy the very same stack to Minikube/Kubernetes with the provided manifest.

---

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Tech Stack](#tech-stack)
4. [Directory Layout](#directory-layout)
5. [Local Development (Compose)](#local-development-compose)
6. [Kubernetes / Minikube Deploy](#kubernetes--minikube-deploy)
7. [Configuration & Secrets](#configuration--secrets)
8. [Database Bootstrap](#database-bootstrap)
9. [Security Notes](#security-notes)
10. [Health & Monitoring](#health--monitoring)
11. [Testing](#testing)
12. [Troubleshooting](#troubleshooting)
13. [Roadmap](#roadmap)
14. [License](#license)

---

## Overview

The backend is split into multiple Spring Boot services, each isolated in its own Maven module and Docker image.
Services communicate synchronously over HTTP (via an API-Gateway) and asynchronously through RabbitMQ events.

---

## Architecture

```
┌─────────────────────────┐
│        api-gateway      │
│  (Spring Cloud Gateway) │
└───────────┬─────────────┘
            │ HTTP
┌───────────▼───────────┐
│     auth-service      │  ← JWT, OAuth2
└───────────┬───────────┘
            │ REST
            │
┌───────────▼──────────┐
│   anagrafica-service │
└───────────┬──────────┘
            │
┌───────────▼──────────┐
│ prescription-service │───┐
└───────────┬──────────┘   │ RabbitMQ
            │              │
┌───────────▼──────────┐   │
│ dispensation-service │<──┘
└───────────┬──────────┘
            │
┌───────────▼──────────┐
│  pharma / inventory  │
└──────────────────────┘
```

* **PostgreSQL 17** for persistent storage (schemas per service).
* **RabbitMQ 3** (management UI exposed) for event distribution.
* **Docker volumes** keep data across restarts.
* **Health-checks** for every service ensure correct start-up ordering.

---

## Tech Stack

| Layer             | Technology                                                        |
| ----------------- | ----------------------------------------------------------------- |
| Language          | Java 17                                                           |
| Frameworks        | Spring Boot 3, Spring Security, Spring Data, Spring Cloud Gateway |
| Messaging         | RabbitMQ 3 (management plugin)                                    |
| Database          | PostgreSQL 17                                                     |
| Build             | Maven + Docker                                                    |
| Container Runtime | Docker Engine / containerd                                        |
| Orchestration     | Docker Compose · Kubernetes / Minikube                            |

---

## Directory Layout

```
backend/
├─ api-gateway/           # Spring Cloud Gateway
├─ auth-service/          # authentication / JWT / OAuth2
├─ anagrafica-service/    # citizen registry
├─ prescription-service/
├─ dispensation-service/
├─ inventory-service/
├─ notification-service/
├─ pharma-service/
├─ docker-compose.yml     # full local stack
├─ k8s-deploy.yaml        # single-file K8s manifest
└─ postgres-init/
   └─ init-schemas.sql    # bootstrap schemas & tables
```

Each sub-folder contains:

```
└─ <service>/
   ├─ pom.xml
   ├─ src/main/java/…
   ├─ src/main/resources/
   └─ Dockerfile
```

---

## Local Development (Compose)

> **Tip:** first run may take a while – Maven builds every service and Docker pulls base images.

```bash
# build & start everything
docker compose up --build

# tear down
docker compose down -v
```

**Default ports:**

| Service                   | Host Port    |
| ------------------------- | ------------ |
| PostgreSQL                | 5432         |
| RabbitMQ (AMQP / UI)      | 5672 / 15672 |
| api-gateway (entry-point) | 8080         |
| prescription-service      | 8081         |
| dispensation-service      | 8082         |
| inventory-service         | 8083         |
| notification-service      | 8084         |
| auth-service              | 8085         |
| pharma-service            | 8086         |
| anagrafica-service        | 8087         |

Hot-reload: simply rebuild the affected image and restart that container.

---

## Kubernetes / Minikube Deploy

### 1. Prepare Minikube

```bash
minikube start --memory 6g --cpus 4
eval "$(minikube docker-env)"
```

### 2. Build images inside the Minikube Docker daemon

```bash
for svc in api-gateway auth-service anagrafica-service \
           prescription-service dispensation-service \
           inventory-service notification-service pharma-service
do
  docker build -t tassproject/$svc:latest ./$svc
done
```

### 3. Apply manifest

```bash
kubectl apply -f k8s-deploy.yaml
kubectl get pods -w
```

### 4. Expose the gateway

```bash
minikube service api-gateway --url
```

The manifest includes:

* **PersistentVolumeClaims** for Postgres (`10 Gi`) and RabbitMQ.
* **Deployments + Services** (ClusterIP) for every backend module.
* **ConfigMaps** for non-secret configs (e.g. SQL bootstrap).
* **Secrets** for DB/Rabbit/JWT credentials.

> Update image names or tags if you push to an external registry.

---

## Configuration & Secrets

| Variable                     | Default (dev)       | Description             |
| ---------------------------- | ------------------- | ----------------------- |
| `POSTGRES_USER`              | `myuser`            | DB username             |
| `POSTGRES_PASSWORD`          | `secret`            | DB password             |
| `JWT_SECRET`                 | `my_jwt_secret`     | 256-bit HMAC key        |
| `JWT_EXPIRATION_MS`          | `3600000`           | Token lifetime (ms)     |
| `RABBITMQ_DEFAULT_USER/PASS` | `myuser` / `secret` | RabbitMQ credentials    |
| `GOOGLE_CLIENT_ID/SECRET`    | *unset*             | OAuth2 for auth-service |

In production **replace every default** via environment variables or external Secrets.

---

## Database Bootstrap

`postgres-init/init-schemas.sql` automatically:

1. Creates dedicated schemas (`auth_service`, `anagrafica_service`, …).
2. Seeds required lookup tables (e.g. roles).
3. Grants privileges to `${POSTGRES_USER}`.

Hibernate runs in `update` mode during dev; switch to `validate` (or disable) in production.

---

## Security Notes

* JWTs are signed with HMAC-SHA256.
* `auth-service` increases a *session version* on logout → all old tokens become invalid.
* Roles: `PATIENT`, `DOCTOR`, `PHARMACIST`, `ADMIN`.
* `api-gateway` and every micro-service run as **resource servers** validating the same secret.
* CORS is open to `http://localhost:3000` and `http://localhost:5173` for SPA development – tighten this in prod.

---

## Health & Monitoring

* `/actuator/health` exposed on every Spring Boot service (used by Docker HC and K8s probes).
* RabbitMQ UI: [http://localhost:15672](http://localhost:15672) (user/pass as above).
* Add Prometheus/Grafana by enabling the **micrometer** actuator endpoints (not included here).

---

## Testing

* Unit + integration tests live under `src/test`.
* Every JPA service ships Testcontainers for Postgres/Rabbit tests.
* CI pipeline not included; feel free to plug in GitHub Actions.

---

## Troubleshooting

| Symptom                              | Check / Fix                                               |
| ------------------------------------ | --------------------------------------------------------- |
| Service stuck in `CrashLoopBackOff`  | `kubectl logs <pod>` – most common is wrong DB credential |
| Gateway returns 401 for all requests | Ensure identical `JWT_SECRET` in gateway + services       |
| RabbitMQ queue not created           | Verify service connected with correct user/pass           |
| DB migrations not applied            | Look at `init-schemas.sql` mount path & permissions       |
| DNS resolution fails in cluster      | `kubectl exec -it <pod> -- nslookup prescription-service` |

---

## Roadmap

* Ingress Controller + TLS termination
* Centralised logging (EFK)
* Terraform / Helm charts for infra provisioning
* k6 load-testing scripts

---


