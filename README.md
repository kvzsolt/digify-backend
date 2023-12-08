# Digify

## Description
Digify is a modern technology news portal offering the latest updates and trends in technology. This backend part is built using Java Spring Boot REST API.

## Technology Stack
**Programming Language:** Java
  - **SDK Version:** JDK 17
- **Framework:** Spring Boot
  - **Version:** 2.5.6
- **Key Dependencies:**
  - Spring Data JPA
  - Spring Web/WebFlux
  - Spring Validation
  - Spring Context
  - MySQL Connector Java
  - Spring Boot Test
  - Spring Mail
  - Spring Thymeleaf
  - Lombok
  - ModelMapper
  - iText PDF
  - H2 Database
  - Spring Security
  - Java JWT
  - Google Cloud Libraries (Translate, Text-to-Speech, Speech-to-Text, Vision)
  - Cloudinary
  - Apache Tika
  - Swagger

## Environment Variables
To run this project, you will need to set the following environment variables to the `.env` file:

```env
SECRET_KEY=your_secret_key
GOOGLE_APPLICATION_CREDENTIALS=path_to_google_credentials.json
BUCKET_NAME=your_bucket_name
OPENAI_API_KEY=your_openai_api_key
GOOGLE_MAPS_API_KEY=your_google_maps_api_key
BILLINGO_API_KEY=your_billingo_api_key
PAYPAL_CLIENT_ID=your_paypal_client_id
PAYPAL_CLIENT_SECRET=your_paypal_client_secret
CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret
MAIL_HOST=your_mail_host
MAIL_PORT=your_mail_port
MAIL_USERNAME=your_mail_username
MAIL_PASSWORD=your_mail_password
MAIL_PROTOCOL=smtp
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS_ENABLE=true
MAIL_SMTP_STARTTLS_REQUIRED=true
MAIL_SMTP_SSL_TRUST=your_smtp_ssl_trust
SERVER_PORT=your_server_port
DATASOURCE_URL=jdbc:your_datasource_url
DATABASE_USERNAME=your_database_username
DATABASE_PASSWORD=your_database_password
```
