<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'

const username = ref('')
const password = ref('')
const auth = useAuthStore()
const router = useRouter()

async function handleLogin() {
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: username.value, password: password.value })
    })

    if (res.ok) {
      const data = await res.json()
      auth.login(data.user.username, data.user.role, data.user.id, data.token)
      await auth.fetchBalance()
      router.push('/')
    } else {
      const msg = await res.text()
      alert('Login failed: ' + msg)
    }
  } catch (e) {
    console.error(e)
    alert('Login error')
  }
}
</script>

<template>
  <div class="flex justify-center items-center h-[80vh]">
    <div class="bg-gray-800 p-8 rounded-lg shadow-xl w-96 border border-gray-700">
      <h2 class="text-3xl font-bold mb-6 text-center text-white">Login</h2>
      <form @submit.prevent="handleLogin" class="space-y-4">
        <div>
          <label class="block text-gray-400 mb-1">Username</label>
          <input v-model="username" type="text" class="w-full bg-gray-700 text-white rounded p-2 focus:outline-none focus:ring-2 focus:ring-green-500" placeholder="admin or user" required />
        </div>
        <div>
          <label class="block text-gray-400 mb-1">Password</label>
          <input v-model="password" type="password" class="w-full bg-gray-700 text-white rounded p-2 focus:outline-none focus:ring-2 focus:ring-green-500" placeholder="password" />
        </div>
        <button type="submit" class="w-full bg-gradient-to-r from-green-500 to-blue-500 text-white font-bold py-2 rounded hover:opacity-90 transition">
          Enter
        </button>
      </form>
      <p class="mt-4 text-sm text-gray-500 text-center">
        Don't have an account? <router-link to="/register" class="text-blue-400 hover:underline">Register here</router-link>
      </p>
    </div>
  </div>
</template>
