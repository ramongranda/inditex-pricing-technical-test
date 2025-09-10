![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.x-brightgreen)
![Hexagonal Architecture](https://img.shields.io/badge/Hexagonal-Architecture-orange)
![Maven](https://img.shields.io/badge/Build-Maven-blueviolet)
![DDD](https://img.shields.io/badge/DDD-Domain--Driven--Design-blueviolet)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ramongranda_inditex-pricing-technical-test&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=ramongranda_inditex-pricing-technical-test)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=ramongranda_inditex-pricing-technical-test&metric=bugs)](https://sonarcloud.io/summary/new_code?id=ramongranda_inditex-pricing-technical-test)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=ramongranda_inditex-pricing-technical-test&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=ramongranda_inditex-pricing-technical-test)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=ramongranda_inditex-pricing-technical-test&metric=coverage)](https://sonarcloud.io/summary/new_code?id=ramongranda_inditex-pricing-technical-test)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=ramongranda_inditex-pricing-technical-test&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=ramongranda_inditex-pricing-technical-test)
![Mutation](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/ramongranda/inditex-pricing-technical-test/master/badges/mutation.json&cacheSeconds=300)

# Inditex Pricing – Technical Test

Pricing service for Inditex built with **Spring Boot 3**, **Hexagonal Architecture**, **OpenAPI (contract-first)** and **H2 in MariaDB mode**.

---

## (Run it)

```bash
# Build all modules
mvn -q -DskipTests package

# Run the application module
mvn -pl inditex-pricing-boot -am spring-boot:run
# or after packaging
java -jar inditex-pricing-boot/target/inditex-pricing-boot-*.jar
```

* **Swagger UI:** `http://localhost:8080/openapi/ui`
* **OpenAPI JSON:** `http://localhost:8080/openapi/api-docs`
* **H2 Console:** `http://localhost:8080/h2-console` (if enabled)

> The default port `8080` and OpenAPI paths come from `inditex-pricing-boot/src/main/resources/application.yaml`.
> You can override them with env vars: `SERVER_PORT`, `SPRINGDOC_SWAGGER_UI_PATH`, `SPRINGDOC_API_DOCS_PATH`.

---

## Architecture & Modules

Multi-module Maven project following a **hexagonal** layout:

```
inditex-pricing-technical-test/
 ├─ inditex-pricing-domain/            # Domain model + outbound ports (interfaces)
 ├─ inditex-pricing-application/       # Use cases (inbound ports + services)
 ├─ inditex-pricing-api-contract/      # OpenAPI contract (YAML) + generated *Api, *ApiDelegate, *Vo
 ├─ inditex-pricing-rest-api/          # REST adapter (inbound) – delegates implementation + mappers
 ├─ inditex-pricing-infrastructure/    # Infrastructure adapters (e.g., JPA) [placeholder]
 ├─ inditex-pricing-boot/              # Boot module (Spring Boot app + config + DB init scripts)
 └─ jacoco-report-aggregate/           # Aggregate JaCoCo reports from all modules
```

**Flow:** Client → REST (delegate) → Inbound Port (application) ��� Domain → Outbound Port → Infrastructure.

---

## API (Contract-first)

* Contract file: `inditex-pricing-api-contract/src/main/resources/openapi.yaml`.
* The build generates `*Api`, `*ApiDelegate` and **Value Objects** with suffix `Vo` from the contract.
* Primary endpoint (as per the contract):

  * `GET /prices?applicationDate=…&productId=…&brandId=…` → returns the applicable price.

Swagger UI is provided by **springdoc** and is exposed at `/openapi/ui`.

---

## Database & Time Zones (Assumptions)

* **Engine:** H2 **in-memory** configured in **MariaDB compatibility mode**.
* **Schema/Data init:** scripts under `inditex-pricing-boot/src/main/resources/db/`:

  * `schema.sql` – creates table `PRICES` and indexes.
  * `data.sql` – seeds the sample price windows for the exercise.
* **Types** (aligned with realistic scale):

  * `BRAND_ID` → `INT`
  * `PRODUCT_ID` → `INT`
  * `PRICE_LIST` → `INT`
  * `PRIORITY` → `TINYINT`
  * `PRICE` → `DECIMAL(10,2)`
  * `CURR` → `CHAR(3)` (ISO‑4217 alpha‑3)
  * `START_DATE` / `END_DATE` → `TIMESTAMP`
* **Time zone policy:**

  * **Database session:** assumed **Spain peninsular time (Europe/Madrid, GMT+1 or GMT+2 depending on daylight saving time)** (MariaDB/H2 session time zone).
  * **Application & REST:** **UTC** (ISO���8601, e.g., `2020-06-14T10:00:00Z`).
  * Hibernate reads/writes temporal values in UTC; session time zone at the DB ensures comparisons against `TIMESTAMP` behave as if data
    were in GMT+1.

> This matches the requirement: **DB in +1; everything else in UTC**.

---

## Configuration

All defaults live in `inditex-pricing-boot/src/main/resources/application.yaml`.

Key settings (can be overridden via environment variables):

* `server.port` → default **8080** (`SERVER_PORT`).
* **Springdoc / Swagger:**

  * API Docs: `/openapi/api-docs` (`SPRINGDOC_API_DOCS_PATH`)
  * UI: `/openapi/ui` (`SPRINGDOC_SWAGGER_UI_PATH`)
* **H2** (when enabled):

  * Console at `/h2-console`.
  * Schema/data auto-executed on startup from `classpath:/db/schema.sql` & `classpath:/db/data.sql`.

If you use a file-based H2 URL, the console JDBC URL typically is:

```
jdbc:h2:file:./data/prices;MODE=MariaDB;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1
```

For in-memory:

```
jdbc:h2:mem:prices;MODE=MariaDB;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1
```

---

## How to Run

```bash
# 1) Build everything
mvn -q -DskipTests package

# 2) Start the app (port 8080 by default)
mvn -pl inditex-pricing-boot -am spring-boot:run
# or
java -jar inditex-pricing-boot/target/inditex-pricing-boot-*.jar

# 3) Check health (optional)
curl -i http://localhost:8080/actuator/health
```

### Access

* Swagger UI → `http://localhost:8080/openapi/ui`
* OpenAPI JSON → `http://localhost:8080/openapi/api-docs`
* H2 Console → `http://localhost:8080/h2-console`

  * JDBC URL (memory): `jdbc:h2:mem:prices;MODE=MariaDB;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1`
  * User: `sa` — Password: *(empty unless configured)*

---

## 🧪 Integration Test (IT)

In the **boot** module there is an integration test (`PricesApiControllerIT`) that verifies the correct price selection according to Inditex business rules.

The test loads the full Spring context (REST API, mappers, persistence with H2 in MariaDB mode, and initial data) and performs HTTP requests against `/prices`.

The expected results come from the following logic:

| **Case** | **applicationDate (UTC)** | **Expected priceList** | **Expected price** | **Reason**                                                      |
|----------|---------------------------|------------------------|--------------------|-----------------------------------------------------------------|
| 1        | 2020-06-14T10:00:00Z      | 1                      | 35.50              | Only tariff 1 is valid at that time.                            |
| 2        | 2020-06-14T16:00:00Z      | 2                      | 25.45              | Tariffs 1 and 2 overlap, tariff 2 has higher priority.          |
| 3        | 2020-06-14T21:00:00Z      | 1                      | 35.50              | Tariff 2 has expired, only tariff 1 is valid.                   |
| 4        | 2020-06-15T10:00:00Z      | 1                      | 35.50              | Tariff 3 has expired at 11:00 Europe/Madrid; tariff 1 is valid. |
| 5        | 2020-06-16T21:00:00Z      | 4                      | 38.95              | Tariffs 1 and 4 overlap, tariff 4 has higher priority.          |

The rule applied is:

1. Filter tariffs whose validity period contains `applicationDate`.
2. If multiple tariffs match, select the one with the **highest priority**.
3. If there is still a tie, select the tariff with the **most recent start date**.

Run integration tests with:

```bash
mvn -pl inditex-pricing-boot verify
```

---

## How to Run Unit Tests

To execute all unit tests for all modules:

```bash
mvn test
```

To run unit tests for a specific module:

```bash
cd inditex-pricing-domain
mvn test
```

Test reports are generated in each module under `target/surefire-reports/`.

---

## Mutation Testing (PIT)

Mutation testing is enabled for all modules using [PIT](https://pitest.org/) and the JUnit 5 plugin. This helps ensure the quality of your unit tests by checking if they can detect small changes (mutations) in the code.

### How to Run Mutation Tests

To execute mutation tests and generate reports:

```bash
# Run PIT mutation tests for all modules
mvn org.pitest:pitest-maven:mutationCoverage
```

Reports will be generated in each module under `target/pit-reports/`.

You can also run PIT for a specific module:

```bash
cd inditex-pricing-domain
mvn org.pitest:pitest-maven:mutationCoverage
```

The configuration uses the JUnit 5 plugin and excludes configuration, DTO, MapperImpl, Exception, and test classes from mutation analysis. Reports are generated in HTML, XML, and CSV formats.

For more details, see the [PIT documentation](https://pitest.org/quickstart/maven/).

---

## Mutation Testing (PIT) in Multi-Module Projects

**Important:** PIT mutation testing must be executed in each module that contains source code and unit tests. Do not run PIT from the root `pom.xml` (packaging `pom`), as it does not contain code or tests and will result in an error like:

```
could not find reports directory [<root>/target/pit-reports]
```

### How to Run Mutation Tests in Each Module

1. Change to the module directory (e.g. `inditex-pricing-domain`, `inditex-pricing-application`, etc.):
   ```bash
   cd inditex-pricing-domain
   mvn org.pitest:pitest-maven:mutationCoverage
   ```
2. The mutation report will be generated in `target/pit-reports/` inside that module.
3. Repeat for each module you want to analyze.

> There is no automatic aggregation of PIT reports across modules. Each module produces its own report.

For more details, see the [PIT documentation](https://pitest.org/quickstart/maven/).

---

## Development Notes

* Contract-first: change `openapi.yaml`, regenerate interfaces/VOs, implement delegates in `inditex-pricing-rest-api`.
* Hexagonal rules:

  * **Inbound ports** in *application*; **delegates/controllers** call use cases.
  * **Outbound ports** in *domain*; **infrastructure adapters** implement them.
* SQL scripts live in **boot** because they are environment bootstrap, not domain logic.

---

## Troubleshooting

* **OpenAPI plugs & compile**: ensure `rest-api` module has `provided` deps for `spring-web`, `spring-context`, `jakarta.servlet-api`,
  `jakarta.validation-api`, `swagger-annotations-jakarta`.
* **Swagger not showing**: confirm `springdoc-openapi-starter-webmvc-ui` is on the **boot** module classpath and the paths `/openapi/ui` &
  `/openapi/api-docs` aren’t overridden elsewhere.
* **H2 console** 404: enable the console and verify the same JDBC URL as the application (memory DB names must match).

---

## License

This repository includes an OSS license file; see `LICENSE` for details.

---

## Docker & Docker Compose

You can run the Inditex Pricing application using Docker or Docker Compose for easy deployment, local development, and testing. Below are the recommended ways to build and run the application using containers.

### Quick Start

#### 1. Run with Docker Compose (Recommended)
This will build the image and start the application (and any dependencies you add in `docker-compose.yml`).

```bash
# Build and run the application and its dependencies
docker compose up --build
```
- The app will be available at [http://localhost:8080/openapi/ui](http://localhost:8080/openapi/ui).
- To stop and remove containers:
  ```bash
  docker compose down
  ```

#### 2. Run with Docker only (no Compose)
If you only want to run the application container:

```bash
# Build the Docker image (from the project root or inditex-pricing-boot)
docker build -t inditex-pricing:latest .

# Run the container (removes container after exit)
docker run --rm -p 8080:8080 inditex-pricing:latest
```

### Customization & Advanced Usage
- **Environment variables:** You can override settings (e.g. `SERVER_PORT`, `SPRING_PROFILES_ACTIVE`) in `docker-compose.yml` under `environment:`.
- **Mount configuration or DB scripts:** Uncomment and edit the `volumes:` section in `docker-compose.yml` to mount custom files.
- **Add services:** You can add a database (e.g. MariaDB) or other dependencies in `docker-compose.yml`.
- **Health checks:** Add a `healthcheck:` section to monitor container health.

### Accessing the Application
- **Swagger UI:** [http://localhost:8080/openapi/ui](http://localhost:8080/openapi/ui)
- **OpenAPI JSON:** [http://localhost:8080/openapi/api-docs](http://localhost:8080/openapi/api-docs)
- **H2 Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

### Troubleshooting
- If the app does not start, check the logs with `docker compose logs` or `docker logs <container>`.
- Make sure port 8080 is not in use by another process.
- For custom ports, set `SERVER_PORT` in the environment section.

### More Information
- See [Docker documentation](https://docs.docker.com/) and [Docker Compose documentation](https://docs.docker.com/compose/) for advanced options.

---
