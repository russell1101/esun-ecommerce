import request from './request'
import { API } from '@/config'

export const memberRegister = (data) => request.post(API.MEMBER_REGISTER, data)
export const memberLogin    = (data) => request.post(API.MEMBER_LOGIN, data)
export const adminLogin     = (data) => request.post(API.ADMIN_LOGIN, data)
