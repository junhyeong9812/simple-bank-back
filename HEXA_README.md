# 헥사고날 아키텍처 구조 상세 설명

## 전체 구조 개요

```
user/
├── domain/              # 핵심 비즈니스 로직
├── application/         # 유스케이스 구현
├── port/                # 인터페이스 (계약)
│   ├── in/             # 들어오는 포트
│   └── out/            # 나가는 포트
└── adapter/            # 구현체 (기술 상세)
    ├── in/             # 들어오는 어댑터
    └── out/            # 나가는 어댑터
```

---

## 1. Domain (도메인)

### 역할
- **순수한 비즈니스 로직**
- 외부 기술(DB, 프레임워크)에 의존하지 않음
- 핵심 규칙과 정책을 담당

### 포함되는 파일
```java
// Entity (엔티티)
public class User {
    private Long id;
    private String username;
    private String password;
    private UserStatus status;
    
    // 비즈니스 로직
    public void activate() {
        if (this.status == UserStatus.BLOCKED) {
            throw new IllegalStateException("차단된 사용자는 활성화할 수 없습니다");
        }
        this.status = UserStatus.ACTIVE;
    }
}

// Value Object (값 객체)
public class Money {
    private final BigDecimal amount;
    
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }
}

// Domain Service (도메인 서비스)
public class TransferPolicy {
    public boolean canTransfer(Account from, Account to, Money amount) {
        return from.hasEnoughBalance(amount) && to.canReceive(amount);
    }
}

// Domain Event (도메인 이벤트)
public class MoneyTransferredEvent {
    private final Long fromAccountId;
    private final Long toAccountId;
    private final Money amount;
}
```

### 특징
- 어떤 것에도 의존하지 않음
- 순수 Java 객체 (POJO)
- 비즈니스 규칙만 포함

---

## 2. Application (애플리케이션)

### 역할
- **유스케이스 구현**
- 도메인 객체들을 조율(orchestration)
- 트랜잭션 관리
- 비즈니스 플로우 제어

### 포함되는 파일
```java
// UseCase 구현체
@Service
@Transactional
public class LoginService implements LoginUseCase {
    
    private final LoadUserPort loadUserPort;           // out port 사용
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public LoginResult login(LoginCommand command) {
        // 1. 사용자 조회 (out port 사용)
        User user = loadUserPort.loadByUsername(command.getUsername())
            .orElseThrow(() -> new UserNotFoundException());
        
        // 2. 도메인 로직 실행
        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }
        
        user.activate();  // 도메인 객체의 메서드 호출
        
        // 3. 결과 반환
        return new LoginResult(user.getId(), user.getUsername());
    }
}

// Command (입력 DTO)
public class LoginCommand {
    private final String username;
    private final String password;
}

// Result (출력 DTO)
public class LoginResult {
    private final Long userId;
    private final String username;
}
```

### 특징
- Port(인터페이스)에만 의존
- 도메인 객체를 사용하여 비즈니스 로직 수행
- 기술적 세부사항은 모름

---

## 3. Port (포트) - 인터페이스

### 역할
- **외부와의 계약**
- 애플리케이션 경계 정의
- 의존성 역전의 핵심

### 3-1. Port In (들어오는 포트)

#### 의미
- **애플리케이션으로 들어오는 메시지**
- "애플리케이션에게 이걸 해달라"는 요청
- Use Case 인터페이스

#### 포함되는 파일
```java
// Use Case 인터페이스
public interface LoginUseCase {
    LoginResult login(LoginCommand command);
}

public interface GetUserInfoUseCase {
    UserInfo getUserInfo(Long userId);
}

public interface TransferMoneyUseCase {
    TransferResult transfer(TransferCommand command);
}
```

#### 특징
- 클라이언트(Controller)가 호출
- Application 계층에서 구현
- "애플리케이션이 제공하는 기능"

### 3-2. Port Out (나가는 포트)

#### 의미
- **애플리케이션에서 나가는 메시지**
- "외부 시스템(DB, 외부 API 등)에게 이걸 해달라"는 요청
- Repository, External Service 인터페이스

#### 포함되는 파일
```java
// Repository 인터페이스
public interface LoadUserPort {
    Optional<User> loadByUsername(String username);
    Optional<User> loadById(Long id);
}

public interface SaveUserPort {
    User save(User user);
}

public interface LoadAccountPort {
    Optional<Account> loadById(Long accountId);
}

// External Service 인터페이스
public interface SendNotificationPort {
    void sendEmail(String to, String subject, String content);
}

public interface ExternalBankApiPort {
    BankResponse validateAccount(String accountNumber);
}
```

#### 특징
- Application 계층에서 호출
- Adapter Out에서 구현
- "애플리케이션이 필요로 하는 외부 기능"

---

## 4. Adapter (어댑터) - 구현체

### 역할
- **기술적 세부사항 구현**
- Port 인터페이스를 실제로 구현
- 외부 세계와 실제 연결

### 4-1. Adapter In (들어오는 어댑터)

#### 의미
- **외부에서 애플리케이션으로 들어오는 요청을 처리**
- Port In을 호출하는 주체

#### 포함되는 파일
```java
// Web Controller
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final LoginUseCase loginUseCase;  // Port In 사용
    private final GetUserInfoUseCase getUserInfoUseCase;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(
            request.getUsername(), 
            request.getPassword()
        );
        
        LoginResult result = loginUseCase.login(command);
        
        return ResponseEntity.ok(new LoginResponse(result));
    }
}

// CLI (명령줄 인터페이스)
public class UserCli {
    private final LoginUseCase loginUseCase;
    
    public void handleLoginCommand(String[] args) {
        // CLI에서 UseCase 호출
    }
}

// Event Listener (이벤트 기반)
@Component
public class UserEventListener {
    private final GetUserInfoUseCase getUserInfoUseCase;
    
    @EventListener
    public void onUserEvent(UserEvent event) {
        // 이벤트 받아서 UseCase 호출
    }
}
```

#### 특징
- HTTP, CLI, 메시지 큐 등 다양한 입력 방식
- Request → Command 변환
- Result → Response 변환

### 4-2. Adapter Out (나가는 어댑터)

#### 의미
- **애플리케이션에서 외부로 나가는 요청을 실제 처리**
- Port Out을 구현

#### 포함되는 파일
```java
// JPA Repository Adapter
@Repository
public class UserRepositoryAdapter implements LoadUserPort, SaveUserPort {
    
    private final UserJpaRepository jpaRepository;  // Spring Data JPA
    
    @Override
    public Optional<User> loadByUsername(String username) {
        return jpaRepository.findByUsername(username)
            .map(UserJpaEntity::toDomain);  // JPA Entity → Domain 변환
    }
    
    @Override
    public User save(User user) {
        UserJpaEntity entity = UserJpaEntity.from(user);
        UserJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }
}

// JPA Entity (Persistence 전용)
@Entity
@Table(name = "users")
class UserJpaEntity {
    @Id
    @GeneratedValue
    private Long id;
    
    private String username;
    private String password;
    
    // Domain ↔ JPA Entity 변환
    public static UserJpaEntity from(User user) { ... }
    public User toDomain() { ... }
}

// External API Adapter
@Component
public class EmailNotificationAdapter implements SendNotificationPort {
    
    private final JavaMailSender mailSender;
    
    @Override
    public void sendEmail(String to, String subject, String content) {
        // 실제 이메일 발송 구현
        MimeMessage message = mailSender.createMimeMessage();
        // ... 이메일 발송
    }
}
```

#### 특징
- DB, 외부 API, 파일 시스템 등 실제 기술 사용
- Domain ↔ Infrastructure 객체 변환 담당

---

## 5. 의존성 방향

```
Adapter In → Port In → Application → Port Out → Adapter Out
(Controller)  (UseCase)  (Service)   (Repository)  (JPA Impl)

의존성 방향: →→→→→→→→→→→
호출 방향:   →→→→→→→→→→→
```

### 핵심 원칙
1. **의존성은 항상 안쪽(Domain)을 향함**
2. **Domain은 아무것에도 의존하지 않음**
3. **Application은 Port에만 의존**
4. **Adapter는 Port를 구현**

---

## 6. 실제 예시: 로그인 플로우

```
1. [Client] 
   → POST /api/users/login
   
2. [Adapter In - UserController]
   → LoginRequest 받음
   → LoginCommand로 변환
   → loginUseCase.login(command) 호출
   
3. [Port In - LoginUseCase]
   → 인터페이스 정의만 (구현 X)
   
4. [Application - LoginService] (UseCase 구현)
   → loadUserPort.loadByUsername() 호출 (Port Out 사용)
   → 도메인 로직 실행 (user.activate())
   → LoginResult 반환
   
5. [Port Out - LoadUserPort]
   → 인터페이스 정의만 (구현 X)
   
6. [Adapter Out - UserRepositoryAdapter]
   → JPA로 DB 조회
   → JpaEntity → Domain 변환
   → User 반환
   
7. [Adapter In - UserController]
   → LoginResult → LoginResponse 변환
   → Client에게 응답
```

---

## 7. In/Out 구분 기준

### In (들어오는)
- **관점**: 애플리케이션 중심
- **의미**: 애플리케이션으로 들어오는 것
- **예시**:
    - HTTP 요청 (Controller)
    - CLI 명령
    - 메시지 큐 수신

### Out (나가는)
- **관점**: 애플리케이션 중심
- **의미**: 애플리케이션에서 나가는 것
- **예시**:
    - DB 접근 (Repository)
    - 외부 API 호출
    - 이메일 발송
    - 파일 저장

### 핵심
**"애플리케이션을 중심으로 생각하라!"**
- 누가 나를(애플리케이션) 호출하나? → In
- 내가(애플리케이션) 누구를 호출하나? → Out

---

## 8. UseCase와 Service의 관계

### 전통적인 Service 레이어 (안티패턴)
```java
// ❌ 하나의 Service에 모든 기능
@Service
public class UserService {
    public void register() { ... }
    public void login() { ... }
    public void logout() { ... }
    public void updateProfile() { ... }
    // ... 계속 늘어남
}
```

### 헥사고날에서의 UseCase 방식
```java
// ✅ 기능별로 독립적인 UseCase

// Port In - UseCase 인터페이스
public interface LoginUseCase {
    LoginResult execute(LoginCommand command);
}

// Application - Service 구현체
@Service
public class LoginService implements LoginUseCase {
    @Override
    public LoginResult execute(LoginCommand command) {
        // 로그인 로직만 담당
    }
}
```

### 핵심 차이점

**전통적 구조:**
```
Controller → Service (모든 기능) → Repository
```

**헥사고날 구조:**
```
Adapter In → Port In (UseCase) → Application (Service) → Port Out → Adapter Out
Controller   인터페이스          구현체                   인터페이스    Repository
```

**Service 레이어가 사라지고 UseCase가 대체!**
- UseCase = 하나의 독립적인 비즈니스 시나리오
- 각 UseCase가 하나의 Service 역할
- Controller가 필요한 UseCase를 직접 주입받아 사용

### 명명 규칙

```
port/in/
├── LoginUseCase.java           # 인터페이스 (Port In)
├── RegisterUserUseCase.java
└── GetUserInfoUseCase.java

application/
├── LoginService.java           # 구현체 (implements LoginUseCase)
├── RegisterUserService.java
└── GetUserInfoService.java
```

**규칙:**
- Port In: `~UseCase` (인터페이스)
- Application: `~Service` (구현체)
- Service가 UseCase를 implements

---

## 9. Spring Data JPA와 계층 관계

### 전체 구조

```java
// ===== Port Out (도메인 관점 인터페이스) =====
// port/out/LoadUserPort.java
public interface LoadUserPort {
    Optional<User> loadById(Long id);
}

// ===== Adapter Out (기술 구현체) =====
// adapter/out/persistence/UserRepositoryAdapter.java
@Repository
public class UserRepositoryAdapter implements LoadUserPort {
    
    private final UserJpaRepository jpaRepository;  // Spring Data JPA 사용
    
    @Override
    public Optional<User> loadById(Long id) {
        return jpaRepository.findById(id)
            .map(UserJpaEntity::toDomain);  // JPA Entity → Domain 변환
    }
}

// ===== Spring Data JPA (기술 상세) =====
// adapter/out/persistence/UserJpaRepository.java
interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByUsername(String username);
}

// ===== JPA Entity (영속성 전용 객체) =====
// adapter/out/persistence/UserJpaEntity.java
@Entity
@Table(name = "users")
class UserJpaEntity {
    @Id @GeneratedValue
    private Long id;
    
    public User toDomain() { ... }
    public static UserJpaEntity from(User user) { ... }
}
```

### 계층별 역할

| 계층 | 역할 | 위치 |
|------|------|------|
| Port Out | 도메인 관점 인터페이스 | port/out/ |
| Adapter Out | Port Out 구현체 | adapter/out/persistence/ |
| Spring Data JPA | JPA 기술 인터페이스 | adapter/out/persistence/ |
| JPA Entity | 영속성 전용 객체 | adapter/out/persistence/ |

**핵심:**
- **Port Out**: "User를 찾아줘" (어떻게는 모름)
- **Adapter Out**: "JPA로 찾을게!" (기술 선택)
- **Spring Data JPA**: JPA 전용 인터페이스 (Adapter 내부에서만 사용)
- **JPA Entity**: DB 테이블 매핑 (Domain과 분리)

### Domain vs JPA Entity 분리 이유

```java
// Domain Entity (비즈니스 로직)
public class User {
    private Long id;
    private String username;
    private UserStatus status;
    
    // 비즈니스 메서드
    public void activate() {
        if (status == UserStatus.BLOCKED) {
            throw new IllegalStateException("차단된 사용자");
        }
        this.status = UserStatus.ACTIVE;
    }
}

// JPA Entity (영속성)
@Entity
class UserJpaEntity {
    @Id @GeneratedValue
    private Long id;
    
    @Column(name = "user_name")
    private String username;
    
    // JPA 어노테이션만, 비즈니스 로직 없음
}
```

**분리 이유:**
- Domain은 순수 비즈니스 로직 (JPA 의존 X)
- JPA Entity는 DB 매핑만 담당
- Domain과 영속성 기술의 독립성 유지

---

## 10. 요약

| 계층 | 역할 | 의존 대상 | 기술 의존성 | 예시 |
|------|------|-----------|-------------|------|
| Domain | 비즈니스 로직 | 없음 | 없음 (순수 Java) | User, Money |
| Application | 유스케이스 구현 | Port, Domain | 없음 | LoginService |
| Port In | 제공 기능 정의 | 없음 | 없음 (인터페이스) | LoginUseCase |
| Port Out | 필요 기능 정의 | Domain | 없음 (인터페이스) | LoadUserPort |
| Adapter In | 외부 → 내부 | Port In | 있음 (Spring MVC) | UserController |
| Adapter Out | 내부 → 외부 | Port Out | 있음 (JPA) | UserRepositoryAdapter |

**의존성 방향**: 바깥 → 안쪽 (Domain이 중심)

### 핵심 원칙 재정리

1. **UseCase = 하나의 독립적인 시나리오**
    - Port In (인터페이스) + Application (구현체)
    - 전통적 Service 레이어를 기능별로 분리한 것

2. **Port = 항상 인터페이스**
    - Port In: 애플리케이션이 제공하는 기능
    - Port Out: 애플리케이션이 필요로 하는 외부 기능

3. **Adapter = 항상 구현체**
    - Adapter In: 외부 요청을 받아서 UseCase 호출
    - Adapter Out: Port Out을 구현하여 실제 외부 연결

4. **Spring Data JPA는 Adapter Out의 일부**
    - Port Out: 도메인 관점 Repository 인터페이스
    - Adapter Out: JPA 기술로 구현한 Repository
    - JPA Entity: Domain과 분리된 영속성 객체