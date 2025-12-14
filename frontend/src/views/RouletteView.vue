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

const loading = ref(true)
const error = ref(null)

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
  loading.value = true
  error.value = null
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/rewards/spin-status`, {
      headers: { 'Authorization': `Bearer ${auth.token}` }
    })
    if (res.ok) {
        const data = await res.json()
        canSpin.value = data.canSpin
        nextSpinTime.value = data.nextSpinTime
    } else {
        error.value = "Error al verificar estado."
    }
  } catch (e) {
    console.error(e)
    error.value = "Error de conexión."
  } finally {
    loading.value = false
  }
}

function startAd() {
    showAdModal.value = true
    adProgress.value = 0
    
    // Attempt to render ad
    setTimeout(() => {
        try {
            (window.adsbygoogle = window.adsbygoogle || []).push({})
        } catch (e) {
            // AdSense push might fail if script not ready or no ads available
            console.log("AdSense push waiting or error", e)
        }
    }, 500)

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
                 auth.fetchBalance() // Update balance
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
    // Dynamic AdSense Script Injection
    if (!document.getElementById('adsense-script')) {
        const script = document.createElement('script')
        script.id = 'adsense-script'
        script.src = 'https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=ca-pub-1795380500051096'
        script.async = true
        script.crossOrigin = 'anonymous'
        document.head.appendChild(script)
    }
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
        <div v-if="loading" class="text-gray-400 animate-pulse">
            Cargando estado...
        </div>

        <div v-else-if="error" class="text-red-400">
            {{ error }}
            <button @click="checkStatus" class="block mx-auto mt-2 text-sm underline">Reintentar</button>
        </div>

        <div v-else-if="reward" class="animate-bounce text-3xl font-bold text-green-400 mb-4">
            ¡Has ganado {{ reward }}€!
        </div>

        <div v-else-if="canSpin && !spinning">
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
        
        <div v-else class="text-gray-500">
            Estado desconocido.
        </div>
    </div>

    <!-- Ad Modal -->
    <div v-if="showAdModal" class="fixed inset-0 bg-black bg-opacity-90 z-50 flex items-center justify-center p-4">
        <div class="bg-white text-black p-4 rounded-lg max-w-lg w-full text-center relative">
            <h2 class="text-xl font-bold mb-2">Patrocinado</h2>
            
            <!-- Google Ad Container -->
            <div class="bg-gray-100 mb-4 flex items-center justify-center min-h-[250px] overflow-hidden">
                 <!-- 
                    IMPORTANTE: Para que salga un anuncio específico aquí, necesitas un "Ad Unit" (Bloque de anuncios).
                    Ve a AdSense -> Anuncios -> Por bloque de anuncios -> Crear nuevo (Display).
                    Copia el 'data-ad-slot' (el número) y ponlo abajo.
                    Si no, Google intentará poner anuncios automáticos si están activados.
                 -->
                 <ins class="adsbygoogle"
                     style="display:block; width: 100%; height: 250px;"
                     data-ad-client="ca-pub-1795380500051096"
                     data-ad-slot="REPLACE_WITH_YOUR_AD_SLOT_ID"
                     data-ad-format="auto"
                     data-full-width-responsive="true"></ins>
            </div>

            <p class="mb-2 text-gray-600">Viendo anuncio...</p>
            
            <div class="w-full bg-gray-200 rounded-full h-4 mb-2">
                <div class="bg-blue-600 h-4 rounded-full transition-all duration-100" :style="{ width: `${adProgress}%` }"></div>
            </div>
            <p class="text-sm text-gray-500">Espera {{ Math.ceil(adDuration - (adProgress / 100 * adDuration)) }} segundos...</p>
        </div>
    </div>

  </div>
</template>
