import request from '@/utils/http'

export const logoutApi=()=>{
    return request({
        url:'/api/auth/logout'
    })
}