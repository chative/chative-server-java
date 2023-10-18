# install build tools

## downloads
maven 3.3.9
http://apache.communilink.net/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.zip
## jdk8u181
http://download.oracle.com/otn-pub/java/jdk/8u181-b13/96a7b8442fe848ef90c96a2fad6ed6d1/jdk-8u181-windows-x64.exe

## install go

https://go.dev/doc/install

## psql & redis

docker-compose up -d
sudo apt install -y postgresql-client redis-tools

## postgresql
sudo apt install -y curl ca-certificates gnupg
curl https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -
sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/postgresql.list'
sudo apt update -y
sudo apt install postgresql-client-12
su postgres
	psql
		\password postgres
		\q
	exit

## redis
apt install redis-server
sudo vim /etc/redis/redis.conf
	requirepass
sudo service redis-server restart



## cert (compatible with Apple devices)
DOMAIN="nnno6ahudk5x.optillel.com"
openssl req -new -x509 -newkey rsa:2048 -sha256 -nodes -keyout $DOMAIN.key -days 825 -out $DOMAIN.crt -config cert.conf
openssl pkcs12 -export -in $DOMAIN.crt -inkey $DOMAIN.key -out $DOMAIN.p12 -name "$DOMAIN"
keytool -importkeystore -srckeystore $DOMAIN.p12 -srcstoretype PKCS12 -deststoretype JKS -destkeystore $DOMAIN.jks
keytool -import -alias SignalSecret -file $DOMAIN.crt -keystore $DOMAIN.store -storetype BKS -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath "/Applications/Android Studio.app/Contents/lib/bcprov-jdk15on-1.64.jar"
keytool -export -alias $DOMAIN -keystore $DOMAIN.jks -storepass WYWtGXZySVqVHEnja5fJfgrHBRszJgEH -file $DOMAIN.cer

## [deprecated] cert
openssl req -new -newkey rsa:4096 -x509 -sha256 -days 730 -nodes -out room.jdcloud.com.crt -keyout room.jdcloud.com.key
openssl pkcs12 -export -in room.jdcloud.com.crt -inkey room.jdcloud.com.key -out room.jdcloud.com.p12 -name "room.jdcloud.com" 
keytool -importkeystore -srckeystore room.jdcloud.com.p12 -srcstoretype PKCS12 -deststoretype JKS -destkeystore room.jdcloud.com.jks
keytool -import -alias SignalSecret -file room.jdcloud.com.crt -keystore room.jdcloud.com.store -storetype BKS -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath "C:/Program Files/Android/Android Studio/lib/bcprov-jdk15on-1.59.jar"
keytool -export -alias room.jdcloud.com -keystore room.jdcloud.com.jks -storepass whisper -file room.jdcloud.com.cer



# build

# Java program

export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home"
mvn package   -DskipTests

# Go program

- go build



# Setup Server

- Init DataBase

psql -h localhost -U postgres -W
    create database accountdb;
    create database messagedb;
    \q

java -jar TextSecureServer-1.88.jar accountdb migrate config/config.yml
java -jar TextSecureServer-1.88.jar messagedb migrate config/config.yml

- Start go server

  ./chative-server-go

- Start java

java -jar TextSecureServer-1.88.jar server config/config.yml


