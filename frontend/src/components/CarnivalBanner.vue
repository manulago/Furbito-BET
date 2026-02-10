<template>
  <div v-if="showBanner" class="fixed inset-0 bg-black bg-opacity-95 z-[70] flex items-center justify-center p-4 animate-fade-in-up">
    <div class="bg-gradient-to-br from-purple-900 via-pink-800 to-orange-700 border-4 border-yellow-400 rounded-xl max-w-md w-full p-6 relative shadow-2xl animate-scale-in overflow-hidden">
      <!-- Close button -->
      <button 
        @click="closeBanner" 
        class="absolute top-4 right-4 text-yellow-300 hover:text-white text-2xl font-bold z-10"
      >
        ×
      </button>

      <!-- Carnival content -->
      <div class="text-center relative z-10">
        <!-- Animated masks -->
        <div class="flex justify-center gap-4 mb-4">
          <span class="text-6xl animate-bounce" style="animation-delay: 0s;">🎭</span>
          <span class="text-6xl animate-bounce" style="animation-delay: 0.2s;">🎊</span>
          <span class="text-6xl animate-bounce" style="animation-delay: 0.4s;">🎉</span>
        </div>

        <h2 class="text-3xl font-bold text-yellow-300 mb-4 carnival-text-glow">
          ¡Feliz Carnaval!
        </h2>

        <div class="bg-white/10 backdrop-blur-sm rounded-lg p-6 mb-6">
          <p class="text-white text-lg mb-4">
            🎭 ¡FurbitoBET está de <span class="text-yellow-300 font-bold">CARNAVAL</span>!
          </p>
          <p class="text-gray-200 text-sm mb-4">
            Hemos activado el tema especial de Carnaval para celebrar estas fiestas contigo. 
            Disfruta del ambiente festivo mientras haces tus apuestas.
          </p>
          <div class="flex justify-center gap-3 text-3xl mt-4">
            <span class="animate-pulse">💃</span>
            <span class="animate-pulse" style="animation-delay: 0.3s;">🕺</span>
            <span class="animate-pulse" style="animation-delay: 0.6s;">🎺</span>
            <span class="animate-pulse" style="animation-delay: 0.9s;">🥁</span>
          </div>
        </div>

        <button 
          @click="closeBanner"
          class="w-full bg-gradient-to-r from-yellow-500 to-orange-500 hover:from-yellow-400 hover:to-orange-400 text-purple-900 font-bold py-3 px-6 rounded-lg shadow-lg transition transform hover:scale-105"
        >
          ¡Vamos a la fiesta! 🎊
        </button>
      </div>

      <!-- Decorative confetti -->
      <div class="absolute top-0 left-0 w-full h-full pointer-events-none overflow-hidden rounded-xl">
        <div class="confetti" style="left: 5%; animation-delay: 0s;">🎊</div>
        <div class="confetti" style="left: 20%; animation-delay: 0.5s;">🎉</div>
        <div class="confetti" style="left: 35%; animation-delay: 1s;">✨</div>
        <div class="confetti" style="left: 50%; animation-delay: 1.5s;">🎭</div>
        <div class="confetti" style="left: 65%; animation-delay: 0.7s;">🎊</div>
        <div class="confetti" style="left: 80%; animation-delay: 1.2s;">🎉</div>
        <div class="confetti" style="left: 95%; animation-delay: 0.3s;">✨</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useAuthStore } from '../stores/auth'

const props = defineProps({
  enabled: {
    type: Boolean,
    default: false
  }
})

const auth = useAuthStore()
const showBanner = ref(false)

const BANNER_KEY = 'carnival_banner_2026_seen'

function checkAndShowBanner() {
  // Only show if carnival theme is enabled and user is logged in
  if (!props.enabled || !auth.user) {
    showBanner.value = false
    return
  }

  const userId = auth.user.id
  const bannerSeen = localStorage.getItem(`${BANNER_KEY}_${userId}`)
  
  if (!bannerSeen) {
    // Show banner after a short delay
    setTimeout(() => {
      showBanner.value = true
    }, 500)
  }
}

onMounted(() => {
  checkAndShowBanner()
})

// Watch for changes in enabled prop or user login
watch(() => [props.enabled, auth.user], () => {
  checkAndShowBanner()
}, { immediate: true })

const closeBanner = () => {
  // Mark as seen for this user
  if (auth.user) {
    const userId = auth.user.id
    localStorage.setItem(`${BANNER_KEY}_${userId}`, 'true')
  }
  showBanner.value = false
}
</script>

<style scoped>
/* Confetti animation */
.confetti {
  position: absolute;
  top: -10%;
  font-size: 1.5rem;
  animation: confetti-fall-banner 4s ease-in-out infinite;
}

@keyframes confetti-fall-banner {
  0% {
    transform: translateY(0) rotate(0deg);
    opacity: 1;
  }
  100% {
    transform: translateY(500px) rotate(720deg);
    opacity: 0;
  }
}

/* Carnival text glow */
.carnival-text-glow {
  text-shadow: 
    0 0 10px rgba(255, 209, 102, 0.8),
    0 0 20px rgba(255, 209, 102, 0.6),
    0 0 30px rgba(247, 37, 133, 0.4);
}

/* Scale-in animation */
@keyframes scale-in {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.animate-scale-in {
  animation: scale-in 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

/* Fade-in-up animation */
@keyframes fade-in-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in-up {
  animation: fade-in-up 0.4s ease-out;
}
</style>
