import request from '@/utils/http'


export const loginApi=(username,password,remember)=>{
    
    return request({
        url:'/api/auth/login',
        method:'POST',
        data:{
            username,
            password
        },
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
}