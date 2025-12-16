import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
    const user = ref(JSON.parse(localStorage.getItem('user')) || null)

    const token = ref(localStorage.getItem('token') || null)

    function login(username, role, id, newToken, balance) {
        user.value = { username, role, id, balance }
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
            // Use the ranking endpoint which is public and includes balance
            const res = await fetch(`${import.meta.env.VITE_API_URL}/api/users/ranking`, {
                headers: { 'Authorization': `Bearer ${token.value}` }
            })
            if (res.ok) {
                const users = await res.json()
                const currentUser = users.find(u => u.id === user.value.id)
                if (currentUser) {
                    user.value.balance = currentUser.balance
                    localStorage.setItem('user', JSON.stringify(user.value))
                }
            }
        } catch (e) {
            console.error('Failed to fetch balance', e)
        }
    }

    return { user, token, login, logout, fetchBalance }
})
