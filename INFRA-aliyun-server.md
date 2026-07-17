# 阿里云服务器 MinIO 入口配置

> 验证时间：2026-07-14
> 验证方式：SSH 登录 `root@120.55.112.67:22`
> 状态：已验证，frps + nginx 已配置

## 机器信息

| 项目 | 值 |
|---|---|
| 域名 | `shanjunwei.shop` |
| MinIO 控制台 | `https://minio.shanjunwei.shop` |
| MinIO API Endpoint | `https://minioapi.shanjunwei.shop` |
| Bucket | `kaoyan` |
| Bucket 访问策略 | Private（需改为 Public） |
| 公网 IP | `120.55.112.67` |
| SSH 端口 | `22` |
| 用户名 | `root` |
| 密码 | `YHY1024.` |

## DNS 配置

需要在阿里云 DNS 控制台添加一条 A 记录：

| 主机记录 | 记录类型 | 记录值 |
|---|---|---|
| `minioapi` | A | `120.55.112.67` |

`minio.shanjunwei.shop` 保持原有解析即可。

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

- nginx 已在服务器上运行，`minio.shanjunwei.shop`（控制台）与 `minioapi.shanjunwei.shop`（API）分开转发。
- 具体站点配置内容未记录，待下次 SSH 补充（`/etc/nginx/conf.d/`）。

## 待办

- [ ] 阿里云 DNS 添加 `minioapi` A 记录 → `120.55.112.67`
- [ ] MinIO 控制台生成 Access Key / Secret Key，填入后端 `application.properties`
- [ ] `kaoyan` bucket 访问策略 Private → Public
- [ ] 补充记录 nginx 站点配置细节
- [ ] Mac Mini 端 SSH 验证（见 `INFRA-mac-mini.md`）
