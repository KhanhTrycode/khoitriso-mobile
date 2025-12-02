# KhoiTriSo (KTS) - Nền tảng học tập trực tuyến

Ứng dụng Android học tập trực tuyến được xây dựng bằng Kotlin và Jetpack Compose, cung cấp các khóa học, sách điện tử, diễn đàn học tập và nhiều tính năng khác.

## 📋 Mục lục

- [Giới thiệu](#giới-thiệu)
- [Kiến trúc](#kiến-trúc)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
- [Hướng dẫn cài đặt](#hướng-dẫn-cài-đặt)
- [Cấu hình](#cấu-hình)
- [Hướng dẫn chạy](#hướng-dẫn-chạy)
- [Tính năng chính](#tính-năng-chính)
- [Testing](#testing)
- [Đóng góp](#đóng-góp)

## 🎯 Giới thiệu

KhoiTriSo là một nền tảng học tập trực tuyến toàn diện, cho phép người dùng:
- Học các khóa học trực tuyến với video và tài liệu
- Đọc sách điện tử với hỗ trợ MathML/LaTeX
- Tham gia diễn đàn học tập và hỏi đáp
- Mua sắm và thanh toán qua VNPay
- Quản lý tiến độ học tập

## 🏗️ Kiến trúc

Dự án sử dụng **Clean Architecture** với **MVVM Pattern**, được tổ chức thành 3 lớp chính:

```
┌─────────────────────────────────────────┐
│           UI Layer (Compose)            │
│  - Screens, ViewModels, Composables     │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│         Domain Layer                    │
│  - Models, UseCases, Repository Interfaces │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│         Data Layer                      │
│  - DTOs, Repository Implementations,   │
│    API Services, Local Storage          │
└─────────────────────────────────────────┘
```

### Kiến trúc chi tiết

#### 1. **UI Layer** (`ui/`)
- **Screens**: Các màn hình chính của ứng dụng
- **ViewModels**: Quản lý state và business logic cho UI
- **Composables**: Các component UI có thể tái sử dụng
- Sử dụng Jetpack Compose với Material 3

#### 2. **Domain Layer** (`domain/`)
- **Models**: Domain models (business entities)
- **UseCases**: Business logic, mỗi use case đại diện cho một hành động cụ thể
- **Repository Interfaces**: Định nghĩa contract cho data layer

#### 3. **Data Layer** (`data/`)
- **DTOs**: Data Transfer Objects cho API responses
- **Repository Implementations**: Triển khai các repository interfaces
- **API Services**: Retrofit interfaces cho REST API
- **Local Storage**: Room Database, DataStore, SharedPreferences

### Luồng dữ liệu

```
User Action → ViewModel → UseCase → Repository → API/Local DB
                ↓           ↓          ↓
              UI State ← Result<T> ← Data
```

## 🛠️ Công nghệ sử dụng

### Core
- **Kotlin** - Ngôn ngữ lập trình chính
- **Jetpack Compose** - UI framework
- **Material 3** - Design system
- **Coroutines & Flow** - Asynchronous programming

### Dependency Injection
- **Hilt** - Dependency injection framework

### Networking
- **Retrofit** - REST API client
- **OkHttp** - HTTP client với logging interceptor
- **SignalR** - Real-time communication

### Local Storage
- **Room Database** - Local database
- **DataStore** - Key-value storage
- **SharedPreferences** - Legacy preferences (cho compatibility)

### Media
- **ExoPlayer** - Video playback
- **Coil** - Image loading

### UI/UX
- **Markwon** - Markdown & LaTeX rendering
- **Accompanist** - Additional Compose utilities
- **Navigation Compose** - Navigation

### Payment
- **VNPay Integration** - Payment gateway

### Authentication
- **Google Sign-In** - OAuth authentication

### Testing
- **JUnit** - Unit testing
- **Mockito** - Mocking framework
- **Coroutines Test** - Testing coroutines

## 📁 Cấu trúc dự án

```
app/src/main/java/com/example/khoitriso/
├── data/                          # Data Layer
│   ├── api/                       # Retrofit API interfaces
│   ├── dto/                       # Data Transfer Objects
│   ├── local/                     # Local storage managers
│   ├── repository/                # Repository implementations
│   ├── request/                   # Request DTOs
│   └── signalr/                   # SignalR service
│
├── domain/                        # Domain Layer
│   ├── models/                    # Domain models
│   ├── repository/                # Repository interfaces
│   ├── request/                   # Domain request models
│   └── usecase/                   # Use cases
│       ├── auth/
│       ├── book/
│       ├── category/
│       ├── course/
│       ├── discussion/
│       ├── forum/
│       ├── notification/
│       ├── order/
│       ├── user/
│       └── wishlist/
│
├── di/                            # Dependency Injection
│   ├── NetworkModule.kt           # Network dependencies
│   ├── RepositoryModule.kt       # Repository bindings
│   ├── UsecaseModule.kt          # Use case providers
│   └── MyApp.kt                  # Application class
│
├── ui/                            # UI Layer
│   ├── behavior/                  # Base classes, utilities
│   ├── cart/                      # Shopping cart
│   ├── checkout/                  # Checkout & payment
│   ├── common/                    # Shared UI components
│   ├── detail/                    # Detail screens
│   ├── explore/                   # Explore screens
│   ├── forum/                     # Forum screens
│   ├── homescreen/                # Home screen
│   ├── learning/                  # Learning screens
│   ├── loginscreen/               # Login screen
│   ├── mypurchase/                # My purchases
│   ├── notification/              # Notifications
│   ├── paymentresult/             # Payment result
│   ├── profilescreen/             # Profile screen
│   ├── searchscreen/              # Search screen
│   └── theme/                     # Theme configuration
│
├── utils/                         # Utilities
│   ├── Constants.kt               # App constants
│   ├── Converter.kt               # Extension functions
│   ├── MathMLConverter.kt         # MathML to LaTeX
│   ├── Navigation.kt               # Navigation routes
│   ├── Types.kt                   # Enum types
│   ├── UiState.kt                 # UI state wrapper
│   └── UiEvent.kt                 # UI events
│
├── test/                          # Test data
│   └── MockData.kt                # Mock data for testing
│
└── MainActivity.kt                # Main activity
```

## 💻 Yêu cầu hệ thống

- **Android Studio** - Hedgehog (2023.1.1) trở lên
- **JDK** - 11 trở lên
- **Android SDK** - API 24 (Android 7.0) trở lên
- **Gradle** - 8.0 trở lên
- **Kotlin** - 1.9.0 trở lên

## 📦 Hướng dẫn cài đặt

### 1. Clone repository

```bash
git clone <repository-url>
cd KTS
```

### 2. Cấu hình local.properties

Tạo file `local.properties` trong thư mục gốc của dự án (hoặc copy từ `local.properties.example`):

```properties
## SDK Location
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk

## API Configuration
API_KEY=http://localhost:8080/api/
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
DEBUG=true
```

**Lưu ý**: 
- Thay `YOUR_SDK_PATH_HERE` bằng đường dẫn Android SDK của bạn
- Thay `your-google-client-id` bằng Google Client ID thực tế của bạn
- File `local.properties` không được commit vào git (đã có trong `.gitignore`)

### 3. Sync Gradle

Mở Android Studio và chọn **File > Sync Project with Gradle Files**

### 4. Build project

```bash
./gradlew build
```

## ⚙️ Cấu hình

### API Configuration

API base URL được cấu hình trong `local.properties`:
```properties
API_KEY=http://localhost:8080/api/
```

Để thay đổi API endpoint, sửa giá trị `API_KEY` trong `local.properties`.

### Google Sign-In

1. Tạo OAuth 2.0 Client ID trong [Google Cloud Console](https://console.cloud.google.com/)
2. Thêm Client ID vào `local.properties`:
```properties
GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
```

### VNPay Integration

VNPay payment gateway được tích hợp sẵn. Đảm bảo backend API hỗ trợ các endpoint:
- `POST /vnpay/create-payment` - Tạo payment URL
- `GET /vnpay/query-transaction` - Kiểm tra trạng thái giao dịch

## 🚀 Hướng dẫn chạy

### Chạy trên Emulator/Device

1. **Kết nối thiết bị** hoặc khởi động emulator
2. **Chạy ứng dụng**:
   - Trong Android Studio: Click nút **Run** (▶️) hoặc nhấn `Shift + F10`
   - Hoặc từ command line:
   ```bash
   ./gradlew installDebug
   ```

### Test Mode

Ứng dụng có chế độ test mode để test UI với mock data. Để bật test mode:

1. Mở `app/src/main/java/com/example/khoitriso/ui/behavior/BaseViewModel.kt`
2. Đặt `_isTestMode = true`

```kotlin
protected val _isTestMode = true  // Set to true for testing
```

Khi test mode được bật, ứng dụng sẽ sử dụng mock data thay vì gọi API thực tế.

### Debugging

- **Logcat**: Xem logs trong Android Studio Logcat
- **Network Logging**: OkHttp logging interceptor đã được cấu hình, xem network requests trong Logcat với tag `OkHttp`

## ✨ Tính năng chính

### 🎓 Học tập
- **Khóa học trực tuyến**: Xem video, tải tài liệu, làm bài tập
- **Sách điện tử**: Đọc sách với hỗ trợ MathML/LaTeX rendering
- **Tiến độ học tập**: Theo dõi tiến độ học tập của bạn
- **Bài tập**: Làm bài tập với nhiều loại câu hỏi (Multiple Choice, True/False, Short Answer)

### 💬 Diễn đàn
- **Hỏi đáp**: Đặt câu hỏi và nhận câu trả lời từ cộng đồng
- **Thảo luận bài học**: Thảo luận về nội dung bài học
- **Bookmarks**: Lưu các câu hỏi yêu thích
- **Vote**: Upvote/downvote câu hỏi và câu trả lời

### 🛒 Mua sắm
- **Giỏ hàng**: Thêm sách và khóa học vào giỏ hàng
- **Thanh toán**: Thanh toán qua VNPay
- **Lịch sử đơn hàng**: Xem và quản lý đơn hàng
- **Wishlist**: Lưu danh sách yêu thích

### 👤 Hồ sơ
- **Quản lý tài khoản**: Cập nhật thông tin cá nhân, avatar
- **Kích hoạt sách**: Kích hoạt sách bằng mã kích hoạt
- **Cài đặt**: Thay đổi ngôn ngữ (Tiếng Việt/English), theme (Sáng/Tối/System)
- **Đăng xuất**: Đăng xuất khỏi ứng dụng

### 🔔 Thông báo
- **Thông báo real-time**: Nhận thông báo qua SignalR
- **Lọc thông báo**: Lọc theo loại và trạng thái đọc/chưa đọc

## 🧪 Testing

### Unit Tests

Chạy unit tests:
```bash
./gradlew test
```

### UI Tests

Chạy UI tests:
```bash
./gradlew connectedAndroidTest
```

### Test Data

Mock data được định nghĩa trong `app/src/main/java/com/example/khoitriso/test/MockData.kt`. Khi test mode được bật, ứng dụng sẽ sử dụng mock data này.

## 📝 Coding Guidelines

Dự án tuân theo các quy tắc coding được định nghĩa trong `CODE_GUIDELINES.md`. Một số điểm chính:

- **Naming Convention**: 
  - Classes: PascalCase
  - Variables/Functions: camelCase
  - Constants: UPPER_SNAKE_CASE
- **Architecture**: Clean Architecture với MVVM
- **State Management**: Sử dụng `UiState<T>` wrapper cho state
- **Error Handling**: Sử dụng `Result<T>` cho error handling
- **Dependency Injection**: Sử dụng Hilt

## 🤝 Đóng góp

1. Fork dự án
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

### Commit Convention

Sử dụng conventional commits:
- `feat:` - Tính năng mới
- `fix:` - Sửa lỗi
- `docs:` - Cập nhật tài liệu
- `style:` - Formatting, thiếu semicolon, etc
- `refactor:` - Refactor code
- `test:` - Thêm tests
- `chore:` - Cập nhật build tasks, dependencies, etc

## 📄 License

[Thêm thông tin license nếu có]

## 👥 Team

[Thêm thông tin team nếu có]

## 📞 Liên hệ

[Thêm thông tin liên hệ nếu có]

---

**Lưu ý**: Đây là dự án đang phát triển. Một số tính năng có thể chưa hoàn thiện hoặc đang trong quá trình phát triển.

