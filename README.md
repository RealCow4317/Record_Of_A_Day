# 📝 Record_Of_A_Day

**하루의 기록을 남기는 개인 블로그 프로젝트**

<br/>

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-green?logo=spring) ![Maven](https://img.shields.io/badge/Maven-4.0.0-blue?logo=apachemaven) ![MariaDB](https://img.shields.io/badge/MariaDB-10.6%2B-blue?logo=mariadb)

> `pom.xml`에 명시된 "Spring Blog Migration to Spring Boot 3.4" 설명에 따라, 기존 Spring 프로젝트를 최신 Spring Boot 버전으로 마이그레이션한 프로젝트입니다.

---

## ✨ 주요 기능

- **사용자 관리:** 회원가입, 로그인 기능 (Spring Security)
- **게시판:** 게시글 CRUD (생성, 읽기, 수정, 삭제)
- **댓글:** 계층형 댓글 기능
- **일정 관리:** 캘린더 기능
- **할 일 목록:** Todo 리스트 관리

---

## 🛠️ 기술 스택

| 구분      | 기술                                                                                                         |
| --------- | ------------------------------------------------------------------------------------------------------------ |
| **Backend** | `Java 17`, `Spring Boot 3.4.1`, `Spring Security`, `Spring Web`, `WebSocket`                                 |
| **Database**| `MariaDB`, `MyBatis`                                                                                         |
| **Frontend**| `Thymeleaf`, `Thymeleaf Layout Dialect`, `HTML`, `CSS`, `JavaScript`                                           |
| **Build**   | `Maven`                                                                                                      |
| **Etc**     | `Lombok`, `Jsoup`                                                                                            |

---

## 🚀 시작하기

### 1. 사전 요구사항

- Java 17
- Maven 3.8+
- MariaDB

### 2. 실행 방법

1.  **프로젝트 클론**
    ```bash
    git clone {저장소_URL}
    cd Record_Of_A_Day/blog
    ```

2.  **데이터베이스 설정**
    - `src/main/resources/` 경로에 `application-local.properties` 또는 `application-local.yml` 파일을 생성합니다.
    - 아래와 같이 자신의 MariaDB 환경에 맞게 데이터베이스 연결 정보를 입력합니다.
      ```properties
      spring.datasource.url=jdbc:mariadb://localhost:3306/{데이터베이스명}
      spring.datasource.username={사용자명}
      spring.datasource.password={비밀번호}
      ```

3.  **애플리케이션 실행**
    ```bash
    mvn spring-boot:run
    ```