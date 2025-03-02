<template>
  <div class="chat-container">
    <!-- 侧边栏：AI列表 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h2>AI助手列表</h2>
        <el-button type="primary" size="small" @click="refreshAIList">刷新</el-button>
      </div>
      <div class="ai-list">
        <div 
          v-for="ai in aiList" 
          :key="ai.id" 
          class="ai-item"
          :class="{ 'ai-active': ai.enabled }"
          @click="toggleAI(ai)"
        >
          <div class="ai-avatar">
            {{ ai.name.charAt(0) }}
          </div>
          <div class="ai-info">
            <div class="ai-name">{{ ai.name }}</div>
            <div class="ai-description">{{ ai.description }}</div>
          </div>
          <el-switch
            v-model="ai.enabled"
            active-color="#13ce66"
            inactive-color="#ff4949"
            @change="updateAIStatus(ai)"
          ></el-switch>
        </div>
      </div>
    </div>

    <!-- 主聊天区域 -->
    <div class="chat-main">
      <!-- 聊天头部 -->
      <div class="chat-header">
        <h2>AI群聊 ({{ enabledAICount }}个AI在线)</h2>
        <div class="chat-actions">
          <el-button type="primary" size="small" @click="clearMessages">清空聊天</el-button>
        </div>
      </div>

      <!-- 聊天消息区域 -->
      <div class="chat-messages" ref="messagesContainer">
        <div v-for="(messageGroup, index) in messageGroups" :key="index" class="message-group">
          <!-- 用户消息 -->
          <div v-if="messageGroup.type === 'user'" class="message user-message">
            <div class="message-avatar">
              <i class="el-icon-user"></i>
            </div>
            <div class="message-content">
              <div class="message-sender">您</div>
              <div class="message-text">{{ messageGroup.content }}</div>
              <div class="message-time">{{ formatTime(messageGroup.timestamp) }}</div>
            </div>
          </div>

          <!-- AI回复 -->
          <div v-else class="ai-responses">
            <div 
              v-for="response in messageGroup.responses" 
              :key="response.aiId" 
              class="message ai-message"
              :class="{ 'error-message': response.error }"
            >
              <div class="message-avatar" :style="{ backgroundColor: getAIColor(response.aiId) }">
                {{ response.aiName.charAt(0) }}
              </div>
              <div class="message-content">
                <div class="message-sender">
                  {{ response.aiName }}
                  <span class="response-time">({{ response.responseTimeMs }}ms)</span>
                </div>
                <div class="message-text" v-if="!response.error">{{ response.content }}</div>
                <div class="message-text error-text" v-else>{{ response.error }}</div>
                <div class="message-time">{{ formatTime(response.timestamp) }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载指示器 -->
        <div v-if="loading" class="loading-indicator">
          <el-loading-spinner></el-loading-spinner>
          <span>AI正在思考中...</span>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input">
        <el-input
          type="textarea"
          :rows="3"
          placeholder="输入您的消息..."
          v-model="userMessage"
          @keyup.ctrl.enter="sendMessage"
        ></el-input>
        <div class="input-actions">
          <el-select v-model="targetAi" placeholder="发送给" style="width: 150px;">
            <el-option label="所有AI" value=""></el-option>
            <el-option 
              v-for="ai in enabledAIs" 
              :key="ai.id" 
              :label="ai.name" 
              :value="ai.id"
            ></el-option>
          </el-select>
          <el-button type="primary" @click="sendMessage" :disabled="loading || !userMessage.trim()">
            发送
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'ChatInterface',
  data() {
    return {
      aiList: [],
      messageGroups: [],
      userMessage: '',
      loading: false,
      targetAi: '',
      aiColors: {}
    };
  },
  computed: {
    enabledAIs() {
      return this.aiList.filter(ai => ai.enabled);
    },
    enabledAICount() {
      return this.enabledAIs.length;
    }
  },
  mounted() {
    this.fetchAIList();
  },
  updated() {
    this.scrollToBottom();
  },
  methods: {
    async fetchAIList() {
      try {
        const response = await axios.get('/api/ai/list');
        this.aiList = response.data;
        
        // 为每个AI分配一个颜色
        this.aiList.forEach(ai => {
          if (!this.aiColors[ai.id]) {
            this.aiColors[ai.id] = this.getRandomColor();
          }
        });
      } catch (error) {
        console.error('Error fetching AI list:', error);
        this.$message.error('获取AI列表失败');
      }
    },
    
    refreshAIList() {
      this.fetchAIList();
      this.$message.success('AI列表已刷新');
    },
    
    async toggleAI(ai) {
      try {
        const endpoint = ai.enabled ? `/api/ai/${ai.id}/disable` : `/api/ai/${ai.id}/enable`;
        const response = await axios.post(endpoint);
        
        // 更新AI状态
        const index = this.aiList.findIndex(item => item.id === ai.id);
        if (index !== -1) {
          this.aiList[index] = response.data;
        }
        
        this.$message.success(`${ai.name} 已${ai.enabled ? '启用' : '禁用'}`);
      } catch (error) {
        console.error('Error toggling AI status:', error);
        this.$message.error(`更改${ai.name}状态失败`);
      }
    },
    
    async updateAIStatus(ai) {
      try {
        const endpoint = ai.enabled ? `/api/ai/${ai.id}/enable` : `/api/ai/${ai.id}/disable`;
        await axios.post(endpoint);
      } catch (error) {
        console.error('Error updating AI status:', error);
        // 恢复原状态
        ai.enabled = !ai.enabled;
        this.$message.error(`更改${ai.name}状态失败`);
      }
    },
    
    async sendMessage() {
      if (!this.userMessage.trim() || this.loading) return;
      
      // 添加用户消息到聊天记录
      this.messageGroups.push({
        type: 'user',
        content: this.userMessage,
        timestamp: new Date()
      });
      
      const message = this.userMessage;
      this.userMessage = '';
      this.loading = true;
      
      try {
        const response = await axios.post('/api/ai/chat', {
          message,
          targetAi: this.targetAi
        });
        
        // 添加AI回复到聊天记录
        const responses = Object.values(response.data);
        this.messageGroups.push({
          type: 'ai',
          responses,
          timestamp: new Date()
        });
      } catch (error) {
        console.error('Error sending message:', error);
        this.$message.error('发送消息失败');
        
        // 添加错误消息
        this.messageGroups.push({
          type: 'ai',
          responses: [{
            aiId: 'error',
            aiName: '系统',
            error: '发送消息失败，请检查网络连接或后端服务是否正常运行。',
            responseTimeMs: 0,
            timestamp: new Date()
          }],
          timestamp: new Date()
        });
      } finally {
        this.loading = false;
        this.scrollToBottom();
      }
    },
    
    clearMessages() {
      this.messageGroups = [];
      this.$message.success('聊天记录已清空');
    },
    
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer;
        if (container) {
          container.scrollTop = container.scrollHeight;
        }
      });
    },
    
    formatTime(timestamp) {
      if (!timestamp) return '';
      
      const date = new Date(timestamp);
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    },
    
    getAIColor(aiId) {
      return this.aiColors[aiId] || '#409EFF';
    },
    
    getRandomColor() {
      const colors = [
        '#409EFF', // 蓝色
        '#67C23A', // 绿色
        '#E6A23C', // 黄色
        '#F56C6C', // 红色
        '#909399', // 灰色
        '#9B59B6', // 紫色
        '#3498DB', // 天蓝色
        '#1ABC9C', // 青绿色
        '#F39C12', // 橙色
        '#D35400'  // 深橙色
      ];
      
      return colors[Math.floor(Math.random() * colors.length)];
    }
  }
};
</script>

<style scoped>
.chat-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background-color: #f5f7fa;
}

/* 侧边栏样式 */
.sidebar {
  width: 300px;
  background-color: #fff;
  border-right: 1px solid #e6e6e6;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 15px;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar-header h2 {
  margin: 0;
  font-size: 18px;
}

.ai-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.ai-item {
  display: flex;
  align-items: center;
  padding: 10px;
  border-radius: 8px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.ai-item:hover {
  background-color: #f5f7fa;
}

.ai-active {
  background-color: #ecf5ff;
}

.ai-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #409EFF;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
  margin-right: 10px;
}

.ai-info {
  flex: 1;
  overflow: hidden;
}

.ai-name {
  font-weight: bold;
  margin-bottom: 5px;
}

.ai-description {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 主聊天区域样式 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  padding: 15px;
  border-bottom: 1px solid #e6e6e6;
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-header h2 {
  margin: 0;
  font-size: 18px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f7fa;
}

.message-group {
  margin-bottom: 20px;
}

.message {
  display: flex;
  margin-bottom: 10px;
  max-width: 80%;
}

.user-message {
  margin-left: auto;
  flex-direction: row-reverse;
}

.ai-message {
  margin-right: auto;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #409EFF;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.user-message .message-avatar {
  margin-left: 10px;
  background-color: #67C23A;
}

.ai-message .message-avatar {
  margin-right: 10px;
}

.message-content {
  background-color: #fff;
  padding: 10px 15px;
  border-radius: 10px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.user-message .message-content {
  background-color: #ecf5ff;
}

.message-sender {
  font-weight: bold;
  margin-bottom: 5px;
  font-size: 14px;
}

.response-time {
  font-weight: normal;
  font-size: 12px;
  color: #909399;
}

.message-text {
  word-break: break-word;
  line-height: 1.5;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  text-align: right;
}

.ai-responses {
  display: flex;
  flex-direction: column;
}

.error-message .message-content {
  background-color: #fef0f0;
}

.error-text {
  color: #F56C6C;
}

.loading-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  color: #909399;
}

/* 输入区域样式 */
.chat-input {
  padding: 15px;
  background-color: #fff;
  border-top: 1px solid #e6e6e6;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}
</style> 