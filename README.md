# TLU-Routine

Đây là dự án prototype được phát triển cho môn học Tương tác Người-Máy. Mục tiêu chính của dự án là thiết kế và hiện thực hóa một giao diện người dùng (UI/UX) thân thiện, dễ sử dụng, đồng thời áp dụng kiến trúc phát triển Android hiện đại để giúp sinh viên trường Đại học Thủy Lợi (TLU) lập kế hoạch học tập hiệu quả.

## 🚀 Công nghệ sử dụng
* **IDE:** Android Studio
* **Ngôn ngữ:** Java & XML
* **Kiến trúc:** Single-Activity
* **Thư viện chính:**
    * Material Design Components
    * AndroidX Navigation Component
    * View Binding

## 🏛️ Kiến trúc Dự án
Dự án được xây dựng theo kiến trúc **Single-Activity**, một phương pháp tiếp cận hiện đại được Google khuyến nghị nhằm tối ưu hóa việc quản lý và điều hướng trong ứng dụng.

* **Một Activity duy nhất**: Toàn bộ ứng dụng được quản lý bởi `MainActivity.java`. Activity này đóng vai trò là một "container" (vật chứa) chính cho tất cả các thành phần giao diện khác.
* **Sử dụng Fragment cho các màn hình**: Tất cả các màn hình chức năng (ví dụ: Đăng nhập, Trang chủ, Chi tiết, Cài đặt) đều được triển khai dưới dạng `Fragment`. Việc này giúp quản lý vòng đời và tài nguyên một cách hiệu quả hơn.
* **Tổ chức thư mục `res`**:
    * `res/navigation`: Chứa tệp `nav_graph.xml` để định nghĩa và quản lý luồng điều hướng (navigation flow) giữa các `Fragment`. Điều này giúp trực quan hóa và kiểm soát việc chuyển màn hình một cách dễ dàng.
    * `res/menu`: Chứa các tệp XML định nghĩa menu có thể được tái sử dụng trên nhiều màn hình, ví dụ như menu trên thanh công cụ (Toolbar) hoặc thanh điều hướng dưới (Bottom Navigation Bar).

## 📂 Cấu trúc Thư mục
Dự án được tổ chức một cách chuyên nghiệp, tách biệt rõ ràng các thành phần trong mã nguồn Java.

```plaintext
TLU-Study-Planner/
└── app/
    └── src/
        └── main/
            ├── java/
            │   └── com/example/tlu_study_planner/
            │       ├── activity/              // Chứa MainActivity
            │       ├── adapter/               // Chứa các lớp Adapter cho RecyclerView
            │       └── fragment/              // Chứa các màn hình (Fragment)
            │
            └── res/
                ├── drawable/
                ├── layout/
                ├── menu/
                ├── mipmap-[...]/
                ├── navigation/
                │   └── main_nav_graph.xml
                └── values/
```

## 🛠️ Hướng dẫn Cài đặt và Chạy thử
1.  **Clone a repository này về máy của bạn:**
    ```bash
    git clone [https://github.com/PhatDo744/TLU-Study-Planner.git](https://github.com/PhatDo744/TLU-Study-Planner.git)
    ```
2.  Mở dự án bằng **Android Studio**.
3.  Đợi cho Gradle build và đồng bộ hóa tất cả các thư viện cần thiết.
4.  Chạy ứng dụng trên máy ảo (Emulator) hoặc thiết bị Android vật lý.

---
