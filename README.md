![Build Status](https://github.com/Leonope/ProjektMemory/actions/workflows/.travis.yml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/Leonope/ProjektMemory/badge.svg?branch=main)](https://coveralls.io/github/Leonope/ProjektMemory?branch=main)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/a864d8e9b58248f695d6666a4ea45128)](https://app.codacy.com/Leonope/ProjektMemory/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)
![GitHub commit activity](https://img.shields.io/github/commit-activity/w/Leonope/ProjektMemory)
![GitHub last commit](https://img.shields.io/github/last-commit/Leonope/ProjektMemory)
![Github Created At](https://img.shields.io/github/created-at/Leonope/ProjektMemory)


# Scala Memory Game

This is a Scala-based memory game implemented as a desktop GUI application using Scala Swing. It allows players to find matching pairs of cards and offers a simple, intuitive user interface.

## Features

- Classic memory game mechanics
- Simple and intuitive GUI
- Implemented in Scala with Scala Swing
- Docker support for easy deployment

## Requirements

- Java JDK 11 or higher
- sbt (Scala Build Tool)
- Docker (optional, for Docker-based execution)

## Installation and Execution
```sh
sbt compile
sbt test
sbt run
```

### Local Docker Execution
```sh
docker build -t scala-game .
set DISPLAY=host.docker.internal:0.0
docker run -e DISPLAY=host.docker.internal:0.0 -v /tmp/.X11-unix:/tmp/.X11-unix scala-game
```

1. **Clone the repository:**
   ```sh
   git clone https://github.com/Leonope/ProjektMemory.git
