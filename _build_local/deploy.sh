# ============================================================
# z-one-company 远程部署脚本
# 流程：本地 push → 远程 pull → 构建 → 重启服务
# ============================================================

SERVER="zifang@101.37.80.51"
REMOTE_DIR="/home/zifang/workplace/z-one-company"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'
log()  { echo -e "${GREEN}[$(date +%H:%M:%S)] $1${NC}"; }
warn() { echo -e "${YELLOW}[WARN] $1${NC}"; }
err()  { echo -e "${RED}[ERROR] $1${NC}"; exit 1; }

# ============================================================
# 检查
# ============================================================
check() {
    log "检查环境..."
    if ! ssh -o ConnectTimeout=5 $SERVER "echo ok" >/dev/null 2>&1; then
        err "无法连接 $SERVER"
    fi

    # 确保远程是 git 仓库且能 pull
    if ! ssh $SERVER "cd $REMOTE_DIR && git remote -v" >/dev/null 2>&1; then
        err "$REMOTE_DIR 不是 git 仓库或不存在"
    fi

    # 确保本地也是 git 仓库
    if [ ! -d ".git" ]; then
        err "本地不是 git 仓库"
    fi

    log "检查通过"
}

# ============================================================
# 步骤 1：本地 push 到远程
# ============================================================
push() {
    log "步骤1: 本地 push 到 origin main..."
    CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
    if [ "$CURRENT_BRANCH" != "main" ]; then
        warn "当前分支是 $CURRENT_BRANCH，将强制推送到远程 main"
    fi

    git push origin main:main
    log "push 完成"
}

# ============================================================
# 步骤 2：远程 pull + 构建后端
# ============================================================
build_backend() {
    log "步骤2: 远程构建后端..."
    ssh $SERVER << 'EOF'
        cd /home/zifang/workplace/z-one-company
        git fetch origin
        git reset --hard origin/main

        echo ">>> 构建后端..."
        cd /home/zifang/workplace/z-one-company
        mvn clean install -DskipTests -pl bootstraps/z-one-company-main-starter -am -q

        JAR="target/z-one-company-main-starter-1.0.0-SNAPSHOT.jar"
        if [ ! -f "$JAR" ]; then
            echo "ERROR: 后端构建失败，JAR 不存在"
            exit 1
        fi
        echo ">>> 后端构建完成: $JAR"
EOF
}

# ============================================================
# 步骤 3：远程构建前端
# ============================================================
build_frontend() {
    log "步骤3: 远程构建前端..."
    ssh $SERVER << 'EOF'
        cd /home/zifang/workplace/z-one-company
        git fetch origin
        git reset --hard origin/main

        echo ">>> 构建前端..."
        cd bootstraps/z-one-company-main-starter-frontend

        # 检查 .env 是否存在
        if [ ! -f ".env" ]; then
            echo "WARN: .env 不存在，跳过（将在启动时使用默认配置）"
        fi

        npm install
        npm run build

        if [ ! -d "dist" ]; then
            echo "ERROR: 前端构建失败，dist 目录不存在"
            exit 1
        fi
        echo ">>> 前端构建完成"
EOF
}

# ============================================================
# 步骤 4：重启服务（停止旧进程 + 启动新进程）
# ============================================================
restart_services() {
    log "步骤4: 重启远程服务..."

    ssh $SERVER << 'EOF'
        set -e
        APP_DIR="/home/zifang/workplace/z-one-company"
        LOG_DIR="${APP_DIR}/logs"
        mkdir -p $LOG_DIR

        # 停止旧进程
        echo ">>> 停止旧进程..."
        kill -9 $(lsof -ti:3000 2>/dev/null) 2>/dev/null || true
        kill -9 $(lsof -ti:8080 2>/dev/null) 2>/dev/null || true
        echo ">>> 旧进程已清理"

        # 启动后端（端口 8080）
        echo ">>> 启动后端..."
        cd $APP_DIR/bootstraps/z-one-company-main-starter
        nohup java -jar target/z-one-company-main-starter-1.0.0-SNAPSHOT.jar \
            --server.port=8080 \
            > $LOG_DIR/backend.log 2>&1 &

        BACKEND_PID=$!
        echo ">>> 后端启动 (PID: $BACKEND_PID)"

        # 等待后端启动
        sleep 8

        # 检查后端是否存活
        if kill -0 $BACKEND_PID 2>/dev/null; then
            echo ">>> 后端运行正常 (PID: $BACKEND_PID)"
        else
            echo "ERROR: 后端启动失败，查看日志:"
            tail -50 $LOG_DIR/backend.log
            exit 1
        fi

        # 启动前端（Next.js 端口 3000）
        echo ">>> 启动前端..."
        cd $APP_DIR/bootstraps/z-one-company-main-starter-frontend
        nohup npx next start -p 3000 \
            > $LOG_DIR/frontend.log 2>&1 &

        FRONTEND_PID=$!
        echo ">>> 前端启动 (PID: $FRONTEND_PID)"

        # 等待前端启动
        sleep 5

        if kill -0 $FRONTEND_PID 2>/dev/null; then
            echo ">>> 前端运行正常 (PID: $FRONTEND_PID)"
        else
            echo "WARN: 前端启动可能失败，查看日志:"
            tail -30 $LOG_DIR/frontend.log
        fi

        # 重载 nginx
        echo ">>> 重载 nginx..."
        sudo nginx -t && sudo nginx -s reload 2>/dev/null || \
            sudo service nginx restart 2>/dev/null || \
            echo "WARN: nginx 重启失败，请手动检查"

        echo ">>> 服务重启完成"
EOF
}

# ============================================================
# 步骤 5：验证
# ============================================================
verify() {
    log "步骤5: 验证部署..."
    sleep 3

    HTTP_BACKEND=$(curl -s -o /dev/null -w "%{http_code}" http://101.37.80.51:8080/doc.html 2>/dev/null || echo "000")
    if [ "$HTTP_BACKEND" = "200" ]; then
        log "后端 API: OK (HTTP $HTTP_BACKEND)"
    else
        warn "后端异常 (HTTP $HTTP_BACKEND)，日志: ssh $SERVER tail -50 /home/zifang/workplace/z-one-company/logs/backend.log"
    fi

    HTTP_FRONTEND=$(curl -s -o /dev/null -w "%{http_code}" http://101.37.80.51:3000 2>/dev/null || echo "000")
    if [ "$HTTP_FRONTEND" = "200" ]; then
        log "前端页面: OK (HTTP $HTTP_FRONTEND)"
    else
        warn "前端异常 (HTTP $HTTP_FRONTEND)，日志: ssh $SERVER tail -50 /home/zifang/workplace/z-one-company/logs/frontend.log"
    fi
}

# ============================================================
# 主流程
# ============================================================
main() {
    log "===================== 开始部署 z-one-company ====================="
    log "远程: $SERVER:$REMOTE_DIR"

    check

    DEPLOY_BACKEND=1
    DEPLOY_FRONTEND=1
    SKIP_VERIFY=0

    for arg in "$@"; do
        case $arg in
            --backend-only)    DEPLOY_FRONTEND=0 ;;
            --frontend-only)  DEPLOY_BACKEND=0 ;;
            --no-verify)      SKIP_VERIFY=1 ;;
            --help)
                echo "用法: $0 [选项]"
                echo "  --backend-only    仅部署后端"
                echo "  --frontend-only   仅部署前端"
                echo "  --no-verify       跳过验证步骤"
                echo "  --help            显示帮助"
                exit 0
                ;;
        esac
    done

    push

    if [ $DEPLOY_BACKEND -eq 1 ]; then
        build_backend
    fi

    if [ $DEPLOY_FRONTEND -eq 1 ]; then
        build_frontend
    fi

    restart_services

    if [ $SKIP_VERIFY -eq 0 ]; then
        verify
    fi

    log "===================== 部署完成 ====================="
    log "访问地址："
    log "  前端: http://101.37.80.51:3000"
    log "  后端: http://101.37.80.51:8080/doc.html"
}

main "$@"
