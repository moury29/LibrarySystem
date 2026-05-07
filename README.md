# St Mary's Digital Library System
## CPS4005 – Object-Oriented Programming | Maryam Razzaque

---

## Login Credentials
- **Username:** admin
- **Password:** library26

---

## How to Run

### Requirements
- Java JDK 22+
- SQLite JDBC JAR in the lib/ folder

### Step 1 - Compile

javac -cp "lib\sqlite-jdbc.jar" -d bin src\library\model*.java src\library\dao*.java src\library\ui*.java src\library\Main.java
### Step 2 - Run GUI
java -cp "bin;lib\sqlite-jdbc.jar" library.Main
### Step 3 - Run Console Mode (optional)
java -cp "bin;lib\sqlite-jdbc.jar" library.Main console
---

## Project Structure

LibrarySystem/
├── src/library/
│   ├── model/       Book.java, Member.java, BorrowRecord.java
│   ├── dao/         DatabaseConnection.java, BookDAO.java,
│   │                MemberDAO.java, BorrowRecordDAO.java, Validator.java
│   ├── ui/          LoginGUI.java, DashboardGUI.java,
│   │                LibraryGUI.java, ConsoleUI.java
│   └── Main.java
├── lib/             sqlite-jdbc.jar
├── database/        library.db (auto-created on first run)
├── README.md
└── gitlog.txt

---

## Database

The SQLite database is automatically created on first run at database/library.db
with sample books, members and borrow records already inserted.

---

## Features

- Login authentication backed by database
- Full CRUD operations for Books, Members and Borrow Records
- Java Swing GUI with dashboard and sidebar navigation
- Console-based menu interface
- Overdue book detection and fine calculation at 50p per day
- Advanced search and filtering across all entities
- Multi-threading for responsive data loading without freezing the GUI
- Confirmation dialogs before all delete and logout actions
- Input validation for email format, date format and duplicate prevention

---

## Sample Data

The following sample data is inserted automatically on first run:

Books: Introduction to Java, Database Systems, Software Engineering Principles,
Clean Code, The Pragmatic Programmer, Design Patterns, Artificial Intelligence,
Computer Networks

Members: Alice Johnson, Michael Lee, Sara Ahmed, James Wilson, Emma Davis,
Oliver Brown

Borrow Records: 3 sample records linking books to members

---

## Git Log

cdef873  Initial commit: St Mary's Digital Library System CPS4005
52b648c  Add VS Code settings for Java classpath
1e729dc  Add sidebar navigation to LibraryGUI
8be7030  fix bugs
c087eb4  fix bugs
85f224c  Remove bin folder and class files from GitHub
fc6c48b  fix bugs