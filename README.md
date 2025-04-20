# BUSGO Backend

A Spring Boot backend application for a bus booking system.

## Project Structure

```
src/main/java/com/multi/mis/busgo_backend/
├── config/                 # Configuration classes
├── controller/            # REST controllers
│   ├── AuthController.java
│   ├── BusBookingController.java
│   └── UserController.javaq
├── model/                 # Entity classes
│   ├── BusBooking.java
│   ├── BusCompany.java
│   ├── BusLocation.java
│   ├── BusSchedule.java
│   ├── LocationAddress.java
│   ├── Payment.java
│   ├── Route.java
│   ├── RouteStop.java
│   └── User.java
├── repository/            # JPA repositories
│   ├── BusBookingRepository.java
│   ├── BusCompanyRepository.java
│   ├── BusLocationRepository.java
│   ├── BusScheduleRepository.java
│   ├── LocationAddressRepository.java
│   ├── PaymentRepository.java
│   ├── RouteRepository.java
│   ├── RouteStopRepository.java
│   └── UserRepository.java
├── security/             # Security configuration
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtUtil.java
│   └── SecurityConfig.java
├── service/              # Business logic
│   ├── BusBookingService.java
│   ├── BusCompanyService.java
│   ├── BusLocationService.java
│   ├── BusScheduleService.java
│   ├── LocationAddressService.java
│   ├── PaymentService.java
│   ├── RouteService.java
│   ├── RouteStopService.java
│   └── UserService.java
└── BusgoBackendApplication.java

src/main/resources/
├── application.properties # Application configuration
└── application.yml       # Alternative configuration
```

## Class Guide

### Controllers

#### AuthController
- Handles user authentication endpoints
- Manages JWT token generation and validation
- Endpoints: login, register, refresh token

#### BusBookingController
- Manages bus booking operations
- Handles CRUD operations for bookings
- Manages seat availability and booking status
- Endpoints: create, read, update, delete bookings

#### UserController
- Manages user-related operations
- Handles user registration and profile management
- Endpoints: CRUD operations for users

### Models (Entities)

#### User
- Represents system users (customers, admins)
- Implements Spring Security's UserDetails
- Fields: id, username, password, email, role, etc.

#### BusCompany
- Represents bus operating companies
- Fields: company details, contact information, status

#### BusLocation
- Represents bus stops and terminals
- Fields: location details, address, coordinates

#### BusSchedule
- Represents bus schedules and routes
- Fields: departure/arrival times, available seats, fare

#### BusBooking
- Represents user bookings
- Fields: booking details, seat numbers, payment status

#### LocationAddress
- Represents detailed address information
- Fields: street, city, state, postal code

#### Payment
- Represents payment transactions
- Fields: amount, status, method, transaction details

#### Route
- Represents bus routes
- Fields: source, destination, stops, distance

#### RouteStop
- Represents intermediate stops on routes
- Fields: stop details, sequence, timing

### Repositories

#### UserRepository
- JPA repository for User entity
- Methods: findByUsername, findByRole, etc.

#### BusBookingRepository
- JPA repository for BusBooking entity
- Methods: findByUser, findBySchedule, etc.

#### BusCompanyRepository
- JPA repository for BusCompany entity
- Methods: CRUD operations for companies

#### BusLocationRepository
- JPA repository for BusLocation entity
- Methods: CRUD operations for locations

#### BusScheduleRepository
- JPA repository for BusSchedule entity
- Methods: search schedules, find by route

#### LocationAddressRepository
- JPA repository for LocationAddress entity
- Methods: CRUD operations for addresses

#### PaymentRepository
- JPA repository for Payment entity
- Methods: CRUD operations for payments

#### RouteRepository
- JPA repository for Route entity
- Methods: CRUD operations for routes

#### RouteStopRepository
- JPA repository for RouteStop entity
- Methods: CRUD operations for stops

### Security

#### SecurityConfig
- Configures Spring Security
- Sets up JWT authentication
- Defines security rules and filters

#### JwtAuthenticationFilter
- Filters and validates JWT tokens
- Extracts user information from tokens
- Sets up security context

#### JwtUtil
- Handles JWT token operations
- Generates and validates tokens
- Extracts claims from tokens

#### CustomUserDetailsService
- Implements UserDetailsService
- Loads user details for authentication
- Maps User entity to UserDetails

### Services

#### UserService
- Business logic for user operations
- Handles user authentication
- Manages user profiles and roles

#### BusBookingService
- Business logic for bookings
- Manages seat availability
- Handles booking status updates

#### BusCompanyService
- Business logic for companies
- Manages company operations
- Handles company status

#### BusLocationService
- Business logic for locations
- Manages location operations
- Handles address associations

#### BusScheduleService
- Business logic for schedules
- Manages schedule operations
- Handles seat availability

#### LocationAddressService
- Business logic for addresses
- Manages address operations
- Handles location associations

#### PaymentService
- Business logic for payments
- Manages payment processing
- Handles transaction status

#### RouteService
- Business logic for routes
- Manages route operations
- Handles stop associations

#### RouteStopService
- Business logic for stops
- Manages stop operations
- Handles timing and sequence

## Technologies Used

- Java 17
- Spring Boot 3.1.4
- Spring Security
- JWT Authentication
- MySQL Database
- JPA/Hibernate
- Maven

## Features

- User authentication and authorization
- Bus booking management
- Route and schedule management
- Payment processing
- Location and address management
- Company management

## API Endpoints

### Authentication
- POST /api/auth/login
- POST /api/auth/register

### Users
- GET /api/users
- GET /api/users/by-role
- GET /api/users/active
- POST /api/users/login
- POST /api/users
- PUT /api/users/{userId}
- DELETE /api/users/{userId}

### Bus Bookings
- GET /api/BusBooking/GetAllBusBookings
- GET /api/BusBooking/GetBusBooking
- POST /api/BusBooking/PostBusBooking
- DELETE /api/BusBooking/DeleteBusBooking

## Setup

1. Clone the repository
2. Configure database in `application.properties`
3. Run `mvn clean install`
4. Start the application with `mvn spring-boot:run`

## Security

The application uses JWT for authentication. All endpoints except login and register require a valid JWT token in the Authorization header. 