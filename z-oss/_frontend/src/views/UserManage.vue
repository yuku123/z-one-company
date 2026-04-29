<template>
  <div class="user-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户信息</span>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
        <el-descriptions-item label="Access Key">
          <el-tag>{{ userInfo.accessKey }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="Secret Key">
          <el-tag type="warning">{{ userInfo.secretKey }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'">
            {{ userInfo.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ formatTime(userInfo.createTime) }}
        </el-descriptions-item>
      </el-descriptions>

      <div class="action-buttons">
        <el-button type="primary" @click="showEditUsername = true">
          <el-icon><Edit /></el-icon>
          修改用户名
        </el-button>
        <el-button type="warning" @click="showResetKey = true">
          <el-icon><Refresh /></el-icon>
          重置AK/SK
        </el-button>
        <el-button type="danger" @click="showChangePassword = true">
          <el-icon><Lock /></el-icon>
          修改密码
        </el-button>
      </div>
    </el-card>

    <!-- 密钥信息说明 -->
    <el-card style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>API密钥说明</span>
        </div>
      </template>
      <el-alert
        title="如何访问API"
        type="info"
        :closable="false"
      >
        <template #default>
          <p>使用以下HTTP头来访问API：</p>
          <ul>
            <li><code>X-Zoss-Access-Key</code>: 您的Access Key</li>
            <li><code>X-Zoss-Secret-Key</code>: 您的Secret Key</li>
          </ul>
          <p>示例：</p>
          <pre>curl -H "X-Zoss-Access-Key: {{ userInfo.accessKey }}" \
     -H "X-Zoss-Secret-Key: {{ userInfo.secretKey }}" \
     http://localhost:8088/api/v1/bucket</pre>
        </template>
      </el-alert>
    </el-card>

    <!-- 修改用户名对话框 -->
    <el-dialog v-model="showEditUsername" title="修改用户名" width="400px">
      <el-form :model="usernameForm" :rules="usernameRules" ref="usernameFormRef" label-width="80px">
        <el-form-item label="新用户名" prop="username">
          <el-input v-model="usernameForm.username" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditUsername = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleUpdateUsername">保存</el-button>
      </template>
    </el-dialog>

    <!-- 重置AK/SK对话框 -->
    <el-dialog v-model="showResetKey" title="重置AK/SK" width="500px">
      <el-alert
        title="警告"
        type="warning"
        description="重置AK/SK后，旧的密钥将失效，请妥善保管新密钥。"
        :closable="false"
        style="margin-bottom: 20px"
      />
      <div v-if="newKeys">
        <el-form label-width="100px">
          <el-form-item label="Access Key">
            <el-input v-model="newKeys.accessKey" readonly>
              <template #append>
                <el-button @click="copyToClipboard(newKeys.accessKey)">复制</el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="Secret Key">
            <el-input v-model="newKeys.secretKey" readonly>
              <template #append>
                <el-button @click="copyToClipboard(newKeys.secretKey)">复制</el-button>
              </template>
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="showResetKey = false">关闭</el-button>
        <el-button type="primary" :loading="loading" @click="handleResetKeys">
          {{ newKeys ? '重新生成' : '重置密钥' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="showChangePassword" title="修改密码" width="400px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="80px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showChangePassword = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleChangePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi } from '../api'

const userInfo = reactive({
  username: '',
  accessKey: '',
  secretKey: '',
  status: 1,
  createTime: null
})

const loading = ref(false)
const showEditUsername = ref(false)
const showResetKey = ref(false)
const showChangePassword = ref(false)
const newKeys = ref(null)

const usernameForm = reactive({ username: '' })
const passwordForm = reactive({ oldPassword: '', newPassword: '' })

const usernameRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

const usernameFormRef = ref(null)
const passwordFormRef = ref(null)

const loadUserInfo = async () => {
  try {
    const res = await userApi.getInfo()
    Object.assign(userInfo, res)
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

const handleUpdateUsername = async () => {
  try {
    await usernameFormRef.value.validate()
    loading.value = true

    await userApi.updateInfo({ username: usernameForm.username })
    ElMessage.success('用户名修改成功')
    userInfo.username = usernameForm.username
    localStorage.setItem('username', usernameForm.username)
    showEditUsername.value = false
  } catch (error) {
    console.error('修改用户名失败:', error)
  } finally {
    loading.value = false
  }
}

const handleResetKeys = async () => {
  try {
    loading.value = true

    const res = await userApi.resetKeys()
    newKeys.value = res

    // 更新本地存储
    localStorage.setItem('accessKey', res.accessKey)
    localStorage.setItem('secretKey', res.secretKey)
    userInfo.accessKey = res.accessKey
    userInfo.secretKey = res.secretKey

    ElMessage.success('AK/SK已重置，请妥善保管')
  } catch (error) {
    console.error('重置密钥失败:', error)
  } finally {
    loading.value = false
  }
}

const handleChangePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    loading.value = true

    await userApi.changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })

    ElMessage.success('密码修改成功')
    showChangePassword.value = false
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
  } catch (error) {
    console.error('修改密码失败:', error)
  } finally {
    loading.value = false
  }
}

const copyToClipboard = (text) => {
  navigator.clipboard.writeText(text)
  ElMessage.success('已复制到剪贴板')
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.card-header {
  font-weight: bold;
}

.action-buttons {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

pre {
  background: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
}

code {
  font-family: monospace;
  background: #f5f7fa;
  padding: 2px 5px;
  border-radius: 3px;
}
</style>