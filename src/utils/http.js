import axios from "axios"
import { ElMessage } from "element-plus"

const customHttp=axios.create({
    baseURL:'http://127.0.0.1:8080',
    timeout:5000
})

customHttp.interceptors.request.use((request)=>{
    const token=localStorage.getItem('access_token')
    if(token){
        request.headers.Authorization=`Bearer ${token}`
    }
    return request
},error=>Promise.reject(error))

customHttp.interceptors.response.use((response)=>{
    return response.data
},error=>{
    ElMessage.error(error)
    Promise.reject(error)
})

export default customHttp