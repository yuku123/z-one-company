#!/bin/bash
# ============================================================
# 快速部署：仅 push + 远程构建 + 重启（不打印详细日志）
# 用法: ./push.sh
#       ./push.sh --backend-only
#       ./push.sh --frontend-only
# ============================================================

SERVER="zifang@101.37.80.51"
REMOTE_DIR="/home/zifang/workplace/z-one-company"
LOG_DIR="${REMOTE_DIR}/logs"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
log()  { echo -e "${GREEN}[$(date +%H:%M:%S)] $1${NC}"; }
warn() { echo -e "${YELLOW}[WARN] $1${NC}"; }
err()  { echo -e "${RED}[ERROR] $1${NC}"; exit 1; }

DEPLOY_BACKEND=1
DEPLOY_FRONTEND=1

for arg in "$@"; do
    case $arg in
        --backend-only)   DEPLOY_FRONTEND=0 ;;
        --frontend-only)  DEPLOY_BACKEND=0 ;;
    esac
done

log "===================== push + 远程构建 ====================="

# 1. push
log "1. git push..."
git push origin main:main

# 2. 远程 pull + build
if [ $DEPLOY_BACKEND -eq 1 ]; then
    log "2. 远程构建后端..."
    ssh $SERVER << 'EOF'
        cd /home/zifang/workplace/z-one-company
        cd /home/zifang/workplace/z-one-company && mvn clean install -DskipTests -pl bootstraps/z-one-company-main-starter -am -q
        echo "backend build ok"
EOF
fi

if [ $DEPLOY_FRONTEND -eq 1 ]; then
    log "3. 远程构建前端..."
    ssh $SERVER << 'EOF'
        cd /home/zifang/workplace/z-one-company
        git fetch origin && git reset --hard origin/main
        cd bootstraps/z-one-company-main-starter-frontend
        npm install && npm run build
        echo "frontend build ok"
EOF
fi

# 3. 重启
log "4. 重启服务..."
ssh $SERVER << 'EOF'
    LOG_DIR="/home/zifang/workplace/z-one-company/logs"
    mkdir -p $LOG_DIR

    # 停止旧进程
    kill -9 $(lsof -ti:3000 2>/dev/null) 2>/dev/null || true
    kill -9 $(lsof -ti:8080 2>/dev/null) 2>/dev/null || true

        # 后端
        cd /home/zifang/workplace/z-one-company/bootstraps/z-one-company-main-starter
        nohup java -jar target/z-one-company-main-starter-1.0.0-SNAPSHOT.jar \
            --server.port=8080 \
            > $LOG_DIR/backend.log 2>&1 &

    sleep 8

    # 前端
    cd /home/zifang/workplace/z-one-company/bootstraps/z-one-company-main-starter-frontend
    nohup npx next start -p 3000 \
        > $LOG_DIR/frontend.log 2>&1 &

    # nginx
    sudo nginx -t && sudo nginx -s reload 2>/dev/null || \
        sudo service nginx restart 2>/dev/null || true

    echo "restart ok"
EOF

log "部署完成"
log "  前端: http://101.37.80.51:3000"
log "  后端: http://101.37.80.51:8080/doc.html"
