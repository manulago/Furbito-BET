<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useLanguageStore } from '../stores/language'

const route = useRoute()
const router = useRouter()
const langStore = useLanguageStore()
const status = ref('loading') // loading, success, error
const errorMessage = ref('')

onMounted(async () => {
  const token = route.query.token
  if (!token) {
    status.value = 'error'
    errorMessage.value = langStore.t('auth.invalidToken')
    return
  }

  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/auth/confirm-account?token=${token}`)
    
    if (res.ok) {
        status.value = 'success'
    } else {
        const msg = await res.text()
        status.value = 'error'
        errorMessage.value = msg || langStore.t('auth.confirmationFailed')
    }
  } catch (e) {
    status.value = 'error'
    errorMessage.value = "Connection error"
  }
})
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-900 section-bg text-white p-4">
    <div class="bg-gray-800 p-8 rounded-lg shadow-xl w-full max-w-md border border-gray-700 animate-fade-in text-center">
      
      <div v-if="status === 'loading'">
         <div class="animate-spin h-10 w-10 border-4 border-blue-500 border-t-transparent rounded-full mx-auto mb-4"></div>
         <p class="text-xl font-bold">{{ langStore.t('auth.verifying') }}...</p>
      </div>

      <div v-else-if="status === 'success'">
         <div class="text-6xl mb-4">✅</div>
         <h2 class="text-2xl font-bold mb-4 text-green-400">{{ langStore.t('auth.accountVerified') }}</h2>
         <p class="text-gray-300 mb-6">{{ langStore.t('auth.accountVerifiedDesc') }}</p>
         <router-link to="/login" class="inline-block w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-4 rounded transition">
            {{ langStore.t('auth.loginNow') }}
         </router-link>
      </div>

      <div v-else-if="status === 'error'">
         <div class="text-6xl mb-4">❌</div>
         <h2 class="text-2xl font-bold mb-4 text-red-500">{{ langStore.t('auth.error') }}</h2>
         <p class="text-gray-300 mb-6">{{ errorMessage }}</p>
         <router-link to="/" class="text-gray-400 hover:text-white underline">
            {{ langStore.t('nav.home') }}
         </router-link>
      </div>

    </div>
  </div>
</template>
