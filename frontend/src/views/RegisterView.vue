<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const error = ref('')

const register = async () => {
    if (password.value !== confirmPassword.value) {
        error.value = "Passwords do not match!"
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
        error.value = "Registration failed"
    }
}
</script>

<template>
    <div class="min-h-screen flex items-center justify-center bg-gray-900 text-white">
        <div class="bg-gray-800 p-8 rounded-lg shadow-lg w-96">
            <h1 class="text-2xl font-bold mb-6 text-center">Register</h1>
            <form @submit.prevent="register" class="space-y-4">
                <div>
                    <label class="block text-sm font-medium mb-1">Username</label>
                    <input v-model="username" type="text" required
                        class="w-full px-3 py-2 bg-gray-700 rounded border border-gray-600 focus:border-blue-500 focus:outline-none" />
                </div>
                <div>
                    <label class="block text-sm font-medium mb-1">Email</label>
                    <input v-model="email" type="email" required
                        class="w-full px-3 py-2 bg-gray-700 rounded border border-gray-600 focus:border-blue-500 focus:outline-none" />
                </div>
                <div>
                    <label class="block text-sm font-medium mb-1">Password</label>
                    <input v-model="password" type="password" required
                        class="w-full px-3 py-2 bg-gray-700 rounded border border-gray-600 focus:border-blue-500 focus:outline-none" />
                </div>
                <div>
                    <label class="block text-sm font-medium mb-1">Confirm Password</label>
                    <input v-model="confirmPassword" type="password" required
                        class="w-full px-3 py-2 bg-gray-700 rounded border border-gray-600 focus:border-blue-500 focus:outline-none" />
                </div>
                <div v-if="error" class="text-red-500 text-sm text-center">{{ error }}</div>
                <button type="submit"
                    class="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded transition">
                    Register
                </button>
            </form>
            <div class="mt-4 text-center text-sm">
                <router-link to="/login" class="text-blue-400 hover:underline">Already have an account? Login</router-link>
            </div>
        </div>
    </div>
</template>
