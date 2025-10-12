# Simple Bank Back

메시지 지향 설계와 TDD 기반의 간단한 은행 시스템 백엔드

## 프로젝트 개요

이 프로젝트는 "객체 지향의 사실과 오해" 책의 메시지 지향 철학을 바탕으로 설계된 간단한 은행 시스템입니다.

## 핵심 기능

- 사용자 로그인/로그아웃
- 사용자 정보 조회
- 계좌 조회
- 계좌 이체
- 계좌 간 자금 흐름 추적

## 기술 스택

- Java 17
- Spring Boot 3.x
- Gradle
- JUnit 5 (TDD)

## 설계 원칙

- 메시지 중심 설계
- 객체 간 협력 중심
- TDD 기반 개발
- 단순하고 명확한 책임 분리

## 빌드 및 실행

```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun

# 테스트
./gradlew test
```

## 프로젝트 구조

```
simple-bank-back/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/simplebank/
│   │   │       ├── domain/
│   │   │       ├── application/
│   │   │       └── infrastructure/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/
└── build.gradle
```

## 개발 진행 상황

- [x] 프로젝트 초기 설정
- [ ] 메시지 목록 정의
- [ ] 인터페이스 설계
- [ ] TDD 기반 구현