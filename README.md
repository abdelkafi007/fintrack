# FinTrack — Personal Finance & Budget Tracker

A modern, secure, and intelligent Personal Finance & Budget Tracker Android application built with Kotlin and Jetpack Compose.

![Android](https://img.shields.io/badge/Android-26+-green) ![Kotlin](https://img.shields.io/badge/Kotlin-2.1-purple) ![Compose](https://img.shields.io/badge/Jetpack_Compose-Material3-blue)

## ✨ Features

- **Transaction Tracking** — Add income, expenses, and transfers with categories, notes, and receipts
- **Multiple Accounts** — Cash, Bank, Credit Card, Savings with per-account balance tracking
- **Budget Management** — Monthly/weekly budgets per category with real-time progress and over-budget alerts
- **Savings Goals** — Set targets with deadlines and track progress with visual indicators
- **Analytics & Reports** — Income vs expense charts, category breakdown, daily spending trends
- **Swipe to Delete** — Swipe transactions to delete with undo support
- **Search & Filter** — Full-text search and type-based filtering
- **Dark Mode** — Full dark theme with dynamic color support (Android 12+)
- **Material Design 3** — Premium UI with glassmorphism cards, gradient accents, and smooth animations

## 🏗️ Architecture

```
Clean Architecture + MVVM
├── data/          # Room DB, DAOs, Repository Implementations
├── domain/        # Models, Repository Interfaces, Use Cases  
├── presentation/  # Compose UI, ViewModels, Navigation
└── core/          # DI Modules, Utilities, Extensions
```

## 🛠️ Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Kotlin 2.1 |
| UI | Jetpack Compose + Material Design 3 |
| Architecture | Clean Architecture + MVVM |
| DI | Hilt (Dagger) |
| Database | Room |
| Async | Kotlin Coroutines + StateFlow |
| Navigation | Jetpack Navigation Compose |
| Typography | Google Fonts (Outfit + Inter) |
| Build | Gradle 8.9 + Version Catalog |

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 35

### Setup
```bash
git clone <repo-url>
cd fintrack
./gradlew assembleDebug
```

### Run
```bash
./gradlew installDebug
# or open in Android Studio and click Run
```

## 📱 Screens

| Home | Transactions | Analytics | Budgets |
|------|-------------|-----------|---------|
| Dashboard with balance, accounts, recent transactions | Searchable list with swipe-to-delete | Charts and category breakdown | Progress bars with over-budget alerts |

## 📁 Project Structure

```
app/src/main/java/com/fintrack/
├── FinTrackApplication.kt          # Hilt Application
├── MainActivity.kt                 # Single Activity entry point
├── core/
│   ├── di/                         # DatabaseModule, RepositoryModule
│   └── utils/                      # CurrencyFormatter, DateFormatter
├── data/
│   ├── local/
│   │   ├── converter/              # Room TypeConverters
│   │   ├── dao/                    # Transaction, Category, Account, Budget, Goal DAOs
│   │   └── entity/                 # Room Entities
│   ├── mapper/                     # Entity ↔ Domain mappers
│   └── repository/                 # Repository implementations
├── domain/
│   ├── model/                      # Domain models + enums
│   ├── repository/                 # Repository interfaces
│   └── usecase/                    # All use cases
└── presentation/
    ├── navigation/                 # Screen routes + NavGraph
    ├── ui/
    │   ├── theme/                  # Colors, Typography, Theme
    │   ├── components/             # Reusable composables
    │   ├── home/                   # Dashboard
    │   ├── transactions/           # Transaction list + add/edit
    │   ├── budgets/                # Budget list + add
    │   ├── accounts/               # Account list + add
    │   ├── goals/                  # Goals list + add
    │   ├── analytics/              # Charts and reports
    │   ├── settings/               # App settings
    │   └── FinTrackApp.kt          # Root composable with bottom nav
    └── viewmodel/                  # All ViewModels

```

## 🔑 Key Design Decisions

- **Offline-first**: All data stored locally in Room, no internet required
- **Flow-based**: All data queries return `Flow<T>` for reactive UI updates
- **Use case pattern**: Business logic encapsulated in single-responsibility use cases
- **Animated UI**: Number counters, progress bars, and transitions animate smoothly
- **Semantic colors**: Green=income, Red=expense, Blue=transfer throughout the app

## 📄 License

MIT License
