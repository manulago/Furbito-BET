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
                const isGoal = (g) => g && g.startsWith('Goles')
                // Basic check for player stats (Goleadores, Asistencias, etc) - usually imply not Main/Goals/Ambos
                // But specifically we just want to ensure we don't block compatible things.
                // Any outcome NOT Main and NOT Goals is treated as "Other" (Player Stats, BTTS) which we allow to combine.

                const newGroup = outcome.outcomeGroup
                const newIsMain = isMain(newGroup)
                const newIsGoal = isGoal(newGroup)

                // Filter out conflicting outcomes
                const conflictingIds = []

                sameEventOutcomes.forEach(existing => {
                    const existingGroup = existing.outcomeGroup
                    const existingIsMain = isMain(existingGroup)
                    const existingIsGoal = isGoal(existingGroup)

                    let conflict = false

                    if (newIsMain && existingIsMain) {
                        // Conflict: Cannot combine 1X2, DC, DNB
                        conflict = true
                    }
                    // Goals can now be combined with each other (Over + Under, etc)
                    // Else: Main + Goal is OK. Main + Other is OK. Goal + Other is OK. Other + Other is OK.

                    if (conflict) {
                        conflictingIds.push(existing.id)
                    }
                })

                // Remove conflicts
                conflictingIds.forEach(id => {
                    const idx = selectedOutcomes.value.findIndex(o => o.id === id)
                    if (idx !== -1) selectedOutcomes.value.splice(idx, 1)
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
