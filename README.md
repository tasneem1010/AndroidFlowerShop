# Flower Shop Android App

An Android application for online flower shopping, that allows users to browse through different categories of products including flowers, bouquets, and decorative pots. built as part of the Mobile Application Development course (COMP438).

## Features

- **Product Browsing**: Browse through different categories of products (flowers, bouquets, pots)
- **Shopping Cart**: Add/remove items to and from cart, track quantities, and save orders
- **Cart Management**: Maintain cart state between sessions using SharedPreferences
- **Search Functionality**: Search for specific products using multi-criteria search
- **Responsive UI**: Simple user interface with card-based product display and consistent styling
- **Product Details**: View detailed information about each product including price and quantity

## Screen Recording of app

https://drive.google.com/file/d/1z44AEYwoT_UXA8cnUGzd-rxvcXTUB9V7/view?usp=share_link

## Technologies Used

- Android SDK
- RecyclerView for displaying product lists
- CardView for elegant product cards
- ConstraintLayout for responsive UI design

## Project Structure

- `app/src/main/java/`: Contains Java source code
- `app/src/main/res/layout/`: XML layout files
  - `activity_main.xml`: Main activity layout with product categories
  - `activity_cart.xml`: Shopping cart screen
  - `activity_search.xml`: Search functionality
  - `flower_card.xml`: Layout for individual product cards
- `app/src/main/res/values/`: Resource files
  - `styles.xml`: Style definitions for UI components
  - `colors.xml`: Color definitions
  - `strings.xml`: String resources

## Setup Instructions

1. Clone this repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Run the app on an emulator or physical device

## Requirements

- Android 5.0 (API level 21) or higher
- Android Studio 4.0 or newer

## Contributing

This project was developed as an individual assignment. Contributions are not currently being accepted, but feel free to fork the repository for personal learning purposes.
