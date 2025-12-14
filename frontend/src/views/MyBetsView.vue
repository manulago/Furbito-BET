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

onMounted(fetchBets)

function isBetCancellable(bet) {
  if (bet.status !== 'PENDING') return false
  const now = new Date()
  // Check if ANY outcome event has started
  return !bet.outcomes.some(o => {
      // Assuming event date is available in outcome.event.date
      // The backend DTO usually contains the full Event object in Outcome
      if (o.event && o.event.date) {
          return new Date(o.event.date) < now
      }
      return false
  })
}

function getNetProfitValue(bet) {
  if (bet.status === 'WON') {
    // If we had potentialWinnings in DTO we could use it, but calculating is consistent with PENDING view
    const totalOdds = bet.outcomes.reduce((acc, curr) => acc * curr.odds, 1)
    return (bet.amount * totalOdds) - bet.amount
  } else if (bet.status === 'LOST') {
    return -bet.amount
  }
  return 0
}
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
      <div v-for="bet in filteredBets" :key="bet.id" class="bg-gray-800 p-6 rounded-lg shadow-lg border border-gray-700 flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div class="w-full">
          <div v-for="outcome in bet.outcomes" :key="outcome.id" class="mb-2 last:mb-0 border-b md:border-b-0 border-gray-700 pb-2 md:pb-0 last:border-0 last:pb-0">
            <h3 class="text-lg font-bold text-green-400 leading-tight">{{ outcome.description }}</h3>
            <p class="text-gray-400 text-sm">{{ outcome.event.name }} <span class="text-gray-500 block sm:inline">@ {{ outcome.odds }}</span></p>
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
                {{ langStore.t('betSlip.potentialReturn') }}: {{ (bet.amount * bet.outcomes.reduce((acc, curr) => acc * curr.odds, 1)).toFixed(2) }} €
              </p>
              <p v-if="bet.status !== 'PENDING'" class="text-sm font-bold mt-1 text-right md:text-right" :class="{
                 'text-green-400': getNetProfitValue(bet) > 0,
                 'text-red-500': getNetProfitValue(bet) < 0,
                 'text-gray-500': getNetProfitValue(bet) === 0
              }">
                 {{ langStore.t('myBets.netProfit') }}: {{ getNetProfitValue(bet) > 0 ? '+' : '' }}{{ getNetProfitValue(bet).toFixed(2) }} €
              </p>
              <button v-if="isBetCancellable(bet)" @click="cancelBet(bet.id)" class="mt-2 bg-red-500 hover:bg-red-600 text-white text-xs font-bold py-1 px-3 rounded transition self-end">
                {{ langStore.t('myBets.cancel') }}
              </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
