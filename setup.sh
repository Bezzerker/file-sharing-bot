#!/bin/bash

############# Вспомогательные функции #############
# Функция считывания порта и его проверки на занятость
used_ports=()
common=0
skip_spring_configuration=0
get_port() {
  local message=$1
  local default_value=$2
  while true; do
    read -p "$message" port
    if [[ -z "$port" ]]
    then
      if [[ -n "$default_value" ]]
      then
        if [[ $(netstat -an -p TCP | grep LISTENING | grep ":$default_value ") ]] || [[ "${used_ports[@]}" =~ "$default_value" ]]
        then
          echo "Порт по умолчанию $default_value уже занят, придется выбрать порт!"
        else
          common=$default_value
          echo "Установлено значение по умолчанию - $default_value"
          break;
        fi
      else
        echo "Ничего не было введено!"
      fi
    elif ! [[ "$port" =~ ^[0-9]+$ ]]
    then
      echo "Должно быть введено число!"
    elif [[ $(netstat -an -p TCP | grep LISTENING | grep ":$port ") ]] || [[ "${used_ports[@]}" =~ "$port" ]]
    then
      echo "Порт $port занят!"
    else
      common=$port
      used_ports+=("$port")
      break;
    fi
  done
  counter=$((counter+1))
}

# Функция, обрабатывающая вопросы с ответами Да и Нет
get_answer() {
  local question=$1
  local main_answer=$2
  local alternative_answer=$3

  echo $question
  echo "Если вы согласны, то введите Y(y), в противном случае - N(n)"
  while true; do
    read -p "Ваш ответ: " answer
    if [[ "$answer" =~ ^[Yy]$ ]]
    then
      common=$main_answer
      break;
    elif [[ "$answer" =~ ^[Nn]$ ]]
    then
      common=$alternative_answer
      break;
    else
      echo "Вы должны ввести Y(y) или N(n), а вы ввели - $answer!"
    fi
  done
  counter=$((counter+1))
}

# Функция, считывающая числа
get_number() {
  local message=$1
  while true; do
    read -p "$message" value
    if [[ -z "$value" ]]
    then
      echo "Вы не ввели значение, повторите попытку!"
    elif [[ "$value" =~ ^[0-9]+$ ]]
    then
      common=$value
      break;
    else
      echo "Вводимое значение должно представлять десятичное число!"
    fi
  done
  counter=$((counter+1))
}

# Функция, считывающая строки
get_string() {
  local message=$1
  local default=$2
  while true; do
    read -p "$message" value
    if [[ -z "$value" ]] && [[ -z "$default" ]]
    then
      echo "Вы не ввели данные, повторите попытку!"
    else
      if [[ -n "$default" ]]
      then
        common=$default
        echo "Установлено значение по умолчанию - $default"
        break
      fi
      common=$value
      break;
    fi
  done
  counter=$((counter+1))
}

# Функция оповещения завершения скрипта
goodbye() {
  echo
  echo
  echo "Остановка скрипта. Завершение всех процессов..."

  if [[ $(find -type f -name "nohup.out") ]]
  then
    rm nohup.out
  fi

  exit 1
}

# Устанавливаем обработчик сигнала SIGINT (Ctrl+C) для вызова функции cleanup
trap goodbye SIGINT

############# Основная часть скрипта #############
echo "**************| Конфигурирование и запуск File Sharing Bot |**************"
echo
echo "  INFO: Если в скобках для параметра указано значение по умолчанию,
        то вы можете оставить значение пустым и нажать на Enter. В таком
        случае установятся стандартные настройки!"
echo
echo "  INFO: Для завершения работы скрипта нажмите комбинацию CTRL + C"

echo
echo "--------------------| Параметры контейнера RabbitMQ |--------------------"
echo
get_answer "Хотите ли выполнить конфигурирование RabbitMQ контейнера?" 1 0
if [ "$common" = 1 ]
then
  echo
  counter=1

  get_string "$counter. Введите hostname для RabbitMQ (по умолчанию rabbitmq): " "rabbitmq"
  rabbitmq_hostname=$common

  get_port "$counter. Введите порт взаимодействия с RabbitMQ (по умолчанию 5672): " 5672
  rabbitmq_port=$common

  get_port "$counter. Введите порт управления RabbitMQ (по умолчанию 15672): " 15672
  rabbitmq_management_port=$common

  get_string "$counter. Введите имя пользователя для доступа к веб-интерфейсу RabbitMQ (по умолчанию rabbit): " "rabbit"
  rabbitmq_user=$common

  get_string "$counter. Введите пароль для доступа к веб-интерфейсу RabbitMQ (по умолчанию rabbit): " "rabbit"
  rabbitmq_password=$common

  get_string "$counter. Введите имя virtual host для RabbitMQ (по умолчанию file_sharing_bot): " "file_sharing_bot"
  rabbitmq_vhost=$common

  # Замена портов и параметров в docker-compose.yml для контейнера rabbitmq
  sed -i "12s/:.*/: $rabbitmq_hostname/" ./docker-compose.yml
  sed -i "10s/[0-9]*:/$rabbitmq_port:/" ./docker-compose.yml
  sed -i "11s/[0-9]*:/$rabbitmq_management_port:/" ./docker-compose.yml
  sed -i "14s/=.*/=$rabbitmq_user/" ./docker-compose.yml
  sed -i "15s/RABBITMQ_DEFAULT_PASS=.*/RABBITMQ_DEFAULT_PASS=$rabbitmq_password/" ./docker-compose.yml
  sed -i "16s/RABBITMQ_DEFAULT_VHOST=.*/RABBITMQ_DEFAULT_VHOST=$rabbitmq_vhost/" ./docker-compose.yml
else
  skip_spring_configuration=1
fi

echo
echo "--------------------| Параметры контейнера PostgreSQL |--------------------"
echo
get_answer "Хотите ли выполнить конфигурирование PostgreSQL контейнера?" 1 0
if [ "$common" = 1 ]
then
  echo
  counter=1

  get_string "$counter. Введите hostname для PostgreSQL (по умолчанию postgres): " "postgres"
  postgres_hostname=$common

  get_port "$counter. Введите порт взаимодействия с PostgreSQL (по умолчанию 5432): " 5432
  postgres_port=$common

  get_string "$counter. Введите имя пользователя для доступа к базе данных PostgreSQL (по умолчанию postgres): " "postgres"
  postgres_user=$common

  get_string "$counter. Введите пароль для доступа к базе данных PostgreSQL (по умолчанию postgres): " "postgres"
  postgres_password=$common

  get_string "$counter. Введите название базы данных PostgreSQL (по умолчанию file_sharing_bot_db): " "file_sharing_bot_db"
  postgres_db=$common

  # Замена портов и параметров в docker-compose.yml для контейнера postgres
  sed -i "25s/hostname:.*/hostname: $postgres_hostname/" ./docker-compose.yml
  sed -i "s/[0-9]*:5432/$postgres_port:5432/" ./docker-compose.yml
  sed -i "s/POSTGRES_USER=.*/POSTGRES_USER=$postgres_user/" ./docker-compose.yml
  sed -i "s/POSTGRES_PASSWORD=.*/POSTGRES_PASSWORD=$postgres_password/" ./docker-compose.yml
  sed -i "s/POSTGRES_DB=.*/POSTGRES_DB=$postgres_db/" ./docker-compose.yml
else
  skip_spring_configuration=1
fi

echo
echo "--------------------| Параметры Spring Boot микросервисов |--------------------"
echo

if [ "$skip_spring_configuration" = 0 ]
then
  get_answer "Хотите ли выполнить конфигурирование Spring Boot микросервисов?" 1 0
  if [ "$common" = 1 ]
  then
    echo
    counter=1

    get_port "$counter. Введите порт для первого микросервиса: "
    message_gateway_port=$common

    get_port "$counter. Введите порт для второго микросервиса: "
    message_handler_port=$common

    get_port "$counter. Введите порт для третьего микросервиса: "
    rest_service_port=$common

    get_port "$counter. Введите порт для четвертого микросервиса: "
    mail_service_port=$common

    get_string "$counter. Введите имя вашего бота, созданного через @BotFather (например some_bot): "
    telegram_bot_username=$common

    get_string "$counter. Введите токен вашего бота, указанный в @BotFather: "
    telegram_bot_token=$common

    get_number "$counter. Введите ваш user id администратора (можно узнать через @getmyid_bot): "
    telegram_creator_user_id=$common

    get_answer "$counter. Будет ли ваш сайт для загрузки файлов использовать HTTPS?" "https" "http"
    protocol=$common

    get_string "$counter. Введите доменное имя вашего сайта для загрузки файлов (например, example.com): "
    address=$common

    get_string "$counter. Введите адрес почтового сервера (например, smtp.example.com): "
    mail_host=$common

    get_number "$counter. Укажите порт для подключения по SMTP (например, 465): "
    mail_port=$common

    get_string "$counter. Укажите ваш адрес электронной почты (например, bot@example.com): "
    mail_username=$common

    get_string "$counter. Укажите пароль от вашей электронной почты: "
    mail_password=$common

    # Получаем ключ для шифрования параметров (указывается в application.yml)
    echo 
    echo "Устанавливаю ключ шифрования..."
    ./mvnw clean package -pl common-utils -DskipTests=true -q
    ciphering_key=$(java -jar ./common-utils/target/ciphering.jar)

    # Пути до конфигурационных файлов Spring Boot для всех микросервисов
    message_gateway_config_path="./telegram-message-gateway/src/main/resources/application.yml"
    message_handler_config_path="./message-handler/src/main/resources/application.yml"
    rest_service_config_path="./rest-service/src/main/resources/application.yml"
    mail_service_config_path="./mail-service/src/main/resources/application.yml"

    # Замена параметров в конфигурационных файлов Spring для микросервисов
    # Настройка портов микросервисов:
    sed -i "3s/:.*/: $message_gateway_port/" $message_gateway_config_path
    sed -i "3s/:.*/: $message_handler_port/" $message_handler_config_path
    sed -i "3s/:.*/: $rest_service_port/" $rest_service_config_path
    sed -i "3s/:.*/: $mail_service_port/" $mail_service_config_path

    # Устанавливаем параметры для микросервиса MessageGateway
    ## Настройка соединения с Telegram ботом
    sed -i "7s/:.*/: $telegram_bot_username/" $message_gateway_config_path
    sed -i "8s/:.*/: $telegram_bot_token/" $message_gateway_config_path
    ## Настройка соединения с RabbitMQ
    sed -i "26s/:.*/: $rabbitmq_port/" $message_gateway_config_path
    sed -i "27s/:.*/: $rabbitmq_user/" $message_gateway_config_path
    sed -i "28s/:.*/: $rabbitmq_password/" $message_gateway_config_path
    sed -i "29s/:.*/: $rabbitmq_vhost/" $message_gateway_config_path

    # Устанавливаем параметры для микросервиса MessageHandler
    ## Настройка соединения с RabbitMQ
    sed -i "9s/:.*/: $rabbitmq_port/" $message_handler_config_path
    sed -i "10s/:.*/: $rabbitmq_user/" $message_handler_config_path
    sed -i "11s/:.*/: $rabbitmq_password/" $message_handler_config_path
    sed -i "12s/:.*/: $rabbitmq_vhost/" $message_handler_config_path
    ## Настройка соединения с базой данных Postgres
    sed -i "15s/:.*/: $postgres_user/" $message_handler_config_path
    sed -i "16s/:.*/: $postgres_password/" $message_handler_config_path
    sed -i "17s/:.*/: jdbc:postgresql:\/\/localhost:$postgres_port\/$postgres_db/" $message_handler_config_path
    ## Настройка данных Telegram бота
    sed -i "57s/:.*/: $telegram_bot_token/" $message_handler_config_path
    sed -i "58s/:.*/: $telegram_creator_user_id/" $message_handler_config_path
    ## Задаем параметры адреса, по которому будут загружаться файлы
    sed -i "64s/:.*/: $protocol/" $message_handler_config_path
    sed -i "65s/:.*/: $address/" $message_handler_config_path
    ## Задаём ключ шифрования параметров
    awk -i inplace -v key="$ciphering_key" 'NR==66{$0 = gensub(/:.*/, ": " key, 1)} {print}' "$message_handler_config_path"
    ## Задаем порты, указывающие на третий и четвёртый микросервисы
    sed -i "68s/:.*/: $rest_service_port/" $message_handler_config_path
    sed -i "69s/:.*/: $mail_service_port/" $message_handler_config_path

    # Устанавливаем параметры для микросервиса Rest Service
    ## Настройка соединения с базой данных Postgres
    sed -i "21s/:.*/: $postgres_user/" $rest_service_config_path
    sed -i "22s/:.*/: $postgres_password/" $rest_service_config_path
    sed -i "23s/:.*/: jdbc:postgresql:\/\/localhost:$postgres_port\/$postgres_db/" $rest_service_config_path
    ## Задаём ключ шифрования параметров
    awk -i inplace -v key="$ciphering_key" 'NR==29{$0 = gensub(/:.*/, ": " key, 1)} {print}' "$rest_service_config_path"
    ## Задаем параметры адреса, по которому будут загружаться файлы
    sed -i "30s/:.*/: $protocol/" $rest_service_config_path
    sed -i "31s/:.*/: $address/" $rest_service_config_path
    ## Задаем порт, указывающий на второй микросервис
    sed -i "33s/:.*/: $message_handler_port/" $rest_service_config_path

    # Устанавливаем параметры для микросервиса Mail Service
    ## Настройка соединения с почтовым сервером
    sed -i "20s/:.*/: $mail_host/" $mail_service_config_path
    sed -i "22s/:.*/: $mail_port/" $mail_service_config_path
    sed -i "23s/:.*/: $mail_username/" $mail_service_config_path
    sed -i "24s/:.*/: $mail_password/" $mail_service_config_path
    ## Задаем имя бота
    sed -i "35s/:.*/: $telegram_bot_username/" $mail_service_config_path
    ## Задаем настройки для создания ссылок и порт Rest Service
    sed -i "37s/:.*/: $protocol/" $mail_service_config_path
    sed -i "38s/:.*/: $address/" $mail_service_config_path
    sed -i "40s/:.*/: $rest_service_port/" $mail_service_config_path

    echo
    echo "Установлен ключ шифрования параметров: $ciphering_key"
  fi
else
  echo "Конфигурирование Spring Boot микросервисов было пропущено, потому что не были настроены контейнеры DOCKER"
fi

echo
echo "--------------------| Инициализация Docker контейнеров |--------------------"
echo

#Поднимаем контейнеры
get_answer "Поднять docker-контейнеры для PostgreSQL и RabbitMQ?" 1 0
if [ "$common" = 1 ]
then
    echo
    docker compose down -v
    echo "Удалены старые контейнеры и соответствующие volume"
    echo
    docker compose up --build --detach

    echo
    echo "DOCKER КОНТЕЙНЕРЫ БЫЛИ ЗАПУЩЕНЫ!"
else
    echo
    echo "КОНФИГУРАЦИЯ ПРИЛОЖЕНИЯ УСПЕШНО БЫЛА НАСТРОЕНА!"
fi

echo
echo "--------------------| Запуск Spring Boot микросервисов |--------------------"
echo
#Запускаем микросервисы
get_answer "Запустить приложение (микросервисы)?" 1 0
if [ "$common" = 1 ]
then
    echo "Собираю приложение..."
    ./mvnw clean package -DskipTests=true -q

    echo
    echo "ПРИЛОЖЕНИЕ FILE SHARING BOT ЗАПУЩЕНО!"
    echo

    gateway=$(find ./telegram-message-gateway -type f -name "*.jar")
    java -jar "$gateway" &

    handler=$(find ./message-handler -type f -name "*.jar")
    nohup java -jar "$handler" &

    rest=$(find ./rest-service -type f -name "*.jar")
    nohup java -jar "$rest" &

    mail=$(find ./mail-service -type f -name "*.jar")
    nohup java -jar "$mail"
else
    echo
    echo "ВСЁ ГОТОВО К ЗАПУСКУ, ПОКА!"
fi