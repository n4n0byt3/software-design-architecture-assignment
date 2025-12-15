# Simple Frustration – Clean Architecture Simulation (Java 25)

This project is a **console-based simulation** of the *Simple Frustration* board game. It is implemented in **Java 25** using **Clean Architecture (Ports & Adapters)** and a set of well-chosen **object-oriented design patterns**.

The goal is to demonstrate **high-quality software design** (maintainability, testability, extensibility, clear separation of concerns) rather than to build a GUI or a “production” game.

---

## Technology stack

- **Language:** Java 25
- **Framework:** Spring Boot 3.5.x (used *only* for Dependency Injection, i.e., wiring objects together)
- **Build tool:** Maven
- **Testing:** JUnit 5 (via `spring-boot-starter-test`)
- **Serialisation:** Jackson (via `spring-boot-starter-json`)

> **Terminology (brief):**  
> **Dependency Injection (DI)** means the application creates and provides objects where needed, instead of classes constructing their own dependencies internally. This reduces coupling and improves testability.

---

## What the program does (runtime behaviour)

When you run the program, it:

1. Reads CLI flags to decide configuration (board size, players, rule variations, dice)
2. Creates a game using a factory (builds board, players, dice, and rules)
3. Automatically alternates turns until someone wins
4. Prints each turn:
   - player name
   - turn number (counting only *non-forfeited* turns)
   - dice roll value
   - position before and after the move (labels match the spec)
   - HIT / overshoot messages when relevant
5. Prints a winner summary:
   - winner name
   - total turns across all players
   - turns taken by the winner
6. Demonstrates correct `GameOver` behaviour by attempting extra turns after the game ends (prints `"Game over"` per extra attempt)
7. Saves the completed game for replay
8. Supports replaying a saved game deterministically (same output)

---

## Build and run

### Build
```bash
mvn clean package
Run (default)
mvn spring-boot:run
Default configuration:

Small board (main ring = 18, tail = 3)

2 players (Red, Blue)

Two dice (2–12)

Basic rules only

CLI flags
Flag	Description
--single	Use a single die (1–6) instead of two dice (2–12).
--exact-end	Player must land exactly on END; overshoot forfeits and player stays in place.
--forfeit-on-hit	If a move would land on another player on the main ring, the turn is forfeited and player stays in place.
--large-board	Use the large board (main ring = 36, tail = 6).
--players=<n>	Number of players (2 or 4). If --large-board and <n> < 4, defaults to 4.
--list-saves	List all saved games with configuration summary.
--replay=<uuid>	Replay a saved game by its id.

Example commands
mvn spring-boot:run
mvn spring-boot:run -- --single --exact-end
mvn spring-boot:run -- --exact-end --forfeit-on-hit
mvn spring-boot:run -- --large-board --players=4 --single
mvn spring-boot:run -- --list-saves
mvn spring-boot:run -- --replay=<uuid>
Features implemented (and how they map to the specification)
1) Basic game (core functionality)
Automatic alternating turns

Dice roll each turn

Player movement around main ring then into a tail

Correct labels:

Home (Position X)

Position Y

Tail Position R1

R3 (End) / R6 (End) depending on board size

End-of-game output includes winner and turn counts

2) Functional variations
All required variations are implemented and can be combined (e.g., --exact-end --forfeit-on-hit --single).

Single die (--single)

Exact end required (--exact-end)

Forfeit on HIT (--forfeit-on-hit)

3) Advanced features
Large board + 4 players (--large-board --players=4)

Explicit game lifecycle states (Ready → InPlay → GameOver)

Save & replay using recorded dice roll sequences

Comprehensive unit testing

Architecture: Clean Architecture (Ports & Adapters)
What Clean Architecture is (short explanation)
Clean Architecture is a way of organising code so that:

Business logic (game rules) is isolated from frameworks and I/O

Dependencies point inwards (outer layers depend on inner layers, not the other way around)

You can swap UI/persistence without changing core logic

Terminology (brief):
A port is an interface (an abstraction) used by the core to communicate outward.
An adapter is a concrete implementation of that interface (e.g., console output).

Why Clean Architecture was chosen
The assignment emphasises software design quality, SOLID, and patterns

Console output and file storage are implementation details; game rules are “business logic”

We want the game engine to be testable without Spring, without the file system, and without the console

Why not MVC (and similar UI-driven architectures)
MVC typically fits interactive applications with user-driven controllers and views. Here:

There is no GUI

The “controller” logic is simple CLI parsing

The focus is on a rules engine that must remain stable and testable

MVC would add conceptual overhead without providing real value for this problem.

Clean Architecture diagram
mermaid
Copy code
flowchart LR
  subgraph Domain["Domain (core business rules)"]
    Game
    Board
    Player
    Rules
    GameState
  end

  subgraph UseCase["Use Case (application orchestration)"]
    PlayGameUseCase
    ReplayGameUseCase
    GameFactory
    GameOutputPort
    GameSaveRepository
    GameEventMediator
  end

  subgraph Infrastructure["Infrastructure (I/O, frameworks, wiring)"]
    CommandLineGameRunner
    ConsoleOutputAdapter
    JsonLinesGameSaveRepository
    ConsoleGameEventMediator
    AppConfig
  end

  UseCase --> Domain
  Infrastructure --> UseCase
Diagram fallback (for non-Mermaid viewers):

Domain: core game logic (Game, Rules, Board, etc.), no I/O, no Spring

Use Case: coordinates gameplay and replay using domain classes and abstract interfaces (ports)

Infrastructure: CLI parsing, console printing, JSON file persistence, Spring configuration
Dependencies flow inward: Infrastructure → UseCase → Domain.

Domain model design (board + movement)
Abstract “progress” model
Players track a single integer called progress:

0 = Home

1..mainSize-1 = movement on main ring

mainSize..endProgress = tail movement

endProgress = mainSize + tailSize - 1

This allows the board logic to be expressed cleanly without creating dozens of “square” objects.

Why this design
Pros

Simple and efficient representation

Works for both small and large boards

Easy to test boundaries (home, ring, tail, end)

Cons / trade-off

Not as visually “board-like” as modelling each square as an object
(but unnecessary for console output and would increase complexity)

Strategy pattern: Dice behaviour
What it is (brief)
The Strategy pattern allows you to swap an algorithm at runtime by programming to an interface.

How it’s implemented
DiceShaker is the strategy interface: int shake()

Implementations:

RandomSingleDiceShaker → returns 1..6

RandomDoubleDiceShaker → sums two single rolls (2..12)

FixedSeqShaker → deterministic sequence for replay/testing

Why Strategy was chosen
Dice behaviour varies independently from game rules

Avoids hardcoded conditionals (if singleDie then ... else ...) across the codebase

Greatly improves testability (fixed dice sequences produce deterministic game outcomes)

Why not alternatives
Conditionals: increase complexity and violate OCP (Open/Closed Principle) as you add more dice types

Inheritance inside Game: would couple Game to dice details and reduce flexibility

Decorator pattern: Rule variations
What it is (brief)
The Decorator pattern wraps an object to add behaviour without modifying the original class.

How it’s implemented
Rules is an interface representing “how moves are applied”

BasicRules implements the core behaviour

Variations wrap Rules:

ExactEndDecorator blocks overshoot and returns a “forfeit” result

ForfeitOnHitDecorator blocks moves that would cause a HIT

Decorators are composed in GameFactory:

start with new BasicRules()

optionally wrap with ExactEndDecorator

optionally wrap with ForfeitOnHitDecorator

Why Decorator was chosen
Variations must be combinable without new subclasses for each combination

It keeps the base rules stable and readable

It is aligned with OCP: add new rule variations without modifying existing ones

Why not alternatives
Boolean flags in one big Rules class: leads to “branching complexity” (lots of if/else), harder to test and extend

Inheritance (BasicRulesExactEnd, BasicRulesHit, BasicRulesExactEndHit, …): causes subclass explosion

State pattern: Game lifecycle
What it is (brief)
The State pattern encodes different “modes” of an object as separate classes rather than as if/else branches.

How it’s implemented
Game holds a GameState:

ReadyState → first playTurn() transitions to InPlayState

InPlayState → normal gameplay, checks for win, transitions to GameOverState

GameOverState → returns a sentinel MoveResult with note "Game over"

stateDiagram-v2
    [*] --> Ready
    Ready --> InPlay : first playTurn()
    InPlay --> GameOver : win condition met
Diagram fallback:
Ready is a startup state. The first turn moves to InPlay.
When a player reaches END, the game enters GameOver.
Any further playTurn() calls return "Game over".

Why State pattern was chosen
Prevents illegal logic like “playing turns before the game starts”

Avoids boolean flag combinations (e.g., started && !finished) which can become error-prone

Makes lifecycle behaviour explicit and testable

Observer pattern: Output and event reporting
What it is (brief)
The Observer pattern lets one object notify other objects when events happen without tightly coupling them.

How it’s implemented
Domain emits events via observer interfaces:

GameStateObserver

PlayerTurnObserver

GameFinishedObserver

GameOutputPort extends these and acts as a “presentation port”

ConsoleOutputAdapter implements GameOutputPort and prints to the console

Why Observer was chosen
Keeps domain pure (no System.out.println in core logic)

Allows alternative presenters (e.g., file logger, GUI) without changing domain

Enables tests to attach “silent” outputs to avoid console noise

Why not direct printing from domain
Direct printing would violate:

SRP (domain would manage both rules and output)

DIP (domain would depend on concrete output mechanisms)

Save & Replay design
Where saves are stored
Saved games are written to:

<project-root>/target/saves/games.json
in JSON Lines (NDJSON) format: one JSON object per line.

Terminology (brief):
NDJSON (JSON Lines) is a format where each line is a separate JSON object. It is easy to append to (append-only logs) and easy to stream/read incrementally.

What is saved
A GameSave stores:

board sizes

player count (2 or 4)

enabled variations

recorded dice roll sequence

Why this replay approach was chosen
Instead of saving the entire in-memory state of the game, we save:

configuration + rolls

Then replay:

rebuild a deterministic game

feed the exact same rolls via FixedSeqShaker

Benefits

Very small and robust storage

Replay is deterministic and uses the real game engine

Avoids bugs caused by manual playback scripting

Why not full state snapshots

more complex to serialize reliably

more fragile if internal models change

less aligned with a “rules engine + deterministic input” design

Turn counting and forfeits (important design detail)
Turns are counted in InPlayState, not in Rules.

Why?
A forfeit (e.g., overshoot-forfeit or hit-forfeit) should not increment a player’s “turns taken” counter.

If Rules incremented turns, decorators would have to “undo” turn counts, which is messy and error-prone.

By counting turns in the game loop:

rules focus only on move outcomes

the orchestration layer decides whether a turn should count

forfeits correctly skip counting

This is a deliberate separation of responsibilities (SRP).

Testing strategy
What is tested
Domain behaviour:

Board mapping/labels

Hit detection

Basic rules

Decorated rule behaviours

State transitions and game lifecycle

Use case behaviour:

Play use case runs a game to completion and saves a snapshot

Replay use case loads and replays deterministically

Why this testing approach
Domain logic is the “core business rules” and should have the highest confidence

Use cases are tested with test doubles to isolate orchestration

Console printing is treated as an adapter concern (not business logic)

What is intentionally not tested
Exact console formatting (presentation detail)

Spring wiring beyond a context smoke test

This matches industry practice: test behaviour and invariants rather than framework internals.

Design patterns used (summary)
Pattern	Where	Why it improves quality
Strategy	DiceShaker	Swappable dice behaviour, clean runtime configuration, testability
Decorator	ExactEndDecorator, ForfeitOnHitDecorator	Combinable variations, avoids branching complexity, OCP
State	ReadyState, InPlayState, GameOverState	Eliminates illegal lifecycle states, clearer control flow
Observer	GameOutputPort + observers	Keeps domain free of I/O, replaceable output, DIP
Factory	GameFactory	Centralises construction logic, avoids duplicate wiring logic
Singleton	Random dice	Reuse stateless strategies, consistent API

SOLID principles (with brief explanation)
SRP (Single Responsibility Principle): each class has one reason to change
(e.g., rules compute moves; output prints; repository persists)

OCP (Open/Closed Principle): behaviour extended via decorators/strategies rather than editing core classes
(add a new rule decorator without changing BasicRules)

LSP (Liskov Substitution Principle): decorators still behave as valid Rules implementations
(they can replace any other Rules wherever a Rules is expected)

ISP (Interface Segregation Principle): ports are focused interfaces rather than one “god interface”
(output and persistence are separate concerns)

DIP (Dependency Inversion Principle): use cases depend on abstractions (ports), not concrete implementations
(PlayGameUseCase depends on GameSaveRepository, not the JSON file class)

Critical evaluation & trade-offs
What this design does well
Strong separation between domain logic and I/O

Variations implemented in a clean, composable way
Deterministic replay using recorded inputs
High testability with minimal mocking complexity

Trade-offs accepted
More classes than a quick procedural solution

Slightly higher upfront conceptual complexity

These trade-offs are justified because the assessment explicitly rewards:

SOLID
patterns
architectural conformance
well-justified design decisions