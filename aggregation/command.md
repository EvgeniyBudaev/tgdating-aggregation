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