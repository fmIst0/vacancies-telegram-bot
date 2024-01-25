## Java Jobs Seeker Bot🤖🔍

---

## 📝Content
* [Overview](#overview)
  * [Technologies](#technologies)
* [Features](#features)
* [Project Structure](#project-structure)
* [Run Project](#run-project)

### 📜Overview
This telegram bot will help Java developers find a suitable vacancy. 
The bot takes information from the largest popular IT community in Ukraine - DOU 
and displays information about existing vacancies to the user.

* ### 🛠️Technologies
    * **Java**: The core programming language used for development.
    * **Spring Boot**: A Java framework used to build the application.
    * **Maven**: Apache Maven is a software project management and comprehension tool.
    * **Telegram bot API**: API for interaction with telegram and bot.
    * **OpenCSV**: Fantastic library for writing, reading, serializing, deserializing, and/or parsing .csv files.
    * **Jsoup**: Library that simplifies working with real-world HTML and XML(In the case of a bot - parsing DOU).
  
### ⚙Features
* Possibility to select vacancies by developer level.
* View full information about the vacancy.
* Ability to go to the source.

### 🗂️Project structure
[com.example.vacanciestelegrambot](src/main/java/com/example/vacanciestelegrambot)

* [bot](src/main/java/com/example/vacanciestelegrambot/bot) : classes for setting up and main bot actions.
* [dto](src/main/java/com/example/vacanciestelegrambot/dto) : data transfer objects for responses and requests.
* [service](src/main/java/com/example/vacanciestelegrambot/service) : classes for business logic(parsing from DOU, writing to file and reading from file, other bot`s logic).
* [Main class](src/main/java/com/example/vacanciestelegrambot/VacanciesTelegramBotApplication.java) : class to run bot.

[resources](src/main/resources)
* [application.properties](src/main/resources/application.properties) : some information about the bot.

---

#### root directory files:
* [pom.xml](pom.xml) : maven configurations

### ✅Run project
1. Clone the repository💾 : `git@github.com:fmIst0/vacancies-telegram-bot.git`
2. Build project with MavenⓂ️ : `mvn clean intall`
3. Run project in IntelliJ IDEA☕
4. Try the bot using this link🚀 : [Java Job Seeker Bot](https://t.me/fmIst0_vacancies_bot)