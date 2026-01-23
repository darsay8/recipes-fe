# Recipes App

Recipes App is a web platform for discovering, creating, and sharing recipes. It enables food enthusiasts to browse, search, and contribute recipes, supporting user registration, authentication, recipe management, commenting, liking, and admin user management. The system consists of a backend API and a client application, built with modern technologies and following best practices for security, maintainability, and responsive design.

## Client Description

Recipes Client is the frontend application that connects to the backend API. It provides a user-friendly interface for browsing recipes, searching by various criteria, registering and logging in, adding new recipes, commenting, liking, and accessing admin features for user and recipe management. The client ensures secure session management and a responsive experience across devices.

## Features

- User registration and login
- Recipe browsing and detailed views
- Advanced recipe search (by name, meal type, country, difficulty)
- Add, edit, and delete recipes (admin only)
- Commenting and liking recipes
- Admin dashboard for user management
- Responsive design for mobile and desktop
- Secure session management

## Stack

- **Frontend:** Thymeleaf, HTML5, CSS3, Bootstrap
- **Backend Communication:** Spring Boot, RestTemplate
- **Authentication:** JWT-based session management
- **Testing:** JUnit, Mockito
- **Build Tools:** Maven
- **Containerization:** Docker

## Preview

![ Recipes App Screenshot 1 ](https://res.cloudinary.com/dtfzj5caw/image/upload/v1768685635/github-repos/recipes-app/1_hwevae.webp)
![ Recipes App Screenshot 2 ](https://res.cloudinary.com/dtfzj5caw/image/upload/v1768685637/github-repos/recipes-app/2_bc9g6y.webp)

## Best Practices Used

- **Security:** Spring Security for authentication and authorization, JWT for secure API calls.
- **Code Quality:** Separation of concerns (controllers, services, models), use of Lombok for boilerplate reduction.
- **Testing:** Unit and integration tests for services and controllers.
- **Configuration:** Externalized configuration via `application.yml` and environment variables.
- **Error Handling:** Custom error pages and logging for troubleshooting.
- **Responsive UI:** Mobile-first design using Bootstrap.
- **Version Control:** `.gitignore` for sensitive and build files, clear branching strategy.
