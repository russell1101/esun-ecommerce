import request from './request'
import { API } from '@/config'

export const createOrder     = (data) => request.post(API.ORDERS_CREATE, data)
export const getMyOrders     = () => request.get(API.ORDERS_ME)
export const getAdminOrders  = () => request.get(API.ADMIN_ORDERS)
export const getOrderDetails = (orderId) => request.get(API.ADMIN_ORDER_DETAILS(orderId))
