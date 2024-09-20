import request from '@/utils/http'
export const registerApi=(username,password,email,code)=>{
    return request({
        url:'/api/auth/register',
        method:'POST',
        data:{
            username,
            password,
            email,
            code
        }
    })
}
export const askCodeApi=(type,email)=>{
    return request({
        url:'/api/auth/ask-code',
        params:{
            type,
            email
        }
    })
}