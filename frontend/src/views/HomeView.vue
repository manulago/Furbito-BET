```vue
<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useBetStore } from '../stores/bet'
import { useLanguageStore } from '../stores/language'

const auth = useAuthStore()
const betStore = useBetStore()
const langStore = useLanguageStore()
const events = ref([])
const router = useRouter()

onMounted(async () => {
  const res = await fetch(`${import.meta.env.VITE_API_URL}/api/events`)
  events.value = await res.json()
})

// Filtrar eventos en curso (UPCOMING y LIVE)
const activeEvents = computed(() => {
  return events.value.filter(event => 
    event.status === 'UPCOMING' || event.status === 'LIVE'
  )
})

// Filtrar eventos finalizados (FINISHED y COMPLETED)
const finishedEvents = computed(() => {
  return events.value.filter(event => 
    event.status === 'FINISHED' || event.status === 'COMPLETED'
  )
})

function selectEvent(event) {
  router.push({ name: 'event-detail', params: { id: event.id } })
}
</script>

<template>
  <div class="space-y-12 pb-24">
    <!-- Eventos en curso -->
    <div class="space-y-6 animate-fade-in-down">
      <div class="flex justify-between items-center">
        <h2 class="text-3xl font-bold text-white">{{ langStore.t('home.upcomingEvents') }}</h2>
        <span v-if="activeEvents.length > 0" class="text-sm text-gray-400">{{ activeEvents.length }} {{ activeEvents.length === 1 ? 'evento' : 'eventos' }}</span>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 stagger-fade-in">
        <div v-for="event in activeEvents" :key="event.id" 
             @click="selectEvent(event)"
             class="bg-gray-800 rounded-xl overflow-hidden shadow-lg border border-gray-700 hover:border-green-500 cursor-pointer transition-all duration-300 hover-lift hover-glow">
          <div class="p-6 flex justify-between items-center">
            <div>
              <h3 class="text-xl font-bold text-white mb-2">{{ event.name }}</h3>
              <p class="text-gray-400 text-sm">{{ new Date(event.date).toLocaleString() }}</p>
            </div>
            <span class="text-xs font-bold px-2 py-1 rounded bg-blue-500/20 text-blue-400 uppercase tracking-wider transition-colors hover:bg-blue-500/30">{{ langStore.t('common.status.' + event.status) }}</span>
          </div>
        </div>
        <div v-if="activeEvents.length === 0" class="text-gray-500 col-span-2 text-center py-8 animate-fade-in">{{ langStore.t('home.noEvents') }}</div>
      </div>
    </div>

    <!-- Eventos finalizados -->
    <div v-if="finishedEvents.length > 0" class="space-y-6 animate-fade-in">
      <div class="flex justify-between items-center border-t border-gray-700 pt-8">
        <h2 class="text-2xl font-bold text-gray-300">Eventos Finalizados</h2>
        <span class="text-sm text-gray-500">{{ finishedEvents.length }} {{ finishedEvents.length === 1 ? 'evento' : 'eventos' }}</span>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 stagger-fade-in">
        <div v-for="event in finishedEvents" :key="event.id" 
             @click="selectEvent(event)"
             class="bg-gray-800/50 rounded-xl overflow-hidden shadow-lg border border-gray-700/50 hover:border-gray-600 cursor-pointer transition-all duration-300 hover-lift opacity-75 hover:opacity-100">
          <div class="p-6 flex justify-between items-center">
            <div>
              <h3 class="text-lg font-bold text-gray-300 mb-2">{{ event.name }}</h3>
              <p class="text-gray-500 text-sm">{{ new Date(event.date).toLocaleString() }}</p>
            </div>
            <span class="text-xs font-bold px-2 py-1 rounded bg-gray-500/20 text-gray-400 uppercase tracking-wider">{{ langStore.t('common.status.' + event.status) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
