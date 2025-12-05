<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const events = ref([])
const auth = useAuthStore()
const router = useRouter()

async function fetchEvents() {
  const res = await fetch(`${import.meta.env.VITE_API_URL}/api/events`)
  events.value = await res.json()
}

function goToEvent(id) {
  router.push(`/event/${id}`)
}

onMounted(fetchEvents)
</script>

<template>
  <div class="space-y-8 pb-24">
    <div class="flex justify-between items-center">
      <h2 class="text-3xl font-bold text-white">Live Events</h2>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div v-for="event in events" :key="event.id" 
           @click="goToEvent(event.id)"
           class="bg-gray-800 rounded-xl overflow-hidden shadow-lg border border-gray-700 hover:border-green-500 cursor-pointer transition transform hover:-translate-y-1">
        <div class="p-6 flex justify-between items-center">
          <div>
            <h3 class="text-xl font-bold text-white mb-2">{{ event.name }}</h3>
            <p class="text-gray-400 text-sm">{{ new Date(event.date).toLocaleString() }}</p>
          </div>
          <span class="text-xs font-bold px-2 py-1 rounded bg-red-500/20 text-red-400 uppercase tracking-wider">{{ event.status }}</span>
        </div>
      </div>
    </div>
  </div>
</template>
