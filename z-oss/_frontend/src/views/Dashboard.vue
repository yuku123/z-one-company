<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #409eff">
            <el-icon><Folder /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.bucketCount }}</div>
            <div class="stat-label">存储桶数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #67c23a">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.objectCount }}</div>
            <div class="stat-label">对象数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #e6a23c">
            <el-icon><Files /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ formatSize(stats.totalSize) }}</div>
            <div class="stat-label">总存储量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #f56c6c">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ username }}</div>
            <div class="stat-label">当前用户</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近操作</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(item, index) in activities"
              :key="index"
              :timestamp="item.timestamp"
              placement="top"
            >
              {{ item.content }}
            </el-timeline-item>
          </el-timeline>
          <el-empty v-if="activities.length === 0" description="暂无操作记录" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快速操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/buckets')">
              <el-icon><Plus /></el-icon>
              创建存储桶
            </el-button>
            <el-button type="success" @click="quickUpload">
              <el-icon><Upload /></el-icon>
              上传文件
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { bucketApi, objectApi } from '../api'

const username = ref(localStorage.getItem('username') || 'User')

const stats = reactive({
  bucketCount: 0,
  objectCount: 0,
  totalSize: 0
})

const activities = ref([])

const formatSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const loadStats = async () => {
  try {
    const buckets = await bucketApi.list()
    stats.bucketCount = buckets.length || 0

    let totalObjects = 0
    let totalSize = 0

    for (const bucket of buckets) {
      try {
        const bucketStats = await bucketApi.getStats(bucket.name)
        totalObjects += bucketStats.objectCount || 0
        totalSize += bucketStats.totalSize || 0
      } catch {}
    }

    stats.objectCount = totalObjects
    stats.totalSize = totalSize
  } catch (error) {
    console.error('加载统计信息失败:', error)
  }
}

const quickUpload = () => {
  // 跳转到存储桶列表选择上传位置
  window.location.href = '/buckets'
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  margin-right: 15px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}

.card-header {
  font-weight: bold;
}

.quick-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.quick-actions .el-button {
  margin: 5px;
}
</style>