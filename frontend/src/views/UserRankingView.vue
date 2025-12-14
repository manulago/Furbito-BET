<script setup>
import { ref, onMounted } from 'vue'
import { useLanguageStore } from '../stores/language'
import { useAuthStore } from '../stores/auth'

const langStore = useLanguageStore()
const auth = useAuthStore()
const userRanking = ref([])
const loading = ref(true)
const error = ref(null)

async function fetchData() {
  loading.value = true
  error.value = null
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/users/ranking`)
    if (!res.ok) throw new Error('Failed to fetch users')
    userRanking.value = await res.json()
  } catch (e) {
    console.error('Error fetching data:', e)
    error.value = langStore.t('ranking.error') + ' (' + e.message + ')'
  } finally {
    loading.value = false
  }
}

function getRank(index) {
  if (index === 0) return 1
  const currentUser = userRanking.value[index]
  const prevUser = userRanking.value[index - 1]
  
  if (currentUser.balance === prevUser.balance) {
    let i = index - 1
    while (i >= 0 && userRanking.value[i].balance === currentUser.balance) {
      i--
    }
    return i + 2
  }
  return index + 1
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="space-y-8">
    <div class="flex justify-between items-center">
      <h2 class="text-3xl font-bold text-white">{{ langStore.t('ranking.users.title') || 'Clasificación de Usuarios' }}</h2>
    </div>

    <div v-if="loading" class="text-center text-gray-400">
      {{ langStore.t('ranking.loading') }}
    </div>

    <div v-else-if="error" class="text-center text-red-400">
      {{ error }}
    </div>

    <div v-else class="bg-gray-800 p-6 rounded-lg shadow-lg border border-gray-700 overflow-x-auto">
      <table class="w-full text-left text-gray-300">
        <thead class="text-gray-400 uppercase bg-gray-700">
          <tr>
            <th class="px-4 py-2">{{ langStore.t('ranking.users.pos') }}</th>
            <th class="px-4 py-2">{{ langStore.t('ranking.users.user') }}</th>
            <th class="px-4 py-2 text-right">{{ langStore.t('ranking.users.balance') }}</th>
            <th class="px-4 py-2 text-right">Ganancia</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(user, index) in userRanking" :key="user.id" class="border-b border-gray-700 hover:bg-gray-700">
            <td class="px-4 py-2 font-bold">{{ getRank(index) }}</td>
            <td class="px-4 py-2 text-white font-medium">
              <router-link :to="'/user/' + user.id" class="hover:text-green-400 hover:underline transition">
                {{ user.username }}
              </router-link>
            </td>
            <td class="px-4 py-2 text-right font-bold text-green-400">{{ user.balance }} €</td>
            <td class="px-4 py-2 text-right font-bold text-green-400">{{ user.grossProfit }} €</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
