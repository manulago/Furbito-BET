<script setup>
import { ref, onMounted, computed } from 'vue'
import { useLanguageStore } from '../stores/language'

const langStore = useLanguageStore()
const players = ref([])
const activeTab = ref('all')

const fetchPlayers = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/players')
    if (response.ok) {
      players.value = await response.json()
    }
  } catch (error) {
    console.error('Error fetching players:', error)
  }
}

const sortedPlayers = computed(() => {
  let list = [...players.value]
  
  if (activeTab.value === 'all') {
    return list.sort((a, b) => a.name.localeCompare(b.name))
  } else if (activeTab.value === 'goals') {
    return list.filter(p => p.goals > 0).sort((a, b) => b.goals - a.goals)
  } else if (activeTab.value === 'assists') {
    return list.filter(p => p.assists > 0).sort((a, b) => b.assists - a.assists)
  } else if (activeTab.value === 'matches') {
    return list.filter(p => p.matchesPlayed > 0).sort((a, b) => b.matchesPlayed - a.matchesPlayed)
  } else if (activeTab.value === 'cards') {
    return list.filter(p => (p.yellowCards + p.redCards) > 0).sort((a, b) => (b.yellowCards + b.redCards) - (a.yellowCards + a.redCards))
  }
  return list
})

onMounted(() => {
  fetchPlayers()
})
</script>

<template>
  <div class="space-y-8">
    <h2 class="text-3xl font-bold text-white">{{ langStore.t('Estadísticas Furbito') || 'Estadísticas Furbito' }}</h2>

    <!-- Tabs -->
    <div class="flex border-b border-gray-700 overflow-x-auto scrollbar-hide">
      <button @click="activeTab = 'all'"
        :class="['px-4 py-2 font-medium whitespace-nowrap', activeTab === 'all' ? 'text-green-500 border-b-2 border-green-500' : 'text-gray-400 hover:text-white']">
        Todos
      </button>
      <button @click="activeTab = 'goals'"
        :class="['px-4 py-2 font-medium whitespace-nowrap', activeTab === 'goals' ? 'text-green-500 border-b-2 border-green-500' : 'text-gray-400 hover:text-white']">
        Goles
      </button>
      <button @click="activeTab = 'assists'"
        :class="['px-4 py-2 font-medium whitespace-nowrap', activeTab === 'assists' ? 'text-green-500 border-b-2 border-green-500' : 'text-gray-400 hover:text-white']">
        Asistencias
      </button>
      <button @click="activeTab = 'matches'"
        :class="['px-4 py-2 font-medium whitespace-nowrap', activeTab === 'matches' ? 'text-green-500 border-b-2 border-green-500' : 'text-gray-400 hover:text-white']">
        Partidos
      </button>
      <button @click="activeTab = 'cards'"
        :class="['px-4 py-2 font-medium whitespace-nowrap', activeTab === 'cards' ? 'text-green-500 border-b-2 border-green-500' : 'text-gray-400 hover:text-white']">
        Tarjetas
      </button>
    </div>

    <!-- Statistics List (Desktop Table / Mobile Cards) -->
    <div class="bg-gray-800 rounded-lg shadow-xl overflow-hidden border border-gray-700">
      
      <!-- Mobile Card View -->
      <div class="block md:hidden">
        <div v-for="(player, index) in sortedPlayers" :key="player.id" class="p-4 border-b border-gray-700 last:border-b-0 hover:bg-gray-750">
          <div class="flex justify-between items-start mb-3">
             <div class="flex items-center gap-3">
                <span class="text-gray-500 font-mono text-lg font-bold">#{{ index + 1 }}</span>
                <div>
                   <span class="text-white font-bold text-lg block leading-tight">{{ player.name }}</span>
                   <span class="text-gray-400 text-sm">{{ player.team || '-' }}</span>
                </div>
             </div>
          </div>
          
          <div class="grid grid-cols-2 gap-3 text-sm">
             <div class="bg-gray-900/50 p-2 rounded flex justify-between items-center" :class="{'ring-1 ring-green-500/50': activeTab === 'goals'}">
                <span class="text-gray-400">Goles</span>
                <span class="font-bold text-white" :class="{'text-green-400 text-lg': activeTab === 'goals'}">{{ player.goals }}</span>
             </div>
             <div class="bg-gray-900/50 p-2 rounded flex justify-between items-center" :class="{'ring-1 ring-green-500/50': activeTab === 'assists'}">
                <span class="text-gray-400">Asistencias</span>
                <span class="font-bold text-white" :class="{'text-green-400 text-lg': activeTab === 'assists'}">{{ player.assists }}</span>
             </div>
             <div class="bg-gray-900/50 p-2 rounded flex justify-between items-center" :class="{'ring-1 ring-green-500/50': activeTab === 'matches'}">
                <span class="text-gray-400">Partidos</span>
                <span class="text-white">
                  <span :class="{'text-green-400 font-bold': activeTab === 'matches'}">{{ player.matchesPlayed }}</span>
                  <span class="text-gray-500 text-xs ml-1">({{ player.matchesStarted }})</span>
                </span>
             </div>
             <div class="bg-gray-900/50 p-2 rounded flex justify-between items-center" :class="{'ring-1 ring-green-500/50': activeTab === 'cards'}">
                <span class="text-gray-400">Tarjetas</span>
                <span>
                   <span class="text-yellow-400 font-bold">{{ player.yellowCards }}</span>
                   <span class="text-gray-600 mx-1">/</span>
                   <span class="text-red-500 font-bold">{{ player.redCards }}</span>
                </span>
             </div>
          </div>
        </div>
        <div v-if="players.length === 0" class="p-8 text-center text-gray-500">
           No hay estadísticas disponibles.
        </div>
      </div>

      <!-- Desktop Table View -->
      <table class="w-full text-left hidden md:table">
        <thead class="bg-gray-700 text-gray-400 uppercase text-sm">
          <tr>
            <th class="p-4">#</th>
            <th class="p-4">Nombre</th>
            <th class="p-4">Equipo</th>
            <th v-if="activeTab === 'all' || activeTab === 'goals'" class="p-4 text-center" :class="{'text-green-400 font-bold': activeTab === 'goals'}">Goles</th>
            <th v-if="activeTab === 'all' || activeTab === 'assists'" class="p-4 text-center" :class="{'text-green-400 font-bold': activeTab === 'assists'}">Asistencias</th>
            <th v-if="activeTab === 'all' || activeTab === 'matches'" class="p-4 text-center" :class="{'text-green-400 font-bold': activeTab === 'matches'}">Partidos (Titular)</th>
            <th v-if="activeTab === 'all' || activeTab === 'cards'" class="p-4 text-center" :class="{'text-green-400 font-bold': activeTab === 'cards'}">Tarjetas (A/R)</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-700">
          <tr v-for="(player, index) in sortedPlayers" :key="player.id" class="hover:bg-gray-750">
            <td class="p-4 text-gray-500 font-mono">{{ index + 1 }}</td>
            <td class="p-4 font-medium text-white">{{ player.name }}</td>
            <td class="p-4 text-gray-400">{{ player.team || '-' }}</td>
            <td v-if="activeTab === 'all' || activeTab === 'goals'" class="p-4 text-center font-bold" :class="activeTab === 'goals' ? 'text-green-400 text-lg' : 'text-gray-300'">
              {{ player.goals }}
            </td>
            <td v-if="activeTab === 'all' || activeTab === 'assists'" class="p-4 text-center font-bold" :class="activeTab === 'assists' ? 'text-green-400 text-lg' : 'text-gray-300'">
              {{ player.assists }}
            </td>
            <td v-if="activeTab === 'all' || activeTab === 'matches'" class="p-4 text-center text-gray-300">
              {{ player.matchesPlayed }} <span class="text-gray-500 text-sm">({{ player.matchesStarted }})</span>
            </td>
            <td v-if="activeTab === 'all' || activeTab === 'cards'" class="p-4 text-center text-gray-300">
              <span class="text-yellow-400">{{ player.yellowCards }}</span> / <span class="text-red-500">{{ player.redCards }}</span>
            </td>
          </tr>
          <tr v-if="players.length === 0">
            <td colspan="7" class="p-8 text-center text-gray-500">
              No hay estadísticas disponibles.
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
