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

| 方法 | 地址 | 说明 |
|---|---|---|
| GET | `/api/v1/home` | 首页聚合数据 |
| GET | `/api/v1/search` | 内容搜索 |
| POST | `/api/v1/search/click` | 搜索结果点击上报 |
| GET | `/api/v1/contents/{id}` | 内容详情 |
| GET | `/api/v1/categories` | 分类列表 |
| GET | `/api/v1/random-tip` | 随机建议 |
| GET | `/actuator/health` | 健康检查 |

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

## 第一阶段说明

当前版本有意保持简单：

- 暂未接微信登录，接口保留了模拟 `userId` 和匿名 `sessionId`；
- 暂未实现收藏接口和管理后台；
- 暂未使用 Elasticsearch、向量数据库和大模型；
- 搜索采用 MySQL 字段、标签和分类匹配，并按相关度、热度和发布时间排序；
- 无结果搜索会返回热门内容兜底，同时仍然记录该搜索词。

## 交给 Trae 的下一条指令

```text
请先阅读 README.md、schema.sql、data.sql 和现有 Java 代码。
不要重构现有接口路径和统一响应结构。
先通过 Docker Compose 启动项目，依次验证首页、搜索、搜索点击上报、内容详情、分类和随机建议接口。
修复所有编译或启动错误，并补充一份 docs/api-test.md，记录每个接口的请求示例和实际返回。
完成后再开始制作微信小程序首页与搜索结果页。
```
