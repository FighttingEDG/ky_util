# 考研工具开发进度

## 项目目标
基于 SpringBoot + Vue3 搭建 2026 考研个人效率工具，覆盖英一、数一、政治、408。

## 当前阶段
实现【MinIO 文件管理模块】。

## 技术栈
- 后端：SpringBoot 3.5.6 + Spring Data JPA + H2 数据库 + Maven + MinIO
- 前端：Vue 3 + Vite + Element Plus + Axios + ECharts
- 数据库：H2 文件数据库（本地 `./data/kaoyan`）
- 对象存储：MinIO（API：`https://minioapi.shanjunwei.shop`，控制台：`https://minio.shanjunwei.shop`，bucket: `kaoyan`）

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
**MinIO 文件管理：后端/前端代码完成；2026-07-14 已 SSH 验证阿里云入口（frps + nginx），API 独立域名 `minioapi.shanjunwei.shop` 已写入 `application.properties`；尚未启动联调测试。**

## 待验证清单
- [ ] 后端能连接 MinIO 并列出目录树
- [ ] 能在指定目录上传图片
- [ ] 前端文件管理器显示目录树和文件
- [ ] 图片 URL 可直接在浏览器打开
- [ ] 卡片/错题表单能选择图片

## 需要你手动操作（MinIO 配置）
1. 在阿里云 DNS 控制台添加 A 记录：`minioapi` → `120.55.112.67`（`minio.shanjunwei.shop` 保持不变）
2. 在 MinIO 控制台生成 Access Key
3. 填入 `kaoyan-server/src/main/resources/application.properties`（endpoint 已更新，只需替换 key）：
   ```properties
   minio.endpoint=https://minioapi.shanjunwei.shop
   minio.bucket=kaoyan
   minio.access-key=你的key
   minio.secret-key=你的secret
   ```
4. 把 `kaoyan` bucket 的 Access Policy 改为 Public（当前是 Private）
5. 后端重新 `mvn clean install`（新增了 MinIO 依赖）
6. 前端重新 `npm install`

## 后续大方向（按顺序）
1. 单词学习模块（等待用户整理 Excel）
2. 统计可视化优化（饼图/环形图）
3. 知识点表单增加图片选择
4. 数据导入导出（备份 H2 数据库）
5. 复习首页 Dashboard（一屏看今日所有待办）

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
https://minio.shanjunwei.shop（控制台）/ https://minioapi.shanjunwei.shop（API）  （阿里云域名解析）
    ↓
阿里云服务器 120.55.112.67:443  （frp 服务端）
    ↓
frp 隧道
    ↓
Mac Mini  （MinIO 实际部署机器）
```

### 阿里云服务器（frp 服务端 / 公网入口）
| 项目 | 值 |
|---|---|
| 公网 IP | `120.55.112.67` |
| 域名 | `shanjunwei.shop` |
| 子域名 | `minio.shanjunwei.shop` |
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
| 公网域名 | 控制台 `https://minio.shanjunwei.shop`，API `https://minioapi.shanjunwei.shop` |
| MinIO Bucket | `kaoyan`（新建，空） |
| Bucket 访问策略 | Private（需改为 Public） |

### MinIO 访问信息
| 项目 | 值 |
|---|---|
| 控制台 | `https://minio.shanjunwei.shop/browser/kaoyan` |
| API Endpoint | `https://minioapi.shanjunwei.shop` |
| Bucket | `kaoyan` |
| Access Key / Secret Key | 在 MinIO 控制台 Access Keys 中生成，填入 `application.properties` |

> 2026-07-14 已 SSH 验证阿里云服务器（frps + nginx），详见 `INFRA-aliyun-server.md`；Mac Mini 端待验证，见 `INFRA-mac-mini.md`。

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
- 2026-07-13：新增 `INFRA-aliyun-server.md`、`INFRA-mac-mini.md` 基础设施文档（初版，待 SSH 验证）。
- 2026-07-14：SSH 验证阿里云服务器：frps（`/opt/frp/frps.ini`，bind_port 9902）+ nginx 已配置；确认 MinIO API 独立域名 `minioapi.shanjunwei.shop`；`application.properties` 的 `minio.endpoint` 同步更新；验证记录写入 `INFRA-aliyun-server.md`。
- 2026-07-17：整理 PROGRESS.md，同步 07-14 服务器验证结果与 MinIO 双域名信息；清理 `INFRA-aliyun-server.md` 旧草稿，补全 frps 配置代码块与待办清单。

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
1. 完成 MinIO 收尾：DNS 加 `minioapi` A 记录、生成 Access Key、bucket 改 Public，启动前后端测试文件上传。
2. 用户整理单词 Excel，进入单词学习模块开发。
3. 根据实际使用情况持续优化统计可视化。
