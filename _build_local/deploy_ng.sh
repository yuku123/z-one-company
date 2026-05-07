#!/bin/bash
# ============================================================
# 部署 nginx 配置（不碰前端/后端）
# 用法: ./deploy_ng.sh
# ============================================================

SERVER="zifang@101.37.80.51"
NGINX_CONF="_build_local/ops/ng/z-one-company.conf"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
log()  { echo -e "${GREEN}[$(date +%H:%M:%S)] $1${NC}"; }
warn() { echo -e "${YELLOW}[WARN] $1${NC}"; }
err()  { echo -e "${RED}[ERROR] $1${NC}"; exit 1; }

log "===================== 部署 nginx 配置 ====================="

# scp 源是本地路径，目标才是 $SERVER:...
scp "$NGINX_CONF" "${SERVER}:/tmp/z-one-company.conf"

ssh $SERVER << 'EOF'
    sudo cp /tmp/z-one-company.conf /etc/nginx/sites-available/z-one-company
    sudo ln -sf /etc/nginx/sites-available/z-one-company /etc/nginx/sites-enabled/

    # 关闭默认 default 站点（如果有冲突）
    sudo rm -f /etc/nginx/sites-enabled/default

    # 测试并重载
    sudo nginx -t && sudo nginx -s reload && echo "nginx 配置生效"
EOF

log "nginx 配置部署完成"
