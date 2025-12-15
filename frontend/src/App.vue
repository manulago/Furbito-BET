<script setup>
import { useAuthStore } from './stores/auth'
import { useBetStore } from './stores/bet'
import { useLanguageStore } from './stores/language'
import { useRouter } from 'vue-router'
import { onMounted, ref, watch, onUnmounted } from 'vue'
import InstallPrompt from './components/InstallPrompt.vue'

const auth = useAuthStore()
const betStore = useBetStore()
const langStore = useLanguageStore()
const router = useRouter()
const isMobileMenuOpen = ref(false)

const showNewsModal = ref(false)
const currentNewsCount = ref(0)
const NEWS_KEY_VERSION = 'news_v1'

// Inactivity timeout (15 minutes)
const INACTIVITY_TIMEOUT = 15 * 60 * 1000 // 15 minutes in milliseconds
let inactivityTimer = null

function resetInactivityTimer() {
  // Clear existing timer
  if (inactivityTimer) {
    clearTimeout(inactivityTimer)
  }

  // Only set timer if user is logged in
  if (auth.user) {
    inactivityTimer = setTimeout(() => {
      console.log('⏰ Auto-logout: 15 minutes of inactivity')
      auth.logout()
      router.push('/login')
      alert('Tu sesión ha expirado por inactividad. Por favor, inicia sesión nuevamente.')
    }, INACTIVITY_TIMEOUT)
  }
}

function setupActivityListeners() {
  // Events that indicate user activity
  const events = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click']
  
  events.forEach(event => {
    window.addEventListener(event, resetInactivityTimer, true)
  })
}

function removeActivityListeners() {
  const events = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click']
  
  events.forEach(event => {
    window.removeEventListener(event, resetInactivityTimer, true)
  })
}

function checkNews() {
  if (!auth.user) return

  auth.fetchBalance()

  // News Modal Logic
  const userId = auth.user.id || 'guest'
  const storageKey = `${NEWS_KEY_VERSION}_count_${userId}`
  const sessionKey = `${NEWS_KEY_VERSION}_seen_session_${userId}` // Specific to user session

  // Check if already seen this session (tab)
  const seenThisSession = sessionStorage.getItem(sessionKey)

  if (!seenThisSession) {
      // Check if news modal is globally enabled
      fetch(`${import.meta.env.VITE_API_URL}/api/admin/news-modal-status`)
        .then(res => res.json())
        .then(data => {
          if (!data.enabled) {
            console.log('📢 News modal is disabled globally by admin')
            return
          }

          let count = parseInt(localStorage.getItem(storageKey) || '0')
          
          if (count < 5) {
              currentNewsCount.value = count
              showNewsModal.value = true
              
              // Increment count and mark session as seen
              localStorage.setItem(storageKey, (count + 1).toString())
              sessionStorage.setItem(sessionKey, 'true')
          }
        })
        .catch(err => {
          console.error('Error checking news modal status:', err)
          // If error, show modal anyway (default behavior)
          let count = parseInt(localStorage.getItem(storageKey) || '0')
          
          if (count < 5) {
              currentNewsCount.value = count
              showNewsModal.value = true
              
              localStorage.setItem(storageKey, (count + 1).toString())
              sessionStorage.setItem(sessionKey, 'true')
          }
        })
  }
}

onMounted(() => {
  checkNews()
  setupActivityListeners()
  resetInactivityTimer()
})

onUnmounted(() => {
  removeActivityListeners()
  if (inactivityTimer) {
    clearTimeout(inactivityTimer)
  }
})

watch(() => auth.user, (newUser) => {
    checkNews()
    
    // Reset timer when user logs in/out
    if (newUser) {
      resetInactivityTimer()
    } else {
      if (inactivityTimer) {
        clearTimeout(inactivityTimer)
      }
    }
})

// Navigation direction tracking for app-like transitions
const navigationHistory = ref([])
const transitionName = ref('fade')
const isNavigatingBack = ref(false)

router.beforeEach((to, from) => {
  // Determine if navigating back
  const toDepth = to.path.split('/').length
  const fromDepth = from.path.split('/').length
  
  // Check if user clicked browser back button
  const lastPath = navigationHistory.value[navigationHistory.value.length - 1]
  if (lastPath === to.path) {
    // Going back
    isNavigatingBack.value = true
    navigationHistory.value.pop()
    transitionName.value = 'slide-right'
  } else {
    // Going forward
    isNavigatingBack.value = false
    navigationHistory.value.push(from.path)
    transitionName.value = 'slide-left'
  }
})

// Page transition handlers
function onBeforeEnter(el) {
  if (isNavigatingBack.value) {
    el.style.opacity = 0
    el.style.transform = 'translateX(-30px)'
  } else {
    el.style.opacity = 0
    el.style.transform = 'translateX(30px)'
  }
}

function onEnter(el, done) {
  el.offsetHeight // trigger reflow
  el.style.transition = 'opacity 0.3s ease, transform 0.3s cubic-bezier(0.4, 0, 0.2, 1)'
  el.style.opacity = 1
  el.style.transform = 'translateX(0)'
  setTimeout(done, 300)
}

function onLeave(el, done) {
  el.style.transition = 'opacity 0.25s ease, transform 0.25s cubic-bezier(0.4, 0, 0.2, 1)'
  el.style.opacity = 0
  if (isNavigatingBack.value) {
    el.style.transform = 'translateX(30px)'
  } else {
    el.style.transform = 'translateX(-30px)'
  }
  setTimeout(done, 250)
}

function logout() {
  if (inactivityTimer) {
    clearTimeout(inactivityTimer)
  }
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <div class="min-h-screen bg-gray-900 text-white font-sans pb-24">
    <nav class="bg-gray-800 p-4 shadow-lg border-b border-gray-700 relative z-50">
      <div class="container mx-auto flex justify-between items-center">
        <div class="flex items-center gap-3">
          <img src="/logo.jpg" alt="FurbitoBET Logo" class="h-10 w-10 rounded-full border-2 border-green-400 shadow-lg" />
          <h1 class="text-2xl font-bold bg-gradient-to-r from-green-400 to-blue-500 bg-clip-text text-transparent">FurbitoBET</h1>
        </div>

        <!-- Desktop Menu -->
        <div class="hidden md:flex items-center gap-2">
          <!-- Public Links -->
          <template v-if="auth.user">
            <router-link to="/" class="px-2 py-1.5 rounded-lg text-gray-300 hover:text-white hover:bg-gray-700 transition font-medium text-sm whitespace-nowrap" active-class="bg-gray-700 text-green-400">{{ langStore.t('nav.home') }}</router-link>
            <RouterLink to="/ranking" class="px-2 py-1.5 rounded-lg text-gray-300 hover:text-white hover:bg-gray-700 transition font-medium text-sm whitespace-nowrap" active-class="bg-gray-700 text-green-400">{{ langStore.t('nav.ranking') }}</RouterLink>
            <RouterLink to="/user-ranking" class="px-2 py-1.5 rounded-lg text-gray-300 hover:text-white hover:bg-gray-700 transition font-medium text-sm whitespace-nowrap" active-class="bg-gray-700 text-green-400">{{ langStore.t('nav.userRanking') || 'Usuarios' }}</RouterLink>
            <router-link to="/statistics" class="px-2 py-1.5 rounded-lg text-gray-300 hover:text-white hover:bg-gray-700 transition font-medium text-sm whitespace-nowrap" active-class="bg-gray-700 text-green-400">{{ langStore.t('nav.statistics') }}</router-link>
          </template>
          <router-link to="/help" class="px-2 py-1.5 rounded-lg text-gray-300 hover:text-white hover:bg-gray-700 transition font-medium text-sm whitespace-nowrap" active-class="bg-gray-700 text-green-400">{{ langStore.t('nav.help') || 'Ayuda' }}</router-link>
          <!-- <router-link v-if="auth.user" to="/roulette" class="text-xl hover:scale-110 transition duration-300" title="Ruleta de la Suerte">🎰</router-link> -->
          <router-link v-if="auth.isAdmin" to="/admin" class="px-2 py-1.5 rounded-lg text-gray-300 hover:text-yellow-400 hover:bg-gray-700 transition font-medium text-sm whitespace-nowrap" active-class="bg-gray-700 text-yellow-500">{{ langStore.t('nav.admin') }}</router-link>

          <div v-if="auth.user" class="flex items-center gap-2 ml-2">
            <RouterLink to="/my-bets" class="px-2 py-1.5 rounded-lg text-gray-300 hover:text-white hover:bg-gray-700 transition font-medium text-sm whitespace-nowrap" active-class="bg-gray-700 text-green-400">{{ langStore.t('nav.myBets') }}</RouterLink>
            <RouterLink to="/profile/settings" class="px-2 py-1.5 rounded-lg text-gray-300 hover:text-white hover:bg-gray-700 transition font-medium text-sm whitespace-nowrap" active-class="bg-gray-700 text-green-400">{{ langStore.t('nav.profile') || 'Perfil' }}</RouterLink>
            <RouterLink v-if="auth.user.role === 'ADMIN'" to="/admin" class="px-2 py-1.5 rounded-lg text-gray-300 hover:text-white hover:bg-gray-700 transition font-medium text-sm whitespace-nowrap" active-class="bg-gray-700 text-green-400">{{ langStore.t('nav.admin') }}</RouterLink>
            
            <div class="flex items-center gap-2 bg-gray-700 px-3 py-1.5 rounded-full border border-gray-600 whitespace-nowrap">
              <span class="text-gray-300 text-xs hidden lg:inline">{{ langStore.t('nav.welcome') }},</span>
              <span class="font-bold text-white text-sm">{{ auth.user?.username }}</span>
              <span class="text-green-400 font-bold text-sm ml-1">{{ auth.user?.balance !== undefined ? auth.user.balance : '...' }} €</span>
            </div>


            <button @click="logout" class="bg-red-500 hover:bg-red-600 text-white px-3 py-1.5 rounded-lg transition shadow-md text-sm whitespace-nowrap">{{ langStore.t('nav.logout') }}</button>
          </div>
          <div v-else class="flex items-center gap-4">
             <RouterLink to="/login" class="bg-green-500 hover:bg-green-600 text-white px-6 py-2 rounded-lg transition font-bold shadow-lg shadow-green-500/20">Login</RouterLink>
          </div>

          <!-- Language Selector -->
          <select 
            v-model="langStore.currentLanguage" 
            @change="langStore.setLanguage($event.target.value)"
            class="bg-gray-700 text-white text-sm p-2 rounded-lg border border-gray-600 focus:border-green-500 outline-none cursor-pointer hover:bg-gray-600 transition"
          >
            <option value="es">🇪🇸 ES</option>
            <option value="gl">🐙 GL</option>
            <option value="en">🇬🇧 EN</option>
          </select>
        </div>

        <!-- Mobile Menu Button -->
        <div class="md:hidden flex items-center gap-4">
           <span v-if="auth.user" class="text-green-400 font-bold text-sm bg-gray-700 px-2 py-1 rounded-full border border-gray-600">{{ auth.user.balance !== undefined ? auth.user.balance : '...' }} €</span>
           <button @click="isMobileMenuOpen = !isMobileMenuOpen" class="text-gray-300 hover:text-white focus:outline-none p-2">

            <svg class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path v-if="!isMobileMenuOpen" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
              <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>

      <!-- Mobile Menu Dropdown -->
      <div v-if="isMobileMenuOpen" class="md:hidden absolute top-full left-0 right-0 bg-gray-800 border-b border-gray-700 shadow-xl p-4 flex flex-col gap-4 animate-fade-in-down">
          <div class="flex justify-between items-center border-b border-gray-700 pb-2">
             <span class="text-gray-400 text-sm font-medium">Language</span>
             <select 
                v-model="langStore.currentLanguage" 
                @change="langStore.setLanguage($event.target.value)"
                class="bg-gray-700 text-white text-sm p-1 rounded border border-gray-600 focus:border-green-500 outline-none"
              >
                <option value="es">Español</option>
                <option value="gl">Galego</option>
                <option value="en">English</option>
              </select>
          </div>

          <template v-if="auth.user">
            <RouterLink to="/" @click="isMobileMenuOpen = false" class="block py-3 px-4 rounded-lg hover:bg-gray-700 text-white font-medium text-lg">{{ langStore.t('nav.home') }}</RouterLink>
            <RouterLink to="/ranking" @click="isMobileMenuOpen = false" class="block py-3 px-4 rounded-lg hover:bg-gray-700 text-white font-medium text-lg">{{ langStore.t('nav.ranking') }}</RouterLink>
            <RouterLink to="/user-ranking" @click="isMobileMenuOpen = false" class="block py-3 px-4 rounded-lg hover:bg-gray-700 text-white font-medium text-lg">{{ langStore.t('nav.userRanking') || 'Usuarios' }}</RouterLink>
            <RouterLink to="/statistics" @click="isMobileMenuOpen = false" class="block py-3 px-4 rounded-lg hover:bg-gray-700 text-white font-medium text-lg">{{ langStore.t('nav.statistics') }}</RouterLink>
            <!-- <RouterLink to="/roulette" @click="isMobileMenuOpen = false" class="block py-3 px-4 rounded-lg hover:bg-gray-700 text-white font-medium text-lg">🎰 Ruleta de la Suerte</RouterLink> -->
          </template>
          <RouterLink to="/help" @click="isMobileMenuOpen = false" class="block py-3 px-4 rounded-lg hover:bg-gray-700 text-white font-medium text-lg">{{ langStore.t('nav.help') || 'Ayuda' }}</RouterLink>

          <div v-if="auth.user" class="flex flex-col gap-2 border-t border-gray-700 pt-2">
            <div class="text-gray-300 text-sm pb-2 px-4">{{ langStore.t('nav.welcome') }}, <span class="font-bold text-white text-lg">{{ auth.user.username }}</span></div>
            
            <RouterLink to="/my-bets" @click="isMobileMenuOpen = false" class="block py-3 px-4 rounded-lg hover:bg-gray-700 text-white font-medium text-lg">{{ langStore.t('nav.myBets') }}</RouterLink>
            <RouterLink to="/profile/settings" @click="isMobileMenuOpen = false" class="block py-3 px-4 rounded-lg hover:bg-gray-700 text-white font-medium text-lg">{{ langStore.t('nav.profile') || 'Gestionar Perfil' }}</RouterLink>
            <RouterLink v-if="auth.user.role === 'ADMIN'" to="/admin" @click="isMobileMenuOpen = false" class="block py-3 px-4 rounded-lg hover:bg-gray-700 text-white font-medium text-lg">{{ langStore.t('nav.admin') }}</RouterLink>
            
            <button @click="logout(); isMobileMenuOpen = false" class="w-full text-left bg-red-500/20 text-red-400 hover:bg-red-500 hover:text-white px-4 py-3 rounded-lg transition mt-2 font-bold">{{ langStore.t('nav.logout') }}</button>
          </div>
          <div v-else class="flex flex-col gap-2 border-t border-gray-700 pt-4">
            <RouterLink to="/login" @click="isMobileMenuOpen = false" class="block py-3 px-4 rounded-lg bg-green-500 hover:bg-green-600 text-white text-center font-bold text-lg shadow-lg">Login</RouterLink>
          </div>
      </div>
    </nav>
    <main class="container mx-auto p-4">
      <router-view v-slot="{ Component, route }">
        <transition 
          :name="route.meta.transition || 'fade'" 
          mode="out-in"
          @before-enter="onBeforeEnter"
          @enter="onEnter"
          @leave="onLeave"
        >
          <component :is="Component" :key="route.path" />
        </transition>
      </router-view>
    </main>

    <!-- Bet Slip -->
    <transition name="slide-up">
      <div v-if="betStore.selectedOutcomes.length > 0" class="fixed bottom-0 left-0 right-0 bg-gray-800 border-t border-gray-700 p-4 shadow-2xl z-50">
        <div class="container mx-auto flex flex-col lg:flex-row justify-between items-center gap-4">
          <!-- Selected Outcomes -->
          <div class="flex-1 w-full lg:w-auto">
            <div class="flex justify-between items-center mb-2 lg:mb-2">
              <h3 class="text-lg font-bold text-white animate-fade-in">{{ langStore.t('betSlip.title') }} ({{ betStore.selectedOutcomes.length }})</h3>
              <button @click="betStore.clearSlip" class="text-xs text-red-400 hover:text-red-300 underline lg:hidden transition-colors">Limpiar</button>
            </div>
            <div class="flex flex-wrap gap-2 max-h-32 overflow-y-auto lg:max-h-none">
              <span v-for="outcome in betStore.selectedOutcomes" :key="outcome.id" class="bg-gray-700 text-xs text-gray-300 px-2 py-1 rounded border border-gray-600 flex items-center gap-2 animate-scale-in hover-scale transition-all">
                <span class="truncate max-w-[150px]">{{ outcome.description }}</span>
                <span class="text-green-400">@{{ outcome.odds }}</span>
                <button @click="betStore.removeOutcome(outcome.id)" class="text-red-400 hover:text-red-300 font-bold bg-gray-600 rounded-full w-4 h-4 flex items-center justify-center leading-none transition-colors hover-scale">&times;</button>
              </span>
            </div>
          </div>
          
          <!-- Bet Actions -->
          <div class="flex flex-col sm:flex-row items-stretch sm:items-center gap-4 w-full lg:w-auto border-t lg:border-t-0 border-gray-700 pt-4 lg:pt-0">
            <div class="flex justify-between sm:justify-end items-center gap-6 flex-1">
               <div class="text-right animate-fade-in-left">
                 <div class="text-xs text-gray-400">{{ langStore.t('betSlip.totalOdds') }}</div>
                 <div class="text-xl font-bold text-yellow-400">{{ betStore.totalOdds }}</div>
               </div>
               
               <div class="animate-fade-in-up delay-100">
                  <label class="block text-xs text-gray-400 mb-1 text-right">{{ langStore.t('betSlip.amount') }}</label>
                  <input v-model="betStore.betAmount" type="number" min="1" class="w-full sm:w-24 bg-gray-900 text-white p-2 rounded border border-gray-600 focus:border-green-500 outline-none text-right transition-all" />
               </div>
   
               <div class="text-right animate-fade-in-right">
                  <div class="text-xs text-gray-400">{{ langStore.t('betSlip.potentialReturn') }}</div>
                  <div class="text-xl font-bold text-green-400">{{ betStore.potentialReturn }} €</div>
               </div>
            </div>
   
            <button @click="betStore.placeBet" class="w-full sm:w-auto bg-gradient-to-r from-green-500 to-green-600 hover:from-green-400 hover:to-green-500 text-white font-bold py-3 px-6 rounded-lg shadow-lg shadow-green-500/20 transition-all hover-scale animate-pulse whitespace-nowrap">
              {{ langStore.t('betSlip.placeBet') }}
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- News Modal -->
    <div v-if="showNewsModal" class="fixed inset-0 bg-black bg-opacity-90 z-[60] flex items-center justify-center p-4 animate-fade-in-up">
      <div class="bg-gray-800 border-2 border-green-500 rounded-xl max-w-lg w-full p-4 md:p-6 relative shadow-2xl flex flex-col max-h-[90vh]">
        <button @click="showNewsModal = false" class="absolute top-2 right-2 md:top-4 md:right-4 text-gray-400 hover:text-white text-xl p-2">&times;</button>
        
        <h2 class="text-2xl md:text-3xl font-bold bg-gradient-to-r from-green-400 to-blue-500 bg-clip-text text-transparent mb-4 md:mb-6 pr-8">
          ¡Grandes Novedades en FurbitoBET!
        </h2>

        <div class="space-y-4 text-gray-200 mb-6 md:mb-8 overflow-y-auto custom-scrollbar flex-1">
           <div class="flex items-start gap-4 p-3 bg-gray-700/50 rounded-lg">
              <span class="text-3xl shrink-0">🎯</span>
              <div>
                <h3 class="font-bold text-blue-400">Categorías Colapsables</h3>
                <p class="text-sm text-gray-300">Los eventos muestran categorías colapsadas. Haz clic para expandir solo las que te interesen. ¡Navegación más limpia!</p>
              </div>
           </div>

           <div class="flex items-start gap-4 p-3 bg-gray-700/50 rounded-lg">
              <span class="text-3xl shrink-0">🧠</span>
              <div>
                <h3 class="font-bold text-purple-400">Validación Inteligente</h3>
                <p class="text-sm text-gray-300">Sistema mejorado que previene combinaciones ilógicas. Sustitución automática sin alertas molestas.</p>
              </div>
           </div>

           <div class="flex items-start gap-4 p-3 bg-gray-700/50 rounded-lg">
              <span class="text-3xl shrink-0">📊</span>
              <div>
                <h3 class="font-bold text-yellow-400">Cuotas Más Realistas</h3>
                <p class="text-sm text-gray-300">Sistema renovado con distribución de Poisson. Considera ataque, defensa y forma del equipo. ¡Más justas!</p>
              </div>
           </div>

           <div class="flex items-start gap-4 p-3 bg-gray-700/50 rounded-lg">
              <span class="text-3xl shrink-0">📅</span>
              <div>
                <h3 class="font-bold text-green-400">Historial Mejorado</h3>
                <p class="text-sm text-gray-300">Tus apuestas ahora muestran fecha exacta, categoría en badges morados y ordenación cronológica.</p>
              </div>
           </div>

           <div class="flex items-start gap-4 p-3 bg-gray-700/50 rounded-lg">
              <span class="text-3xl shrink-0">🗑️</span>
              <div>
                <h3 class="font-bold text-red-400">Eventos Cancelados</h3>
                <p class="text-sm text-gray-300">Si se cancela un evento, tus apuestas se anulan automáticamente y recibes tu dinero + email de notificación.</p>
              </div>
           </div>

           <div class="flex items-start gap-4 p-3 bg-gray-700/50 rounded-lg">
              <span class="text-3xl shrink-0">📱</span>
              <div>
                <h3 class="font-bold text-blue-400">¡Instala la App!</h3>
                <p class="text-sm text-gray-300">Acceso rápido desde tu pantalla de inicio. Android: botón "Instalar". iPhone: Compartir → Añadir.</p>
              </div>
           </div>
        </div>

        <button @click="showNewsModal = false" class="w-full bg-gradient-to-r from-green-500 to-blue-600 hover:from-green-400 hover:to-blue-500 text-white font-bold py-3 rounded-lg shadow-lg transition transform hover:scale-105 shrink-0">
          ¡Entendido!
        </button>
        
        <p class="text-center text-xs text-gray-500 mt-4 shrink-0">
          Este aviso desaparecerá después de {{ 5 - currentNewsCount }} inicios más.
        </p>
      </div>
    </div>

    <!-- PWA Install Prompt -->
    <InstallPrompt />
  </div>
</template>

<style scoped>
/* Page Transition Styles */

/* Fade transition (default) */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Slide transition */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.slide-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

/* Scale transition */
.scale-enter-active,
.scale-leave-active {
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.scale-enter-from {
  opacity: 0;
  transform: scale(0.95);
}

.scale-leave-to {
  opacity: 0;
  transform: scale(1.05);
}

/* Fade-in-down animation for mobile menu */
@keyframes fade-in-down {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in-down {
  animation: fade-in-down 0.3s ease-out;
}

/* Fade-in-up animation for modals */
@keyframes fade-in-up {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.animate-fade-in-up {
  animation: fade-in-up 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

/* Custom scrollbar for news modal */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(34, 197, 94, 0.5);
  border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: rgba(34, 197, 94, 0.7);
}

/* Hide scrollbar for mobile menu */
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}

.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

/* Slide-up transition for bet slip */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.slide-up-enter-from {
  opacity: 0;
  transform: translateY(100%);
}

.slide-up-leave-to {
  opacity: 0;
  transform: translateY(100%);
}

/* Slide-left transition (forward navigation) */
.slide-left-enter-active,
.slide-left-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-left-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

/* Slide-right transition (back navigation) */
.slide-right-enter-active,
.slide-right-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-right-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.slide-right-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
