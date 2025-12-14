<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useLanguageStore } from '../stores/language'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const langStore = useLanguageStore()
const router = useRouter()

const formData = ref({
  username: '',
  email: '',
  password: ''
})

const loading = ref(false)
const message = ref('')
const error = ref('')
const showPassword = ref(false)

onMounted(async () => {
    if (!auth.user) {
        router.push('/login')
        return
    }

    // Safety check: fetch fresh user data including email
    try {
        const res = await fetch(`${import.meta.env.VITE_API_URL}/api/users/${auth.user.id}`, {
             headers: { 'Authorization': `Bearer ${auth.token}` }
        })
        if (res.ok) {
            const userData = await res.json()
            formData.value.username = userData.username
            formData.value.email = userData.email
        }
    } catch(e) {
        console.error(e)
    }
})

async function submitChanges() {
    loading.value = true
    message.value = ''
    error.value = ''

    try {
        const payload = {
            username: formData.value.username !== auth.user.username ? formData.value.username : null,
            email: formData.value.email, 
            password: formData.value.password || null
        }

        const res = await fetch(`${import.meta.env.VITE_API_URL}/api/users/${auth.user.id}/request-update`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${auth.token}`
            },
            body: JSON.stringify(payload)
        })

        if (res.ok) {
            message.value = 'Solicitud enviada. Por favor, revisa tu correo electrónico para confirmar los cambios (enlace válido por 24h).'
            formData.value.password = '' // clear password field
            showPassword.value = false // reset password visibility
        } else {
            const text = await res.text()
            try {
                const json = JSON.parse(text)
                error.value = json.message || json.error || 'Error al actualizar perfil'
            } catch {
                error.value = text || 'Error al actualizar perfil'
            }
        }
    } catch (e) {
        error.value = 'Error de conexión'
    } finally {
        loading.value = false
    }
}
</script>

<template>
  <div class="max-w-md mx-auto mt-10 p-6 bg-gray-800 rounded-lg shadow-xl border border-gray-700">
    <h2 class="text-2xl font-bold text-white mb-6 text-center">Gestionar Perfil</h2>
    
    <div v-if="message" class="mb-4 p-4 bg-green-500/20 border border-green-500 rounded text-green-200 text-sm">
        {{ message }}
    </div>

    <div v-if="error" class="mb-4 p-4 bg-red-500/20 border border-red-500 rounded text-red-200 text-sm">
        {{ error }}
    </div>

    <form @submit.prevent="submitChanges" class="space-y-4">
      <div>
        <label class="block text-sm font-bold text-gray-400 mb-1">Nombre de Usuario</label>
        <input v-model="formData.username" type="text" required class="w-full bg-gray-900 border border-gray-600 rounded p-2 text-white focus:border-green-500 focus:outline-none" />
      </div>

      <div>
        <label class="block text-sm font-bold text-gray-400 mb-1">Email</label>
        <input v-model="formData.email" type="email" required class="w-full bg-gray-900 border border-gray-600 rounded p-2 text-white focus:border-green-500 focus:outline-none" />
      </div>

      <div>
        <label class="block text-sm font-bold text-gray-400 mb-1">Nueva Contraseña (Opcional)</label>
        <div class="relative">
            <input v-model="formData.password" :type="showPassword ? 'text' : 'password'" placeholder="Dejar en blanco para mantener la actual" class="w-full bg-gray-900 border border-gray-600 rounded p-2 text-white focus:border-green-500 focus:outline-none pr-10" />
            <button type="button" @click="showPassword = !showPassword" class="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-200 focus:outline-none">
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

      <button type="submit" :disabled="loading" class="w-full bg-green-500 hover:bg-green-600 text-white font-bold py-2 rounded transition disabled:opacity-50">
        {{ loading ? 'Enviando...' : 'Guardar Cambios' }}
      </button>
    </form>
  </div>
</template>
