# Simple Bank 메시지 설계 문서

## 1. 도메인 식별

### 주요 도메인
- **User**: 사용자
- **Account**: 계좌
- **Transaction**: 거래

---

## 2. 시나리오별 메시지 설계

### 2.1 사용자 로그인

#### 시나리오
```
1. 사용자가 username, password 입력
2. 시스템이 사용자 정보 확인
3. 비밀번호 검증
4. 사용자 활성화 상태 확인
5. 로그인 성공 결과 반환
```

#### 메시지 흐름
```
[Client] 
  → "username, password로 로그인해줘" 
  → [UserController]

[UserController] 
  → LoginCommand(username, password) 
  → [LoginUseCase]

[LoginService] 
  → "username으로 사용자 찾아줘" 
  → [LoadUserPort]

[LoadUserPort] 
  → Optional<User> 
  → [LoginService]

[LoginService] 
  → "비밀번호 검증해줘" 
  → [User.verifyPassword()]

[User] 
  → "활성화해줘" 
  → [User.activate()]

[LoginService] 
  → LoginResult 
  → [UserController]
```

#### 필요한 Port

**Port In:**
- `LoginUseCase`

**Port Out:**
- `LoadUserPort.loadByUsername(String username): Optional<User>`

---

### 2.2 사용자 로그아웃

#### 시나리오
```
1. 사용자가 로그아웃 요청
2. 현재 사용자의 세션 무효화
3. 로그아웃 성공 결과 반환
```

#### 메시지 흐름
```
[Client] 
  → "로그아웃해줘" 
  → [UserController]

[UserController] 
  → LogoutCommand(userId) 
  → [LogoutUseCase]

[LogoutService] 
  → "사용자 ID로 사용자 찾아줘" 
  → [LoadUserPort]

[LogoutService] 
  → "비활성화해줘" 
  → [User.deactivate()]

[LogoutService] 
  → LogoutResult 
  → [UserController]
```

#### 필요한 Port

**Port In:**
- `LogoutUseCase`

**Port Out:**
- `LoadUserPort.loadById(Long userId): Optional<User>`

---

### 2.3 사용자 정보 조회

#### 시나리오
```
1. 사용자 ID로 정보 조회 요청
2. 사용자 정보 반환
```

#### 메시지 흐름
```
[Client] 
  → "사용자 ID로 정보 조회해줘" 
  → [UserController]

[UserController] 
  → GetUserInfoQuery(userId) 
  → [GetUserInfoUseCase]

[GetUserInfoService] 
  → "사용자 ID로 사용자 찾아줘" 
  → [LoadUserPort]

[GetUserInfoService] 
  → UserInfo 
  → [UserController]
```

#### 필요한 Port

**Port In:**
- `GetUserInfoUseCase`

**Port Out:**
- `LoadUserPort.loadById(Long userId): Optional<User>`

---

### 2.4 계좌 조회

#### 시나리오
```
1. 사용자 ID로 계좌 목록 조회 요청
2. 해당 사용자의 모든 계좌 반환
```

#### 메시지 흐름
```
[Client] 
  → "사용자 ID로 계좌 목록 조회해줘" 
  → [AccountController]

[AccountController] 
  → GetAccountsQuery(userId) 
  → [GetAccountsUseCase]

[GetAccountsService] 
  → "사용자 ID로 계좌 목록 찾아줘" 
  → [LoadAccountPort]

[GetAccountsService] 
  → List<AccountInfo> 
  → [AccountController]
```

#### 필요한 Port

**Port In:**
- `GetAccountsUseCase`

**Port Out:**
- `LoadAccountPort.loadByUserId(Long userId): List<Account>`

---

### 2.5 계좌 이체

#### 시나리오
```
1. 출금 계좌 ID, 입금 계좌 ID, 금액 입력
2. 출금 계좌 조회 및 잔액 확인
3. 입금 계좌 조회 및 입금 가능 여부 확인
4. 출금 계좌에서 금액 차감
5. 입금 계좌에 금액 추가
6. 거래 내역 생성 및 저장
7. 계좌 정보 업데이트
8. 이체 결과 반환
```

#### 메시지 흐름
```
[Client] 
  → "A계좌에서 B계좌로 금액 이체해줘" 
  → [AccountController]

[AccountController] 
  → TransferCommand(fromAccountId, toAccountId, amount) 
  → [TransferMoneyUseCase]

[TransferMoneyService] 
  → "계좌 ID로 출금 계좌 찾아줘" 
  → [LoadAccountPort]

[TransferMoneyService] 
  → "계좌 ID로 입금 계좌 찾아줘" 
  → [LoadAccountPort]

[TransferMoneyService] 
  → "이체 가능한지 검증해줘" 
  → [TransferPolicy.canTransfer()]

[TransferMoneyService] 
  → "출금해줘" 
  → [Account.withdraw(amount)]

[TransferMoneyService] 
  → "입금해줘" 
  → [Account.deposit(amount)]

[TransferMoneyService] 
  → "거래 생성해줘" 
  → [Transaction.create()]

[TransferMoneyService] 
  → "거래 저장해줘" 
  → [SaveTransactionPort]

[TransferMoneyService] 
  → "계좌 정보 업데이트해줘" 
  → [SaveAccountPort]

[TransferMoneyService] 
  → TransferResult 
  → [AccountController]
```

#### 필요한 Port

**Port In:**
- `TransferMoneyUseCase`

**Port Out:**
- `LoadAccountPort.loadById(Long accountId): Optional<Account>`
- `SaveAccountPort.save(Account account): Account`
- `SaveTransactionPort.save(Transaction transaction): Transaction`

---

### 2.6 거래 내역 조회

#### 시나리오
```
1. 계좌 ID로 거래 내역 조회 요청
2. 해당 계좌의 모든 거래 내역 반환 (최신순)
```

#### 메시지 흐름
```
[Client] 
  → "계좌 ID로 거래 내역 조회해줘" 
  → [TransactionController]

[TransactionController] 
  → GetTransactionsQuery(accountId) 
  → [GetTransactionsUseCase]

[GetTransactionsService] 
  → "계좌 ID로 거래 목록 찾아줘" 
  → [LoadTransactionPort]

[GetTransactionsService] 
  → List<TransactionInfo> 
  → [TransactionController]
```

#### 필요한 Port

**Port In:**
- `GetTransactionsUseCase`

**Port Out:**
- `LoadTransactionPort.loadByAccountId(Long accountId): List<Transaction>`

---

## 3. 도메인별 Port 정리

### 3.1 User Domain

#### Port In (제공 기능)
```java
// 로그인
public interface LoginUseCase {
    LoginResult execute(LoginCommand command);
}

// 로그아웃
public interface LogoutUseCase {
    void execute(LogoutCommand command);
}

// 사용자 정보 조회
public interface GetUserInfoUseCase {
    UserInfo execute(GetUserInfoQuery query);
}
```

#### Port Out (필요 기능)
```java
// 사용자 조회
public interface LoadUserPort {
    Optional<User> loadById(Long userId);
    Optional<User> loadByUsername(String username);
}

// 사용자 저장 (필요시)
public interface SaveUserPort {
    User save(User user);
}
```

---

### 3.2 Account Domain

#### Port In (제공 기능)
```java
// 계좌 목록 조회
public interface GetAccountsUseCase {
    List<AccountInfo> execute(GetAccountsQuery query);
}

// 계좌 이체
public interface TransferMoneyUseCase {
    TransferResult execute(TransferCommand command);
}
```

#### Port Out (필요 기능)
```java
// 계좌 조회
public interface LoadAccountPort {
    Optional<Account> loadById(Long accountId);
    List<Account> loadByUserId(Long userId);
}

// 계좌 저장
public interface SaveAccountPort {
    Account save(Account account);
}
```

---

### 3.3 Transaction Domain

#### Port In (제공 기능)
```java
// 거래 내역 조회
public interface GetTransactionsUseCase {
    List<TransactionInfo> execute(GetTransactionsQuery query);
}
```

#### Port Out (필요 기능)
```java
// 거래 조회
public interface LoadTransactionPort {
    List<Transaction> loadByAccountId(Long accountId);
}

// 거래 저장
public interface SaveTransactionPort {
    Transaction save(Transaction transaction);
}
```

---

## 4. 도메인 모델 설계

### 4.1 User (도메인 엔티티)
```java
public class User {
    private Long id;
    private String username;
    private String password;
    private UserStatus status;  // ACTIVE, INACTIVE, BLOCKED
    
    // 비즈니스 메서드
    public void activate() { ... }
    public void deactivate() { ... }
    public boolean verifyPassword(String rawPassword) { ... }
}
```

### 4.2 Account (도메인 엔티티)
```java
public class Account {
    private Long id;
    private Long userId;
    private String accountNumber;
    private Money balance;
    private AccountStatus status;  // ACTIVE, CLOSED
    
    // 비즈니스 메서드
    public void withdraw(Money amount) { ... }
    public void deposit(Money amount) { ... }
    public boolean hasEnoughBalance(Money amount) { ... }
}
```

### 4.3 Transaction (도메인 엔티티)
```java
public class Transaction {
    private Long id;
    private Long fromAccountId;
    private Long toAccountId;
    private Money amount;
    private TransactionType type;  // TRANSFER, DEPOSIT, WITHDRAW
    private LocalDateTime createdAt;
    
    // 생성 메서드
    public static Transaction createTransfer(
        Long fromAccountId, 
        Long toAccountId, 
        Money amount
    ) { ... }
}
```

### 4.4 Money (값 객체)
```java
public class Money {
    private final BigDecimal amount;
    
    public Money add(Money other) { ... }
    public Money subtract(Money other) { ... }
    public boolean isGreaterThan(Money other) { ... }
}
```

---

## 5. 메시지 흐름 요약

### 전체 협력 구조
```
Controller (Adapter In)
    ↓ Command/Query
UseCase (Port In)
    ↓ 구현
Service (Application)
    ↓ 도메인 객체 조작
Domain (Entity, Value Object)
    ↓ 필요시 외부 요청
Port Out
    ↓ 구현
Adapter Out (Repository)
```

### 주요 메시지 패턴

1. **조회 패턴**: Query → UseCase → LoadPort → Domain
2. **명령 패턴**: Command → UseCase → Domain 조작 → SavePort
3. **복합 패턴**: Command → UseCase → 여러 Domain 조율 → 여러 Port

---

## 6. 다음 단계

1. ✅ 메시지 목록 정의 완료
2. 📝 각 Port In 인터페이스 생성 (다음)
3. 📝 각 Port Out 인터페이스 생성
4. 📝 Domain 모델 생성
5. 📝 TDD로 UseCase 구현
6. 📝 Adapter 구현