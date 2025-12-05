<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()

const activeTab = ref('events')
const events = ref([])
const eventName = ref('')
const eventDate = ref('')
const selectedEventId = ref(null)
const outcomeDesc = ref('')
const outcomeOdds = ref('')

const users = ref([])
const selectedUserId = ref('')
const balanceAmount = ref('')
const newPassword = ref('')

const categories = ref([])
const newCategoryName = ref('')

const editingEvent = ref(null)
const editEventName = ref('')
const editEventDate = ref('')

const editingOutcome = ref(null)
const editOutcomeDesc = ref('')
const editOutcomeOdds = ref('')
const editOutcomeGroup = ref('')

async function fetchUsers() {
  const res = await fetch(`${import.meta.env.VITE_API_URL}/api/users`, {
    headers: { 'Authorization': `Bearer ${auth.token}` }
  })
  users.value = await res.json()
}

async function fetchEvents() {
  const res = await fetch(`${import.meta.env.VITE_API_URL}/api/events`)
  events.value = await res.json()
}

async function fetchCategories() {
  const res = await fetch(`${import.meta.env.VITE_API_URL}/api/categories`)
  categories.value = await res.json()
}

async function createEvent() {
  await fetch(`${import.meta.env.VITE_API_URL}/api/admin/events`, {
    method: 'POST',
    headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${auth.token}`
    },
    body: JSON.stringify({ name: eventName.value, date: eventDate.value })
  })
  eventName.value = ''
  eventDate.value = ''
  fetchEvents()
}

function startEditEvent(event) {
  editingEvent.value = event
  editEventName.value = event.name
  editEventDate.value = event.date
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
  if (!confirm('Clone this event?')) return
  await fetch(`${import.meta.env.VITE_API_URL}/api/admin/events/${id}/clone`, { 
      method: 'POST',
      headers: { 'Authorization': `Bearer ${auth.token}` }
  })
  fetchEvents()
  fetchEvents()
}

async function deleteEvent(id) {
  if (!confirm('Delete this event? This action cannot be undone.')) return
  await fetch(`${import.meta.env.VITE_API_URL}/api/events/${id}`, { 
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${auth.token}` }
  })
  fetchEvents()
}

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
  fetchCategories()
}

async function deleteCategory(id) {
  if (!confirm('Delete category?')) return
  await fetch(`${import.meta.env.VITE_API_URL}/api/categories/${id}`, { 
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${auth.token}` }
  })
  fetchCategories()
}

const outcomeGroup = ref('')

async function addOutcome() {
  if (!selectedEventId.value) return
  await fetch(`${import.meta.env.VITE_API_URL}/api/admin/events/${selectedEventId.value}/outcomes`, {
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
  outcomeGroup.value = ''
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
  await fetch(`${import.meta.env.VITE_API_URL}/api/users/${userId}/balance`, {
    method: 'PUT',
    headers: { 
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${auth.token}`
    },
    body: balanceAmount.value
  })
  balanceAmount.value = ''
  fetchUsers()
  alert('Balance updated!')
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

async function deleteUser(id) {
  if (!confirm('Delete this user? This action cannot be undone.')) return
  await fetch(`${import.meta.env.VITE_API_URL}/api/users/${id}`, { 
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${auth.token}` }
  })
  fetchUsers()
}

async function deleteOutcome(id) {
  if (!confirm('Delete outcome?')) return
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
  if (!confirm(`Resolve event ${resolvingEvent.value.name} with score ${resolveHomeGoals.value}-${resolveAwayGoals.value}?`)) return
  
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
  alert('Event resolved and bets settled!')
}

onMounted(() => {
  fetchEvents()
  fetchUsers()
  fetchCategories()
})
</script>

<template>
  <div class="space-y-8">
    <h2 class="text-3xl font-bold text-white">Admin Dashboard</h2>

    <!-- Tabs -->
    <div class="flex border-b border-gray-700">
      <button @click="activeTab = 'events'"
        :class="['px-4 py-2 font-medium', activeTab === 'events' ? 'text-green-400 border-b-2 border-green-400' : 'text-gray-400 hover:text-white']">
        Events
      </button>
      <button @click="activeTab = 'users'"
        :class="['px-4 py-2 font-medium', activeTab === 'users' ? 'text-blue-400 border-b-2 border-blue-400' : 'text-gray-400 hover:text-white']">
        Users
      </button>
      <button @click="activeTab = 'categories'"
        :class="['px-4 py-2 font-medium', activeTab === 'categories' ? 'text-yellow-400 border-b-2 border-yellow-400' : 'text-gray-400 hover:text-white']">
        Categories
      </button>
    </div>

    <!-- Events Tab -->
    <div v-if="activeTab === 'events'" class="space-y-8">
      <!-- Create Event -->
      <div class="bg-gray-800 p-6 rounded-lg shadow-lg border border-gray-700">
        <h3 class="text-xl font-semibold mb-4 text-green-400">Create Event</h3>
        <div class="flex gap-4">
          <input v-model="eventName" placeholder="Event Name (e.g. Madrid vs Barça)"
            class="flex-1 bg-gray-700 text-white p-2 rounded" />
          <input v-model="eventDate" type="datetime-local" class="bg-gray-700 text-white p-2 rounded" />
          <button @click="createEvent"
            class="bg-green-500 hover:bg-green-600 px-4 py-2 rounded text-white font-bold">Create</button>
        </div>
      </div>

      <!-- Edit Event Modal/Inline -->
      <div v-if="editingEvent" class="bg-gray-800 p-6 rounded-lg shadow-lg border border-yellow-500">
        <h3 class="text-xl font-semibold mb-4 text-yellow-400">Edit Event</h3>
        <div class="flex gap-4">
          <input v-model="editEventName" class="flex-1 bg-gray-700 text-white p-2 rounded" />
          <input v-model="editEventDate" type="datetime-local" class="bg-gray-700 text-white p-2 rounded" />
          <button @click="updateEvent" class="bg-yellow-500 hover:bg-yellow-600 px-4 py-2 rounded text-white font-bold">Save</button>
          <button @click="editingEvent = null" class="bg-gray-600 hover:bg-gray-700 px-4 py-2 rounded text-white">Cancel</button>
        </div>
      </div>

      <!-- Resolve Event Modal -->
      <div v-if="resolvingEvent" class="bg-gray-800 p-6 rounded-lg shadow-lg border border-blue-500">
        <h3 class="text-xl font-semibold mb-4 text-blue-400">Simulate Result: {{ resolvingEvent.name }}</h3>
        <div class="flex gap-4 items-center">
          <div class="flex flex-col">
             <label class="text-gray-400 text-sm">Home Goals</label>
             <input v-model="resolveHomeGoals" type="number" class="bg-gray-700 text-white p-2 rounded w-24" />
          </div>
          <div class="flex flex-col">
             <label class="text-gray-400 text-sm">Away Goals</label>
             <input v-model="resolveAwayGoals" type="number" class="bg-gray-700 text-white p-2 rounded w-24" />
          </div>
          <button @click="resolveEvent" class="bg-blue-500 hover:bg-blue-600 px-4 py-2 rounded text-white font-bold mt-5">Resolve & Settle</button>
          <button @click="resolvingEvent = null" class="bg-gray-600 hover:bg-gray-700 px-4 py-2 rounded text-white mt-5">Cancel</button>
        </div>
      </div>

      <!-- Event List for Editing -->
      <div class="bg-gray-800 p-6 rounded-lg shadow-lg border border-gray-700">
        <h3 class="text-xl font-semibold mb-4 text-white">Existing Events</h3>
        <div class="space-y-2">
            <div v-for="event in events" :key="event.id" class="flex justify-between items-center bg-gray-700 p-3 rounded">
                <div>
                    <span class="font-bold text-white">{{ event.name }}</span>
                    <span class="text-gray-400 text-sm ml-2">{{ new Date(event.date).toLocaleString() }}</span>
                    <span class="ml-2 text-xs uppercase font-bold" :class="{
                        'text-yellow-400': event.status === 'UPCOMING',
                        'text-green-500': event.status === 'COMPLETED',
                        'text-red-500': event.status === 'CANCELLED'
                    }">{{ event.status }}</span>
                </div>
                <div class="flex gap-2">
                  <button v-if="event.status === 'UPCOMING'" @click="startResolveEvent(event)" class="bg-blue-600 hover:bg-blue-700 text-white text-xs px-2 py-1 rounded">Simulate Result</button>
                  <button @click="startEditEvent(event)" class="bg-blue-500 hover:bg-blue-600 text-white text-xs px-2 py-1 rounded">Edit</button>
                  <button @click="cloneEvent(event.id)" class="bg-purple-500 hover:bg-purple-600 text-white text-xs px-2 py-1 rounded">Clone</button>
                  <button @click="deleteEvent(event.id)" class="bg-red-500 hover:bg-red-600 text-white text-xs px-2 py-1 rounded">Delete</button>
                </div>
            </div>
        </div>
      </div>

      <!-- Add Outcome -->
      <div class="bg-gray-800 p-6 rounded-lg shadow-lg border border-gray-700">
        <h3 class="text-xl font-semibold mb-4 text-blue-400">Add Outcome to Event</h3>
        <div class="flex gap-4 items-end">
          <div class="flex-1">
            <label class="block text-sm text-gray-400 mb-1">Select Event</label>
            <select v-model="selectedEventId" class="w-full bg-gray-700 text-white p-2 rounded">
              <option v-for="event in events" :key="event.id" :value="event.id">
                {{ event.name }} ({{ event.status }})
              </option>
            </select>
          </div>
          <div class="flex-1">
            <label class="block text-sm text-gray-400 mb-1">Outcome Description</label>
            <input v-model="outcomeDesc" placeholder="e.g. Madrid Wins"
              class="w-full bg-gray-700 text-white p-2 rounded" />
          </div>
          <div class="w-32">
            <label class="block text-sm text-gray-400 mb-1">Category</label>
            <select v-model="outcomeGroup" class="w-full bg-gray-700 text-white p-2 rounded">
              <option value="" disabled>Select Category</option>
              <option v-for="category in categories" :key="category.id" :value="category.name">
                {{ category.name }}
              </option>
            </select>
          </div>
          <div class="w-24">
            <label class="block text-sm text-gray-400 mb-1">Odds</label>
            <input v-model="outcomeOdds" type="number" step="0.01" placeholder="1.5"
              class="w-full bg-gray-700 text-white p-2 rounded" />
          </div>
          <button @click="addOutcome"
            class="bg-blue-500 hover:bg-blue-600 px-4 py-2 rounded text-white font-bold">Add</button>
        </div>
      </div>

      <!-- Outcomes List (Moved here) -->
      <div v-if="selectedEventId" class="mt-6">
        <h3 class="text-xl font-bold text-white mb-4">Manage Outcomes</h3>
        
        <!-- Edit Outcome Form -->
        <div v-if="editingOutcome" class="bg-gray-800 p-4 rounded border border-yellow-500 mb-4">
           <h4 class="text-yellow-400 font-bold mb-2">Edit Outcome</h4>
           <div class="flex gap-4 items-end">
              <div class="flex-1">
                 <label class="block text-xs text-gray-400">Description</label>
                 <input v-model="editOutcomeDesc" class="w-full bg-gray-700 text-white p-1 rounded" />
              </div>
              <div class="w-32">
                 <label class="block text-xs text-gray-400">Category</label>
                 <select v-model="editOutcomeGroup" class="w-full bg-gray-700 text-white p-1 rounded">
                    <option value="">None</option>
                    <option v-for="category in categories" :key="category.id" :value="category.name">
                      {{ category.name }}
                    </option>
                 </select>
              </div>
              <div class="w-20">
                 <label class="block text-xs text-gray-400">Odds</label>
                 <input v-model="editOutcomeOdds" type="number" step="0.01" class="w-full bg-gray-700 text-white p-1 rounded" />
              </div>
              <button @click="updateOutcome" class="bg-yellow-500 hover:bg-yellow-600 text-white px-3 py-1 rounded">Save</button>
              <button @click="editingOutcome = null" class="bg-gray-600 hover:bg-gray-700 text-white px-3 py-1 rounded">Cancel</button>
           </div>
        </div>

        <div class="space-y-2">
          <div v-for="outcome in events.find(e => e.id === selectedEventId)?.outcomes || []" :key="outcome.id" 
               class="bg-gray-700 p-3 rounded flex justify-between items-center">
            <div>
              <span class="text-white font-bold">{{ outcome.description }}</span>
              <span class="text-gray-400 text-sm ml-2">@ {{ outcome.odds }}</span>
              <span v-if="outcome.outcomeGroup" class="text-xs bg-gray-600 px-2 py-1 rounded ml-2">{{ outcome.outcomeGroup }}</span>
              <span class="ml-2 text-xs uppercase font-bold" :class="{
                'text-yellow-400': outcome.status === 'PENDING',
                'text-green-500': outcome.status === 'WON',
                'text-red-500': outcome.status === 'LOST'
              }">{{ outcome.status }}</span>
            </div>
            <div class="flex gap-2">
              <button @click="startEditOutcome(outcome)" class="bg-blue-500 hover:bg-blue-600 text-white text-xs px-2 py-1 rounded">Edit</button>
              <button v-if="outcome.status === 'PENDING'" @click="settleOutcome(outcome.id, 'WON')" class="bg-green-500 hover:bg-green-600 text-white text-xs px-2 py-1 rounded">Win</button>
              <button v-if="outcome.status === 'PENDING'" @click="settleOutcome(outcome.id, 'LOST')" class="bg-red-500 hover:bg-red-600 text-white text-xs px-2 py-1 rounded">Lose</button>
              <button @click="deleteOutcome(outcome.id)" class="bg-gray-600 hover:bg-red-700 text-white text-xs px-2 py-1 rounded">Delete</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Users Tab -->
    <div v-if="activeTab === 'users'" class="space-y-8">
      <div class="bg-gray-800 p-6 rounded-lg shadow-lg border border-gray-700">
        <h3 class="text-xl font-semibold mb-4 text-purple-400">User Management</h3>
        <div class="overflow-x-auto">
          <table class="w-full text-left text-gray-300">
            <thead class="text-gray-400 uppercase bg-gray-700">
              <tr>
                <th class="px-4 py-2">ID</th>
                <th class="px-4 py-2">Username</th>
                <th class="px-4 py-2">Email</th>
                <th class="px-4 py-2">Balance</th>
                <th class="px-4 py-2">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in users" :key="user.id" class="border-b border-gray-700 hover:bg-gray-700">
                <td class="px-4 py-2">{{ user.id }}</td>
                <td class="px-4 py-2">{{ user.username }}</td>
                <td class="px-4 py-2">{{ user.email }}</td>
                <td class="px-4 py-2">{{ user.balance }} €</td>
                <td class="px-4 py-2 flex gap-2">
                  <div class="flex gap-2 items-center">
                    <input v-model="balanceAmount" type="number" placeholder="New Balance"
                      class="w-24 bg-gray-600 text-white p-1 rounded text-sm" />
                    <button @click="updateBalance(user.id)"
                      class="bg-purple-500 hover:bg-purple-600 px-2 py-1 rounded text-white text-xs">Set Balance</button>
                  </div>
                  <div class="flex gap-2 items-center">
                    <input v-model="newPassword" type="text" placeholder="New Pass"
                      class="w-24 bg-gray-600 text-white p-1 rounded text-sm" />
                    <button @click="resetPassword(user.id)"
                      class="bg-red-500 hover:bg-red-600 px-2 py-1 rounded text-white text-xs">Reset Pass</button>
                  </div>
                  <div class="flex gap-2 items-center">
                    <button @click="deleteUser(user.id)"
                      class="bg-red-700 hover:bg-red-800 px-2 py-1 rounded text-white text-xs">Delete</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Categories Tab -->
    <div v-if="activeTab === 'categories'" class="space-y-8">
      <div class="bg-gray-800 p-6 rounded-lg shadow-lg border border-gray-700">
        <h3 class="text-xl font-semibold mb-4 text-yellow-400">Manage Categories</h3>
        <div class="flex gap-4 mb-6">
          <input v-model="newCategoryName" placeholder="Category Name (e.g. Match Winner)"
            class="flex-1 bg-gray-700 text-white p-2 rounded" />
          <button @click="createCategory"
            class="bg-yellow-500 hover:bg-yellow-600 px-4 py-2 rounded text-white font-bold">Create</button>
        </div>
        
        <div class="grid gap-2">
          <div v-for="category in categories" :key="category.id" class="bg-gray-700 p-3 rounded text-white flex justify-between items-center">
            <span>{{ category.name }}</span>
            <button @click="deleteCategory(category.id)" class="text-red-400 hover:text-red-300 font-bold">Delete</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
