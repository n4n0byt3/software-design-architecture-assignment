Simple Frustration – Clean Architecture Simulation

This project is a console-based simulation of the Simple Frustration board game, implemented in Java using Clean Architecture (Ports & Adapters) and multiple object-oriented design patterns to support rule/board variations and advanced features.

Java: 25

Framework: Spring Boot 3.5.x (DI container + dependency management)

Build: Maven

Testing: JUnit 5 (via spring-boot-starter-test)

Contents

1. What this program does

2. How to build and run

2.1 Build

2.2 Run

2.3 CLI flags

2.4 Example commands

2.5 Run tests

3. Features implemented

3.1 Basic game functional spec

3.2 Functional variations

3.3 Advanced features

4. Design & architecture

4.1 Clean Architecture layers

4.2 Key domain model

4.3 Design patterns used

4.4 SOLID principles

4.5 Dependency Injection

5. Save & replay format

6. Unit testing approach

7. Evaluation

8. What we used from lectures/labs (and what we didn’t)

9. Project structure

10. Submission checklist

1. What this program does

A simulation run:

Sets up the board and player pieces

Alternates turns automatically

Rolls either one die or two dice

Applies the game rules and optional variations

Prints turn-by-turn output:

current player

roll value

position before and after

running turns taken by that player

Prints end-of-game summary:

winning player

total turns taken by all players

It also supports saving completed games and replaying them later.

2. How to build and run
   2.1 Build
   mvn clean package

2.2 Run

Option A (run from Maven / Spring Boot):

mvn spring-boot:run -- <flags>


Option B (run the packaged JAR):

java -jar target/game-0.0.1-SNAPSHOT.jar <flags>


Flags are optional. Running with no flags uses the default configuration (small board, 2 players, two dice, basic rules).

2.3 CLI flags
Flag	Meaning
--single	Use single die (1–6) instead of two dice (2–12).
--exact-end	Must land exactly on END to win; overshoot = forfeit (stay in place).
--forfeit-on-hit	If you would HIT another player on the main ring, your move is forfeited (stay in place).
--large-board	Use large board: main ring = 36, tail = 6.
--players=<n>	Choose number of players. Supported values: 2 or 4. If --large-board is used and <n> is less than 4, it defaults to 4.
--list-saves	List all saved games (reads the save file and prints summary lines).
--replay=<uuid>	Replay a previously saved game by id.
2.4 Example commands

Default run (small board, 2 players, two dice, basic rules):

mvn spring-boot:run


Single die + exact-end:

mvn spring-boot:run -- --single --exact-end


Forfeit on hit + exact-end (variations can be combined):

mvn spring-boot:run -- --exact-end --forfeit-on-hit


Large board with 4 players + single die:

mvn spring-boot:run -- --large-board --players=4 --single


List all saved games:

mvn spring-boot:run -- --list-saves


Replay a specific saved game:

mvn spring-boot:run -- --replay=2f6bbd7c-3df9-4c77-9a8d-0b9e9e1d9a12

2.5 Run tests
mvn test

3. Features implemented
   3.1 Basic game functional spec

Implemented outputs:

Alternating player turns until a winner occurs

Each turn prints:

current player

roll

from-position and to-position

running turns taken for the current player

End of game prints:

winner

total turns across all players

Positions printed match the expected “Home (Position X)”, “Position Y”, “Tail Position R1”, “R3 (End)” style.

3.2 Functional variations

All required variations are implemented and are composable (any combination can run together):

Single Die (--single)
Implemented via a dice Strategy (RandomSingleDiceShaker) selected by configuration.

Exact End (--exact-end)
Implemented as a Rules decorator (ExactEndDecorator) that enforces “exact landing” without modifying the base rules.

Forfeit on HIT (--forfeit-on-hit)
Implemented as a Rules decorator (ForfeitOnHitDecorator) that checks for a HIT before delegating.

3.3 Advanced features

Large board with 4 players (--large-board and --players=4)

Main ring: 36

Tail: 6

Home positions per spec: Red=1, Blue=10, Green=19, Yellow=28

Game States (State pattern)
Game transitions through:

Ready → InPlay → GameOver

Transitions are printed as:

Game state Ready -> InPlay

Game state InPlay -> GameOver

In GameOver, additional playTurn() calls return a sentinel result with note "Game over" (and the console prints Game over once).

Save + Replay
At game end, the simulation stores:

configuration (board, players, variations)

roll sequence

Replay re-runs the game deterministically using the stored rolls.

Unit testing
A suite of JUnit tests demonstrates that the design is testable at both:

domain level (rules, hit detection, state machine)

use case level (play + replay behaviour with test doubles)

4. Design & architecture
   4.1 Clean Architecture layers

Goal: keep the game mechanics independent of technology (console, files, Spring), while still allowing an assembled runnable application.

domain
Pure rules/entities/state machine. No Spring, no file IO, no console IO.

usecase
Orchestration of “Play” and “Replay” use cases. Depends on domain. Uses ports.

infrastructure
Concrete adapters for:

Console output

Save repository (NDJSON file)

Spring wiring / CLI runner

Architecture diagram (Mermaid)
flowchart LR
subgraph Domain
G[Game]
B[Board]
R[Rules]
S[GameState]
end

subgraph UseCase
PUC[PlayGameUseCase]
RUC[ReplayGameUseCase]
OP[GameOutputPort]
SR[GameSaveRepository]
F[GameFactory]
M[GameEventMediator]
end

subgraph Infrastructure
CLI[CommandLineGameRunner]
CO[ConsoleOutputAdapter]
JS[JsonLinesGameSaveRepository]
MED[ConsoleGameEventMediator]
CFG[AppConfig (Spring Beans)]
end

PUC --> G
RUC --> G
PUC --> OP
PUC --> SR
PUC --> F
PUC --> M
RUC --> OP
RUC --> SR
RUC --> F
RUC --> M

CO -.implements.-> OP
JS -.implements.-> SR
MED -.implements.-> M

CLI --> PUC
CLI --> RUC
CLI --> SR

CFG --> CO
CFG --> JS
CFG --> MED
CFG --> PUC
CFG --> RUC


Dependency rule: Infrastructure depends on UseCase + Domain, never the other way around.

4.2 Key domain model

Board
Holds main ring size + tail size, converts abstract progress to printed labels.

Player
Holds name, home index, colour letter, progress, and turns taken.

Game
Owns:

players (via TurnOrder)

rules strategy

dice strategy

state machine

event observers

timeline of results

MoveResult
Immutable record of a turn outcome (from/to progress, hit/overshoot/win flags, victim info).

4.3 Design patterns used

Strategy

Dice strategy: DiceShaker

RandomSingleDiceShaker

RandomDoubleDiceShaker

FixedSeqShaker (deterministic for replay/tests)

Rules strategy: Rules

BasicRules implements baseline rules

Decorator

ExactEndDecorator(Rules inner)

ForfeitOnHitDecorator(Rules inner)

RecordingDiceShaker(DiceShaker inner) records rolls for saving

This supports the requirement: “a simulation should be able to run with any combination of variations”.

State

ReadyState

InPlayState

GameOverState

Observer

GameObserver (composite)

GameStateObserver

PlayerTurnObserver

GameFinishedObserver

The console adapter implements the output port and listens to game events.

4.4 SOLID principles

S — Single Responsibility
Examples:

Board maps progress → labels / absolute ring positions

BasicRules applies baseline movement rules only

CommandLineGameRunner only parses CLI and dispatches use cases

repository only persists/loads/list summaries

O — Open/Closed
Adding new rule variations does not require modifying BasicRules; we add decorators.

L — Liskov Substitution
Decorators implement Rules and can substitute anywhere a Rules is expected.

I — Interface Segregation
Observer interfaces are separated rather than a single “god listener”.

D — Dependency Inversion
Use cases depend on abstractions (GameOutputPort, GameSaveRepository, GameEventMediator) not concrete infrastructure classes.

4.5 Dependency Injection

Spring Boot is used as the DI container to assemble the application at runtime:

AppConfig provides @Bean definitions for:

output adapter

repository adapter

mediator adapter

use cases

factory

The domain layer is not wired by Spring (kept framework-free).

5. Save & replay format
   Storage location

Saved games are stored in:

target/saves/games.json


(Using target/ keeps saves within the project for marking convenience; this is acceptable for a prototype.)

Format

The file is NDJSON / JSON Lines:

Each line is one serialized GameSave object

Append-only: new completed games add a new line

What is saved

This implementation stores configuration + roll sequence, then replays using the real engine.

Saved fields:

board sizes

player count

enabled variations

recorded dice rolls

Replay:

loads GameSave

uses FixedSeqShaker to reproduce the roll sequence

runs the real game loop to reproduce identical output

6. Unit testing approach

Tests are split across:

Domain tests: rules, board mapping, hit detection, state machine lifecycle

Use case tests: play and replay orchestration using test doubles

Key points:

Domain tests run without Spring context (fast, deterministic)

Determinism is achieved via FixedSeqShaker

For use cases, repositories/mediators are provided as in-memory test doubles

JUnit version note: the project uses JUnit Jupiter (JUnit 5) transitively via spring-boot-starter-test. Exact artifact versions are managed by Spring Boot’s dependency management (BOM), so they remain consistent with the Spring Boot 3.5.x line.

7. Evaluation
   What went well

Variations are fully composable via decorators (no complex branching)

State machine is explicit and observable (required transitions printed)

Clean Architecture boundaries are respected:

domain has no framework dependencies

infrastructure is swappable (console/file could be replaced)

Save/replay is deterministic and simple to inspect due to NDJSON

Trade-offs / limitations

CLI parsing is intentionally minimal (manual scanning of args)

Saved game storage is append-only: no deletion/compaction (acceptable for coursework scope)

Output formatting is console-focused (no GUI by design)

Potential improvements (future work)

Richer CLI parsing (e.g., PicoCLI) — not used to keep dependencies minimal and follow “no extra required libs”

More detailed stats reporting (averages across many simulations)

More validations / clearer error messages on invalid CLI combos

8. What we used from lectures/labs (and what we didn’t)

This README references module topics by concept (“referencing by topic is fine”).

Used (and where)

Google Java style / readable code conventions
Applied: naming, formatting, defensive argument checks, clear JavaDoc.

Object-oriented decomposition
Responsibilities split across small focused classes (Board / Rules / Game / adapters).

Design patterns (Strategy/Decorator/State/Observer)
Used for variations, lifecycle states, swappable dice, and decoupled output.

Clean Architecture / Ports & Adapters
Domain is framework-free, use cases orchestrate, infrastructure holds adapters/wiring.

Spring Boot dependency injection
Used to assemble application components as required.

JSON serialization using Jackson
Implemented via spring-boot-starter-json and ObjectMapper.

Markdown (README.md)
Used to present the report-style explanation with tables and diagrams.

Mermaid diagrams
Included for architecture documentation.

JUnit unit testing + determinism
Tests use deterministic dice sequences to avoid randomness.

Not used (and why)

GUI frameworks / Web UI
Explicitly not required; assignment requests console output only.

Complex CLI libraries
Not required; scope is met with simple parsing and fewer dependencies.

Database persistence
Not required; file-based NDJSON is simpler and suitable for a prototype.

9. Project structure
   src/
   main/
   java/
   uk/ac/mmu/game/
   GameApplication.java

   uk/ac/mmu/game/domain/
   Board.java
   Player.java
   TurnOrder.java
   Game.java
   MoveResult.java
   HitInfo.java
   Rules.java
   BasicRules.java
   ExactEndDecorator.java
   ForfeitOnHitDecorator.java
   DiceShaker.java
   RandomSingleDiceShaker.java
   RandomDoubleDiceShaker.java
   FixedSeqShaker.java
   RecordingDiceShaker.java
   GameState.java
   ReadyState.java
   InPlayState.java
   GameOverState.java
   GameObserver.java
   GameStateObserver.java
   PlayerTurnObserver.java
   GameFinishedObserver.java

   uk/ac/mmu/game/usecase/
   PlayGameUseCase.java
   ReplayGameUseCase.java
   GameFactory.java
   GameSave.java
   GameSaveRepository.java
   GameOutputPort.java
   GameEventMediator.java

   uk/ac/mmu/game/infrastructure/
   AppConfig.java
   CommandLineGameRunner.java
   ConsoleOutputAdapter.java
   ConsoleGameEventMediator.java
   JsonLinesGameSaveRepository.java

test/
java/
uk/ac/mmu/game/...
uk/ac/mmu/game/domain/...
uk/ac/mmu/game/usecase/...