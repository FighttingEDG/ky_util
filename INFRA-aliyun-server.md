# 阿里云服务器 MinIO 入口配置

> 验证时间：2026-07-14
> 验证方式：SSH 登录 `root@120.55.112.67:22`
> 状态：已验证，frps + nginx 已配置
> 2026-07-20 复验：`http://minio.shanjunwei.fun/` 与 `/minio/health/live` 均返回 200；域名已从 `.shop` 切换到 `.fun`，当前仅 HTTP（未配证书），控制台与 API 共用同一域名，不再需要独立的 `minioapi` 子域名。

## 机器信息

| 项目 | 值 |
|---|---|
| 域名 | `shanjunwei.fun` |
| MinIO 控制台 | `http://minio.shanjunwei.fun` |
| MinIO API Endpoint | `http://minio.shanjunwei.fun`（与控制台同域名） |
| Bucket | `kaoyan` |
| Bucket 访问策略 | Private（需改为 Public） |
| 公网 IP | `120.55.112.67` |
| SSH 端口 | `22` |
| 用户名 | `root` |
| 密码 | `YHY1024.` |

## DNS 配置

只需保证 `minio.shanjunwei.fun` 的 A 记录指向 `120.55.112.67` 即可。
控制台与 API 挂在同一域名下，不再需要 `minioapi` 子域名。

## 已验证的部署配置

### 1. 运行进程

- frp 服务端：`/opt/frp/frps -c /opt/frp/frps.ini`
- nginx：`/usr/sbin/nginx`

### 2. frp 服务端配置（`/opt/frp/frps.ini`）

```ini
[common]
bind_port = 9902
token = jevon
log_level = info
log_file = ./frps.log
log_max_days = 3
```

### 3. nginx 配置

- nginx 已在服务器上运行，`minio.shanjunwei.fun` 统一转发控制台与 API（同域名，按路径区分）。
- 当前仅 HTTP 80 端口，未配置 TLS 证书，Access Key 会明文过网络；自用学习项目可接受，后续需补 HTTPS。
- 具体站点配置内容未记录，待下次 SSH 补充（`/etc/nginx/conf.d/`）。

## 待办

- [ ] MinIO 控制台生成 Access Key / Secret Key，填入后端 `application.properties`
- [ ] `kaoyan` bucket 访问策略 Private → Public
- [ ] 为 `minio.shanjunwei.fun` 配置 TLS 证书，切换到 HTTPS
- [ ] 补充记录 nginx 站点配置细节
- [ ] Mac Mini 端 SSH 验证（见 `INFRA-mac-mini.md`）
