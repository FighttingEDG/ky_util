# 考研工具开发进度

## 项目目标
基于 SpringBoot + Vue3 搭建 2026 考研个人效率工具，覆盖英一、数一、政治、408。

## 当前阶段
实现【错题本】。

## 已完成的代码（2026-07-09）

### 记忆卡片系统（已完成）
后端：`Card`、`Subject`、`ReviewRecord` 实体、`CardService`、`CardController`、`ReviewRequest`、`CardVO`。
前端：`CardList.vue`、`CardReview.vue`、`CardForm.vue`。

### 错题本（新增）

#### 后端 kaoyan-server
- 实体：`Mistake.java`、`MistakeReviewRecord.java`
- Repository：`MistakeRepository.java`（含薄弱知识点统计 SQL）、`MistakeReviewRecordRepository.java`
- VO：`MistakeVO.java`
- Service：`MistakeService.java`（CRUD、今日复习、复习调度、统计、薄弱知识点）
- Controller：`MistakeController.java`

#### 后端 API
- `GET /api/mistakes` — 分页查询错题（subjectId/wrongReason/tag/keyword）
- `GET /api/mistakes/{id}` — 错题详情
- `POST /api/mistakes` — 创建错题
- `PUT /api/mistakes/{id}` — 更新错题
- `DELETE /api/mistakes/{id}` — 删除错题
- `GET /api/mistakes/today` — 今日待复习错题
- `POST /api/mistakes/{id}/review` — 提交复习结果（again/hard/good/easy）
- `GET /api/mistakes/stats` — 统计总/今日/已掌握/学习中数量
- `GET /api/mistakes/weak-points` — 薄弱知识点 Top N

#### 前端 kaoyan-web
- API：`api/mistake.js`
- 页面组件：
  - `MistakeList.vue`：统计、筛选、分页、薄弱知识点排行
  - `MistakeForm.vue`：新增/编辑错题表单
  - `MistakeReview.vue`：错题重做，查看答案后四档评分
- 导航和路由：`App.vue`、`router/index.js` 增加错题本入口

## 错题本状态
**后端代码：完成。前端代码：完成。**
尚未启动/测试。

## 后续大方向（按顺序）
1. 每日计划与专注计时
2. 408 知识图谱

## 手动操作说明（由用户执行）
- 后端启动：`cd kaoyan-server && mvn spring-boot:run`，端口 **8081**
- 前端启动：`cd kaoyan-web && npm install && npm run dev`，端口 5173
- H2 控制台：`http://localhost:8081/h2-console`

## 变更递增记录
- 2026-07-09：创建 kaoyan-server 后端全部代码，记忆卡片 API 完成。
- 2026-07-09：创建 kaoyan-web 前端全部代码，记忆卡片页面完成。
- 2026-07-09：后端端口由 8080 改为 8081（原 8080 被占用），前端 Vite 代理同步改为 `http://localhost:8081`。
- 2026-07-09：修复 `data.sql` 重复执行导致的主键冲突，改用 `MERGE INTO subject ... KEY (code)`。
- 2026-07-09：新增错题本模块后端与前端代码，含错题管理、重做复习、薄弱知识点统计。

- 2026-07-09：新增 `.gitignore`，排除 `target/`、`node_modules/`、`.idea/`、`data/`、`dist/`、日志等不需要提交的文件。

## 下一步建议
1. 用户手动重启后端和前端，验证错题本 CRUD、重做复习、薄弱知识点统计。
2. 若验证通过，进入【每日计划与专注计时】开发。
