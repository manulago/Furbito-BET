<script setup>
import { ref, onMounted, computed } from 'vue'
import { useLanguageStore } from '../stores/language'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const langStore = useLanguageStore()
const bets = ref([])
const activeTab = ref('active')
const filterStatus = ref('ALL')

async function fetchBets() {
  if (!auth.user) return
  const res = await fetch(`${import.meta.env.VITE_API_URL}/api/bets/user/${auth.user.id}`, {
      headers: { 'Authorization': `Bearer ${auth.token}` }
  })
  if (res.ok) {
    bets.value = await res.json()
  }
}

const filteredBets = computed(() => {
  if (activeTab.value === 'active') {
    return bets.value.filter(b => b.status === 'PENDING')
  } else {
    let finished = bets.value.filter(b => ['WON', 'LOST', 'CANCELLED', 'VOID'].includes(b.status))
    if (filterStatus.value !== 'ALL') {
      finished = finished.filter(b => b.status === filterStatus.value)
    }
    return finished
  }
})

async function cancelBet(betId) {
  if (!confirm('Are you sure you want to cancel this bet?')) return
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/bets/${betId}/cancel?userId=${auth.user.id}`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${auth.token}` }
    })
    if (res.ok) {
      alert('Bet cancelled and refunded!')
      fetchBets()
      auth.fetchBalance()
    } else {
      const msg = await res.text()
      alert('Failed to cancel: ' + msg)
    }
  } catch (e) {
    console.error(e)
    alert('Error cancelling bet')
  }
}

onMounted(fetchBets)
</script>

<template>
  <div class="space-y-8">
    <div class="flex flex-col md:flex-row justify-between items-center gap-4">
      <h2 class="text-3xl font-bold text-white">{{ langStore.t('nav.myBets') }}</h2>
      <div class="flex items-center gap-4">
        <div class="flex bg-gray-700 rounded-lg p-1">
          <button @click="activeTab = 'active'" 
            :class="['px-4 py-2 rounded-md text-sm font-bold transition', activeTab === 'active' ? 'bg-gray-600 text-white shadow' : 'text-gray-400 hover:text-white']">
            {{ langStore.t('common.active') }}
          </button>
          <button @click="activeTab = 'finished'" 
            :class="['px-4 py-2 rounded-md text-sm font-bold transition', activeTab === 'finished' ? 'bg-gray-600 text-white shadow' : 'text-gray-400 hover:text-white']">
            {{ langStore.t('common.finished') }}
          </button>
        </div>

        <select v-if="activeTab === 'finished'" v-model="filterStatus" class="bg-gray-700 text-white text-sm p-2 rounded border border-gray-600 focus:border-green-500 outline-none">
          <option value="ALL">{{ langStore.t('common.all') }}</option>
          <option value="WON">{{ langStore.t('common.status.WON') }}</option>
          <option value="LOST">{{ langStore.t('common.status.LOST') }}</option>
          <option value="CANCELLED">{{ langStore.t('common.status.CANCELLED') }}</option>
        </select>
      </div>
    </div>

    <div v-if="filteredBets.length === 0" class="text-gray-400 text-center py-10">
      {{ langStore.t('myBets.noBets') }}
    </div>

    <div v-else class="grid gap-4">
      <div v-for="bet in filteredBets" :key="bet.id" class="bg-gray-800 p-6 rounded-lg shadow-lg border border-gray-700 flex justify-between items-center">
        <div>
          <div v-for="outcome in bet.outcomes" :key="outcome.id" class="mb-2">
            <h3 class="text-lg font-bold text-green-400">{{ outcome.description }}</h3>
            <p class="text-gray-400 text-sm">{{ outcome.event.name }} <span class="text-gray-500">@ {{ outcome.odds }}</span></p>
          </div>
        </div>
        <div class="text-right">
          <p class="text-white font-bold text-lg">{{ bet.amount }} €</p>
          <p class="text-sm font-bold uppercase" :class="{
            'text-yellow-400': bet.status === 'PENDING',
            'text-green-500': bet.status === 'WON',
            'text-red-500': bet.status === 'LOST',
            'text-gray-500': bet.status === 'CANCELLED' || bet.status === 'VOID'
          }">
            {{ langStore.t('common.status.' + bet.status) }}
          </p>
          <p v-if="bet.status === 'PENDING'" class="text-xs text-gray-500">
            {{ langStore.t('betSlip.totalOdds') }}: {{ bet.outcomes.reduce((acc, curr) => acc * curr.odds, 1).toFixed(2) }}
          </p>
          <p v-if="bet.status === 'PENDING'" class="text-xs text-gray-500">
            {{ langStore.t('betSlip.potentialReturn') }}: {{ (bet.amount * bet.outcomes.reduce((acc, curr) => acc * curr.odds, 1)).toFixed(2) }} €
          </p>
          <button v-if="bet.status === 'PENDING'" @click="cancelBet(bet.id)" class="mt-2 bg-red-500 hover:bg-red-600 text-white text-xs font-bold py-1 px-2 rounded transition">
            {{ langStore.t('myBets.cancel') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
