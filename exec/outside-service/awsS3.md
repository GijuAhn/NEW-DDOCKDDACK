# aws s3 설정

![AWS S3 1](../exec_contents/aws-s3-1.png)

버킷 만들기 클릭

![AWS S3 2](../exec_contents/aws-s3-2.png)

![AWS S3 3](../exec_contents/aws-s3-3.png)

- 버킷 이름은 고유하게 설정
- AWS 리전은 아시아 태평양 ap-northeast-2 선택

![AWS S3 4](../exec_contents/aws-s3-4.png)

- ACL 활성화 선택

![AWS S3 5](../exec_contents/aws-s3-5.png)

- 모든 퍼블릭 액세스 차단 해제
  - 새 ACL(액세스 제어 목록)을 통해 부여된 버킷 및 객체에 대한 퍼블릭 액세스 차단을 해제해서 객체를 업로드 할 수 있게 함
  - 임의의 ACL(액세스 제어 목록)을 통해 부여된 버킷 및 객체에 대한 퍼블릭 액세스 차단을 해제해서 업로드한 객체를 볼수 있게 함

![AWS S3 6](../exec_contents/aws-s3-6.png)

버킷 생성 설정이 끝나면 하단에 버킷만들기 클릭

보안 자격 증명(IAM) 설정(Access Key, Secret Key 발급**)**

![AWS S3 7](../exec_contents/aws-s3-7.png)

![AWS S3 8](../exec_contents/aws-s3-8.png)

![AWS S3 9](../exec_contents/aws-s3-9.png)

- 왼쪽 액세스 관리 메뉴에서 사용자 클릭 후 사용자 추가

![AWS S3 10](../exec_contents/aws-s3-10.png)

![AWS S3 11](../exec_contents/aws-s3-11.png)

- [사용자 추가] - [기존 정책 직접 연결] - S3 검색 - 'AmazonS3FullAccess' 체크

![AWS S3 12](../exec_contents/aws-s3-12.png)

![AWS S3 13](../exec_contents/aws-s3-13.png)

사용자 생성후 나오는 Access Key, Secret Key 확인 후 저장 (spring boot에서 사용)

Gradle에 Dependency 추가

![AWS S3 14](../exec_contents/aws-s3-14.png)

- 맨 아래 spring-ckoud-starter-aws 추가

application\*.yml (프로퍼티 설정)

![AWS S3 15](../exec_contents/aws-s3-15.png)

- bucket - 버킷 생성시 작성했던 버킷 명
- access-key , secret-key : IAM 설정 시 발급 받은 키 입력
