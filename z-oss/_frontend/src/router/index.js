import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Layout from '../views/Layout.vue'
import Dashboard from '../views/Dashboard.vue'
import BucketList from '../views/BucketList.vue'
import ObjectList from '../views/ObjectList.vue'
import UserManage from '../views/UserManage.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard
      },
      {
        path: 'buckets',
        name: 'BucketList',
        component: BucketList
      },
      {
        path: 'objects/:bucketName',
        name: 'ObjectList',
        component: ObjectList
      },
      {
        path: 'users',
        name: 'UserManage',
        component: UserManage
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const accessKey = localStorage.getItem('accessKey')
  if (to.path !== '/login' && !accessKey) {
    next('/login')
  } else if (to.path === '/login' && accessKey) {
    next('/')
  } else {
    next()
  }
})

export default router