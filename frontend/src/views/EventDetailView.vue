<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useBetStore } from '../stores/bet'
import { useLanguageStore } from '../stores/language'

const route = useRoute()
const betStore = useBetStore()
const langStore = useLanguageStore()
const event = ref(null)
const loading = ref(true)

async function fetchEvent() {
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/events/${route.params.id}`)
    if (res.ok) {
      event.value = await res.json()
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const outcomesByCategory = computed(() => {
  if (!event.value || !event.value.outcomes) return {}
  
  const groups = {}
  event.value.outcomes.forEach(outcome => {
    const category = outcome.outcomeGroup || langStore.t('event.uncategorized')
    if (!groups[category]) {
      groups[category] = []
    }
    groups[category].push(outcome)
  })
  return groups
})

onMounted(fetchEvent)
</script>

<template>
  <div v-if="loading" class="text-white text-center py-10">{{ langStore.t('event.loading') }}</div>
  <div v-else-if="!event" class="text-white text-center py-10">{{ langStore.t('event.notFound') }}</div>
  <div v-else class="space-y-8 pb-24">
    <div class="bg-gray-800 p-6 rounded-lg shadow-lg border border-gray-700">
      <h2 class="text-3xl font-bold text-white mb-2">{{ event.name }}</h2>
      <p class="text-gray-400">{{ new Date(event.date).toLocaleString(langStore.currentLanguage) }}</p>
      <span class="inline-block mt-2 text-xs font-bold px-2 py-1 rounded bg-red-500/20 text-red-400 uppercase tracking-wider">{{ langStore.t('common.status.' + event.status) }}</span>
    </div>

    <div class="space-y-6">
      <div v-for="(outcomes, category) in outcomesByCategory" :key="category" class="bg-gray-800 rounded-lg overflow-hidden shadow-lg border border-gray-700">
        <div class="bg-gray-700 p-3 border-b border-gray-700">
          <h3 class="text-lg font-bold text-white">{{ category }}</h3>
        </div>
        <div class="p-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <button v-for="outcome in outcomes" :key="outcome.id" 
            @click="betStore.toggleOutcome(outcome)"
            :class="['flex justify-between items-center p-3 rounded border transition duration-200',
              betStore.selectedOutcomes.find(o => o.id === outcome.id)
                ? 'bg-green-500/20 border-green-500 text-white'
                : 'bg-gray-700/50 border-gray-600 hover:border-gray-500 text-gray-300 hover:text-white'
            ]">
            <span class="font-medium">{{ outcome.description }}</span>
            <span class="font-bold bg-gray-900 px-2 py-1 rounded text-green-400">{{ outcome.odds }}</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
