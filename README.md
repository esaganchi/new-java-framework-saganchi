# New Java Framework (UI Tests)
Учебный проект для демонстрации автотестов на Java с использованием Selenium WebDriver, JUnit 5 и Gradle.

## Стек
Java 23, Selenium WebDriver 4, JUnit 5, Gradle, AssertJ

## Запуск
1. Клонировать репозиторий:  
   `git clone https://github.com/esaganchi/new-java-framework-saganchi.git && cd new-java-framework-saganchi`  
2. Запустить тесты:  
   `./gradlew test`  
3. Отчёт доступен по пути:  
   `build/reports/tests/test/index.html`

## Тесты
- `submitWebFormTest` – проверка отправки формы  
- `loadingImagesImplicitWaitTest` – проверка загрузки изображений (implicit wait)  
- `loadingImagesExplicitWaitTest` – проверка загрузки изображений (explicit wait)
