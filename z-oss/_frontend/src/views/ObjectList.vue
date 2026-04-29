<template>
  <div class="object-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-button @click="$router.back()">
              <el-icon><ArrowLeft /></el-icon>
              返回
            </el-button>
            <span class="bucket-name">{{ bucketName }} / {{ currentPrefix || '根目录' }}</span>
          </div>
          <div class="header-right">
            <el-button type="primary" @click="showCreateFolder = true">
              <el-icon><FolderAdd /></el-icon>
              新建文件夹
            </el-button>
            <el-upload
              :action="uploadUrl"
              :headers="uploadHeaders"
              :data="uploadData"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              multiple
            >
              <el-button type="success">
                <el-icon><Upload /></el-icon>
                上传文件
              </el-button>
            </el-upload>
          </div>
        </div>
      </template>

      <!-- 面包屑导航 -->
      <div class="breadcrumb">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>
            <a href="javascript:void(0)" @click="goToPrefix('')">桶根目录</a>
          </el-breadcrumb-item>
          <el-breadcrumb-item v-for="(item, index) in prefixList" :key="index">
            <a href="javascript:void(0)" @click="goToPrefix(item.path)">{{ item.name }}</a>
          </el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <!-- 文件列表 -->
      <el-table
        :data="objects"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        stripe
      >
        <el-table-column type="selection" width="40" />
        <el-table-column label="名称" min-width="300">
          <template #default="{ row }">
            <div class="object-name" @click="handleObjectClick(row)">
              <el-icon v-if="row.folder" class="folder-icon"><Folder /></el-icon>
              <el-icon v-else class="file-icon"><Document /></el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="size" label="大小" width="120">
          <template #default="{ row }">
            {{ row.folder ? '-' : formatSize(row.size) }}
          </template>
        </el-table-column>
        <el-table-column prop="contentType" label="类型" width="150" />
        <el-table-column prop="lastModified" label="修改时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.lastModified) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="!row.folder" type="primary" link @click="handleDownload(row)">
              <el-icon><Download /></el-icon>
              下载
            </el-button>
            <el-button type="warning" link @click="handleCopy(row)">
              <el-icon><CopyDocument /></el-icon>
              复制
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 批量操作 -->
      <div class="batch-actions" v-if="selectedObjects.length > 0">
        <el-button type="danger" @click="handleBatchDelete">
          批量删除 ({{ selectedObjects.length }})
        </el-button>
      </div>
    </el-card>

    <!-- 新建文件夹对话框 -->
    <el-dialog v-model="showCreateFolder" title="新建文件夹" width="400px">
      <el-form :model="folderForm" ref="folderFormRef" label-width="80px">
        <el-form-item label="文件夹名" prop="name">
          <el-input v-model="folderForm.name" placeholder="请输入文件夹名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateFolder = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleCreateFolder">创建</el-button>
      </template>
    </el-dialog>

    <!-- 复制对话框 -->
    <el-dialog v-model="showCopyDialog" title="复制对象" width="500px">
      <el-form :model="copyForm" :rules="copyRules" ref="copyFormRef" label-width="80px">
        <el-form-item label="目标桶" prop="destBucketName">
          <el-select v-model="copyForm.destBucketName" placeholder="选择目标桶">
            <el-option v-for="b in buckets" :key="b.name" :label="b.name" :value="b.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标路径" prop="destObjectKey">
          <el-input v-model="copyForm.destObjectKey" placeholder="请输入目标路径" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCopyDialog = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleCopySubmit">复制</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { objectApi, bucketApi } from '../api'

const route = useRoute()
const router = useRouter()

const bucketName = computed(() => route.params.bucketName)
const currentPrefix = ref('')
const objects = ref([])
const buckets = ref([])
const loading = ref(false)
const selectedObjects = ref([])

const showCreateFolder = ref(false)
const showCopyDialog = ref(false)
const folderForm = reactive({ name: '' })
const copyForm = reactive({
  sourceObjectKey: '',
  destBucketName: '',
  destObjectKey: ''
})

const copyRules = {
  destBucketName: [{ required: true, message: '请选择目标桶', trigger: 'change' }],
  destObjectKey: [{ required: true, message: '请输入目标路径', trigger: 'blur' }]
}

const folderFormRef = ref(null)
const copyFormRef = ref(null)

// 计算面包屑
const prefixList = computed(() => {
  if (!currentPrefix.value) return []
  const parts = currentPrefix.value.split('/').filter(Boolean)
  let path = ''
  return parts.map(part => {
    path += part + '/'
    return { name: part, path }
  })
})

// 上传相关
const uploadUrl = computed(() => `/api/v1/object/${bucketName.value}/`)
const uploadHeaders = computed(() => ({
  'X-Zoss-Access-Key': localStorage.getItem('accessKey') || '',
  'X-Zoss-Secret-Key': localStorage.getItem('secretKey') || ''
}))
const uploadData = computed(() => ({
  prefix: currentPrefix.value
}))

const loadObjects = async () => {
  try {
    loading.value = true
    objects.value = await objectApi.list(bucketName.value, currentPrefix.value)
  } catch (error) {
    console.error('加载对象失败:', error)
  } finally {
    loading.value = false
  }
}

const loadBuckets = async () => {
  try {
    buckets.value = await bucketApi.list()
  } catch (error) {
    console.error('加载桶列表失败:', error)
  }
}

const handleObjectClick = (row) => {
  if (row.folder) {
    // 进入文件夹
    currentPrefix.value = row.key
    loadObjects()
  } else {
    // 预览或下载
    handleDownload(row)
  }
}

const goToPrefix = (prefix) => {
  currentPrefix.value = prefix
  loadObjects()
}

const handleUploadSuccess = () => {
  ElMessage.success('上传成功')
  loadObjects()
}

const handleUploadError = (error) => {
  ElMessage.error('上传失败: ' + error.message)
}

const handleCreateFolder = async () => {
  if (!folderForm.name) {
    ElMessage.warning('请输入文件夹名称')
    return
  }

  try {
    loading.value = true
    const folderKey = currentPrefix.value + folderForm.name + '/'
    await objectApi.createFolder(bucketName.value, folderKey)
    ElMessage.success('创建成功')
    showCreateFolder.value = false
    folderForm.name = ''
    loadObjects()
  } catch (error) {
    console.error('创建文件夹失败:', error)
  } finally {
    loading.value = false
  }
}

const handleDownload = async (row) => {
  try {
    const response = await objectApi.download(bucketName.value, row.key)
    const url = window.URL.createObjectURL(new Blob([response]))
    const link = document.createElement('a')
    link.href = url
    link.download = row.name
    link.click()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    ElMessage.error('下载失败')
  }
}

const handleCopy = (row) => {
  copyForm.sourceObjectKey = row.key
  copyForm.destBucketName = bucketName.value
  copyForm.destObjectKey = row.key
  showCopyDialog.value = true
}

const handleCopySubmit = async () => {
  try {
    await copyFormRef.value.validate()
    loading.value = true

    await objectApi.copy(bucketName.value, copyForm.sourceObjectKey, {
      destBucketName: copyForm.destBucketName,
      destObjectKey: copyForm.destObjectKey
    })

    ElMessage.success('复制成功')
    showCopyDialog.value = false
  } catch (error) {
    console.error('复制失败:', error)
  } finally {
    loading.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除 "${row.name}" 吗？`, '警告', {
      type: 'warning'
    })

    await objectApi.delete(bucketName.value, row.key)
    ElMessage.success('删除成功')
    loadObjects()
  } catch {}
}

const handleSelectionChange = (selection) => {
  selectedObjects.value = selection
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedObjects.value.length} 个对象吗？`, '警告', {
      type: 'warning'
    })

    const objectKeys = selectedObjects.value.map(o => o.key)
    await objectApi.batchDelete(bucketName.value, objectKeys)
    ElMessage.success('批量删除成功')
    loadObjects()
  } catch {}
}

const formatSize = (bytes) => {
  if (!bytes || bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => {
  loadObjects()
  loadBuckets()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.bucket-name {
  font-size: 16px;
  font-weight: bold;
}

.header-right {
  display: flex;
  gap: 10px;
}

.breadcrumb {
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.object-name {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.object-name:hover {
  color: #409eff;
}

.folder-icon {
  color: #e6a23c;
}

.file-icon {
  color: #909399;
}

.batch-actions {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}
</style>