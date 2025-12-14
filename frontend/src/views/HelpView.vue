<script setup>
import { useLanguageStore } from '../stores/language'

const langStore = useLanguageStore()
</script>

<template>
  <div class="space-y-8 animate-fade-in py-8">
    
    <!-- Title Section -->
    <div class="text-center mb-12">
      <h1 class="text-4xl md:text-5xl font-extrabold bg-gradient-to-r from-green-400 to-blue-500 bg-clip-text text-transparent mb-4">
        {{ langStore.t('help.title') || 'Ayuda y Guía' }}
      </h1>
      <p class="text-gray-400 text-lg max-w-2xl mx-auto">
        {{ langStore.t('help.subtitle') || 'Todo lo que necesitas saber para disfrutar de FurbitoBET al máximo.' }}
      </p>
    </div>

    <!-- Content Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-8 max-w-6xl mx-auto px-4">
      
      <!-- Card 1: Registration -->
      <div class="bg-gray-800 p-8 rounded-2xl shadow-lg border border-gray-700 hover:border-green-500/50 transition duration-300">
        <div class="flex items-center gap-4 mb-4">
          <span class="text-3xl">📝</span>
          <h2 class="text-2xl font-bold text-white">{{ langStore.t('help.registration.title') || 'Registro' }}</h2>
        </div>
        <p class="text-gray-300 leading-relaxed">
          {{ langStore.t('help.registration.desc') || 'Para apostar, necesitas una cuenta. Regístrate con tu correo y confirma tu identidad a través del enlace que recibirás.' }}
        </p>
        <ul class="list-disc list-inside mt-4 text-gray-400 space-y-2">
            <li>Usuario único</li>
            <li>Email válido</li>
            <li>Confirmación obligatoria</li>
        </ul>
      </div>

      <!-- Card 2: Betting -->
      <div class="bg-gray-800 p-8 rounded-2xl shadow-lg border border-gray-700 hover:border-green-500/50 transition duration-300">
        <div class="flex items-center gap-4 mb-4">
          <span class="text-3xl">💰</span>
          <h2 class="text-2xl font-bold text-white">{{ langStore.t('help.betting.title') || 'Cómo Apostar' }}</h2>
        </div>
        <p class="text-gray-300 leading-relaxed">
          {{ langStore.t('help.betting.desc') || 'Navega a la página principal, selecciona un evento y haz clic en las cuotas. Se añadirán a tu boleto de apuestas.' }}
        </p>
         <ul class="list-disc list-inside mt-4 text-gray-400 space-y-2">
            <li>Apuestas simples</li>
            <li>Apuestas combinadas (misma categoría restringida)</li>
            <li>Revisa tu "Boleto" abajo</li>
        </ul>
      </div>

      <!-- Card 3: Ranking -->
      <div class="bg-gray-800 p-8 rounded-2xl shadow-lg border border-gray-700 hover:border-green-500/50 transition duration-300">
        <div class="flex items-center gap-4 mb-4">
          <span class="text-3xl">🏆</span>
          <h2 class="text-2xl font-bold text-white">{{ langStore.t('help.ranking.title') || 'Clasificaciones' }}</h2>
        </div>
         <p class="text-gray-300 leading-relaxed">
          {{ langStore.t('help.ranking.desc') || 'Sigue la liga de cerca. Tenemos clasificaciones tanto de los equipos de la liga como de los mejores apostadores.' }}
        </p>
         <ul class="list-disc list-inside mt-4 text-gray-400 space-y-2">
            <li>Clasificación de Liga</li>
            <li>Ranking de Usuarios (¡Compite por ser el #1!)</li>
            <li>Resultados de partidos anteriores</li>
        </ul>
      </div>

      <!-- Card 4: Stats -->
      <div class="bg-gray-800 p-8 rounded-2xl shadow-lg border border-gray-700 hover:border-green-500/50 transition duration-300">
        <div class="flex items-center gap-4 mb-4">
          <span class="text-3xl">📊</span>
          <h2 class="text-2xl font-bold text-white">{{ langStore.t('help.stats.title') || 'Estadísticas' }}</h2>
        </div>
         <p class="text-gray-300 leading-relaxed">
          {{ langStore.t('help.stats.desc') || 'Consulta las estadísticas detalladas de los jugadores de Furbito FIC: goles, asistencias, tarjetas y más.' }}
        </p>
      </div>

    </div>

    <!-- Contact Section -->
    <div class="text-center mt-12 bg-gray-800 p-8 rounded-2xl border border-gray-700 max-w-4xl mx-auto">
        <h3 class="text-xl font-bold text-white mb-2">{{ langStore.t('help.contact') || '¿Necesitas más ayuda?' }}</h3>
        <p class="text-gray-400">{{ langStore.t('help.contactDesc') || 'Contacta con el administrador del sistema.' }}</p>
    </div>
  </div>
</template>
