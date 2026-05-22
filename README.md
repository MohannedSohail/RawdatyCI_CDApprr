<div align="center">

<br/>

<img src="assets/rawdatyLogo.png" width="120" alt="Rawdaty Logo"/>

<br/><br/>

# روضتي — Rawdaty

### نظام إدارة رياض الأطفال المتكامل  
### Enterprise Multi-Tenant Kindergarten Management System

*Kotlin Multiplatform · Compose Multiplatform · Clean Architecture · Offline-First*

<br/>

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.x-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.7.x-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/compose-multiplatform/)
[![Android](https://img.shields.io/badge/Android-API%2024+-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Desktop](https://img.shields.io/badge/Desktop-JVM%2017+-FF6B35?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Django](https://img.shields.io/badge/Backend-Django%20REST-092E20?style=for-the-badge&logo=django&logoColor=white)](https://djangoproject.com)
[![License](https://img.shields.io/badge/License-MIT-22c55e?style=for-the-badge)](LICENSE)

<br/>

[![GitHub Stars](https://img.shields.io/github/stars/MohannedSohail/RawdatyCI_CDApprr?style=flat-square&color=yellow)](https://github.com/MohannedSohail/RawdatyCI_CDApprr/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/MohannedSohail/RawdatyCI_CDApprr?style=flat-square&color=blue)](https://github.com/MohannedSohail/RawdatyCI_CDApprr/forks)
[![Issues](https://img.shields.io/github/issues/MohannedSohail/RawdatyCI_CDApprr?style=flat-square&color=red)](https://github.com/MohannedSohail/RawdatyCI_CDApprr/issues)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen?style=flat-square)](CONTRIBUTING.md)

<br/>

### 🔗 Quick Access

[**🌐 Live Demo & Presentation**](https://kindergarten-manager-pro--ahoe.replit.app/) &nbsp;·&nbsp;
[**📱 Download APK**](https://github.com/MohannedSohail/RawdatyCI_CDApprr/releases/latest) &nbsp;·&nbsp;
[**🖥️ Desktop Build**](https://github.com/MohannedSohail/RawdatyCI_CDApprr/releases/latest) &nbsp;·&nbsp;
[**🐛 Report Bug**](https://github.com/MohannedSohail/RawdatyCI_CDApprr/issues/new)

<br/>

> **🎯 Try the full interactive demo → [kindergarten-manager-pro--ahoe.replit.app](https://kindergarten-manager-pro--ahoe.replit.app/)**

</div>

---

## 📋 Table of Contents

- [✨ Overview](#-overview)  
- [🌐 Live Demo](#-live-demo)  
- [👥 User Roles](#-user-roles)  
- [📱 App Screens](#-app-screens)  
- [🖥️ Desktop Screens](#️-desktop-screens)  
- [🚀 Features](#-features)  
- [🏗️ Architecture](#️-architecture)  
- [🛠️ Tech Stack](#️-tech-stack)  
- [📁 Project Structure](#-project-structure)  
- [⚡ Getting Started](#-getting-started)  
- [🗺️ Roadmap](#️-roadmap)  
- [🤝 Contributing](#-contributing)  
- [📄 License](#-license)  

---

## ✨ Overview

<img src="assets/Cover_Rwdaty.png" alt="Rawdaty Overview" width="100%"/>

<br/>

**Rawdaty (روضتي)** is a production-ready, multi-tenant kindergarten management platform built for modern educational institutions. One unified codebase powers a full-featured **Android app** and a **Desktop app** — sharing 90%+ of UI and business logic via **Kotlin Multiplatform** and **Compose Multiplatform**.

The platform connects three key stakeholders — **Administrators**, **Teachers**, and **Parents** — in a seamless digital ecosystem backed by a robust **Django REST Framework** backend with full multi-tenancy support.

> **"رعاية متميزة لجيل واعد"** — *Exceptional care for a promising generation*

<br/>

| Challenge | Rawdaty's Solution |
|-----------|-------------------|
| Managing multiple kindergarten branches | Multi-tenant architecture with isolated per-tenant data |
| Cross-platform consistency | Compose Multiplatform — shared UI & logic for Android + Desktop |
| Working in poor connectivity | SQLDelight offline-first + background Ktor sync |
| Complex role management | RBAC: Super Admin, Admin, Teacher, Parent |
| Parent-school communication | Built-in messaging, news feed & complaint system |
| Engaging children educationally | Interactive quiz/game module for parents & children |

---

## 🌐 Live Demo

<div align="center">

### 👉 [kindergarten-manager-pro--ahoe.replit.app](https://kindergarten-manager-pro--ahoe.replit.app/)

*Full interactive presentation — no installation required*

| Role | Demo Credentials |
|------|----------------|
| 🔐 **Admin** | `admin@demo.rawdati.app` |
| 👩‍🏫 **Teacher** | `teacher30@demo.rawdaty.app` |
| 👨‍👩‍👦 **Parent** | `parent@demo.rawdaty.app` |

> 💡 Explore every feature live — dashboards, attendance, payments, messaging, analytics, and more.

</div>

---

## 👥 User Roles

Rawdaty is a **multi-role system** built around three main user types, each with a tailored experience:

| Role | Description | Access Level |
|------|-------------|-------------|
| 🔐 **Super Admin** | Platform-wide management across all tenants | Full system control |
| 🏫 **Tenant Admin** | Kindergarten director / institution manager | Full institution control |
| 👩‍🏫 **Teacher** | Class supervisor and daily activities manager | Class + student level |
| 👨‍👩‍👦 **Parent** | Child's guardian tracking progress and communication | Child-level read/write |

---

## 📱 App Screens

### 🔐 Admin App

> The administrator has full control over the institution — managing users, classes, notifications, complaints, and news.

<img src="assets/Cover_Admin3.png" alt="Admin App Screens" width="100%"/>

<br/>

**📹 Admin App Demo:**

https://github.com/MohannedSohail/RawdatyCI_CDApprr/assets/admin-app-demo/Admin_App.mp4

> *Full admin workflow: login → dashboard → manage users → add classes → send notifications → handle complaints*

<details>
<summary>📋 Admin App — Key Screens</summary>

| Screen | Description |
|--------|-------------|
| 🏠 Dashboard | Summary cards: teachers count, parents count, active classes |
| 👥 User Management | List, search, add Teachers & Parents with role assignment |
| ➕ Add User | Create accounts for teachers and parents with full details |
| 🏫 Class Management | Add/manage classes with teacher assignment and capacity |
| 📢 Notifications | Send urgent alerts and general announcements |
| 📰 News Management | Add and publish news with media attachments |
| 💬 Complaints | View and respond to parent complaints |
| 👤 Profile | Kindergarten details, admin profile, security settings |

</details>

---

### 👩‍🏫 Teacher App

> Teachers manage their classes daily — recording attendance, communicating with parents, and tracking student progress.

<img src="assets/Cover_Teacher.png" alt="Teacher App Screens" width="100%"/>

<br/>

**📹 Teacher App Demo:**

https://github.com/MohannedSohail/RawdatyCI_CDApprr/assets/teacher-app-demo/Teacher_App.mp4

> *Teacher workflow: login → view classes → record attendance → view student files → check notifications*

<details>
<summary>📋 Teacher App — Key Screens</summary>

| Screen | Description |
|--------|-------------|
| 🏠 Dashboard | Today's stats: attendance rate, class count, active students |
| 📚 My Classes | All assigned classes with student count and today's status |
| ✅ Attendance | One-tap attendance marking per student with save & push |
| 📁 Student File | Full student profile: personal info, skills progress |
| 👥 All Students | Search and browse all students in teacher's classes |
| 📋 News & Announcements | View school-wide news and official announcements |
| 💬 Complaints | Submit and track complaints and suggestions |
| 👤 Profile | Personal info, kindergarten data, security settings |

</details>

---

### 👨‍👩‍👦 Parent App

> Parents stay connected with their child's kindergarten life — attendance, news, progress, and interactive educational games.

<img src="assets/Cover_Parent.png" alt="Parent App Screens" width="100%"/>

<br/>

**📹 Parent App Demo:**

https://github.com/MohannedSohail/RawdatyCI_CDApprr/assets/parent-app-demo/Parent_App.mp4

> *Parent workflow: login → view child dashboard → check attendance → read news → play educational games → submit complaint*

<details>
<summary>📋 Parent App — Key Screens</summary>

| Screen | Description |
|--------|-------------|
| 🏠 Dashboard | Child's today summary: attendance, recent news, quick services |
| 📁 Child File | Full profile: personal details, attendance rate, skills progress |
| 📊 Progress Report | Detailed academic and skills progress with visual charts |
| 🎮 Educational Games | Interactive quiz games for children with scoring and ranking |
| 📋 Leaderboard | Quiz rankings: gold, silver, bronze achievements |
| 📰 News Feed | School news, official announcements, holiday notices |
| 💬 Complaints | Submit and track complaints; receive admin responses |
| 🔔 Notifications | All system notifications with read/unread status |
| 👤 Profile | Personal info, enrolled children, account settings |

</details>

---

### 📱 App Onboarding & Splash

<img src="assets/Cover_Rwdaty.png" alt="Rawdaty Onboarding Screens" width="100%"/>

*Beautiful onboarding flow with role selection — Parent, Teacher, or Admin*

---

## 🖥️ Desktop Screens

> The Desktop version targets kindergarten administrators, providing a full-featured management interface on Windows, macOS, and Linux.

**📹 Desktop App Demo:**

https://github.com/MohannedSohail/RawdatyCI_CDApprr/assets/desktop-demo/Admin_Desktop.mp4

### Splash & Onboarding

<img src="assets/Cover_Desktop1.png" alt="Desktop Splash & Onboarding" width="100%"/>

*Smooth animated splash screen followed by a feature-rich onboarding carousel*

---

### Onboarding Slides

<img src="assets/Cover_Desktop2.png" alt="Desktop Onboarding Slides" width="100%"/>

*Three onboarding screens: "Follow your child every moment", "Safe direct communication", "Events and occasions"*

---

### Login & Role Selection

<img src="assets/Cover_Desktop3.png" alt="Desktop Login" width="100%"/>

*RTL-native login screen with credential fields + role selection (Admin / Teacher / Parent)*

---

### Dashboard & User Management

<img src="assets/Cover_Desktop4.png" alt="Desktop Dashboard & Users" width="100%"/>

*Control panel with live stats (8 teachers, 23 parents, 11 classes) + full user management with search and role tags*

---

### Class Management & Profile

<img src="assets/Cover_Desktop4___1.png" alt="Desktop Class Management" width="100%"/>

*Add/edit classes with teacher assignment, capacity settings, and visual progress indicators per class*

---

### Add New Account

<img src="assets/Cover_Desktop4___4.png" alt="Desktop Add Account" width="100%"/>

*Full account creation form for Teachers and Parents with bilingual name support (Arabic + English)*

---

### Kindergarten Profile & Settings

<img src="assets/Cover_Desktop5.png" alt="Desktop Profile & Settings" width="100%"/>

*Admin profile page with account info, kindergarten details (name, address, contact), and security settings*

---

## 🔧 Backend — Django Admin

The backend is powered by **Django REST Framework** with a robust admin interface for super-admin management.

<img src="assets/WhatsApp_Image_2026-04-13_at_12_49_27_PM.jpeg" alt="Django Admin Panel" width="100%"/>

<img src="assets/WhatsApp_Image_2026-044-12_at_9_42_57_PM.jpeg" alt="Django Admin Children" width="100%"/>

**Backend Modules:**

| Module | Endpoints |
|--------|-----------|
| 🏢 Super Admin | Tenants, Super Admins, Broadcasts, Audit Logs, Invoices |
| 👶 Children | Child profiles with tenant isolation |
| 📚 Classes | Kinder classes per tenant |
| ✅ Attendance | Attendance records + sessions |
| 💬 Chat | Conversations + Messages |
| 📰 News | News items per tenant |
| 🔔 Notifications | Push notifications system |
| 🎮 Games | Game questions + results |
| 💼 Complaints | Complaint tracking per tenant |
| 🔐 Auth | Users, Groups, Refresh Tokens, Tenant Users |

---

## 🚀 Features

<details open>
<summary><strong>🏢 Multi-Tenant Architecture</strong></summary>
<br/>

- Complete data isolation per kindergarten tenant
- Tenant onboarding via super-admin panel  
- Each tenant has its own slug, users, classes, and data
- Centralized super-admin dashboard with audit logs

</details>

<details>
<summary><strong>👶 Student Management</strong></summary>
<br/>

- Full student profiles with personal data and photo
- Class assignment and enrollment management  
- Skills progress tracking (Language, Math, Social, Motor)
- Emergency contacts and medical notes
- Parent linkage with full access to child data

</details>

<details>
<summary><strong>📅 Attendance Tracking</strong></summary>
<br/>

- One-tap daily attendance per student  
- Present / Absent / Late status options
- Session locking after submission
- Real-time sync to parents on attendance save
- Historical attendance rate per student and class

</details>

<details>
<summary><strong>📲 Parent Communication</strong></summary>
<br/>

- Direct teacher-parent messaging
- School-wide news and announcements feed
- Complaint submission with admin response tracking
- Push notifications for attendance, news, and alerts
- Read/unread status on all notifications

</details>

<details>
<summary><strong>🎮 Educational Games</strong></summary>
<br/>

- Interactive multi-question quiz system  
- Math and language skill questions  
- Real-time scoring with star rating (1–3 stars)
- Global leaderboard with Gold/Silver/Bronze ranks
- Progress tracking per child per game session

</details>

<details>
<summary><strong>🔐 Role-Based Access Control</strong></summary>
<br/>

- 4 roles: Super Admin, Tenant Admin, Teacher, Parent
- Feature-level access gating per role
- Tenant-scoped data access enforcement
- Audit logs for sensitive admin actions

</details>

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Presentation Layer                          │
│          Compose Multiplatform UI · ViewModels · UI State           │
│                  (Android + Desktop — shared 90%+)                  │
├─────────────────────────────────────────────────────────────────────┤
│                           Domain Layer                              │
│         Use Cases · Domain Models · Repository Interfaces           │
├─────────────────────────────────────────────────────────────────────┤
│                            Data Layer                               │
│    Repository Impl · Remote (Ktor → Django REST) · Local (SQLDelight)│
├─────────────────────────────────────────────────────────────────────┤
│                       Platform / DI Layer                           │
│             Koin Modules · expect/actual Platform APIs              │
└─────────────────────────────────────────────────────────────────────┘
       ↑ androidMain              ↑ desktopMain
       
                    ↕ REST API (Ktor Client)
                    
┌─────────────────────────────────────────────────────────────────────┐
│                     Django REST Framework Backend                   │
│         Multi-Tenant · JWT Auth · PostgreSQL · Admin Panel          │
└─────────────────────────────────────────────────────────────────────┘
```

| Principle | Implementation |
|-----------|----------------|
| **Single Source of Truth** | SQLDelight as the only UI-observable data source |
| **Offline-First** | Full functionality offline; Ktor syncs in background |
| **Unidirectional Data Flow** | `StateFlow` + sealed `UiState` in every ViewModel |
| **Dependency Inversion** | Koin DI — interface-based injection at all layers |
| **Multi-Tenancy** | Per-tenant slug routing, scoped data, isolated storage |

---

## 🛠️ Tech Stack

<div align="center">

**Client (Kotlin Multiplatform)**

| Category | Technology |
|----------|-----------|
| Language | Kotlin 2.x |
| UI | Compose Multiplatform (Android + Desktop) |
| Design | Material 3 |
| Database | SQLDelight (offline-first SQLite) |
| Networking | Ktor Client |
| DI | Koin |
| Async | Coroutines + Flow |
| Architecture | Clean Architecture + MVVM |

**Backend**

| Category | Technology |
|----------|-----------|
| Framework | Django REST Framework |
| Auth | JWT (SimpleJWT) |
| Admin | Django Admin (customized) |
| Database | PostgreSQL |
| Deployment | Docker + CI/CD |

</div>

---

## 📁 Project Structure

```
RawdatyCI_CDApprr/
│
├── 📱 composeApp/
│   └── src/
│       ├── commonMain/kotlin/com/rawdaty/
│       │   ├── core/              # DI, networking, database setup
│       │   ├── domain/            # Models, use cases, repository interfaces
│       │   ├── data/              # Repository impl, local (SQLDelight), remote (Ktor)
│       │   └── presentation/
│       │       ├── navigation/    # Navigation graph
│       │       ├── theme/         # Material 3 theme + RTL support
│       │       └── screens/
│       │           ├── auth/      # Login, role selection, onboarding
│       │           ├── admin/     # Dashboard, users, classes, notifications
│       │           ├── teacher/   # Classes, attendance, student files
│       │           └── parent/    # Child file, games, news, complaints
│       ├── androidMain/           # Android platform code
│       └── desktopMain/           # Desktop platform code
│
├── 🐍 backend/                    # Django REST API
│   ├── attendance/
│   ├── children/
│   ├── classes/
│   ├── complaints/
│   ├── games/
│   ├── news/
│   ├── notifications/
│   ├── super_admin/
│   └── users/
│
├
├── 🎨 assets/                     # Screenshots, logos, media
```

---

## ⚡ Getting Started

### Prerequisites

| Requirement | Version |
|-------------|---------|
| Android Studio | Hedgehog (2023.1.1)+ |
| JDK | 17+ |
| Python | 3.11+ (for backend) |
| Android SDK | API 24+ |
| Gradle | 8.x (via wrapper) |

### 1. Clone

```bash
git clone https://github.com/MohannedSohail/RawdatyCI_CDApprr.git
cd RawdatyCI_CDApprr
```

### 2. Run Android / Desktop App

```bash
# Android
./gradlew :composeApp:installDebug

# Desktop
./gradlew :composeApp:run
```

### 3. Run Backend

```bash
cd backend
pip install -r requirements.txt
python manage.py migrate
python manage.py runserver
```

### 4. Configure API

```properties
# local.properties
rawdaty.api.baseUrl=http://localhost:8000
```

### 5. Demo Login

| Role | Email | Password |
|------|-------|----------|
| Admin | `admin@demo.rawdati.app` | (see demo) |
| Teacher | `teacher30@demo.rawdaty.app` | (see demo) |
| Parent | `parent@demo.rawdaty.app` | (see demo) |

> 🌐 Or just use the [**live demo**](https://kindergarten-manager-pro--ahoe.replit.app/) directly!

---

## 🗺️ Roadmap

| Phase | Feature | Status |
|-------|---------|:------:|
| **v1.0** | Multi-tenant architecture | ✅ Done |
| **v1.0** | Admin app (Android + Desktop) | ✅ Done |
| **v1.0** | Teacher app with attendance | ✅ Done |
| **v1.0** | Parent app with child tracking | ✅ Done |
| **v1.0** | Educational games module | ✅ Done |
| **v1.0** | Django REST backend | ✅ Done |
| **v1.1** | Payments & receipt module | 🚧 In Progress |
| **v1.1** | Advanced analytics dashboard | 🚧 In Progress |
| **v1.2** | Real-time chat (WebSocket) | 📋 Planned |
| **v1.2** | Photo & document sharing | 📋 Planned |
| **v2.0** | iOS support | 🔮 Future |
| **v2.0** | Web app (Compose WASM) | 🔮 Future |
| **v2.1** | AI attendance (face recognition) | 🔮 Future |

---

<div align="center">

🌐 **[Live Demo](https://kindergarten-manager-pro--ahoe.replit.app/)** &nbsp;·&nbsp;
📱 **[Releases](https://github.com/MohannedSohail/RawdatyCI_CDApprr/releases)** &nbsp;·&nbsp;
⭐ **Star if useful!**

<br/>

*Made with ❤️ in Palestine 🇵🇸*

<br/>

<img src="assets/rawdatyLogo.png" width="60" alt="Rawdaty"/>

</div>
