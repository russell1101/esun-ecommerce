# 玉山銀行電商平台

簡易電商購物中心平台，實作前台會員購物與後台商品管理，包含完整 JWT 認證、樂觀鎖、Stored Procedure 交易管理與 Docker 容器化部署。

---

## 技術棧

| 項目 | 選擇 |
|------|------|
| 後端框架 | Spring Boot 3.x |
| Java 版本 | Java 17 |
| ORM | Spring Data JPA（Hibernate）|
| 資料庫 | MySQL 8.0 |
| 安全 | Spring Security + jjwt 0.12.x |
| 前端框架 | Vue 3 + Vue CLI（webpack）|
| 前端套件 | Element Plus、Axios、Pinia |
| 套件管理 | yarn（沿用模板 yarn.lock 確保版本一致性）|
| 容器化 | Docker + Docker Compose |

> 前端採用 Vue CLI（webpack）而非 Vite，原因是沿用 Skote 模板的技術棧以確保 SCSS 與元件相容性。

---

## 快速啟動（生產環境）

**前提：已安裝 Docker Desktop 或 Docker Engine + Compose v2**

```bash
git clone <repo-url>
cd esun-ecommerce
docker compose up --build
```

等待以下三行出現後即啟動完成：

```
esun-mysql    | ... ready for connections
esun-backend  | Started EsunEcommerceApplication in X seconds
esun-frontend | ... nginx started
```

| 服務 | 網址 |
|------|------|
| 前台 | http://localhost/shop |
| 後台 | http://localhost/admin/login |
| API | http://localhost:8080 |

---

## 預設帳號

| 角色 | 帳號 | 密碼 |
|------|------|------|
| 管理員 | `admin` | `test123` |
| 會員 | `member1` | `test123` |

> 密碼皆以 BCrypt 加密儲存於資料庫，程式碼中無明文密碼。

---

## 頁面路由

| 路由 | 頁面 | 存取權限 |
|------|------|------|
| `/shop` | 商品列表 | **公開**（無需登入可瀏覽） |
| `/login` | 會員登入 | 公開 |
| `/register` | 會員註冊 | 公開 |
| `/orders` | 我的訂單 | 需會員登入 |
| `/admin/login` | 管理員登入 | 公開 |
| `/admin/products` | 商品管理（新增 / 編輯） | 需管理員登入 |
| `/admin/orders` | 訂單管理（查看明細） | 需管理員登入 |

> 進入商品列表可自由瀏覽，點擊「立即購買」時若尚未登入，系統會導向登入頁。

---

## API 設計

### 統一回應格式

所有 API 都會可以用 response body 的 `success` 欄位判斷結果：

```json
{
  "success": 1,
  "errMsg": null,
  "token": "eyJhbGci...",
  "data": {}
}
```

| `success` | 意義 |
|-----------|------|
| `1` | 成功 |
| `-999` | Token 無效或過期，需重新登入 |
| `-1` | 業務錯誤（庫存不足、帳號重複等）|
| `-2` | 格式驗證錯誤（欄位缺失、格式不符）|
| `-500` | 系統錯誤 |

> `token` 欄位位於最外層（與 `success` 同層），方便前端 Axios interceptor 在任何 response 直接取用。

---

### 認證（無需 JWT）

#### POST `/api/front/auth/register` — 會員註冊
```json
{
  "username": "user001",
  "password": "mypassword",
  "email": "user@example.com",
  "phone": "0912345678"
}
```

#### POST `/api/front/auth/login` — 會員登入
```json
{ "username": "member1", "password": "test123" }
```
Response `data`：
```json
{ "role": "MEMBER", "userId": 1, "username": "member1" }
```

#### POST `/api/admin/auth/login` — 管理員登入
```json
{ "username": "admin", "password": "test123" }
```
Response `data`：
```json
{ "role": "ADMIN", "userId": 1, "username": "admin" }
```

---

### 商品（前台，無需 JWT）

#### GET `/api/front/products/available` — 查詢上架且有庫存的商品

無 request body。

Response `data`：
```json
[
  {
    "productSn": 1,
    "productId": "P001",
    "productName": "osii 舒壓按摩椅",
    "price": 98000.00,
    "quantity": 5,
    "status": 1,
    "inptime": "2025-05-16T14:30:00"
  }
]
```

---

### 商品（後台，需 Admin JWT）

#### POST `/api/admin/products` — 新增商品
```json
{
  "productName": "新商品名稱",
  "price": 1000.00,
  "quantity": 10,
  "status": 1
}
```
> `status` 選填，不傳預設 `1`（上架）。`productId` 由系統自動產生，無需傳入。

#### GET `/api/admin/products` — 查詢所有商品（含下架）

無 request body。

#### PUT `/api/admin/products/{productId}` — 更新商品
```json
{
  "productName": "修改後的商品名稱",
  "price": 1200.00,
  "quantity": 8,
  "status": 0
}
```
> `status`：`0` 下架 / `1` 上架。更新時觸發樂觀鎖保護，若版本衝突回傳 `-1`。

---

### 訂單（前台，需 Member JWT）

#### POST `/api/front/orders` — 建立訂單
```json
{
  "items": [
    { "productId": "P001", "quantity": 1 },
    { "productId": "P002", "quantity": 2 }
  ],
  "remark": "備註（選填）"
}
```
Response `data`：訂單編號字串，例如 `"Ms20250516843921"`。

#### GET `/api/front/orders/me` — 查詢自己的訂單（含明細）

無 request body。

Response `data`：
```json
[
  {
    "orderId": "Ms20250516843921",
    "totalPrice": 100400.00,
    "payStatus": 0,
    "orderStatus": 1,
    "remark": "備註",
    "inptime": "2025-05-16T14:30:00",
    "details": [
      {
        "orderItemSn": 1,
        "productId": "P001",
        "productName": "osii 舒壓按摩椅",
        "quantity": 1,
        "standPrice": 98000.00,
        "itemPrice": 98000.00
      }
    ]
  }
]
```

---

### 訂單（後台，需 Admin JWT）

#### GET `/api/admin/orders` — 查詢所有訂單主檔

無 request body。Response 含 `memberUsername` 欄位。

#### GET `/api/admin/orders/{orderId}/details` — 查詢訂單明細

無 request body。Response 同前台 `details` 陣列結構。

---

## 資料庫 Schema 概覽

```
admin        管理員資料（帳號存 DB，BCrypt 加密）
member       會員資料
product      商品資料（含 @Version 樂觀鎖）
orders       訂單主檔
order_detail 訂單明細（FK 使用 order_sn / product_sn 整數關聯）
```

初始化 SQL 執行順序（Docker 啟動時自動執行）：

| 檔案 | 說明 |
|------|------|
| `DB/01_schema.sql` | 建立所有資料表 |
| `DB/02_data.sql` | 預設管理員帳號 + 3 筆範例商品 |
| `DB/03_procedures.sql` | `create_order` Stored Procedure |

---

## 停止 / 清除環境

```bash
# 停止容器（保留 DB 資料）
docker compose down

# 完全清除（含 DB volume），用於重新測試
docker compose down -v
```
