<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const token = route.query.token

const password = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const message = ref('')
const error = ref('')

async function handleSubmit() {
    if (password.value !== confirmPassword.value) {
        error.value = 'Las contraseñas no coinciden.'
        return
    }

    message.value = ''
    error.value = ''

    try {
        const res = await fetch(`${import.meta.env.VITE_API_URL}/api/auth/reset-password`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ token, newPassword: password.value })
        })

        if (res.ok) {
            message.value = 'Contraseña restablecida con éxito. Redirigiendo...'
            setTimeout(() => {
                router.push('/login')
            }, 3000)
        } else {
            const txt = await res.text()
            error.value = 'Error: ' + txt
        }
    } catch (e) {
        console.error(e)
        error.value = 'Error de conexión.'
    }
}
</script>

<template>
  <div class="flex justify-center items-center h-[80vh]">
    <div class="bg-gray-800 p-8 rounded-lg shadow-xl w-96 border border-gray-700">
      <h2 class="text-2xl font-bold mb-6 text-center text-white">Nueva Contraseña</h2>
      
      <div v-if="message" class="bg-green-600 text-white p-3 rounded mb-4 text-sm">
        {{ message }}
      </div>
      <div v-if="error" class="bg-red-600 text-white p-3 rounded mb-4 text-sm">
        {{ error }}
      </div>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div>
          <label class="block text-gray-400 mb-1">Nueva Contraseña</label>
          <div class="relative">
            <input v-model="password" :type="showPassword ? 'text' : 'password'" class="w-full bg-gray-700 text-white rounded p-2 pr-10 focus:outline-none focus:ring-2 focus:ring-green-500" placeholder="******" required />
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
        <div>
          <label class="block text-gray-400 mb-1">Confirmar Contraseña</label>
          <div class="relative">
            <input v-model="confirmPassword" :type="showConfirmPassword ? 'text' : 'password'" class="w-full bg-gray-700 text-white rounded p-2 pr-10 focus:outline-none focus:ring-2 focus:ring-green-500" placeholder="******" required />
            <button type="button" @click="showConfirmPassword = !showConfirmPassword" class="absolute inset-y-0 right-0 px-3 flex items-center text-gray-400 hover:text-white">
                <svg v-if="!showConfirmPassword" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
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
          Cambiar Contraseña
        </button>
      </form>
    </div>
  </div>
</template>
