<script setup>
import { useLanguageStore } from '../stores/language'

const langStore = useLanguageStore()

function formatText(text) {
  if (!text) return ''
  // Replace **text** with styled span
  return text.replace(/\*\*(.*?)\*\*/g, '<span class="text-green-400 font-bold">$1</span>')
}
</script>

<template>
  <div class="space-y-12 animate-fade-in py-12 px-4 md:px-8">
    
    <!-- Title Section -->
    <div class="text-center mb-16 relative">
      <div class="absolute inset-0 flex items-center justify-center opacity-10 pointer-events-none">
         <div class="w-64 h-64 bg-green-500 rounded-full blur-3xl"></div>
      </div>
      <h1 class="relative text-5xl md:text-6xl font-extrabold bg-gradient-to-r from-green-400 via-blue-500 to-purple-600 bg-clip-text text-transparent mb-6 tracking-tight">
        {{ langStore.t('help.title') }}
      </h1>
      <p class="relative text-gray-300 text-xl max-w-3xl mx-auto leading-relaxed">
        {{ langStore.t('help.intro') }}
      </p>
    </div>

    <!-- Content Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-8 max-w-7xl mx-auto">
      
      <!-- Card 1: Registration -->
      <div class="group bg-gray-800/50 backdrop-blur-sm p-8 rounded-3xl border border-gray-700 hover:border-green-500/50 transition-all duration-300 hover:shadow-2xl hover:shadow-green-500/10 hover:-translate-y-1">
        <div class="flex items-center gap-4 mb-6">
          <div class="w-14 h-14 bg-blue-500/20 rounded-2xl flex items-center justify-center text-3xl group-hover:scale-110 transition-transform">📝</div>
          <h2 class="text-2xl font-bold text-white">{{ langStore.t('help.registration.title') }}</h2>
        </div>
        <p class="text-gray-300 mb-6 text-lg">{{ langStore.t('help.registration.desc') }}</p>
        <ul class="space-y-3">
          <li v-for="(item, i) in langStore.t('help.registration.details')" :key="i" class="flex items-start gap-3 text-gray-400">
            <span class="text-green-500 mt-1">✓</span>
            <span v-html="formatText(item)"></span>
          </li>
        </ul>
      </div>

      <!-- Card 2: Betting -->
      <div class="group bg-gray-800/50 backdrop-blur-sm p-8 rounded-3xl border border-gray-700 hover:border-green-500/50 transition-all duration-300 hover:shadow-2xl hover:shadow-green-500/10 hover:-translate-y-1">
        <div class="flex items-center gap-4 mb-6">
          <div class="w-14 h-14 bg-yellow-500/20 rounded-2xl flex items-center justify-center text-3xl group-hover:scale-110 transition-transform">💰</div>
          <h2 class="text-2xl font-bold text-white">{{ langStore.t('help.betting.title') }}</h2>
        </div>
        <p class="text-gray-300 mb-6 text-lg">{{ langStore.t('help.betting.desc') }}</p>
        <ul class="space-y-3">
           <li v-for="(item, i) in langStore.t('help.betting.details')" :key="i" class="flex items-start gap-3 text-gray-400">
             <span class="text-green-500 mt-1">✓</span>
             <span v-html="formatText(item)"></span>
           </li>
        </ul>
      </div>

      <!-- Card 3: Ranking -->
      <div class="group bg-gray-800/50 backdrop-blur-sm p-8 rounded-3xl border border-gray-700 hover:border-green-500/50 transition-all duration-300 hover:shadow-2xl hover:shadow-green-500/10 hover:-translate-y-1">
        <div class="flex items-center gap-4 mb-6">
          <div class="w-14 h-14 bg-orange-500/20 rounded-2xl flex items-center justify-center text-3xl group-hover:scale-110 transition-transform">🏆</div>
          <h2 class="text-2xl font-bold text-white">{{ langStore.t('help.ranking.title') }}</h2>
        </div>
         <p class="text-gray-300 mb-6 text-lg">{{ langStore.t('help.ranking.desc') }}</p>
         <ul class="space-y-3">
            <li v-for="(item, i) in langStore.t('help.ranking.details')" :key="i" class="flex items-start gap-3 text-gray-400">
              <span class="text-green-500 mt-1">✓</span>
              <span v-html="formatText(item)"></span>
            </li>
        </ul>
      </div>

      <!-- Card 4: Stats -->
      <div class="group bg-gray-800/50 backdrop-blur-sm p-8 rounded-3xl border border-gray-700 hover:border-green-500/50 transition-all duration-300 hover:shadow-2xl hover:shadow-green-500/10 hover:-translate-y-1">
        <div class="flex items-center gap-4 mb-6">
          <div class="w-14 h-14 bg-purple-500/20 rounded-2xl flex items-center justify-center text-3xl group-hover:scale-110 transition-transform">📊</div>
          <h2 class="text-2xl font-bold text-white">{{ langStore.t('help.stats.title') }}</h2>
        </div>
         <p class="text-gray-300 mb-6 text-lg">{{ langStore.t('help.stats.desc') }}</p>
         <ul class="space-y-3">
           <li v-for="(item, i) in langStore.t('help.stats.details')" :key="i" class="flex items-start gap-3 text-gray-400">
             <span class="text-green-500 mt-1">✓</span>
             <span v-html="formatText(item)"></span>
           </li>
         </ul>
      </div>

    </div>

    <!-- Contact Section -->
    <div class="text-center mt-16">
       <div class="inline-block bg-gray-800 p-8 rounded-2xl border border-gray-700 max-w-4xl hover:border-green-500 transition duration-300">
         <h3 class="text-2xl font-bold text-white mb-2">{{ langStore.t('help.contact') }}</h3>
         <p class="text-gray-400 text-lg">{{ langStore.t('help.contactDesc') }}</p>
       </div>
    </div>
  </div>
</template>
