<template>
  <div style="width: 100%;height: 100dvh;overflow: hidden;display: flex;">
  
    <div style="flex: 2.5;background-color: black;">
      <el-image style="width: 100%;height: 100%;" fit="cover" :src="welcome"></el-image>
    </div>

   

    <div  class="right-card">
      <transition name="el-fade-in-linear">
        <router-view></router-view>
      </transition>
      
    </div>
  </div>
  <div class="welcome-title" >
      <div style="font-size: 30px;font-weight: bold;">welcome</div>
      <div style="margin-top: 5px;">you can learn Java here</div>
      <div style="margin-top: 5px;">make friends as well</div>
  </div>
  
</template>

<script setup>
import welcome from '@/assets/6.jpg'
</script>

<style>
.right-card{
  flex: 1;
}
.welcome-title{
  position: absolute;
  left: 30px;
  bottom: 30px;
  color: #f4f1f1;
 
}
</style>