# 🤖 AI Test Failure Analyzer for Selenium/Appium

**Drop AI-powered root cause analysis into any Selenium + TestNG project in minutes.**

When a test fails, instead of manually digging through stack traces, this tool automatically sends the failure context to an LLM and prints a structured diagnosis — root cause, likely fix, and a prevention tip — directly in your console and CI logs.

```
========== 🤖 AI FAILURE ANALYSIS ==========
Root Cause:
  The element was not found because the page had not fully loaded
  before Selenium attempted to locate #email.

Likely Fix:
  • Add an explicit wait: ExpectedConditions.visibilityOfElementLocated()
  • Increase the timeout in config.properties (currently 15s)
  • Verify the element ID hasn't changed after a recent deploy

Prevention Tip:
  Use Page Object methods that encapsulate waits rather than
  interacting with raw WebDriver calls in step definitions.
=============================================
```

---

## How It Works

Two classes. Zero configuration changes to your existing tests.

**`AiAnalyzer.java`** — sends test name, error message, and stack trace to the [Groq API](https://groq.com/) (LLaMA 3.3 70B) and returns a structured analysis.

**`AIFailureListener.java`** — a TestNG `ITestListener` that intercepts every test failure and calls the analyzer automatically.

```
Test Fails
    │
    ▼
AIFailureListener.onTestFailure()
    │  captures: testName, errorMessage, stackTrace
    ▼
AiAnalyzer.analyzeFailure()
    │  sends structured prompt to Groq API
    ▼
LLaMA 3.3 70B
    │  returns: root cause + fix + prevention tip
    ▼
Printed to console / CI log
```

---

## Quick Start

### 1. Add Dependencies

Add to your `pom.xml`:

```xml
<!-- OkHttp for API calls -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.12.0</version>
</dependency>

<!-- JSON parsing -->
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20240303</version>
</dependency>
```

### 2. Copy the Source Files

Drop these two files into your project:

```
src/test/java/
├── listeners/
│   └── AIFailureListener.java
└── utils/
    └── AiAnalyzer.java
```

### 3. Get a Groq API Key

Sign up free at [console.groq.com](https://console.groq.com/) and set your key as an environment variable:

```bash
# macOS / Linux
export GROQ_API_KEY=your_api_key_here

# Windows (PowerShell)
$env:GROQ_API_KEY="your_api_key_here"

# Jenkins / CI
# Add as a credential or environment variable in your pipeline config
```

> **Why Groq?** It's free to start and uses open-weight models, so your test failure data isn't being sent to a proprietary closed system. LLaMA 3.3 70B is fast and accurate for code/debugging tasks.

### 4. Register the Listener

**Option A — `testng.xml`** (recommended):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Your Suite">
    <listeners>
        <listener class-name="listeners.AIFailureListener"/>
    </listeners>
    <test name="Your Tests">
        <classes>
            <class name="runners.YourRunner"/>
        </classes>
    </test>
</suite>
```

**Option B — `@Listeners` annotation** on your runner class:

```java
@Listeners(AIFailureListener.class)
public class MainRunner extends AbstractTestNGCucumberTests { ... }
```

### 5. Run Your Tests

```bash
mvn clean test
```

Any test failure will now trigger an AI analysis printed inline with your test output.

---

## Source Code

### `AiAnalyzer.java`

```java
package utils;

import okhttp3.*;
import org.json.*;

public class AiAnalyzer {

    private static final String API_KEY = System.getenv("GROQ_API_KEY");
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final OkHttpClient client = new OkHttpClient();

    public static String analyzeFailure(String testName, String errorMessage, String stackTrace) {

        String prompt = """
                You are a Selenium test automation expert.
                Analyze this test failure and provide:
                1. Root cause (1-2 sentences)
                2. Likely fix (bullet points)
                3. Prevention tip

                Test Name: %s
                Error: %s
                Stack Trace: %s
                """.formatted(testName, errorMessage,
                        stackTrace.substring(0, Math.min(800, stackTrace.length())));

        try {
            String requestBody = new JSONObject()
                    .put("model", "llama-3.3-70b-versatile")
                    .put("messages", new JSONArray()
                            .put(new JSONObject()
                                    .put("role", "user")
                                    .put("content", prompt)))
                    .put("max_tokens", 500)
                    .toString();

            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String json = response.body().string();
                return new JSONObject(json)
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            }
        } catch (Exception e) {
            return "AI analysis unavailable: " + e.getMessage();
        }
    }
}
```

### `AIFailureListener.java`

```java
package listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.AiAnalyzer;

public class AIFailureListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        String testName   = result.getName();
        String error      = result.getThrowable().getMessage();
        String stackTrace = java.util.Arrays.toString(result.getThrowable().getStackTrace());

        System.out.println("\n========== 🤖 AI FAILURE ANALYSIS ==========");
        System.out.println(AiAnalyzer.analyzeFailure(testName, error, stackTrace));
        System.out.println("=============================================\n");
    }
}
```

---

## Requirements

- Java 17+
- Maven 3.8+
- TestNG 7+
- A [Groq API key](https://console.groq.com/) (free tier available)

---

## Compatibility

| Framework | Compatible |
|---|---|
| TestNG (any version 7+) | ✅ |
| Selenium WebDriver 4.x | ✅ |
| Appium 10.x | ✅ |
| Cucumber + TestNG | ✅ |
| JUnit 5 | 🔜 Planned (see [Contributing](#contributing)) |

---

## Roadmap

- [ ] JUnit 5 Extension support
- [ ] Option to write AI analysis to a dedicated log file
- [ ] Include page source / screenshot context in the prompt
- [ ] Track repeated failures and surface patterns across runs
- [ ] Support additional AI providers (OpenAI, Claude, Gemini)
- [ ] Maven plugin for zero-code integration

---

## Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request.

Found a bug? [Open an issue](../../issues). Have an idea? [Start a discussion](../../discussions).

---

## License

MIT License — free to use, modify, and distribute. See [LICENSE](LICENSE) for details.

---

## Author

Built by **Luiz Lopes** as part of a real-world Selenium + Appium test automation project.
Connect on [LinkedIn](https://www.linkedin.com/in/luiz-lopes-fonseca-neto/) | Follow on [GitHub](https://github.com/bozito1716)

---

*If this saved you debugging time, consider giving it a ⭐ — it helps others find the tool.*
