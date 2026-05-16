import request from './request'
import { API } from '@/config'

export const getAvailableProducts = () => request.get(API.PRODUCTS_AVAILABLE)
export const getAdminProducts     = () => request.get(API.ADMIN_PRODUCTS)
export const createProduct        = (data) => request.post(API.ADMIN_PRODUCTS, data)
export const updateProduct        = (productId, data) => request.put(API.ADMIN_PRODUCT_BY_ID(productId), data)
