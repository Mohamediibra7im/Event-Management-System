# Event Management System

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Setup](#setup)
4. [Directory Structure](#directory-structure)
5. [Classes and Interfaces](#classes-and-interfaces)
6. [Contributing](#contributing)
7. [Collaborators and Team Members](#collaborators-and-team-members)

## Overview

The Event Management System is a Java-based application designed to manage events, notifications, and user interactions. It provides a comprehensive platform for organizing and handling various aspects of event planning and execution.

## Features

- **Event Management**: Create, update, and delete events.
- **Notification System**: Send notifications to users about upcoming events.
- **User Management**: Manage user accounts and permissions.
- **Admin and Customer Interfaces**: Different interfaces for administrators and customers.
- **Data Persistence**: Store and retrieve data from text files.

## Setup

To set up the Event Management System, follow these steps:

1. **Prerequisites**:
   - Java Development Kit (JDK) installed.
   - Any IDE that supports Java (e.g., IntelliJ IDEA, Eclipse).

2. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/event-management-system.git
   ```

3. **Build the Project**:
   - Open the project in your IDE.
   - Build the project using the IDE's build tools.

4. **Run the Application**:
   - Run the `EventManagementSystem.java` class to start the application.

## Directory Structure

```
EVENT-MANAGEMENT-SYSTEM/
│
├── src/
│   ├── model/
│   │   ├── Event.java
│   │   ├── Notification.java
│   │   └── User.java
│   ├── service/
│   │   ├── EventService.java
│   │   ├── NotificationService.java
│   │   └── UserService.java
│   ├── ui/
│   │   ├── AdminFrame.java
│   │   ├── CustomerFrame.java
│   │   ├── LoginFrame.java
│   │   ├── ProjectManagerFrame.java
│   │   ├── RegisterFrame.java
│   │   └── ServiceProviderFrame.java
│   ├── util/
│   │   ├── DataAccessObject.java
│   │   └── FileHandler.java
│   │
│   └── EventManagementSystem.java
```

## Classes and Interfaces

### Model

- **Event**: Represents an event with attributes like name, date, and location.
- **Notification**: Represents a notification with attributes like message and recipient.
- **User**: Represents a user with attributes like username and password.

### Service

- **EventService**: Handles operations related to events.
- **NotificationService**: Handles operations related to notifications.
- **UserService**: Handles operations related to users.

### UI

- **AdminFrame**: Interface for administrators to manage events and users.
- **CustomerFrame**: Interface for customers to view events and receive notifications.
- **LoginFrame**: Login interface for users.
- **RegisterFrame**: Registration interface for new users.

### Util

- **DataAccessObject**: Handles data persistence operations.
- **FileHandler**: Manages file operations for data storage.
- **EventManagementSystem**: Main class that initializes the application.

## Collaborators and Team Members
- **Mohammed Ibrahim**:  [GitHub](https://github.com/Mohamediibra7im) | [LinkedIn](https://www.linkedin.com/in/mohammed-ibra7im/)

- **Marwan Ashraf**:  [GitHub](https://github.com/marwan149) | [LinkedIn](https://www.linkedin.com/in/marwan-ashref-1b9aba2ab/)

- **Mahmoud Gamal**:  [GitHub](https://github.com/mahmoudmatter12) | [LinkedIn](https://www.linkedin.com/in/mahmoudmatter/)

- **Ahmed Khairy**:  [GitHub](https://github.com/Ahmedkhairy0106) | [LinkedIn](https://www.linkedin.com/in/ahmedkhairy010)

- **Haidy Hossam**: [GitHub](https://github.com/Haidy-Hosam) | [LinkedIn](https://www.linkedin.com/in/haidyhosam93/)

- **Yehia Khalid**:  [GitHub](https://github.com/lazydiv) | [LinkedIn](https://www.linkedin.com/in/lazy-dev/)

