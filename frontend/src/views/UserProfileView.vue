<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useLanguageStore } from '../stores/language'

const route = useRoute()
const auth = useAuthStore()
const langStore = useLanguageStore()
const userId = route.params.id
const user = ref(null)
const bets = ref([])
const loading = ref(true)

async function fetchData() {
  loading.value = true
  try {
    // Fetch user details
    const userRes = await fetch(`${import.meta.env.VITE_API_URL}/api/users/${userId}`, {
      headers: { 'Authorization': `Bearer ${auth.token}` }
    })
    if (userRes.ok) {
      user.value = await userRes.json()
    }

    // Fetch user bets
    const betsRes = await fetch(`${import.meta.env.VITE_API_URL}/api/bets/user/${userId}`, {
      headers: { 'Authorization': `Bearer ${auth.token}` }
    })
    if (betsRes.ok) {
      bets.value = await betsRes.json()
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function formatOutcome(bet) {
  return bet.outcomes.map(o => `${o.event.name}: ${o.description} (@${o.odds})`).join(', ')
}

function calculatePotentialWin(bet) {
  const totalOdds = bet.outcomes.reduce((acc, o) => acc * o.odds, 1)
  return (bet.amount * totalOdds).toFixed(2)
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="max-w-4xl mx-auto space-y-8">
    <div v-if="loading" class="text-center text-gray-400">{{ langStore.t('profile.loading') }}</div>
    
    <div v-else-if="user" class="space-y-6">
      <div class="bg-gray-800 p-6 rounded-lg shadow-lg border border-gray-700 flex items-center gap-4">
        <div class="bg-purple-600 w-16 h-16 rounded-full flex items-center justify-center text-2xl font-bold text-white uppercase">
          {{ user.username.charAt(0) }}
        </div>
        <div>
          <h2 class="text-3xl font-bold text-white">{{ user.username }}</h2>
          <p class="text-gray-400">{{ langStore.t('profile.balance') }}: <span class="text-green-400 font-bold">{{ user.balance }} €</span></p>
        </div>
      </div>

      <h3 class="text-2xl font-bold text-white">{{ langStore.t('profile.history') }}</h3>
      
      <div v-if="bets.length === 0" class="text-gray-400 text-center py-8 bg-gray-800 rounded-lg">
        {{ langStore.t('profile.noBets') }}
      </div>

      <div v-else class="space-y-4">
        <div v-for="bet in bets" :key="bet.id" class="bg-gray-800 p-4 rounded-lg shadow border border-gray-700">
          <div class="flex justify-between items-start mb-2">
            <div>
              <span class="text-sm text-gray-400">{{ new Date(bet.placedAt).toLocaleString() }}</span>
              <div class="font-bold text-white mt-1">{{ formatOutcome(bet) }}</div>
            </div>
            <span class="px-2 py-1 rounded text-xs font-bold uppercase" :class="{
              'bg-yellow-900 text-yellow-400': bet.status === 'PENDING',
              'bg-green-900 text-green-400': bet.status === 'WON',
              'bg-red-900 text-red-400': bet.status === 'LOST',
              'bg-gray-700 text-gray-400': bet.status === 'VOID' || bet.status === 'CANCELLED'
            }">{{ langStore.t('common.status.' + bet.status) }}</span>
          </div>
          <div class="flex justify-between items-center text-sm border-t border-gray-700 pt-2 mt-2">
            <span class="text-gray-300">{{ langStore.t('profile.wager') }}: <span class="font-bold text-white">{{ bet.amount }} €</span></span>
            
            <span v-if="bet.status === 'PENDING'" class="text-gray-300">
              {{ langStore.t('profile.potWin') }}: <span class="font-bold text-green-400">{{ calculatePotentialWin(bet) }} €</span>
            </span>
            <span v-else class="text-gray-300">
              Ganancia Total: <span class="font-bold text-green-400">{{ bet.winnings !== null ? bet.winnings : '0.00' }} €</span>
            </span>

          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="text-center text-red-400">{{ langStore.t('profile.notFound') }}</div>
  </div>
</template>

