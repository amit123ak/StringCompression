# 🧪 PwC — QA Automation Engineer Interview Experience (2026)

> **Role:** QA Automation Engineer  
> **Company:** PwC  
> **Year:** 2026  
> **Stack:** Java · Selenium · TestNG · Rest Assured · Jenkins · GitHub Actions

---

## 📋 Table of Contents

- [Round 1 – Technical Round](#round-1--technical-round)
  - [Q1 – Scalable Selenium Framework Design](#q1--how-would-you-design-a-scalable-selenium-automation-framework-using-java-and-testng)
  - [Q2 – Page Object Model (POM)](#q2--explain-the-page-object-model-pom)
  - [Q3 – Managing Test Environments](#q3--how-do-you-manage-test-execution-across-different-browsers-and-environments)
  - [Q4 – Parallel Tests in TestNG](#q4--how-do-you-run-parallel-tests-in-testng-and-avoid-thread-safety-issues)
  - [Q5 – Dynamic Elements](#q5--how-do-you-handle-dynamic-elements-with-changing-ids)
  - [Q6 – File Upload/Download](#q6--can-you-automate-file-uploaddownload-in-selenium)
  - [Q7 – Alerts, Popups, and iFrames](#q7--how-do-you-handle-alerts-popups-and-iframes)
  - [Q8 – Screenshots on Failure](#q8--how-do-you-capture-screenshots-on-test-failures-and-attach-to-extent-reports)
  - [Q9 – Flaky Test Debugging](#q9--how-do-you-debug-flaky-or-intermittent-test-failures)
  - [Q10 – Rest Assured / Postman](#q10--have-you-worked-with-rest-assured-or-postman-for-api-automation)
  - [Q11 – JSON Validation in Java](#q11--how-do-you-validate-a-json-response-using-java)
  - [Q12 – Selenium Waits](#q12--different-waits-in-selenium--when-to-use-implicit-explicit-or-fluent-waits)
  - [Q13 – Jenkins Freestyle vs Pipeline](#q13--difference-between-jenkins-freestyle-jobs-and-pipeline-scripts)
  - [Q14 – Jenkins / GitHub Actions Integration](#q14--how-to-integrate-your-selenium-suite-with-jenkins-or-github-actions)
  - [Q15 – Data-Driven Testing](#q15--how-to-implement-data-driven-testing-using-excel-csv-or-json)
  - [Q16 – Reusable & Maintainable Scripts](#q16--strategies-to-keep-test-scripts-reusable-and-maintainable)
  - [Q17 – Java Program: Palindrome / Reverse String](#q17--write-a-java-program-to-reverse-a-string-or-check-for-a-palindrome)
- [Round 2 – HR Round](#round-2--hr-round)
  - [Q1 – Salary Expectation](#q1--salary-expectation)
  - [Q2 – Night Shift](#q2--are-you-ok-with-night-shift)

---

## Round 1 – Technical Round

---

### Q1 – How would you design a scalable Selenium automation framework using Java and TestNG?

**Answer:**

A scalable framework follows a layered architecture:

| Layer | Responsibility |
|-------|---------------|
| **Base Layer** | WebDriver init, browser config, common utilities |
| **POM Layer** | Page classes with locators and page-level actions |
| **Test Layer** | TestNG test classes using page objects |
| **Config Layer** | Property/YAML files for environment-specific settings |
| **Reporting Layer** | ExtentReports or Allure for visual HTML reports |

**Key design principles:**

- Use **Maven** or **Gradle** for dependency management
- Store test data externally (Excel / JSON / CSV) — never hardcode in test classes
- Parameterize browser and environment via TestNG XML or CI pipeline variables
- Implement `BaseTest` class for `@BeforeMethod` / `@AfterMethod` driver setup/teardown
- Use `ThreadLocal<WebDriver>` for parallel-safe driver management

**Folder structure:**
```
src/
├── main/java/
│   ├── base/          → BaseTest, DriverManager
│   ├── pages/         → Page Object classes
│   └── utils/         → Waits, Screenshots, Config reader
├── test/java/
│   └── tests/         → TestNG test classes
└── resources/
    ├── config/        → qa.properties, uat.properties
    ├── testdata/      → Excel, JSON, CSV files
    └── testng.xml     → Suite configuration
```

---

### Q2 – Explain the Page Object Model (POM)

**Answer:**

POM is a design pattern where each web page is represented as a **separate Java class**. All locators and page-specific actions are encapsulated inside that class — keeping test code clean, readable, and easy to maintain.

```java
// LoginPage.java
public class LoginPage {

    WebDriver driver;

    @FindBy(id = "username")
    WebElement usernameField;

    @FindBy(id = "password")
    WebElement passwordField;

    @FindBy(id = "loginBtn")
    WebElement loginButton;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void login(String username, String password) {
        usernameField.clear();
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();
    }
}
```

```java
// LoginTest.java
public class LoginTest extends BaseTest {

    @Test
    public void testValidLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin", "password123");
        Assert.assertTrue(homePage.isLoaded());
    }
}
```

**Benefits:**
- Single place to update if a locator changes
- Test classes focus on logic, not selectors
- Promotes code reuse across multiple tests

---

### Q3 – How do you manage test execution across different browsers and environments?

**Answer:**

Store environment-specific values in separate `.properties` files:

```
resources/config/
├── qa.properties
├── uat.properties
└── staging.properties
```

```properties
# qa.properties
base.url=https://qa.myapp.com
db.host=qa-db.internal
api.endpoint=https://api-qa.myapp.com
```

**Reading config dynamically:**

```java
public class ConfigReader {
    private static Properties props = new Properties();

    static {
        String env = System.getProperty("env", "qa"); // default: qa
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config/" + env + ".properties");
            props.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Config file not found for env: " + env);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
```

**Running for a specific environment via Maven:**

```bash
mvn test -Denv=uat -Dbrowser=chrome
```

In Jenkins or GitHub Actions, pass `-Denv=staging` as a build parameter.

---

### Q4 – How do you run parallel tests in TestNG and avoid thread-safety issues?

**Answer:**

**Step 1 – Enable parallel execution in testng.xml:**

```xml
<suite name="RegressionSuite" parallel="methods" thread-count="4">
    <test name="LoginTests">
        <classes>
            <class name="tests.LoginTest"/>
        </classes>
    </test>
</suite>
```

**Step 2 – Use `ThreadLocal<WebDriver>` to avoid shared state:**

```java
public class DriverManager {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static void setDriver(WebDriver d) {
        driver.set(d);
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove(); // prevent memory leak
        }
    }
}
```

```java
@BeforeMethod
public void setUp() {
    WebDriver d = new ChromeDriver();
    DriverManager.setDriver(d);
}

@AfterMethod
public void tearDown() {
    DriverManager.quitDriver();
}
```

**Thread-safety rules:**
- Never use `static WebDriver` — each thread must have its own instance
- Avoid static variables for page objects
- Use `ThreadLocal` for any per-test data (like test names, screenshot paths)

---

### Q5 – How do you handle dynamic elements with changing IDs?

**Answer:**

Avoid fragile auto-generated IDs. Use stable, meaningful locator strategies:

```java
// ❌ Fragile - auto-generated ID changes every load
driver.findElement(By.id("j_id_2k:0:username"));

// ✅ Stable - XPath with partial match
driver.findElement(By.xpath("//input[contains(@id,'username')]"));

// ✅ CSS attribute selector
driver.findElement(By.cssSelector("input[name='email']"));

// ✅ Text-based XPath
driver.findElement(By.xpath("//button[text()='Submit']"));
driver.findElement(By.xpath("//button[contains(text(),'Log')]"));

// ✅ Parent-child relationship (anchor to stable parent)
driver.findElement(By.xpath("//div[@class='login-form']//input[@type='password']"));

// ✅ Best practice - data-testid (added by developers for automation)
driver.findElement(By.cssSelector("[data-testid='login-button']"));
```

**Tip:** Ask developers to add `data-testid` attributes — they are stable by convention and dedicated for automation use.

---

### Q6 – Can you automate file upload/download in Selenium?

**Answer:**

**File Upload (using `sendKeys`):**

```java
// Works for standard <input type="file"> elements
WebElement uploadInput = driver.findElement(By.id("fileUpload"));
uploadInput.sendKeys("/absolute/path/to/file.pdf");
```

**File Download (configure ChromeOptions):**

```java
ChromeOptions options = new ChromeOptions();
Map<String, Object> prefs = new HashMap<>();
prefs.put("download.default_directory", "/home/user/downloads");
prefs.put("download.prompt_for_download", false);
prefs.put("plugins.always_open_pdf_externally", true);
options.setExperimentalOption("prefs", prefs);

WebDriver driver = new ChromeDriver(options);
```

**Verify download completed:**

```java
public boolean isFileDownloaded(String downloadPath, String fileName) {
    File dir = new File(downloadPath);
    File[] files = dir.listFiles();
    if (files != null) {
        for (File file : files) {
            if (file.getName().equals(fileName)) return true;
        }
    }
    return false;
}
```

**For OS-level file dialogs (drag-and-drop upload areas):**
- Use **Robot class** for keyboard/mouse simulation
- Use **AutoIT** (Windows only)
- Use **Sikuli** for image-based automation

---

### Q7 – How do you handle alerts, popups, and iFrames?

**Answer:**

**JavaScript Alerts:**

```java
// Accept (click OK)
Alert alert = driver.switchTo().alert();
System.out.println(alert.getText()); // read message
alert.accept();

// Dismiss (click Cancel)
alert.dismiss();

// Prompt (send text input)
alert.sendKeys("my input");
alert.accept();
```

**iFrames:**

```java
// Switch by name or ID
driver.switchTo().frame("frameName");

// Switch by index
driver.switchTo().frame(0);

// Switch by WebElement
WebElement iframeEl = driver.findElement(By.cssSelector("iframe.content-frame"));
driver.switchTo().frame(iframeEl);

// Always switch back when done
driver.switchTo().defaultContent();

// Go one level up (nested iframes)
driver.switchTo().parentFrame();
```

**New Window / Tab:**

```java
String mainWindow = driver.getWindowHandle();

// Switch to new window
for (String handle : driver.getWindowHandles()) {
    if (!handle.equals(mainWindow)) {
        driver.switchTo().window(handle);
        break;
    }
}

// Come back to main window
driver.switchTo().window(mainWindow);
```

---

### Q8 – How do you capture screenshots on test failures and attach to Extent Reports?

**Answer:**

**Step 1 – Screenshot utility:**

```java
public class ScreenshotUtil {

    public static String capture(WebDriver driver, String testName) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File src = ts.getScreenshotAs(OutputType.FILE);
        String path = System.getProperty("user.dir") + "/screenshots/" + testName + "_" + System.currentTimeMillis() + ".png";
        try {
            FileUtils.copyFile(src, new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
```

**Step 2 – Implement `ITestListener`:**

```java
public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = DriverManager.getDriver();
        String screenshotPath = ScreenshotUtil.capture(driver, result.getName());

        // Attach to Extent Report
        ExtentReportManager.getTest().fail("Test Failed!")
            .addScreenCaptureFromPath(screenshotPath);
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentReportManager.createTest(result.getName());
    }
}
```

**Step 3 – Register listener in testng.xml:**

```xml
<suite name="Suite">
    <listeners>
        <listener class-name="utils.TestListener"/>
    </listeners>
    <test name="Tests">
        <classes>
            <class name="tests.LoginTest"/>
        </classes>
    </test>
</suite>
```

---

### Q9 – How do you debug flaky or intermittent test failures?

**Answer:**

Flaky tests are one of the biggest challenges in automation. Here is a systematic approach:

**1. Replace implicit waits with explicit/fluent waits:**

```java
// ❌ Unreliable
driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

// ✅ Wait for the exact condition you need
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
wait.until(ExpectedConditions.elementToBeClickable(By.id("submitBtn")));
```

**2. Add logging at each step** to identify exactly where it fails.

**3. Check for race conditions** — element may be visible but not yet interactive:

```java
wait.until(ExpectedConditions.elementToBeClickable(element));
wait.until(ExpectedConditions.visibilityOf(element));
wait.until(ExpectedConditions.stalenessOf(oldElement)); // for re-rendered elements
```

**4. Run the failing test in isolation** — rules out test order dependency.

**5. Compare CI vs local** — check for headless mode, screen resolution, or timing differences.

**6. Use `IRetryAnalyzer` as a short-term patch:**

```java
public class RetryAnalyzer implements IRetryAnalyzer {
    private int count = 0;
    private static final int MAX_RETRY = 2;

    @Override
    public boolean retry(ITestResult result) {
        if (count < MAX_RETRY) {
            count++;
            return true;
        }
        return false;
    }
}

// Apply on test
@Test(retryAnalyzer = RetryAnalyzer.class)
public void testCheckout() { ... }
```

> Retry is a patch — always investigate and fix the root cause.

---

### Q10 – Have you worked with Rest Assured or Postman for API automation?

**Answer:**

Yes. **Rest Assured** is the standard Java library for API automation in Java-based projects.

**Basic GET request:**

```java
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

given()
    .baseUri("https://api.example.com")
    .header("Authorization", "Bearer " + token)
    .contentType(ContentType.JSON)
.when()
    .get("/users/1")
.then()
    .statusCode(200)
    .body("name", equalTo("John Doe"))
    .body("email", containsString("@example.com"));
```

**POST request:**

```java
String requestBody = "{ \"name\": \"Alice\", \"role\": \"admin\" }";

given()
    .baseUri("https://api.example.com")
    .header("Authorization", "Bearer " + token)
    .contentType(ContentType.JSON)
    .body(requestBody)
.when()
    .post("/users")
.then()
    .statusCode(201)
    .body("id", notNullValue());
```

**Postman** is used for:
- Exploratory / manual API testing
- Generating test collections
- Running via **Newman CLI** in CI pipelines:

```bash
newman run collection.json -e qa_environment.json --reporters cli,junit
```

---

### Q11 – How do you validate a JSON response using Java?

**Answer:**

**Method 1 – Rest Assured built-in matchers (quick assertions):**

```java
.then()
    .body("status", equalTo("active"))
    .body("user.age", greaterThan(18))
    .body("items.size()", equalTo(5))
    .body("roles", hasItems("admin", "user"))
    .body("address.city", equalTo("Mumbai"));
```

**Method 2 – Parse to POJO using Jackson (complex validation):**

```java
// User.java (POJO)
public class User {
    private String name;
    private String email;
    private int age;
    // getters and setters
}

// In test
Response response = given().get("/users/1");
ObjectMapper mapper = new ObjectMapper();
User user = mapper.readValue(response.body().asString(), User.class);

Assert.assertEquals("John Doe", user.getName());
Assert.assertTrue(user.getAge() >= 18);
```

**Method 3 – JSONPath for nested/array data:**

```java
Response response = given().get("/orders");
JsonPath jp = response.jsonPath();

String status = jp.getString("orders[0].status");
int totalItems = jp.getList("orders").size();
List<String> ids = jp.getList("orders.id");

Assert.assertEquals("SHIPPED", status);
Assert.assertEquals(3, totalItems);
```

---

### Q12 – Different waits in Selenium — when to use implicit, explicit, or fluent waits?

**Answer:**

| Wait Type | Scope | When to Use |
|-----------|-------|-------------|
| **Implicit Wait** | Global — all `findElement()` calls | Simple scripts; avoid in serious frameworks |
| **Explicit Wait** | Specific element + specific condition | Most common — preferred approach |
| **Fluent Wait** | Like explicit but with polling config | Slow-loading/dynamic elements |

**Implicit Wait:**

```java
// Applied globally — waits up to 10s for every findElement()
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
```

**Explicit Wait:**

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

// Common conditions
wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("result")));
wait.until(ExpectedConditions.elementToBeClickable(By.id("submitBtn")));
wait.until(ExpectedConditions.textToBePresentInElement(element, "Success"));
wait.until(ExpectedConditions.urlContains("/dashboard"));
```

**Fluent Wait:**

```java
Wait<WebDriver> fluentWait = new FluentWait<>(driver)
    .withTimeout(Duration.ofSeconds(30))
    .pollingEvery(Duration.ofSeconds(3))         // check every 3 seconds
    .ignoring(NoSuchElementException.class);     // ignore this exception while polling

WebElement el = fluentWait.until(driver -> driver.findElement(By.id("result")));
```

> **Best practice:** Use explicit or fluent waits. Never mix implicit with explicit waits — it causes unpredictable timeouts.

---

### Q13 – Difference between Jenkins freestyle jobs and pipeline scripts?

**Answer:**

| Feature | Freestyle Job | Pipeline (Jenkinsfile) |
|---------|--------------|----------------------|
| Configuration | GUI-based | Code (Groovy DSL) |
| Version control | ❌ Not stored in Git | ✅ Stored in repo |
| Parallel execution | Limited | ✅ Native support |
| Conditional logic | Limited | ✅ Full scripting |
| Reusability | Low | ✅ Shared libraries |
| Best for | Simple builds | Complex workflows |

**Example Jenkinsfile (Declarative Pipeline):**

```groovy
pipeline {
    agent any

    parameters {
        choice(name: 'ENV', choices: ['qa', 'uat', 'staging'], description: 'Target environment')
        choice(name: 'BROWSER', choices: ['chrome', 'firefox'], description: 'Browser')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/your/repo.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile -q'
            }
        }

        stage('Run Tests') {
            steps {
                sh "mvn test -Denv=${params.ENV} -Dbrowser=${params.BROWSER}"
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
            publishHTML(target: [reportDir: 'reports', reportFiles: 'index.html', reportName: 'Extent Report'])
        }
        failure {
            emailext subject: "Build Failed: ${env.JOB_NAME}", body: "Check console: ${env.BUILD_URL}", to: 'team@example.com'
        }
    }
}
```

---

### Q14 – How to integrate your Selenium suite with Jenkins or GitHub Actions?

**Answer:**

**Jenkins Integration:**

1. Install plugins: Maven Integration, TestNG Results, HTML Publisher
2. Create a Pipeline job pointing to your `Jenkinsfile` in the repo
3. Configure build parameters for `env` and `browser`
4. Publish reports using `publishHTML` plugin

**GitHub Actions Integration:**

Create `.github/workflows/regression.yml`:

```yaml
name: Selenium Regression Suite

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]
  schedule:
    - cron: '0 6 * * *'   # Run daily at 6 AM UTC

jobs:
  test:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v3

      - name: Set up Java 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Set up Chrome
        uses: browser-actions/setup-chrome@latest

      - name: Run tests
        run: mvn test -Denv=qa -Dbrowser=headless-chrome -Dsuite=regression

      - name: Upload test report
        uses: actions/upload-artifact@v3
        if: always()
        with:
          name: extent-report
          path: reports/

      - name: Publish test results
        uses: dorny/test-reporter@v1
        if: always()
        with:
          name: TestNG Results
          path: target/surefire-reports/*.xml
          reporter: java-junit
```

---

### Q15 – How to implement data-driven testing using Excel, CSV, or JSON?

**Answer:**

**Using TestNG `@DataProvider` with Excel (Apache POI):**

```java
@DataProvider(name = "loginData")
public Object[][] getLoginData() throws IOException {
    FileInputStream fis = new FileInputStream("src/test/resources/testdata/LoginData.xlsx");
    Workbook wb = new XSSFWorkbook(fis);
    Sheet sheet = wb.getSheet("Login");
    int rowCount = sheet.getLastRowNum();
    Object[][] data = new Object[rowCount][2];

    for (int i = 1; i <= rowCount; i++) {
        Row row = sheet.getRow(i);
        data[i - 1][0] = row.getCell(0).getStringCellValue(); // username
        data[i - 1][1] = row.getCell(1).getStringCellValue(); // password
    }
    wb.close();
    return data;
}

@Test(dataProvider = "loginData")
public void testLogin(String username, String password) {
    loginPage.login(username, password);
    Assert.assertTrue(homePage.isLoaded(), "Login failed for: " + username);
}
```

**Using CSV (OpenCSV):**

```java
@DataProvider(name = "csvData")
public Object[][] getCsvData() throws IOException {
    CSVReader reader = new CSVReader(new FileReader("src/test/resources/testdata/users.csv"));
    List<String[]> rows = reader.readAll();
    Object[][] data = new Object[rows.size() - 1][2]; // skip header row
    for (int i = 1; i < rows.size(); i++) {
        data[i - 1][0] = rows.get(i)[0];
        data[i - 1][1] = rows.get(i)[1];
    }
    return data;
}
```

**Using JSON (Jackson):**

```java
// TestData.java
public class TestData {
    public String username;
    public String password;
    public String expectedRole;
}

@DataProvider(name = "jsonData")
public Object[][] getJsonData() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    TestData[] dataArray = mapper.readValue(
        new File("src/test/resources/testdata/loginData.json"),
        TestData[].class
    );
    Object[][] result = new Object[dataArray.length][1];
    for (int i = 0; i < dataArray.length; i++) {
        result[i][0] = dataArray[i];
    }
    return result;
}
```

---

### Q16 – Strategies to keep test scripts reusable and maintainable as the project grows?

**Answer:**

1. **Strictly follow POM** — one class per page, zero locators in test classes
2. **Extract reusable actions** into utility/helper classes (`WaitUtils`, `AssertUtils`, `DateUtils`)
3. **Use constants** for URLs, element IDs, and test data keys — avoid magic strings
4. **Parameterize everything** — environment, browser, base URL, user roles
5. **Write tests at a high level of abstraction** — test behavior, not implementation details
6. **Tag tests with groups** for selective execution:

```java
@Test(groups = { "smoke", "login" })
public void testValidLogin() { ... }

@Test(groups = { "regression" })
public void testPasswordReset() { ... }
```

```xml
<!-- Run only smoke tests -->
<groups>
    <run>
        <include name="smoke"/>
    </run>
</groups>
```

7. **Regular refactoring sessions** — treat test code like production code
8. **Code reviews for test PRs** — catch duplication and bad practices early
9. **Document test intent in method names** — not just comments:

```java
// ❌ Unclear
@Test
public void test1() { ... }

// ✅ Self-documenting
@Test
public void shouldRedirectToHomePageAfterSuccessfulLogin() { ... }
```

10. **Use `@Factory` for running same test with different user roles:**

```java
@Factory
public Object[] createTests() {
    return new Object[] {
        new LoginTest("admin", "pass1"),
        new LoginTest("viewer", "pass2")
    };
}
```

---

### Q17 – Write a Java program to reverse a string or check for a palindrome

**Answer:**

**Reverse a String:**

```java
public class ReverseString {

    // Method 1: Using StringBuilder (simplest)
    public static String reverseUsingBuilder(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    // Method 2: Using char array (manual approach)
    public static String reverseManually(String str) {
        char[] chars = str.toCharArray();
        int left = 0, right = chars.length - 1;
        while (left < right) {
            char temp = chars[left];
            chars[left] = chars[right];
            chars[right] = temp;
            left++;
            right--;
        }
        return new String(chars);
    }

    public static void main(String[] args) {
        System.out.println(reverseUsingBuilder("hello"));  // Output: olleh
        System.out.println(reverseManually("Selenium"));   // Output: muineleS
    }
}
```

**Check for a Palindrome:**

```java
public class PalindromeCheck {

    // Basic check (case-insensitive)
    public static boolean isPalindrome(String str) {
        String clean = str.toLowerCase();
        String reversed = new StringBuilder(clean).reverse().toString();
        return clean.equals(reversed);
    }

    // Advanced check (ignores spaces and special characters)
    public static boolean isPalindromeAdvanced(String str) {
        String clean = str.toLowerCase().replaceAll("[^a-z0-9]", "");
        String reversed = new StringBuilder(clean).reverse().toString();
        return clean.equals(reversed);
    }

    public static void main(String[] args) {
        System.out.println(isPalindrome("racecar"));          // true
        System.out.println(isPalindrome("hello"));            // false
        System.out.println(isPalindromeAdvanced("A man a plan a canal Panama")); // true
        System.out.println(isPalindromeAdvanced("Was it a car or a cat I saw")); // true
    }
}
```

---

## Round 2 – HR Round

---

### Q1 – Salary Expectation?

**Answer:**

Research the market rate for your experience level and city **before** the interview. A confident, structured response:

> *"Based on my research and my hands-on experience with Selenium, Java, TestNG, Rest Assured, and CI/CD pipelines, I am looking for a package in the range of ₹X – ₹Y LPA. However, I am open to discussion considering the overall compensation structure, growth opportunities, and the scope of work at PwC."*

**Tips:**
- Always give a **range**, not a single number
- Base your range on Glassdoor / Naukri / LinkedIn salary data
- Include your current CTC + expected hike % if asked

---

### Q2 – Are you ok with night shift?

**Answer:**

Be honest and professional. PwC GDS (Global Delivery Services) roles often involve overlapping hours with US/UK clients.

**If you are comfortable:**
> *"Yes, I am flexible with shift timings. I understand that working with global delivery teams may require overlapping hours with international clients, and I am comfortable working in night shifts if the role requires it."*

**If you have constraints:**
> *"I can manage occasional night shifts or rotational shifts. I would appreciate knowing the expected frequency upfront so I can plan accordingly. I am committed to meeting project requirements."*

---

## 📁 Suggested Repository Structure

```
qa-automation-interview-prep/
│
├── README.md                        ← This file (Interview Q&A)
│
├── selenium/
│   ├── waits.md
│   ├── page-object-model.md
│   ├── dynamic-elements.md
│   └── file-upload-download.md
│
├── testng/
│   ├── parallel-execution.md
│   └── data-driven-testing.md
│
├── api-testing/
│   ├── rest-assured-basics.md
│   └── json-validation.md
│
├── cicd/
│   ├── jenkins-pipeline.md
│   └── github-actions.yml
│
└── java-programs/
    ├── ReverseString.java
    └── PalindromeCheck.java
```

---

## 🔖 Useful Resources

| Topic | Link |
|-------|------|
| Selenium Docs | https://www.selenium.dev/documentation |
| TestNG Docs | https://testng.org/doc |
| Rest Assured | https://rest-assured.io |
| ExtentReports | https://www.extentreports.com |
| Apache POI | https://poi.apache.org |
| GitHub Actions | https://docs.github.com/en/actions |
| Jenkins Docs | https://www.jenkins.io/doc |

---

## 💚 Good luck to everyone preparing for interviews!

> *Feel free to fork this repo, add your own notes, and share it with your network.*  
> *Wishing you success — hope you land your dream job soon!*

---

⭐ **If this helped you, please star the repo!**
