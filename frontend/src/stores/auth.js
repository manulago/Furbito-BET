import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
    const user = ref(JSON.parse(localStorage.getItem('user')) || null)

    const token = ref(localStorage.getItem('token') || null)

    function login(username, role, id, newToken) {
        user.value = { username, role, id }
        token.value = newToken
        localStorage.setItem('user', JSON.stringify(user.value))
        localStorage.setItem('token', newToken)
    }

    function logout() {
        user.value = null
        token.value = null
        localStorage.removeItem('user')
        localStorage.removeItem('token')
    }

    async function fetchBalance() {
        if (!user.value) return
        try {
            const res = await fetch(`${import.meta.env.VITE_API_URL}/api/users/${user.value.id}`, {
                headers: { 'Authorization': `Bearer ${token.value}` }
            })
            if (res.ok) {
                const userData = await res.json()
                user.value.balance = userData.balance
                localStorage.setItem('user', JSON.stringify(user.value))
            }
        } catch (e) {
            console.error('Failed to fetch balance', e)
        }
    }

    return { user, token, login, logout, fetchBalance }
})
