<template>
  <div style="text-align: center; margin: 10px,30px;">
    <div style="margin-top: 100px; font-size: 40px; font-weight: bold;">登录</div>
    <div style="color: gray;margin-top: 20px;">请先输入用户名和密码进行登录</div>
    <div style="margin-top: 40px;">
      <el-form :model="user" :rules="rules">
        <el-form-item prop="username">
          <el-input v-model="user.username" placeholder="用户名/邮箱" type="text" maxlength="20">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="user.password" placeholder="密码" type="password" maxlength="20">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
          <el-row>
            <el-col :span="12" style="text-align: left;">
              <el-form-item>
              <el-checkbox v-model="user.rememberMe" label="记住我"></el-checkbox>
            </el-form-item>
            </el-col>
            <el-col :span="12" style="text-align: right;">
              <el-link href="/reset">忘记密码？</el-link>
            </el-col>
          </el-row>
      </el-form>
      <el-button type="primary" plain style="width: 200px;" @click="login">登录</el-button>
      <el-divider content-position="center" >
        <span style="color: grey;"> 没有账号？</span>
      </el-divider>
      <el-button type="warning" plain style="width: 200px;" @click="router.push('/register')">注册</el-button>
      
      <!-- <br><el-button type="success" plain style="width: 200px;margin-top: 50px;" @click="test">测试</el-button> -->
    </div>
  </div>
</template>

<script setup>
import {User,Lock} from '@element-plus/icons-vue'
import { ref } from 'vue';
import { loginApi } from '@/apis/login';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
const router=useRouter()
const user=ref({
  username:'kobe',
  password:'123456',
  rememberMe:true
})

const rules=ref({
  username:[
    {required:true,message:'请输入用户名或邮箱',trigger:['blur','change']},
    {max:14,min:3,message:'长度为3到14',trigger:['blur','change']}
  ],
  password:[
    {required:true,message:'请输入密码',trigger:['blur','change']}
  ]
})

const login=async()=>{

    const storeToken=(remember,token)=>{
        if(remember){
            localStorage.setItem('access_token',token)
        }else{
            sessionStorage.setItem('access_token',token)
        }
    }
    const data=await loginApi(user.value.username,user.value.password,user.value.rememberMe)
    if(data.code===200){
            storeToken(user.value.rememberMe,data.data.token)
            ElMessage({
                type:'success',
                message:'登录成功'
            })
            router.push('/main')
        }else{
            ElMessage({
                type:'error',
                message:data.message
            })
        }
}

</script>

<style>

</style>