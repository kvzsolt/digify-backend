# Digify

## Description
Digify is a modern technology news portal. This backend part is built using Java Spring Boot REST API.

## Digify Web Application Features

Digify, the modern technology news portal, offers a variety of features to enhance user experience and functionality. Here's a brief overview:

### 1. Newsletter Subscription
- Users can easily subscribe to the newsletter for regular updates.

### 2. Premium Plan
- Unlock exclusive content and features with the premium subscription plan.

### 3. Invoicing with Billingo API
- Seamless integration with Billingo API for efficient and automated invoicing.

### 4. PayPal Integration
- Secure payment processing through seamless integration with PayPal.

### 5. Post Management
- Create, delete, and edit posts effortlessly.

### 6. Comment Sections
- Engage with the community through interactive comment sections.

### 7. Category System
- Organize content systematically with a robust category system.

### 8. Tags
- Enhance content discoverability with a tagging system.

### 9. Accessibility Features
- **Google Translate Integration:**
  - Translate content seamlessly for a global audience.
- **Text-to-Speech:**
  - Enable users to listen to articles with text-to-speech functionality.
- **Speech-to-Text:**
  - Transform spoken words into written text for easy interaction.
- **Post-to-Audio Conversion:**
  - Convert posts into audio format for accessibility.
- **Voice-Based Post Creation:**
  - Create posts using speech recognition for a hands-free experience.

Digify is committed to providing an inclusive and accessible platform, leveraging advanced features for a user-friendly experience.


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
 
## Future Plans

### Refactoring
I'll be dedicating time to refine and enhance the codebase. This means improving the code's readability and performance through ongoing refactoring efforts.

### Structure Refactor
In order to make the project more organized and adaptable, I'll be reviewing and adjusting its overall structure. This includes tidying up directories and streamlining naming conventions for a more polished codebase.

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
