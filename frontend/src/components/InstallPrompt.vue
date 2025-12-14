<script setup>
import { ref, onMounted } from 'vue'

const deferredPrompt = ref(null)
const showInstallButton = ref(false)
const showIOSInstructions = ref(false)
const isIOS = ref(false)

onMounted(() => {
  // Detect iOS
  const userAgent = window.navigator.userAgent.toLowerCase()
  isIOS.value = /iphone|ipad|ipod/.test(userAgent)
  
  // Check if already installed (standalone mode)
  const isStandalone = window.matchMedia('(display-mode: standalone)').matches || 
                       window.navigator['standalone'] === true

  if (isStandalone) {
    // App is already installed, don't show anything
    return
  }

  if (isIOS.value) {
    // For iOS, show instructions banner after a delay
    // Only show if not already dismissed in this session
    const dismissed = sessionStorage.getItem('ios_install_dismissed')
    if (!dismissed) {
      setTimeout(() => {
        showIOSInstructions.value = true
      }, 3000) // Show after 3 seconds
    }
  } else {
    // For Android/Desktop, use the standard install prompt
    window.addEventListener('beforeinstallprompt', (e) => {
      e.preventDefault()
      deferredPrompt.value = e
      showInstallButton.value = true
    })

    window.addEventListener('appinstalled', () => {
      showInstallButton.value = false
      deferredPrompt.value = null
    })
  }
})

async function installApp() {
  if (!deferredPrompt.value) return

  deferredPrompt.value.prompt()
  const { outcome } = await deferredPrompt.value.userChoice
  
  console.log(`User response: ${outcome}`)
  
  deferredPrompt.value = null
  showInstallButton.value = false
}

function dismissIOSInstructions() {
  showIOSInstructions.value = false
  sessionStorage.setItem('ios_install_dismissed', 'true')
}
</script>

<template>
  <!-- Android/Desktop Install Button -->
  <div v-if="showInstallButton && !isIOS" class="fixed bottom-20 right-4 z-50 animate-bounce">
    <button 
      @click="installApp"
      class="bg-gradient-to-r from-green-500 to-blue-600 hover:from-green-400 hover:to-blue-500 text-white font-bold py-3 px-6 rounded-full shadow-2xl flex items-center gap-3 transition transform hover:scale-105"
    >
      <span class="text-2xl">📱</span>
      <div class="text-left">
        <div class="text-sm font-bold">Instalar App</div>
        <div class="text-xs opacity-90">Acceso rápido</div>
      </div>
    </button>
  </div>

  <!-- iOS Install Instructions Banner -->
  <div v-if="showIOSInstructions && isIOS" class="fixed bottom-20 left-4 right-4 z-50 animate-slide-up">
    <div class="bg-gradient-to-r from-blue-600 to-purple-600 rounded-2xl shadow-2xl p-4 border-2 border-white/20">
      <button 
        @click="dismissIOSInstructions"
        class="absolute top-2 right-2 text-white/80 hover:text-white text-2xl leading-none p-1"
      >
        ×
      </button>
      
      <div class="flex items-start gap-3 pr-6">
        <div class="text-4xl shrink-0">📱</div>
        <div class="flex-1">
          <h3 class="text-white font-bold text-base mb-2">¡Instala FurbitoBET!</h3>
          <div class="text-white/90 text-sm space-y-1">
            <p class="flex items-center gap-2">
              <span class="text-lg">1️⃣</span>
              <span>Toca el botón <strong>Compartir</strong> 
                <svg class="inline w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M16 5l-1.42 1.42-1.59-1.59V16h-1.98V4.83L9.42 6.42 8 5l4-4 4 4zm4 5v11c0 1.1-.9 2-2 2H6c-1.11 0-2-.9-2-2V10c0-1.11.89-2 2-2h3v2H6v11h12V10h-3V8h3c1.1 0 2 .89 2 2z"/>
                </svg>
              </span>
            </p>
            <p class="flex items-center gap-2">
              <span class="text-lg">2️⃣</span>
              <span>Selecciona <strong>"Añadir a pantalla de inicio"</strong></span>
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes slide-up {
  from {
    transform: translateY(100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.animate-slide-up {
  animation: slide-up 0.5s ease-out;
}
</style>
