
# Event Management System

## Overview
The Event Management System is a Java-based desktop application designed to facilitate event planning and coordination. It provides role-based dashboards for Customers, Project Managers, Service Providers, and Admins, allowing users to book events, manage bookings, assign project managers, set prices, and communicate via chat.

## Features
- **User Roles**:
  - **Customer**: Book events, manage bookings, and contact assigned Project Managers.
  - **Project Manager**: View assigned events, send prices to customers, and communicate with Customers and Service Providers.
  - **Service Provider**: View event requests, set prices and ready dates, and contact Project Managers.
  - **Admin**: Manage users (update roles, delete users), assign events to Project Managers, and view all events.
- **Authentication**: Login and registration with role selection (Customer, Project Manager, Service Provider, Admin).
- **Event Management**: Book events with details (name, date, location, additional details), track event status, and update event information.
- **Communication**: Role-specific chat functionality for collaboration between Customers, Project Managers, and Service Providers.
- **Data Persistence**: Stores users, events, and chat messages in text files (`users.txt`, `events.txt`, `chats.txt`).

## Prerequisites
- Java Development Kit (JDK) 8 or higher
- A Java IDE (e.g., IntelliJ IDEA, Eclipse) or command-line tools for compilation and execution

## Installation
1. **Clone or Download the Project**:
   - Clone the repository or download the source code.
2. **Set Up the Project**:
   - Import the project into your preferred IDE or navigate to the project directory.
3. **Ensure File Structure**:
   - The application expects the following files in the project root directory for data storage:
     - `users.txt` (for user data)
     - `events.txt` (for event data)
     - `chats.txt` (for chat messages)
   - If these files do not exist, the application will create them automatically when data is saved.
4. **Compile and Run**:
   - Compile the Java files using your IDE or command line:
     ```bash
     javac *.java
     ```
   - Run the main class `EventManagementSystem`:
     ```bash
     java EventManagementSystem
     ```

## Usage
1. **Launch the Application**:
   - Run the `EventManagementSystem` class to open the main window with "Login" and "Register" tabs.
2. **Register a User**:
   - Navigate to the "Register" tab.
   - Enter a username, password, and select a role (Customer, Project Manager, Service Provider, or Admin).
   - Click "Register" to create the account.
3. **Login**:
   - Navigate to the "Login" tab.
   - Enter your username and password.
   - Click "Login" to access the role-specific dashboard.
4. **Role-Specific Dashboards**:
   - **Customer**:
     - Book events by providing event details.
     - View and manage bookings.
     - Chat with the assigned Project Manager.
   - **Project Manager**:
     - View events assigned to you.
     - Send price updates to customers.
     - Communicate with Customers and Service Providers.
   - **Service Provider**:
     - View event requests.
     - Set prices and ready dates for events.
     - Chat with Project Managers.
   - **Admin**:
     - Manage user roles or delete users.
     - Assign events to Project Managers.
     - View all events in the system.
5. **Logout**:
   - Each dashboard includes a "Logout" tab to close the dashboard window.

## File Structure
- **Source Files**:
  - `EventManagementSystem.java`: Main application entry point with login and registration UI.
  - `User.java`: Abstract base class for all user roles.
  - `Customer.java`: Handles customer-specific functionality (booking, managing events, chatting).
  - `ProjectManager.java`: Manages assigned events and communication.
  - `ServiceProvider.java`: Handles event requests and pricing.
  - `Admin.java`: Manages users and event assignments.
  - `Event.java`: Represents an event with attributes like ID, customer, status, and price.
  - `ChatMessage.java`: Represents a chat message with sender, receiver, and timestamp.
  - `DataHandler.java`: Manages data persistence (users, events, chats) using text files.
- **Data Files** (created/used by the application):
  - `users.txt`: Stores user data in the format `username,role,password`.
  - `events.txt`: Stores event data in the format `eventId|customerUsername|details|status|assignedPM|price|readyDate`.
  - `chats.txt`: Stores chat messages in the format `sender,receiver,message,timestamp`.

## Limitations
- **No Real-Time Updates**: The UI does not automatically refresh when data changes (e.g., new messages or events). Users must navigate tabs or reopen dashboards to see updates.
- **Basic Error Handling**: Input validation is minimal, and some errors (e.g., file I/O issues) are printed to the console.
- **Security**: Passwords are stored in plain text in `users.txt`. In a production environment, passwords should be hashed.
- **Single-Threaded**: The application is not designed for concurrent access to data files, which may cause issues in a multi-user environment.


## Collaborators and Team Members
- **Mohammed Ibrahim**:  [GitHub](https://github.com/Mohamediibra7im) | [LinkedIn](https://www.linkedin.com/in/mohammed-ibra7im/)

- **Marwan Ashraf**:  [GitHub](https://github.com/marwan149) | [LinkedIn](https://www.linkedin.com/in/marwan-ashref-1b9aba2ab/)

- **Mahmoud Gamal**:  [GitHub](https://github.com/mahmoudmatter12) | [LinkedIn](https://www.linkedin.com/in/mahmoudmatter/)

- **Ahmed Khairy**:  [GitHub](https://github.com/Ahmedkhairy0106) | [LinkedIn](https://www.linkedin.com/in/ahmedkhairy010)

- **Haidy Hossam**: [GitHub](https://github.com/Haidy-Hosam) | [LinkedIn](https://www.linkedin.com/in/haidyhosam93/)

- **Yehia Khalid**:  [GitHub](https://github.com/lazydiv) | [LinkedIn](https://www.linkedin.com/in/lazy-dev/)

