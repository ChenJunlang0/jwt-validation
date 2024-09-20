<template>
  <div style="margin: 0,20px; text-align: center;">
    <div style="margin-top: 50px;">
        <div style="font-size: 35px; font-weight: bold;">
            注册
        </div>
        <div style="margin-top: 20px; color: grey;">
            欢迎注册网站
        </div>
    </div>
    <div style="margin-top:40px; margin-left: 20px;margin-right: 10px;">
        <el-form :model="form" :rules="rules" ref="formRef">
            <el-form-item prop="username">
                <el-input placeholder="请输入用户名" v-model="form.username" type="text">
                    <template #prefix>
                    <el-icon><User /></el-icon>
                    </template>
                </el-input>
            </el-form-item>
            <el-form-item prop="password">
                <el-input placeholder="请输入密码" v-model="form.password" type="password">
                    <template #prefix>
                    <el-icon><Lock /></el-icon>
                    </template>
                </el-input>
            </el-form-item>
            <el-form-item prop="password_repeat">
                <el-input placeholder="请再次输入密码" v-model="form.password_repeat" type="password">
                    <template #prefix>
                    <el-icon><Lock /></el-icon>
                    </template>
                </el-input>
            </el-form-item>
            <el-form-item prop="email">
                <el-input placeholder="请输入邮箱" v-model="form.email" type="email">
                    <template #prefix>
                    <el-icon><Message /></el-icon>
                    </template>
                </el-input>
            </el-form-item>
            <el-form-item prop="code">
                <el-row style="width: 100%;" gutter="10px" >
                    <el-col :span="17" >
                        <el-input placeholder="请输入验证码" v-model="form.code" type="text">
                            <template #prefix>
                                <el-icon><EditPen /></el-icon>
                            </template>
                            
                        </el-input>
                    </el-col>
                    <el-col :span="7">
                        <el-button type="success" style="width: 100%;" @click="askCode" :disabled='countDowner.on'>{{textShow}}</el-button>
                    </el-col>
                </el-row>
            </el-form-item>
        </el-form>
    </div>
    <div style="margin-top: 65px;">
        <el-button type="primary" plain style="width:270px" @click="register">注册</el-button>
    </div>
    <div style="margin-top: 20px;">
        <span style="font-size: 14px; color: grey;;">已有账号?</span>
        <el-link style="translate: 0 -1px;" @click="$router.push('/')">点击登录</el-link>
    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue';
import {User,Lock,Message,EditPen} from '@element-plus/icons-vue'
import { registerApi,askCodeApi } from '@/apis/register';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
const router=useRouter()
const formRef=ref()
const form=ref({
    username:'',
    password:'',
    password_repeat:'',
    email:'',
    code:''
})
const countDowner=ref({
    count:0,
    on:false,
})
const textShow=ref('获取验证码');
const validatePassword=(rule,value,callback)=>{
    if(form.value.password===value){
        callback()
    }else{
        callback(new Error('两次输入密码应该一致'))
    }
}
const rules=ref({
    username:[
    {required:true,message:'用户名不为空',trigger:['blur','change']},
    {min:3,max:14,message:'长度为3到14位字符',trigger:['blur','change']}
    ],
    password:[
    {required:true,message:'密码不为空',trigger:['blur','change']},
    {min:3,max:20,message:'长度为3到20位字符',trigger:['blur','change']}
    ],
    password_repeat:[
    {required:true,message:'密码不为空',trigger:['blur','change']},
    {validator:validatePassword,message:'两次输入密码应该一致',trigger:['blur','change']}
    ],
    email:[
    {required:true,message:'邮箱不为空',trigger:['blur','change']},
    {type:'email',message:'请输入合法的电子邮箱地址',trigger:['blur','change']}
    ],
    code:[
    {required:true,message:'验证码不为空',trigger:['blur','change']},
    {min:6,max:6,message:'请输入6位验证码',trigger:['blur','change']}
    ]
})
const askCode=async()=>{

    const res=await askCodeApi('register',form.value.email)
    if(res.code===200){
        ElMessage.success('验证码发送成功，请注意验收')
        startCountDowner()
    }else{
        ElMessage.error('验证码发送失败')
    }
    
}
const startCountDowner=()=>{
    countDowner.value.count=60
    countDowner.value.on=true
    const intervalId=setInterval(
        ()=>{
            if(countDowner.value.count>0){
                countDowner.value.count--
                textShow.value=countDowner.value.count
            }else{
                countDowner.value.on=false
                textShow.value='获取验证码'
                clearInterval(intervalId)
            }
        }
    ,1000)
}
const register=async()=>{
    formRef.value.validate(async(valid)=>{
        if(valid){
            const res=await registerApi(form.value.username,form.value.password,form.value.email,form.value.code)
            if(res.code===200){
                ElMessage.success(res.message)
                router.push('/')
            }else{
                ElMessage.error(res.message) 
            }
        }else{
            ElMessage.error('请输入信息')
        }
    })
    
        
    
}

</script>

<style>

</style>
