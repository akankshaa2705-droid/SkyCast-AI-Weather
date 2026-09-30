# 🌦️ SkyCast AI — Intelligent Weather Assistant

> A modern AI-powered weather application built with Java and Spring Boot that combines real-time weather data, AI-powered weather conversations, and global weather news in one futuristic interface.

## 🚀 Live Demo

**Try SkyCast AI:**
https://skycast-ai-weather.onrender.com

## ✨ Features

* 🌍 Search weather for cities worldwide
* 🌡️ Real-time temperature and weather conditions
* 💧 Humidity, wind speed and feels-like temperature
* 🤖 AI-powered weather chatbot
* 📰 Global weather news
* ☁️ Animated futuristic weather interface
* 📱 Responsive design for desktop and mobile
* 🔐 API keys protected using environment variables
* ⚡ REST APIs built with Spring Boot

## 🛠️ Tech Stack

### Backend

* Java 21
* Spring Boot
* REST APIs
* Maven

### APIs & AI

* OpenWeather API
* Google Gemini API
* News API

### Frontend

* HTML5
* CSS3
* JavaScript
* Responsive UI
* Glassmorphism / futuristic UI

### Deployment

* Docker
* Render
* GitHub

## 🧠 How It Works

```text
User
  ↓
SkyCast AI Frontend
  ↓
Spring Boot REST API
  ↓
 ┌───────────────┬───────────────┬───────────────┐
 ↓               ↓               ↓
OpenWeather    Gemini AI       News API
 ↓               ↓               ↓
Weather Data   AI Response     Weather News
 └───────────────┴───────────────┘
                 ↓
          SkyCast AI Interface
```

## 💬 AI Weather Assistant

The AI assistant receives the current weather information for the selected city and uses it to answer weather-related questions.

For example, users can ask:

* "What's the weather like?"
* "How does it feel outside?"
* "Do I need an umbrella right now?"
* "Is it humid?"

The assistant is designed to distinguish between **current weather information** and information that is not available from the current-weather API.

## 📊 Weather Information

SkyCast AI displays:

* Current temperature
* Feels-like temperature
* Humidity
* Wind speed
* Weather condition
* City information

## 📰 Global Weather News

The application also collects weather-related news and presents relevant global weather stories through the application's news section.

## 🔐 Environment Variables

API keys are **not stored directly in the source code**.

The application uses environment variables:

```text
WEATHER_API_KEY
GEMINI_API_KEY
NEWS_API_KEY
```

This keeps sensitive credentials separate from the public GitHub repository.

## 🐳 Run Locally

### 1. Clone the repository

```bash
git clone https://github.com/akankshaa2705-droid/SkyCast-AI-Weather.git
```

### 2. Open the project

Open the project in IntelliJ IDEA.

### 3. Configure environment variables

Add your own API keys:

```text
WEATHER_API_KEY=your_openweather_key
GEMINI_API_KEY=your_gemini_key
NEWS_API_KEY=your_newsapi_key
```

### 4. Run the application

Run:

```text
AiWeatherAppApplication
```

Then open:

```text
http://localhost:8080
```

## 📁 Project Structure

```text
SkyCast-AI-Weather/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/aiweatherapp/
│       │       ├── controller/
│       │       └── service/
│       │
│       └── resources/
│           └── static/
│               ├── index.html
│               ├── style.css
│               └── script.js
│
├── Dockerfile
├── pom.xml
├── mvnw
└── README.md
```

## 🎯 Project Goal

SkyCast AI was created to demonstrate how a Java/Spring Boot backend can integrate multiple external APIs and AI services into a single user-friendly application.

The project focuses on:

* Backend development
* REST API integration
* AI integration
* API security
* Frontend-backend communication
* Cloud deployment
* Docker-based deployment

## 👩‍💻 Developer

**Akanksha Singh**

BSc IT Student | Java Backend Developer | Spring Boot Learner

### Interests

* Java
* Backend Development
* Spring Boot
* SQL
* REST APIs
* Artificial Intelligence
* Data Structures & Algorithms

## ⭐ Project

If you find this project interesting, feel free to explore the repository and try the live application.

**Built with Java + Spring Boot + AI + curiosity.**
