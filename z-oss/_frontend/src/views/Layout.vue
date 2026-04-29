<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="200px">
      <div class="logo">
        <h3>Z-OSS</h3>
        <p>对象存储管理</p>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/buckets">
          <el-icon><Folder /></el-icon>
          <span>存储桶</span>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 头部 -->
      <el-header>
        <div class="header-left">
          <span class="username">{{ username }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              <el-icon><User /></el-icon>
              {{ username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="info">个人信息</el-dropdown-item>
                <el-dropdown-item command="keys">AK/SK</el-dropdown-item>
                <el-dropdown-item command="password">修改密码</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>

  <!-- 个人信息对话框 -->
  <el-dialog v-model="showInfo" title="个人信息" width="500px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
      <el-descriptions-item label="Access Key">{{ userInfo.accessKey }}</el-descriptions-item>
      <el-descriptions-item label="Secret Key">{{ userInfo.secretKey }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ userInfo.status === 1 ? '启用' : '禁用' }}</el-descriptions-item>
    </el-descriptions>
  </el-dialog>

  <!-- AK/SK对话框 -->
  <el-dialog v-model="showKeys" title="重置AK/SK" width="400px">
    <el-alert
      title="警告"
      type="warning"
      description="重置AK/SK后，旧的密钥将失效，请妥善保管新密钥。"
      :closable="false"
      style="margin-bottom: 20px"
    />
    <div v-if="newKeys">
      <el-form label-width="80px">
        <el-form-item label="Access Key">
          <el-input v-model="newKeys.accessKey" readonly />
        </el-form-item>
        <el-form-item label="Secret Key">
          <el-input v-model="newKeys.secretKey" readonly />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <el-button @click="showKeys = false">关闭</el-button>
      <el-button type="primary" :loading="loading" @click="handleResetKeys">重置密钥</el-button>
    </template>
  </el-dialog>

  <!-- 修改密码对话框 -->
  <el-dialog v-model="showPassword" title="修改密码" width="400px">
    <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="80px">
      <el-form-item label="旧密码" prop="oldPassword">
        <el-input v-model="passwordForm.oldPassword" type="password" />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="passwordForm.newPassword" type="password" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="showPassword = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleChangePassword">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '../api'

const router = useRouter()
const route = useRoute()

const username = ref(localStorage.getItem('username') || 'User')
const activeMenu = computed(() => route.path)

const showInfo = ref(false)
const showKeys = ref(false)
const showPassword = ref(false)
const loading = ref(false)

const userInfo = reactive({
  username: '',
  accessKey: '',
  secretKey: '',
  status: 1
})

const newKeys = ref(null)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: ''
})

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

const passwordFormRef = ref(null)

const handleCommand = async (command) => {
  switch (command) {
    case 'info':
      await loadUserInfo()
      showInfo.value = true
      break
    case 'keys':
      newKeys.value = null
      showKeys.value = true
      break
    case 'password':
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      showPassword.value = true
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          type: 'warning'
        })
        localStorage.removeItem('accessKey')
        localStorage.removeItem('secretKey')
        localStorage.removeItem('username')
        router.push('/login')
      } catch {}
      break
  }
}

const loadUserInfo = async () => {
  try {
    const res = await userApi.getInfo()
    Object.assign(userInfo, res)
  } catch (error) {
    console.error('获取用户信息失败:', error)
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
    showPassword.value = false
  } catch (error) {
    console.error('修改密码失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
}

.logo {
  padding: 20px;
  text-align: center;
  color: #fff;
  border-bottom: 1px solid #3d4f66;
}

.logo h3 {
  margin: 0;
  font-size: 24px;
}

.logo p {
  margin: 5px 0 0 0;
  font-size: 12px;
  color: #8fa0b9;
}

.el-menu-vertical {
  border-right: none;
}

.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.username {
  font-size: 14px;
  color: #333;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
}

.user-dropdown:hover {
  background-color: #f5f7fa;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>