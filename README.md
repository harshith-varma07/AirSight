# AirSight

AirSight is a real-time web application designed to monitor air quality, provide downloadable AQI data reports, and send emergency alerts to users when pollution levels become hazardous.

## ✨ Features

- **Real-Time Dashboard:** View live Air Quality Index (AQI) data on an interactive map and dashboard.
- **Historical Data:** Analyze historical air quality trends with beautiful charts and graphs.
- **PDF Reports:** Download detailed AQI data for a selected period as a PDF file.
- **Emergency Alerts:** Receive SMS or email notifications when the AQI in your monitored location crosses a dangerous threshold.
- **User Accounts:** Register and set custom locations and alert preferences.

## 🛠️ Tech Stack

- **Backend:** Java 17, Spring Boot 3
- **Database:** MySQL 8
- **Frontend:** HTML5, CSS3, JavaScript (ES6+)
- **Real-time Communication:** Spring WebSocket
- **Alerting:** Spring Scheduled Tasks, integration with an email/SMS service (e.g., Twilio, SendGrid)
- **API:** REST

## 🚀 Getting Started

### Prerequisites

- Java JDK 17 or later
- Maven 3.8 or later
- MySQL 8 or later
- An IDE like IntelliJ IDEA or VS Code with Java extensions

### Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/your-username/AirSight.git
   cd AirSight
   ```
2. **Backend Setup:**
   - Navigate to the `backend` directory.
   - Update the `src/main/resources/application.properties` file with your MySQL database credentials.
   - Run the application: `mvn spring-boot:run`

3. **Frontend Setup:**
   - Open the `frontend/index.html` file in your web browser.
