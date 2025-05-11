# 📁 FileVault - Secure File Sharing API

![API](https://img.shields.io/badge/API-RESTful-success)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7+-blue)
![MinIO](https://img.shields.io/badge/MinIO-S3%20Storage-orange)
![H2](https://img.shields.io/badge/H2-In-memory%20DB-red)

A secure, production-ready API for temporary file uploads and sharing (like WeTransfer) with expiration and download limits.

## ✨ Features

- **Secure File Uploads** - Multipart form data handling
- **Shareable Links** - Unique, hard-to-guess download URLs
- **Expiration Control** - Auto-delete after time or downloads
- **Rate Limiting** - Protection against API abuse
- **Simple Metadata** - Track files with H2 database

## 🛠️ Technologies

| Component       | Technology |
|----------------|------------|
| Backend        | Spring Boot |
| File Storage   | MinIO (S3-compatible) |
| Database       | H2 (In-memory) |
| API Docs       | OpenAPI/Swagger |

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Docker (for MinIO)
- Maven

### 1. Start MinIO Container
```bash
docker run -p 9000:9000 -p 9001:9001 --name minio \
  -e "MINIO_ROOT_USER=minioadmin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  -v ~/minio-data:/data \
  minio/minio server /data --console-address ":9001"
```
You can enter MinIO interface with the URL: localhost:9001.
After entering the interface you need to create a bucket to store files and define it inside application.yml
### 2. Configure Application
You need to create .env file to import properties.
Create application.yml:
```bash
spring:
  config:
    import: optional:file:.env[.properties]
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    driver-class-name: org.h2.Driver
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.H2Dialect
    show-sql: true
minio:
  url: ${MINIO_ENDPOINT}
  access-key: ${MINIO_ACCESS_KEY}
  secret-key: ${MINIO_SECRET_KEY}
  bucket: ${MINIO_BUCKET_NAME}
```

### 3. Build & Run
mvn clean install
mvn spring-boot:run

## API Reference

#### File upload

```http
  POST /api/v1/upload
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `file` | `MultipartFile` | **Required**. To store the file |
| `password`|`String`| `The password for the file`|
| `expirationHours`| `int`| `Expiration date of the file`|
| `downloadLimit`| `int` | `Download limit of the file`|

Returns download link of the file.

#### File download

```http
  GET /api/v1/download/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of file to fetch |
|`password`| `string` | Password of the file to download

Returns file.



