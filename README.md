# 🧠 AI Work Logger

> A desktop application that uses **NVIDIA NIM's AI API** to intelligently structure your daily work summaries into a professional Excel timesheet — automatically.

---

## ✨ Features

- 📝 **Natural Language Input** — Describe what you did in plain English
- 🤖 **AI-Powered Structuring** — NVIDIA NIM (LLM) parses and formats your input into structured work log entries
- 📊 **Excel Timesheet Export** — Outputs a clean, professional `.xlsx` timesheet
- 🖥️ **Desktop GUI** — Built with Java Swing, dark-themed and easy to use
- 📦 **Fat JAR** — Single executable JAR, no complex setup needed

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| Framework | Spring Boot |
| UI | Java Swing |
| AI API | NVIDIA NIM (LLM Inference) |
| Excel Output | Apache POI |
| Build Tool | Maven |
| Packaging | Fat JAR (Spring Boot Maven Plugin) |

---

## 🚀 Getting Started

### Prerequisites

- Java 17+ installed
- Maven 3.8+ installed
- An active **NVIDIA NIM API Key** ([Get one here](https://build.nvidia.com/))

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/AIExcelLogger.git
cd AIExcelLogger
```

### 2. Configure Your API Key

Open `src/main/resources/application.properties` and replace the placeholder:

```properties
nvidia.nim.api.key=YOUR_KEY_HERE   ← replace this with your actual key
nvidia.nim.url=https://integrate.api.nvidia.com/v1/chat/completions
nvidia.nim.model=meta/llama-3.3-70b-instruct
```

> ⚠️ Never commit your real API key. The `application.properties` file is committed with placeholder values only.

### 3. Build the Fat JAR

```bash
mvn clean package -DskipTests
```

### 4. Run the Application

```bash
java -jar target/AIExcelLogger-1.0.0.jar
```

---

## 🖼️ How It Works

1. Launch the app — the dark-themed Swing GUI opens
2. Type or paste your daily work summary in natural language
3. Click **"Generate Log"**
4. The AI structures your input into task entries (task name, time spent, category, etc.)
5. Click **"Export to Excel"** — your timesheet is saved as a `.xlsx` file

---

## 📁 Project Structure

```
AIExcelLogger/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/viraj/aiexcellogger/
│   │   │       ├── AIExcelLoggerApplication.java   # Spring Boot entry point
│   │   │       ├── ui/
│   │   │       │   └── MainFrame.java               # Swing GUI
│   │   │       ├── service/
│   │   │       │   ├── OpenAIService.java           # NVIDIA NIM API integration
│   │   │       │   └── ExcelExportService.java      # Apache POI Excel generation
│   │   │       └── model/
│   │   │           └── WorkEntry.java               # Work log data model
│   │   └── resources/
│   │       └── application.properties               # Config (placeholder values committed)
├── pom.xml
└── README.md
```

---

## 📤 Excel Output Format

The generated `.xlsx` timesheet includes columns like:

| Date | Task Name | Description | Category | Time Spent (hrs) |
|------|-----------|-------------|----------|------------------|
| 2025-06-10 | SailPoint Rule Config | Configured BeanShell provisioning rule | IAM | 2.5 |

---

## 🔐 Security Note

- `application.properties` is committed with **placeholder values only**
- Your real API key should **only exist locally** and never be pushed
- If a key is accidentally exposed, revoke it immediately at [build.nvidia.com](https://build.nvidia.com)

---

## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you'd like to change.

---

## 📄 License

This project is licensed under the MIT License.

---

## 👤 Author

**Viraj** — [GitHub](https://github.com/your-username) | Built as part of personal productivity tooling & AI integration exploration.
