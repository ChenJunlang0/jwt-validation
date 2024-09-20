import request from '@/utils/http'

export const checkTokenApi=(token)=>{
    return request({
        url:'check/token',
        params:{
            token
        }
    })
}