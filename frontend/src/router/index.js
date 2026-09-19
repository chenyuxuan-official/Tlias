import { createRouter, createWebHistory } from 'vue-router'
import LayoutView from '@/views/layout/index.vue'

// 路由表：一个对象就是一条路由规则（路径 → 组件）
const routes = [
  // ===== 布局路由：所有后台页面都是它的子路由，共享顶栏 / 菜单 / 页脚 =====
  {
    path: '/',
    component: LayoutView,
    redirect: '/index',   // 访问根路径时，重定向到首页
    children: [
      // 子路由的 path 不带 "/"，是相对父路径的（'emp' → /emp）
      {
        path: 'index',
        name: 'index',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'clazz',
        name: 'clazz',
        component: () => import('@/views/clazz/index.vue'),
        meta: { title: '班级管理' }
      },
      {
        path: 'stu',
        name: 'stu',
        component: () => import('@/views/stu/index.vue'),
        meta: { title: '学员管理' }
      },
      {
        path: 'dept',
        name: 'dept',
        component: () => import('@/views/dept/index.vue'),
        meta: { title: '部门管理' }
      },
      {
        path: 'emp',
        name: 'emp',
        component: () => import('@/views/emp/index.vue'),
        meta: { title: '员工管理' }
      },
      {
        path: 'log',
        name: 'log',
        component: () => import('@/views/log/index.vue'),
        meta: { title: '日志管理' }
      },
      {
        path: 'empReport',
        name: 'empReport',
        component: () => import('@/views/report/emp/index.vue'),
        meta: { title: '员工信息统计' }
      },
      {
        path: 'stuReport',
        name: 'stuReport',
        component: () => import('@/views/report/stu/index.vue'),
        meta: { title: '学员信息统计' }
      },

      // 404 兜底：必须放在 children 的最后
      // 写在 children 里，404 页面也会套上布局，用户还能点菜单回去
      {
        path: ':pathMatch(.*)*',
        name: 'notFound',
        component: () => import('@/views/Placeholder.vue'),
        meta: { title: '404', desc: '页面不存在，请检查地址是否正确' }
      }
    ]
  },

  // ===== 不需要布局的页面：与布局路由平级，不会套上顶栏和菜单 =====
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/index.vue')
  }
]

// 创建路由器实例
const router = createRouter({
  // createWebHistory()：URL 形如 /emp（部署时服务器需配置 fallback）
  // createWebHashHistory()：URL 形如 /#/emp（不会出现刷新 404，部署省心）
  history: createWebHistory(),
  routes
})

// 全局前置守卫：没登录不让进后台
router.beforeEach((to) => {
  const token = localStorage.getItem('token')

  // 去登录页：已经登录过就不必再登一次，直接进后台
  if (to.path === '/login') {
    return token ? '/' : true
  }

  // 去其他页面：没有 token 一律送去登录页
  if (!token) {
    return { path: '/login' }
  }

  return true
})

export default router
