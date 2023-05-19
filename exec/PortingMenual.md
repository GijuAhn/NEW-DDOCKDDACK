# 1. Docker 설치(Ubuntu 20.04 LTS)

---

## 기존에 설치된 Docker 삭제

```bash
$ sudo apt-get remove docker docker-engine docker.io containerd runc
```

## Repository 설정

- Repository를 이용하기 위한 패키지 설치

```bash
# 1. apt 패키지 매니저 업데이트
$ sudo apt-get update

# 2. 패키지 설치
$ sudo apt-get install ca-certificates curl gnupg lsb-release

# 3. Docker 공식 GPG Key 등록
$ curl -fsSL <https://download.docker.com/linux/ubuntu/gpg> | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# 4. Stable Repository 등록
$ echo \\
  "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] <https://download.docker.com/linux/ubuntu> \\
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

## Docker 엔진 설치

```bash
#  패키지 매니저 최신화
$ sudo apt-get update
$ sudo apt-get upgrade

# Docker 엔진 설치
$ sudo apt-get install docker-ce docker-ce-cli containerd.io
```

# 2. Nginx 설정

---

## **Nginx 설치**

```bash
sudo apt update
sudo apt install nginx
```

## 방화벽 설정

- 방화벽 설정값 확인
    
    ```
    sudo ufw app list
    ```
    
- http(80), https(443) 방화벽 허용
    
    ```
    sudo ufw allow 'Nginx Full'
    ```
    
- 방화벽 가동
    
    ```
    sudo ufw enable
    ```
    

## SSL/TLS 접속을 위한 인증서 발급

- 인증서를 간단하게 발급/갱신하는 패키지 설치
    
    ```
    sudo snap install --classic certbot
    ```
    
- certbot을 활용하여 인증서 발급(내부적으로 Let’s encrypt를 거쳐 인증서를 발급해줌)
    
    ```
    sudo certbot --nginx
    
    ## nginx config file 을 만들지 않고 ssl file 만 필요한 경우
    sudo certbot certonly --nginx
    ```
    
- /etc/nginx/sites-available 에서 생성된 설정파일 확인
    
    ```
    server{
    
        listen 443 ssl; # managed by Certbot
        server_name [당신의 도메인명] www.[당신의 도메인명];
    
        ssl_certificate /etc/letsencrypt/live/[당신의 도메인명]/fullchain.pem; # managed by Certbot
        ssl_certificate_key /etc/letsencrypt/live/[당신의 도메인명]/privkey.pem; # managed by Certbot
    
        include /etc/letsencrypt/options-ssl-nginx.conf; # managed by Certbot
        ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem; # managed by Certbot
    
    }
    
    server{
    
    		if ($host = www.[당신의 도메인명]) {
          	return 301 https://$host$request_uri;
      	} # managed by Certbot
    
      	if ($host = [당신의 도메인명]) {
          	return 301 https://$host$request_uri;
      	} # managed by Certbot
    
    	listen 80;
    	server_name [당신의 도메인명];
    	return 404; # managed by Certbot
    
    }
    ```
    

## 포트포워딩 설정

- 각 서버에 할당할 포트를 정한 뒤 URL로 매핑
    - vue → root
    - Spring Boot → 9999
    - Prometheus → 9090
    - Grafana → 3000
    - Openvidu → 4443
    
    ```
    # /etc/nginx/sites-available/default
    upstream spring {
    	server localhost:9999;
    }
    
    upstream prometheus {
    	server localhost:9090;
    }
    
    upstream grafana {
    	server localhost:3000;
    }
    
    server{
    
    	...
    
    	root [프로젝트 루트 경로]/frontend/dist;
    	index index.html index.htm index.nginx-debian.html;
    
    	...
    
    	location / {
    
    		try_files $uri $uri/ /index.html;
    
    	}
    
    	location /api/ {
    
    		proxy_pass http://spring/;
    
    	}
    
    	location /openvidu {
    
    		proxy_set_header Upgrade $http_upgrade;
    		proxy_set_header Connection $connection_upgrade;
    		proxy_http_version 1.1;
    
    		proxy_pass https://[당신의 도메인명]:4443/;
    
    	}
    
    	location /prometheus/ {
    
    		proxy_pass http://prometheus/;
    
    	        proxy_set_header Accept-Encoding "";
    	        proxy_set_header Host $host;
    	        proxy_set_header X-Real-IP $remote_addr;
    	        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    	        proxy_set_header X-Forwarded-Proto $scheme;
    
    	}
    
      	location /grafana/ {
        		rewrite  ^/grafana/(.*)  /$1 break;
        		proxy_set_header Host $http_host;
    	    	proxy_pass http://grafana;
      	}
    
      	location /grafana/api/live/ {
        		rewrite  ^/grafana/(.*)  /$1 break;
    	        proxy_http_version 1.1;
    	        proxy_set_header Upgrade $http_upgrade;
    	        proxy_set_header Connection $connection_upgrade;
    	        proxy_set_header Host $http_host;
    	        proxy_pass http://grafana;
      	}
    
    	...
    
    }
    ```
    

# 3. FE 배포

---

## Node.js & Vue-CLI 설치

```
# nodejs 설치
sudo apt install nodejs

# node 설치 확인
nodejs -v

# vue-cli 설치
npm install -g @vue/cli

# Vue-cli 설치 확인
vue -V
```

## FE 소스코드 빌드

- FE 프로젝트 디렉토리로 이동

```
cd frontend
```

- 소스코드 빌드

```
# 빌드 진행
npm run prod
```

# 4. BE 서버 배포

---

## JDK 설치(zulu 11.0.18+10)

```
# zulu 11버전 jdk 설치
sudo apt install zulu11-jdk

# jdk 설치 확인
java -version
```

## BE 소스코드 빌드

- BE 프로젝트 디렉토리로 이동

```
cd Backend
```

- 빌드 시 필요한 환경변수 파일을 직접 입력

```bash
vi src/main/resources/application-env.yml

## vi 에디터 상에서 하단의 내용 작성
spring:
  redis:
    host: [당신의 도메인명]
    port: 6379
    password: [당신의 redis 접속 비밀번호]

  datasource:
    url: jdbc:mysql://[당신의 도메인명]:3306/[당신의 DB명]?serverTimeZone=KST
    username: [당신의 DB 접속 유저명]
    password: [당신의 DB 접속 비밀번호]
    driver-class-name: com.mysql.cj.jdbc.Driver
  security:
    oauth2:
      client:
        registration:
          kakao:
            client-id: [당신의 카카오 클라이언트 아이디]
            redirect-uri: https://[당신의 도메인명]/api/login/oauth2/code/kakao
            client-authentication-method: POST
            client-secret: [당신의 카카오 시크릿 키]
            authorization-grant-type: authorization_code
            scope:
              - profile_nickname
              - account_email
            client_name: kakao
          google:
            client_id: [당신의 구글 클라이언트 아이디]
            client_secret: [당신의 구글 클라이언트 시크릿 키]
            redirect_uri: https://[당신의 도메인명]/api/login/oauth2/code/google
            scope:
              - email
              - profile
        provider:
          kakao:
            authorization-uri: https://kauth.kakao.com/oauth/authorize
            token-uri: https://kauth.kakao.com/oauth/token
            user-info-uri: https://kapi.kakao.com/v2/user/me
            user-name-attribute: id
jwt:
  token:
    secret-key: [당신의 jwt 시크릿 키]
  access-token:
    expire-length: 8640000 #하루 #10분 300000 #5분
  refresh-token:
    expire-length: 1209600000 #14일

cloud:
  aws:
    s3:
      bucket: [당신의 AWS S3 버킷 이름]
    credentials:
      access-key: [당신의 AWS 엑세스 키]
      secret-key: [당신의 AWS 시크릿 키]
    region:
      static: ap-northeast-2
      auto: false
    stack:
      auto: false

IMAGE_PATH: [당신의 S3 공개 도메인 주소]
OPENVIDU_URL: https://[당신의 도메인명]:4443/
OPENVIDU_SECRET: [당신의 오픈비두 시크릿 키]
OPENVIDU_HEADER: Basic [오픈비두 시크릿 키를 Base64로 인코딩한 값]
OPENVIDU_API_URL: https://[당신의 도메인명]:4443/openvidu/api/signal

LOGIN_SUCCESS_URL: https://[당신의 도메인명]/login-success?accessToken=
```

- Gradle 빌드

```
# gradlew에 권한 추가
chmod +x gradlew

# 캐시 초기화 & 빌드 진행
./gradlew clean build
```

## Docker 이미지 빌드

- Dockerfile 작성

```
# 빈 Dockerfile 생성
touch Dockerfile

# Dockerfile 편집 모드로 열기
vi Dockerfile
```

```
## Dockerfile 작성 내용 ##

# zulu JDK 공식 도커이미지 11.0.18 버전 가져오기
FROM azul/zulu-openjdk:11.0.18

# 9999번 포트 노출
EXPOSE 9999

# 빌드후 생성된 jar 파일을 컨테이너 내부에 복사
COPY /build/libs/ddockddack-0.0.1-SNAPSHOT.jar app.jar

# jar 파일 실행(= BE 서버 실행)
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Docker compose 파일 작성

```
version: "3.7"

services:
  ddockddack-server:
    container_name: ddockddack-server
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - 9999:9999
    restart: always
```

## Docker 이미지 빌드 & 컨테이너 실행

```
docker-compose up -d --build
```

# 5. Redis 컨테이너 실행

---

## Docker compose 파일 작성

```
version: "3.7"

services:
  redis-server:
    image: redis:latest
    container_name: redis-server
    volumes:
      - ./data:/data
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    network_mode: host
    command: redis-server /usr/local/etc/redis/redis.conf
```

## Redis 설정 파일 작성

```
# 포트번호 설정
port 6379

# AOF 를 통해 failover 된 레디스 노드 재 시작시 이전 데이터를 다시 로드해 올 수 있습니다.
appendonly yes

# 패스워드 설정
requirepass [당신의 Redis 비밀번호]
```

## Docker 컨테이너 실행

```
docker-compose up -d
```

# 6. MySQL 컨테이너 실행

---

## Docker compose 파일 작성

```bash
version: "3.7"

services:
  mysql-server:
    container_name: mysql-server
    image: mysql:latest
    restart: always
    volumes:
      - ./db/data:/var/lib/mysql
      - ./db/conf.d:/etc/mysql/conf.d
      - ./db/initdb.d:/docker-entrypoint-initdb.d
    env_file: .env
    ports:
      - 3306:3306
    environment:
      - TZ=Asia/Seoul
```

## 디렉토리 및 설정파일 생성

- `docker-compose.yml` 파일이 위치한 디렉토리에 `db`라는 빈 디렉토리 생성
    - `db` 디렉토리 하위에 `conf.d`, `data`, `initdb.d` 디렉토리 생성
- `conf.d`에 `my.cnf` 파일 만들기
    
    ```yaml
    [client]
    default-character-set = utf8mb4
    
    [mysql]
    default-character-set = utf8mb4
    
    [mysqld]
    character-set-client-handshake = FALSE
    character-set-server           = utf8mb4
    collation-server               = utf8mb4_unicode_ci
    ```
    
- `initdb.d`에 빈 `create_table.sql`, `load_data.sql` 파일 생성
    - `create_table.sql` ⇒ 테이블 정의 쿼리 작성
    - `load_data.sql` ⇒ 초기 데이터 insert 쿼리 작성
- `.env` 파일에 계정정보 설정
    
    ```jsx
    MYSQL_HOST=localhost
    MYSQL_PORT=3306
    MYSQL_ROOT_PASSWORD=root!
    MYSQL_DATABASE=students
    MYSQL_USER=inti
    MYSQL_PASSWORD=inti1234
    ```
    
- 최종 디렉토리 구조
    
    ```yaml
    - db
      - conf.d
        - my.cnf
      - data
        - ...
      - initdb.d
        - create_table.sql
        - load_data.sql
    - docker-compose.yml
    - .env
    ```
    

## Docker 컨테이너 실행

```bash
docker-compose up -d
```

# 7. Prometheus 컨테이너 실행

---

## Docker compose 파일 작성

```bash
version: "3"

services:
  prometheus-server:
    image: prom/prometheus
    container_name: prometheus-server
    privileged: true
    ports:
      - 9090:9090
    volumes:
      - ./config:/etc/prometheus
    command:
      - "--config.file=/etc/prometheus/prometheus.yml"
      - "--web.route-prefix=/"
      - "--web.external-url=https://[당신의 도메인명]/prometheus"
```

## 디렉토리 및 설정파일 작성

- 디렉토리 구조
    
    ```yaml
    - config
      - prometheus.yml
      - scrape-config
        - prometheus.yml
        - spring-boot.yml
    - docker-compose.yml
    ```
    
- 설정파일 구성
    
    ```bash
    ## config/prometheus.yml
    global:
      scrape_interval: 15s
      scrape_timeout: 15s
      evaluation_interval: 15s
    
    scrape_config_files:
      - ./scrape-config/prometheus.yml
      - ./scrape-config/spring-boot.yml
    
    ## config/scrape-config/prometheus.yml
    scrape_configs:
      - job_name: 'prometheus'
        static_configs:
          - targets: ['localhost:9090']
    
    ## config/scrape-config/spring-boot.yml
    scrape_configs:
      - job_name: spring-boot
        scrape_interval: 5s
        metrics_path: /api/actuator/prometheus
        static_configs:
          - targets: ['[당신의 도메인명]']
    ```
    

## Docker 컨테이너 실행

```bash
docker-compose up -d
```

# 8. Prometheus 컨테이너 실행

---

## Docker compose 파일 작성

```bash
version: "3"

services:
  grafana-server:
    image: grafana/grafana-oss
    container_name: grafana-server
    privileged: true
    ports:
      - 3000:3000
    volumes:
      - ./grafana.ini:/etc/grafana/grafana.ini
```

## 디렉토리 및 설정파일 작성

- 디렉토리 구조
    
    ```yaml
    - grafana.ini
    - docker-compose.yml
    ```
    
- 설정파일 구성
    - `[도메인주소]/grafana` 로 접속가능하도록 하는 설정
    
    ```bash
    ## grafana.ini
    [server]
    root_url = %(protocol)s://%(domain)s:%(http_port)s/grafana/
    serve_from_sub_path = true
    ```
    

## Docker 컨테이너 실행

```bash
docker-compose up -d
```

# 9. 오픈비두 배포

---

- 오픈비두를 배포하기 root 권한을 얻어야 함

```
sudo su
```

- 오픈비두를 설치하기 위해 권장되는 경로인 `/opt`로 이동

```
cd /opt
```

- 오픈비두 설치

```
curl <https://s3-eu-west-1.amazonaws.com/aws.openvidu.io/install_openvidu_latest.sh> | bash
```

- 설치 후 오픈비두가 설치된 경로로 이동

```
$ cd openvidu
```

- 도메인 또는 퍼블릭IP와 오픈비두와 통신을 위한 환경설정

```
$ nano .env
# OpenVidu configuration
# ----------------------
# 도메인 또는 퍼블릭IP 주소
DOMAIN_OR_PUBLIC_IP=[당신의 도메인명]
# 오픈비두 서버와 통신을 위한 시크릿
OPENVIDU_SECRET=[당신의 시크릿 코드]
# Certificate type (selfsigned, owncert, letsencrypt)
CERTIFICATE_TYPE=letsencrypt
# 인증서 타입이 letsencrypt일 경우 이메일 설정
LETSENCRYPT_EMAIL=[당신의 이메일]
# http, https 포트 변경(기존 NginX를 사용하기 위함)
HTTP_PORT=4080
HTTPS_PORT=4443
```

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
   * OpenVidu Server: https://[당신의 도메인명]:4443/
   * OpenVidu Dashboard: https://[당신의 도메인명]:4443/dashboard/
----------------------------------------------------
```
