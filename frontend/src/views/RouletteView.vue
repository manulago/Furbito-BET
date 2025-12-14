<script setup>
import { ref, onMounted, computed } from 'vue'
import { useLanguageStore } from '../stores/language'
import { useAuthStore } from '../stores/auth'

const langStore = useLanguageStore()
const auth = useAuthStore()

const canSpin = ref(false)
const nextSpinTime = ref(null)
const spinning = ref(false)
const reward = ref(null)
const showAdModal = ref(false)
const adProgress = ref(0)
const adDuration = 10 // seconds
const wheelRotation = ref(0)

const timeLeft = ref('')

// Calculate time left interval
setInterval(() => {
  if (nextSpinTime.value) {
    const now = new Date()
    const next = new Date(nextSpinTime.value)
    const diff = next - now
    if (diff <= 0) {
      timeLeft.value = ''
      checkStatus() // Refresh status
    } else {
      const hours = Math.floor(diff / (1000 * 60 * 60))
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
      const seconds = Math.floor((diff % (1000 * 60)) / 1000)
      timeLeft.value = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
    }
  }
}, 1000)

async function checkStatus() {
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/rewards/spin-status`, {
      headers: { 'Authorization': `Bearer ${auth.token}` }
    })
    if (res.ok) {
        const data = await res.json()
        canSpin.value = data.canSpin
        nextSpinTime.value = data.nextSpinTime
    }
  } catch (e) {
    console.error(e)
  }
}

function startAd() {
    showAdModal.value = true
    adProgress.value = 0
    const interval = setInterval(() => {
        adProgress.value += 1
        if (adProgress.value >= 100) {
            clearInterval(interval)
            setTimeout(() => {
                showAdModal.value = false
                spinWheel()
            }, 500)
        }
    }, adDuration * 10) // 100 steps
}

async function spinWheel() {
    if (spinning.value) return
    spinning.value = true
    
    // Start visual spinning
    wheelRotation.value += 1800 + Math.random() * 360 // At least 5 full rotations

    try {
        const res = await fetch(`${import.meta.env.VITE_API_URL}/api/rewards/spin`, {
            method: 'POST',
             headers: { 'Authorization': `Bearer ${auth.token}` }
        })
        if (res.ok) {
             const data = await res.json()
             setTimeout(() => {
                 reward.value = data.reward
                 auth.fetchUser() // Update balance
                 spinning.value = false
                 canSpin.value = false
                 checkStatus()
             }, 3500) // Wait for animation roughly
        } else {
             spinning.value = false
        }
    } catch (e) {
        spinning.value = false
    }
}

onMounted(() => {
    checkStatus()
})
</script>

<template>
  <div class="min-h-screen py-12 px-4 flex flex-col items-center">
    <h1 class="text-4xl font-bold text-white mb-8 bg-gradient-to-r from-yellow-400 to-red-500 bg-clip-text text-transparent">
        Ruleta de la Fortuna
    </h1>

    <!-- Wheel Visual -->
    <div class="relative w-80 h-80 mb-12">
        <div class="w-full h-full rounded-full border-4 border-yellow-500 bg-gray-800 overflow-hidden shadow-2xl transition-transform duration-[3s] ease-out"
             :style="{ transform: `rotate(${wheelRotation}deg)` }">
             <!-- Simple segments -->
             <div class="absolute inset-0 flex items-center justify-center">
                 <div class="w-full h-0.5 bg-gray-600 absolute rotate-0"></div>
                 <div class="w-full h-0.5 bg-gray-600 absolute rotate-45"></div>
                 <div class="w-full h-0.5 bg-gray-600 absolute rotate-90"></div>
                 <div class="w-full h-0.5 bg-gray-600 absolute rotate-135"></div>
             </div>
             <div class="absolute inset-0 flex items-center justify-center">
                 <span class="text-yellow-400 font-bold text-xl">€</span>
             </div>
        </div>
        <!-- Pointer -->
        <div class="absolute -top-4 left-1/2 transform -translate-x-1/2 text-white text-4xl">
            ▼
        </div>
    </div>

    <!-- Controls -->
    <div class="text-center space-y-6">
        <div v-if="reward" class="animate-bounce text-3xl font-bold text-green-400 mb-4">
            ¡Has ganado {{ reward }}€!
        </div>

        <div v-if="canSpin && !spinning">
            <button @click="startAd" class="bg-gradient-to-r from-green-500 to-blue-500 hover:from-green-600 hover:to-blue-600 text-white font-bold py-4 px-8 rounded-full text-xl shadow-lg transform hover:scale-105 transition flex items-center gap-3">
                <span>📺</span> Ver Anuncio para Girar
            </button>
            <p class="text-gray-400 mt-2 text-sm">Gana entre 10€ y 50€</p>
        </div>

        <div v-else-if="!canSpin && nextSpinTime" class="bg-gray-800 p-6 rounded-xl border border-gray-700">
            <p class="text-gray-400 mb-2">Próximo giro en:</p>
            <p class="text-3xl font-mono text-yellow-400">{{ timeLeft }}</p>
        </div>
        
         <div v-else-if="spinning" class="text-yellow-400 text-xl font-bold animate-pulse">
            ¡Girando...!
        </div>
    </div>

    <!-- Ad Modal -->
    <div v-if="showAdModal" class="fixed inset-0 bg-black bg-opacity-90 z-50 flex items-center justify-center p-4">
        <div class="bg-white text-black p-8 rounded-lg max-w-md w-full text-center relative">
            <h2 class="text-2xl font-bold mb-4">Patrocinado</h2>
            <div class="aspect-video bg-gray-200 mb-4 flex items-center justify-center rounded">
                <span class="text-4xl animate-pulse">🎬</span>
            </div>
            <p class="mb-4 text-gray-600">Viendo anuncio publicitario...</p>
            
            <div class="w-full bg-gray-200 rounded-full h-4 mb-2">
                <div class="bg-blue-600 h-4 rounded-full transition-all duration-100" :style="{ width: `${adProgress}%` }"></div>
            </div>
            <p class="text-sm text-gray-500">Espera {{ Math.ceil(adDuration - (adProgress / 100 * adDuration)) }} segundos...</p>
        </div>
    </div>

  </div>
</template>
