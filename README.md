# 累了么 V1 后端

这是“累了么”第一阶段的可运行后端骨架，核心目标是打通：

```text
首页 → 搜索 → 搜索结果点击 → 内容详情 → 行为记录
```

## 技术栈

- Java 21
- Spring Boot 3.5.16
- MyBatis-Plus 3.5.17
- MySQL 8.4
- Redis（已预留，V1 暂未用于核心业务）
- Maven
- Docker Compose

## 已实现接口

### V1 第一阶段

| 方法 | 地址 | 说明 |
|---|---|---|
| GET | `/api/v1/home` | 首页聚合数据 |
| GET | `/api/v1/search` | 内容搜索 |
| POST | `/api/v1/search/click` | 搜索结果点击上报 |
| GET | `/api/v1/contents/{id}` | 内容详情（登录后返回 `isFavorite`） |
| GET | `/api/v1/categories` | 分类列表 |
| GET | `/api/v1/random-tip` | 随机建议 |
| GET | `/actuator/health` | 健康检查 |

### V1 第二阶段：登录 / 收藏 / 浏览历史

| 方法 | 地址 | 鉴权 | 说明 |
|---|---|---|---|
| POST | `/api/v1/auth/wechat-login` | 否 | 微信登录（code 换 token），登录时合并匿名浏览历史 |
| POST | `/api/v1/contents/{id}/favorite` | 是 | 收藏内容（幂等） |
| DELETE | `/api/v1/contents/{id}/favorite` | 是 | 取消收藏（幂等） |
| GET | `/api/v1/me/favorites?page=1&size=20` | 是 | 我的收藏列表（分页） |
| GET | `/api/v1/me/history?page=1&size=20` | 是 | 浏览历史列表（分页，按最近浏览倒序） |
| DELETE | `/api/v1/me/history` | 是 | 清空浏览历史 |
| DELETE | `/api/v1/me/history/{contentId}` | 是 | 删除单条浏览历史 |

请求头鉴权方式：

```text
Authorization: Bearer <accessToken>
```

未携带或携带失效 Token 访问需要鉴权的接口会返回 `401`。

## 最省事的启动方式

电脑先安装 Docker Desktop，然后在项目根目录运行：

```bash
docker compose up --build
```

启动完成后访问：

```text
http://localhost:8080/actuator/health
http://localhost:8080/api/v1/home
http://localhost:8080/api/v1/search?keyword=爆款文案&page=1&size=20
http://localhost:8080/api/v1/contents/1004?sessionId=test-session
http://localhost:8080/api/v1/categories
http://localhost:8080/api/v1/random-tip
```

停止服务：

```bash
docker compose down
```

同时删除数据库数据：

```bash
docker compose down -v
```

## 本地 Maven 启动

先启动本地 MySQL，并创建：

```text
数据库：leileme
用户名：leileme
密码：leileme123
```

然后运行：

```bash
mvn spring-boot:run
```

也可通过环境变量修改数据库配置：

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=leileme
export DB_USER=leileme
export DB_PASSWORD=leileme123
mvn spring-boot:run
```

Windows PowerShell：

```powershell
$env:DB_HOST="localhost"
$env:DB_NAME="leileme"
$env:DB_USER="leileme"
$env:DB_PASSWORD="leileme123"
mvn spring-boot:run
```

## 搜索示例

```bash
curl "http://localhost:8080/api/v1/search?keyword=爆款文案&page=1&size=20&sessionId=demo"
```

按类型筛选：

```bash
curl "http://localhost:8080/api/v1/search?keyword=AI&contentType=TOOL&page=1&size=20"
```

点击上报：

```bash
curl -X POST "http://localhost:8080/api/v1/search/click" \
  -H "Content-Type: application/json" \
  -d '{
    "searchRequestId":"把搜索接口返回的ID放这里",
    "contentId":1004,
    "position":1,
    "sessionId":"demo"
  }'
```

## 登录配置（微信登录 / Mock 登录）

后端登录相关配置全部通过环境变量注入，`application.yml` 不写任何真实密钥。

### 环境变量

| 变量名 | 用途 | 默认值 |
|---|---|---|
| `WECHAT_APP_ID` | 微信小程序 AppID | 空 |
| `WECHAT_APP_SECRET` | 微信小程序 AppSecret（仅服务端持有） | 空 |
| `JWT_SECRET` | JWT 签名密钥（生产必须为强随机 32 字节以上） | 空 |
| `WECHAT_MOCK_ENABLED` | 是否开启 mock 登录（开发用） | `false` |

> 安全约束：`WECHAT_APP_SECRET` 与 `JWT_SECRET` 禁止写入小程序代码、Git 仓库或任何接口响应。`openid` 不会直接返回给前端，仅用于服务端识别用户。

### 方式一：Mock 登录（开发推荐）

复制示例配置为本地配置：

```bash
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml
```

`application-dev.yml` 已在 `.gitignore` 中，不会被提交。启用 mock 后，可用任意测试 code（如 `dev-user-001`）直接调用登录接口，相同 code 会对应同一模拟 openid，无需真实微信 AppID/Secret：

```bash
# 启用 dev profile
export SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run

# 调用登录接口
curl -X POST http://localhost:8080/api/v1/auth/wechat-login \
  -H "Content-Type: application/json" \
  -d '{"code":"dev-user-001","sessionId":"anon-session-1"}'
```

### 方式二：正式微信登录

在小程序后台配置后端域名后，通过环境变量注入真实凭证：

```bash
export WECHAT_APP_ID=你的小程序AppID
export WECHAT_APP_SECRET=你的小程序AppSecret
export JWT_SECRET=生成的强随机密钥至少32字节
export WECHAT_MOCK_ENABLED=false
mvn spring-boot:run
```

正式环境下后端会调用微信 `jscode2session` 接口，用小程序 `wx.login()` 返回的 `code` 换取 `openid`，再按 `openid` 查询或创建 `user_account`，最后签发 JWT。`mock-enabled` 在生产必须关闭。

## 数据初始化

应用默认执行：

```text
src/main/resources/schema.sql
src/main/resources/data.sql
```

已内置：

- 6 个分类
- 6 个热门搜索词
- 25 条演示内容
- 标签及内容标签关系
- 适用人群数据
- 首页推荐配置

生产环境不建议每次启动都执行初始化脚本。部署前将：

```text
SQL_INIT_MODE=never
```

并使用 `sql/` 目录中的脚本单独初始化数据库。

## 阶段说明

**第一阶段**已打通首页 → 搜索 → 详情 → 行为记录主链路，接口保留了模拟 `userId` 和匿名 `sessionId`。

**第二阶段**已新增：

- 微信登录（生产 `jscode2session` + 开发 mock 模式），JWT 7 天有效期
- 收藏 / 取消收藏（幂等、事务计数、详情返回 `isFavorite`）
- 浏览历史（重复浏览累加 `browse_count`、按最近浏览倒序、支持单条删除与清空）
- 登录时合并匿名 `sessionId` 对应的浏览记录到当前用户
- 小程序「我的」页面登录态、收藏列表页、浏览历史页

暂未实现：

- 会员、积分、广告、AI 生成功能
- Elasticsearch、向量数据库和大模型
- 收藏的匿名状态（第一版收藏必须登录，不迁移匿名收藏）

搜索采用 MySQL 字段、标签和分类匹配，并按相关度、热度和发布时间排序；无结果搜索会返回热门内容兜底，同时仍然记录该搜索词。

## 交给 Trae 的下一条指令

```text
请先阅读 README.md、schema.sql、data.sql 和现有 Java 代码。
不要重构现有接口路径和统一响应结构。
先通过 Docker Compose 启动项目，依次验证首页、搜索、搜索点击上报、内容详情、分类和随机建议接口。
修复所有编译或启动错误，并补充一份 docs/api-test.md，记录每个接口的请求示例和实际返回。
完成后再开始制作微信小程序首页与搜索结果页。
```
