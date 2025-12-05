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
const router = useRouter() // Keep router as it might be used elsewhere or was implicitly part of the original setup

onMounted(async () => {
  const res = await fetch(`${import.meta.env.VITE_API_URL}/api/events`)
  events.value = await res.json()
})

function selectEvent(event) {
  if (event.outcomes && event.outcomes.length > 0) {
    // Logic to expand event or show outcomes
    // For now, we can just toggle expansion if we had that UI, 
    // but the current UI implies clicking adds to bet slip or shows details.
    // Let's assume clicking adds the first outcome for now or does nothing if not implemented.
    // Actually, the previous code didn't have selectEvent logic shown in the snippet, 
    // but the template calls it. Let's just keep it simple.
    console.log('Selected event:', event)
  }
}
</script>

<template>
  <div class="space-y-8 pb-24">
    <div class="flex justify-between items-center">
      <h2 class="text-3xl font-bold text-white">{{ langStore.t('home.upcomingEvents') }}</h2>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div v-for="event in events" :key="event.id" 
           @click="selectEvent(event)"
           class="bg-gray-800 rounded-xl overflow-hidden shadow-lg border border-gray-700 hover:border-green-500 cursor-pointer transition transform hover:-translate-y-1">
        <div class="p-6 flex justify-between items-center">
          <div>
            <h3 class="text-xl font-bold text-white mb-2">{{ event.name }}</h3>
            <p class="text-gray-400 text-sm">{{ new Date(event.date).toLocaleString() }}</p>
          </div>
          <span class="text-xs font-bold px-2 py-1 rounded bg-blue-500/20 text-blue-400 uppercase tracking-wider">{{ langStore.t('common.status.' + event.status) }}</span>
        </div>
      </div>
      <div v-if="events.length === 0" class="text-gray-500 col-span-2 text-center py-8">{{ langStore.t('home.noEvents') }}</div>
    </div>
  </div>
</template>
