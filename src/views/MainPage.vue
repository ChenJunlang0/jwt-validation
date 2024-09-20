<template>
  <div>
    <el-button @click="logout">退出登录</el-button>
    <br><el-button type="success" plain style="width: 200px;margin-top: 50px;" @click="test">测试</el-button>
  </div>
</template>

<script setup>
import { checkTokenApi } from '@/apis/checkToken';
import { logoutApi } from '@/apis/logout';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
const router=useRouter()
const logout=async()=>{
    const res=await logoutApi()
    if(res.code===200){
        localStorage.removeItem('access_token')
        sessionStorage.removeItem('access_token')
        ElMessage.success('登出成功')
        router.push('/')
    }else{
        ElMessage.error('登出失败')
    }
}
const test=async()=>{
  const token= localStorage.getItem('access_token')
  const res=await checkTokenApi(`Bearer ${token}`)
  console.log(res)
}
</script>

<style>

</style>