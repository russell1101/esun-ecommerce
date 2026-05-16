export const API_BASE_URL = process.env.VUE_APP_API_BASE_URL ?? ''

export const API = {
  MEMBER_REGISTER:     '/api/front/auth/register',
  MEMBER_LOGIN:        '/api/front/auth/login',
  ADMIN_LOGIN:         '/api/admin/auth/login',
  PRODUCTS_AVAILABLE:  '/api/front/products/available',
  ADMIN_PRODUCTS:      '/api/admin/products',
  ADMIN_PRODUCT_BY_ID: (id) => `/api/admin/products/${id}`,
  ORDERS_CREATE:       '/api/front/orders',
  ORDERS_ME:           '/api/front/orders/me',
  ADMIN_ORDERS:        '/api/admin/orders',
  ADMIN_ORDER_DETAILS: (orderId) => `/api/admin/orders/${orderId}/details`,
}
