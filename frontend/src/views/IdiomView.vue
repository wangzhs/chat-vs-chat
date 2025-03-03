<template>
  <div class="idiom-container">
    <div class="idiom-content">
      <h1 class="title">成语接龙</h1>
      
      <div class="game-area" v-if="!gameStarted">
        <el-input
          v-model="userInput"
          placeholder="请输入一个成语开始游戏"
          class="input-box"
          :disabled="loading"
        />
        <el-button 
          type="primary" 
          @click="startGame"
          :loading="loading"
          class="start-button"
        >
          开始游戏
        </el-button>
      </div>

      <div class="rounds-container" v-if="rounds.length > 0">
        <h2 class="rounds-title">游戏记录</h2>
        <div class="round-list">
          <div v-for="(round, index) in rounds" :key="index" class="round-item">
            <div class="player-name" :class="{ 'ai-player': round.player !== '玩家' }">
              {{ round.player }}
            </div>
            <div class="idiom-text">{{ round.idiom }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const userInput = ref('')
const gameStarted = ref(false)
const loading = ref(false)
const rounds = ref([])
const aiPlayers = ref([])

// 获取AI列表
const fetchAIList = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/ai/list')
    if (!response.ok) {
      throw new Error('获取AI列表失败')
    }
    const data = await response.json()
    console.log('获取到的AI列表:', data)
    // 过滤出启用的AI模型
    aiPlayers.value = data.filter(ai => ai.enabled).map(ai => ({
      id: ai.id,
      name: ai.name,
      type: ai.type,
      displayName: ai.name // 用于显示的名称
    }))
  } catch (error) {
    console.error('获取AI列表失败:', error)
    ElMessage.error('获取AI列表失败')
    aiPlayers.value = []
  }
}

// 在组件挂载时获取AI列表
onMounted(() => {
  fetchAIList()
})

const startGame = async () => {
  if (!userInput.value.trim()) {
    ElMessage.warning('请输入一个成语')
    return
  }

  if (aiPlayers.value.length === 0) {
    ElMessage.error('没有可用的AI模型')
    return
  }

  loading.value = true
  gameStarted.value = true
  
  try {
    // 初始化游戏
    rounds.value = [{ player: '玩家', idiom: userInput.value }]
    let currentIdiom = userInput.value
    
    // 进行5轮对话
    for (let i = 0; i < 5; i++) {
      const currentAI = aiPlayers.value[i % aiPlayers.value.length]
      console.log('发送请求:', {
        previousIdiom: currentIdiom,
        aiPlayer: currentAI.name  // 使用AI的name作为标识
      })

      const response = await fetch('http://localhost:8080/api/idiom/next', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: JSON.stringify({
          previousIdiom: currentIdiom,
          aiPlayer: currentAI.name  // 使用AI的name作为标识
        }),
      })

      const data = await response.json()
      
      if (!response.ok) {
        throw new Error(data.error || data.message || '接口请求失败')
      }
      
      if (data.error) {
        ElMessage.error(data.error)
        break
      }

      currentIdiom = data.idiom
      rounds.value.push({
        player: currentAI.displayName || currentAI.name,  // 优先使用显示名称
        idiom: currentIdiom
      })
    }
  } catch (error) {
    console.error('游戏过程出错:', error)
    ElMessage.error(error.message || '游戏进行过程中出现错误')
    // 如果出错，重置游戏状态
    gameStarted.value = false
    rounds.value = []
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.idiom-container {
  min-height: 100vh;
  padding: 2rem;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.idiom-content {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  color: #409EFF;
  font-size: 2rem;
  margin-bottom: 2rem;
}

.game-area {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}

.input-box {
  flex: 1;
}

.start-button {
  min-width: 120px;
}

.rounds-container {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
}

.rounds-title {
  color: #303133;
  font-size: 1.5rem;
  margin-bottom: 1rem;
}

.round-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.round-item {
  background: white;
  padding: 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.player-name {
  font-size: 0.9rem;
  color: #67c23a;
  margin-bottom: 0.5rem;
}

.player-name.ai-player {
  color: #409EFF;
}

.idiom-text {
  font-size: 1.2rem;
  color: #303133;
}
</style> 