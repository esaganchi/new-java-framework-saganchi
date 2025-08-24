# new-java-framevork-saganchi

Тестовый фреймворк для автоматизации тестирования API и UI на Java.

## 📋 Описание

Проект представляет собой тестовый фреймворк, объединяющий API и UI тестирование с использованием популярных Java библиотек. Фреймворк поддерживает запуск тестов как локально, так и в удаленной среде (например, через Docker с Selenium Grid).

## 🛠 Технологический стек

- **Java 17** - основной язык программирования
- **JUnit 5** - фреймворк для написания и запуска тестов
- **RestAssured** - для тестирования REST API
- **Selenium WebDriver** - для UI автоматизации
- **Allure** - для генерации отчетов о тестах
- **AssertJ** - для улучшенных assertions
- **Lombok** - для сокращения boilerplate кода
- **Owner** - для управления конфигурацией
- **Gradle** - система сборки

## 📁 Структура проекта

```
src/
├── main/java/esaganchi/
│   └── Main.java                    # Основной класс приложения
└── test/java/
    ├── config/
    │   └── TestPropertiesConfig.java # Конфигурация тестов
    ├── constants/
    │   └── CommonConstants.java      # Общие константы
    ├── controllers/
    │   └── UserController.java       # API контроллер для работы с пользователями
    ├── models/
    │   ├── AddUserResponse.java      # Модель ответа API
    │   └── User.java                # Модель пользователя
    ├── pages/
    │   ├── BasePage.java            # Базовая страница для Page Object
    │   └── LoginPage.java           # Страница логина
    ├── testdata/
    │   └── TestData.java            # Тестовые данные
    ├── tests/
    │   ├── ApiTests.java            # API тесты
    │   ├── BaseTest.java            # Базовый класс для UI тестов
    │   ├── UIPomTests.java          # UI тесты с Page Object Model
    │   └── UITests.java             # UI тесты
    └── utils/
        ├── AfterTestExtension.java   # JUnit расширение
        └── AllureSteps.java         # Шаги для Allure отчетов
```

## ⚙️ Конфигурация

Настройки тестов находятся в файлах:
- `src/test/resources/default.properties` - основная конфигурация
- `src/test/resources/dev.properties` - настройки для dev среды  
- `src/test/resources/test.properties` - настройки для тестовой среды

### Основные параметры:
- `apiBaseUrl` - базовый URL для API тестов (по умолчанию: https://petstore.swagger.io/v2/)
- `uiBaseUrl` - базовый URL для UI тестов (по умолчанию: https://bonigarcia.dev/selenium-webdriver-java/)

## 🚀 Запуск тестов

### Локальный запуск
```bash
# Запуск всех тестов
./gradlew test

# Запуск только API тестов
./gradlew test --tests "*ApiTests*"

# Запуск только UI тестов  
./gradlew test --tests "*UiTests*"
```

### Удаленный запуск (Selenium Grid)
Установите переменную окружения `SELENIUM_REMOTE_URL`:
```bash
export SELENIUM_REMOTE_URL=http://selenium-hub:4444/wd/hub
./gradlew test
```

## 📊 Генерация отчетов Allure

```bash
# Генерация отчета
./gradlew allureReport

# Запуск сервера с отчетом
./gradlew allureServe
```

## ✨ Особенности

- **Поддержка удаленного WebDriver**: автоматическое переключение между локальным и удаленным драйвером
- **Page Object Model**: структурированная организация UI тестов
- **Параметризованные тесты**: поддержка JUnit 5 параметризации
- **Интеграция с Allure**: детальные отчеты с шагами и скриншотами
- **Безопасные клики**: защита от перехвата кликов элементами интерфейса
- **Мягкие проверки**: использование SoftAssertions для множественных проверок

## 📝 Примеры тестов

### API тест
```java
@Test
void createUser() {
    Response response = userController.createUser(DEFAULT_USER);
    AddUserResponse createdUserResponse = response.as(AddUserResponse.class);
    
    Assertions.assertEquals(200, response.statusCode());
    Assertions.assertEquals(200, createdUserResponse.getCode());
}
```

### UI тест
```java
@Test  
void submitWebFormTest() {
    driver.get(UI_BASE_URL);
    
    WebElement webFormLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Web form")));
    webFormLink.click();
    
    WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("my-text-id")));
    input.sendKeys("Text");
    
    // Безопасный клик с защитой от перекрытий
    safeClick(submitBtn);
}
```

## 🤝 Участие в разработке

1. Форкните репозиторий
2. Создайте ветку для новой функции (`git checkout -b feature/amazing-feature`)
3. Закоммитьте изменения (`git commit -m 'Add some amazing feature'`)
4. Запушьте в ветку (`git push origin feature/amazing-feature`)
5. Создайте Pull Request

## 📄 Лицензия

Этот проект распространяется под лицензией MIT. См. файл `LICENSE` для получения дополнительной информации.

## 👨‍💻 Автор

**Evgeniy Saganchi**

- GitHub: [@esaganchi](https://github.com/esaganchi)

## 📈 Версия

1.0-SNAPSHOT