'use client'

import { useState } from 'react'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'

export default function IdiomPage() {
  const [userInput, setUserInput] = useState('')
  const [gameStarted, setGameStarted] = useState(false)
  const [rounds, setRounds] = useState<{ player: string; idiom: string }[]>([])
  const [isLoading, setIsLoading] = useState(false)

  const startGame = async () => {
    if (!userInput.trim()) {
      alert('请输入一个成语')
      return
    }

    setIsLoading(true)
    setGameStarted(true)
    
    // 初始化游戏
    const newRounds = [{ player: '玩家', idiom: userInput }]
    setRounds(newRounds)

    try {
      // 进行5轮对话
      let currentIdiom = userInput
      const aiPlayers = ['GPT-4', 'Claude', 'Gemini']
      
      for (let i = 0; i < 5; i++) {
        const response = await fetch('/api/idiom', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            previousIdiom: currentIdiom,
            aiPlayer: aiPlayers[i % aiPlayers.length]
          }),
        })

        if (!response.ok) throw new Error('接口请求失败')
        
        const data = await response.json()
        currentIdiom = data.idiom
        
        newRounds.push({
          player: aiPlayers[i % aiPlayers.length],
          idiom: currentIdiom
        })
        
        setRounds([...newRounds])
      }
    } catch (error) {
      console.error('游戏过程出错:', error)
      alert('游戏进行过程中出现错误')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <main className="flex min-h-screen flex-col items-center p-24 gap-8">
      <h1 className="text-4xl font-bold">成语接龙</h1>
      
      <div className="w-full max-w-md space-y-4">
        <Input
          type="text"
          placeholder="请输入一个成语开始游戏"
          value={userInput}
          onChange={(e) => setUserInput(e.target.value)}
          disabled={gameStarted && isLoading}
        />
        
        <Button 
          onClick={startGame}
          disabled={gameStarted && isLoading}
          className="w-full"
        >
          {isLoading ? '游戏进行中...' : '开始游戏'}
        </Button>

        {rounds.length > 0 && (
          <div className="mt-8 space-y-4">
            <h2 className="text-xl font-semibold">游戏记录：</h2>
            {rounds.map((round, index) => (
              <div
                key={index}
                className="p-4 rounded-lg bg-white shadow dark:bg-gray-800"
              >
                <div className="font-medium text-gray-700 dark:text-gray-300">
                  {round.player}
                </div>
                <div className="text-lg">{round.idiom}</div>
              </div>
            ))}
          </div>
        )}
      </div>
    </main>
  )
} 