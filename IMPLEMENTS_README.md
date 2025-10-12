# TDD 구현 순서 설계 문서

## 1. TDD 접근 방식: Outside-In

### 핵심 원칙
- **바깥에서 안으로**: 사용자 인터페이스(Controller)부터 시작
- **필요에 의한 발견**: 테스트를 작성하면서 필요한 협력자를 발견
- **점진적 구현**: 한 번에 하나의 계층씩 구현

### 구현 순서
```
Controller Test → UseCase Interface 발견
    ↓
UseCase Test → Port Out 발견, Domain 발견
    ↓
Domain Test → 비즈니스 로직 검증
    ↓
Repository Test → Adapter Out 구현
```

---

## 2. 기능별 우선순위

### 우선순위 결정 기준
1. **의존성이 적은 것부터**: 다른 기능에 의존하지 않는 것
2. **핵심 기능**: 시스템의 가장 중요한 기능
3. **복잡도**: 단순한 것부터 복잡한 것으로

### 구현 순서
```
1순위: 로그인 (의존성 낮음, 핵심 기능)
2순위: 사용자 정보 조회 (로그인 다음 자연스러운 흐름)
3순위: 계좌 조회 (사용자와 연결)
4순위: 로그아웃 (로그인과 유사)
5순위: 계좌 이체 (복잡도 높음, 여러 도메인 협력)
6순위: 거래 내역 조회 (이체 후 확인)
```

---

## 3. Feature #1: 로그인 구현 흐름

### Phase 1: Controller Layer
**목표**: HTTP 요청/응답 처리

#### 작업 순서
1. UserControllerTest 작성
    - 로그인 성공 케이스
    - 잘못된 비밀번호 케이스
    - 존재하지 않는 사용자 케이스
    - 차단된 사용자 케이스

2. 테스트 실행 → Red (실패)

3. UserController 구현
    - LoginRequest 받기
    - LoginCommand 변환
    - LoginUseCase 호출
    - LoginResponse 반환
    - 예외 처리

4. 테스트 실행 → Green (성공)

#### 발견되는 것들
- `LoginUseCase` 인터페이스 (Port In)
- `LoginCommand`, `LoginResult` (UseCase DTO)
- `LoginRequest`, `LoginResponse` (Web DTO)
- 예외 클래스들

---

### Phase 2: Application Layer (UseCase)
**목표**: 비즈니스 로직 조율

#### 작업 순서
1. LoginUseCase 인터페이스 생성
    - execute 메서드 정의

2. LoginServiceTest 작성
    - 올바른 인증 정보로 로그인 성공
    - 존재하지 않는 사용자 실패
    - 잘못된 비밀번호 실패
    - 차단된 사용자 실패

3. 테스트 실행 → Red (실패)

4. LoginService 구현
    - LoadUserPort로 사용자 조회
    - 비밀번호 검증
    - 사용자 상태 확인
    - 결과 반환

5. 테스트 실행 → Green (성공)

#### 발견되는 것들
- `LoadUserPort` 인터페이스 (Port Out)
- `User` 도메인 모델
- `UserStatus` enum
- `PasswordEncoder`

---

### Phase 3: Domain Layer
**목표**: 순수 비즈니스 로직

#### 작업 순서
1. UserTest 작성
    - User 생성 검증
    - 활성 상태 확인
    - 차단 상태 확인
    - 비즈니스 규칙 검증

2. 테스트 실행 → Red (실패)

3. User 도메인 모델 구현
    - 필드 정의
    - 비즈니스 메서드 구현
    - 상태 검증 로직

4. UserStatus enum 생성

5. 테스트 실행 → Green (성공)

#### 확인사항
- JPA 의존성 없음
- 순수 Java 객체
- 비즈니스 로직만 포함

---

### Phase 4: Adapter Out (Repository)
**목표**: 외부 저장소 연결

#### 작업 순서
1. LoadUserPort, SaveUserPort 인터페이스 생성
    - 메서드 시그니처 정의

2. UserRepositoryAdapterTest 작성
    - 사용자명으로 조회 성공
    - 존재하지 않는 사용자 조회
    - ID로 조회 성공
    - 사용자 저장

3. 테스트 실행 → Red (실패)

4. UserRepositoryAdapter 구현
    - Port Out 인터페이스 구현
    - UserJpaRepository 사용
    - Domain ↔ JPA Entity 변환

5. UserJpaRepository 생성 (Spring Data JPA)

6. UserJpaEntity 생성
    - JPA 어노테이션
    - 변환 메서드 (toDomain, from)

7. 테스트 실행 → Green (성공)

#### 확인사항
- Port Out 인터페이스 구현
- Domain과 JPA Entity 분리
- 변환 로직 명확

---

## 4. Feature #2: 사용자 정보 조회 구현 흐름

### Phase 1: Controller Layer
1. UserControllerTest (사용자 정보 조회) 작성
2. 테스트 실행 → Red
3. UserController에 getUserInfo 메서드 추가
4. 테스트 실행 → Green

**발견**: GetUserInfoUseCase 인터페이스

### Phase 2: Application Layer
1. GetUserInfoUseCase 인터페이스 생성
2. GetUserInfoServiceTest 작성
3. 테스트 실행 → Red
4. GetUserInfoService 구현
5. 테스트 실행 → Green

**재사용**: LoadUserPort (이미 존재)

### Phase 3: Domain Layer
**재사용**: User 도메인 모델 (이미 존재)

### Phase 4: Adapter Out
**재사용**: UserRepositoryAdapter (이미 존재)

---

## 5. Feature #3: 계좌 조회 구현 흐름

### Phase 1: Controller Layer
1. AccountControllerTest 작성
2. 테스트 실행 → Red
3. AccountController 구현
4. 테스트 실행 → Green

**발견**: GetAccountsUseCase 인터페이스

### Phase 2: Application Layer
1. GetAccountsUseCase 인터페이스 생성
2. GetAccountsServiceTest 작성
3. 테스트 실행 → Red
4. GetAccountsService 구현
5. 테스트 실행 → Green

**발견**: LoadAccountPort 인터페이스

### Phase 3: Domain Layer
1. AccountTest 작성
2. 테스트 실행 → Red
3. Account 도메인 모델 구현
4. Money 값 객체 구현
5. 테스트 실행 → Green

### Phase 4: Adapter Out
1. LoadAccountPort 인터페이스 생성
2. AccountRepositoryAdapterTest 작성
3. 테스트 실행 → Red
4. AccountRepositoryAdapter 구현
5. AccountJpaRepository 생성
6. AccountJpaEntity 생성
7. 테스트 실행 → Green

---

## 6. Feature #4: 로그아웃 구현 흐름

### Phase 1: Controller Layer
1. UserControllerTest (로그아웃) 작성
2. 테스트 실행 → Red
3. UserController에 logout 메서드 추가
4. 테스트 실행 → Green

**발견**: LogoutUseCase 인터페이스

### Phase 2: Application Layer
1. LogoutUseCase 인터페이스 생성
2. LogoutServiceTest 작성
3. 테스트 실행 → Red
4. LogoutService 구현
5. 테스트 실행 → Green

**재사용**: LoadUserPort, SaveUserPort

### Phase 3: Domain Layer
1. UserTest에 비활성화 테스트 추가
2. 테스트 실행 → Red
3. User에 deactivate 메서드 추가
4. 테스트 실행 → Green

### Phase 4: Adapter Out
**재사용**: UserRepositoryAdapter (이미 존재)

---

## 7. Feature #5: 계좌 이체 구현 흐름

### Phase 1: Controller Layer
1. AccountControllerTest (이체) 작성
    - 이체 성공
    - 잔액 부족
    - 존재하지 않는 계좌
2. 테스트 실행 → Red
3. AccountController에 transfer 메서드 추가
4. 테스트 실행 → Green

**발견**: TransferMoneyUseCase 인터페이스

### Phase 2: Application Layer
1. TransferMoneyUseCase 인터페이스 생성
2. TransferMoneyServiceTest 작성
    - 이체 성공 (출금, 입금, 거래 생성)
    - 잔액 부족 실패
    - 출금 계좌 없음
    - 입금 계좌 없음
3. 테스트 실행 → Red
4. TransferMoneyService 구현
    - 계좌 조회 (출금, 입금)
    - 이체 가능 검증
    - 출금/입금 처리
    - 거래 생성
    - 저장
5. 테스트 실행 → Green

**발견**:
- SaveAccountPort
- SaveTransactionPort
- Transaction 도메인

### Phase 3: Domain Layer

#### Account 확장
1. AccountTest에 출금/입금 테스트 추가
2. 테스트 실행 → Red
3. Account에 withdraw, deposit 메서드 추가
4. 테스트 실행 → Green

#### Transaction 생성
1. TransactionTest 작성
    - Transaction 생성
    - 이체 타입 검증
2. 테스트 실행 → Red
3. Transaction 도메인 모델 구현
4. 테스트 실행 → Green

#### Money 확장
1. MoneyTest 작성
    - 덧셈, 뺄셈
    - 비교 연산
2. 테스트 실행 → Red
3. Money 값 객체 구현
4. 테스트 실행 → Green

### Phase 4: Adapter Out
1. SaveAccountPort, SaveTransactionPort 인터페이스 생성
2. AccountRepositoryAdapter 확장 테스트
3. TransactionRepositoryAdapterTest 작성
4. 테스트 실행 → Red
5. AccountRepositoryAdapter에 save 추가
6. TransactionRepositoryAdapter 구현
7. TransactionJpaRepository 생성
8. TransactionJpaEntity 생성
9. 테스트 실행 → Green

---

## 8. Feature #6: 거래 내역 조회 구현 흐름

### Phase 1: Controller Layer
1. TransactionControllerTest 작성
2. 테스트 실행 → Red
3. TransactionController 구현
4. 테스트 실행 → Green

**발견**: GetTransactionsUseCase 인터페이스

### Phase 2: Application Layer
1. GetTransactionsUseCase 인터페이스 생성
2. GetTransactionsServiceTest 작성
3. 테스트 실행 → Red
4. GetTransactionsService 구현
5. 테스트 실행 → Green

**발견**: LoadTransactionPort 인터페이스

### Phase 3: Domain Layer
**재사용**: Transaction 도메인 모델 (이미 존재)

### Phase 4: Adapter Out
1. LoadTransactionPort 인터페이스 생성
2. TransactionRepositoryAdapter 확장 테스트
3. 테스트 실행 → Red
4. TransactionRepositoryAdapter에 load 메서드 추가
5. 테스트 실행 → Green

---

## 9. Red-Green-Refactor 사이클

### 각 Phase마다 반복

#### Red (실패하는 테스트)
1. 테스트 코드 작성
2. 테스트 실행
3. 실패 확인 (컴파일 오류 또는 테스트 실패)

#### Green (최소한으로 통과)
1. 테스트를 통과시킬 최소한의 구현
2. 테스트 실행
3. 성공 확인

#### Refactor (리팩토링)
1. 코드 개선 (중복 제거, 명확성 향상)
2. 테스트 실행
3. 여전히 통과하는지 확인

---

## 10. 전체 구현 흐름 요약

### 1단계: User Domain (Feature #1, #2, #4)
```
로그인 → 사용자 정보 조회 → 로그아웃
```
- Controller → UseCase → Domain → Repository
- User 도메인 완성
- LoadUserPort, SaveUserPort 완성

### 2단계: Account Domain (Feature #3)
```
계좌 조회
```
- Controller → UseCase → Domain → Repository
- Account 도메인 생성
- LoadAccountPort 완성

### 3단계: Transaction Domain (Feature #5, #6)
```
계좌 이체 → 거래 내역 조회
```
- Controller → UseCase → Domain → Repository
- Transaction 도메인 완성
- Account 도메인 확장 (withdraw, deposit)
- SaveAccountPort, SaveTransactionPort, LoadTransactionPort 완성

---

## 11. 체크리스트

### Feature #1: 로그인
- [ ] Phase 1: UserControllerTest → UserController
- [ ] Phase 2: LoginServiceTest → LoginService
- [ ] Phase 3: UserTest → User Domain
- [ ] Phase 4: UserRepositoryAdapterTest → UserRepositoryAdapter

### Feature #2: 사용자 정보 조회
- [ ] Phase 1: UserControllerTest 확장 → UserController 확장
- [ ] Phase 2: GetUserInfoServiceTest → GetUserInfoService
- [ ] Phase 3: (재사용)
- [ ] Phase 4: (재사용)

### Feature #3: 계좌 조회
- [ ] Phase 1: AccountControllerTest → AccountController
- [ ] Phase 2: GetAccountsServiceTest → GetAccountsService
- [ ] Phase 3: AccountTest → Account Domain
- [ ] Phase 4: AccountRepositoryAdapterTest → AccountRepositoryAdapter

### Feature #4: 로그아웃
- [ ] Phase 1: UserControllerTest 확장 → UserController 확장
- [ ] Phase 2: LogoutServiceTest → LogoutService
- [ ] Phase 3: UserTest 확장 → User Domain 확장
- [ ] Phase 4: (재사용)

### Feature #5: 계좌 이체
- [ ] Phase 1: AccountControllerTest 확장 → AccountController 확장
- [ ] Phase 2: TransferMoneyServiceTest → TransferMoneyService
- [ ] Phase 3: AccountTest 확장 → Account Domain 확장
- [ ] Phase 3: TransactionTest → Transaction Domain
- [ ] Phase 3: MoneyTest → Money Value Object
- [ ] Phase 4: AccountRepositoryAdapter 확장
- [ ] Phase 4: TransactionRepositoryAdapterTest → TransactionRepositoryAdapter

### Feature #6: 거래 내역 조회
- [ ] Phase 1: TransactionControllerTest → TransactionController
- [ ] Phase 2: GetTransactionsServiceTest → GetTransactionsService
- [ ] Phase 3: (재사용)
- [ ] Phase 4: TransactionRepositoryAdapter 확장

---

## 12. 핵심 원칙

### Outside-In 순서 준수
```
항상 바깥(Controller)부터 시작
→ 필요한 것을 발견하면서 안쪽으로
→ 절대 Domain부터 만들지 않음
```

### 테스트 우선
```
구현 전에 항상 테스트 먼저
→ Red 상태 확인
→ 최소 구현으로 Green
→ 리팩토링
```

### 점진적 발견
```
한 번에 모든 인터페이스를 설계하지 않음
→ 테스트를 작성하면서 자연스럽게 발견
→ 필요할 때만 추가
```

### 재사용 우선
```
이미 만든 Port/Domain이 있는지 먼저 확인
→ 새로 만들기 전에 재사용 검토
→ 확장이 필요하면 기존 것 확장
```

---

## 13. 다음 단계

1. ✅ TDD 구현 순서 문서 완료
2. 📝 Feature #1 (로그인) 구현 시작
    - UserControllerTest 작성부터 시작
    - Red → Green → Refactor 반복
3. 📝 각 Feature 순차적 구현
4. 📝 통합 테스트 작성