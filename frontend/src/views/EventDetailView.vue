<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useBetStore } from '../stores/bet'
import { useLanguageStore } from '../stores/language'
import LoadingSpinner from '../components/LoadingSpinner.vue'

const route = useRoute()
const betStore = useBetStore()
const langStore = useLanguageStore()
const event = ref(null)
const loading = ref(true)
const expandedCategories = ref({})

async function fetchEvent() {
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/events/${route.params.id}`)
    if (res.ok) {
      event.value = await res.json()
      // Initialize all categories as collapsed by default
      if (event.value && event.value.outcomes) {
        const categories = [...new Set(event.value.outcomes.map(o => o.outcomeGroup || langStore.t('event.uncategorized')))]
        categories.forEach(cat => {
          expandedCategories.value[cat] = false
        })
      }
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

function toggleCategory(category) {
  expandedCategories.value[category] = !expandedCategories.value[category]
}

onMounted(fetchEvent)
</script>

<template>
  <LoadingSpinner v-if="loading" />
  <div v-else-if="!event" class="text-white text-center py-10">{{ langStore.t('event.notFound') }}</div>
  <div v-else class="space-y-8 pb-24">
    <div class="bg-gray-800 p-6 rounded-lg shadow-lg border border-gray-700 animate-fade-in-down">
      <h2 class="text-3xl font-bold text-white mb-2">{{ event.name }}</h2>
      <p class="text-gray-400">{{ new Date(event.date).toLocaleString(langStore.currentLanguage) }}</p>
      <span class="inline-block mt-2 text-xs font-bold px-2 py-1 rounded bg-red-500/20 text-red-400 uppercase tracking-wider">{{ langStore.t('common.status.' + event.status) }}</span>
    </div>

    <div class="space-y-6 stagger-fade-in">
      <div v-for="(outcomes, category) in outcomesByCategory" :key="category" class="bg-gray-800 rounded-lg overflow-hidden shadow-lg border border-gray-700">
        <button 
          @click="toggleCategory(category)"
          class="w-full bg-gray-700 p-3 border-b border-gray-700 flex justify-between items-center hover:bg-gray-600 transition cursor-pointer"
        >
          <h3 class="text-lg font-bold text-white">{{ category }}</h3>
          <svg 
            class="w-5 h-5 text-white transition-transform duration-200"
            :class="{ 'rotate-180': expandedCategories[category] }"
            fill="none" 
            stroke="currentColor" 
            viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
          </svg>
        </button>
        <transition
          enter-active-class="transition-all duration-300 ease-out"
          leave-active-class="transition-all duration-200 ease-in"
          enter-from-class="max-h-0 opacity-0"
          enter-to-class="max-h-screen opacity-100"
          leave-from-class="max-h-screen opacity-100"
          leave-to-class="max-h-0 opacity-0"
        >
          <div v-show="expandedCategories[category]" class="overflow-hidden">
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
        </transition>
      </div>
    </div>
  </div>
</template>
