<template>
  <div class="ai-list-component">
    <div class="ai-list-header">
      <h3>可用AI助手</h3>
      <el-button type="text" size="small" :icon="Refresh" @click="refreshAiList" :loading="loading">刷新</el-button>
    </div>
    
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>
    
    <div v-else-if="aiList.length === 0" class="empty-list">
      <el-icon class="empty-icon"><WarningFilled /></el-icon>
      <p>暂无可用AI助手</p>
      <el-button type="primary" size="small" @click="refreshAiList">刷新列表</el-button>
    </div>
    
    <div v-else class="ai-list">
      <div 
        v-for="ai in aiList" 
        :key="ai.id" 
        class="ai-item"
        :class="{ 'ai-active': ai.enabled }"
        @click="selectAi(ai)"
      >
        <div class="ai-avatar" :style="{ backgroundColor: getAvatarColor(ai.name) }">
          {{ ai.name.charAt(0).toUpperCase() }}
        </div>
        <div class="ai-info">
          <div class="ai-name">{{ ai.name }}</div>
          <div class="ai-description">{{ ai.description }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { Refresh, WarningFilled } from '@element-plus/icons-vue'

// 状态管理
const store = useStore()

// 计算属性
const aiList = computed(() => store.getters.getAiList)
const loading = computed(() => store.getters.isAiListLoading)

// 组件挂载时获取AI列表
onMounted(async () => {
  if (!aiList.value.length) {
    await refreshAiList()
  }
})

// 刷新AI列表
const refreshAiList = async () => {
  await store.dispatch('fetchAiList')
}

// 选择AI
const selectAi = (ai) => {
  store.dispatch('setInputPrefix', `@${ai.name} `)
}

// 获取头像颜色
const getAvatarColor = (name) => {
  // 根据AI名称生成一致的颜色
  const colors = [
    '#409EFF', // Primary Blue
    '#67C23A', // Success Green
    '#E6A23C', // Warning Yellow
    '#F56C6C', // Danger Red
    '#909399', // Info Grey
    '#9B59B6', // Purple
    '#3498DB', // Light Blue
    '#1ABC9C', // Teal
    '#E74C3C', // Red
    '#34495E'  // Dark Blue
  ]
  
  // 简单的哈希函数获取一致的索引
  let hash = 0
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash)
  }
  
  const index = Math.abs(hash) % colors.length
  return colors[index]
}
</script>

<style scoped>
.ai-list-component {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.ai-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 15px 12px;
  flex-shrink: 0;
}

.ai-list-header h3 {
  margin: 0;
  font-size: 16px;
  color: #606266;
}

.loading-container {
  padding: 20px;
}

.empty-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 20px;
  color: #909399;
  text-align: center;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 15px;
  color: #dcdfe6;
}

.empty-list p {
  margin-bottom: 15px;
}

.ai-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 15px 15px;
}

.ai-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: #f5f7fa;
}

.ai-item:hover {
  background-color: #ecf5ff;
  transform: translateY(-2px);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.ai-active {
  background-color: #ecf5ff;
  border-left: 3px solid #409EFF;
}

.ai-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 12px;
  flex-shrink: 0;
}

.ai-info {
  flex: 1;
  min-width: 0;
  margin-right: 10px;
}

.ai-name {
  font-weight: bold;
  margin-bottom: 4px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ai-description {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style> 