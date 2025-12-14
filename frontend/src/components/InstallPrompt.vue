<script setup>
import { ref, onMounted } from 'vue'

const deferredPrompt = ref(null)
const showInstallButton = ref(false)

onMounted(() => {
  // Listen for the beforeinstallprompt event
  window.addEventListener('beforeinstallprompt', (e) => {
    // Prevent the mini-infobar from appearing on mobile
    e.preventDefault()
    // Stash the event so it can be triggered later
    deferredPrompt.value = e
    // Show install button
    showInstallButton.value = true
  })

  // Hide button when app is installed
  window.addEventListener('appinstalled', () => {
    showInstallButton.value = false
    deferredPrompt.value = null
  })
})

async function installApp() {
  if (!deferredPrompt.value) return

  // Show the install prompt
  deferredPrompt.value.prompt()
  
  // Wait for the user to respond to the prompt
  const { outcome } = await deferredPrompt.value.userChoice
  
  console.log(`User response: ${outcome}`)
  
  // Clear the deferredPrompt
  deferredPrompt.value = null
  showInstallButton.value = false
}
</script>

<template>
  <div v-if="showInstallButton" class="fixed bottom-20 right-4 z-50 animate-bounce">
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
</template>
