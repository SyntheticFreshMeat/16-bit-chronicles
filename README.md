# 🎮 16-Bit Chronicles

A retro-inspired narrative web adventure built with **Spring Boot (Java)** and **vanilla HTML/CSS/JavaScript**.

This project simulates a classic 16-bit JRPG-style experience featuring dynamic decisions, random events, an inventory system, and persistent save/load functionality using Hibernate.

---

## 🧠 Project Overview

16-Bit Chronicles is a browser-based narrative RPG where the player explores different locations, makes decisions, gains experience, collects items, and survives random events.

The goal of this project was to:

- Practice backend architecture with Spring Boot
- Implement clean DTO-based REST communication
- Integrate Hibernate (JPA) for persistence
- Design a retro UI without frameworks
- Apply separation of concerns between layers

---

## ⚙️ Tech Stack

- **Java 21**
- **Spring Boot**
- **Spring Data JPA (Hibernate)**
- **H2 Database**
- **Maven (Wrapper)**
- **HTML / CSS / Vanilla JavaScript (Fetch API)**

---

## 🏗 Architecture

The project follows a layered structure:


######
controller → handles HTTP endpoints
service → contains game logic
model → core domain classes
entity → JPA persistence layer
repository → database access
dto → API communication objects
config → game constants
######


Key features:

- Dynamic decisions per location
- Random event system with probability handling
- Player inventory system (backpack with capacity)
- Item usage logic (e.g., healing herbs)
- Game Over handling
- Save and Load functionality via Hibernate
- Custom player name at game start

---

## 🎮 Gameplay Features

- 🗺 Multiple locations
- 🎲 Randomized events
- ❤️ Health & experience system
- 🎒 Inventory with item stacking
- 💾 Save & resume game
- ☠ Game Over state handling

---

## 🚀 Running the Project

Clone the repository:

```bash
git clone https://github.com/SyntheticFreshMeat/16-bit-chronicles.git
cd 16-bit-chronicles


Run using Maven Wrapper:
mvnw.cmd spring-boot:run

Then open;
http://localhost:8080/index.html


################################################################################
Why This Project?

This project demonstrates:

Backend-driven state management

Clean REST communication

Basic game engine logic

Practical Hibernate usage

Full-stack integration without heavy frontend frameworks

It was built as a learning project to consolidate backend fundamentals while keeping the frontend lightweight and controlled.




Future Improvements (Optional)

Authentication per player

Multiple save slots

More item types and effects

Refactoring into a more modular architecture

Deployment to cloud platform


                            ####################
                            ####   Author  #####

Developed by Josep
Backend-focused developer passionate about clean architecture and retro-inspired systems.