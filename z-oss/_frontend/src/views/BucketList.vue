<template>
  <div class="bucket-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>存储桶管理</span>
          <el-button type="primary" @click="showCreateDialog = true">
            <el-icon><Plus /></el-icon>
            创建存储桶
          </el-button>
        </div>
      </template>

      <el-table :data="buckets" v-loading="loading" stripe>
        <el-table-column prop="name" label="桶名称" min-width="150">
          <template #default="{ row }">
            <el-link type="primary" @click="goToObjects(row.name)">
              {{ row.name }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="region" label="区域" width="120" />
        <el-table-column prop="acl" label="ACL" width="100">
          <template #default="{ row }">
            <el-tag :type="getAclType(row.acl)">{{ row.acl }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="goToObjects(row.name)">
              <el-icon><FolderOpened /></el-icon>
              文件
            </el-button>
            <el-button type="warning" link @click="showEditDialog(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editBucket ? '编辑存储桶' : '创建存储桶'"
      width="500px"
    >
      <el-form :model="bucketForm" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="桶名称" prop="name" v-if="!editBucket">
          <el-input v-model="bucketForm.name" placeholder="小写字母、数字、连字符" />
        </el-form-item>
        <el-form-item label="区域" prop="region">
          <el-select v-model="bucketForm.region" placeholder="选择区域">
            <el-option label="默认区域 (default)" value="default" />
            <el-option label="华南 (cn-south)" value="cn-south" />
            <el-option label="华北 (cn-north)" value="cn-north" />
            <el-option label="华东 (cn-east)" value="cn-east" />
          </el-select>
        </el-form-item>
        <el-form-item label="ACL" prop="acl">
          <el-select v-model="bucketForm.acl" placeholder="选择ACL">
            <el-option label="私有 (private)" value="private" />
            <el-option label="公共读 (public-read)" value="public-read" />
            <el-option label="公共读写 (public-read-write)" value="public-read-write" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">
          {{ editBucket ? '保存' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { bucketApi } from '../api'

const router = useRouter()

const buckets = ref([])
const loading = ref(false)
const showCreateDialog = ref(false)
const editBucket = ref(null)

const bucketForm = reactive({
  name: '',
  region: 'default',
  acl: 'private'
})

const rules = {
  name: [
    { required: true, message: '请输入桶名称', trigger: 'blur' },
    { pattern: /^[a-z0-9][a-z0-9-]{2,62}[a-z0-9]$/, message: '桶名称格式不正确', trigger: 'blur' }
  ]
}

const formRef = ref(null)

const loadBuckets = async () => {
  try {
    loading.value = true
    buckets.value = await bucketApi.list()
  } catch (error) {
    console.error('加载存储桶失败:', error)
  } finally {
    loading.value = false
  }
}

const goToObjects = (bucketName) => {
  router.push(`/objects/${bucketName}`)
}

const showEditDialog = (bucket) => {
  editBucket.value = bucket
  bucketForm.name = bucket.name
  bucketForm.region = bucket.region
  bucketForm.acl = bucket.acl
  showCreateDialog.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    if (editBucket.value) {
      await bucketApi.update(editBucket.value.name, {
        region: bucketForm.region,
        acl: bucketForm.acl
      })
      ElMessage.success('更新成功')
    } else {
      await bucketApi.create(bucketForm)
      ElMessage.success('创建成功')
    }

    showCreateDialog.value = false
    editBucket.value = null
    bucketForm.name = ''
    bucketForm.region = 'default'
    bucketForm.acl = 'private'
    loadBuckets()
  } catch (error) {
    console.error('操作失败:', error)
  } finally {
    loading.value = false
  }
}

const handleDelete = async (bucket) => {
  try {
    await ElMessageBox.confirm(`确定要删除存储桶 "${bucket.name}" 吗？`, '警告', {
      type: 'warning'
    })

    await bucketApi.delete(bucket.name)
    ElMessage.success('删除成功')
    loadBuckets()
  } catch {}
}

const getAclType = (acl) => {
  const map = {
    'private': '',
    'public-read': 'success',
    'public-read-write': 'warning'
  }
  return map[acl] || ''
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => {
  loadBuckets()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>