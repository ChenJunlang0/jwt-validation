import request from '@/utils/http'

export const testApi=()=>{
    return request({
        url:'/test/hello'
    })
}
