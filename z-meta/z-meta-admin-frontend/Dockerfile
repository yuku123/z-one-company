# 阶段1：构建前端
FROM node:18-alpine as frontend-builder

WORKDIR /app

# 复制前端代码
COPY package.json package-lock.json ./
RUN npm install

COPY . .
RUN npm run build

# 阶段2：使用 Nginx 运行前端
FROM nginx:alpine

# 复制构建产物
COPY --from=frontend-builder /app/dist /usr/share/nginx/html

# 复制 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]