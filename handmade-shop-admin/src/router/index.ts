import {createRouter, createWebHashHistory, createWebHistory} from 'vue-router'

import { useHomeStore } from '@/stores/home-store'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'layout-view',
      component: () => import('@/layout/layout-view.vue'),
      children: [
        { path: '/user', component: () => import('@/views/user/user-view.vue') },
        {
          path: '/role',
          name: 'role',
          component: () => import('@/views/role/role-view.vue')
        },
        {
          path: '/menu',
          name: 'menu',
          component: () => import('@/views/menu/menu-view.vue')
        },
        {
          path: '/dict',
          name: 'dict',
          component: () => import('@/views/dict/dict-view.vue')
        },
        {
          path: '/product',
          component: () => import('@/views/product/product-view.vue')
        },
        {
          path: '/product-details',
          component: () => import('@/views/product/product-details-view.vue'),
          props(to) {
            return { id: to.query.id }
          }
        },
        {
          path: '/product-category',
          component: () => import('@/views/product-category/product-category-view.vue')
        },
        {
          path: '/product-category-details',
          component: () => import('@/views/product-category/product-category-details-view.vue'),
          props(to) {
            return { id: to.query.id }
          }
        },
        {
          path: '/slideshow',
          component: () => import('@/views/slideshow/slideshow-view.vue')
        },
        {
          path: '/slideshow-details',
          component: () => import('@/views/slideshow/slideshow-details-view.vue'),
          props(to) {
            return { id: to.query.id }
          }
        },
        {
          path: '/handicraft',
          component: () => import('@/views/handicraft/handicraft-view.vue')
        },
        {
          path: '/handicraft-details',
          component: () => import('@/views/handicraft/handicraft-details-view.vue'),
          props(to) {
            return { id: to.query.id }
          }
        },
        {
          path: '/handicraft-booking',
          component: () => import('@/views/handicraft-booking/handicraft-booking-view.vue')
        },
        {
          path: '/handicraft-booking-details',
          component: () => import('@/views/handicraft-booking/handicraft-booking-details-view.vue'),
          props(to) {
            return { id: to.query.id }
          }
        },
        {
          path: '/handicraft-category',
          component: () => import('@/views/handicraft-category/handicraft-category-view.vue')
        },
        {
          path: '/handicraft-category-details',
          component: () =>
            import('@/views/handicraft-category/handicraft-category-details-view.vue'),
          props(to) {
            return { id: to.query.id }
          }
        },
        {
          path: '/feedback',
          component: () => import('@/views/feedback/feedback-view.vue')
        },
        {
          path: '/product-order',
          component: () => import('@/views/product-order/product-order-view.vue')
        },
        {
          path: '/handicraft-order',
          component: () => import('@/views/handicraft-order/handicraft-order-view.vue')
        },
        {
          path: '/vip-level',
          component: () => import('@/views/vip-level/vip-level-view.vue')
        },
        {
          path: '/vip-level-details',
          component: () => import('@/views/vip-level/vip-level-details-view.vue'),
          props(to) {
            return { id: to.query.id }
          }
        },
        {
          path: '/vip',
          component: () => import('@/views/vip/vip-view.vue')
        },
        {
          path: '/vip-config',
          component: () => import('@/views/vip/vip-config-view.vue')
        },

        {
          path: '/vip-details',
          component: () => import('@/views/vip/vip-details-view.vue'),
          props(to) {
            return { id: to.query.id }
          }
        },
        {
          path: '/vip-order',
          component: () => import('@/views/vip-order/vip-order-view.vue')
        },
        {
          path: '/product-carriage-template',
          component: () =>
            import('@/views/product-carriage-template/product-carriage-template-view.vue')
        },
        {
          path: '/product-carriage-template-details',
          component: () =>
            import('@/views/product-carriage-template/product-carriage-template-details-view.vue'),
          props(to) {
            return { id: to.query.id }
          }
        }
      ]
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/login/login-view.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/login/register-view.vue')
    },
    {
      path: '/rest-password',
      component: () => import('@/views/login/rest-password-view.vue')
    }
  ]
})
// 添加路由拦截，在进入路由之前需要校验是否有该菜单的权限
// eslint-disable-next-line no-sparse-arrays
const whiteList = ['/login', '/register', '/rest-password', '/']
router.beforeEach(async (to, from, next) => {
  const homeStore = useHomeStore()
  if (
    whiteList.includes(to.path) ||
    (await homeStore.getMenuList()).findIndex((menu) => menu.path === to.path) >= 0
  ) {
    next()
  } else {
    return next('/')
  }
})
export default router
