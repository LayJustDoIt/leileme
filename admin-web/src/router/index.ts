import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import AdminLayout from '@/layouts/AdminLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: AdminLayout,
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '数据概览' }
      },
      {
        path: 'contents',
        name: 'ContentList',
        component: () => import('@/views/ContentList.vue'),
        meta: { title: '内容列表' }
      },
      {
        path: 'contents/new',
        name: 'ContentCreate',
        component: () => import('@/views/ContentEdit.vue'),
        meta: { title: '新建内容' }
      },
      {
        path: 'contents/:id/edit',
        name: 'ContentEdit',
        component: () => import('@/views/ContentEdit.vue'),
        meta: { title: '编辑内容' }
      },
      {
        path: 'categories',
        name: 'CategoryList',
        component: () => import('@/views/CategoryList.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'tags',
        name: 'TagList',
        component: () => import('@/views/TagList.vue'),
        meta: { title: '标签管理' }
      },
      {
        path: 'recommendations',
        name: 'RecommendationList',
        component: () => import('@/views/RecommendationList.vue'),
        meta: { title: '推荐位管理' }
      },
      {
        path: 'hot-keywords',
        name: 'HotKeywordList',
        component: () => import('@/views/HotKeywordList.vue'),
        meta: { title: '热门搜索词' }
      },
      {
        path: 'public-config',
        name: 'PublicConfig',
        component: () => import('@/views/PublicConfig.vue'),
        meta: { title: '公共配置' }
      },
      {
        path: 'feedbacks',
        name: 'FeedbackList',
        component: () => import('@/views/FeedbackList.vue'),
        meta: { title: '反馈管理' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳转 /login
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('leileme_admin_token')
  if (to.meta.requiresAuth === false) {
    // 已登录访问登录页则跳转首页
    if (to.path === '/login' && token) {
      next('/dashboard')
      return
    }
    next()
    return
  }
  if (!token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  next()
})

export default router
