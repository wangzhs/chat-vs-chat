import { NextResponse } from 'next/server'

export async function POST(request: Request) {
  try {
    const { previousIdiom, aiPlayer } = await request.json()

    const response = await fetch('http://localhost:8080/api/idiom/next', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        previousIdiom,
        aiPlayer,
      }),
    })

    if (!response.ok) {
      throw new Error('Backend API request failed')
    }

    const data = await response.json()
    return NextResponse.json(data)
  } catch (error) {
    console.error('Error in idiom API:', error)
    return NextResponse.json(
      { error: 'Internal Server Error' },
      { status: 500 }
    )
  }
} 