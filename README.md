# Digify

## Description

**Digify** is a modern technology news portal.  
This is the backend service, built with **Java Spring Boot REST API**.

## Features

Digify offers a variety of features to enhance user experience and functionality:

### Content & User Experience
- Newsletter Subscription
- Premium Plan for exclusive content
- Post Management (create, edit, delete)
- Comment Sections
- Category System & Tags

### Accessibility
- Google Translate Integration
- Text-to-Speech support
- Speech-to-Text support
- Post-to-Audio Conversion
- Voice-Based Post Creation

### Payments & Invoicing
- PayPal Integration
- Automated invoicing with Billingo API

### Infrastructure
- Cloudinary integration for media management
- Google Cloud services:
  - Translate
  - Text-to-Speech
  - Speech-to-Text
  - Vision
- Google Maps Geocoding

## Technology Stack

**Language:** Java 17  
**Framework:** Spring Boot 2.5.6  

### Key Dependencies
- Spring Data JPA
- Spring Web / WebFlux
- Spring Security
- Spring Validation
- Spring Context
- MySQL Connector Java
- H2 Database (test & dev)
- Spring Boot Test
- Spring Mail
- Spring Thymeleaf
- Lombok
- ModelMapper
- iText PDF
- Java JWT
- Google Cloud Client Libraries
- Cloudinary SDK
- Apache Tika
- Swagger / OpenAPI

## Build & CI/CD

- Build tool: **Maven**
- CI/CD: **GitHub Actions**
- Google Cloud authentication: **Workload Identity Federation (WIF)** → no key file required in CI.

## Future Plans

### Refactoring
- Ongoing improvements for code readability and performance.

### Structure Refactor
- Project restructuring to improve maintainability and organization.

## Environment Variables

To run this project, set the following environment variables (e.g. in `.env` or in CI/CD secrets):

```env
# Application secret
SECRET_KEY=your_secret_key

# Google Cloud
# In CI/CD, GOOGLE_APPLICATION_CREDENTIALS is set automatically via Workload Identity Federation.
GOOGLE_APPLICATION_CREDENTIALS=path_to_google_credentials.json (for local development only)
GOOGLE_MAPS_GEOCODING_API_KEY=your_google_maps_api_key

# Cloud Storage
BUCKET_NAME=your_bucket_name

# OpenAI
OPENAI_API_KEY=your_openai_api_key

# Billingo API
BILLINGO_API_KEY=your_billingo_api_key

# PayPal
PAYPAL_CLIENT_ID=your_paypal_client_id
PAYPAL_CLIENT_SECRET=your_paypal_client_secret

# Cloudinary
CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret

# Email (SMTP)
MAIL_HOST=your_mail_host
MAIL_PORT=587
MAIL_USERNAME=your_mail_username
MAIL_PASSWORD=your_mail_password
MAIL_PROTOCOL=smtp
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS_ENABLE=true
MAIL_SMTP_STARTTLS_REQUIRED=true
MAIL_SMTP_SSL_TRUST=your_smtp_ssl_trust

# Database
DATASOURCE_URL=jdbc:your_datasource_url
DATABASE_USERNAME=your_database_username
DATABASE_PASSWORD=your_database_password

# Server
SERVER_PORT=your_server_port
