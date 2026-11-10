# HealthCheckMonitor![Build Status](https://github.com/yourusername/HealthCheckMonitor/actions/workflows/build.yml/badge.svg)![License](https://img.shields.io/badge/license-MIT-blue)

**HealthCheckMonitor** is a lightweight, production‑ready Java service that periodically checks the health of configurable HTTP endpoints and exposes the results via a simple REST API. 
It demonstrates modern Java practices: immutable data models, dependency injection, robust error handling, logging with SLF4J, and comprehensive unit testing.

## Table of Contents
- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Service](#running-the-service)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [License](#license)

## Features
- Configurable list of health‑check URLs (via `application.properties` or environment variables)
- Concurrent health checks with configurable timeout
- Detailed JSON response with status, response time, and error messages
- Structured logging (JSON layout compatible with ELK)
- Graceful shutdown handling
- 100 % unit test coverage for core logic

## Architecture

<!-- minor polish -->
