# SmartBite: Native Android Food Delivery App

SmartBite is a native Android food delivery application built using Java and a local SQLite database. It functions as a two-sided platform, catering to both Customers and Delivery Partners with distinct user flows and interfaces.

## 📱 Features

### Customer Flow
*   **Authentication:** Customers can register or log in, with the app assigning and authenticating the "customer" role.
*   **Restaurant Discovery:** A dedicated dashboard lists available restaurants, complete with a search bar to filter by name or cuisine type.
*   **Interactive Ordering:** Users can add or remove items from menus, with a Floating Action Button (FAB) displaying real-time quantity and total price.
*   **Cart & Checkout:** The cart summarizes subtotals and delivery fees. Customers must provide a delivery address before placing an order.
*   **Order Tracking:** An Order History screen provides updates on active and past orders (e.g., "Pending", "Picked Up", "Delivered").
*   **Profile Management:** Customers can manage their username, email, and default address.

### Delivery Partner Flow
*   **Authentication:** Delivery partners use the same login interface but are authenticated with the "delivery" role.
*   **Order Dashboard:** Displays unassigned "Pending" orders, showing customer names, full addresses, and total amounts.
*   **Order Fulfillment:** 
    *   **Pickup:** Accepting an order updates its status and notifies the customer.
    *   **Navigation:** Integrated Google Maps view pinpoints current location, restaurant, and customer address, linking to turn-by-turn navigation.
    *   **Delivery:** One-click confirmation marks the order as complete.

## 🛠 Tech Stack & Architecture

*   **Platform:** Android Native (Java)
*   **Database:** SQLite (managed via `SQLiteOpenHelper`)
*   **Architecture:** MVC-based with Data Access Objects (DAOs) for clean database interactions
*   **UI/UX:** Material Design Components, RecyclerViews, CardViews
*   **Libraries:** 
    *   Google Maps & Location Services API
    *   Glide (Image Loading)
    *   AndroidX Core & Appcompat

## 📊 Performance Metrics & Assessment

*   **Architecture & Modularity (8/10):** The codebase is well-structured into distinct packages (`adapters`, `database`, `models`, `ui`, `utils`). The use of DAOs isolates database operations from UI logic effectively.
*   **UI/UX Quality (8/10):** Implementation of Material Design components and Glide for image rendering provides a smooth and native look and feel.
*   **Database Efficiency (6/10):** While `DatabaseHelper` and DAOs are well-organized, raw SQLite queries are prone to syntax errors. Migrating to Room Database would improve compile-time safety and observability.
*   **Security (4/10):** Currently, passwords and sensitive user data (like roles and IDs in `SharedPreferences`) are stored as plain text. Implementing password hashing and using `EncryptedSharedPreferences` is highly recommended for production.
*   **Stability & Resilience (9/10):** Critical bugs, such as a `ClassCastException` on the profile screen and Google Maps API authorization failures, have been resolved, ensuring stable runtime behavior.

## 🚀 Setup & Installation

1.  Clone this repository.
2.  Open the project in **Android Studio**.
3.  Add your Google Maps API Key in `AndroidManifest.xml` (replace `YOUR_MAPS_API_KEY`).
4.  Build and run the project on an emulator or physical device.
