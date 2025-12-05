<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useLanguageStore } from '../stores/language'

const router = useRouter()
const langStore = useLanguageStore()
const username = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const error = ref('')

const register = async () => {
    if (password.value !== confirmPassword.value) {
        error.value = langStore.t('auth.passwordsDoNotMatch')
        return
    }

    try {
        const res = await fetch(`${import.meta.env.VITE_API_URL}/api/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                username: username.value,
                email: email.value,
                password: password.value
            })
        })

        if (res.ok) {
            router.push('/login')
        } else {
            const msg = await res.text()
            error.value = msg
        }
    } catch (e) {
        error.value = langStore.t('auth.registrationFailed')
    }
}
</script>

<template>
    <div class="min-h-screen flex items-center justify-center bg-gray-900 text-white">
        <div class="bg-gray-800 p-8 rounded-lg shadow-lg w-96">
            <h1 class="text-2xl font-bold mb-6 text-center">{{ langStore.t('auth.register') }}</h1>
            <form @submit.prevent="register" class="space-y-4">
                <div>
                    <label class="block text-sm font-medium mb-1">{{ langStore.t('auth.username') }}</label>
                    <input v-model="username" type="text" required
                        class="w-full px-3 py-2 bg-gray-700 rounded border border-gray-600 focus:border-blue-500 focus:outline-none" />
                </div>
                <div>
                    <label class="block text-sm font-medium mb-1">{{ langStore.t('auth.email') }}</label>
                    <input v-model="email" type="email" required
                        class="w-full px-3 py-2 bg-gray-700 rounded border border-gray-600 focus:border-blue-500 focus:outline-none" />
                </div>
                <div>
                    <label class="block text-sm font-medium mb-1">{{ langStore.t('auth.password') }}</label>
                    <input v-model="password" type="password" required
                        class="w-full px-3 py-2 bg-gray-700 rounded border border-gray-600 focus:border-blue-500 focus:outline-none" />
                </div>
                <div>
                    <label class="block text-sm font-medium mb-1">{{ langStore.t('auth.confirmPassword') }}</label>
                    <input v-model="confirmPassword" type="password" required
                        class="w-full px-3 py-2 bg-gray-700 rounded border border-gray-600 focus:border-blue-500 focus:outline-none" />
                </div>
                <div v-if="error" class="text-red-500 text-sm text-center">{{ error }}</div>
                <button type="submit"
                    class="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded transition">
                    {{ langStore.t('auth.register') }}
                </button>
            </form>
            <div class="mt-4 text-center text-sm">
                <span class="text-gray-400">{{ langStore.t('auth.hasAccount') }}</span>
                <router-link to="/login" class="text-blue-400 hover:underline ml-1">{{ langStore.t('auth.loginHere') }}</router-link>
            </div>
        </div>
    </div>
</template>
