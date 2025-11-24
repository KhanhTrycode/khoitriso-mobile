# Quy Tắc Code và Coding Conventions — KhoiTriSo (KTS)

Tài liệu này chứa các quy tắc, tiêu chuẩn và thực hành khuyến nghị để giữ codebase Kotlin/Android nhất quán, dễ đọc và dễ bảo trì.

Mục tiêu ngắn gọn
- Đồng nhất tên biến, class, properties (camelCase cho properties/biến; PascalCase cho class/data class). 
- Giữ rõ ràng ranh giới layers: `data/dto` (API) → `data/repository` → `domain/models` + usecases → `ui` (Compose).
- Viết unit test cho Usecase/Logic, tránh test UI nặng trong test unit.
- Thiết lập formatter/linter (ktlint/spotless/detekt) trong CI.

1. Cấu trúc dự án (đề xuất giữ như hiện có)
- `data/dto` — các DTO tương ứng JSON/REST. DTO có thể giữ naming theo API nếu cần, nhưng map sang domain bằng extension `.toDomain()`.
- `data` — các lớp repository, data source, mapper.
- `domain/models` — `data class` domain (sử dụng camelCase cho properties). Đây là giao diện nội bộ app.
- `domain/usecase` — các usecase (logic giàu) trả `Result<T>` hoặc `Either` (hiện project dùng `Result`).
- `ui` — Jetpack Compose screens, viewmodels.

2. Quy tắc đặt tên
- Class / Interface: PascalCase (ví dụ `ForumUsecase`, `GetQuestions`).
- Data class: PascalCase (`ForumQuestion`, `ForumAnswer`).
- Properties / variables / function names: camelCase (`userId`, `createdAt`, `getQuestions`).
- Constants: UPPER_SNAKE_CASE trong `object Constants` hoặc `const val` trong companion nếu cần (`GOOGLE_CLIENT_ID`).
- Test classes: Suffix `Test` (ví dụ `ForumUsecaseTest`).

3. Data classes & DTO mapping
- Domain models phải dùng camelCase. DTOs có thể giữ tên như API trả nhưng hãy viết extension mapper:
  - `fun CourseDto.toDomain(): Course` — đặt trong cùng package `data/dto` hoặc `data/mappers`.
- Khi tạo dữ liệu mock (ví dụ `MockData`), dùng named parameters để tránh lỗi thứ tự, ví dụ `ForumQuestion(id = "q1", title = "...")`.

3.1 API response types
- Rule: Khi API trả về danh sách (list), sử dụng `Respone<ApiResponeData<T>>`.
- Rule: Khi API trả về một object (single item), sử dụng `Respone<ApiRespone<T>>`.
- Ghi chú: đặt DTO trong `data/dto` và map sang domain bằng extension `toDomain()` như các ví dụ ở trên.

4. Functions / Usecases
- Usecase nhỏ (single responsibility). Usecases trong `domain/usecase` nên là classes với `operator fun invoke(...)` để dễ gọi.
- Usecase trả `Result<T>` (hiện project đang dùng `Result`) — trong tests kiểm tra `isSuccess`/`isFailure` và `getOrNull()`.

5. Coroutines & Concurrency
- Dùng `viewModelScope` trong ViewModel để launch coroutines.
- Usecase/interface repository suspend functions — gọi từ ViewModel bằng `viewModelScope.launch`.
- Không block UI thread. Tránh `runBlocking` trong code production (chỉ dùng `runBlocking` trong unit tests khi cần).

6. Jetpack Compose
- Composables phải là hàm thuần (không side-effect) nếu có side-effect thì dùng `LaunchedEffect`, `remember`, `rememberSaveable` hợp lý.
- Tránh gọi navigator/side-effect trực tiếp từ composable: dùng event/callback tới ViewModel hoặc NavController được truyền vào.
- Các composable đặt trong package `ui/XXX`. Đặt preview riêng nếu cần.

7. Dependency Injection (Hilt)
- Sử dụng `@HiltViewModel` cho ViewModel, `@HiltAndroidApp` cho Application.
- Module cung cấp repository/service nên ở `di/`.

8. Error handling và logging
- Trả về `Result.failure(Throwable)` khi có lỗi trong repository/usecase.
- Chỉ log lỗi ở mức cần thiết; dùng helper `debug`/logger của project.

9. Testing
- Unit tests: đặt trong `app/src/test/java/...` như các test hiện có.
- Mocking: nếu không muốn thêm thư viện, dùng fake implementations (như `FakeRepo`) cho usecase tests. Nếu thêm mocking library, dùng `mockk` hoặc `Mockito-Kotlin`.
- Coroutine tests: dùng `kotlinx-coroutines-test` để kiểm soát dispatcher nếu cần.

10. MockData và fixtures
- Tạo helper functions để generate mocks (ví dụ `createMockLessonsForCourse(courseId, count)`), tránh copy/paste data nặng.
- Khi domain thay đổi (ví dụ đổi tên property), cập nhật mock bằng named parameters.

11. Lint/Formatter/Static Analysis (khuyến nghị)
- Thêm và bật `ktlint` hoặc `spotless` với `ktfmt`/`ktlint` trong Gradle để đảm bảo format đồng nhất.
- Thêm `detekt` để phát hiện code smells.
- Thực hiện format trước khi commit (pre-commit hook) và chạy lint trong CI.

12. Git và commit message
- Commit message ngắn gọn: `<scope>: <short description>` (ví dụ `mockdata: add learning path mocks`).
- Tạo PR nhỏ, review thay đổi model/DTO cẩn thận.

13. Ví dụ ngắn (naming + mapper)
```kotlin
// Domain
data class Author(val id: Int, val fullName: String, val avatar: String?)

// DTO mapper
fun AuthorDto.toDomain(): Author = Author(id = this.Id, fullName = this.FullName, avatar = this.Avatar as? String)
```

14. Câu lệnh chạy thường dùng
- Chạy unit tests:
```powershell
./gradlew :app:testDebugUnitTest --no-daemon
```
- Chạy formatter lint (nếu dùng ktlint/spotless):
```powershell
./gradlew spotlessApply
./gradlew ktlintFormat
```

15. Next steps (gợi ý triển khai)
- Thêm `ktlint`/`spotless`/`detekt` config và chạy một lần để chuẩn hóa toàn repo.
- Tạo file `CONTRIBUTING.md` nếu nhiều collaborator tham gia, mô tả quy trình PR, code review.
- Thêm Github Actions/CI để chạy `./gradlew check` và unit tests trên PR.

Nếu bạn muốn, tôi sẽ:
- Tạo file config `ktlint`/`spotless` cơ bản và patch Gradle để áp dụng (an toàn, có thể revert).
- Thêm mẫu `CONTRIBUTING.md` ngắn.

---
Tài liệu này được viết dựa trên cấu trúc và phong cách hiện có của project. Nếu bạn muốn thay đổi một quy tắc (ví dụ: dùng `mockk` cho tests, hoặc chuyển sang `Either` cho error), nói tôi sẽ cập nhật file này tương ứng.
