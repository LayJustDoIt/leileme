# API 联调清单

## 首页

```bash
curl http://localhost:8080/api/v1/home
```

## 搜索

```bash
curl "http://localhost:8080/api/v1/search?keyword=爆款文案&page=1&size=20&sessionId=demo"
```

## 内容详情

```bash
curl "http://localhost:8080/api/v1/contents/1004?sessionId=demo"
```

## 分类

```bash
curl http://localhost:8080/api/v1/categories
```

## 随机建议

```bash
curl http://localhost:8080/api/v1/random-tip
```
