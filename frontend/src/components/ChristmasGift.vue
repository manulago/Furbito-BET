<template>
  <div v-if="showGift" class="fixed inset-0 bg-black bg-opacity-95 z-[70] flex items-center justify-center p-4 animate-fade-in-up">
    <div class="bg-gradient-to-br from-red-900 via-green-900 to-red-900 border-4 border-yellow-400 rounded-xl max-w-md w-full p-6 relative shadow-2xl animate-scale-in">
      <!-- Close button -->
      <button 
        @click="closeGift" 
        class="absolute top-4 right-4 text-yellow-300 hover:text-white text-2xl font-bold z-10"
      >
        ×
      </button>

      <!-- Gift icon with animation -->
      <div v-if="!opened" class="text-center mb-6">
        <div class="text-9xl animate-bounce cursor-pointer" @click="openGift">
          🎁
        </div>
        <p class="text-yellow-300 font-bold text-xl mt-4">
          ¡Tienes un regalo de Navidad!
        </p>
        <p class="text-white text-sm mt-2">
          Haz clic en el regalo para abrirlo
        </p>
      </div>

      <!-- Opened gift content -->
      <div v-else class="text-center">
        <div class="text-7xl mb-4 animate-pulse">
          🎄✨🎅
        </div>
        <h2 class="text-3xl font-bold text-yellow-300 mb-4">
          ¡Feliz Navidad!
        </h2>
        <div class="bg-white/10 backdrop-blur-sm rounded-lg p-6 mb-6">
          <p class="text-white text-lg mb-4">
            🎉 ¡Te regalamos <span class="text-yellow-300 font-bold text-2xl">100€</span> para que disfrutes apostando estas Navidades!
          </p>
          <p class="text-gray-300 text-sm">
            Este es nuestro regalo para ti. ¡Úsalo sabiamente y que tengas mucha suerte! 🍀
          </p>
        </div>
        <button 
          @click="closeGift"
          class="w-full bg-gradient-to-r from-yellow-500 to-yellow-600 hover:from-yellow-400 hover:to-yellow-500 text-red-900 font-bold py-3 px-6 rounded-lg shadow-lg transition transform hover:scale-105"
        >
          ¡Gracias! 🎄
        </button>
      </div>

      <!-- Decorative snowflakes -->
      <div class="absolute top-0 left-0 w-full h-full pointer-events-none overflow-hidden rounded-xl">
        <div class="snowflake" style="left: 10%; animation-delay: 0s;">❄️</div>
        <div class="snowflake" style="left: 30%; animation-delay: 1s;">❄️</div>
        <div class="snowflake" style="left: 50%; animation-delay: 2s;">❄️</div>
        <div class="snowflake" style="left: 70%; animation-delay: 1.5s;">❄️</div>
        <div class="snowflake" style="left: 90%; animation-delay: 0.5s;">❄️</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const showGift = ref(false)
const opened = ref(false)

const GIFT_KEY = 'christmas_gift_2024_seen'

onMounted(() => {
  // Check if user is logged in and hasn't seen the gift
  if (auth.user) {
    const userId = auth.user.id
    const giftSeen = localStorage.getItem(`${GIFT_KEY}_${userId}`)
    
    if (!giftSeen) {
      // Show gift modal after a short delay
      setTimeout(() => {
        showGift.value = true
      }, 1000)
    }
  }
})

const openGift = () => {
  opened.value = true
  // Play a sound effect if available (optional)
  // new Audio('/gift-open.mp3').play().catch(() => {})
}

const closeGift = () => {
  // Mark as seen for this user
  if (auth.user) {
    const userId = auth.user.id
    localStorage.setItem(`${GIFT_KEY}_${userId}`, 'true')
  }
  showGift.value = false
}

// Watch for user login to show gift
import { watch } from 'vue'
watch(() => auth.user, (newUser) => {
  if (newUser) {
    const userId = newUser.id
    const giftSeen = localStorage.getItem(`${GIFT_KEY}_${userId}`)
    
    if (!giftSeen) {
      setTimeout(() => {
        showGift.value = true
      }, 1000)
    }
  }
})
</script>

<style scoped>
@keyframes fade-in-up {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes scale-in {
  from {
    transform: scale(0.8);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

.animate-fade-in-up {
  animation: fade-in-up 0.5s ease-out;
}

.animate-scale-in {
  animation: scale-in 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.snowflake {
  position: absolute;
  top: -20px;
  font-size: 1.5rem;
  animation: fall 8s linear infinite;
  opacity: 0.7;
}

@keyframes fall {
  to {
    transform: translateY(500px) rotate(360deg);
    opacity: 0;
  }
}
</style>
