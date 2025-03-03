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
          @keyup.enter="startGame"
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
        <div class="rounds-header">
          <h2 class="rounds-title">游戏记录</h2>
          <el-button 
            type="warning" 
            @click="resetGame"
            :disabled="loading"
            size="small"
          >
            重新开始
          </el-button>
        </div>
        <div class="round-list">
          <div v-for="(round, index) in rounds" :key="index" class="round-item">
            <div class="player-name" :class="{ 'ai-player': round.player !== '玩家' }">
              {{ round.player }}
            </div>
            <div class="idiom-text">{{ round.idiom }}</div>
          </div>
        </div>
      </div>

      <div v-if="gameStarted && !loading && isWaitingForUser" class="user-input-area">
        <div class="input-hint">轮到你接龙了</div>
        <div class="input-wrapper">
          <el-input
            v-model="userInput"
            :placeholder="'请接上一个成语：' + lastIdiom"
            class="input-box"
            @keyup.enter="submitUserIdiom"
          />
          <el-button 
            type="primary" 
            @click="submitUserIdiom"
            class="submit-button"
          >
            提交
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const userInput = ref('')
const gameStarted = ref(false)
const loading = ref(false)
const rounds = ref([])
const aiPlayers = ref([])
const isWaitingForUser = ref(false)

// 计算最后一个成语
const lastIdiom = computed(() => {
  return rounds.value.length > 0 ? rounds.value[rounds.value.length - 1].idiom : ''
})

// 重置游戏
const resetGame = () => {
  gameStarted.value = false
  rounds.value = []
  userInput.value = ''
  loading.value = false
  isWaitingForUser.value = false
}

// 验证成语
const isValidIdiom = (idiom) => {
  return idiom && idiom.trim().length === 4
}

// 用户提交成语
const submitUserIdiom = async () => {
  if (!userInput.value.trim()) {
    ElMessage.warning('请输入成语')
    return
  }

  if (!isValidIdiom(userInput.value)) {
    ElMessage.warning('请输入四字成语')
    return
  }

  // 添加用户的成语
  rounds.value.push({ player: '玩家', idiom: userInput.value })
  
  // 清空输入框
  const currentIdiom = userInput.value
  userInput.value = ''
  isWaitingForUser.value = false
  
  // AI接龙
  await getAIResponse(currentIdiom)
}

// 获取AI响应
const getAIResponse = async (currentIdiom) => {
  if (aiPlayers.value.length === 0) {
    ElMessage.error('没有可用的AI模型')
    return
  }

  loading.value = true
  
  try {
    const currentAI = aiPlayers.value[Math.floor(rounds.value.length / 2) % aiPlayers.value.length]
    console.log('发送请求:', {
      previousIdiom: currentIdiom,
      aiPlayer: currentAI.name
    })

    const response = await fetch('http://localhost:8080/api/idiom/next', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify({
        previousIdiom: currentIdiom,
        aiPlayer: currentAI.name
      }),
    })

    const data = await response.json()
    
    if (!response.ok) {
      throw new Error(data.error || data.message || '接口请求失败')
    }
    
    if (data.error) {
      ElMessage.error(data.error)
      return
    }

    rounds.value.push({
      player: currentAI.displayName || currentAI.name,
      idiom: data.idiom
    })

    // AI回答完后，等待用户输入
    isWaitingForUser.value = true
  } catch (error) {
    console.error('AI响应出错:', error)
    ElMessage.error(error.message || 'AI响应出错')
  } finally {
    loading.value = false
  }
}

// 获取AI列表
const fetchAIList = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/ai/list')
    if (!response.ok) {
      throw new Error('获取AI列表失败')
    }
    const data = await response.json()
    console.log('获取到的AI列表:', data)
    aiPlayers.value = data.filter(ai => ai.enabled).map(ai => ({
      id: ai.id,
      name: ai.name,
      type: ai.type,
      displayName: ai.name
    }))
  } catch (error) {
    console.error('获取AI列表失败:', error)
    ElMessage.error('获取AI列表失败')
    aiPlayers.value = []
  }
}

// 开始游戏
const startGame = async () => {
  if (!userInput.value.trim()) {
    ElMessage.warning('请输入一个成语')
    return
  }

  if (!isValidIdiom(userInput.value)) {
    ElMessage.warning('请输入四字成语')
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
    const currentIdiom = userInput.value
    userInput.value = ''
    
    // 第一轮AI响应
    await getAIResponse(currentIdiom)
  } catch (error) {
    console.error('游戏开始出错:', error)
    ElMessage.error(error.message || '游戏开始出错')
    resetGame()
  }
}

// 在组件挂载时获取AI列表
onMounted(() => {
  fetchAIList()
})
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
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 4rem);
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
  flex: 1;
  margin-bottom: 2rem;
  overflow-y: auto;
}

.rounds-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  position: sticky;
  top: 0;
  background: #f8f9fa;
  padding: 0.5rem 0;
  z-index: 1;
}

.rounds-title {
  color: #303133;
  font-size: 1.5rem;
  margin: 0;
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

.user-input-area {
  margin-top: auto;
  padding-top: 1rem;
  border-top: 1px solid #eee;
}

.input-hint {
  text-align: center;
  color: #67c23a;
  font-size: 1rem;
  margin-bottom: 1rem;
}

.input-wrapper {
  display: flex;
  gap: 1rem;
}

.submit-button {
  min-width: 120px;
}
</style> 