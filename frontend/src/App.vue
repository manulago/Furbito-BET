<script setup>
import { useAuthStore } from './stores/auth'
import { useBetStore } from './stores/bet'
import { useLanguageStore } from './stores/language'
import { useRouter } from 'vue-router'
import { onMounted, ref } from 'vue'

const auth = useAuthStore()
const betStore = useBetStore()
const langStore = useLanguageStore()
const router = useRouter()
const isMobileMenuOpen = ref(false)

onMounted(() => {
  if (auth.user) {
    auth.fetchBalance()
  }
})

function logout() {
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
        <div class="hidden md:flex items-center gap-4">
          <!-- Public Links -->
          <template v-if="auth.user">
            <router-link to="/" class="px-3 py-2 rounded-lg text-gray-300 hover:text-white hover:bg-gray-700 transition font-medium text-lg" active-class="bg-gray-700 text-green-400">{{ langStore.t('nav.home') }}</router-link>
            <RouterLink to="/ranking" class="px-3 py-2 rounded-lg text-gray-300 hover:text-white hover:bg-gray-700 transition font-medium text-lg" active-class="bg-gray-700 text-green-400">{{ langStore.t('nav.ranking') }}</RouterLink>
            <RouterLink to="/user-ranking" class="px-3 py-2 rounded-lg text-gray-300 hover:text-white hover:bg-gray-700 transition font-medium text-lg" active-class="bg-gray-700 text-green-400">{{ langStore.t('nav.userRanking') || 'Usuarios' }}</RouterLink>
            <router-link to="/statistics" class="px-3 py-2 rounded-lg text-gray-300 hover:text-white hover:bg-gray-700 transition font-medium text-lg" active-class="bg-gray-700 text-green-400">{{ langStore.t('nav.statistics') }}</router-link>
          </template>
          <router-link to="/help" class="px-3 py-2 rounded-lg text-gray-300 hover:text-white hover:bg-gray-700 transition font-medium text-lg" active-class="bg-gray-700 text-green-400">{{ langStore.t('nav.help') || 'Ayuda' }}</router-link>
          <!-- <router-link v-if="auth.user" to="/roulette" class="text-2xl hover:scale-110 transition duration-300" title="Ruleta de la Suerte">🎰</router-link> -->
          <router-link v-if="auth.isAdmin" to="/admin" class="px-3 py-2 rounded-lg text-gray-300 hover:text-yellow-400 hover:bg-gray-700 transition font-medium text-lg" active-class="bg-gray-700 text-yellow-500">{{ langStore.t('nav.admin') }}</router-link>

          <div v-if="auth.user" class="flex items-center gap-4">
            <RouterLink to="/my-bets" class="px-3 py-2 rounded-lg text-gray-300 hover:text-white hover:bg-gray-700 transition font-medium text-lg" active-class="bg-gray-700 text-green-400">{{ langStore.t('nav.myBets') }}</RouterLink>
            <RouterLink to="/profile/settings" class="px-3 py-2 rounded-lg text-gray-300 hover:text-white hover:bg-gray-700 transition font-medium text-lg" active-class="bg-gray-700 text-green-400">{{ langStore.t('nav.profile') || 'Gestionar Perfil' }}</RouterLink>
            <RouterLink v-if="auth.user.role === 'ADMIN'" to="/admin" class="px-3 py-2 rounded-lg text-gray-300 hover:text-white hover:bg-gray-700 transition font-medium text-lg" active-class="bg-gray-700 text-green-400">{{ langStore.t('nav.admin') }}</RouterLink>
            
            <div class="flex items-center gap-3 bg-gray-700 px-4 py-2 rounded-full border border-gray-600">
              <span class="text-gray-300 text-sm">{{ langStore.t('nav.welcome') }}, <span class="font-bold text-white">{{ auth.user?.username }}</span></span>
              <span class="text-green-400 font-bold text-lg">{{ auth.user?.balance !== undefined ? auth.user.balance : '...' }} €</span>
            </div>


            <button @click="logout" class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg transition shadow-md">{{ langStore.t('nav.logout') }}</button>
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
          <RouterLink to="/help" @click="isMobileMenuOpen = false" class="block py-3 px-4 rounded-lg hover:bg-gray-700 text-white font-medium text-lg">{{ langStore.t('nav.help') || 'Ayuda' }}</routerLink>

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
      <router-view></router-view>
    </main>

    <!-- Bet Slip -->
    <div v-if="betStore.selectedOutcomes.length > 0" class="fixed bottom-0 left-0 right-0 bg-gray-800 border-t border-gray-700 p-4 shadow-2xl z-50">
      <div class="container mx-auto flex flex-col lg:flex-row justify-between items-center gap-4">
        <!-- Selected Outcomes -->
        <div class="flex-1 w-full lg:w-auto">
          <div class="flex justify-between items-center mb-2 lg:mb-2">
            <h3 class="text-lg font-bold text-white">{{ langStore.t('betSlip.title') }} ({{ betStore.selectedOutcomes.length }})</h3>
            <button @click="betStore.clearSlip" class="text-xs text-red-400 hover:text-red-300 underline lg:hidden">Limpiar</button>
          </div>
          <div class="flex flex-wrap gap-2 max-h-32 overflow-y-auto lg:max-h-none">
            <span v-for="outcome in betStore.selectedOutcomes" :key="outcome.id" class="bg-gray-700 text-xs text-gray-300 px-2 py-1 rounded border border-gray-600 flex items-center gap-2">
              <span class="truncate max-w-[150px]">{{ outcome.description }}</span>
              <span class="text-green-400">@{{ outcome.odds }}</span>
              <button @click="betStore.removeOutcome(outcome.id)" class="text-red-400 hover:text-red-300 font-bold bg-gray-600 rounded-full w-4 h-4 flex items-center justify-center leading-none">&times;</button>
            </span>
          </div>
        </div>
        
        <!-- Bet Actions -->
        <div class="flex flex-col sm:flex-row items-stretch sm:items-center gap-4 w-full lg:w-auto border-t lg:border-t-0 border-gray-700 pt-4 lg:pt-0">
          <div class="flex justify-between sm:justify-end items-center gap-6 flex-1">
             <div class="text-right">
               <div class="text-xs text-gray-400">{{ langStore.t('betSlip.totalOdds') }}</div>
               <div class="text-xl font-bold text-yellow-400">{{ betStore.totalOdds }}</div>
             </div>
             
             <div>
                <label class="block text-xs text-gray-400 mb-1 text-right">{{ langStore.t('betSlip.amount') }}</label>
                <input v-model="betStore.betAmount" type="number" min="1" class="w-full sm:w-24 bg-gray-900 text-white p-2 rounded border border-gray-600 focus:border-green-500 outline-none text-right" />
             </div>
  
             <div class="text-right">
                <div class="text-xs text-gray-400">{{ langStore.t('betSlip.potentialReturn') }}</div>
                <div class="text-xl font-bold text-green-400">{{ betStore.potentialReturn }} €</div>
             </div>
          </div>
  
          <button @click="betStore.placeBet" class="w-full sm:w-auto bg-gradient-to-r from-green-500 to-green-600 hover:from-green-400 hover:to-green-500 text-white font-bold py-3 px-6 rounded-lg shadow-lg shadow-green-500/20 transition transform hover:-translate-y-0.5 whitespace-nowrap">
            {{ langStore.t('betSlip.placeBet') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
