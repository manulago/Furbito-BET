<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useLanguageStore } from '../stores/language'
import { useRouter } from 'vue-router'

const username = ref('')
const password = ref('')
const showPassword = ref(false)
const auth = useAuthStore()
const langStore = useLanguageStore()
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
      <h2 class="text-3xl font-bold mb-6 text-center text-white">{{ langStore.t('auth.login') }}</h2>
      <form @submit.prevent="handleLogin" class="space-y-4">
        <div>
          <label class="block text-gray-400 mb-1">{{ langStore.t('auth.username') }}</label>
          <input v-model="username" type="text" class="w-full bg-gray-700 text-white rounded p-2 focus:outline-none focus:ring-2 focus:ring-green-500" :placeholder="langStore.t('auth.username')" required />
        </div>
        <div>
          <label class="block text-gray-400 mb-1">{{ langStore.t('auth.password') }}</label>
          <div class="relative">
            <input v-model="password" :type="showPassword ? 'text' : 'password'" class="w-full bg-gray-700 text-white rounded p-2 pr-10 focus:outline-none focus:ring-2 focus:ring-green-500" :placeholder="langStore.t('auth.password')" />
            <button type="button" @click="showPassword = !showPassword" class="absolute inset-y-0 right-0 px-3 flex items-center text-gray-400 hover:text-white">
              <svg v-if="!showPassword" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
              </svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
              </svg>
            </button>
          </div>
        </div>
        <button type="submit" class="w-full bg-gradient-to-r from-green-500 to-blue-500 text-white font-bold py-2 rounded hover:opacity-90 transition">
          {{ langStore.t('auth.login') }}
        </button>
        <div class="text-center mt-4">
            <router-link to="/forgot-password" class="text-sm text-gray-400 hover:text-white transition-colors duration-200">
                {{ langStore.t('auth.forgotPassword') }}
            </router-link>
        </div>
      </form>
      <p class="mt-4 text-sm text-gray-500 text-center">
        {{ langStore.t('auth.noAccount') }} <router-link to="/register" class="text-blue-400 hover:underline">{{ langStore.t('auth.registerHere') }}</router-link>
      </p>
    </div>
  </div>
</template>
