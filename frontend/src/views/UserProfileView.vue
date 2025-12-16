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
    // Fetch user details from public ranking endpoint
    const rankingRes = await fetch(`${import.meta.env.VITE_API_URL}/api/users/ranking`, {
      headers: { 'Authorization': `Bearer ${auth.token}` }
    })
    if (rankingRes.ok) {
      const users = await rankingRes.json()
      user.value = users.find(u => u.id == userId)
      if (!user.value) {
        console.error('User not found in ranking')
        return
      }
    }

    // Try to fetch user bets (will only work if viewing own profile)
    const betsRes = await fetch(`${import.meta.env.VITE_API_URL}/api/bets/user/${userId}`, {
      headers: { 'Authorization': `Bearer ${auth.token}` }
    })
    if (betsRes.ok) {
      const fetchedBets = await betsRes.json()
      // Sort bets by date, most recent first
      bets.value = fetchedBets.sort((a, b) => {
        if (!a.placedAt && !b.placedAt) return 0
        if (!a.placedAt) return 1
        if (!b.placedAt) return -1
        return new Date(b.placedAt) - new Date(a.placedAt)
      })
    } else {
      // If unauthorized (viewing another user's profile), show message
      if (betsRes.status === 500 || betsRes.status === 403) {
        console.log('Cannot view other users\' bets (protected)')
        bets.value = [] // Empty array, will show "no bets" message
      }
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function formatBetDate(bet) {
  if (!bet.placedAt) {
    return 'Sin fecha'
  }
  try {
    const date = new Date(bet.placedAt)
    if (isNaN(date.getTime())) {
      return 'Sin fecha'
    }
    return date.toLocaleString()
  } catch (e) {
    return 'Sin fecha'
  }
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
        <div v-if="user.id == auth.user?.id">
          {{ langStore.t('profile.noBets') }}
        </div>
        <div v-else class="space-y-2">
          <p class="text-lg">🔒 Apuestas Privadas</p>
          <p class="text-sm text-gray-500">Las apuestas de este usuario son privadas por seguridad.</p>
        </div>
      </div>

      <div v-else class="space-y-4">
        <div v-for="bet in bets" :key="bet.id" class="bg-gray-800 p-6 rounded-lg shadow-lg border border-gray-700 flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div class="w-full">
            <div class="text-sm text-gray-400 mb-3">{{ formatBetDate(bet) }}</div>
            <div v-for="outcome in bet.outcomes" :key="outcome.id" class="mb-2 last:mb-0 border-b md:border-b-0 border-gray-700 pb-2 md:pb-0 last:border-0 last:pb-0">
              <div class="flex items-start gap-2">
                <span v-if="outcome.outcomeGroup" class="inline-block bg-purple-600 text-white text-xs font-bold px-2 py-0.5 rounded whitespace-nowrap flex-shrink-0">
                  {{ outcome.outcomeGroup }}
                </span>
                <h3 class="text-lg font-bold text-green-400 leading-tight">{{ outcome.description }}</h3>
              </div>
              <p class="text-gray-400 text-sm mt-1">
                {{ outcome.event.name }} 
                <span class="text-gray-500 block sm:inline">@ {{ outcome.odds }}</span>
              </p>
            </div>
          </div>
          <div class="w-full md:w-auto text-left md:text-right border-t md:border-t-0 border-gray-700 pt-4 md:pt-0 mt-2 md:mt-0 flex flex-row md:flex-col justify-between items-center md:items-end gap-x-4 flex-wrap">
            <div class="flex flex-col md:items-end">
              <p class="text-white font-bold text-lg">{{ bet.amount }} €</p>
              <p class="text-sm font-bold uppercase" :class="{
                'text-yellow-400': bet.status === 'PENDING',
                'text-green-500': bet.status === 'WON',
                'text-red-500': bet.status === 'LOST',
                'text-gray-500': bet.status === 'CANCELLED' || bet.status === 'VOID'
              }">
                {{ langStore.t('common.status.' + bet.status) }}
              </p>
            </div>
            
            <div class="flex flex-col md:items-end">
              <p v-if="bet.status === 'PENDING'" class="text-xs text-gray-500 text-right md:text-right">
                {{ langStore.t('betSlip.totalOdds') }}: {{ bet.outcomes.reduce((acc, curr) => acc * curr.odds, 1).toFixed(2) }}
              </p>
              <p v-if="bet.status === 'PENDING'" class="text-xs text-gray-500 text-right md:text-right">
                {{ langStore.t('betSlip.potentialReturn') }}: {{ calculatePotentialWin(bet) }} €
              </p>
              <p v-if="bet.status !== 'PENDING'" class="text-sm font-bold mt-1 text-right md:text-right text-green-400">
                Ganancia Total: {{ bet.winnings !== null ? bet.winnings : '0.00' }} €
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="text-center text-red-400">{{ langStore.t('profile.notFound') }}</div>
  </div>
</template>

