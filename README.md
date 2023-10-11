# File Sharing Bot

**File Sharing Bot** – это современный и удобный сервис обмена данными, который
обеспечивает безопасное и эффективное взаимодействие с файлами. Бот позволяет
пользователям легко и быстро делиться документами и изображениями с другими
пользователями, предоставляя им уникальные ссылки для скачивания.

## Особенности бота:
- **Простая регистрация:** Для начала использования бота вам нужно всего лишь
  зарегистрироваться, подтвердив свой адрес электронной почты. Этот шаг
  обеспечивает безопасность и конфиденциальность ваших данных.

- **Максимальный размер файла 20 Мб:** Бот поддерживает загрузку файлов
  размером до 20 мегабайт, что позволяет обмениваться как небольшими документами,
  так и изображениями высокого качества.

- **Безопасность и надежность:** Мы гарантируем сохранность ваших файлов и
  личных данных за счет использования алгоритмов шифрования.

- **Удобное взаимодействие:** Простой и интуитивно понятный интерфейс бота
  делает обмен файлами максимально удобным и приятным процессом.

## Конфигурирование и запуск
Для установки и запуска приложения вы можете выбрать один из двух
способов: автоматический или ручной.

### Автоматический метод

Для автоматической установки необходимых параметров и запуска приложения
выполните следующие шаги:

1. **Установите JDK**: Для запуска микросервисов необходимо установить
   [Java Development Kit](https://www.oracle.com/java/technologies/downloads/)
   версии 17 или выше. После установки JDK необходимо убедиться,
   что задана переменная окружения JAVA_HOME.

2. **Установите Docker**: Убедитесь, что на вашем компьютере установлен
   Docker. Если нет, вы можете скачать и установить его с
   [официального сайта Docker](https://docs.docker.com/get-docker/).

3. **Установите Git**: Данная утилита необходима в первую очередь Windows пользователям
   для получения возможности запуска скрипта конфигурации. Если git у вас не
   установлен, то скачайте его с [официального сайта Git](https://git-scm.com/downloads)

4. **Откройте терминал:** Запустите терминал и перейдите в папку, в которую хотите
   загрузить файлы проекта. Если вы используете Windows, откройте Git Bash, так как
   команды из скрипта конфигурации не работают в командной строке Windows.

5. **Клонируйте репозиторий**: Скопируйте репозиторий с кодом проекта на свой
   компьютер, выполнив указанную ниже команду.
   ```bash
   git clone https://github.com/Bezzerker/file-sharing-bot
   ```

6. **Перейдите в директорию проекта**: Для этого выполните указанную ниже команду.
   ```bash
   cd ./file-sharing-bot
   ```

7. **Запустите скрипт конфигурации**: Запустите скрипт, который автоматически
   настроит контейнеры, параметры Spring Boot для микросервисов, запустит
   контейнеры и микросервисы. Для этого выполните следующую команду в терминале
   и следуйте инструкциям на экране.
   ```bash
   ./setup.sh
   ```

### Ручной метод
Для ручного конфигурирования и запуска приложения выполните следующие шаги:
1. **Согласно [инструкции по автоматическому конфигурированию и запуску](#автоматический-метод) выполните шаги 1 – 6.**
2. **Отредактируйте файл docker-compose.yml**: Установите желаемые значения параметров
   для контейнеров RabbitMQ и PostgreSQL.
3. **Инициализируйте параметры из application.yml**: Для каждого из _telegram-message-gateway_,
   _message-handler_, _rest-service_ и _mail-service_ установите значения параметров для
   соединения с базой данных, соединения с брокером сообщений, соединения с почтовым
   сервером, соединения с телеграм-ботом, а также параметры для генерации ссылок.

   Для определенных сервисов может потребоваться ключ для шифрования и дешифрования строк
   его можно будет получить с помощью выполнения следующих команд:
   ```bash
   ./mvnw clean package -pl :common-utils -DskipTests=true -q
   ```
   ```bash
   java -jar ./common-utils/target/ciphering.jar
   ```
4. **Поднимите контейнеры**: Для того, чтобы запустить контейнеры с RabbitMQ и Postgres
   необходимо выполнить следующие команды.
   ```bash
   docker compose down -v
   ```
   ```bash
   docker compose up --build --detach
   ```
5. **Соберите проект**: Для сборки проекта необходимо воспользоваться инструментом сборки
   [Maven](https://maven.apache.org/what-is-maven.html) и выполнить следующую команду.
   ```bash
   ./mvnw clean package -DskipTests=true -q
   ```
6. **Запустите микросервисы**: Выполните указанную ниже команду, заменив путь_до_сервиса на
   пути к исполняемым файлам сервисов с расширением .jar, находящихся в папке target в
   директориях _telegram-message-gateway_, _message-handler_, _rest-service_ и _mail-service_.
   ```
   java -jar ./путь_до_сервиса1.jar & java -jar ./путь_до_сервиса2.jar & java -jar ./путь_до_сервиса3.jar & java -jar ./путь_до_сервиса4.jar 
   ```

## Используемые технологии
Проект стремится к использованию современных технологий и инструментов,
которые обеспечивают высокую надежность, производительность и масштабируемость.
Ниже представлен подробный перечень основных технологических решений и
библиотек, применяемых в данном приложении:

### Фреймворки и библиотеки:
- [**Spring Boot**](https://spring.io/projects/spring-boot):
    - [Spring Boot Starter Web](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html):
      Для создания веб-приложения и API.
    - [Spring Boot Starter Mail](https://docs.spring.io/spring-framework/docs/5.0.7.RELEASE/spring-framework-reference/integration.html#mail):
      Для отправки электронных писем.
    - [Spring Boot Starter Test](https://docs.spring.io/spring-boot/docs/1.5.2.RELEASE/reference/html/boot-features-testing.html):
      Для тестирования.
    - [Spring Data JPA](https://spring.io/projects/spring-data-jpa):
      Для взаимодействия с базой данных.
    - [Spring AMQP](https://spring.io/projects/spring-amqp): Для работы с RabbitMQ.

### Библиотеки и инструменты:
- [**Lombok**](https://projectlombok.org/): Упрощает разработку Java-кода с
  использованием аннотаций.
- [**Logback**](https://logback.qos.ch/): Позволяет настраивать и вести
  логирование приложения.
- [**JAXB**](https://javaee.github.io/jaxb-v2/): Для кодирования и декодирования
  строки в base64
- [**Common Validator**](https://commons.apache.org/proper/commons-validator/):
  Используется для валидации данных.
- [**TelegramBots**](https://github.com/rubenlagus/TelegramBots) от rubenlagus:
  Для создания и управления Telegram-ботом.

### Технологии интеграции:
- [**RabbitMQ**](https://www.rabbitmq.com/): Служит для обеспечения асинхронной
  обработки сообщений и взаимодействия микросервисов.
- [**PostgreSQL**](https://www.postgresql.org/): Высокопроизводительная реляционная
  база данных, используемая для хранения данных приложения.

### Контейнеризация:
- [**Docker**](https://docs.docker.com/): Позволяет упаковать приложение и его
  зависимости в контейнеры для легкой установки и масштабирования.

## Архитектура приложения

Приложение построено на микросервисной архитектуре, что обеспечивает масштабируемость
и отказоустойчивость системы. Микросервисы взаимодействуют друг с другом с
использованием RabbitMQ и REST API.

### Список микросервисов и их описание:

1. **Telegram Message Gateway**

   **Функции:**
    - *Анализ и маршрутизация сообщений:* Сервис анализирует типы сообщений, отправленных
      в бот, и размещает их в соответствующих очередях RabbitMQ (тексты, изображения и документы)
      для дальнейшей обработки в *Message Handler*. Это позволяет разгрузить основной сервис и
      повысить масштабируемость системы.
    - *Доставка сообщений:* Сервис получает сообщения из очереди ответов RabbitMQ и доставляет
      их пользователям через API Telegram.

2. **Message Handler**

   **Функции:**
    - *Обработка команд пользователя:* Message Handler анализирует сообщения и выполняет
      различные команды, такие как /start, /help, /register, /reset и /cancel, с
      учетом контекста и статуса пользователя.
    - *Загрузка файлов в базу данных:* Загружает отправленный пользователем файл через
      API Telegram и сохраняет в локальную базу данных приложения.
    - *Создание ссылок для загрузки файлов:* После того, как файл добавлен в БД, сервис
      создает соответствующую ссылку на загруженный пользователем файл.
    - *Проверка валидности электронной почты:* В ходе регистрации пользователей, проверяется
      валидность предоставленных ими адресов электронной почты.
    - *Управление активностью ссылок:* Ссылки для подтверждения регистрации действуют час,
      после чего деактивируются. Для этого система использует очередь RabbitMQ с временными
      ограничениями (заданы *x-message-ttl* и *x-dead-letter-exchange*), которая переносит
      неактивных пользователей в очередь кандидатов на отключение ссылки подтверждения.
    - *Управление ошибками:* Определяются и обрабатывают ситуации, когда пользователь
      не зарегистрирован, вводит недопустимые команды или отправляет файлы, размер которых превышает 20 МБ.
    - *Генерация ответных сообщений:* Сервис создаёт ответные сообщения для пользователя
      и помещает их в очередь ответов RabbitMQ для последующей доставки через Telegram Message Gateway.

3. **Rest Service**

   **Функции:**
    - Предоставляет REST API для подтверждения пользователем электронной почты.
    - Предоставляет REST API для загрузки изображений и документов по сгенерированным ссылкам.

4. **Mail Service**

   **Функции:**
    - Предоставляет REST API для отправки электронных писем, содержащих ссылку на регистрацию.