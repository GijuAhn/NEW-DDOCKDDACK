# kakao Login 설정

[https://developers.kakao.com/](https://developers.kakao.com/) 접속

상단의 ‘내애플리케이션’ 클릭

![카카오 로그인 1](../exec_contents/kakao-login1.png)

애플리케이션 추가하기 클릭

![카카오 로그인 2](../exec_contents/kakao-login2.png)

내용 작성 후 저장

![카카오 로그인 3](../exec_contents/kakao-login3.png)

kakao API를 사용하기 위한 앱키 확인

![카카오 로그인 4](../exec_contents/kakao-login4.png)

kakao API용 Token 발급 시 보안을 강화 하기 위해 Client Secret 키 추가

![카카오 로그인 5](../exec_contents/kakao-login5.png)

REST API에서 사용하기 위해 kakao-login AccessToken 발급하기 위한 Redirrect URI를 설정

![카카오 로그인 6](../exec_contents/kakao-login6.png)

profile 설정을 위한 필수 동의 설정

![카카오 로그인 7](../exec_contents/kakao-login7.png)

Spring-security 설정

Provider를 직접 작성해서 연결

![카카오 로그인 8](../exec_contents/kakao-login8.png)
