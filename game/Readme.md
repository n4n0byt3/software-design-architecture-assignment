# Simple Frustration – Clean Architecture Simulation

This project implements a console-based simulation of the **Simple Frustration** board game using **Clean Architecture / Ports & Adapters** and several classic **object-oriented design patterns** (Strategy, State, Observer, Decorator, Bridge-style abstractions).

The code is organised into three main layers:

- `domain` – pure game rules and entities (no framework dependencies)
- `usecase` – application orchestration (Play / Replay game)
- `infrastructure` – Spring Boot wiring, console I/O, file persistence

Java: **25**  
Framework: **Spring Boot 3.5.x**  
Build tool: **Maven**

---

## 1. Game Overview

### 1.1 Board and Players

The game board is modelled by `Board`:

- A **shared main ring** of positions `1..mainSize`
- A **tail** of positions per player (`tailSize`), e.g.:
    - Small board: `mainSize = 18`, `tailSize = 3`
    - Large board: `mainSize = 36`, `tailSize = 6`

Logical progress for a player is tracked as an integer:

- `0` = **Home**
- `1..(mainSize - 1)` = main ring
- `mainSize..endProgress()` = tail
- `endProgress()` = last tail square / **End**

Key class:

- `Player`
    - `name` (e.g. `"Red"`, `"Blue"`)
    - `homeIndex` on main ring (e.g. 1, 10, 19, 28)
    - `colourLetter` for tail labels (e.g. `"R"`, `"B"`)
    - `progress` (0..`board.endProgress()`)
    - `turnsTaken` (counted by the **Game** / **State** layer)

`Board.labelFor(Player, progress)` converts the abstract `progress` into human-readable labels such as:

- `Home (Position 1)`
- `Position 10`
- `Tail Position R1`
- `R3 (End)`

This matches the example output from the lectures.

---

## 2. Running the Application

The app is a Spring Boot console application.

### 2.1 Build

```bash
mvn clean package
