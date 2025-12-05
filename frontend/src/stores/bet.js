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
            // Check if there is already an outcome from the same event and category
            const conflictIndex = selectedOutcomes.value.findIndex(o =>
                o.event.id === outcome.event.id &&
                o.outcomeGroup === outcome.outcomeGroup
            )

            if (conflictIndex !== -1) {
                // Swap: remove the conflicting outcome and add the new one
                selectedOutcomes.value.splice(conflictIndex, 1)
            }
            selectedOutcomes.value.push(outcome)
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
        placeBet
    }
})
