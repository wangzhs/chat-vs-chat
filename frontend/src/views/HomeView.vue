<template>
  <div class="home-container">
    <div class="home-content">
      <div class="logo-container">
        <h1 class="app-title">Chat-vs-Chat</h1>
        <p class="app-subtitle">多AI助手实时对话平台</p>
      </div>
      
      <div class="features-container">
        <div class="feature-card">
          <el-icon class="feature-icon"><ChatDotRound /></el-icon>
          <h3>多AI实时对话</h3>
          <p>同时与多个AI助手进行对话，比较不同AI的回答</p>
        </div>
        
        <div class="feature-card">
          <el-icon class="feature-icon"><Opportunity /></el-icon>
          <h3>智能分析</h3>
          <p>对比不同AI的回答质量和特点</p>
        </div>
        
        <div class="feature-card">
          <el-icon class="feature-icon"><User /></el-icon>
          <h3>个性化体验</h3>
          <p>自由选择想要对话的AI助手</p>
        </div>
      </div>
      
      <div class="action-container">
        <router-link to="/chat">
          <el-button type="primary" size="large" class="enter-button">
            进入聊天
            <el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ChatDotRound, Opportunity, User, ArrowRight } from '@element-plus/icons-vue'
</script>

<style scoped>
.home-container {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.home-content {
  max-width: 900px;
  width: 100%;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 40px;
  text-align: center;
}

.logo-container {
  margin-bottom: 40px;
}

.app-title {
  font-size: 3rem;
  color: #409EFF;
  margin-bottom: 10px;
}

.app-subtitle {
  font-size: 1.2rem;
  color: #606266;
}

.features-container {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  margin-bottom: 40px;
}

.feature-card {
  width: 250px;
  padding: 20px;
  margin: 10px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  font-size: 2.5rem;
  color: #409EFF;
  margin-bottom: 15px;
}

.feature-card h3 {
  font-size: 1.2rem;
  margin-bottom: 10px;
  color: #303133;
}

.feature-card p {
  color: #606266;
  font-size: 0.9rem;
}

.action-container {
  margin-top: 20px;
}

.enter-button {
  padding: 12px 30px;
  font-size: 1.1rem;
  border-radius: 50px;
  transition: transform 0.2s ease;
}

.enter-button:hover {
  transform: scale(1.05);
}
</style> 