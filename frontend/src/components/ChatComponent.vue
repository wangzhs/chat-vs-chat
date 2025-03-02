<template>
  <div class="chat-component">
    <div class="chat-header-info">
      <div class="chat-status">
        <span class="status-indicator" :class="{ 'active': !loading }"></span>
        <span>{{ loading ? '正在响应...' : '在线' }}</span>
        <span v-if="currentAiName" class="current-ai">
          <el-icon><ChatDotRound /></el-icon>
          正在与 {{ currentAiName }} 对话
        </span>
      </div>
      <div class="chat-actions">
        <el-button type="text" :icon="Delete" @click="clearMessages">清空聊天</el-button>
      </div>
    </div>
    
    <div class="chat-messages" ref="messagesContainer">
      <div v-if="messages.length === 0" class="empty-chat">
        <div class="empty-icon">
          <el-icon><ChatDotRound /></el-icon>
        </div>
        <p>开始与AI助手对话吧</p>
        <p class="empty-tip">提示: 在左侧可以选择你指定想要对话的AI助手</p>
      </div>
      
      <div v-for="message in messages" :key="message.id" class="message-container" :class="{ 'user-message': message.sender === 'user' }">
        <div class="message-avatar" :class="{ 'user-avatar': message.sender === 'user' }">
          <span v-if="message.sender === 'user'">
            <el-icon><User /></el-icon>
          </span>
          <span v-else>{{ message.sender.charAt(0) }}</span>
        </div>
        
        <div class="message-bubble">
          <div class="message-header">
            <span class="message-sender">{{ message.sender === 'user' ? '你' : message.sender }}</span>
            <span class="message-time">{{ formatTime(message.timestamp) }}</span>
          </div>
          <div class="message-content">{{ message.content }}</div>
        </div>
      </div>
    </div>
    
    <div class="chat-input">
      <el-input 
        v-model="newMessage" 
        @keyup.enter="sendMessage"
        placeholder="输入消息，按回车发送..."
        :disabled="loading"
        clearable
        type="textarea"
        :rows="2"
        resize="none"
        ref="inputRef"
      >
      </el-input>
      <el-button 
        type="primary" 
        @click="sendMessage" 
        :disabled="loading || !newMessage.trim()"
        :loading="loading"
        :icon="Promotion"
      >
        发送
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted } from 'vue'
import { useStore } from 'vuex'
import { Delete, User, ChatDotRound, Promotion } from '@element-plus/icons-vue'

// 状态管理
const store = useStore()
const newMessage = ref('')
const messagesContainer = ref(null)
const inputRef = ref(null)

// 计算属性
const messages = computed(() => store.getters.getMessages)
const loading = computed(() => store.getters.isLoading)
const aiList = computed(() => store.getters.getAiList)
const inputPrefix = computed(() => store.getters.getInputPrefix)
const currentAiName = computed(() => {
  const message = newMessage.value.trim()
  const atMatch = message.match(/^@(\S+)\s+/)
  if (atMatch) {
    const [, aiName] = atMatch
    const targetAi = aiList.value.find(ai => 
      ai.name.toLowerCase() === aiName.toLowerCase()
    )
    return targetAi ? targetAi.name : null
  }
  return null
})

// 监听消息变化，自动滚动到底部
watch(messages, async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}, { deep: true })

// 监听输入框前缀变化
watch(inputPrefix, (newPrefix) => {
  if (newPrefix) {
    newMessage.value = newPrefix
    store.dispatch('setInputPrefix', '') // 清除前缀，避免重复添加
    // 自动聚焦输入框并将光标移到末尾
    nextTick(() => {
      if (inputRef.value) {
        const textarea = inputRef.value.$el.querySelector('textarea')
        textarea.focus()
        textarea.setSelectionRange(newMessage.value.length, newMessage.value.length)
      }
    })
  }
})

// 组件挂载时获取AI列表
onMounted(async () => {
  if (!aiList.value.length) {
    await store.dispatch('fetchAiList')
  }
})

// 发送消息
const sendMessage = async () => {
  if (!newMessage.value.trim() || loading.value) return
  
  const message = newMessage.value.trim()
  
  // 检查消息是否针对特定AI (@AIName message)
  const atMatch = message.match(/^@(\S+)\s+(.+)$/)
  if (atMatch) {
    const [, aiName, actualMessage] = atMatch
    // 检查AI是否存在且已启用
    const targetAi = aiList.value.find(ai => 
      ai.name.toLowerCase() === aiName.toLowerCase() && ai.enabled
    )
    
    if (targetAi) {
      await store.dispatch('sendMessage', { 
        message: actualMessage.trim(),
        targetAi: targetAi.name
      })
    } else {
      // 如果AI不存在或未启用，添加一条提示消息
      store.commit('ADD_MESSAGE', {
        id: Date.now(),
        content: `AI助手 "${aiName}" 不存在或未启用，请先在左侧启用该AI助手。`,
        sender: 'system',
        timestamp: new Date().toISOString()
      })
    }
  } else {
    // 发送给所有启用的AI
    const enabledAis = aiList.value.filter(ai => ai.enabled)
    if (enabledAis.length === 0) {
      // 如果没有启用的AI，添加提示消息
      store.commit('ADD_MESSAGE', {
        id: Date.now(),
        content: '请先在左侧启用至少一个AI助手。',
        sender: 'system',
        timestamp: new Date().toISOString()
      })
    } else {
      await store.dispatch('sendMessage', { 
        message,
        targetAi: null
      })
    }
  }
  
  newMessage.value = ''
}

// 格式化时间
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

// 清空消息
const clearMessages = () => {
  store.dispatch('clearMessages')
}
</script>

<style scoped>
.chat-component {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.chat-header-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  border-bottom: 1px solid #ebeef5;
  flex-shrink: 0;
}

.chat-status {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #606266;
  gap: 12px;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #909399;
  margin-right: 6px;
}

.status-indicator.active {
  background-color: #67c23a;
}

.current-ai {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #409EFF;
  font-weight: 500;
}

.current-ai .el-icon {
  font-size: 16px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f9fafc;
  min-height: 200px;
}

.empty-chat {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 20px;
  color: #dcdfe6;
}

.empty-tip {
  font-size: 12px;
  margin-top: 10px;
  color: #c0c4cc;
}

.message-container {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-start;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-container.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin: 0 10px;
}

.user-avatar {
  background-color: #67c23a;
}

.message-bubble {
  max-width: 70%;
  border-radius: 12px;
  padding: 12px 16px;
  background-color: white;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.user-message .message-bubble {
  background-color: #ecf5ff;
}

.message-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
  font-size: 12px;
}

.message-sender {
  font-weight: bold;
  color: #303133;
}

.message-time {
  color: #909399;
}

.message-content {
  word-break: break-word;
  line-height: 1.5;
  color: #303133;
}

.chat-input {
  padding: 12px 15px;
  border-top: 1px solid #ebeef5;
  display: flex;
  align-items: flex-end;
  gap: 10px;
  background-color: #fff;
  flex-shrink: 0;
}

.chat-input .el-input {
  flex: 1;
}

.chat-input .el-button {
  height: 56px;
  padding: 0 20px;
}

.chat-input :deep(.el-textarea__inner) {
  resize: none;
  border-radius: 4px;
  min-height: 56px !important;
  padding: 12px;
}
</style> 