JDK
```
Установка JDK
sudo dpkg -i jdk-22_linux-x64_bin.deb
если есть ошибки с libc6-i386 libc-x32, то sudo apt install -y libc6-i386 libc-x32
Далее
sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/jdk-22.0.2-oracle-x64/bin/java 1
sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/jdk-22.0.2-oracle-x64/bin/javac 1
sudo update-alternatives --config java
ls /usr/lib/jvm/
Список JDK
sudo dpkg --list | grep -i jdk
Удалить JDK
sudo apt purge openjdk-22*
Узнать путь до установленного JDK
sudo update-alternatives --list java
В моем случае это /usr/lib/jvm/jdk-22.0.2-oracle-x64/bin/java
export JAVA_HOME=/usr/lib/jvm/jdk-22.0.2-oracle-x64/bin/java
echo $JAVA_HOME
sudo gedit /etc/environment
source /etc/environment
```

PostGIS
```
pg_config --version // PostgreSQL 16.4 (Ubuntu 16.4-1.pgdg22.04+1)

sudo apt-get update
sudo apt install postgis postgresql-16-postgis-3
sudo -u postgres psql -c "CREATE EXTENSION postgis;" tgbot
sudo systemctl restart postgresql
```

Keycloak
```
export KEYCLOAK_ADMIN=root
export KEYCLOAK_ADMIN_PASSWORD=root
./kc.sh start-dev --http-port=8181
```

Docker
Список контейнеров
```
sudo docker ps -a
```

Логи контейнера
Флаг -f смотреть логи в реальном времени
```
docker-compose logs -f
```

Список всех образов
```
sudo docker image ls
```

Список всех volumes
```
sudo docker volume ls
```

STOP все контейнеры
```
docker stop $(docker ps -a -q)
```

Удаление контейнера
```
sudo docker rm container_id
```

Удаление образа
```
sudo docker image rm id_image
```

Удаление volume
```
docker volume rm volume_name
sudo docker volume rm volume_name
```

Удаление всех контейнеров
```
docker rm -f $(docker ps -a -q)
sudo docker rm -f $(sudo docker ps -a -q)
```
Удаление всех образов
```
docker rmi -f $(docker images -q)
docker rm $(docker ps -a -q)
sudo docker rmi -f $(sudo docker images -q)
```

Удаление всех volumes
```
docker volume rm $(docker volume ls -qf dangling=true)
docker volume rm $(docker volume ls -q)
docker volume prune
docker volume prune -a
sudo docker volume prune
```

Сборка docker-образа
```
docker build . # Соберёт образ на основе Dockerfile
docker image ls # Отобразит информацию обо всех образах
```

Создание docker network
```
docker network create web-network
```