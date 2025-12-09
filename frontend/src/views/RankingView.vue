<script setup>
import { ref, onMounted, computed } from 'vue'
import { useLanguageStore } from '../stores/language'
import { useAuthStore } from '../stores/auth'

const langStore = useLanguageStore()
const auth = useAuthStore()
const standings = ref([])
const matchResults = ref([])
const userRanking = ref([])
const loading = ref(true)
const activeTab = ref('standings') // 'standings', 'results', 'users'
const onlyFurbitoFic = ref(false)

const error = ref(null)

async function fetchData() {
  console.log('Fetching data...')
  loading.value = true
  error.value = null
  try {
    const [standingsRes, resultsRes, usersRes] = await Promise.all([
      fetch(`${import.meta.env.VITE_API_URL}/api/league/standings`),
      fetch(`${import.meta.env.VITE_API_URL}/api/league/results`),
      fetch(`${import.meta.env.VITE_API_URL}/api/users/ranking`)
    ])

    console.log('Responses received', standingsRes.status, resultsRes.status, usersRes.status)

    if (!resultsRes.ok) throw new Error('Failed to fetch results')
    if (!standingsRes.ok) throw new Error('Failed to fetch standings')
    if (!usersRes.ok) throw new Error('Failed to fetch users')

    standings.value = await standingsRes.json()
    matchResults.value = await resultsRes.json() || [] 
    userRanking.value = await usersRes.json()
    console.log('Data loaded', standings.value.length, matchResults.value.length, userRanking.value.length)
  } catch (e) {
    console.error('Error fetching data:', e)
    error.value = langStore.t('ranking.error') + ' (' + e.message + ')'
    matchResults.value = [] 
  } finally {
    loading.value = false
  }
}

function formatTeamName(name) {
  return name === 'INFORMÁTICA-1' ? 'Furbito FIC' : name
}

const filteredMatchResults = computed(() => {
  if (!Array.isArray(matchResults.value)) return []
  
  if (!onlyFurbitoFic.value) {
    return matchResults.value
  }
  return matchResults.value.filter(match => 
    match.homeTeam === 'INFORMÁTICA-1' || match.awayTeam === 'INFORMÁTICA-1'
  )
})

const groupedMatchResults = computed(() => {
  if (!filteredMatchResults.value.length) return []
  
  const groups = {}
  filteredMatchResults.value.forEach(match => {
    const day = match.matchDay || langStore.t('ranking.results.unknown')
    if (!groups[day]) {
      groups[day] = []
    }
    groups[day].push(match)
  })
  return Object.keys(groups).map(day => ({
    name: day,
    matches: groups[day]
  }))
})

    async function addEventFromMatch(match) {
  if (!confirm(`${langStore.t('ranking.admin.confirmCreate')} ${match.homeTeam} vs ${match.awayTeam}?`)) return

  // Parse date: "18/11/2025 - 14:00" -> "2025-11-18T14:00:00"
  try {
    const [datePart, timePart] = match.dateTime.split(' - ')
    const [day, month, year] = datePart.split('/')
    const isoDate = `${year}-${month}-${day}T${timePart}:00`

    const eventName = `${match.homeTeam} vs ${match.awayTeam}`
    
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/admin/events`, {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ name: eventName, date: isoDate })
    })

    if (res.ok) {
      alert(langStore.t('ranking.admin.success'))
    } else {
      alert(langStore.t('ranking.admin.fail'))
    }
  } catch (e) {
    console.error(e)
    alert('Error creating event: ' + e.message)
  }
}

onMounted(() => {
  fetchData()
})

function getRank(index) {
  if (index === 0) return 1
  const currentUser = userRanking.value[index]
  const prevUser = userRanking.value[index - 1]
  
  if (currentUser.balance === prevUser.balance) {
    // Recursive check to find the first user with this balance
    let i = index - 1
    while (i >= 0 && userRanking.value[i].balance === currentUser.balance) {
      i--
    }
    return i + 2 // i is the index of the user with DIFFERENT balance, so i+1 is the first user with SAME balance. Rank is 1-based.
  }
  return index + 1
}
</script>

<template>
  <div class="space-y-8">
    <div class="flex flex-col md:flex-row justify-between items-center gap-4 md:gap-0">
      <h2 class="text-3xl font-bold text-white">{{ langStore.t('ranking.title') }}</h2>
      <div class="flex space-x-2 md:space-x-4 bg-gray-800 p-1 rounded-lg overflow-x-auto max-w-full">
        <button 
          @click="activeTab = 'standings'"
          :class="['px-3 py-2 md:px-4 md:py-2 rounded-md transition whitespace-nowrap text-sm md:text-base', activeTab === 'standings' ? 'bg-green-500 text-white' : 'text-gray-400 hover:text-white']"
        >
          {{ langStore.t('ranking.tabs.standings') }}
        </button>
        <button 
          @click="activeTab = 'results'"
          :class="['px-3 py-2 md:px-4 md:py-2 rounded-md transition whitespace-nowrap text-sm md:text-base', activeTab === 'results' ? 'bg-green-500 text-white' : 'text-gray-400 hover:text-white']"
        >
          {{ langStore.t('ranking.tabs.results') }}
        </button>
        <button 
          @click="activeTab = 'users'"
          :class="['px-3 py-2 md:px-4 md:py-2 rounded-md transition whitespace-nowrap text-sm md:text-base', activeTab === 'users' ? 'bg-green-500 text-white' : 'text-gray-400 hover:text-white']"
        >
          {{ langStore.t('ranking.tabs.users') }}
        </button>
      </div>
    </div>

    <div v-if="loading" class="text-center text-gray-400">
      {{ langStore.t('ranking.loading') }}
    </div>

    <div v-else-if="error" class="text-center text-red-400">
      {{ error }}
    </div>

    <!-- Standings Table -->
    <div v-else-if="activeTab === 'standings'" class="bg-gray-800 p-6 rounded-lg shadow-lg border border-gray-700 overflow-x-auto">
      <table class="w-full text-left text-gray-300">
        <thead class="text-gray-400 uppercase bg-gray-700">
          <tr>
            <th class="px-4 py-2">{{ langStore.t('ranking.standings.pos') }}</th>
            <th class="px-4 py-2">{{ langStore.t('ranking.standings.team') }}</th>
            <th class="px-4 py-2">{{ langStore.t('ranking.standings.pts') }}</th>
            <th class="px-4 py-2">{{ langStore.t('ranking.standings.played') }}</th>
            <th class="px-4 py-2">{{ langStore.t('ranking.standings.won') }}</th>
            <th class="px-4 py-2">{{ langStore.t('ranking.standings.drawn') }}</th>
            <th class="px-4 py-2">{{ langStore.t('ranking.standings.lost') }}</th>
            <th class="px-4 py-2">{{ langStore.t('ranking.standings.gf') }}</th>
            <th class="px-4 py-2">{{ langStore.t('ranking.standings.ga') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(team, index) in standings" :key="index" class="border-b border-gray-700 hover:bg-gray-700">
            <td class="px-4 py-2 font-bold">{{ team.position }}</td>
            <td class="px-4 py-2 text-white">{{ formatTeamName(team.team) }}</td>
            <td class="px-4 py-2 font-bold text-yellow-400">{{ team.points }}</td>
            <td class="px-4 py-2">{{ team.played }}</td>
            <td class="px-4 py-2 text-green-400">{{ team.won }}</td>
            <td class="px-4 py-2 text-gray-400">{{ team.drawn }}</td>
            <td class="px-4 py-2 text-red-400">{{ team.lost }}</td>
            <td class="px-4 py-2">{{ team.gf }}</td>
            <td class="px-4 py-2">{{ team.ga }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Match Results -->
    <div v-else-if="activeTab === 'results'" class="space-y-6">
      <div class="flex justify-end">
        <label class="flex items-center space-x-2 text-gray-300 cursor-pointer">
          <input type="checkbox" v-model="onlyFurbitoFic" class="form-checkbox h-5 w-5 text-green-500 rounded border-gray-600 bg-gray-700 focus:ring-green-500">
          <span>{{ langStore.t('ranking.results.filter') }}</span>
        </label>
      </div>

      <div v-for="(group, gIndex) in groupedMatchResults" :key="gIndex" class="space-y-4">
        <h3 class="text-xl font-bold text-green-400 border-b border-gray-700 pb-2">{{ group.name }}</h3>
        <div v-for="(match, index) in group.matches" :key="index" class="bg-gray-800 p-4 rounded-xl shadow-lg border border-gray-700 flex flex-col gap-3 transition hover:border-gray-600">
          <div class="text-xs text-gray-500 font-mono uppercase tracking-wider text-center">
            {{ match.dateTime }}
          </div>
          
          <div class="flex flex-col md:grid md:grid-cols-[1fr_auto_1fr] items-center gap-3 w-full">
            <span class="text-center md:text-right text-white font-bold text-lg md:text-lg leading-tight break-words w-full">
              {{ formatTeamName(match.homeTeam) }}
            </span>
            
            <div class="bg-gray-900 px-4 py-2 rounded-lg border border-gray-700 shadow-inner min-w-[80px] text-center flex flex-col justify-center my-1 md:my-0">
              <span class="text-yellow-400 font-mono text-2xl font-bold tracking-widest">
                {{ match.score ? match.score.replace('-', ':') : 'vs' }}
              </span>
            </div>

            <span class="text-center md:text-left text-white font-bold text-lg md:text-lg leading-tight break-words w-full">
              {{ formatTeamName(match.awayTeam) }}
            </span>
          </div>
          
          <button v-if="auth.user && auth.user.role === 'ADMIN'" 
                  @click="addEventFromMatch(match)"
                  class="bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold px-4 py-2 rounded-full self-center shadow-md transition transform hover:scale-105">
            {{ langStore.t('ranking.admin.createEvent') }}
          </button>
        </div>
      </div>
    </div>

    <!-- User Ranking -->
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
            <td class="px-4 py-2 font-bold">
              {{ getRank(index) }}
            </td>
            <td class="px-4 py-2 text-white font-medium">
              <router-link :to="'/user/' + user.id" class="hover:text-green-400 hover:underline transition">
                {{ user.username }}
              </router-link>
            </td>
            <td class="px-4 py-2 text-right font-bold text-green-400">{{ user.balance }} €</td>
            <td class="px-4 py-2 text-right font-bold text-green-400">
              {{ user.grossProfit }} €
            </td>
          </tr>

        </tbody>
      </table>
    </div>
  </div>
</template>
