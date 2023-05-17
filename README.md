# **똑딱**

## 똑딱 의미

'똑' 닮았다. <br>'딱' 알겠다.

## 개요

친구들끼리 사람 혹은 사물을 보며 서로 자기가 더 닮았다고 설전을 펼치신 적 있으신가요?<br>
더 이상 그럴 필요 없습니다. '똑딱'이 4가지 이미지 분석 알고리즘을 이용해 누가 더 닮았는지 알려드리겠습니다.

## 목차

1. [주요 기능](#주요-기능)
2. [개발 환경](#개발-환경)
3. [서비스 아키텍쳐](#서비스-아키텍쳐)
4. [이미지 분석 알고리즘](#이미지-분석-알고리즘)
5. [협업툴](#협업툴)
6. [기능 명세서](#기능-명세서)
7. [API 명세서](https://lab.ssafy.com/s08-webmobile1-sub2/S08P12A409/-/blob/develop/README.md#api-%EB%AA%85%EC%84%B8%EC%84%9C)
8. [화면 설계서](https://lab.ssafy.com/s08-webmobile1-sub2/S08P12A409/-/blob/develop/README.md#%ED%99%94%EB%A9%B4-%EC%84%A4%EA%B3%84%EC%84%9C)
9. [ER Diagram](https://lab.ssafy.com/s08-webmobile1-sub2/S08P12A409/-/blob/develop/README.md#er-diagram)
10. [컨벤션 전략](https://lab.ssafy.com/s08-webmobile1-sub2/S08P12A409/-/blob/develop/README.md#%EC%BB%A8%EB%B2%A4%EC%85%98-%EC%A0%84%EB%9E%B5)
11. [EC2 포트 정리](https://lab.ssafy.com/s08-webmobile1-sub2/S08P12A409/-/blob/develop/README.md#ec2-%ED%8F%AC%ED%8A%B8-%EC%A0%95%EB%A6%AC)
12. [팀 역할](https://lab.ssafy.com/s08-webmobile1-sub2/S08P12A409#%ED%8C%80%EC%9B%90-%EC%97%AD%ED%95%A0)

## 주요 기능

- webRTC를 이용한 실시간 화상 게임
- 사용자가 직접 커스터마이징 가능한 문제
- 게임 과정에서 찍힌 사진을 확인, 공유 가능
- 게임 기록 보관, 확인

## 기능 시연

- #### 메인 - 카카오 로그인

![Main_1](/ReadMe_contents/Main_1.gif)<br/><br/><br/><br/>

- #### 메인 - 구글 로그인

![Main_1](/ReadMe_contents/Main_2.gif)<br/><br/><br/><br/>

- #### 메인 - PIN 번호로 입장

![Main_1](/ReadMe_contents/Main_3.gif)<br/><br/><br/><br/>

- #### 같이 하기 - 정렬 및 검색

![GameList_1](/ReadMe_contents/GameList_1.gif)<br/><br/><br/><br/>

- #### 같이 하기 - 방 생성

![GameList_2](/ReadMe_contents/GameList_2.gif)<br/><br/><br/><br/>

- #### 같이 하기 - 즐겨찾기

![GameList_4](/ReadMe_contents/GameList_4.gif)<br/><br/><br/><br/>

- #### 같이 하기 - 문제 미리보기

![GameList_5](/ReadMe_contents/GameList_5.gif)<br/><br/><br/><br/>

- #### 같이 하기 - 신고하기

![GameList_6](/ReadMe_contents/GameList_6.gif)<br/><br/><br/><br/>

- #### 게임 만들기

![GameMake_1](/ReadMe_contents/GameMake_1.gif)<br/><br/><br/><br/>

- #### 베스트 컷 - 정렬 및 검색

![BestcutList_1](/ReadMe_contents/BestcutList_1.gif)<br/><br/><br/><br/>

- #### 베스트 컷 - 좋아요

![BestcutList_2](/ReadMe_contents/BestcutList_2.gif)<br/><br/><br/><br/>

- #### 베스트 컷 - 상세보기

![BestcutList_3](/ReadMe_contents/BestcutList_3.gif)<br/><br/><br/><br/>

- #### 마이페이지 - 프로필 사진 및 닉네임 수정, 나의 베스트 컷, 게임 즐겨찾기, 내가 만든 게임

![MyPage_1](/ReadMe_contents/MyPage_1.gif)<br/><br/><br/><br/>

- #### 관리자페이지 - 신고된 게임, 베스트 컷 목록 조회 및 처리 

![AdminPage_1](/ReadMe_contents/AdminPage_1.gif)<br/><br/><br/><br/>

- #### 게임방 - 게임 진행 화면

![GameRoom_2](/ReadMe_contents/GameRoom_2.gif)<br/><br/><br/><br/>

- #### 게임방 - 사용자 비디오, 오디오 on/off

![GameRoom_3](/ReadMe_contents/GameRoom_3.gif)<br/><br/><br/><br/>

- #### 게임방 - 게임 종료 후 베스트 컷 업로드

![GameRoom_4](/ReadMe_contents/GameRoom_4.gif)<br/><br/><br/><br/>

- #### 게임방 - 최종 결과

![GameRoom_5](/ReadMe_contents/GameRoom_5.gif)<br/><br/><br/><br/>

- #### 혼자 하기 - 탐색
![SingleGame_1](/ReadMe_contents/SingGame_1.gif)<br/><br/><br/><br/>

- #### 혼자 하기 - 게임 입장
![SingleGame_2](/ReadMe_contents/SingGame_2.gif)<br/><br/><br/><br/>

- #### 혼자 하기 - 게임 진행 및 결과 등록
![SingleGame_3](/ReadMe_contents/SingGame_3.gif)<br/><br/><br/><br/>

- #### 혼자 하기 - 랭킹 및 사진 확인
![SingleGame_4](/ReadMe_contents/SingGame_4.gif)<br/><br/><br/><br/>

## 개발 환경

| Category        | Tech stack         | Version                    | Docker                |
| --------------- | ------------------ | -------------------------- | --------------------- |
| Version Control | GitLab             |                            |                       |
|                 | Jira               |                            |                       |
| Documentation   | notion             |                            |                       |
| Front-End       | HTML5              |                            |                       |
|                 | CSS3               |                            |                       |
|                 | JavaScript(ES6)    |                            |                       |
|                 | vue/cli            | 5.0.8                      |                       |
|                 | Vue.js (Vue3)      | 3.2.45                     |                       |
|                 | node.js            | 14.19.0                    |                       |
|                 | Visual Studio Code | 1.74.2                     |                       |
| Back-End        | Java               | OpenJDK Azul zulu 11.60.19 | official docker image |
|                 | gradle             | 7.6                        | official docker image |
|                 | SpringBoot         | 2.7.7                      |                       |
|                 | Intellij           | 2022.3                     |                       |
| DB              | MySQL              | 8.0.31                     | official docker image |
| Server          | AWS EC2            |                            |                       |
|                 | AWS S3             |                            |                       |
|                 | Nginx              |                            | official docker image |
|                 | Ubuntu             | 22.04.1 LTS                | official docker image |
|                 | Openvidu           | 2.25.0                     | official docker image |
| CI/CD           | Docker             |                            |                       |
|                 | Jenkins            |                            |                       |
|                 | Ansible            |                            |                       |

---

## 서비스 아키텍쳐

![서비스 아키텍처](./ReadMe_contents/Architecture.jpg)

## Jenkins를 이용한 CD 구축

Jenkins를 이용하여 빌드하고 전달, ansible playbook, dockerCompose를 이용하여 docker container로 배포하였습니다. <br>
letsencrypt를 이용하여 ssl 인증서를 적용하였고, 프론트엔드는 443(https)로 프록시로 분기, 백엔드는 /api 경로로 프록시를 걸어줬습니다.

<br>

**[상세 보기](./exec/PortingMenual.md)**

## 이미지 분석 알고리즘

- [KAZE feature detection and description](https://lab.ssafy.com/s08-webmobile1-sub2/S08P12A409/-/wikis/%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%9C%A0%EC%82%AC%EB%8F%84-/-KAZE-feature-detection-and-description)
  - 방향 성분으로 부분 영상의 특징을 실수 정보로 저장합니다.부분영상을 추출하여 Gradient 방향 성분에 대한 히스토그램을 추출, 각각의 작은 구역에서 방향 히스토그램을 계산합니다. 두 이미지가 동일한 KAZE Interest Point 를 많이 가지고 있을수록 높은 유사도를 갖는다고 판단, 높은 점수를 부여합니다.
- [Structural Similarity Index Measure](https://lab.ssafy.com/s08-webmobile1-sub2/S08P12A409/-/wikis/%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%9C%A0%EC%82%AC%EB%8F%84-/-Structural-Similarity-Index-Measure)
  - 휘도(Luminance), 대비(Contrast), 구조(Structure) 를 종합적으로 평가합니다. 단순 픽셀간 비교가 아니라, 영상을 구성하는 주요 요소(휘도, 대비, 구조)를 비교합니다.
- [Image Histogram Analysis](https://lab.ssafy.com/s08-webmobile1-sub2/S08P12A409/-/wikis/%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%9C%A0%EC%82%AC%EB%8F%84-/-Image-Histogram-Analysis)
  - 이미지 히스토그램 분석은 이미지를 3개의 채널(Red,Green,Blue)로 분할하여 각 성분의 세기(Intensity)의 빈도(frequency)와 분포(distribution)를 측정합니다.
- [Perceptual Hashing](https://lab.ssafy.com/s08-webmobile1-sub2/S08P12A409/-/wikis/%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%9C%A0%EC%82%AC%EB%8F%84-/-Perceptual-Hashing)
  - 4를 factor 로 하여 타겟 이미지를 32×32 이미지로 scale down 합니다. 타겟 이미지에 이산 코사인 변환(Discrete cosine transform)을 행별로 수행 후, 열별로 수행합니다.
  - 높은 빈도로 등장하는 픽셀(high-frequencies pixels)은 좌측 상단 모서리에 위치하게 됩니다. 다음으로, 전체 이미지의 회색 중앙값(grayscaled median)을 계산합니다. 중앙값을 기준으로 바이너리 해싱을 수행해 결과를 반환합니다.

## 협업툴

- Git
- Jira
- Notion
- Mattermost
- Webex
- Figma

## 기능 명세서

![기능 명세서 회원](./ReadMe_contents/Functional%20Specification_1.png)
![기능 명세서 방](./ReadMe_contents/Functional%20Specification_2.png)
![기능 명세서 게임](./ReadMe_contents/Functional%20Specification_3.png)
![기능 명세서 베스트 컷, 관리자](./ReadMe_contents/Functional%20Specification_4.png)

## API 명세서

![API 명세서_1](./ReadMe_contents/API%20Specification_1.png)
![API 명세서_2](./ReadMe_contents/API%20Specification_2.png)
![API 명세서_3](./ReadMe_contents/API%20Specification_3.png)
![API 명세서_4](./ReadMe_contents/API%20Specification_4.png)
![API 명세서_5](./ReadMe_contents/API%20Specification_5.png)

## 화면 설계서

### 전체

![전체 목업](./ReadMe_contents/mockup_whole.png)

### 목록 목업

![목록 목업](./ReadMe_contents/mockup_list.png)

### 요소 목업

![요소 목업_1](./ReadMe_contents/mockup_common_component_1.png)
![요소 목업_2](./ReadMe_contents/mockup_common_component_2.png)

## ER Diagram

![ER Diagram](./ReadMe_contents/ddockddackERD.png)

## 컨벤션 전략

### [상세 보기](https://lab.ssafy.com/s08-webmobile1-sub2/S08P12A409/-/wikis/%EC%BB%A8%EB%B2%A4%EC%85%98-%EC%A0%84%EB%9E%B5)

### **Git Commit Message Convention**

> [지라 티켓 번호] [타입]: [제목] (Ticket-Number Type: Subject)
>
> (공백)
>
> 본문 (Body)
>
> (공백)
>
> \*(optional) 꼬리말 (Footer)

### **Git Branch Convention**

**1. master branch**

제품으로 출시(release)될 수 있는 브랜치

**2. develop branch**

다음 출시 버전을 개발하는 브랜치

**3. feature branch**

기능을 개발하는 브랜치

### **Jira Convention**

**Epic > Story > Task > Sub-Task**

**Epic : 기능단위 분류**

**Story : 세부 기능**

## **[Notion](https://jet-play-ae5.notion.site/0de091d9369048ea806dd8140734cfae)**

![노션 예시](./ReadMe_contents/notion_example.png)

- ※ 표시 : 전체 공지 사항
- 북마크 : 개발하며 필요한 자료 정리
- 미팅 : 회의 기록
- 폐기 : 사용하지 않는 자료 또는 기록 보관
- 학습 내용 공유 : 서로 알아두면 좋은 내용 공유

## EC2 포트 정리

|   **PORT**    |                           **이름**                           |
| :-----------: | :----------------------------------------------------------: |
|      22       |                             ssh                              |
|      80       |                      from HTTP to HTTPS                      |
|      443      |                            HTTPS                             |
|     3478      |       used by STUN/TURN server to resolve clients IPs.       |
|     5443      |                           Openvidu                           |
|     3306      |                            MySQL                             |
|     7080      |                 vue, NginX Docker Container                  |
|     8080      |                           Jenkins                            |
|     8081      |                 Spring boot Docker Container                 |
|     10022     |                       ansible ssh port                       |
|     20022     |                        dood ssh port                         |
|     30022     |                  vue, NginX Docker ssh port                  |
| 40000 - 57000 | used by Kurento Media Server to establish media connections. |
| 57001 - 65535 | used by TURN server to establish relayed media connections.  |

