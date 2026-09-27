# REST API - Автосервис "Корова 'Ладно'"

Данный проект представляет собой REST API для автосервиса. Ниже представлена пошаговая инструкция для запуска проекта.

## Стек технологий

*   **Язык программирования:** Java 17
*   **Фреймворк:** Spring Boot 4.1.1
*   **Сборщик проектов:** Gradle
*   **База данных:** H2 (in-memory) / PostgreSQL
*   **Миграции БД:** Liquibase
*   **Безопасность:** Spring Security (JWT)
*   **Документация API:** Swagger / SpringDoc OpenAPI 3.1.0
*   **Линтер:** Checkstyle (12.1.1)

## Запуск проекта

### Требования к окружению

Для успешного запуска проекта на вашей машине должны быть установлены следующие компоненты:
*   [Docker и Docker Compose](https://www.docker.com/products/docker-desktop/)
*   [Git](https://git-scm.com/downloads)

### Пошаговая инструкция

1.  **Клонирование репозитория**
    Скопируйте проект на ваш компьютер с помощью команды:
    ```bash
    git clone https://github.com/Stepan113/rpplabs.git
    ```

2.  **Переход в директорию проекта**
    ```bash
    cd rpplabs
    ```

3.  **Настройка переменных окружения**
    В проекте используется файл `.env` для хранения конфигурации. Скопируйте шаблон конфигурации `config.env` в файл `.env`.
    ```bash
    cp config.env .env
    ```
    *Примечание: Откройте файл `.env` и при необходимости заполните/измените переменные (JWT_SECRET, настройки подключения к БД).*

4.  **Сборка и запуск контейнеров**
    Соберите и запустите приложение, PostgreSQL и RabbitMQ одной командой:
    ```bash
    docker compose up --build -d
    ```

    Предварительно собирать JAR не требуется: multi-stage `Dockerfile` запускает
    Gradle внутри build-контейнера, а в итоговый образ копирует только готовое
    приложение и Java Runtime.

5.  **Доступ к приложению**
    После успешного запуска Swagger UI с документацией API будет доступен по следующему адресу:
    [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

    Панель RabbitMQ доступна по адресу
    [http://localhost:15672](http://localhost:15672). Логин и пароль задаются
    переменными `RABBITMQ_USER` и `RABBITMQ_PASSWORD` в `.env`.

### Локальный запуск приложения из IDE

Если приложение запускается из IDE или через `./gradlew bootRun`, поднимите
только инфраструктуру:

```bash
docker compose up -d postgres rabbitmq
./gradlew bootRun
```

В этом режиме приложение подключается к PostgreSQL и RabbitMQ через
`localhost`. Значения по умолчанию находятся в `application.yaml`; при
необходимости их можно переопределить переменными окружения Spring Boot.

### Остановка проекта

Чтобы остановить работу контейнеров, выполните команду:
```bash
docker compose down
```

### Повторный запуск (без пересборки)

Если вы не вносили изменения в код, проект можно запустить быстрее:
```bash
docker compose up -d
```

## Полезные команды Gradle (для разработки)

Если вы ведете разработку локально, вам могут пригодиться следующие команды Gradle (выполняются через `./gradlew` (Linux/macOS) или `gradlew.bat` (Windows)):

*   **Проверка стиля кода (Checkstyle):**
    ```bash
    ./gradlew checkAll
    ```
*   **Запуск тестов:**
    ```bash
    ./gradlew test
    ```
*   **Генерация миграций Liquibase (diff):**
    ```bash
    ./gradlew generateDiff
    ```
