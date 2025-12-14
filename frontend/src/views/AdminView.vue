<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useLanguageStore } from '../stores/language'

const auth = useAuthStore()
const langStore = useLanguageStore()

const activeTab = ref('events')
const events = ref([])
const eventName = ref('')
const eventDate = ref('')
const selectedEvent = ref(null) // The event currently being viewed/edited in detail
const showCreateEvent = ref(false)
const notifyEvent = ref(false)
const showAddOutcome = ref(false)

const outcomeDesc = ref('')
const outcomeOdds = ref('')
const outcomeGroup = ref('')

const users = ref([])
const selectedUser = ref(null) // The user currently being viewed/edited in detail
const selectedUserId = ref('') // Deprecated, but keeping to avoid breaking if used elsewhere
const balanceAmount = ref('')
const newPassword = ref('')

const categories = ref([])
const newCategoryName = ref('')

const editingEvent = ref(null) // For the modal/inline edit of event details
const editEventName = ref('')
const editEventDate = ref('')

// Players Logic
const players = ref([])
const showCreatePlayer = ref(false)
const editingPlayer = ref(null)
const playerForm = ref({
  name: '',
  team: '',
  goals: 0,
  assists: 0,
  matchesPlayed: 0,
  matchesStarted: 0,
  yellowCards: 0,
  redCards: 0
})

// Newsletter Logic
const showNewsletterModal = ref(false)
const newsletterSubject = ref('🎉 ¡Novedades en FurbitoBET!')
const newsletterMessage = ref(`¡Hola!

Tenemos grandes novedades en FurbitoBET que queremos compartir contigo:

📱 ¡INSTALA LA APP!
Ahora puedes instalar FurbitoBET en tu móvil o PC como una aplicación.
Acceso rápido desde tu pantalla de inicio, sin abrir el navegador.

🔹 En Android: Busca el botón "Instalar App" en la página
🔹 En iPhone: Toca Compartir → "Añadir a pantalla de inicio"

📱 MEJORA MÓVIL
Experiencia 100% optimizada para tu teléfono.
Navegación más fluida y accesible.

❓ NUEVA PÁGINA DE AYUDA
¿Dudas? Visita nuestra sección de ayuda para aprender cómo funciona todo.

⚙️ GESTIÓN DE PERFIL
Control total sobre tu cuenta.
Actualiza tus datos y preferencias fácilmente.

👀 ESPÍA A LOS MEJORES
Visita el perfil de otros usuarios desde el ranking.
Ve su historial de apuestas y estrategias.

---

¡Entra ahora y descubre todas las mejoras!
https://furbitobet.vercel.app

Saludos,
El equipo de FurbitoBET 🎰`)

const fetchPlayers = async () => {
  try {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/api/players`)
    if (response.ok) {
      players.value = await response.json()
    }
  } catch (error) {
    console.error('Error fetching players:', error)
  }
}

const createPlayer = async () => {
  if (!playerForm.value.name) return alert(langStore.t('admin.playerName') + ' is required')
  if (playerForm.value.goals < 0 || playerForm.value.assists < 0) return alert('Stats cannot be negative')


  try {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/api/players`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${auth.token}`
      },
      body: JSON.stringify(playerForm.value)
    })
    
    if (response.ok) {
      showCreatePlayer.value = false
      resetPlayerForm()
      fetchPlayers()
    } else {
      const err = await response.text()
      alert('Error creating player: ' + err)
    }
  } catch (error) {
    console.error('Error creating player:', error)
    alert('Error: ' + error.message)
  }
}

const updatePlayerStats = async () => {
  if (!editingPlayer.value) return
  if (!playerForm.value.name) return alert(langStore.t('admin.playerName') + ' is required')
  
  try {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/api/players/${editingPlayer.value.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${auth.token}`
      },
      body: JSON.stringify(playerForm.value)
    })
    
    if (response.ok) {
      editingPlayer.value = null
      resetPlayerForm()
      fetchPlayers()
    } else {
      const err = await response.text()
      alert('Error updating player: ' + err)
    }
  } catch (error) {
    console.error('Error updating player:', error)
    alert('Error: ' + error.message)
  }
}

const deletePlayer = async (id) => {
  if (!confirm(langStore.t('admin.confirmDeletePlayer'))) return
  
  try {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/api/players/${id}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${auth.token}`
      }
    })
    
    if (response.ok) {
      fetchPlayers()
    }
  } catch (error) {
    console.error('Error deleting player:', error)
  }
}

const startEditPlayer = (player) => {
  editingPlayer.value = player
  playerForm.value = { ...player }
}

const resetPlayerForm = () => {
  playerForm.value = {
    name: '',
    team: '',
    goals: 0,
    assists: 0,
    matchesPlayed: 0,
    matchesStarted: 0,
    yellowCards: 0,
    redCards: 0
  }
}

const editingOutcome = ref(null)
const editOutcomeDesc = ref('')
const editOutcomeOdds = ref('')
const editOutcomeGroup = ref('')

async function fetchUsers() {
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/users`, {
      headers: { 'Authorization': `Bearer ${auth.token}` }
    })
    if (res.ok) {
      users.value = await res.json()
      
      // Refresh selected user if active
      if (selectedUser.value) {
          const updated = users.value.find(u => u.id === selectedUser.value.id)
          if (updated) selectedUser.value = updated
          else selectedUser.value = null
      }
    } else {
      console.error('Failed to fetch users:', res.status)
    }
  } catch (error) {
    console.error('Error fetching users:', error)
  }
}


async function fetchEvents() {
  const res = await fetch(`${import.meta.env.VITE_API_URL}/api/events`)
  events.value = await res.json()
  
  // If we are viewing an event, update it with fresh data
  if (selectedEvent.value) {
    const updated = events.value.find(e => e.id === selectedEvent.value.id)
    if (updated) {
      selectedEvent.value = updated
    } else {
      selectedEvent.value = null // Event was deleted
    }
  }
}

async function fetchCategories() {
  const res = await fetch(`${import.meta.env.VITE_API_URL}/api/categories`)
  categories.value = await res.json()
}

function selectEvent(event) {
  selectedEvent.value = event
  showAddOutcome.value = false
  editingOutcome.value = null
}

async function createEvent() {
  await fetch(`${import.meta.env.VITE_API_URL}/api/admin/events`, {
    method: 'POST',
    headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${auth.token}`
    },
    body: JSON.stringify({ name: eventName.value, date: eventDate.value, notifyUsers: notifyEvent.value })
  })
  eventName.value = ''
  eventDate.value = ''
  notifyEvent.value = false
  showCreateEvent.value = false
  fetchEvents()
}

function startEditEvent(event) {
  const newName = prompt("Event Name:", event.name)
  if (newName === null) return
  
  if (newName && newName !== event.name) {
      editEventName.value = newName
      editEventDate.value = event.date // Keep date
      editingEvent.value = event // Set this to trigger the update
      updateEvent()
  }
}

async function updateEvent() {
  if (!editingEvent.value) return
  await fetch(`${import.meta.env.VITE_API_URL}/api/admin/events/${editingEvent.value.id}`, {
    method: 'PUT',
    headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${auth.token}`
    },
    body: JSON.stringify({ name: editEventName.value, date: editEventDate.value })
  })
  editingEvent.value = null
  fetchEvents()
}

async function cloneEvent(id) {
  if (!confirm(langStore.t('admin.confirmClone'))) return
  await fetch(`${import.meta.env.VITE_API_URL}/api/admin/events/${id}/clone`, { 
      method: 'POST',
      headers: { 'Authorization': `Bearer ${auth.token}` }
  })
  fetchEvents()
}

async function deleteEvent(id) {
  if (!confirm(langStore.t('admin.confirmDeleteEvent'))) return
  await fetch(`${import.meta.env.VITE_API_URL}/api/events/${id}`, { 
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${auth.token}` }
  })
  if (selectedEvent.value && selectedEvent.value.id === id) {
      selectedEvent.value = null
  }
  fetchEvents()
}

const showCreateCategory = ref(false)

async function createCategory() {
  if (!newCategoryName.value) return
  await fetch(`${import.meta.env.VITE_API_URL}/api/categories`, {
    method: 'POST',
    headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${auth.token}`
    },
    body: JSON.stringify({ name: newCategoryName.value })
  })
  newCategoryName.value = ''
  showCreateCategory.value = false
  fetchCategories()
}

async function deleteCategory(id) {
  if (!confirm(langStore.t('admin.confirmDeleteCategory'))) return
  await fetch(`${import.meta.env.VITE_API_URL}/api/categories/${id}`, { 
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${auth.token}` }
  })
  fetchCategories()
}

async function initDefaultCategories() {
    const defaults = [
        "Ganador del Partido",
        "Doble Oportunidad",
        "Apuesta sin Empate",
        "Goles - Más de",
        "Goles - Menos de",
        "Ambos Marcan"
    ]
    
    for (const name of defaults) {
        if (!categories.value.find(c => c.name === name)) {
            await fetch(`${import.meta.env.VITE_API_URL}/api/categories`, {
                method: 'POST',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.token}`
                },
                body: JSON.stringify({ name })
            })
        }
    }
    fetchCategories()
    alert('Default categories initialized!')
}

async function addOutcome() {
  if (!selectedEvent.value) return
  await fetch(`${import.meta.env.VITE_API_URL}/api/admin/events/${selectedEvent.value.id}/outcomes`, {
    method: 'POST',
    headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${auth.token}`
    },
    body: JSON.stringify({ 
      description: outcomeDesc.value, 
      odds: parseFloat(outcomeOdds.value),
      outcomeGroup: outcomeGroup.value || null
    })
  })
  outcomeDesc.value = ''
  outcomeOdds.value = ''
  // outcomeGroup.value = '' // Keep category selected for convenience
  showAddOutcome.value = false
  fetchEvents()
}

function startEditOutcome(outcome) {
  editingOutcome.value = outcome
  editOutcomeDesc.value = outcome.description
  editOutcomeOdds.value = outcome.odds
  editOutcomeGroup.value = outcome.outcomeGroup || ''
}

async function updateOutcome() {
  if (!editingOutcome.value) return
  await fetch(`${import.meta.env.VITE_API_URL}/api/admin/outcomes/${editingOutcome.value.id}`, {
    method: 'PUT',
    headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${auth.token}`
    },
    body: JSON.stringify({ 
      description: editOutcomeDesc.value, 
      odds: parseFloat(editOutcomeOdds.value),
      outcomeGroup: editOutcomeGroup.value || null
    })
  })
  editingOutcome.value = null
  fetchEvents()
}

async function updateBalance(userId) {
  if (!balanceAmount.value) return
  const res = await fetch(`${import.meta.env.VITE_API_URL}/api/users/${userId}/balance`, {
    method: 'PUT',
    headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${auth.token}`
    },
    body: balanceAmount.value
  })
  
  if (res.ok) {
      const updatedUser = await res.json()
      if (selectedUser.value && selectedUser.value.id === userId) {
          selectedUser.value = updatedUser
      }
      balanceAmount.value = ''
      fetchUsers()
      alert('Balance updated!')
  }
}

async function resetPassword(userId) {
  if (!newPassword.value) return
  await fetch(`${import.meta.env.VITE_API_URL}/api/users/${userId}/password`, {
    method: 'PUT',
    headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${auth.token}`
    },
    body: newPassword.value
  })
  newPassword.value = ''
  alert('Password reset!')
}

async function sendTestEmail(email) {
  if (!confirm(`Send test email to ${email}?`)) return
  const res = await fetch(`${import.meta.env.VITE_API_URL}/api/admin/test-email?to=${encodeURIComponent(email)}`, {
    method: 'POST',
    headers: { 
        'Authorization': `Bearer ${auth.token}`
    }
  })
  if (res.ok) {
      alert('Email sent successfully! Check inbox (and spam).')
  } else {
      alert('Failed to send email. Check backend logs.')
  }
}

async function deleteUser(id) {
  if (!confirm('Delete this user? This action cannot be undone.')) return
  const res = await fetch(`${import.meta.env.VITE_API_URL}/api/users/${id}`, { 
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${auth.token}` }
  })
  
  if (res.ok) {
      if (selectedUser.value && selectedUser.value.id === id) {
          selectedUser.value = null
      }
      fetchUsers()
  } else {
      const msg = await res.text()
      try {
          const json = JSON.parse(msg)
          alert('Failed to delete user: ' + (json.message || json.error || msg))
      } catch (e) {
          alert('Failed to delete user: ' + msg)
      }
  }
}

async function deleteOutcome(id) {
  if (!confirm(langStore.t('admin.confirmDeleteOutcome'))) return
  await fetch(`${import.meta.env.VITE_API_URL}/api/admin/outcomes/${id}`, { 
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${auth.token}` }
  })
  fetchEvents()
}

async function settleOutcome(id, status) {
  if (!confirm(`Mark outcome as ${status}?`)) return
  await fetch(`${import.meta.env.VITE_API_URL}/api/admin/outcomes/${id}/status?status=${status}`, { 
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${auth.token}` }
  })
  fetchEvents()
}

const resolvingEvent = ref(null)
const resolveHomeGoals = ref(0)
const resolveAwayGoals = ref(0)

function startResolveEvent(event) {
  resolvingEvent.value = event
  resolveHomeGoals.value = 0
  resolveAwayGoals.value = 0
}

async function resolveEvent() {
  if (!resolvingEvent.value) return
  if (!confirm(langStore.t('admin.confirmResolve') + ` ${resolvingEvent.value.name} ${resolveHomeGoals.value}-${resolveAwayGoals.value}?`)) return
  
  await fetch(`${import.meta.env.VITE_API_URL}/api/admin/events/${resolvingEvent.value.id}/resolve`, {
    method: 'POST',
    headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${auth.token}`
    },
    body: JSON.stringify({ 
      homeGoals: resolveHomeGoals.value, 
      awayGoals: resolveAwayGoals.value 
    })
  })
  
  resolvingEvent.value = null
  fetchEvents()
  alert(langStore.t('admin.eventResolved'))
}

async function syncEvents() {
  if (!confirm('¿Forzar sincronización de eventos? Esto buscará nuevos partidos y generará cuotas y jugadores.')) return
  
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/admin/sync-events`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${auth.token}` }
    })
    
    if (res.ok) {
        alert('Sincronización iniciada correctamente.')
        fetchEvents()
    } else {
        alert('Error al iniciar sincronización.')
    }
  } catch (e) {
    alert('Error de conexión.')
  }
}

async function sendNewsletter() {
  // Open modal to customize message
  showNewsletterModal.value = true
}

async function confirmSendNewsletter() {
  if (!newsletterSubject.value.trim() || !newsletterMessage.value.trim()) {
    alert('⚠️ El asunto y el mensaje no pueden estar vacíos.')
    return
  }

  const confirmMessage = `¿Enviar email a TODOS los usuarios?\n\n` +
                        `Asunto: ${newsletterSubject.value}\n\n` +
                        `Esto enviará un correo a cada usuario registrado (excepto admins).\n\n` +
                        `⚠️ Esta acción no se puede deshacer.`
  
  if (!confirm(confirmMessage)) return
  
  showNewsletterModal.value = false
  
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/admin/send-newsletter`, {
      method: 'POST',
      headers: { 
        'Authorization': `Bearer ${auth.token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        subject: newsletterSubject.value,
        message: newsletterMessage.value
      })
    })
    
    if (res.ok) {
        const message = await res.text()
        alert(`✅ ${message}\n\nRevisa la consola del backend para más detalles.`)
    } else {
        const error = await res.text()
        alert(`❌ Error al enviar newsletter:\n${error}`)
    }
  } catch (e) {
    alert(`❌ Error de conexión:\n${e.message}`)
  }
}

// News Modal Settings
const newsModalEnabled = ref(true)
const loadingNewsStatus = ref(false)

async function fetchNewsModalStatus() {
  loadingNewsStatus.value = true
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/admin/news-modal-status`, {
      headers: { 'Authorization': `Bearer ${auth.token}` }
    })
    if (res.ok) {
      const data = await res.json()
      newsModalEnabled.value = data.enabled
    }
  } catch (e) {
    console.error('Error fetching news modal status:', e)
  } finally {
    loadingNewsStatus.value = false
  }
}

async function toggleNewsModal() {
  const action = newsModalEnabled.value ? 'disable' : 'enable'
  const endpoint = newsModalEnabled.value 
    ? `${import.meta.env.VITE_API_URL}/api/admin/disable-news-modal`
    : `${import.meta.env.VITE_API_URL}/api/admin/enable-news-modal`
  
  const confirmMsg = newsModalEnabled.value
    ? '¿Desactivar el modal de novedades para TODOS los usuarios?\n\nEsto evitará que el modal aparezca al iniciar sesión.'
    : '¿Reactivar el modal de novedades?\n\nLos usuarios volverán a ver el modal al iniciar sesión (hasta 5 veces).'
  
  if (!confirm(confirmMsg)) return
  
  try {
    const res = await fetch(endpoint, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${auth.token}` }
    })
    
    if (res.ok) {
      newsModalEnabled.value = !newsModalEnabled.value
      alert(`✅ Modal de novedades ${newsModalEnabled.value ? 'activado' : 'desactivado'} correctamente`)
    } else {
      alert('❌ Error al cambiar el estado del modal')
    }
  } catch (e) {
    alert(`❌ Error: ${e.message}`)
  }
}

async function sendNewsEmail() {
  const confirmMsg = '📧 ¿Enviar email de NOVEDADES a todos los usuarios?\n\n' +
                     'Esto enviará un correo con las últimas novedades de FurbitoBET.\n\n' +
                     '⚠️ Esta acción no se puede deshacer.'
  
  if (!confirm(confirmMsg)) return
  
  try {
    const res = await fetch(`${import.meta.env.VITE_API_URL}/api/admin/send-news-email`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${auth.token}` }
    })
    
    if (res.ok) {
      const message = await res.text()
      alert(`✅ ${message}\n\nRevisa la consola del backend para más detalles.`)
    } else {
      const error = await res.text()
      alert(`❌ Error al enviar emails:\n${error}`)
    }
  } catch (e) {
    alert(`❌ Error de conexión:\n${e.message}`)
  }
}

onMounted(() => {
  fetchEvents()
  fetchUsers()
  fetchCategories()
  fetchPlayers()
  fetchNewsModalStatus()
})
</script>

<template>
  <div class="space-y-6 md:space-y-8">
    <h2 class="text-2xl md:text-3xl font-bold text-white">{{ langStore.t('admin.dashboard') }}</h2>

    <!-- Tabs -->
    <div class="flex border-b border-gray-700 overflow-x-auto scrollbar-hide">
      <button @click="activeTab = 'events'"
        :class="['px-3 md:px-4 py-2 font-medium text-sm md:text-base whitespace-nowrap', activeTab === 'events' ? 'text-green-400 border-b-2 border-green-400' : 'text-gray-400 hover:text-white']">
        {{ langStore.t('admin.events') }}
      </button>
      <button @click="activeTab = 'users'"
        :class="['px-3 md:px-4 py-2 font-medium text-sm md:text-base whitespace-nowrap', activeTab === 'users' ? 'text-blue-400 border-b-2 border-blue-400' : 'text-gray-400 hover:text-white']">
        {{ langStore.t('admin.users') }}
      </button>
      <button @click="activeTab = 'categories'"
        :class="['px-3 md:px-4 py-2 font-medium text-sm md:text-base whitespace-nowrap', activeTab === 'categories' ? 'text-yellow-400 border-b-2 border-yellow-400' : 'text-gray-400 hover:text-white']">
        {{ langStore.t('admin.categories') }}
      </button>
      <button @click="activeTab = 'players'"
        :class="['px-3 md:px-4 py-2 font-medium text-sm md:text-base whitespace-nowrap', activeTab === 'players' ? 'text-yellow-400 border-b-2 border-yellow-400' : 'text-gray-400 hover:text-white']">
        {{ langStore.t('admin.players') }}
      </button>
      <button @click="activeTab = 'settings'"
        :class="['px-3 md:px-4 py-2 font-medium text-sm md:text-base whitespace-nowrap', activeTab === 'settings' ? 'text-purple-400 border-b-2 border-purple-400' : 'text-gray-400 hover:text-white']">
        ⚙️ Configuración
      </button>
    </div>

    <!-- Events Tab -->
    <div v-if="activeTab === 'events'" class="space-y-4 md:space-y-6">
      
      <!-- Top Actions -->
      <div v-if="!selectedEvent" class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3">
        <h3 class="text-xl md:text-2xl font-bold text-white">{{ langStore.t('admin.events') }}</h3>
        <div class="flex gap-2 w-full sm:w-auto">
            <button @click="syncEvents" class="flex-1 sm:flex-none bg-blue-600 hover:bg-blue-700 text-white px-3 md:px-4 py-2 rounded font-bold shadow flex items-center justify-center gap-2 text-sm md:text-base">
              <span>↻</span> <span class="hidden sm:inline">Sincronizar</span>
            </button>
            <button @click="showCreateEvent = true" class="flex-1 sm:flex-none bg-green-500 hover:bg-green-600 text-white px-3 md:px-4 py-2 rounded font-bold shadow flex items-center justify-center gap-2 text-sm md:text-base">
              <span>+</span> <span class="hidden sm:inline">{{ langStore.t('admin.createEvent') }}</span>
            </button>
        </div>
      </div>

      <!-- Create Event Modal -->
      <div v-if="showCreateEvent" class="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50 p-4">
        <div class="bg-gray-800 p-6 rounded-lg shadow-xl border border-gray-700 w-full max-w-md">
          <h3 class="text-xl font-bold mb-4 text-green-400">{{ langStore.t('admin.newEvent') }}</h3>
          <div class="space-y-4">
            <div>
              <label class="block text-sm text-gray-400 mb-1">{{ langStore.t('admin.eventName') }}</label>
              <input v-model="eventName" placeholder="e.g. Madrid vs Barça" class="w-full bg-gray-700 text-white p-2 rounded border border-gray-600 focus:border-green-500 outline-none" />
            </div>
            <div>
              <label class="block text-sm text-gray-400 mb-1">{{ langStore.t('admin.dateTime') }}</label>
              <input v-model="eventDate" type="datetime-local" class="w-full bg-gray-700 text-white p-2 rounded border border-gray-600 focus:border-green-500 outline-none" />
            </div>
            <div class="flex items-center gap-2">
              <input type="checkbox" id="notifyEvent" v-model="notifyEvent" class="w-4 h-4 rounded border-gray-600 text-green-500 focus:ring-green-500 bg-gray-700" />
              <label for="notifyEvent" class="text-sm text-gray-300">Notificar usuarios por email</label>
            </div>
            <div class="flex justify-end gap-3 mt-6">
              <button @click="showCreateEvent = false" class="px-4 py-2 text-gray-300 hover:text-white">{{ langStore.t('admin.cancel') }}</button>
              <button @click="createEvent" class="bg-green-500 hover:bg-green-600 px-6 py-2 rounded text-white font-bold">{{ langStore.t('admin.create') }}</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Resolve Event Modal -->
      <div v-if="resolvingEvent" class="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50 p-4">
        <div class="bg-gray-800 p-6 rounded-lg shadow-xl border border-blue-500 w-full max-w-md">
          <h3 class="text-xl font-bold mb-4 text-blue-400">{{ langStore.t('admin.simulateResult') }}</h3>
          <p class="text-white mb-4 font-bold">{{ resolvingEvent.name }}</p>
          <div class="flex gap-4 items-center justify-center mb-6">
            <div class="flex flex-col items-center">
               <label class="text-gray-400 text-sm mb-1">{{ langStore.t('admin.home') }}</label>
               <input v-model="resolveHomeGoals" type="number" class="bg-gray-700 text-white p-2 rounded w-20 text-center text-xl font-bold border border-gray-600 focus:border-blue-500 outline-none" />
            </div>
            <span class="text-2xl text-gray-500">-</span>
            <div class="flex flex-col items-center">
               <label class="text-gray-400 text-sm mb-1">{{ langStore.t('admin.away') }}</label>
               <input v-model="resolveAwayGoals" type="number" class="bg-gray-700 text-white p-2 rounded w-20 text-center text-xl font-bold border border-gray-600 focus:border-blue-500 outline-none" />
            </div>
          </div>
          <div class="flex justify-end gap-3">
            <button @click="resolvingEvent = null" class="px-4 py-2 text-gray-300 hover:text-white">{{ langStore.t('admin.cancel') }}</button>
            <button @click="resolveEvent" class="bg-blue-600 hover:bg-blue-500 px-6 py-2 rounded text-white font-bold">{{ langStore.t('admin.resolveSettle') }}</button>
          </div>
        </div>
      </div>

      <!-- Event List -->
      <div v-if="!selectedEvent" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div v-for="event in events" :key="event.id" 
             @click="selectEvent(event)"
             class="bg-gray-800 p-4 rounded-lg border border-gray-700 hover:border-blue-500 cursor-pointer transition shadow-lg group relative overflow-hidden">
          
          <div class="absolute top-0 right-0 p-2 opacity-0 group-hover:opacity-100 transition">
             <span class="text-xs text-blue-400">{{ langStore.t('admin.clickToEdit') }}</span>
          </div>

          <div class="flex justify-between items-start mb-2">
            <span class="text-xs font-mono text-gray-400 bg-gray-900 px-2 py-1 rounded">{{ new Date(event.date).toLocaleDateString() }}</span>
            <span class="text-xs font-bold uppercase px-2 py-1 rounded" :class="{
                'bg-yellow-900 text-yellow-400': event.status === 'UPCOMING',
                'bg-green-900 text-green-400': event.status === 'COMPLETED',
                'bg-red-900 text-red-400': event.status === 'CANCELLED'
            }">{{ event.status }}</span>
          </div>
          
          <h4 class="text-lg font-bold text-white mb-1">{{ event.name }}</h4>
          <p class="text-sm text-gray-500">{{ new Date(event.date).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}) }}</p>
          
          <div class="mt-4 flex gap-2">
             <button @click.stop="cloneEvent(event.id)" class="text-xs bg-purple-900 text-purple-300 px-2 py-1 rounded hover:bg-purple-800">{{ langStore.t('admin.clone') }}</button>
             <button @click.stop="deleteEvent(event.id)" class="text-xs bg-red-900 text-red-300 px-2 py-1 rounded hover:bg-red-800">{{ langStore.t('admin.delete') }}</button>
          </div>
        </div>
      </div>

      <!-- Event Editor (Detail View) -->
      <div v-if="selectedEvent" class="bg-gray-800 rounded-lg shadow-xl border border-gray-700 overflow-hidden">
        <!-- Header -->
        <div class="bg-gray-900 p-4 md:p-6 border-b border-gray-700 flex flex-col md:flex-row justify-between items-start gap-4">
          <div class="flex-1">
            <button @click="selectedEvent = null" class="text-gray-400 hover:text-white mb-2 flex items-center gap-1 text-sm">
              &larr; {{ langStore.t('admin.backToEvents') }}
            </button>
            <h2 class="text-xl md:text-2xl font-bold text-white flex flex-wrap items-center gap-2 md:gap-3">
              <span class="break-words">{{ selectedEvent.name }}</span>
              <span class="text-xs md:text-sm font-normal bg-gray-700 px-2 py-1 rounded text-gray-300 whitespace-nowrap">{{ selectedEvent.status }}</span>
            </h2>
            <p class="text-gray-400 text-xs md:text-sm mt-1">{{ new Date(selectedEvent.date).toLocaleString() }}</p>
          </div>
          <div class="flex gap-2 w-full md:w-auto">
             <button @click="startEditEvent(selectedEvent)" class="flex-1 md:flex-none bg-blue-600 hover:bg-blue-500 text-white px-2 md:px-3 py-1 rounded text-xs md:text-sm font-bold">{{ langStore.t('admin.editDetails') }}</button>
             <button v-if="selectedEvent.status === 'UPCOMING'" @click="startResolveEvent(selectedEvent)" class="flex-1 md:flex-none bg-green-600 hover:bg-green-500 text-white px-2 md:px-3 py-1 rounded text-xs md:text-sm font-bold whitespace-nowrap">{{ langStore.t('admin.simulateResult') }}</button>
          </div>
        </div>

        <!-- Outcomes Section -->
        <div class="p-6">
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-xl font-bold text-white">{{ langStore.t('admin.outcomes') }}</h3>
            <button @click="showAddOutcome = true" class="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded text-sm font-bold shadow">
              {{ langStore.t('admin.addOutcome') }}
            </button>
          </div>

          <!-- Add Outcome Form (Inline) -->
          <div v-if="showAddOutcome" class="bg-gray-700 p-3 md:p-4 rounded-lg mb-4 md:mb-6 border border-blue-500 animate-fade-in">
            <h4 class="text-blue-300 font-bold mb-3 text-sm md:text-base">{{ langStore.t('admin.newOutcome') }}</h4>
            <div class="grid grid-cols-1 gap-3 md:gap-4 mb-4">
              <div>
                <label class="block text-xs text-gray-400 mb-1">{{ langStore.t('admin.description') }}</label>
                <input v-model="outcomeDesc" placeholder="e.g. Home Win" class="w-full bg-gray-600 text-white p-2 rounded border border-gray-500 focus:border-blue-400 outline-none" />
              </div>
              <div>
                <label class="block text-xs text-gray-400 mb-1">{{ langStore.t('admin.category') }}</label>
                <select v-model="outcomeGroup" class="w-full bg-gray-600 text-white p-2 rounded border border-gray-500 focus:border-blue-400 outline-none">
                  <option value="" disabled>Select Category</option>
                  <option v-for="category in categories" :key="category.id" :value="category.name">{{ category.name }}</option>
                </select>
              </div>
              <div>
                <label class="block text-xs text-gray-400 mb-1">{{ langStore.t('admin.odds') }}</label>
                <input v-model="outcomeOdds" type="number" step="0.01" placeholder="1.50" class="w-full bg-gray-600 text-white p-2 rounded border border-gray-500 focus:border-blue-400 outline-none" />
              </div>
            </div>
            <div class="flex justify-end gap-2">
              <button @click="showAddOutcome = false" class="text-gray-300 hover:text-white px-3 py-1">{{ langStore.t('admin.cancel') }}</button>
              <button @click="addOutcome" class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-1 rounded font-bold">{{ langStore.t('admin.addOutcome') }}</button>
            </div>
          </div>

          <!-- Outcomes List -->
          <div class="space-y-2">
            <div v-if="!selectedEvent.outcomes || selectedEvent.outcomes.length === 0" class="text-gray-500 text-center py-8 italic">
              No outcomes defined for this event.
            </div>
            
            <div v-for="outcome in selectedEvent.outcomes" :key="outcome.id" 
                 class="bg-gray-700 p-4 rounded-lg flex flex-col md:flex-row justify-between items-center gap-4 border border-gray-600 hover:border-gray-500 transition">
              
              <div v-if="editingOutcome && editingOutcome.id === outcome.id" class="flex-1 w-full grid grid-cols-1 md:grid-cols-3 gap-2">
                  <input v-model="editOutcomeDesc" class="bg-gray-600 text-white p-2 rounded" />
                  <select v-model="editOutcomeGroup" class="bg-gray-600 text-white p-2 rounded">
                     <option v-for="category in categories" :key="category.id" :value="category.name">{{ category.name }}</option>
                  </select>
                  <input v-model="editOutcomeOdds" type="number" step="0.01" class="bg-gray-600 text-white p-2 rounded" />
              </div>
              <div v-else class="flex-1">
                <div class="flex items-center gap-2">
                   <span class="text-white font-bold text-lg">{{ outcome.description }}</span>
                   <span v-if="outcome.outcomeGroup" class="text-xs bg-gray-600 text-gray-300 px-2 py-0.5 rounded border border-gray-500">{{ outcome.outcomeGroup }}</span>
                </div>
                <div class="text-sm text-gray-400 mt-1">
                  {{ langStore.t('admin.odds') }}: <span class="text-yellow-400 font-bold">{{ outcome.odds }}</span>
                  <span class="ml-2 px-2 py-0.5 rounded text-xs uppercase font-bold" :class="{
                    'bg-yellow-900 text-yellow-400': outcome.status === 'PENDING',
                    'bg-green-900 text-green-400': outcome.status === 'WON',
                    'bg-red-900 text-red-400': outcome.status === 'LOST',
                    'bg-gray-700 text-gray-300': outcome.status === 'VOID'
                  }">{{ outcome.status }}</span>
                </div>
              </div>

              <div class="flex gap-2 shrink-0">
                <template v-if="editingOutcome && editingOutcome.id === outcome.id">
                   <button @click="updateOutcome" class="bg-green-500 hover:bg-green-600 text-white px-3 py-1 rounded text-sm font-bold">{{ langStore.t('admin.save') }}</button>
                   <button @click="editingOutcome = null" class="bg-gray-500 hover:bg-gray-600 text-white px-3 py-1 rounded text-sm">{{ langStore.t('admin.cancel') }}</button>
                </template>
                <template v-else>
                   <button @click="startEditOutcome(outcome)" class="bg-blue-600 hover:bg-blue-500 text-white px-3 py-1 rounded text-sm">{{ langStore.t('admin.edit') }}</button>
                   <button @click="deleteOutcome(outcome.id)" class="bg-red-600 hover:bg-red-500 text-white px-3 py-1 rounded text-sm">{{ langStore.t('admin.delete') }}</button>
                   
                   <div v-if="outcome.status === 'PENDING'" class="flex gap-1 ml-2 border-l border-gray-600 pl-2">
                      <button @click="settleOutcome(outcome.id, 'WON')" class="bg-green-700 hover:bg-green-600 text-white px-2 py-1 rounded text-xs" title="Mark as Won">W</button>
                      <button @click="settleOutcome(outcome.id, 'LOST')" class="bg-red-700 hover:bg-red-600 text-white px-2 py-1 rounded text-xs" title="Mark as Lost">L</button>
                      <button @click="settleOutcome(outcome.id, 'VOID')" class="bg-gray-600 hover:bg-gray-500 text-white px-2 py-1 rounded text-xs" title="Mark as Void(Anular)">V</button>
                   </div>
                </template>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Users Tab -->
    <div v-if="activeTab === 'users'" class="space-y-6">
      
      <!-- Top Actions -->
      <div v-if="!selectedUser" class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3">
        <h3 class="text-xl md:text-2xl font-bold text-white">{{ langStore.t('admin.users') }}</h3>
        <button @click="sendNewsletter" class="bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-500 hover:to-pink-500 text-white px-4 py-2 rounded font-bold shadow flex items-center gap-2 text-sm md:text-base">
          <span>📧</span> Enviar Novedades por Email
        </button>
      </div>

      <!-- User List -->
      <div v-if="!selectedUser" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3 md:gap-4">
        <div v-for="user in users" :key="user.id" 
             @click="selectedUser = user"
             class="bg-gray-800 p-4 rounded-lg border border-gray-700 hover:border-purple-500 cursor-pointer transition shadow-lg group relative overflow-hidden">
          
          <div class="absolute top-0 right-0 p-2 opacity-0 group-hover:opacity-100 transition">
             <span class="text-xs text-purple-400">{{ langStore.t('admin.clickToManage') }}</span>
          </div>

          <div class="flex items-center gap-4 mb-3">
            <div class="bg-purple-600 w-10 h-10 rounded-full flex items-center justify-center text-lg font-bold text-white uppercase shrink-0">
              {{ user.username.charAt(0) }}
            </div>
            <div class="overflow-hidden">
              <h4 class="text-lg font-bold text-white truncate">{{ user.username }}</h4>
              <p class="text-xs text-gray-400 truncate">{{ user.email }}</p>
            </div>
          </div>
          
          <div class="flex justify-between items-center text-sm">
             <span class="text-gray-400">{{ langStore.t('admin.currentBalance') }}: <span class="text-green-400 font-bold">{{ user.balance }} €</span></span>
             <span class="px-2 py-0.5 rounded text-xs uppercase font-bold" :class="{
                'bg-purple-900 text-purple-300': user.role === 'ADMIN',
                'bg-blue-900 text-blue-300': user.role === 'USER'
             }">{{ user.role }}</span>
          </div>
        </div>
      </div>

      <!-- User Detail View -->
      <div v-if="selectedUser" class="bg-gray-800 rounded-lg shadow-xl border border-gray-700 overflow-hidden">
        <!-- Header -->
        <div class="bg-gray-900 p-6 border-b border-gray-700 flex justify-between items-start">
          <div>
            <button @click="selectedUser = null" class="text-gray-400 hover:text-white mb-2 flex items-center gap-1 text-sm">
              &larr; {{ langStore.t('admin.backToUsers') }}
            </button>
            <div class="flex items-center gap-4">
               <div class="bg-purple-600 w-16 h-16 rounded-full flex items-center justify-center text-3xl font-bold text-white uppercase">
                  {{ selectedUser.username.charAt(0) }}
               </div>
               <div>
                  <h2 class="text-2xl font-bold text-white">{{ selectedUser.username }}</h2>
                  <p class="text-gray-400">{{ selectedUser.email }}</p>
               </div>
            </div>
          </div>
          <div class="text-right">
             <p class="text-sm text-gray-400">{{ langStore.t('admin.role') }}</p>
             <span class="px-3 py-1 rounded text-sm uppercase font-bold" :class="{
                'bg-purple-900 text-purple-300': selectedUser.role === 'ADMIN',
                'bg-blue-900 text-blue-300': selectedUser.role === 'USER'
             }">{{ selectedUser.role }}</span>
          </div>
        </div>

        <!-- Actions Section -->
        <div class="p-6 grid grid-cols-1 md:grid-cols-2 gap-8">
           
           <!-- Balance Management -->
           <div class="bg-gray-700 p-6 rounded-lg border border-gray-600">
              <h3 class="text-xl font-bold text-green-400 mb-4">{{ langStore.t('admin.manageBalance') }}</h3>
              <p class="text-gray-300 mb-4">{{ langStore.t('admin.currentBalance') }}: <span class="text-white font-bold text-2xl">{{ selectedUser.balance }} €</span></p>
              
              <div class="flex gap-2">
                 <input v-model="balanceAmount" type="number" placeholder="Amount to Add" class="flex-1 bg-gray-600 text-white p-2 rounded border border-gray-500 focus:border-green-400 outline-none" />
                 <button @click="updateBalance(selectedUser.id)" class="bg-green-600 hover:bg-green-500 text-white px-4 py-2 rounded font-bold">{{ langStore.t('admin.addFunds') }}</button>
              </div>
              <p class="text-xs text-gray-400 mt-2">Enter a negative amount to deduct funds.</p>
           </div>

           <!-- Security Management -->
           <div class="bg-gray-700 p-6 rounded-lg border border-gray-600">
              <h3 class="text-xl font-bold text-red-400 mb-4">{{ langStore.t('admin.security') }}</h3>
              
              <div class="mb-6">
                 <label class="block text-sm text-gray-300 mb-1">{{ langStore.t('admin.resetPassword') }}</label>
                 <div class="flex gap-2">
                    <input v-model="newPassword" type="text" :placeholder="langStore.t('admin.newPassword')" class="flex-1 bg-gray-600 text-white p-2 rounded border border-gray-500 focus:border-red-400 outline-none" />
                    <button @click="resetPassword(selectedUser.id)" class="bg-yellow-600 hover:bg-yellow-500 text-white px-4 py-2 rounded font-bold">{{ langStore.t('admin.reset') }}</button>
                 </div>
              </div>

              <div class="mb-6 border-t border-gray-600 pt-4">
                 <h4 class="text-blue-300 font-bold mb-2">{{ langStore.t('admin.emailSystem') }}</h4>
                 <button @click="sendTestEmail(selectedUser.email)" class="w-full bg-blue-600 hover:bg-blue-500 text-white px-4 py-2 rounded font-bold flex items-center justify-center gap-2">
                    <span>📧</span> {{ langStore.t('admin.sendTestEmail') }}
                 </button>
                 <p class="text-xs text-gray-400 mt-1 text-center">Sends a verification email to {{ selectedUser.email }}</p>
              </div>

              <div class="border-t border-gray-600 pt-4 mt-4">
                 <button @click="deleteUser(selectedUser.id)" class="w-full bg-red-700 hover:bg-red-600 text-white px-4 py-3 rounded font-bold flex items-center justify-center gap-2">
                    <span>⚠️</span> {{ langStore.t('admin.deleteUser') }}
                 </button>
                 <p class="text-xs text-gray-400 mt-2 text-center">This action cannot be undone. Admin accounts cannot be deleted.</p>
              </div>
           </div>
        </div>
      </div>
    </div>

    <!-- Categories Tab -->
    <div v-if="activeTab === 'categories'" class="space-y-6">
      
      <!-- Top Actions -->
      <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3">
        <h3 class="text-xl md:text-2xl font-bold text-white">{{ langStore.t('admin.categories') }}</h3>
        <div class="flex gap-2 w-full sm:w-auto">
           <button @click="initDefaultCategories" class="bg-blue-600 hover:bg-blue-500 text-white px-4 py-2 rounded font-bold shadow flex items-center gap-2">
             <span>⚡</span> {{ langStore.t('admin.initDefaults') }}
           </button>
           <button @click="showCreateCategory = true" class="bg-yellow-500 hover:bg-yellow-600 text-white px-4 py-2 rounded font-bold shadow flex items-center gap-2">
             <span>+</span> {{ langStore.t('admin.newCategory') }}
           </button>
        </div>
      </div>

      <!-- Create Category Modal -->
      <div v-if="showCreateCategory" class="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50 p-4">
        <div class="bg-gray-800 p-6 rounded-lg shadow-xl border border-gray-700 w-full max-w-md">
          <h3 class="text-xl font-bold mb-4 text-yellow-400">{{ langStore.t('admin.newCategory') }}</h3>
          <div class="space-y-4">
            <div>
              <label class="block text-sm text-gray-400 mb-1">{{ langStore.t('admin.categoryName') }}</label>
              <input v-model="newCategoryName" placeholder="e.g. Match Winner" class="w-full bg-gray-700 text-white p-2 rounded border border-gray-600 focus:border-yellow-500 outline-none" />
            </div>
            <div class="flex justify-end gap-3 mt-6">
              <button @click="showCreateCategory = false" class="px-4 py-2 text-gray-300 hover:text-white">{{ langStore.t('admin.cancel') }}</button>
              <button @click="createCategory" class="bg-yellow-500 hover:bg-yellow-600 px-6 py-2 rounded text-white font-bold">{{ langStore.t('admin.create') }}</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Categories List -->
      <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3 md:gap-4">
        <div v-for="category in categories" :key="category.id" 
             class="bg-gray-800 p-4 rounded-lg border border-gray-700 hover:border-yellow-500 transition shadow-lg flex justify-between items-center group">
          
          <span class="text-lg font-bold text-white">{{ category.name }}</span>
          
          <button @click="deleteCategory(category.id)" class="text-gray-500 hover:text-red-500 opacity-0 group-hover:opacity-100 transition">
             <span class="text-xl">×</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Players Tab -->
    <div v-if="activeTab === 'players'" class="space-y-6">
      <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <h3 class="text-2xl font-bold text-white">{{ langStore.t('admin.playersTitle') }}</h3>
        <button @click="showCreatePlayer = true" class="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded font-bold shadow flex items-center gap-2">
          <span>+</span> {{ langStore.t('admin.addPlayer') }}
        </button>
      </div>

      <!-- Create/Edit Player Modal -->
      <div v-if="showCreatePlayer || editingPlayer" class="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50 p-4">
        <div class="bg-gray-800 p-6 rounded-lg shadow-xl border border-gray-700 w-full max-w-lg md:max-w-2xl max-h-[90vh] overflow-y-auto">
          <h3 class="text-xl font-bold mb-4 text-white">{{ editingPlayer ? langStore.t('admin.editPlayer') : langStore.t('admin.newPlayer') }}</h3>

          
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
            <div>
              <label class="block text-sm text-gray-400 mb-1">{{ langStore.t('admin.playerName') }}</label>
              <input v-model="playerForm.name" class="w-full bg-gray-700 text-white p-2 rounded border border-gray-600 focus:border-green-500 outline-none" />
            </div>
            <div>
              <label class="block text-sm text-gray-400 mb-1">{{ langStore.t('admin.playerTeam') }}</label>
              <input v-model="playerForm.team" class="w-full bg-gray-700 text-white p-2 rounded border border-gray-600 focus:border-green-500 outline-none" />
            </div>
            <div>
              <label class="block text-sm text-gray-400 mb-1">{{ langStore.t('admin.goals') }}</label>
              <input v-model="playerForm.goals" type="number" class="w-full bg-gray-700 text-white p-2 rounded border border-gray-600 focus:border-green-500 outline-none" />
            </div>
            <div>
              <label class="block text-sm text-gray-400 mb-1">{{ langStore.t('admin.assists') }}</label>
              <input v-model="playerForm.assists" type="number" class="w-full bg-gray-700 text-white p-2 rounded border border-gray-600 focus:border-green-500 outline-none" />
            </div>
            <div>
              <label class="block text-sm text-gray-400 mb-1">{{ langStore.t('admin.matchesPlayed') }}</label>
              <input v-model="playerForm.matchesPlayed" type="number" class="w-full bg-gray-700 text-white p-2 rounded border border-gray-600 focus:border-green-500 outline-none" />
            </div>
            <div>
              <label class="block text-sm text-gray-400 mb-1">{{ langStore.t('admin.matchesStarted') }}</label>
              <input v-model="playerForm.matchesStarted" type="number" class="w-full bg-gray-700 text-white p-2 rounded border border-gray-600 focus:border-green-500 outline-none" />
            </div>
            <div>
              <label class="block text-sm text-gray-400 mb-1">{{ langStore.t('admin.yellowCards') }}</label>
              <input v-model="playerForm.yellowCards" type="number" class="w-full bg-gray-700 text-white p-2 rounded border border-gray-600 focus:border-green-500 outline-none" />
            </div>
            <div>
              <label class="block text-sm text-gray-400 mb-1">{{ langStore.t('admin.redCards') }}</label>
              <input v-model="playerForm.redCards" type="number" class="w-full bg-gray-700 text-white p-2 rounded border border-gray-600 focus:border-green-500 outline-none" />
            </div>
          </div>

          <div class="flex justify-end gap-3 mt-6">
            <button @click="showCreatePlayer = false; editingPlayer = false; resetPlayerForm()" class="px-4 py-2 text-gray-300 hover:text-white">{{ langStore.t('admin.cancel') }}</button>
            <button @click="editingPlayer ? updatePlayerStats() : createPlayer()" class="bg-green-500 hover:bg-green-600 px-6 py-2 rounded text-white font-bold">
              {{ editingPlayer ? langStore.t('admin.saveChanges') : langStore.t('admin.create') }}
            </button>
          </div>
        </div>
      </div>

      <!-- Players List (Desktop Table / Mobile Cards) -->
      <div class="bg-gray-800 rounded-lg shadow-xl overflow-hidden border border-gray-700">
        
        <!-- Mobile Card View -->
        <div class="block md:hidden">
          <div v-for="player in players" :key="player.id" class="p-4 border-b border-gray-700 last:border-b-0 hover:bg-gray-750">
            <div class="flex justify-between items-start mb-2">
              <div>
                <span class="text-white font-bold text-lg block">{{ player.name }}</span>
                <span class="text-gray-400 text-sm">{{ player.team }}</span>
              </div>
              <div class="flex gap-2">
                <button @click="startEditPlayer(player)" class="text-blue-400 hover:text-blue-300 text-sm font-bold border border-blue-400 px-2 py-1 rounded">{{ langStore.t('admin.edit') }}</button>
                <button @click="deletePlayer(player.id)" class="text-red-400 hover:text-red-300 text-sm font-bold border border-red-400 px-2 py-1 rounded">{{ langStore.t('admin.delete') }}</button>
              </div>
            </div>
            <div class="grid grid-cols-2 gap-2 text-sm">
              <div class="flex justify-between bg-gray-900 p-2 rounded">
                 <span class="text-gray-400">{{ langStore.t('admin.goals') }}</span>
                 <span class="font-bold text-green-400">{{ player.goals }}</span>
              </div>
              <div class="flex justify-between bg-gray-900 p-2 rounded">
                 <span class="text-gray-400">{{ langStore.t('admin.assists') }}</span>
                 <span class="font-bold text-blue-400">{{ player.assists }}</span>
              </div>
              <div class="flex justify-between bg-gray-900 p-2 rounded">
                 <span class="text-gray-400">{{ langStore.t('admin.matchesPlayed') }}</span>
                 <span class="text-white">{{ player.matchesPlayed }} ({{ player.matchesStarted }})</span>
              </div>
              <div class="flex justify-between bg-gray-900 p-2 rounded">
                 <span class="text-gray-400">Cards (Y/R)</span>
                 <span><span class="text-yellow-400 font-bold">{{ player.yellowCards }}</span> / <span class="text-red-500 font-bold">{{ player.redCards }}</span></span>
              </div>
            </div>
          </div>
          <div v-if="players.length === 0" class="p-8 text-center text-gray-500">
             {{ langStore.t('admin.noPlayers') }}
          </div>
        </div>

        <!-- Desktop Table View -->
        <table class="w-full text-left hidden md:table">
          <thead class="bg-gray-700 text-gray-400 uppercase text-xs">
            <tr>
              <th class="p-3">{{ langStore.t('admin.playerName') }}</th>
              <th class="p-3">{{ langStore.t('admin.playerTeam') }}</th>
              <th class="p-3 text-center">{{ langStore.t('admin.goals') }}</th>
              <th class="p-3 text-center">{{ langStore.t('admin.assists') }}</th>
              <th class="p-3 text-center">{{ langStore.t('admin.matchesPlayed') }} ({{langStore.t('admin.matchesStarted')}})</th>
              <th class="p-3 text-center">{{ langStore.t('admin.yellowCards') }} / {{ langStore.t('admin.redCards') }}</th>
              <th class="p-3 text-right">{{ langStore.t('admin.actions') }}</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-700">
            <tr v-for="player in players" :key="player.id" class="hover:bg-gray-750">
              <td class="p-3 font-bold text-white">{{ player.name }}</td>
              <td class="p-3 text-gray-400">{{ player.team }}</td>
              <td class="p-3 text-center text-green-400 font-bold">{{ player.goals }}</td>
              <td class="p-3 text-center text-blue-400 font-bold">{{ player.assists }}</td>
              <td class="p-3 text-center text-gray-300">{{ player.matchesPlayed }} ({{ player.matchesStarted }})</td>
              <td class="p-3 text-center">
                <span class="text-yellow-400">{{ player.yellowCards }}</span> / <span class="text-red-500">{{ player.redCards }}</span>
              </td>
              <td class="p-3 text-right">
                <button @click="startEditPlayer(player)" class="text-blue-400 hover:text-blue-300 mr-3 font-bold">{{ langStore.t('admin.edit') }}</button>
                <button @click="deletePlayer(player.id)" class="text-red-400 hover:text-red-300 font-bold">{{ langStore.t('admin.delete') }}</button>
              </td>
            </tr>
            <tr v-if="players.length === 0">
              <td colspan="7" class="p-8 text-center text-gray-500">{{ langStore.t('admin.noPlayers') }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Settings Tab -->
    <div v-if="activeTab === 'settings'" class="space-y-6">
      <h3 class="text-2xl font-bold text-white mb-6">⚙️ Configuración de la Aplicación</h3>

      <!-- News Modal Settings Card -->
      <div class="bg-gray-800 rounded-lg p-6 border border-gray-700">
        <div class="flex items-center gap-3 mb-4">
          <span class="text-3xl">📢</span>
          <div>
            <h4 class="text-xl font-bold text-white">Modal de Novedades</h4>
            <p class="text-sm text-gray-400">Controla si los usuarios ven el modal de novedades al iniciar sesión</p>
          </div>
        </div>

        <div class="bg-gray-900 p-4 rounded-lg mb-4">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-white font-medium">Estado Actual:</p>
              <p class="text-sm text-gray-400 mt-1">
                {{ newsModalEnabled ? '✅ Activado - Los usuarios verán el modal (hasta 5 veces)' : '❌ Desactivado - El modal no se mostrará a ningún usuario' }}
              </p>
            </div>
            <div v-if="loadingNewsStatus" class="text-gray-400">
              Cargando...
            </div>
            <div v-else class="flex items-center gap-2">
              <span :class="newsModalEnabled ? 'text-green-400' : 'text-red-400'" class="text-2xl">
                {{ newsModalEnabled ? '●' : '○' }}
              </span>
            </div>
          </div>
        </div>

        <div class="flex gap-3">
          <button 
            @click="toggleNewsModal"
            :disabled="loadingNewsStatus"
            :class="[
              'flex-1 px-4 py-3 rounded-lg font-bold transition',
              newsModalEnabled 
                ? 'bg-red-600 hover:bg-red-700 text-white' 
                : 'bg-green-600 hover:bg-green-700 text-white',
              loadingNewsStatus ? 'opacity-50 cursor-not-allowed' : ''
            ]"
          >
            {{ newsModalEnabled ? '🚫 Desactivar Modal' : '✅ Activar Modal' }}
          </button>
        </div>

        <div class="mt-4 p-3 bg-blue-900/20 border border-blue-700 rounded-lg">
          <p class="text-xs text-blue-300">
            <strong>ℹ️ Nota:</strong> Cuando el modal está desactivado, ningún usuario lo verá al iniciar sesión, 
            independientemente de cuántas veces lo hayan visto antes. Esto es útil cuando las novedades ya no son relevantes.
          </p>
        </div>
      </div>

      <!-- Send News Email Card -->
      <div class="bg-gray-800 rounded-lg p-6 border border-gray-700">
        <div class="flex items-center gap-3 mb-4">
          <span class="text-3xl">📧</span>
          <div>
            <h4 class="text-xl font-bold text-white">Enviar Email de Novedades</h4>
            <p class="text-sm text-gray-400">Envía un correo con las últimas novedades a todos los usuarios</p>
          </div>
        </div>

        <div class="bg-gray-900 p-4 rounded-lg mb-4">
          <p class="text-white font-medium mb-2">📝 Contenido del Email:</p>
          <ul class="text-sm text-gray-400 space-y-1 ml-4">
            <li>• 📱 Instalación de la App (PWA)</li>
            <li>• 📱 Mejoras en la versión móvil</li>
            <li>• ❓ Nueva página de ayuda</li>
            <li>• ⚙️ Gestión de perfil</li>
            <li>• 👀 Perfiles públicos de usuarios</li>
          </ul>
        </div>

        <button 
          @click="sendNewsEmail"
          class="w-full bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-500 hover:to-purple-500 text-white px-6 py-3 rounded-lg font-bold transition shadow-lg"
        >
          📧 Enviar Email de Novedades a Todos
        </button>

        <div class="mt-4 p-3 bg-yellow-900/20 border border-yellow-700 rounded-lg">
          <p class="text-xs text-yellow-300">
            <strong>⚠️ Advertencia:</strong> Este email se enviará a TODOS los usuarios registrados (excepto admins). 
            Úsalo solo cuando haya novedades importantes que comunicar. El contenido del email está predefinido con las últimas mejoras de FurbitoBET.
          </p>
        </div>
      </div>

      <!-- Newsletter Card (Custom) -->
      <div class="bg-gray-800 rounded-lg p-6 border border-gray-700">
        <div class="flex items-center gap-3 mb-4">
          <span class="text-3xl">✉️</span>
          <div>
            <h4 class="text-xl font-bold text-white">Newsletter Personalizado</h4>
            <p class="text-sm text-gray-400">Envía un email personalizado a todos los usuarios</p>
          </div>
        </div>

        <div class="bg-gray-900 p-4 rounded-lg mb-4">
          <p class="text-white font-medium mb-2">✏️ Características:</p>
          <ul class="text-sm text-gray-400 space-y-1 ml-4">
            <li>• Personaliza el asunto del email</li>
            <li>• Escribe tu propio mensaje</li>
            <li>• Vista previa antes de enviar</li>
            <li>• Envío a todos los usuarios</li>
          </ul>
        </div>

        <button 
          @click="sendNewsletter"
          class="w-full bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-500 hover:to-pink-500 text-white px-6 py-3 rounded-lg font-bold transition shadow-lg"
        >
          ✉️ Crear Newsletter Personalizado
        </button>
      </div>
    </div>

    <!-- Newsletter Modal -->
    <div v-if="showNewsletterModal" class="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50 p-4">
      <div class="bg-gray-800 p-6 rounded-lg shadow-xl border border-purple-500 w-full max-w-3xl max-h-[90vh] overflow-y-auto">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-2xl font-bold text-purple-400">📧 Personalizar Newsletter</h3>
          <button @click="showNewsletterModal = false" class="text-gray-400 hover:text-white text-2xl">&times;</button>
        </div>

        <div class="space-y-4">
          <!-- Subject -->
          <div>
            <label class="block text-sm text-gray-400 mb-2">Asunto del Email</label>
            <input 
              v-model="newsletterSubject" 
              type="text"
              placeholder="Ej: 🎉 ¡Novedades en FurbitoBET!"
              class="w-full bg-gray-700 text-white p-3 rounded border border-gray-600 focus:border-purple-500 outline-none"
            />
          </div>

          <!-- Message -->
          <div>
            <label class="block text-sm text-gray-400 mb-2">Mensaje del Email</label>
            <textarea 
              v-model="newsletterMessage" 
              rows="15"
              placeholder="Escribe el mensaje que se enviará a todos los usuarios..."
              class="w-full bg-gray-700 text-white p-3 rounded border border-gray-600 focus:border-purple-500 outline-none font-mono text-sm"
            ></textarea>
            <p class="text-xs text-gray-500 mt-1">💡 Tip: Usa emojis y saltos de línea para hacer el mensaje más atractivo</p>
          </div>

          <!-- Preview -->
          <div class="bg-gray-900 p-4 rounded border border-gray-700">
            <h4 class="text-sm font-bold text-gray-400 mb-2">Vista Previa:</h4>
            <div class="bg-white text-black p-4 rounded text-sm whitespace-pre-wrap">
              <div class="font-bold mb-2">{{ newsletterSubject }}</div>
              <div>{{ newsletterMessage }}</div>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex justify-end gap-3 pt-4 border-t border-gray-700">
            <button 
              @click="showNewsletterModal = false" 
              class="px-6 py-2 text-gray-300 hover:text-white border border-gray-600 rounded"
            >
              Cancelar
            </button>
            <button 
              @click="confirmSendNewsletter" 
              class="bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-500 hover:to-pink-500 text-white px-6 py-2 rounded font-bold"
            >
              Enviar a Todos los Usuarios
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
