<script setup>
import { ref, onMounted } from 'vue'
import { useLanguageStore } from '../stores/language'

const langStore = useLanguageStore()
const events = ref([])
const loading = ref(true)
const selectedEvent = ref(null)
const eventResults = ref([])
const loadingResults = ref(false)
const selectedUserBets = ref(null)

async function fetchCompletedEvents() {
  loading.value = true
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/events`)
    const allEvents = await res.json()
    events.value = allEvents.filter(e => e.status === 'COMPLETED').sort((a, b) => new Date(b.date) - new Date(a.date))
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function showEventResults(event) {
  selectedEvent.value = event
  loadingResults.value = true
  eventResults.value = []
  selectedUserBets.value = null
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/events/${event.id}/winning-bets`)
    if (res.ok) {
      eventResults.value = await res.json()
    }
  } catch (e) {
    console.error(e)
  } finally {
    loadingResults.value = false
  }
}

function showUserBets(result) {
  selectedUserBets.value = result
}

function getNetResult(result) {
  return result.totalWon - result.totalWagered
}
</script>

<template>
  <div class="space-y-8">
    <h2 class="text-3xl font-bold text-white">{{ langStore.t('results.board') }}</h2>

    <div v-if="loading" class="text-center text-gray-400">{{ langStore.t('results.loading') }}</div>
    
    <div v-else-if="events.length === 0" class="text-center text-gray-400 bg-gray-800 p-8 rounded-lg">
      {{ langStore.t('results.noCompleted') }}
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="event in events" :key="event.id" 
           @click="showEventResults(event)"
           class="bg-gray-800 p-6 rounded-xl shadow-lg border border-gray-700 hover:border-green-500 cursor-pointer transition transform hover:-translate-y-1 relative overflow-hidden group">
        
        <div class="absolute top-0 right-0 bg-green-600 text-white text-xs font-bold px-3 py-1 rounded-bl-lg">
          {{ langStore.t('common.status.COMPLETED') }}
        </div>

        <div class="text-sm text-gray-400 mb-2">{{ new Date(event.date).toLocaleDateString() }}</div>
        <h3 class="text-xl font-bold text-white mb-4">{{ event.name }}</h3>
        
        <div class="text-green-400 font-bold text-sm group-hover:underline">
          {{ langStore.t('results.viewResults') }} &rarr;
        </div>
      </div>
    </div>

    <!-- Modal for Event Results -->
    <div v-if="selectedEvent" class="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50 p-4" @click.self="selectedEvent = null">
      <div class="bg-gray-800 p-6 rounded-lg shadow-xl border border-green-500 w-full max-w-4xl max-h-[90vh] overflow-y-auto flex flex-col">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-2xl font-bold text-white">{{ selectedEvent.name }} - {{ langStore.t('ranking.tabs.results') }}</h3>
          <button @click="selectedEvent = null" class="text-gray-400 hover:text-white text-2xl">&times;</button>
        </div>

        <div v-if="loadingResults" class="text-center text-gray-400 py-8">{{ langStore.t('results.loading') }}</div>

        <div v-else-if="eventResults.length === 0" class="text-center text-gray-400 py-8">
          {{ langStore.t('results.noBetsEvent') }}
        </div>

        <div v-else class="flex flex-col md:flex-row gap-6">
          <!-- User List -->
          <div class="flex-1 overflow-x-auto">
            <table class="w-full text-left text-gray-300">
              <thead class="text-gray-400 uppercase bg-gray-700">
                <tr>
                  <th class="px-4 py-2">{{ langStore.t('results.user') }}</th>
                  <th class="px-4 py-2 text-right">{{ langStore.t('results.wagered') }}</th>
                  <th class="px-4 py-2 text-right">{{ langStore.t('results.won') }}</th>
                  <th class="px-4 py-2 text-right">{{ langStore.t('results.net') }}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(result, index) in eventResults" :key="index" 
                    @click="showUserBets(result)"
                    class="border-b border-gray-700 hover:bg-gray-700 cursor-pointer transition-colors"
                    :class="{'bg-gray-700': selectedUserBets && selectedUserBets.username === result.username}">
                  <td class="px-4 py-2 font-bold text-white">{{ result.username }}</td>
                  <td class="px-4 py-2 text-right">{{ result.totalWagered.toFixed(2) }} €</td>
                  <td class="px-4 py-2 text-right text-green-400">{{ result.totalWon.toFixed(2) }} €</td>
                  <td class="px-4 py-2 text-right font-bold" 
                      :class="getNetResult(result) >= 0 ? 'text-green-400' : 'text-red-500'">
                    {{ getNetResult(result) > 0 ? '+' : '' }}{{ getNetResult(result).toFixed(2) }} €
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Selected User Bets Details -->
          <div v-if="selectedUserBets" class="w-full md:w-1/3 bg-gray-900 p-4 rounded-lg border border-gray-700">
            <h4 class="text-lg font-bold text-white mb-4 border-b border-gray-700 pb-2">
              {{ langStore.t('results.betsBy') }} {{ selectedUserBets.username }}
            </h4>
            <div class="space-y-4 max-h-[60vh] overflow-y-auto pr-2">
              <div v-for="bet in selectedUserBets.bets" :key="bet.id" class="bg-gray-800 p-3 rounded border border-gray-700">
                <div class="flex justify-between text-sm mb-2">
                  <span class="text-gray-400">{{ langStore.t('results.bet') }} #{{ bet.id }}</span>
                  <span class="font-bold" :class="{
                    'text-green-400': bet.status === 'WON',
                    'text-red-500': bet.status === 'LOST',
                    'text-yellow-500': bet.status === 'PENDING',
                    'text-gray-400': bet.status === 'VOID' || bet.status === 'CANCELLED'
                  }">{{ langStore.t('common.status.' + bet.status) }}</span>
                </div>
                <div class="text-sm text-gray-300 mb-2">
                  <ul class="list-disc list-inside">
                    <li v-for="(outcome, idx) in bet.outcomes" :key="idx" class="truncate">
                      {{ outcome }}
                    </li>
                  </ul>
                </div>
                <div class="flex justify-between text-sm border-t border-gray-700 pt-2 mt-2">
                  <span>{{ langStore.t('results.wager') }}: {{ bet.amount.toFixed(2) }} €</span>
                  <span v-if="bet.status === 'WON'" class="text-green-400 font-bold">{{ langStore.t('results.won') }}: {{ bet.potentialWinnings.toFixed(2) }} €</span>
                  <span v-else class="text-gray-500">{{ langStore.t('results.potWin') }}: {{ bet.potentialWinnings.toFixed(2) }} €</span>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="hidden md:flex w-1/3 items-center justify-center text-gray-500 text-sm italic border border-gray-700 rounded-lg bg-gray-900">
            {{ langStore.t('results.selectUser') }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
