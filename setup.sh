#!/bin/bash

############# Вспомогательные функции #############
# Функция считывания порта и его проверки на занятость
used_ports=()
common=0
skip_application_deployment=0
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
    if [[ -z $value ]]
    then
      if [[ -n $default ]]
      then
        common=$default
        echo "Установлено значение по умолчанию - $default"
        break
      else
        echo "Вы не ввели данные, повторите попытку!"
      fi
    else
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
  echo "Завершение работы скрипта..."

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

  get_string "$counter. Введите hostname для RabbitMQ (по умолчанию broker): " "broker"
  rabbitmq_hostname=$common

  get_string "$counter. Введите имя пользователя для доступа к RabbitMQ (по умолчанию rabbit): " "rabbit"
  rabbitmq_username=$common

  get_string "$counter. Введите пароль для доступа к RabbitMQ (по умолчанию rabbit): " "rabbit"
  rabbitmq_password=$common

  # Инициализация параметров контейнера с брокером сообщений RabbitMQ
  sed -i "s/RABBITMQ_HOSTNAME=.*/RABBITMQ_HOSTNAME=$rabbitmq_hostname/" ./docker.env
  sed -i "s/RABBITMQ_USERNAME=.*/RABBITMQ_USERNAME=$rabbitmq_username/" ./docker.env
  sed -i "s/RABBITMQ_PASSWORD=.*/RABBITMQ_PASSWORD=$rabbitmq_password/" ./docker.env
else
  skip_application_deployment=1
fi


echo
echo "--------------------| Параметры контейнера PostgreSQL |--------------------"
echo
get_answer "Хотите ли выполнить конфигурирование PostgreSQL контейнера?" 1 0
if [ "$common" = 1 ]
then
  echo
  counter=1

  get_string "$counter. Введите hostname для PostgreSQL (по умолчанию database): " "database"
  postgres_hostname=$common

  get_string "$counter. Введите имя пользователя для доступа к базе данных PostgreSQL (по умолчанию postgres): " "postgres"
  postgres_username=$common

  get_string "$counter. Введите пароль для доступа к базе данных PostgreSQL (по умолчанию postgres): " "postgres"
  postgres_password=$common

  # Инициализация параметров контейнера с базой данных PostgreSQL
  sed -i "s/POSTGRES_HOSTNAME=.*/POSTGRES_HOSTNAME=$postgres_hostname/" ./docker.env
  sed -i "s/POSTGRES_URL=.*/POSTGRES_URL=jdbc:postgresql:\/\/$postgres_hostname:5432\/file_sharing_bot_db/" ./docker.env
  sed -i "s/POSTGRES_USERNAME=.*/POSTGRES_USERNAME=$postgres_username/" ./docker.env
  sed -i "s/POSTGRES_PASSWORD=.*/POSTGRES_PASSWORD=$postgres_password/" ./docker.env
else
  skip_application_deployment=1
fi


echo
echo "--------------------| Параметры соединения с почтовым сервером |--------------------"
echo
get_answer "Хотите ли выполнить конфигурирование параметров для связи с почтовым сервером?" 1 0
if [ "$common" = 1 ]
then
  echo
  counter=1

  get_string "$counter. Введите SMTP адрес почтового сервера (например, smtp.example.com): "
  mail_server_host=$common

  get_number "$counter. Укажите порт для подключения к почтовому серверу по SMTP (например, 465): "
  mail_server_port=$common

  get_string "$counter. Укажите ваш адрес электронной почты (например, bot@example.com): "
  email_username=$common

  get_string "$counter. Укажите пароль от вашей электронной почты: "
  email_password=$common

  # Инициализация параметров для связи с почтовым сервером
  sed -i "s/MAIL_SERVER_HOST=.*/MAIL_SERVER_HOST=$mail_server_host/" ./docker.env
  sed -i "s/MAIL_SERVER_PORT=.*/MAIL_SERVER_PORT=$mail_server_port/" ./docker.env
  sed -i "s/EMAIL_USERNAME=.*/EMAIL_USERNAME=$email_username/" ./docker.env
  sed -i "s/EMAIL_PASSWORD=.*/EMAIL_PASSWORD=$email_password/" ./docker.env
else
  skip_application_deployment=1
fi


echo
echo "--------------------| Параметры соединения с Telegram Bot |--------------------"
echo
get_answer "Хотите ли выполнить конфигурирование параметров для связи с Telegram ботом?" 1 0
if [ "$common" = 1 ]
then
  echo
  counter=1

  get_string "$counter. Введите имя вашего бота, созданного через @BotFather (например example_bot): "
  telegram_bot_name=$common

  get_string "$counter. Введите токен бота, выданный в @BotFather: "
  telegram_bot_token=$common

  get_number "$counter. Введите ваш уникальный идентификатор (user id можно узнать через @getmyid_bot): "
  telegram_bot_creator_id=$common

  # Параметры соединения с телеграм ботом
  sed -i "s/TELEGRAM_BOT_NAME=.*/TELEGRAM_BOT_NAME=$telegram_bot_name/" ./docker.env
  sed -i "s/TELEGRAM_BOT_TOKEN=.*/TELEGRAM_BOT_TOKEN=$telegram_bot_token/" ./docker.env
  sed -i "s/TELEGRAM_BOT_CREATOR_ID=.*/TELEGRAM_BOT_CREATOR_ID=$telegram_bot_creator_id/" ./docker.env
else
  skip_application_deployment=1
fi


echo
echo "--------------------| Доменное имя сайта, на котором будет развернут бот |--------------------"
echo
get_answer "Хотите ли вы установить доменное сайта?" 1 0
if [ "$common" = 1 ]
then
  echo
  counter=1

  get_string "$counter. Введите доменное имя вашего сайта для загрузки файлов (по умолчанию bot.localhost): " "bot.localhost"
  main_domain=$common

  # Инициализация доменного имени сайта
  sed -i "s/MAIN_DOMAIN=.*/MAIN_DOMAIN=$main_domain/" ./docker.env

  # Добавляем / убираем строки, отвечающие за выбор центра сертификации Let's Ecnrypt
  if [ $(echo "$main_domain" | grep -E "\.(af|by|cu|er|gn|ir|kp|lr|ru|ss|sy|zw)$") ]
  then
      sed -i "17s/.*/    tls {/" ./Caddyfile
      sed -i "18s/.*/        ca https:\/\/acme-v02.api.letsencrypt.org\/directory/" ./Caddyfile
      sed -i "19s/.*/    }/" ./Caddyfile
  else
      sed -i "17s/.*/    # tls {/" ./Caddyfile
      sed -i "18s/.*/    #     ca https:\/\/acme-v02.api.letsencrypt.org\/directory/" ./Caddyfile
      sed -i "19s/.*/    # }/" ./Caddyfile
  fi
else
  skip_application_deployment=1
fi


echo
echo "--------------------| Параметры Spring Boot микросервисов |--------------------"
echo
get_answer "Хотите ли выполнить конфигурирование Spring Boot микросервисов?" 1 0
if [ "$common" = 1 ]
then
  echo
  counter=1

  get_port "$counter. Введите порт для микросервиса message-gateway (по умолчанию 9781): " 9781
  message_gateway_port=$common

  get_port "$counter. Введите порт для микросервиса message-handler (по умолчанию 9782): " 9782
  message_handler_port=$common

  get_port "$counter. Введите порт для микросервиса rest-service (по умолчанию 9783): " 9783
  rest_service_port=$common

  get_port "$counter. Введите порт для микросервиса mail-service (по умолчанию 9784): " 9784
  mail_service_port=$common

  get_string "$counter. Введите hostname для контейнера message-gateway (по умолчанию gateway): " "gateway"
  message_gateway_hostname=$common

  get_string "$counter. Введите hostname для контейнера message-handler (по умолчанию handler): " "handler"
  message_handler_hostname=$common

  get_string "$counter. Введите hostname для контейнера rest-service (по умолчанию rest): " "rest"
  rest_service_hostname=$common

  get_string "$counter. Введите hostname для контейнера mail-service (по умолчанию mail): " "mail"
  mail_service_hostname=$common

  # Инициализация портов контейнеров с микросервисами
  sed -i "s/MESSAGE_GATEWAY_PORT=.*/MESSAGE_GATEWAY_PORT=$message_gateway_port/" ./docker.env
  sed -i "s/MESSAGE_HANDLER_PORT=.*/MESSAGE_HANDLER_PORT=$message_handler_port/" ./docker.env
  sed -i "s/REST_SERVICE_PORT=.*/REST_SERVICE_PORT=$rest_service_port/" ./docker.env
  sed -i "s/MAIL_SERVICE_PORT=.*/MAIL_SERVICE_PORT=$mail_service_port/" ./docker.env
  # Инициализация названий хостов контейнеров с микросервисами
  sed -i "s/MESSAGE_GATEWAY_HOSTNAME=.*/MESSAGE_GATEWAY_HOSTNAME=$message_gateway_hostname/" ./docker.env
  sed -i "s/MESSAGE_HANDLER_HOSTNAME=.*/MESSAGE_HANDLER_HOSTNAME=$message_handler_hostname/" ./docker.env
  sed -i "s/REST_SERVICE_HOSTNAME=.*/REST_SERVICE_HOSTNAME=$rest_service_hostname/" ./docker.env
  sed -i "s/MAIL_SERVICE_HOSTNAME=.*/MAIL_SERVICE_HOSTNAME=$mail_service_hostname/" ./docker.env

  # Генерация ключа шифрования
  echo
  echo "Генерирую ключ шифрования параметров запросов, придется немного подождать..."
  echo
  docker build -q -t keygen --build-arg SERVICE_NAME=common-utils .
  ciphering_key=$(docker run --rm keygen)

  # Инициализация ключа шифрования
  sed -i "s/CIPHERING_KEY=.*/CIPHERING_KEY=${ciphering_key////'\'/}/" ./docker.env
  echo
  echo "Ключ шифрования параметров был успешно установлен"
else
  skip_application_deployment=1
fi


echo
echo "--------------------| Запуск всех сервисов |--------------------"
echo
get_answer "Хотите ли вы запустить приложение?" 1 0
if [ "$common" = 1 ]
then
  if [ "$skip_application_deployment" = 1 ]
  then
    get_answer "Не все параметры были заданы! Вы все равно хотите запустить приложение?" 1 0
  fi

  if [ "$common" = 1 ]
  then
    docker compose --env-file ./docker.env down -v
    docker rmi -f message-gateway message-handler rest-service mail-service
    docker compose --env-file ./docker.env up -d

    echo
    echo "ПРИЛОЖЕНИЕ ЗАПУЩЕНО!"
    echo
    echo "Для остановки всех сервисов (приложения) необходимо выполнить одну из написанных ниже команд."
    echo "Команда 1: docker compose --env-file ./docker.env stop"
    echo "Команда 2: docker stop gateway handler endpoints mail caddy rabbitmq postgres"
  fi
else
  echo
  echo "КОНФИГУРАЦИЯ ПРИЛОЖЕНИЯ УСПЕШНО БЫЛА НАСТРОЕНА!"
  echo
  echo "Для запуска приложения вы должны выполнить написанную ниже команду."
  echo "Команда: docker compose --env-file ./docker.env up -d"
fi