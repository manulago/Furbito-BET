<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const status = ref('loading') // loading, success, error
const message = ref('')

onMounted(async () => {
    const token = route.query.token
    if (!token) {
        status.value = 'error'
        message.value = 'Token no proporcionado'
        return
    }

    try {
        const res = await fetch(`${import.meta.env.VITE_API_URL}/api/users/confirm-update`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ token })
        })

        if (res.ok) {
            status.value = 'success'
            message.value = 'Cambios confirmados exitosamente. Por favor, inicia sesión nuevamente si cambiaste tu contraseña o usuario.'
            // Optional: Logout if critical changes
            // auth.logout() 
        } else {
            const text = await res.text()
            status.value = 'error'
            try {
                const json = JSON.parse(text)
                message.value = json.message || json.error || 'Error desconocido'
            } catch {
                message.value = text
            }
        }
    } catch (e) {
        status.value = 'error'
        message.value = 'Error de conexión'
    }
})
</script>

<template>
  <div class="max-w-md mx-auto mt-20 p-8 bg-gray-800 rounded-lg shadow-xl text-center border border-gray-700">
    <div v-if="status === 'loading'" class="text-white">
        <h2 class="text-2xl font-bold mb-4">Verificando...</h2>
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-green-500 mx-auto"></div>
    </div>

    <div v-if="status === 'success'" class="text-green-400">
        <div class="text-6xl mb-4">✅</div>
        <h2 class="text-2xl font-bold mb-2">¡Confirmado!</h2>
        <p class="text-gray-300 mb-6">{{ message }}</p>
        <router-link to="/login" class="inline-block bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-6 rounded transition">
            Iniciar Sesión
        </router-link>
    </div>

    <div v-if="status === 'error'" class="text-red-400">
        <div class="text-6xl mb-4">❌</div>
        <h2 class="text-2xl font-bold mb-2">Error</h2>
        <p class="text-gray-300 mb-6">{{ message }}</p>
        <router-link to="/" class="inline-block bg-gray-600 hover:bg-gray-500 text-white font-bold py-2 px-6 rounded transition">
            Volver al Inicio
        </router-link>
    </div>
  </div>
</template>
