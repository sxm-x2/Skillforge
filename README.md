# Skillforge

A Kotlin Android application built as part of the Clickretina Android Developer Take-Home Assignment.

The app is built using Jetpack Compose and follows the provided UI designs. It consumes the provided API to display categories, courses, course details, and lesson information.

---

## Features

- Home screen displaying:
  - Categories
  - Popular courses
- Course Detail screen
  - Hero section
  - Instructor information
  - Course details
  - Lesson list
- Lesson Player screen
  - Video player UI using ExoPlayer
  - Lesson list
  - Notes & Resources tabs
- Retrofit API integration
- Coil image loading
- Loading state
- Error state with retry
- MVVM architecture
- Navigation Component
- Unit Test included

---

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- MVVM
- Retrofit
- Kotlin Coroutines
- Kotlin Flow / StateFlow
- Coil
- AndroidX Navigation Compose
- Media3 ExoPlayer
- JUnit

---

## Project Structure

```
com.example.skillforge

├── data
│   ├── remote
│   │   ├── api
│   │   ├── dto
│   └── repo
│
├── presentation
│   ├── home
│   ├── course
│   ├── lesson
│   └── navigation
│
├── ui
│   └── theme
│
├── util
│
└── MainActivity.kt
```

---

## API

The application uses the API provided in the assignment.

The API returns:

- Categories
- Courses
- Lessons
- Instructor details

---

## Notes

The provided API does not contain valid image assets and playable video URLs for the UI shown in the mockups.

Therefore:

- Images are loaded directly from the API whenever available.
- Empty or invalid media is handled gracefully without crashing the application.
- No mock or hardcoded media assets were added.

---

# AI Usage

This assignment was completed using AI as requested.

### AI Tools Used

- Gemini (Android Studio Agent)
- ChatGPT (GPT-5.5)

---

## Example Prompts

### Prompt 1

> Generate the complete Skillforge Android application using Kotlin, Jetpack Compose, MVVM, Retrofit, Coil and Navigation Compose. Follow the provided UI designs exactly and use the supplied API for all data.

---

### Prompt 2

> Review the existing project and improve the UI to more closely match the provided mockups. Keep the architecture unchanged, improve spacing, typography, cards, navigation, loading/error states, and overall Compose code quality.

---

### Prompt 3

> Refactor only the necessary files to improve readability, reduce recomposition, simplify state management, and remove duplicate Compose code without changing functionality.

---

## What AI Got Right

- Generated the overall project structure.
- Helped scaffold Compose UI quickly.
- Generated Retrofit models and networking.
- Implemented navigation between the three required screens.
- Reduced development time significantly.

---

## What AI Got Wrong

- Some layouts did not exactly match the provided mockups.
- A few spacing and typography values required manual adjustment.
- The lesson player initially attempted to play invalid media URLs without handling empty video URLs properly.
- Minor Compose UI refinements and code cleanup were completed manually.

---

## Manual Improvements

- Refined UI spacing.
- Improved Compose layouts.
- Improved navigation.
- Improved loading/error handling.
- Fixed multiple UI inconsistencies.
- Reviewed generated code before submission.

---



---

Thank you for reviewing my submission.
