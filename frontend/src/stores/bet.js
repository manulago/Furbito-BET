import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useAuthStore } from './auth'

export const useBetStore = defineStore('bet', () => {
    const selectedOutcomes = ref([])
    const betAmount = ref(10)
    const auth = useAuthStore()

    function toggleOutcome(outcome) {
        const index = selectedOutcomes.value.findIndex(o => o.id === outcome.id)
        if (index === -1) {
            // Check if there is already an outcome from the same event
            const sameEventOutcomes = selectedOutcomes.value.filter(o => o.event.id === outcome.event.id)

            if (sameEventOutcomes.length > 0) {
                const mainMarkets = ['Ganador del Partido', 'Doble Oportunidad', 'Apuesta sin Empate']

                const isMain = (g) => mainMarkets.includes(g)

                // Extract base category (remove "- Más de" or "- Menos de")
                const extractBaseCategory = (group) => {
                    if (!group) return ''
                    return group
                        .replace(/ - Más de$/, '')
                        .replace(/ - Menos de$/, '')
                        .trim()
                }

                // Extract direction (MAS, MENOS, or empty)
                const extractDirection = (group) => {
                    if (!group) return ''
                    if (group.includes(' - Más de')) return 'MAS'
                    if (group.includes(' - Menos de')) return 'MENOS'
                    return ''
                }

                const newGroup = outcome.outcomeGroup
                const newIsMain = isMain(newGroup)
                const newBaseCategory = extractBaseCategory(newGroup)
                const newDirection = extractDirection(newGroup)

                // Find and remove conflicting outcomes
                const toRemove = []

                for (const existing of sameEventOutcomes) {
                    const existingGroup = existing.outcomeGroup
                    const existingIsMain = isMain(existingGroup)
                    const existingBaseCategory = extractBaseCategory(existingGroup)
                    const existingDirection = extractDirection(existingGroup)

                    let isConflict = false

                    // Conflict: main markets (1X2, DC, DNB)
                    if (newIsMain && existingIsMain) {
                        isConflict = true
                    }

                    // Check for overlapping goal bets
                    if (newBaseCategory === existingBaseCategory && newBaseCategory !== '') {
                        // Allow Goleadores and Asistencias
                        if (newBaseCategory === 'Goleadores' || newBaseCategory === 'Asistencias') {
                            continue
                        }

                        // If same direction (both MAS or both MENOS), they overlap
                        if (newDirection === existingDirection && newDirection !== '') {
                            isConflict = true
                        }

                        // If both have no direction (like Ganador, Ambos Marcan), they're exclusive
                        if (newDirection === '' && existingDirection === '') {
                            isConflict = true
                        }
                    }

                    if (isConflict) {
                        toRemove.push(existing.id)
                    }
                }

                // Remove all conflicting outcomes
                toRemove.forEach(id => {
                    const idx = selectedOutcomes.value.findIndex(o => o.id === id)
                    if (idx !== -1) {
                        selectedOutcomes.value.splice(idx, 1)
                    }
                })

                selectedOutcomes.value.push(outcome)
            } else {
                selectedOutcomes.value.push(outcome)
            }
        } else {
            selectedOutcomes.value.splice(index, 1)
        }
    }

    function removeOutcome(outcomeId) {
        const index = selectedOutcomes.value.findIndex(o => o.id === outcomeId)
        if (index !== -1) {
            selectedOutcomes.value.splice(index, 1)
        }
    }

    function clearSlip() {
        selectedOutcomes.value = []
    }

    const totalOdds = computed(() => {
        return selectedOutcomes.value.reduce((acc, curr) => acc * curr.odds, 1).toFixed(2)
    })

    const potentialReturn = computed(() => {
        return (betAmount.value * totalOdds.value).toFixed(2)
    })

    async function placeBet() {
        if (!auth.user) {
            alert('Please login first')
            return
        }

        if (selectedOutcomes.value.length === 0) return

        // Client-side check for started events
        const now = new Date()
        const hasStarted = selectedOutcomes.value.some(o => {
            if (o.event && o.event.date) {
                return new Date(o.event.date) < now
            }
            return false
        })

        if (hasStarted) {
            alert('Cannot place bet on events that have already started.')
            return
        }

        try {
            const res = await fetch(`${import.meta.env.VITE_API_URL}/api/bets`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.token}`
                },
                body: JSON.stringify({
                    userId: auth.user.id,
                    outcomeIds: selectedOutcomes.value.map(o => o.id),
                    amount: betAmount.value
                })
            })

            if (res.ok) {
                alert('Bet placed successfully!')
                selectedOutcomes.value = []
                await auth.fetchBalance()
            } else {
                const msg = await res.text()
                // Extract message from JSON if possible, otherwise use text
                try {
                    const json = JSON.parse(msg)
                    alert('Failed to place bet: ' + (json.message || json.error || msg))
                } catch (e) {
                    alert('Failed to place bet: ' + msg)
                }
            }
        } catch (e) {
            console.error(e)
            alert('Error placing bet')
        }
    }

    return {
        selectedOutcomes,
        betAmount,
        toggleOutcome,
        removeOutcome,
        totalOdds,
        potentialReturn,
        placeBet,
        clearSlip
    }
})
