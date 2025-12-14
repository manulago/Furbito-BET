<template>
  <div class="flex items-center justify-center" :class="containerClass">
    <div class="relative">
      <!-- Spinner Circle -->
      <div 
        class="loading-spinner"
        :style="{
          width: size + 'px',
          height: size + 'px',
          borderWidth: borderWidth + 'px'
        }"
      ></div>
      
      <!-- Optional Text -->
      <p v-if="text" class="text-gray-400 text-sm mt-3 text-center animate-pulse">
        {{ text }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

const props = defineProps({
  size: {
    type: Number,
    default: 40
  },
  borderWidth: {
    type: Number,
    default: 3
  },
  text: {
    type: String,
    default: ''
  },
  containerClass: {
    type: String,
    default: 'py-8'
  }
})
</script>

<style scoped>
.loading-spinner {
  border: 3px solid rgba(255, 255, 255, 0.1);
  border-top-color: #22c55e;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
