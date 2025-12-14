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

    <!-- PWA Installation Section -->
    <div class="max-w-6xl mx-auto mt-12">
      <div class="group bg-gradient-to-br from-blue-600/20 to-purple-600/20 backdrop-blur-sm p-8 rounded-3xl border-2 border-blue-500/50 hover:border-blue-400 transition-all duration-300 hover:shadow-2xl hover:shadow-blue-500/20">
        <div class="flex items-center gap-4 mb-6">
          <div class="w-16 h-16 bg-blue-500/30 rounded-2xl flex items-center justify-center text-4xl group-hover:scale-110 transition-transform">📱</div>
          <div>
            <h2 class="text-3xl font-bold text-white">Instala la App en Cualquier Dispositivo</h2>
            <p class="text-blue-300">Acceso rápido desde tu pantalla de inicio - Móvil y PC</p>
          </div>
        </div>

        <p class="text-gray-300 mb-8 text-lg">
          FurbitoBET es una PWA (Progressive Web App) que puedes instalar en <strong>cualquier dispositivo</strong>: Android, iPhone, Windows, Mac o Linux. 
          Disfruta de acceso instantáneo sin abrir el navegador.
        </p>

        <!-- Benefits -->
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
          <div class="bg-gray-800/50 p-4 rounded-xl border border-gray-700 hover:border-green-500 transition">
            <div class="text-2xl mb-2">⚡</div>
            <h4 class="font-bold text-white mb-1">Más Rápido</h4>
            <p class="text-sm text-gray-400">Carga instantánea desde tu pantalla de inicio</p>
          </div>
          <div class="bg-gray-800/50 p-4 rounded-xl border border-gray-700 hover:border-blue-500 transition">
            <div class="text-2xl mb-2">📴</div>
            <h4 class="font-bold text-white mb-1">Funciona Offline</h4>
            <p class="text-sm text-gray-400">Visualiza contenido aunque pierdas conexión</p>
          </div>
          <div class="bg-gray-800/50 p-4 rounded-xl border border-gray-700 hover:border-purple-500 transition">
            <div class="text-2xl mb-2">🎯</div>
            <h4 class="font-bold text-white mb-1">Sin Navegador</h4>
            <p class="text-sm text-gray-400">Experiencia de app nativa completa</p>
          </div>
          <div class="bg-gray-800/50 p-4 rounded-xl border border-gray-700 hover:border-yellow-500 transition">
            <div class="text-2xl mb-2">🌍</div>
            <h4 class="font-bold text-white mb-1">Multiplataforma</h4>
            <p class="text-sm text-gray-400">Funciona en todos tus dispositivos</p>
          </div>
        </div>

        <!-- Installation Instructions -->
        <div class="bg-gray-900/50 p-6 rounded-2xl border border-gray-700">
          <h3 class="text-2xl font-bold text-white mb-6 text-center">📖 Instrucciones de Instalación por Plataforma</h3>
          
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <!-- Android Instructions -->
            <div class="bg-gray-800/50 p-5 rounded-xl border border-gray-700 hover:border-green-500 transition">
              <h4 class="font-bold text-green-400 mb-4 flex items-center gap-2 text-lg">
                <span class="text-3xl">🤖</span> Android (Chrome/Edge)
              </h4>
              <ol class="space-y-3 text-gray-300">
                <li class="flex items-start gap-3">
                  <span class="text-green-400 font-bold shrink-0 bg-green-900/30 w-6 h-6 rounded-full flex items-center justify-center text-sm">1</span>
                  <span>Abre <strong>FurbitoBET</strong> en Chrome o Edge</span>
                </li>
                <li class="flex items-start gap-3">
                  <span class="text-green-400 font-bold shrink-0 bg-green-900/30 w-6 h-6 rounded-full flex items-center justify-center text-sm">2</span>
                  <span>Busca el botón flotante <strong>"📱 Instalar App"</strong> o el icono ⊕ en la barra de direcciones</span>
                </li>
                <li class="flex items-start gap-3">
                  <span class="text-green-400 font-bold shrink-0 bg-green-900/30 w-6 h-6 rounded-full flex items-center justify-center text-sm">3</span>
                  <span>Toca <strong>"Instalar"</strong> o <strong>"Añadir a pantalla de inicio"</strong></span>
                </li>
                <li class="flex items-start gap-3">
                  <span class="text-green-400 font-bold shrink-0 bg-green-900/30 w-6 h-6 rounded-full flex items-center justify-center text-sm">4</span>
                  <span>¡Listo! Encontrarás el icono en tu pantalla de inicio</span>
                </li>
              </ol>
            </div>

            <!-- iOS Instructions -->
            <div class="bg-gray-800/50 p-5 rounded-xl border border-gray-700 hover:border-blue-500 transition">
              <h4 class="font-bold text-blue-400 mb-4 flex items-center gap-2 text-lg">
                <span class="text-3xl">🍎</span> iPhone/iPad (Safari)
              </h4>
              <ol class="space-y-3 text-gray-300">
                <li class="flex items-start gap-3">
                  <span class="text-blue-400 font-bold shrink-0 bg-blue-900/30 w-6 h-6 rounded-full flex items-center justify-center text-sm">1</span>
                  <span>Abre <strong>FurbitoBET</strong> en Safari (⚠️ debe ser Safari)</span>
                </li>
                <li class="flex items-start gap-3">
                  <span class="text-blue-400 font-bold shrink-0 bg-blue-900/30 w-6 h-6 rounded-full flex items-center justify-center text-sm">2</span>
                  <span>Toca el botón <strong>Compartir</strong> 
                    <svg class="inline w-4 h-4 mx-1" fill="currentColor" viewBox="0 0 24 24">
                      <path d="M16 5l-1.42 1.42-1.59-1.59V16h-1.98V4.83L9.42 6.42 8 5l4-4 4 4zm4 5v11c0 1.1-.9 2-2 2H6c-1.11 0-2-.9-2-2V10c0-1.11.89-2 2-2h3v2H6v11h12V10h-3V8h3c1.1 0 2 .89 2 2z"/>
                    </svg>
                    en la barra inferior
                  </span>
                </li>
                <li class="flex items-start gap-3">
                  <span class="text-blue-400 font-bold shrink-0 bg-blue-900/30 w-6 h-6 rounded-full flex items-center justify-center text-sm">3</span>
                  <span>Desplázate y selecciona <strong>"Añadir a pantalla de inicio"</strong></span>
                </li>
                <li class="flex items-start gap-3">
                  <span class="text-blue-400 font-bold shrink-0 bg-blue-900/30 w-6 h-6 rounded-full flex items-center justify-center text-sm">4</span>
                  <span>Toca <strong>"Añadir"</strong> y ¡disfruta de la app!</span>
                </li>
              </ol>
            </div>

            <!-- Windows Instructions -->
            <div class="bg-gray-800/50 p-5 rounded-xl border border-gray-700 hover:border-purple-500 transition">
              <h4 class="font-bold text-purple-400 mb-4 flex items-center gap-2 text-lg">
                <span class="text-3xl">🪟</span> Windows (Chrome/Edge)
              </h4>
              <ol class="space-y-3 text-gray-300">
                <li class="flex items-start gap-3">
                  <span class="text-purple-400 font-bold shrink-0 bg-purple-900/30 w-6 h-6 rounded-full flex items-center justify-center text-sm">1</span>
                  <span>Abre <strong>FurbitoBET</strong> en Chrome o Edge</span>
                </li>
                <li class="flex items-start gap-3">
                  <span class="text-purple-400 font-bold shrink-0 bg-purple-900/30 w-6 h-6 rounded-full flex items-center justify-center text-sm">2</span>
                  <span>Busca el icono <strong>⊕</strong> o <strong>🖥️</strong> en la barra de direcciones</span>
                </li>
                <li class="flex items-start gap-3">
                  <span class="text-purple-400 font-bold shrink-0 bg-purple-900/30 w-6 h-6 rounded-full flex items-center justify-center text-sm">3</span>
                  <span>Haz clic en <strong>"Instalar FurbitoBET"</strong></span>
                </li>
                <li class="flex items-start gap-3">
                  <span class="text-purple-400 font-bold shrink-0 bg-purple-900/30 w-6 h-6 rounded-full flex items-center justify-center text-sm">4</span>
                  <span>La app se abrirá en una ventana independiente y aparecerá en el menú de inicio</span>
                </li>
              </ol>
              <div class="mt-3 p-2 bg-purple-900/20 rounded text-xs text-purple-300">
                💡 También puedes usar: Menú (⋮) → "Instalar FurbitoBET"
              </div>
            </div>

            <!-- Mac Instructions -->
            <div class="bg-gray-800/50 p-5 rounded-xl border border-gray-700 hover:border-yellow-500 transition">
              <h4 class="font-bold text-yellow-400 mb-4 flex items-center gap-2 text-lg">
                <span class="text-3xl">🍏</span> Mac (Chrome/Edge/Safari)
              </h4>
              <ol class="space-y-3 text-gray-300">
                <li class="flex items-start gap-3">
                  <span class="text-yellow-400 font-bold shrink-0 bg-yellow-900/30 w-6 h-6 rounded-full flex items-center justify-center text-sm">1</span>
                  <span>Abre <strong>FurbitoBET</strong> en Chrome, Edge o Safari</span>
                </li>
                <li class="flex items-start gap-3">
                  <span class="text-yellow-400 font-bold shrink-0 bg-yellow-900/30 w-6 h-6 rounded-full flex items-center justify-center text-sm">2</span>
                  <span><strong>Chrome/Edge:</strong> Busca el icono ⊕ en la barra de direcciones</span>
                </li>
                <li class="flex items-start gap-3">
                  <span class="text-yellow-400 font-bold shrink-0 bg-yellow-900/30 w-6 h-6 rounded-full flex items-center justify-center text-sm">3</span>
                  <span>Haz clic en <strong>"Instalar FurbitoBET"</strong></span>
                </li>
                <li class="flex items-start gap-3">
                  <span class="text-yellow-400 font-bold shrink-0 bg-yellow-900/30 w-6 h-6 rounded-full flex items-center justify-center text-sm">4</span>
                  <span>La app aparecerá en el Dock y en Aplicaciones</span>
                </li>
              </ol>
              <div class="mt-3 p-2 bg-yellow-900/20 rounded text-xs text-yellow-300">
                💡 Safari: Archivo → "Añadir a Dock" (macOS Sonoma+)
              </div>
            </div>

            <!-- Linux Instructions -->
            <div class="bg-gray-800/50 p-5 rounded-xl border border-gray-700 hover:border-orange-500 transition lg:col-span-2">
              <h4 class="font-bold text-orange-400 mb-4 flex items-center gap-2 text-lg">
                <span class="text-3xl">🐧</span> Linux (Chrome/Firefox/Edge)
              </h4>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <p class="text-sm font-bold text-orange-300 mb-2">Chrome/Chromium/Edge:</p>
                  <ol class="space-y-2 text-gray-300 text-sm">
                    <li class="flex items-start gap-2">
                      <span class="text-orange-400 font-bold shrink-0">1.</span>
                      <span>Abre FurbitoBET en el navegador</span>
                    </li>
                    <li class="flex items-start gap-2">
                      <span class="text-orange-400 font-bold shrink-0">2.</span>
                      <span>Busca el icono ⊕ en la barra de direcciones</span>
                    </li>
                    <li class="flex items-start gap-2">
                      <span class="text-orange-400 font-bold shrink-0">3.</span>
                      <span>Haz clic en "Instalar FurbitoBET"</span>
                    </li>
                    <li class="flex items-start gap-2">
                      <span class="text-orange-400 font-bold shrink-0">4.</span>
                      <span>La app aparecerá en tu menú de aplicaciones</span>
                    </li>
                  </ol>
                </div>
                <div>
                  <p class="text-sm font-bold text-orange-300 mb-2">Firefox:</p>
                  <ol class="space-y-2 text-gray-300 text-sm">
                    <li class="flex items-start gap-2">
                      <span class="text-orange-400 font-bold shrink-0">1.</span>
                      <span>Abre FurbitoBET en Firefox</span>
                    </li>
                    <li class="flex items-start gap-2">
                      <span class="text-orange-400 font-bold shrink-0">2.</span>
                      <span>Menú (☰) → "Instalar sitio como aplicación"</span>
                    </li>
                    <li class="flex items-start gap-2">
                      <span class="text-orange-400 font-bold shrink-0">3.</span>
                      <span>Confirma la instalación</span>
                    </li>
                    <li class="flex items-start gap-2">
                      <span class="text-orange-400 font-bold shrink-0">4.</span>
                      <span>Accede desde el menú de aplicaciones</span>
                    </li>
                  </ol>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Call to Action -->
        <div class="text-center mt-8">
          <div class="bg-gradient-to-r from-green-900/30 to-blue-900/30 p-4 rounded-xl border border-green-700/50 mb-4">
            <p class="text-gray-300 text-sm">
              💡 <strong>Tip:</strong> Una vez instalada, la app se abrirá en pantalla completa sin la barra del navegador, 
              funcionará más rápido y podrás acceder incluso sin conexión a internet.
            </p>
          </div>
          <p class="text-gray-400 text-xs">
            ¿Problemas con la instalación? Asegúrate de usar un navegador compatible (Chrome, Edge, Safari o Firefox)
          </p>
        </div>
      </div>
    </div>

    <!-- Contact Section -->
    <div class="text-center mt-16">
       <div class="inline-block bg-gray-800 p-8 rounded-2xl border border-gray-700 max-w-4xl hover:border-green-500 transition duration-300">
         <h3 class="text-2xl font-bold text-white mb-2">{{ langStore.t('help.contact') }}</h3>
         <p class="text-gray-400 text-lg">{{ langStore.t('help.contactDesc') }}</p>
       </div>
    </div>

    <!-- Install Prompt Component -->
    <InstallPrompt />
  </div>
</template>
