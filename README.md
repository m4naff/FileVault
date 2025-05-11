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

Configure Application
Create application.yml:
minio:
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket: filevault

server:
  port: 8080

# Rate limiting config
rate-limit:
  capacity: 10
  refill-rate: 1

Build & Run
mvn clean install
mvn spring-boot:run

📚 API Reference
Upload File
POST /api/files
Content-Type: multipart/form-data

Params:
- file: The file to upload
- password: The password for the file
- expiresIn: Expiration in hours
- maxDownloads: Maximum downloads

Download File
GET /api/files/{fileId}



