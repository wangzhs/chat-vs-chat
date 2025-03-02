import { createStore } from 'vuex'
import axios from 'axios'

export default createStore({
  state: {
    aiList: [],
    messages: [],
    messageLoading: false,
    aiListLoading: false,
    inputPrefix: '',
  },
  getters: {
    getAiList: state => state.aiList,
    getMessages: state => state.messages,
    isLoading: state => state.messageLoading,
    isAiListLoading: state => state.aiListLoading,
    getInputPrefix: state => state.inputPrefix,
  },
  mutations: {
    SET_AI_LIST(state, aiList) {
      state.aiList = aiList
    },
    ADD_MESSAGE(state, message) {
      state.messages.push(message)
    },
    SET_MESSAGE_LOADING(state, loading) {
      state.messageLoading = loading
    },
    SET_AI_LIST_LOADING(state, loading) {
      state.aiListLoading = loading
    },
    CLEAR_MESSAGES(state) {
      state.messages = []
    },
    UPDATE_AI_STATUS(state, updatedAi) {
      const index = state.aiList.findIndex(ai => ai.id === updatedAi.id)
      if (index !== -1) {
        state.aiList[index] = updatedAi
      }
    },
    SET_INPUT_PREFIX(state, prefix) {
      state.inputPrefix = prefix
    },
  },
  actions: {
    async fetchAiList({ commit }) {
      try {
        commit('SET_AI_LIST_LOADING', true)
        const response = await axios.get('/api/ai/list')
        commit('SET_AI_LIST', response.data)
        return response.data
      } catch (error) {
        console.error('Error fetching AI list:', error)
        throw error
      } finally {
        commit('SET_AI_LIST_LOADING', false)
      }
    },
    async sendMessage({ commit }, { message, targetAi }) {
      try {
        commit('SET_MESSAGE_LOADING', true)
        
        // Add user message to the chat
        const userMessage = {
          id: Date.now(),
          content: message,
          sender: 'user',
          timestamp: new Date().toISOString()
        }
        commit('ADD_MESSAGE', userMessage)
        
        // Send message to backend
        const response = await axios.post('/api/chat/send', {
          message,
          targetAi
        })
        
        // Add AI responses to the chat
        response.data.forEach(aiResponse => {
          const aiMessage = {
            id: Date.now() + Math.random(),
            content: aiResponse.content,
            sender: aiResponse.aiName,
            timestamp: new Date().toISOString()
          }
          commit('ADD_MESSAGE', aiMessage)
        })
        
        return response.data
      } catch (error) {
        console.error('Error sending message:', error)
        throw error
      } finally {
        commit('SET_MESSAGE_LOADING', false)
      }
    },
    clearMessages({ commit }) {
      commit('CLEAR_MESSAGES')
    },
    updateAiStatus({ commit }, ai) {
      commit('UPDATE_AI_STATUS', ai)
    },
    setInputPrefix({ commit }, prefix) {
      commit('SET_INPUT_PREFIX', prefix)
    },
  }
}) 