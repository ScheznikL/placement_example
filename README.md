# AI-Powered 3D Model Generation Mobile App

A mobile application for generating and visualizing three-dimensional models from text descriptions and photographs using artificial intelligence.

---

## Requirements

The application must:

- Generate accurate 3D model descriptions from user input using AI
- Support model creation from both **text descriptions** and **images** (including photographs)
- Allow users to manage and browse their created models (view, delete, categorize)
- Display models in a **3D scene** and via **Augmented Reality (AR)**
- Follow modern mobile application development principles

---

## Tech Stack & Tools

| Tool / Service | Purpose |
|---|---|
| **Meshy AI** | AI-powered 3D model generation API (text-to-3D and image-to-3D) |
| **OpenAI GPT API** | Conversational AI assistant for refining model descriptions |
| **Firebase Authentication** | User authentication and session management |
| **Firestore** | Flexible, scalable NoSQL database for mobile/web (Google Cloud) |
| **Amazon S3** | Object storage for 3D model assets |
| **Retrofit** | HTTP client library — maps REST API endpoints to Java interfaces |
| **Hilt** | Dependency injection framework |
| **Filament + ARCore** | 3D rendering and Augmented Reality display |

---

## Architecture

The app follows a clean architecture pattern with the following layers:

```
ChatScreen → ChatScreen ViewModel → SendUseCase → GPT API RepositoryImpl → GPT API
```

Key components:
- **ViewModel** — manages UI state and lifecycle
- **UseCase** — encapsulates business logic (e.g., sending messages, generating models)
- **Repository** — abstracts data sources (remote APIs, Firestore, S3)

---

## AI Model Description Flow

The GPT assistant guides the user through a structured conversation to produce an accurate 3D model prompt.

### System Prompt Behavior

The assistant is configured with the following logic:

- Asks targeted questions about the object (color, size, shape, material, style, quality)
- On user input containing `END` → immediately outputs the **final description**
- On user input containing `NEXT` → continues asking refinement questions
- Final response always begins with: `FINAL object is ...`

### Style & Quality Options Suggested to Users

**Style:** fantasy, cartoon, sci-fi, futurist, realistic, ancient, elegant, ultra realistic, trending on artstation, masterpiece, cinema 4d, unreal engine, octane render

**Quality:** highly detailed, high resolution, highest quality, best quality, 4K, 8K, HDR, studio quality

---

Supported output formats: **GLB**, **FBX**, **USDZ**

---

## Core Features

### 3D Model Creation
- Text-to-3D via Meshy AI (prompted through GPT assistant)
- Image/photo-to-3D via Meshy AI

### Model Management
- View models in a list with category filtering
- Delete models
- Browse model history

### Model Display
- **3D Scene** — interactive 3D viewer powered by Filament
- **AR Mode** — place models in real-world environments via ARCore

### Model Refinement
- Post-generation model improvement workflow

### User Authentication & Sync
- Sign in via Firebase Authentication
- User data and model metadata synced with Firestore