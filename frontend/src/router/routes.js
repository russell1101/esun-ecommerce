export default [
  // 根路徑導向 /shop
  { path: '/', redirect: '/shop' },

  // ── 前台（Member） ──
  {
    path: '/login',
    name: 'MemberLogin',
    component: () => import('@/views/member/LoginView'),
    meta: { title: '會員登入' },
  },
  {
    path: '/register',
    name: 'MemberRegister',
    component: () => import('@/views/member/RegisterView'),
    meta: { title: '會員註冊' },
  },
  {
    path: '/shop',
    name: 'Shop',
    component: () => import('@/views/member/ShopView'),
    meta: { title: '商品列表' },
  },
  {
    path: '/orders',
    name: 'MyOrders',
    component: () => import('@/views/member/MyOrdersView'),
    meta: { title: '我的訂單', requiresMember: true },
  },

  // ── 後台（Admin） ──
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/AdminLoginView'),
    meta: { title: '管理員登入' },
  },
  {
    path: '/admin/products',
    name: 'AdminProducts',
    component: () => import('@/views/admin/ProductManage'),
    meta: { title: '商品管理', requiresAdmin: true },
  },
  {
    path: '/admin/orders',
    name: 'AdminOrders',
    component: () => import('@/views/admin/OrderManage'),
    meta: { title: '訂單管理', requiresAdmin: true },
  },

  // catch-all
  { path: '/:pathMatch(.*)*', redirect: '/shop' },
]
