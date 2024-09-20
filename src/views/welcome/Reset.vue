<script setup>
import { ref } from 'vue';
import {Lock,Message,EditPen} from '@element-plus/icons-vue'
import { askCodeApi } from '@/apis/register';
import { ElMessage } from 'element-plus';
import { resetApi } from '@/apis/reset';
import { useRouter } from 'vue-router';
const active=ref(0)
const form =ref({
    email:'',
    code:'',
    password:'',
    password_repeat:''
})
const router=useRouter()
const formRefFirst=ref()
const formRefSecond=ref()
const countDowner=ref({
    count:0,
    on:false,
})
const validatePassword=(rule,value,callback)=>{
    if(form.value.password===value){
        callback()
    }else{
        callback(new Error('两次输入密码应该一致'))
    }
}
const rules=ref({
    email:[
        {required:true,message:'邮箱不为空',trigger:['blur','change']},
        {type:'email',message:'请输入合法的电子邮箱地址',trigger:['blur','change']}
        ],
    code:[
        {required:true,message:'验证码不为空',trigger:['blur','change']},
        {min:6,max:6,message:'请输入6位验证码',trigger:['blur','change']}
        ],
    password:[
        {required:true,message:'密码不为空',trigger:['blur','change']},
        {min:3,max:20,message:'长度为3到20位字符',trigger:['blur','change']}
    ],
    password_repeat:[
        {required:true,message:'密码不为空',trigger:['blur','change']},
        {validator:validatePassword,message:'两次输入密码应该一致',trigger:['blur','change']}
    ]
})

const textShow=ref('获取验证码');
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
const askCode=async()=>{

const res=await askCodeApi('reset',form.value.email)
if(res.code===200){
    ElMessage.success('验证码发送成功，请注意验收')
    startCountDowner()
}else{
    ElMessage.error('验证码发送失败')
}
}
const nextStep=()=>{
    formRefFirst.value.validate((valid)=>{
        if(valid){
            active.value++
        }else{
            ElMessage.error('请输入验证信息')
        }
    })
    
}
const lastStep=()=>{
    active.value--
}
const reset=async()=>{
    formRefSecond.value.validate(async(valid)=>{
        if(valid){
            const res=await resetApi(form.value.email,form.value.code,form.value.password)
            if(res.code===200){
                ElMessage.success(res.message)
                active.value++
                router.push('/')
            }else{
            ElMessage.error(res.message)
            }
        }else{
            ElMessage.error('请输入正确的密码')
        }
    })
    
}
</script>

<template>
  <div style="text-align: center;margin: 0 20px;">
    <div style="margin-top: 30px;">
        <el-steps :active="active" align-center finish-status="success">
            <el-step title="邮箱验证"></el-step>
            <el-step title="密码重置"></el-step>
        </el-steps>
    </div>
    <div v-if="active===0" style="margin-top: 40px;">
        <div style="font-size: 30px; font-weight: bold;">
            邮箱验证
        </div>
        <div style="font-size: 14px;color: grey;margin-top: 10px;">
            请输入您的邮箱
        </div>
        <div style="margin-top: 50px;">
            <el-form :model="form" :rules="rules" ref="formRefFirst">
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
        <div style="margin-top: 150px;">
            <el-button plain style="width: 200px;" type="warning" @click="nextStep">开始重置密码</el-button>
        </div>
    </div>
    <div v-if="active===1" style="margin-top: 40px;">
        <div style="font-size: 30px; font-weight: bold;">
            重置密码
        </div>
        <div style="font-size: 14px;color: grey;margin-top: 10px;">
            请输入您重置的密码
        </div>
        <div style="margin-top: 50px;">
            <el-form :model="form" :rules="rules" ref="formRefSecond">
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
            </el-form>
        </div>
        <div style="margin-top: 150px;">
            <el-row style="width: 100%;" :gutter="10"> 
                <el-col :span="12">
                    <el-button plain  type="warning" @click="lastStep" style="width:100%">返回上一步</el-button>
                </el-col>
                <el-col :span="12" >
                    <el-button plain  type="warning" @click="reset" style="width: 100%;">重置密码</el-button>
                </el-col>
            </el-row>
        </div>
    </div>
  </div>
</template>

<style>

</style>
