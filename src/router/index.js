import {createRouter,createWebHistory} from 'vue-router'
import { checkTokenApi } from '@/apis/checkToken'
const route=createRouter({
    history:createWebHistory(import.meta.env.BASE_URL),
    routes:[
        {
            path:'/',
            name: 'welcome',
            component: ()=>import('@/views/WelcomeView.vue'),
            children:[
                {
                    path:'',
                    name: 'welcome-login',
                    component:()=>import('@/views/welcome/LoginView.vue')
                },
                {
                    path:'/register',
                    name:'welcome-register',
                    component:()=>import('@/views/welcome/Register.vue')
                },
                {
                    path:'/reset',
                    name:'welcome-reset',
                    component:()=>import('@/views/welcome/Reset.vue')
                }
            ]
        },
        {
            path:'/main',
            name:'main',
            component:()=>import('@/views/MainPage.vue')
        }
    ]
})
route.beforeEach(async(to,from,next)=>{
    const token=localStorage.getItem('access_token')
    const res=await checkTokenApi(`Bearer ${token}`);
    const isValid=res.data;
    if(to.name.startsWith('main')&&isValid===null){
        next('/')
    }else if(to.name.startsWith('welcome-')&&isValid!=null){
        next('/main')
    }else{
        next()
    }
})
export default route