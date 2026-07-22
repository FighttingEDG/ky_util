# 考研工具开发进度

## 项目目标
基于 SpringBoot + Vue3 搭建 2026 考研个人效率工具，覆盖英一、数一、政治、408。

## 当前阶段
实现【MinIO 文件管理模块】。

## 技术栈
- 后端：SpringBoot 3.5.6 + Spring Data JPA + H2 数据库 + Maven + MinIO
- 前端：Vue 3 + Vite + Element Plus + Axios + ECharts
- 数据库：H2 文件数据库（本地 `./data/kaoyan`）
- 对象存储：MinIO（控制台与 API 同域名：`http://minio.shanjunwei.fun`，bucket: `kaoyan`；当前仅 HTTP，证书待配）

## 项目结构
```
ky_util/
├── kaoyan-server/          # SpringBoot 后端
│   ├── pom.xml
│   └── src/main/java/fun/jevon/kaoyan/
│       ├── KaoyanApplication.java
│       ├── config/         # WebConfig, MinioConfig
│       ├── controller/     # CardController, MistakeController, DailyTaskController, FocusController, KnowledgeController, FileController
│       ├── dto/            # ApiResponse, ReviewRequest, FocusStartRequest
│       ├── entity/         # Card, Subject, ReviewRecord, Mistake, MistakeReviewRecord, DailyTask, FocusSession, KnowledgeNode, KnowledgeEdge, KnowledgeNodeReviewRecord
│       ├── repository/     # 对应 Repository
│       ├── service/        # 对应 Service
│       └── vo/             # 对应 VO
│   └── src/main/resources/
│       ├── application.properties
│       └── data.sql
├── kaoyan-web/             # Vue 前端
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── main.js
│       ├── App.vue
│       ├── api/            # request.js, card.js, mistake.js, task.js, focus.js, knowledge.js, file.js
│       ├── components/     # FilePicker.vue
│       ├── router/         # index.js
│       └── views/          # CardList/Form/Review, MistakeList/Form/Review, DailyPlan, FocusTimer/Stats, KnowledgeGraph/NodeList/Review/Recommend, FileManager
├── kaoyan-tools-plan.md
├── PROGRESS.md
├── INFRA-aliyun-server.md  # 阿里云入口（frps + nginx）SSH 验证记录
├── INFRA-mac-mini.md       # Mac Mini MinIO 部署（待 SSH 验证）
└── .gitignore
```

## 已完成的模块

### 1. 记忆卡片系统 ✅
- 卡片 CRUD、艾宾浩斯复习调度、今日复习队列、掌握度统计
- 页面：卡片管理、今日复习、新增/编辑卡片

### 2. 错题本 ✅
- 错题 CRUD、错误原因分类、重做复习、薄弱知识点 Top N 统计
- 页面：错题管理、错题重做、新增/编辑错题

### 3. 每日计划与专注计时 ✅
- 每日任务清单、番茄钟倒计时、专注时长记录
- 今日/本周/本月专注统计、科目分布、近 7 天趋势
- 页面：任务清单、专注计时、专注统计

### 4. 408 知识图谱（高级版）✅
- 408 四门课知识点树形结构、前置依赖关系
- ECharts 力导向图可视化、节点按掌握率着色
- 知识点复习、薄弱点推荐、学习路径
- 页面：知识图谱、知识点管理、今日复习、薄弱推荐

### 5. MinIO 文件管理模块 ✅
- 浏览 MinIO `kaoyan` bucket 真实目录树
- 新建文件夹、上传图片、删除文件
- 卡片/错题表单集成图片选择
- 页面：文件管理、图片选择弹窗

## 当前模块状态

**MinIO 文件管理：后端/前端代码完成；Access Key 仍待用户填入 `application.properties`，尚未启动联调测试。**

**单词学习模块：后端/前端代码完成（2026-07-22），尚未启动测试。** 新增内容：
- 后端：`Word`、`WordExample`、`WordPhrase`、`WordReviewRecord`、`WordStats` 实体 + Repository + `WordImportService`（启动自动导入 Excel）+ `WordService` + `WordController`；pom.xml 新增 `poi-ooxml` 5.2.5
- 前端：`WordStudy.vue`（沉浸式学习：深色背景、大字卡片、翻面、发音、四档评分、XP 飘动、快捷键空格/1-4）、`WordList.vue`（单词库）、`WordStats.vue`（学习报告：XP/等级/streak/柱状图/饼图）；`package.json` 新增 `@element-plus/icons-vue`

## 待验证清单（单词模块）
- [ ] 启动后端自动导入必背词 2735 + 主考词 508（同单词合并，主考词优先）
- [ ] 学习队列按 stage 升序取词
- [ ] 翻面显示释义、例句、词组、词根/派生
- [ ] 发音按钮朗读单词（Web Speech API）
- [ ] 评分后 stage 推进、XP 累计、streak 更新
- [ ] 单词库搜索/筛选正常
- [ ] 学习报告图表正常

## 单词数据（已整理，位于 kaoyan-server/src/main/resources/）

### 必背词_clean.xlsx（2735 条）
| 列 | 说明 |
|---|---|
| 单词 | word |
| 音标 | phonetic |
| 词性 | word_class，如 `n. v.` |
| 释义 | meaning（词性+中文，可能多行） |
| 考点例句 | examples（可能多条，`\n` 分隔，中英对照） |
| 词组 | phrases（可能多条） |
| 派生词 | derivatives（词性+中文） |
| 近义词 | synonyms |
| 反义词 | antonyms |

### 主考词_clean.xlsx（508 条）
| 列 | 说明 |
|---|---|
| 单词 | word |
| 音标 | phonetic |
| 词性 | word_class |
| 释义 | meaning |
| 词根记忆 | root_memo |
| 基础语境 | base_example（中英） |
| 主考词义 | exam_meaning（词义+真题搭配，多行） |
| 拓展词 | extensions（词性+中文+搭配） |
| 真题重现 | exam_context（真题出处+解析，多行） |

### 数据库设计
`word` 主表 + `word_example`（例句，一对多）+ `word_phrase`（词组，一对多）+ `word_review_record`（复习记录）+ `word_stats`（XP/streak 单行统计）。`word_type` 区分 `must`（必背）/ `exam`（主考）。复习复用艾宾浩斯 stage 调度。XP 规则：again +5 / hard +10 / good +15 / easy +20，等级 = XP/100。

## 需要你手动操作（按顺序）

1. **MinIO Access Key**：在 MinIO 控制台生成，填入 `application.properties`
2. **依赖更新**：后端 `mvn clean install`（新增 MinIO + POI 依赖）；前端 `npm install`（新增图标库 + echarts）
3. **启动后端**：`cd kaoyan-server && mvn spring-boot:run`，首次启动会自动导入两个 Excel（日志会打印导入数量）
4. **启动前端**：`cd kaoyan-web && npm run dev`
5. **验证顺序**：背单词 → 文件管理（需 MinIO key 已填）

## 后续大方向（按顺序）
1. 统计可视化优化（饼图/环形图）
2. 知识点表单增加图片选择
3. 数据导入导出（备份 H2 数据库）
4. 复习首页 Dashboard（一屏看今日所有待办）

## 启动方式
- 后端：`cd kaoyan-server && mvn spring-boot:run`，端口 **8081**
- 前端：`cd kaoyan-web && npm install && npm run dev`，端口 5173
- H2 控制台：`http://localhost:8081/h2-console`

## 服务器/基础设施信息

> ⚠️ 以下信息为敏感信息，请勿上传到公开仓库或分享给他人。

## 基础设施架构

```
用户浏览器
    ↓
http://minio.shanjunwei.fun（控制台与 API 同域名，按路径区分）  （阿里云域名解析）
    ↓
阿里云服务器 120.55.112.67:80  （frp 服务端，当前仅 HTTP，证书待配）
    ↓
frp 隧道
    ↓
Mac Mini  （MinIO 实际部署机器）
```

### 阿里云服务器（frp 服务端 / 公网入口）
| 项目 | 值 |
|---|---|
| 公网 IP | `120.55.112.67` |
| 域名 | `shanjunwei.fun` |
| 子域名 | `minio.shanjunwei.fun` |
| SSH 端口 | `22` |
| SSH 用户名 | `root` |
| SSH 密码 | `YHY1024.` |
| 作用 | 接收公网请求，通过 frp 转发到 Mac Mini |

### Mac Mini（MinIO 实际部署机器）
| 项目 | 值 |
|---|---|
| 实际位置 | 本地内网机器 |
| 公网访问方式 | 通过 frp 映射到阿里云 `120.55.112.67:10001` |
| SSH 连接 | `ssh yuhongyang@120.55.112.67 -p 10001` |
| SSH 密码 | `SJW0328.` |
| 部署服务 | MinIO 服务端 |
| 公网域名 | 控制台与 API 同域名 `http://minio.shanjunwei.fun` |
| MinIO Bucket | `kaoyan`（新建，空） |
| Bucket 访问策略 | Private（需改为 Public） |

### MinIO 访问信息
| 项目 | 值 |
|---|---|
| 控制台 | `http://minio.shanjunwei.fun/browser/kaoyan` |
| API Endpoint | `http://minio.shanjunwei.fun`（与控制台同域名） |
| Bucket | `kaoyan` |
| Access Key / Secret Key | 在 MinIO 控制台 Access Keys 中生成，填入 `application.properties` |

> 2026-07-14 已 SSH 验证阿里云服务器（frps + nginx），详见 `INFRA-aliyun-server.md`；2026-07-20 复验 `http://minio.shanjunwei.fun/` 与 `/minio/health/live` 均 200，确认域名已切换到 `.fun` 且 API 不再需要独立子域名；Mac Mini 端待验证，见 `INFRA-mac-mini.md`。

### 本地开发环境
| 项目 | 值 |
|---|---|
| 工作目录 | `/Users/jevonsmac/Desktop/project/demo/ky_util` |
| JDK | 17 |
| Maven | 3.9.11 |
| 后端端口 | 8081 |
| 前端端口 | 5173 |
| 数据库 | H2 文件库，路径 `./kaoyan-server/data/kaoyan` |

## 变更递增记录
- 2026-07-09：创建 kaoyan-server 后端全部代码，记忆卡片 API 完成。
- 2026-07-09：创建 kaoyan-web 前端全部代码，记忆卡片页面完成。
- 2026-07-09：后端端口由 8080 改为 8081（原 8080 被占用），前端 Vite 代理同步改为 `http://localhost:8081`。
- 2026-07-09：修复 `data.sql` 重复执行导致的主键冲突，改用 `MERGE INTO subject ... KEY (code)`。
- 2026-07-09：新增错题本模块后端与前端代码。
- 2026-07-09：新增 `.gitignore`，排除 `target/`、`node_modules/`、`.idea/`、`data/`、`dist/`、日志等。
- 2026-07-09：新增每日计划与专注计时模块后端与前端代码。
- 2026-07-10：修复从任务列表进入专注页面时，番茄钟时长未同步任务预计时长的问题。
- 2026-07-10：新增 408 知识图谱高级版后端与前端代码。
- 2026-07-13：新增 MinIO 文件管理模块，含目录树浏览、图片上传、新建文件夹、卡片/错题关联图片。
- 2026-07-22：新增单词学习模块，含 Excel 自动导入（必背词+主考词）、沉浸式学习 UI、XP/streak 游戏化、发音、学习报告。
- 2026-07-22：优化语音发音（优先 Samantha/Alex/Google US English），新增手动重新导入按钮（单词库页面）。
- 2026-07-22：新增语速调节入口（学习页面右上角，0.6-1.2 可选）。
- 2026-07-13：新增 `INFRA-aliyun-server.md`、`INFRA-mac-mini.md` 基础设施文档（初版，待 SSH 验证）。
- 2026-07-14：SSH 验证阿里云服务器：frps（`/opt/frp/frps.ini`，bind_port 9902）+ nginx 已配置；确认 MinIO API 独立域名 `minioapi.shanjunwei.shop`；`application.properties` 的 `minio.endpoint` 同步更新；验证记录写入 `INFRA-aliyun-server.md`。
- 2026-07-17：整理 PROGRESS.md，同步 07-14 服务器验证结果与 MinIO 双域名信息；清理 `INFRA-aliyun-server.md` 旧草稿，补全 frps 配置代码块与待办清单。
- 2026-07-20：MinIO 域名由 `.shop` 切换到 `.fun` 且仅 HTTP（证书未配）；验证 `http://minio.shanjunwei.fun/` 与 `/minio/health/live` 均 200，确认控制台与 API 共用同一域名，废弃 `minioapi` 子域名方案；`application.properties` 的 `minio.endpoint` 改为 `http://minio.shanjunwei.fun`；同步更新 PROGRESS.md 与 `INFRA-aliyun-server.md`。

## 单词学习模块设计方案（待开发）

**结论：不单独开项目，放在同一个系统里，但 UI 单独设计。**

数据格式（Excel 导入）：
```csv
word,phonetic,word_class,meaning,example_en,example_cn,phrase,derivatives
```

游戏化机制：
- Streak 连续打卡
- XP / 等级
- 进度条
- 音频朗读（Web Speech API）
- 例句语境
- 智能排序
- 完成动画/音效
- 学习报告

## 其他待优化想法
- 专注统计、错题统计等页面增加圆形饼图/环形图
- 一屏 Dashboard 看今日所有待办
- H2 数据导入导出备份

## 下一步建议
1. 完成 MinIO 收尾：控制台生成 Access Key 填入 `application.properties`、bucket 改 Public，启动前后端测试文件上传。
2. 用户整理单词 Excel，进入单词学习模块开发。
3. 根据实际使用情况持续优化统计可视化。
4. 给 `minio.shanjunwei.fun` 配 TLS 证书，切换到 HTTPS。
