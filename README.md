# Etkinlik Yönetimi Uygulaması — Backend

Spring Boot ile geliştirilmiş RESTful API projesi. Kullanıcıların etkinlik oluşturup yönetebileceği, diğer kullanıcıların etkinliklere katılım sağlayabileceği bir platform sunmaktadır.

---

## Kullanılan Teknolojiler

| Teknoloji | Versiyon |
|-----------|----------|
| Java | 21+ |
| Spring Boot | 4.0.6 |
| Spring Data JPA | - |
| Spring Validation | - |
| H2 Database | 2.4.240 |
| Hibernate ORM | 7.2.12 |
| Lombok | 1.18.46 |
| ModelMapper | 3.2.6 |
| SpringDoc OpenAPI (Swagger) | 2.8.0 |

---

## Proje Yapısı

```
src/main/java/com/works/etkinlikyonetimiuygulamasi/
├── config/
│   ├── AppBeans.java                  # ModelMapper bean tanımı
│   ├── GlobalExceptionHandler.java    # Global hata yönetimi (@RestControllerAdvice)
│   ├── SessionFilter.java             # HTTP Session kimlik doğrulama filtresi
│   ├── SwaggerConfig.java             # Swagger/OpenAPI yapılandırması
│   └── WebConfig.java                 # CORS ve statik kaynak yapılandırması
├── controller/
│   ├── EventRestController.java       # Etkinlik API endpoint'leri (/api/events)
│   └── UsersRestController.java       # Kullanıcı API endpoint'leri (/users)
├── dto/
│   ├── EventCreateDto.java            # Etkinlik oluşturma isteği
│   ├── EventUpdateDto.java            # Etkinlik güncelleme isteği
│   ├── EventResponseDto.java          # Etkinlik yanıt modeli
│   ├── UserResponseDto.java           # Kullanıcı yanıt modeli (şifresiz)
│   ├── UsersLoginDto.java             # Giriş isteği
│   └── UsersRegisterDto.java          # Kayıt isteği
├── entity/
│   ├── Event.java                     # Etkinlik entity'si
│   ├── EventStatus.java               # PUBLISHED | PAUSED | ARCHIVED
│   └── Users.java                     # Kullanıcı entity'si
├── repository/
│   ├── EventRepository.java           # Etkinlik veritabanı işlemleri
│   └── UsersRepository.java           # Kullanıcı veritabanı işlemleri
├── service/
│   ├── EventService.java              # Etkinlik iş mantığı
│   └── UsersService.java              # Kullanıcı iş mantığı
└── EtkinlikYonetimiUygulamasiApplication.java
```

---

## Kurulum ve Çalıştırma

### Gereksinimler

- Java 21 veya üzeri
- Maven

### Adımlar

```bash
# 1. Projeyi klonla
git clone <repo-url>
cd Etkinlik-Yonetimi-Uygulamasi

# 2. Derle ve çalıştır
./mvnw spring-boot:run
```

Uygulama `http://localhost:8090` adresinde çalışmaya başlar.

> **Not:** IntelliJ IDEA kullanıyorsan `EtkinlikYonetimiUygulamasiApplication.java` dosyasını açıp yeşil ▶ butonuna tıklayarak da çalıştırabilirsin.

---

## Veritabanı (H2)

Proje **H2 file-based database** kullanmaktadır. Veriler uygulama kapatılsa bile saklanır.

### Bağlantı Bilgileri

| Alan | Değer |
|------|-------|
| URL | `http://localhost:8090/h2-console` |
| JDBC URL | `jdbc:h2:file:~/yigitsagim_etkinlikyonetimi` |
| Kullanıcı Adı | `sa` |
| Şifre | `sa` |

### application.properties

```properties
server.port=8090
spring.datasource.url=jdbc:h2:file:~/yigitsagim_etkinlikyonetimi;AUTO_SERVER=TRUE
spring.datasource.username=sa
spring.datasource.password=sa
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## Swagger API Dokümantasyonu

Uygulama çalışırken tüm endpoint'ler Swagger üzerinden test edilebilir:

```
http://localhost:8090/swagger-ui/index.html
```

---

## API Endpoint'leri

### Kullanıcı API (`/users`)

| Method | URL | Açıklama | Auth |
|--------|-----|----------|------|
| POST | `/users/register` | Yeni kullanıcı kaydı | ❌ |
| POST | `/users/login` | Kullanıcı girişi | ❌ |
| POST | `/users/logout` | Oturum kapatma | ❌ |

**Kayıt isteği örneği:**
```json
{
  "username": "Yiğit Sağım",
  "email": "yigit@example.com",
  "password": "12345678"
}
```

**Giriş isteği örneği:**
```json
{
  "email": "yigit@example.com",
  "password": "12345678"
}
```

---

### Etkinlik API (`/api/events`)

| Method | URL | Açıklama | Auth |
|--------|-----|----------|------|
| GET | `/api/events?page=0&size=10` | Yayındaki etkinlikleri listele | ✅ |
| GET | `/api/events/search?keyword=konser&page=0&size=10` | Etkinlik ara | ✅ |
| GET | `/api/events/{id}` | Etkinlik detayı | ✅ |
| GET | `/api/events/my` | Kendi etkinliklerimi listele (tüm durumlar) | ✅ |
| POST | `/api/events/create` | Yeni etkinlik oluştur | ✅ |
| PUT | `/api/events/update/{id}` | Etkinlik güncelle (sadece sahibi) | ✅ |
| DELETE | `/api/events/delete/{id}` | Etkinlik sil (sadece sahibi) | ✅ |
| PATCH | `/api/events/{id}/publish` | Etkinliği yayınla | ✅ |
| PATCH | `/api/events/{id}/pause` | Yayını durdur | ✅ |
| PATCH | `/api/events/{id}/archive` | Arşivle | ✅ |
| POST | `/api/events/{id}/join` | Etkinliğe katıl | ✅ |
| GET | `/api/events/{id}/participants` | Katılımcıları listele (sadece sahibi) | ✅ |

**Etkinlik oluşturma isteği örneği:**
```json
{
  "title": "Java Spring Boot Eğitimi",
  "date": "2026-06-15",
  "time": "14:00:00",
  "location": "Bursa Teknopark",
  "description": "Spring Boot ile backend geliştirme eğitimi.",
  "category": "Teknoloji"
}
```

---

## Kimlik Doğrulama (HTTP Session)

Proje **HTTP Session** tabanlı kimlik doğrulama kullanmaktadır.

- Kullanıcı giriş yaptığında session'a kaydedilir
- `SessionFilter` her istekte session kontrolü yapar
- Session yoksa `401 Unauthorized` döner
- Oturum gerektirmeyen URL'ler:

```
/users/register
/users/login
/users/logout
/swagger-ui
/v3/api-docs
/actuator
```

---

## Etkinlik Durumları

| Durum | Açıklama |
|-------|----------|
| `PUBLISHED` | Yayında — tüm kullanıcılar listede görebilir |
| `PAUSED` | Durduruldu — sadece etkinlik sahibi görebilir |
| `ARCHIVED` | Arşivlendi — sadece etkinlik sahibi görebilir |

---

## Hata Yönetimi

`GlobalExceptionHandler` ile merkezi hata yönetimi sağlanmıştır:

| Hata Tipi | HTTP Kodu | Açıklama |
|-----------|-----------|----------|
| Validation hatası | `400 Bad Request` | Form alanı doğrulama hataları |
| Kayıt bulunamadı | `404 Not Found` | İstenen kaynak yok |
| Yetkisiz işlem | `401 Unauthorized` | Oturum açılmamış |
| Yetki yok | `403 Forbidden` | Başkasının etkinliğine erişim |
| Sistem hatası | `500 Internal Server Error` | Beklenmedik hatalar |

---

## Validation Kuralları

| Alan | Kural |
|------|-------|
| username | Zorunlu |
| email | Zorunlu, geçerli email formatı |
| password | Zorunlu, min 8 karakter |
| title | Zorunlu |
| date | Zorunlu |
| time | Zorunlu |
| location | Zorunlu |
| category | Zorunlu |

---

## Frontend Bağlantısı

Bu backend, Angular frontend projesi ile birlikte çalışmaktadır. Frontend'in çalışması için backend'in açık olması gerekir.

- Backend: `http://localhost:8090`
- Frontend: `http://localhost:4200`
- CORS: Frontend adresi `WebConfig.java`'da izin verilmiştir
