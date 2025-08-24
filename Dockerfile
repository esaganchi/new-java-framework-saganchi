# Используем образ с Java 17 и Gradle
FROM gradle:8.5-jdk17 AS builder

# Создаем рабочую директорию
WORKDIR /app

# Копируем файлы сборки
COPY build.gradle settings.gradle ./
COPY gradle/ ./gradle/

# Загружаем зависимости (кеширование слоя)
RUN gradle dependencies --no-daemon

# Копируем исходный код
COPY src/ ./src/

# Собираем приложение
RUN gradle build -x test --no-daemon

# Финальный образ для запуска тестов
FROM openjdk:17-jdk-slim

# Установка необходимых пакетов
RUN apt-get update && \
    apt-get install -y wget gnupg2 software-properties-common curl && \
    # Установка Google Chrome
    wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list' && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    # Установка ChromeDriver
    CHROME_DRIVER_VERSION=$(curl -sS chromedriver.storage.googleapis.com/LATEST_RELEASE) && \
    wget -O /tmp/chromedriver.zip http://chromedriver.storage.googleapis.com/$CHROME_DRIVER_VERSION/chromedriver_linux64.zip && \
    unzip /tmp/chromedriver.zip chromedriver -d /usr/local/bin/ && \
    chmod +x /usr/local/bin/chromedriver && \
    # Очистка кеша
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/chromedriver.zip

# Установка Gradle
COPY --from=gradle:8.5-jdk17 /opt/gradle /opt/gradle
ENV PATH="/opt/gradle/bin:${PATH}"

# Создание пользователя для тестов
RUN groupadd -r testuser && useradd -r -g testuser -m -d /app -s /bin/bash testuser

# Создание рабочей директории
WORKDIR /app

# Копирование собранного приложения
COPY --from=builder /app ./
COPY --chown=testuser:testuser --from=builder /app ./

# Создание директории для отчетов
RUN mkdir -p /app/build/allure-results && \
    mkdir -p /app/build/reports && \
    chown -R testuser:testuser /app

# Переключение на пользователя testuser
USER testuser

# Переменные окружения для Chrome
ENV CHROME_BIN=/usr/bin/google-chrome
ENV DISPLAY=:99

# Точка входа для запуска тестов
ENTRYPOINT ["gradle"]
CMD ["test", "--no-daemon"]