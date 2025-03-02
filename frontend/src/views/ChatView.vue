<template>
  <div class="chat-container">
    <div class="chat-content">
      <div class="chat-sidebar">
        <div class="sidebar-header">
          <router-link to="/" class="back-link">
            <el-icon><ArrowLeft /></el-icon>
          </router-link>
          <h3>AI助手列表</h3>
        </div>
        <AIListComponent />
      </div>
      
      <div class="chat-main">
        <ChatComponent />
      </div>
    </div>
  </div>
</template>

<script setup>
import ChatComponent from '@/components/ChatComponent.vue'
import AIListComponent from '@/components/AIListComponent.vue'
import { ArrowLeft } from '@element-plus/icons-vue'
</script>

<style scoped>
.chat-container {
  height: 100vh;
  background-color: #f9f9f9;
  overflow: hidden;
}

.chat-content {
  display: flex;
  height: 100%;
}

.chat-sidebar {
  width: 260px;
  background-color: #fff;
  border-right: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.03);
  flex-shrink: 0;
}

.sidebar-header {
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  gap: 10px;
}

.back-link {
  color: #909399;
  text-decoration: none;
  display: flex;
  align-items: center;
  transition: color 0.2s;
}

.back-link:hover {
  color: #409EFF;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
  flex: 1;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  margin: 10px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  overflow: hidden;
}
</style> 