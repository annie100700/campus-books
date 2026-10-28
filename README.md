# CampusBooks
CampusBooks是筆者以學習後端技能為目標打造的個人專案，模擬面向大學生族群的二手書交易平台。  
本專案使用以下技術:
- Restful API 設計
- Spring Boot 應用程式開發
- Spring Security
- JWT 驗證
- 資料庫設計(使用MySQL)
- JPA/Hibernate
- 單元測試 (待完善)
- Dock 容器化 (未來規劃)

## API Overview端點概覽
Base URL:
```text
/api.campus-books.com
```
### Book 相關
| Method   | Endpoint            | Authentication |
|----------|---------------------|----------------|
| `POST`   | `/books/`           | JWT            |
| `GET`    | `/books/search`     | Public         |
| `GET`    | `/books/**`         | Public         |
| `PATCH`  | `/books/{id}`       | JWT            |
| `DELETE` | `/books/{id}`       | JWT            |
| `DELETE` | `/books/{id}/image` | JWT            |
### User 相關
| Method   | Endpoint                                    | Authentication       |
|----------|---------------------------------------------|----------------------|
| `POST`   | `/users/register`                           | Public               |
| `POST`   | `/users/login`                              | Public               |
| `POST`   | `/users/refresh`                            | Public               |
| `POST`   | `/users/forget-password`                    | Public               |
| `POST`   | `/users/refresh`                            | Refresh Token        |
| `POST`   | `/users/logout`                             | JWT                  |
| `POST`   | `/users/reset-password`                     | Reset Password Token |
| `GET`    | `/users/`                                   | JWT                  |
| `GET`    | `/users/{account}`                          | JWT                  |
| `GET`    | `/users/{account}/books/{status}`           | JWT                  |
| `GET`    | `/users/{account}/orders/purchase/{status}` | JWT                  |
| `GET`    | `/users/{account}/orders/sell/{status}`     | JWT                  |
| `PATCH`  | `/users/{account}`                          | JWT                  |
| `PATCH`  | `/users/current/password`                   | JWT                  |
| `DELETE` | `/users/{account}`                          | JWT                  |
### Order 相關
| Method  | Endpoint                | Authentication |
|---------|-------------------------|----------------|
| `POST`  | `/orders/`              | JWT            |
| `GET`   | `/orders/`              | JWT            |
| `GET`   | `/orders/{id}`          | JWT            |
| `PATCH` | `/{id}/status/complete` | JWT            |
| `PATCH` | `/{id}/status/cancel`   | JWT            |

## System Architecture系統架構
本專案為分層架構:
```text
Client
│ HTTP/HTTPS
↓
Controller
↓
Service
↓
Repository
↓
MySQL
```
Authentication Flow由 JWT和 Spring Security先後處理不同部分:
```text
Client
│ Login
↓
User Controller
↓
User Service
↓
JWT Service
↓
JWT Access Token
↓
Client
│ Authorization: Bearer<access-token>
↓
JWT Authentication Filter
↓
Spring Security
↓
Protected Controller
```

## Project Structure專案結構
本專案遵循典型的Spring Boot分層結構:
```text
campus-books/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/annie/campus_books/
│   │   │       ├── configuration/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       ├── security.filter/
│   │   │       ├── service/
│   │   │       └── utilities/
│   │   └── resources/
│   │       ├── application.yaml
│   │       ├── application-dev.yaml
│   │       └── application-prod.yaml
│   └── test/
├── .gitignore 
├── pom.xml
└── README.md
```

## Tech Stack技術堆疊
### 後端
- Java 21
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- Hibernate
- JWT
- Maven
### 資料庫
- MySQL
### 電子郵件
- Spring Boot Starter Mail
- Gmail SMTP
### 測試
- JUnit 5
- Mockito
- Data Jpa Test
### 開發工具
- intellij IDEA
- Postman
- Git/GitHub
### 部署
以下是預計部署的環境:
- Docker

## Testing測試
本專案使用Mockito進行單元測試，主要的目標包括:
- Controller層
- Service層
- Repository層  
  使用以下指令執行測試:
```
./mvnw test
```
Window環境也可用以下指令:
```
mvnw.cmd test
```
目前僅包含BookController、BookService和BookRepo的部分方法。

## Function功能
### 書籍相關
- 新增書籍
- 瀏覽書籍和詳細資料
- 更新書籍資訊或圖片
- 刪除書籍
- 管理書籍
### 使用者相關
- 註冊
- 登入
- 基於JWT驗證的授權(Access 和 refresh token)
- 查看當前使用者
- 登出
- 透過驗證email重設密碼
- 基於身分的授權制度
- 查看擁有的買賣訂單(可依狀態篩選)
### 訂單相關
- 建立、取消和完成訂單
### 授權
USER僅可對自己擁有的物件進行刪改動作，ADMIN則可對幾乎所有物件進行操作。

## Installation安裝
### Requirements環境要求
請確保以下應用已安裝:
- Java21
- Maven
- MySQL8
- Git
### Step1. 複製 Repository
執行以下指令:
```
git clone <repository-url>
cd campus-books
```
### Step2. 建立資料庫
致行以下指令以建立一個MySQL資料庫:
```
CREATE DATABASE campus_books;
```
### STEP3. 設定環境變數
以下是必要的環境變數及其範例:  
```
DB_HOST=localhost  
DB_PORT=3306  
DB_NAME=campus_books  
DB_USER=your_username  
DB_PASSWORD=your_password  

MAIL_USER=your_email@gmail.com
MAIL_PASSWORD=your_app_password

SECRET_KEY=your_secret_key
```
### STEP4. 執行應用程式
你可以用Maven執行:
```
./mvnw spring-boot:run
```
或是在Windows環境執行以下指令:
```
mvnw.cmd spring-boot:run
```
