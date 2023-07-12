# Cinema Booking App

## Contents
- **[Overview](#overview)**
- **[Technologies Used](#technologies-used)**
- **[Purpose of Project](#purpose-of-project)**
- **[Using the Project Yourself](#using-the-project-yourself)**
- **[How the Project Works](#how-the-project-works)**
- **[Project Status](#project-status)**
- **[Room for Improvement](#room-for-improvement)**
## Overview

This is a cinema booking application that runs in the terminal.

## Technologies Used
- Java 18

## Purpose of Project

The project was created alongside my studies of Year 1 Computer Science.

This project demonstrates my continued learning of programming principles, including:
- JDBC (MongoDB Atlas)
- Java Concurrency
- File I/O
- Exception Handling
- JUnit testing
- Responsibility-driven design
- Cohesion & Coupling
- Maintainability

## Using the Project Yourself

###### _Paste the following commands into your terminal._

1. Clone the repository to your local machine:
```
git clone https://github.com/harirathod/cinema-booking-application.git
```

2. Start the application with the following command:
```py
java -jar -Dpassword="<database-password>" -Dusername="<database-username>" cinema-booking-application/out/artifacts/cinema_jar/cinema.jar
```
> **Note:** Please make sure you have the JDK (Java Development Kit) installed.
>  
> **Note 2:** The user must provide the database password and username at the CLI. Typically, the customer doesn't provide database credentials when booking movie tickets, but this aspect was not a key focus of my application. Hence, I have intentionally simplified the process by requesting for credentials always.

And you're done! You can now try out the cinema booking application via the terminal!

## How the Project Works
##### TODO
Any tickets booked by the user are written to "tickets.ser", so are stored persistently. These tickets can be viewed with the 'basket' command. 

## Project Status

This project is completed.
