<script setup>
import { useLanguageStore } from '../stores/language'
import { ref, onMounted } from 'vue'
import InstallPrompt from '../components/InstallPrompt.vue'

const langStore = useLanguageStore()
const isIOS = ref(false)
const isAndroid = ref(false)

onMounted(() => {
  const userAgent = window.navigator.userAgent.toLowerCase()
  isIOS.value = /iphone|ipad|ipod/.test(userAgent)
  isAndroid.value = /android/.test(userAgent)
})

function formatText(text) {
  if (!text) return ''
  // Replace **text** with styled span
  return text.replace(/\*\*(.*?)\*\*/g, '<span class="text-green-400 font-bold">$1</span>')
}
</script>

<template>
  <div class="space-y-8 md:space-y-12 animate-fade-in py-6 md:py-12 px-4 md:px-8">
    
    <!-- Title Section -->
    <div class="text-center mb-8 md:mb-16 relative">
      <div class="absolute inset-0 flex items-center justify-center opacity-10 pointer-events-none">
         <div class="w-64 h-64 bg-green-500 rounded-full blur-3xl"></div>
      </div>
      <h1 class="relative text-3xl md:text-5xl lg:text-6xl font-extrabold bg-gradient-to-r from-green-400 via-blue-500 to-purple-600 bg-clip-text text-transparent mb-4 md:mb-6 tracking-tight">
        {{ langStore.t('help.title') }}
      </h1>
      <p class="relative text-gray-300 text-base md:text-xl max-w-3xl mx-auto leading-relaxed">
        {{ langStore.t('help.intro') }}
      </p>
    </div>

    <!-- Content Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-8 max-w-7xl mx-auto">
      
      <!-- Card 1: Registration -->
      <div class="group bg-gray-800/50 backdrop-blur-sm p-5 md:p-8 rounded-2xl md:rounded-3xl border border-gray-700 hover:border-green-500/50 transition-all duration-300 hover:shadow-2xl hover:shadow-green-500/10 hover:-translate-y-1">
        <div class="flex items-center gap-3 md:gap-4 mb-4 md:mb-6">
          <div class="w-10 h-10 md:w-14 md:h-14 bg-blue-500/20 rounded-xl md:rounded-2xl flex items-center justify-center text-2xl md:text-3xl group-hover:scale-110 transition-transform">📝</div>
          <h2 class="text-xl md:text-2xl font-bold text-white">{{ langStore.t('help.registration.title') }}</h2>
        </div>
        <p class="text-gray-300 mb-4 md:mb-6 text-sm md:text-lg">{{ langStore.t('help.registration.desc') }}</p>
        <ul class="space-y-3">
          <li v-for="(item, i) in langStore.t('help.registration.details')" :key="i" class="flex items-start gap-3 text-gray-400">
            <span class="text-green-500 mt-1">✓</span>
            <span v-html="formatText(item)"></span>
          </li>
        </ul>
      </div>

      <!-- Card 2: Betting -->
      <div class="group bg-gray-800/50 backdrop-blur-sm p-5 md:p-8 rounded-2xl md:rounded-3xl border border-gray-700 hover:border-green-500/50 transition-all duration-300 hover:shadow-2xl hover:shadow-green-500/10 hover:-translate-y-1">
        <div class="flex items-center gap-3 md:gap-4 mb-4 md:mb-6">
          <div class="w-10 h-10 md:w-14 md:h-14 bg-yellow-500/20 rounded-xl md:rounded-2xl flex items-center justify-center text-2xl md:text-3xl group-hover:scale-110 transition-transform">💰</div>
          <h2 class="text-xl md:text-2xl font-bold text-white">{{ langStore.t('help.betting.title') }}</h2>
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
      <div class="group bg-gray-800/50 backdrop-blur-sm p-5 md:p-8 rounded-2xl md:rounded-3xl border border-gray-700 hover:border-green-500/50 transition-all duration-300 hover:shadow-2xl hover:shadow-green-500/10 hover:-translate-y-1">
        <div class="flex items-center gap-3 md:gap-4 mb-4 md:mb-6">
          <div class="w-10 h-10 md:w-14 md:h-14 bg-orange-500/20 rounded-xl md:rounded-2xl flex items-center justify-center text-2xl md:text-3xl group-hover:scale-110 transition-transform">🏆</div>
          <h2 class="text-xl md:text-2xl font-bold text-white">{{ langStore.t('help.ranking.title') }}</h2>
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
      <div class="group bg-gray-800/50 backdrop-blur-sm p-5 md:p-8 rounded-2xl md:rounded-3xl border border-gray-700 hover:border-green-500/50 transition-all duration-300 hover:shadow-2xl hover:shadow-green-500/10 hover:-translate-y-1">
        <div class="flex items-center gap-3 md:gap-4 mb-4 md:mb-6">
          <div class="w-10 h-10 md:w-14 md:h-14 bg-purple-500/20 rounded-xl md:rounded-2xl flex items-center justify-center text-2xl md:text-3xl group-hover:scale-110 transition-transform">📊</div>
          <h2 class="text-xl md:text-2xl font-bold text-white">{{ langStore.t('help.stats.title') }}</h2>
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

    <!-- PWA Installation Section -->
    <div class="max-w-6xl mx-auto mt-8 md:mt-12">
      <div class="group bg-gradient-to-br from-blue-600/20 to-purple-600/20 backdrop-blur-sm p-5 md:p-8 rounded-2xl md:rounded-3xl border-2 border-blue-500/50 hover:border-blue-400 transition-all duration-300 hover:shadow-2xl hover:shadow-blue-500/20">
        <div class="flex items-center gap-3 md:gap-4 mb-4 md:mb-6">
          <div class="w-12 h-12 md:w-16 md:h-16 bg-blue-500/30 rounded-xl md:rounded-2xl flex items-center justify-center text-3xl md:text-4xl group-hover:scale-110 transition-transform">📱</div>
          <div>
            <h2 class="text-xl md:text-2xl lg:text-3xl font-bold text-white">{{ langStore.t('help.pwa.title') }}</h2>
            <p class="text-sm md:text-base text-blue-300">{{ langStore.t('help.pwa.subtitle') }}</p>
          </div>
        </div>

        <p class="text-gray-300 mb-6 md:mb-8 text-sm md:text-lg" v-html="langStore.t('help.pwa.description')">
        </p>

        <!-- Benefits -->
        <div class="grid grid-cols-2 md:grid-cols-4 gap-3 md:gap-4 mb-6 md:mb-8">
          <div class="bg-gray-800/50 p-3 md:p-4 rounded-lg md:rounded-xl border border-gray-700 hover:border-green-500 transition">
            <div class="text-xl md:text-2xl mb-1 md:mb-2">⚡</div>
            <h4 class="font-bold text-white mb-1 text-sm md:text-base">{{ langStore.t('help.pwa.benefits.faster.title') }}</h4>
            <p class="text-xs md:text-sm text-gray-400">{{ langStore.t('help.pwa.benefits.faster.desc') }}</p>
          </div>
          <div class="bg-gray-800/50 p-3 md:p-4 rounded-lg md:rounded-xl border border-gray-700 hover:border-blue-500 transition">
            <div class="text-xl md:text-2xl mb-1 md:mb-2">📴</div>
            <h4 class="font-bold text-white mb-1 text-sm md:text-base">{{ langStore.t('help.pwa.benefits.offline.title') }}</h4>
            <p class="text-xs md:text-sm text-gray-400">{{ langStore.t('help.pwa.benefits.offline.desc') }}</p>
          </div>
          <div class="bg-gray-800/50 p-3 md:p-4 rounded-lg md:rounded-xl border border-gray-700 hover:border-purple-500 transition">
            <div class="text-xl md:text-2xl mb-1 md:mb-2">🎯</div>
            <h4 class="font-bold text-white mb-1 text-sm md:text-base">{{ langStore.t('help.pwa.benefits.noBrowser.title') }}</h4>
            <p class="text-xs md:text-sm text-gray-400">{{ langStore.t('help.pwa.benefits.noBrowser.desc') }}</p>
          </div>
          <div class="bg-gray-800/50 p-3 md:p-4 rounded-lg md:rounded-xl border border-gray-700 hover:border-yellow-500 transition">
            <div class="text-xl md:text-2xl mb-1 md:mb-2">🌍</div>
            <h4 class="font-bold text-white mb-1 text-sm md:text-base">{{ langStore.t('help.pwa.benefits.multiplatform.title') }}</h4>
            <p class="text-xs md:text-sm text-gray-400">{{ langStore.t('help.pwa.benefits.multiplatform.desc') }}</p>
          </div>
        </div>

        <!-- Installation Instructions -->
        <div class="bg-gray-900/50 p-4 md:p-6 rounded-xl md:rounded-2xl border border-gray-700">
          <h3 class="text-lg md:text-2xl font-bold text-white mb-4 md:mb-6 text-center">{{ langStore.t('help.pwa.instructions.title') }}</h3>
          
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-4 md:gap-6">
            <!-- Android Instructions -->
            <div class="bg-gray-800/50 p-4 md:p-5 rounded-lg md:rounded-xl border border-gray-700 hover:border-green-500 transition">
              <h4 class="font-bold text-green-400 mb-3 md:mb-4 flex items-center gap-2 text-base md:text-lg">
                <span class="text-2xl md:text-3xl">🤖</span> {{ langStore.t('help.pwa.instructions.android.title') }}
              </h4>
              <ol class="space-y-2 md:space-y-3 text-gray-300 text-sm md:text-base">
                <li class="flex items-start gap-2 md:gap-3">
                  <span class="text-green-400 font-bold shrink-0 bg-green-900/30 w-5 h-5 md:w-6 md:h-6 rounded-full flex items-center justify-center text-xs md:text-sm">1</span>
                  <span v-html="langStore.t('help.pwa.instructions.android.step1')"></span>
                </li>
                <li class="flex items-start gap-2 md:gap-3">
                  <span class="text-green-400 font-bold shrink-0 bg-green-900/30 w-5 h-5 md:w-6 md:h-6 rounded-full flex items-center justify-center text-xs md:text-sm">2</span>
                  <span v-html="langStore.t('help.pwa.instructions.android.step2')"></span>
                </li>
                <li class="flex items-start gap-2 md:gap-3">
                  <span class="text-green-400 font-bold shrink-0 bg-green-900/30 w-5 h-5 md:w-6 md:h-6 rounded-full flex items-center justify-center text-xs md:text-sm">3</span>
                  <span v-html="langStore.t('help.pwa.instructions.android.step3')"></span>
                </li>
                <li class="flex items-start gap-2 md:gap-3">
                  <span class="text-green-400 font-bold shrink-0 bg-green-900/30 w-5 h-5 md:w-6 md:h-6 rounded-full flex items-center justify-center text-xs md:text-sm">4</span>
                  <span v-html="langStore.t('help.pwa.instructions.android.step4')"></span>
                </li>
              </ol>
            </div>

            <!-- iOS Instructions -->
            <div class="bg-gray-800/50 p-4 md:p-5 rounded-lg md:rounded-xl border border-gray-700 hover:border-blue-500 transition">
              <h4 class="font-bold text-blue-400 mb-3 md:mb-4 flex items-center gap-2 text-base md:text-lg">
                <span class="text-2xl md:text-3xl">🍎</span> {{ langStore.t('help.pwa.instructions.ios.title') }}
              </h4>
              <ol class="space-y-2 md:space-y-3 text-gray-300 text-sm md:text-base">
                <li class="flex items-start gap-2 md:gap-3">
                  <span class="text-blue-400 font-bold shrink-0 bg-blue-900/30 w-5 h-5 md:w-6 md:h-6 rounded-full flex items-center justify-center text-xs md:text-sm">1</span>
                  <span v-html="langStore.t('help.pwa.instructions.ios.step1')"></span>
                </li>
                <li class="flex items-start gap-2 md:gap-3">
                  <span class="text-blue-400 font-bold shrink-0 bg-blue-900/30 w-5 h-5 md:w-6 md:h-6 rounded-full flex items-center justify-center text-xs md:text-sm">2</span>
                  <span v-html="langStore.t('help.pwa.instructions.ios.step2')"></span>
                </li>
                <li class="flex items-start gap-2 md:gap-3">
                  <span class="text-blue-400 font-bold shrink-0 bg-blue-900/30 w-5 h-5 md:w-6 md:h-6 rounded-full flex items-center justify-center text-xs md:text-sm">3</span>
                  <span v-html="langStore.t('help.pwa.instructions.ios.step3')"></span>
                </li>
                <li class="flex items-start gap-2 md:gap-3">
                  <span class="text-blue-400 font-bold shrink-0 bg-blue-900/30 w-5 h-5 md:w-6 md:h-6 rounded-full flex items-center justify-center text-xs md:text-sm">4</span>
                  <span v-html="langStore.t('help.pwa.instructions.ios.step4')"></span>
                </li>
              </ol>
            </div>

            <!-- Windows Instructions -->
            <div class="bg-gray-800/50 p-4 md:p-5 rounded-lg md:rounded-xl border border-gray-700 hover:border-purple-500 transition">
              <h4 class="font-bold text-purple-400 mb-3 md:mb-4 flex items-center gap-2 text-base md:text-lg">
                <span class="text-2xl md:text-3xl">🪟</span> {{ langStore.t('help.pwa.instructions.windows.title') }}
              </h4>
              <ol class="space-y-2 md:space-y-3 text-gray-300 text-sm md:text-base">
                <li class="flex items-start gap-2 md:gap-3">
                  <span class="text-purple-400 font-bold shrink-0 bg-purple-900/30 w-5 h-5 md:w-6 md:h-6 rounded-full flex items-center justify-center text-xs md:text-sm">1</span>
                  <span v-html="langStore.t('help.pwa.instructions.windows.step1')"></span>
                </li>
                <li class="flex items-start gap-2 md:gap-3">
                  <span class="text-purple-400 font-bold shrink-0 bg-purple-900/30 w-5 h-5 md:w-6 md:h-6 rounded-full flex items-center justify-center text-xs md:text-sm">2</span>
                  <span v-html="langStore.t('help.pwa.instructions.windows.step2')"></span>
                </li>
                <li class="flex items-start gap-2 md:gap-3">
                  <span class="text-purple-400 font-bold shrink-0 bg-purple-900/30 w-5 h-5 md:w-6 md:h-6 rounded-full flex items-center justify-center text-xs md:text-sm">3</span>
                  <span v-html="langStore.t('help.pwa.instructions.windows.step3')"></span>
                </li>
                <li class="flex items-start gap-2 md:gap-3">
                  <span class="text-purple-400 font-bold shrink-0 bg-purple-900/30 w-5 h-5 md:w-6 md:h-6 rounded-full flex items-center justify-center text-xs md:text-sm">4</span>
                  <span v-html="langStore.t('help.pwa.instructions.windows.step4')"></span>
                </li>
              </ol>
              <div class="mt-2 md:mt-3 p-2 bg-purple-900/20 rounded text-xs text-purple-300" v-html="langStore.t('help.pwa.instructions.windows.tip')">
              </div>
            </div>

            <!-- Mac Instructions -->
            <div class="bg-gray-800/50 p-4 md:p-5 rounded-lg md:rounded-xl border border-gray-700 hover:border-yellow-500 transition">
              <h4 class="font-bold text-yellow-400 mb-3 md:mb-4 flex items-center gap-2 text-base md:text-lg">
                <span class="text-2xl md:text-3xl">🍏</span> {{ langStore.t('help.pwa.instructions.mac.title') }}
              </h4>
              <ol class="space-y-2 md:space-y-3 text-gray-300 text-sm md:text-base">
                <li class="flex items-start gap-2 md:gap-3">
                  <span class="text-yellow-400 font-bold shrink-0 bg-yellow-900/30 w-5 h-5 md:w-6 md:h-6 rounded-full flex items-center justify-center text-xs md:text-sm">1</span>
                  <span v-html="langStore.t('help.pwa.instructions.mac.step1')"></span>
                </li>
                <li class="flex items-start gap-2 md:gap-3">
                  <span class="text-yellow-400 font-bold shrink-0 bg-yellow-900/30 w-5 h-5 md:w-6 md:h-6 rounded-full flex items-center justify-center text-xs md:text-sm">2</span>
                  <span v-html="langStore.t('help.pwa.instructions.mac.step2')"></span>
                </li>
                <li class="flex items-start gap-2 md:gap-3">
                  <span class="text-yellow-400 font-bold shrink-0 bg-yellow-900/30 w-5 h-5 md:w-6 md:h-6 rounded-full flex items-center justify-center text-xs md:text-sm">3</span>
                  <span v-html="langStore.t('help.pwa.instructions.mac.step3')"></span>
                </li>
                <li class="flex items-start gap-2 md:gap-3">
                  <span class="text-yellow-400 font-bold shrink-0 bg-yellow-900/30 w-5 h-5 md:w-6 md:h-6 rounded-full flex items-center justify-center text-xs md:text-sm">4</span>
                  <span v-html="langStore.t('help.pwa.instructions.mac.step4')"></span>
                </li>
              </ol>
              <div class="mt-2 md:mt-3 p-2 bg-yellow-900/20 rounded text-xs text-yellow-300" v-html="langStore.t('help.pwa.instructions.mac.tip')">
              </div>
            </div>

            <!-- Linux Instructions -->
            <div class="bg-gray-800/50 p-4 md:p-5 rounded-lg md:rounded-xl border border-gray-700 hover:border-orange-500 transition lg:col-span-2">
              <h4 class="font-bold text-orange-400 mb-3 md:mb-4 flex items-center gap-2 text-base md:text-lg">
                <span class="text-2xl md:text-3xl">🐧</span> {{ langStore.t('help.pwa.instructions.linux.title') }}
              </h4>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-3 md:gap-4">
                <div>
                  <p class="text-xs md:text-sm font-bold text-orange-300 mb-2">{{ langStore.t('help.pwa.instructions.linux.chromeBrowser') }}</p>
                  <ol class="space-y-1 md:space-y-2 text-gray-300 text-xs md:text-sm">
                    <li class="flex items-start gap-2">
                      <span class="text-orange-400 font-bold shrink-0">1.</span>
                      <span v-html="langStore.t('help.pwa.instructions.linux.chromeStep1')"></span>
                    </li>
                    <li class="flex items-start gap-2">
                      <span class="text-orange-400 font-bold shrink-0">2.</span>
                      <span v-html="langStore.t('help.pwa.instructions.linux.chromeStep2')"></span>
                    </li>
                    <li class="flex items-start gap-2">
                      <span class="text-orange-400 font-bold shrink-0">3.</span>
                      <span v-html="langStore.t('help.pwa.instructions.linux.chromeStep3')"></span>
                    </li>
                    <li class="flex items-start gap-2">
                      <span class="text-orange-400 font-bold shrink-0">4.</span>
                      <span v-html="langStore.t('help.pwa.instructions.linux.chromeStep4')"></span>
                    </li>
                  </ol>
                </div>
                <div>
                  <p class="text-xs md:text-sm font-bold text-orange-300 mb-2">{{ langStore.t('help.pwa.instructions.linux.firefoxBrowser') }}</p>
                  <ol class="space-y-1 md:space-y-2 text-gray-300 text-xs md:text-sm">
                    <li class="flex items-start gap-2">
                      <span class="text-orange-400 font-bold shrink-0">1.</span>
                      <span v-html="langStore.t('help.pwa.instructions.linux.firefoxStep1')"></span>
                    </li>
                    <li class="flex items-start gap-2">
                      <span class="text-orange-400 font-bold shrink-0">2.</span>
                      <span v-html="langStore.t('help.pwa.instructions.linux.firefoxStep2')"></span>
                    </li>
                    <li class="flex items-start gap-2">
                      <span class="text-orange-400 font-bold shrink-0">3.</span>
                      <span v-html="langStore.t('help.pwa.instructions.linux.firefoxStep3')"></span>
                    </li>
                    <li class="flex items-start gap-2">
                      <span class="text-orange-400 font-bold shrink-0">4.</span>
                      <span v-html="langStore.t('help.pwa.instructions.linux.firefoxStep4')"></span>
                    </li>
                  </ol>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Call to Action -->
        <div class="text-center mt-6 md:mt-8">
          <div class="bg-gradient-to-r from-green-900/30 to-blue-900/30 p-3 md:p-4 rounded-lg md:rounded-xl border border-green-700/50 mb-3 md:mb-4">
            <p class="text-gray-300 text-xs md:text-sm" v-html="langStore.t('help.pwa.tip')">
            </p>
          </div>
          <p class="text-gray-400 text-xs">
            {{ langStore.t('help.pwa.troubleshooting') }}
          </p>
        </div>
      </div>
    </div>

    <!-- Contact Section -->
    <div class="text-center mt-8 md:mt-16">
       <div class="inline-block bg-gray-800 p-5 md:p-8 rounded-xl md:rounded-2xl border border-gray-700 max-w-4xl hover:border-green-500 transition duration-300">
         <h3 class="text-xl md:text-2xl font-bold text-white mb-2">{{ langStore.t('help.contact') }}</h3>
         <p class="text-gray-400 text-sm md:text-lg">{{ langStore.t('help.contactDesc') }}</p>
       </div>
    </div>

    <!-- Install Prompt Component -->
    <InstallPrompt />
  </div>
</template>
