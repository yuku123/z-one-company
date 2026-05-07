#!/bin/bash
# ============================================================
# 远程构建脚本（在云服务器上执行）
# 用法: bash build_remote.sh
# ============================================================

set -e
APP_DIR="/home/zifang/workplace/z-one-company"
LOG_DIR="${APP_DIR}/logs"
mkdir -p $LOG_DIR

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
log()  { echo -e "${GREEN}[$(date +%H:%M:%S)] $1${NC}"; }
warn() { echo -e "${YELLOW}[WARN] $1${NC}"; }
err()  { echo -e "${RED}[ERROR] $1${NC}"; exit 1; }

log "===================== 远程构建 z-one-company ====================="

# ============================================================
# 步骤 1：拉取最新代码
# ============================================================
log "步骤1: git pull..."
cd $APP_DIR
git fetch origin
git reset --hard origin/main
log "代码同步完成 (commit: $(git rev-parse --short HEAD))"

# ============================================================
# 步骤 2：构建后端
# ============================================================
log "步骤2: 构建后端 (mvn clean install -pl bootstraps/z-one-company-main-starter -am)..."
cd $APP_DIR
mvn clean install -DskipTests -pl bootstraps/z-one-company-main-starter -am

JAR="$APP_DIR/bootstraps/z-one-company-main-starter/target/z-one-company-main-starter-1.0.0-SNAPSHOT.jar"
if [ ! -f "$JAR" ]; then
    err "后端构建失败，JAR 不存在"
fi
log "后端构建完成: $(du -sh $JAR | cut -f1)"

# ============================================================
# 步骤 3：构建前端
# ============================================================
log "步骤3: 构建前端..."
cd $APP_DIR/bootstraps/z-one-company-main-starter-frontend

if [ ! -f ".env" ]; then
    warn ".env 不存在，跳过"
fi

npm install
npm run build

if [ ! -d "dist" ]; then
    err "前端构建失败，dist 目录不存在"
fi
log "前端构建完成: $(du -sh dist | cut -f1)"

# ============================================================
# 步骤 4：重启服务
# ============================================================
log "步骤4: 重启服务..."

# 停止旧进程
log "停止旧进程..."
kill -9 $(lsof -ti:3000 2>/dev/null) 2>/dev/null || true
kill -9 $(lsof -ti:8080 2>/dev/null) 2>/dev/null || true
sleep 2

# 启动后端
log "启动后端 (8080)..."
cd $APP_DIR/bootstraps/z-one-company-main-starter
nohup java -jar target/z-one-company-main-starter-1.0.0-SNAPSHOT.jar \
    --server.port=8080 \
    > $LOG_DIR/backend.log 2>&1 &

BACKEND_PID=$!
log "后端 PID: $BACKEND_PID"

# 等待后端启动
sleep 10

# 检查后端
if kill -0 $BACKEND_PID 2>/dev/null; then
    log "后端启动成功"
else
    err "后端启动失败，查看日志: tail -50 $LOG_DIR/backend.log"
fi

# 启动前端
log "启动前端 (3000)..."
cd $APP_DIR/bootstraps/z-one-company-main-starter-frontend
nohup npx next start -p 3000 \
    > $LOG_DIR/frontend.log 2>&1 &

FRONTEND_PID=$!
log "前端 PID: $FRONTEND_PID"

sleep 5

if kill -0 $FRONTEND_PID 2>/dev/null; then
    log "前端启动成功"
else
    warn "前端可能启动失败，查看日志: tail -30 $LOG_DIR/frontend.log"
fi

# nginx
log "重载 nginx..."
sudo nginx -t && sudo nginx -s reload 2>/dev/null || \
    sudo service nginx restart 2>/dev/null || \
    warn "nginx 重启失败，请手动检查"

# ============================================================
# 步骤 5：验证
# ============================================================
log "步骤5: 验证..."
sleep 3

HTTP_BACKEND=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8080/doc.html 2>/dev/null || echo "000")
if [ "$HTTP_BACKEND" = "200" ]; then
    log "后端 API: OK (HTTP $HTTP_BACKEND)"
else
    warn "后端异常 (HTTP $HTTP_BACKEND)"
fi

HTTP_FRONTEND=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:3000 2>/dev/null || echo "000")
if [ "$HTTP_FRONTEND" = "200" ]; then
    log "前端页面: OK (HTTP $HTTP_FRONTEND)"
else
    warn "前端异常 (HTTP $HTTP_FRONTEND)"
fi

log "===================== 构建完成 ====================="
log "前端: http://101.37.80.51:3000"
log "后端: http://101.37.80.51:8080/doc.html"
log ""
log "日志位置: $LOG_DIR/"
