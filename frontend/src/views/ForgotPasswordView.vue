<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const email = ref('')
const message = ref('')
const error = ref('')
const router = useRouter()

async function handleSubmit() {
  message.value = ''
  error.value = ''
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/auth/forgot-password`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: email.value })
    })

    if (res.ok) {
        // Even if email doesn't exist, we usually say "If account exists..." for security, 
        // but the backend returns a text we can show or just a generic success message.
        message.value = 'Si el correo existe en nuestra base de datos, recibirás un enlace para restablecer tu contraseña.'
    } else {
        error.value = 'Ocurrió un error. Inténtalo de nuevo.'
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
      <h2 class="text-2xl font-bold mb-6 text-center text-white">Recuperar Contraseña</h2>
      
      <div v-if="message" class="bg-green-600 text-white p-3 rounded mb-4 text-sm">
        {{ message }}
      </div>
      <div v-if="error" class="bg-red-600 text-white p-3 rounded mb-4 text-sm">
        {{ error }}
      </div>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div>
          <label class="block text-gray-400 mb-1">Correo Electrónico</label>
          <input v-model="email" type="email" class="w-full bg-gray-700 text-white rounded p-2 focus:outline-none focus:ring-2 focus:ring-green-500" placeholder="tu@email.com" required />
        </div>
        <button type="submit" class="w-full bg-gradient-to-r from-green-500 to-blue-500 text-white font-bold py-2 rounded hover:opacity-90 transition">
          Enviar Enlace
        </button>
      </form>
      <div class="mt-4 text-center">
        <router-link to="/login" class="text-blue-400 hover:underline text-sm">Volver al inicio de sesión</router-link>
      </div>
    </div>
  </div>
</template>
