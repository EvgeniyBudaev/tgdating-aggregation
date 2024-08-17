PostGIS
```
pg_config --version // PostgreSQL 16.4 (Ubuntu 16.4-1.pgdg22.04+1)

sudo apt-get update
sudo apt install postgis postgresql-16-postgis-3
sudo -u postgres psql -c "CREATE EXTENSION postgis;" tgbot
sudo systemctl restart postgresql
```