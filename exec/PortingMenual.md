# Openvidu 백엔드 및 프론트엔드 배포, SSL 인증서 적용

## Openvidu 배포

---

- 오픈비두를 배포하기 root 권한을 얻어야 함

```bash
sudo su
```

- 오픈비두를 설치하기 위해 권장되는 경로인 `/opt`로 이동

```bash
cd /opt
```

- 오픈비두 설치

```bash
curl <https://s3-eu-west-1.amazonaws.com/aws.openvidu.io/install_openvidu_latest.sh> | bash
```

- 설치 후 오픈비두가 설치된 경로로 이동

```bash
$ cd openvidu
```

- 도메인 또는 퍼블릭IP와 오픈비두와 통신을 위한 환경설정

````bash
$ nano .env
# OpenVidu configuration
# ----------------------
# 도메인 또는 퍼블릭IP 주소
DOMAIN_OR_PUBLIC_IP={당신의 퍼블릭 IP}
# 오픈비두 서버와 통신을 위한 시크릿
OPENVIDU_SECRET={당신의 비밀 코드}
# Certificate type (selfsigned, owncert, letsencrypt)
CERTIFICATE_TYPE=letsencrypt
# 인증서 타입이 letsencrypt일 경우 이메일 설정
LETSENCRYPT_EMAIL={당신의 이메일}

- 설정 후 오픈비두 서버 실행(`ctrl + c`를 누르면 백그라운드로 실행됨)

```bash
$ ./openvidu start
Creating openvidu-docker-compose_coturn_1          ... done
Creating openvidu-docker-compose_app_1             ... done
Creating openvidu-docker-compose_kms_1             ... done
Creating openvidu-docker-compose_nginx_1           ... done
Creating openvidu-docker-compose_redis_1           ... done
Creating openvidu-docker-compose_openvidu-server_1 ... done
----------------------------------------------------
   OpenVidu Platform is ready!
   ---------------------------
   * OpenVidu Server: https://DOMAIN_OR_PUBLIC_IP/
   * OpenVidu Dashboard: https://DOMAIN_OR_PUBLIC_IP/dashboard/
----------------------------------------------------
````

- openvidu-nginx 설정 수정

```bash
docker exec -it openvidu-nginx-1 bash

vi /etc/nginx/conf.d/default.conf

# default.conf 수정할 부분
# Your App

server {
    listen 80;
    listen [::]:80;
    server_name {당신의 도메인};

    # Redirect to https
    location / {
        rewrite ^(.*) https://{당신의 도메인}:443$1 permanent;
    }

    location /nginx_status {
        stub_status;
        allow 127.0.0.1;        #only allow requests from localhost
        allow 172.17.0.1;       # 추가
        deny all;               #deny all other hosts
    }
}



server {
    listen 443 ssl;
    listen [::]:443 ssl;
    server_name {당신의 도메인};

    # Your App
    location / {
        proxy_pass http://172.17.0.1:7080;
    }

    include /etc/nginx/conf.d/service-url.inc;

    location /api {
        proxy_pass          http://172.17.0.1:8081;
    }

    location /static {
        proxy_pass          http://172.17.0.1:8081;
    }

    location /login {
        proxy_pass          http://172.17.0.1:8081;
    }

    location /oauth2 {
        proxy_pass          http://172.17.0.1:8081;
    }

    location /login-success {
        proxy_pass          http://172.17.0.1:7080;
    }

}
```

## google, kakao, S3 설정

- [kakao share](./outside-service/kakaoShare.md)
- [kakao login](./outside-service/kakaoLogin.md)
- [google login](./outside-service/GoogleLogin.md)
- [aws s3](./outside-service/awsS3.md)

## 버전

IDE 버전

- Intellij 2022.2.3 / Intellij 2020.1.1

JVM, jdk (java) 버전

- IDE Azul Zulu version 11.0.18
- SEVER Docker img adoptopenjdk/openjdk11

배포 라이브러리 버전

- Gradle 7.6

## git clone 및 배포 과정

### 1. git clone

```bash
git clone https://lab.ssafy.com/s08-webmobile1-sub2/S08P12A409.git
```

### 2. mySql 설치

해당 프로젝트에서는 mySql을 사용하였습니다. 작업 전 선행 설치를 바랍니다.

```bash
apt update
apt install mysql-server

sudo mysql -u root -p

create database ddockddack;
```

포트는 변경되어도 상관없으나 기본적으로 3306 포트를 상정하겠습니다. <br>
mySql 유저 생성과 root 비밀번호 설정을 해주시기 바랍니다.

### 3. 프론트엔드 빌드 및 배포

- 프로젝트 폴더 내에 있는 frontend 디렉토리의 루트 경로에서 다음의 명령어를 실행합니다.
- frontend 경로에 다음과 같은 Dockerfile이 있습니다. 이를 이용하여 Docker Container를 이용하여 프론트엔드를 배포할 준비를 합니다.
- Nginx와 vue가 함께 배포됩니다.

```bash
# Dockerfile
# nginx 이미지를 사용합니다. 뒤에 tag가 없으면 latest 를 사용합니다.
FROM nginx
# root 에 app 폴더를 생성
RUN mkdir /app
# work dir 고정
WORKDIR /app
# work dir 에 build 폴더 생성 /app/build
RUN mkdir ./dist
# host pc의 현재경로의 build 폴더를 workdir 의 build 폴더로 복사
ADD ./dist ./dist
# nginx 의 default.conf 를 삭제
RUN rm /etc/nginx/conf.d/default.conf
# host pc 의 nginx.conf 를 아래 경로에 복사
COPY ./default.conf /etc/nginx/conf.d/default.conf
# 7080 포트 오픈
EXPOSE 7080
# container 실행 시 자동으로 실행할 command. nginx 시작함
CMD ["nginx", "-g", "daemon off;"]
```

이후에는 다음의 명령어를 차례로 입력하여 module 설치 및 빌드, docker 이미지를 만드는 과정을 거칩니다. 그 이후에 배포를 완료합니다.

```bash
# module 설치
npm install
# 빌드 파일 생성
npm run prod
# 도커 이미지 빌드
docker build -t nginx-server .
# 도커 컨테이너를 이용한 프론트엔드 배포
docker run --name nginx-server -d -p 7080:7080 nginx-server
```

### 4. 백엔드 빌드 및 배포 과정

- 프로젝트 폴더 내에 있는 backend 디렉토리의 루트 경로에서 다음의 명령어를 실행합니다.
- backend 경로에 다음과 같은 Dockerfile이 있습니다. 이를 이용하여 Docker Container를 이용하여 프론트엔드를 배포할 준비를 합니다.

```bash
# Dockerfile
FROM adoptopenjdk/openjdk11
ARG JAR_FILE=*SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

```bash
# docker-compose
version: "3"

services:
  application:
      image: ddockddack
      restart: always
      container_name: ddockddack-server
      privileged: true
      environment:
        container: docker
        SPRING_PROFILES_ACTIVE: green
        SPRING_DATASOURCE_URL: jdbc:mysql://172.17.0.1:3306/ddockddack?serverTimeZone=UTC
        SPRING_DATASOURCE_USERNAME: {MySql 유저 아이디}
        SPRING_DATASOURCE_PASSWORD: {MySql 유저 비밀번호}
        OPENVIDU_URL: http://172.17.0.1:5443/
        OPENVIDU_SECRET: {당신의 비밀 코드}
        OPENVIDU_HEADER: {당신의 비밀코드를 base 64로 암호화 한 값}
        LOGIN_SUCCESS_URL: https://{당신의 퍼블릭 IP}/login-success?accessToken=
        JWT_TOKEN_SECRET_KEY: {당신의 키 값}
        KAKAO_CLIENT_ID: {당신의 카카오 클라이언트 아이디}
        KAKAO_REDIRECT_URL: https://{당신의 퍼블릭 IP}/login/oauth2/code/kakao
        KAKAO_CLIENT_SECRET: {당신의 카카오 비밀 코드}
        GOOGLE_CLIENT_ID: {당신의 구글 클라이언트 아이디}
        GOOGLE_CLIENT_SECRET: {당신의 구글 비밀 코드}
        GOOGLE_REDIRECT_URI: https://{당신의 퍼블릭 IP}/login/oauth2/code/google
        AWS_S3_BUCKET: {당신의 s3 BUCKET 이름}
        AWS_ACCESS_KEY: {당신의 s3 엑세스 키}
        AWS_SECRET_KEY: {당신의 s3 비밀 키}
      ports:
        - 8081:8080
      network_mode: bridge
```

이후에는 다음의 명령어를 차례로 입력하여 module 설치 및 빌드, docker 이미지를 만드는 과정을 거칩니다. 그 이후에 배포를 완료합니다.

```bash
gradle clean build
docker build -t ddockddack .
docker-compose up
```

### 5. Nginx 설정과 ssl 인증서 발급 및 적용

Openvidu 같은 경우, 카메라를 사용하기 위해서는 반드시 https로 이용해야 하기에 SSL 인증서를 발급받아야 합니다만, 해당 작업은 .env 파일을 정상적으로 수정했다면 Openvidu 컨테이너 작동 시 알아서 발급합니다.
