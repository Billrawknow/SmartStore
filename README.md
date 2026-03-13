# 🛍️ SmartStore - E-Commerce Backend API

A production-ready e-commerce backend built with Spring Boot 4.0.3, GraphQL, PostgreSQL, and M-Pesa payment integration.

## 🚀 Features

- ✅ **GraphQL API** - Modern, flexible API with query and mutation support
- ✅ **JWT Authentication** - Secure token-based authentication
- ✅ **Role-Based Authorization** - Customer and Admin roles with @PreAuthorize
- ✅ **PostgreSQL Database** - Robust relational database with Hibernate ORM
- ✅ **M-Pesa Integration** - Daraja API for mobile payments
- ✅ **Cloudinary Integration** - Cloud-based image storage
- ✅ **Docker Support** - Containerized PostgreSQL database
- ✅ **Data Seeding** - Auto-seeded admin user and categories
- ✅ **Password Encryption** - BCrypt password hashing
- ✅ **Validation** - Jakarta Bean Validation

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 4.0.3 | Backend Framework |
| Spring GraphQL | 2.0.2 | GraphQL API |
| Spring Security | 7.0.3 | Authentication & Authorization |
| PostgreSQL | 15-alpine | Database |
| Docker | Latest | Database Containerization |
| JWT (jjwt) | 0.12.3 | Token Authentication |
| Cloudinary | 1.36.0 | Image Storage |
| Lombok | 1.18.42 | Code Generation |
| Hibernate | 7.2.4 | ORM |

---

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.8+**
- **Docker Desktop** (for PostgreSQL)
- **IntelliJ IDEA** (recommended) or any Java IDE
- **Postman** (for API testing)

---

## 🔧 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/Billrawknow/SmartStore.git
cd SmartStore
```

### 2. Start PostgreSQL with Docker
```bash
docker-compose up -d
```

**Verify database is running:**
```bash
docker ps
```

You should see `smartstore-postgres` container running on port `5432`.

### 3. Configure Database Password

**Update `application.yml` if needed:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/smartstore_db
    username: postgres
    password: bill1234  # Change if you set a different password
```

### 4. Install Dependencies
```bash
mvn clean install
```

### 5. Run the Application
```bash
mvn spring-boot:run
```

**OR** run from IntelliJ IDEA:
- Open `RwaknowSmartstoreApplication.java`
- Click the green run button

---

## ✅ Verify Setup

**Application started successfully when you see:**
```
Started RwaknowSmartstoreApplication in X seconds
Tomcat started on port 8080 (http)
✅ Admin user created: admin@smartstore.com / Admin@123
✅ 8 categories created
```

---

## 🔐 Default Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@smartstore.com | Admin@123 |

---

## 📡 API Endpoints

### GraphQL Endpoint
```
POST http://localhost:8080/graphql
```

---

## 🧪 Testing with Postman

### 1. Register New User
```json
{
  "query": "mutation { register(email: \"john@example.com\", password: \"Test@123\", firstName: \"John\", lastName: \"Doe\") { token user { id email firstName role } } }"
}
```

**Response:**
```json
{
  "data": {
    "register": {
      "token": "eyJhbGciOiJIUzI1NiJ9...",
      "user": {
        "id": "2",
        "email": "john@example.com",
        "firstName": "John",
        "role": "CUSTOMER"
      }
    }
  }
}
```

---

### 2. Login
```json
{
  "query": "mutation { login(email: \"admin@smartstore.com\", password: \"Admin@123\") { token user { id email firstName role } } }"
}
```

**Copy the token from the response!**

---

### 3. Get All Categories (Public - No Auth Required)
```json
{
  "query": "query { categories { id name description } }"
}
```

**Response:**
```json
{
  "data": {
    "categories": [
      {
        "id": "1",
        "name": "Electronics",
        "description": "Phones, laptops, tablets, and electronic accessories"
      },
      ...
    ]
  }
}
```

---

### 4. Create Product (Admin Only - Requires JWT)

**Add Authorization Header in Postman:**
- **Key:** `Authorization`
- **Value:** `Bearer YOUR_TOKEN_HERE`
```json
{
  "query": "mutation { createProduct(name: \"iPhone 15 Pro\", description: \"Latest Apple smartphone with A17 Pro chip\", price: 999.99, stock: 50, categoryId: 1) { id name price stock category { name } } }"
}
```

---

### 5. Get Products with Filters
```json
{
  "query": "query { products(categoryId: 1, page: 0, size: 10) { items { id name price stock } totalItems totalPages } }"
}
```

---

### 6. Create Order (Customer - Requires JWT)
```json
{
  "query": "mutation { createOrder(items: [{productId: 1, quantity: 2}], shippingAddress: \"123 Main St, Nairobi\", phoneNumber: \"254712345678\") { id total status items { product { name } quantity price } } }"
}
```

---

### 7. Initiate M-Pesa Payment
```json
{
  "query": "mutation { initiatePayment(orderId: 1, phoneNumber: \"254712345678\", amount: 1999.98) { success checkoutRequestId responseDescription } }"
}
```

---

## 📁 Project Structure
```
src/
├── main/
│   ├── java/com/rwaknow/smartstore/
│   │   ├── config/          # Configuration classes
│   │   │   ├── CloudinaryConfig.java
│   │   │   ├── DataLoader.java
│   │   │   └── MpesaConfig.java
│   │   ├── controller/      # REST controllers
│   │   │   └── MpesaCallbackController.java
│   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── AuthPayload.java
│   │   │   ├── CreateOrderInput.java
│   │   │   ├── CreateProductInput.java
│   │   │   └── ...
│   │   ├── graphql/         # GraphQL resolvers
│   │   │   ├── AuthResolver.java
│   │   │   ├── CategoryResolver.java
│   │   │   ├── OrderResolver.java
│   │   │   └── ProductResolver.java
│   │   ├── model/           # JPA entities
│   │   │   ├── User.java
│   │   │   ├── Category.java
│   │   │   ├── Product.java
│   │   │   ├── Order.java
│   │   │   └── ...
│   │   ├── repository/      # JPA repositories
│   │   │   ├── UserRepository.java
│   │   │   ├── CategoryRepository.java
│   │   │   ├── ProductRepository.java
│   │   │   └── OrderRepository.java
│   │   ├── security/        # Security configuration
│   │   │   ├── JwtUtil.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   ├── SecurityConfig.java
│   │   │   ├── CustomUserDetails.java
│   │   │   └── CustomUserDetailsService.java
│   │   ├── service/         # Business logic
│   │   │   ├── AuthService.java
│   │   │   ├── CategoryService.java
│   │   │   ├── ProductService.java
│   │   │   ├── OrderService.java
│   │   │   ├── MpesaService.java
│   │   │   └── CloudinaryService.java
│   │   └── RwaknowSmartstoreApplication.java
│   └── resources/
│       ├── application.yml
│       └── graphql/
│           └── schema.graphqls
├── test/
└── pom.xml
docker-compose.yml
README.md
```

---

## 🔒 Security Features

1. **JWT Authentication** - Stateless token-based auth
2. **BCrypt Password Hashing** - Secure password storage
3. **Role-Based Access Control** - `@PreAuthorize` annotations
4. **CSRF Protection** - Disabled for stateless API
5. **Stateless Sessions** - No server-side sessions

---

## 🗄️ Database Schema

### Tables Created Automatically:
- **users** - User accounts with roles
- **categories** - Product categories
- **products** - Product catalog
- **product_images** - Product images (Cloudinary URLs)
- **orders** - Customer orders
- **order_items** - Order line items

---

## 🌱 Seeded Data

### Default Admin User:
- Email: `admin@smartstore.com`
- Password: `Admin@123`
- Role: `ADMIN`

### Default Categories:
1. Electronics
2. Clothing
3. Home & Kitchen
4. Books
5. Sports & Outdoors
6. Beauty & Health
7. Toys & Games
8. Automotive

---

## 🔑 Environment Variables

Create a `.env` file or update `application.yml`:
```yaml
# Database
DB_USERNAME=postgres
DB_PASSWORD=bill1234

# JWT
JWT_SECRET=your-super-secret-key-change-in-production
JWT_EXPIRATION=86400000

# Cloudinary
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# M-Pesa
MPESA_CONSUMER_KEY=your_consumer_key
MPESA_CONSUMER_SECRET=your_consumer_secret
MPESA_SHORTCODE=174379
MPESA_PASSKEY=your_passkey
MPESA_ENVIRONMENT=sandbox
```

---

## 🚧 Future Improvements

- [ ] Add product reviews and ratings
- [ ] Implement product image upload via Cloudinary
- [ ] Add email notifications
- [ ] Implement order tracking
- [ ] Add shopping cart functionality
- [ ] Implement product search with Elasticsearch
- [ ] Add GraphQL subscriptions for real-time updates
- [ ] Implement payment webhooks for M-Pesa
- [ ] Add API rate limiting
- [ ] Add comprehensive unit and integration tests

---

## 📞 Contact

**Developer:** Bildad Rwaknow  
**GitHub:** [@Billrawknow](https://github.com/Billrawknow)  
**Repository:** [SmartStore](https://github.com/Billrawknow/SmartStore)

---

## 📄 License

This project is licensed under the MIT License.

---

## 🙏 Acknowledgments

- Spring Boot Team
- GraphQL Java Team
- Safaricom Daraja API
- Cloudinary

---

**Built with ❤️ using Spring Boot & GraphQL**