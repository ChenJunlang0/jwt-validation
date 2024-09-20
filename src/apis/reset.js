import request from '@/utils/http'

export const resetApi=(email,code,password)=>{
    return request({
        url:'/api/auth/reset',
        method:'POST',
        data:{
            email,
            code,
            password
        }
    })
}