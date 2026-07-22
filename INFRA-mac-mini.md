# Mac Mini MinIO 部署情况

> 来源：PROGRESS.md 中的基础设施信息，尚未通过 SSH 验证。
> 状态：待 SSH 验证。

## 机器信息

| 项目 | 值 |
|---|---|
| 机器 | Mac Mini |
| 用途 | MinIO 实际部署节点 |
| 访问方式 | 通过阿里云服务器 frp 转发 |
| SSH 跳板 | `ssh yuhongyang@120.55.112.67 -p 10001` |
| 密码 | `SJW0328.` |

## 部署说明

- MinIO 实际部署在本机（Mac Mini）上。
- 通过 frp 把 MinIO 服务转发到阿里云服务器 `120.55.112.67`。
- 阿里云服务器再通过域名 `minio.shanjunwei.fun` 暴露到公网（控制台与 API 同域名，当前仅 HTTP）。

## 待验证内容

- [ ] Mac Mini 上是否确实运行了 MinIO 服务
- [ ] MinIO 版本、监听端口、启动方式（systemd / docker / 手动）
- [ ] 数据目录、配置文件路径
- [ ] `kaoyan` bucket 是否已创建
- [ ] bucket 访问策略（Private / Public）
- [ ] 已生成的 Access Key / Secret Key 信息
- [ ] frp 客户端配置（转发端口、目标地址）

## 验证命令（计划）

```bash
# 通过阿里云跳板登录 Mac Mini
ssh yuhongyang@120.55.112.67 -p 10001

# 检查 MinIO 进程
ps aux | grep minio

# 检查端口监听
netstat -an | grep LISTEN
# 或
ss -tlnp

# 若使用 Docker
docker ps | grep minio

# 检查 bucket 策略（需 mc 客户端）
mc alias set local http://localhost:9000 ROOT_KEY ROOT_SECRET
mc ls local/kaoyan
mc policy get local/kaoyan
```

## 备注

- 当前 `kaoyan-server/src/main/resources/application.properties` 中 MinIO 的 `access-key` 和 `secret-key` 仍为占位符，需要生成真实 key 后填入。
- 当前 bucket 访问策略在 PROGRESS.md 中记录为 Private，需要改为 Public 才能在前端直接访问图片 URL。
