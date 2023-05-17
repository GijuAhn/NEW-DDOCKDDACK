# Git Commit Message Convention

## **1. 커밋 메시지 컨벤션**

양식

> [지라 티켓 번호] [타입]: [제목] (Ticket-Number Type: Subject)
>
> (공백)
>
> 본문 (Body)
>
> (공백)
>
> \*(optional) 꼬리말 (Footer)

## **2. 헤더 타입**

| 타입      | 설명                                                             |
|----------|----------------------------------------------------------------|
| Feat     | 새로운 기능 추가                                                      |
| Fix      | 버그 수정                                                          |
| Design   | CSS 등 사용자 UI 디자인 변경                                            |
| Style    | 코드 포맷 변경, 세미 콜론 누락, 코드 수정이 없는 경우                               |
| Refactor | 프로덕션 코드 리팩토링                                                   |
| Docs     | 문서 추가 및 수정                                                     |
| Test     | 테스트 코드, 리펙토링 테스트 코드 추가, Production Code(실제로 사용하는 코드) 변경 없음     |
| Chore    | 빌드 업무 수정, 패키지 매니저 수정, 패키지 관리자 구성 등 업데이트, Production Code 변경 없음 |
| Rename   | 파일 혹은 폴더명을 수정하거나 옮기는 작업만인 경우                                   |
| Remove   | 파일을 삭제하는 작업만 수행한 경우                                            |

## **3. 제목**

- 제목은 50자 이내로 작성
- 한글로 작성
- 마침표 사용X

## **4. 본문**

- 코드 변경 시 상세 이유 작성(무엇을, 왜)

## **5. 꼬릿말**

- 유형: #이슈 번호" 형식으로 작성
- 여러 개의 이슈 번호는 쉼표로 구분
- 이슈 트래커 유형은 다음 중 하나를 사용

  | Fixes      | 이슈 수정중 (아직 해결되지 않은 경우)                 |
      | ---------- | ----------------------------------------------------- |
  | Resolves   | 이슈 해결                                             |
  | Ref        | 참고할 이슈가 있을 때 사용                            |
  | Related to | 해당 커밋에 관련된 이슈번호 (아직 해결되지 않은 경우) |

# Git Branch Convention

## **1. master branch**

제품으로 출시(release)될 수 있는 브랜치

## **2. develop branch**

다음 출시 버전을 개발하는 브랜치

- 기능 개발을 위한 브랜치들을 병합하기 위해 사용한다.
- 모든 기능이 추가되고 버그가 수정되어 배포 가능한 안정적인 사태라면 develop 브랜치를 master 브랜치에 merge(병합)한다.

## **3. feature branch**

기능을 개발하는 브랜치

새로운 기능 개발 및 버그 수정이 필요할 때마다 develop 브랜치로부터 분기한다.

1. develop 브랜치에서 새로운 기능에 대한 feature 랜치를 분기한다.
2. 새로운 기능에 대한 작업을 수행한다.
3. 작업이 끝나면 develop 브랜치로 merge한다.
4. 더 이상 필요하지 않은 feature 브랜치는 삭제한다. - 많은 줄기 ( branch)는 작업에 혼란을 줄 수 있다.

## **Branch 네이밍 규칙**

### **1. master branch, develop branch**

**master**와 **develop** 브랜치는 본래 이름 그대로

### **2. feature branch**

**ex) feature/{feature-name}**

- 어떤 이름도 가능하지만 master, develop, release-..., hotfix-...와 같이 다른 브랜치 명으로 사용하고 있는 것들은 사용할 수 없다.

- feature/기능요약 형식을 추천한다고 한다. ex) feature/login

- feature/{issue-number}-{feature-name} 이슈추적을 사용한다면 이와 같은 형식을 따른다.

# Jira Convention

**Epic > Story / Task**

### **Epic : 기능단위 대분류**

### **Story : 프로젝트 외 업무 (학습 등)**

### **Task : 프로젝트 관련 (기술적) 작업**

### **이슈 관리**

- 이슈 생성
    - 월요일에 주 단위 스프린트 이슈들을 생성
    - 일주일에 40시간 이상 이슈 생성 및 처리
    - 스토리 하나 당 최대 4 스토리 포인트
- 이슈 완료
    - 하나의 스토리는 되도록 하나의 커밋으로 처리
- To Do/In Progress/Done 체크
- Story Point : 담당자가 작성하되 여유롭게 시간을 작성해서 진행하기
- 지각 / 결석 / 공가일 경우 Epic 수정

# Java Convention

- 구글 스타일 가이드 적용
- [https://google.github.io/styleguide/javaguide.html](https://google.github.io/styleguide/javaguide.html)
- [Intellij에 적용하기](https://velog.io/@injoon2019/IntelliJ%EC%97%90-Google-Java-Style-Guide-%EC%A0%81%EC%9A%A9%ED%95%98%EA%B8%B0)

# Database Convention

# 데이터베이스 명명 규칙

## 명명 규칙

### 소문자(Lowercase)

**테이블, 뷰, 컬럼을 비롯한 모든 식별자들은 소문자로 작성**. 대소문자가 섞여있는 식별자 이름들을 사용하는 X. 이유는 예약어들과 구분짓기 위함입니다.

ex)`first_name`으로 사용하는 것이`First_Name`으로 사용하는 것 보다 좋다.

### 데이터 타입을 이름으로 정하는 것은 피하자

ojbect에 type을 이름으로 사용하면 안된다. 특히 컬럼명도 명사로 짓기.
컬럼명에 type명을 사용하는 것은 좋지 않다.

ex)`text`,`timestamp`등은 좋지 않은 컬럼명.

### 복합어구에는 \_를 사용하자

**여러 글자가 합쳐져 만든 복합어구에는`_`**(snake_case)를 사용.

ex)`wordcount`,`wordCount`보다는`word_count`,`team_member_id`가 좋다.

### 축약어보다는 풀네임을 사용하자

**Object 이름들은 약어를 사용하기 보다는 풀네임을 사용**. 대부분의 SQL 데이터베이스는 30자 이상의 Object 이름을 설정할 수 있도록 지원.

ex)`middle_name`이`mid_nm`보다 좋다.

### 약어를 사용해야할 때는 공통적인 약어를 사용하자

**어쩔 수 없이 약어를 사용해야한다면, 공통적으로 사용하는 약어를 사용하기**.

### 단수 명사

**table, view 들은 단수의 명사를 사용.**
그 이유중 첫번째는 일반적인 경우 다른 테이블과의 관계를 맺을 때 1개의 row와 관계를 맺기때문이며, 두번째는 어플리케이션에서 이름을 명명할 때 혼란이 없기 때문.

ex_1) teams보다는 team으로 테이블이름을 짓는게 좋다.

### Key Fields

- **primary key**
  {table명}\_id를 사용.
- **foreign key**

foreign key 필드는`{참조되는 테이블}_id`의 이름이 좋다. 이역시 짧고, 단순하며, 명확하기 때문.

### Prefixes and Suffixes (bad)

- **Relation Type 선행**

몇몇 옛날 가이드라인들은 테이블이면 TB*, 뷰면 VM* , 프로시져면 SP 등 prefix를 붙여주는게 좋다라고 하는 가이드들이 있음.
이렇게 prefix를 붙이면 프로그래머들이 즉시 이게 어떤 Object인지 알 수 있다는 장점이있다.
하지만**이렇게 해버리면 만약 뷰의 경우 테이블로 전환하게 되면 원래 가지고 있던 SQL 쿼리 등 수정해야 하는 부분이 많이 생기게 된다.**

- **Data Type 행**

컬럼과 같은 필드에 type을 추가하는 것?
예를 들면 text 필드라면 name_tx. 이것은 좋지 않은생각 이다.**컬럼의 타입은 변경될 수 있다.**만약 timestamp형이 었던 date가 varchar로 변경될 수 있다.
그렇기 때문에 붙이지 않는것이 좋다.

### 자동 생성되는 이름

hibernate(JPA 등)과 같은 ORM을 이용하다보면 이름이 자동으로 생성되는 경우가 있다.
제약조건이나 Index이 대표적인 예. 이런 제약조건과 Index또한 이름을 제대로 지어 줄 필요가 있다.

index의 경우`{table 이름}_ix_{column_1}..{column_2}...`가 좋으며 제약조건 이름 역시`{table 이름}\_{제약조건 이름}`으로 명확하게 하는게 좋다.

참고 : [https://launchbylunch.com/posts/2014/Feb/16/sql-naming-conventions/](https://launchbylunch.com/posts/2014/Feb/16/sql-naming-conventions/)

# Vue.js Convention

- vue.js 공식 문서 - **Composition API**
- [https://v3-docs.vuejs-korea.org/guide/introduction.html](https://v3-docs.vuejs-korea.org/guide/introduction.html)
- 상태관리 : vuex
