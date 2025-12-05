import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useLanguageStore = defineStore('language', () => {
    const currentLanguage = ref('es') // Default to Spanish

    const translations = {
        es: {
            nav: {
                home: 'Inicio',
                myBets: 'Mis Apuestas',
                ranking: 'Clasificación',
                admin: 'Admin',
                logout: 'Cerrar Sesión',
                welcome: 'Bienvenido'
            },
            ranking: {
                title: 'Clasificación',
                tabs: {
                    standings: 'Clasificación',
                    results: 'Resultados',
                    users: 'Usuarios'
                },
                standings: {
                    pos: 'Pos',
                    team: 'Equipo',
                    pts: 'Pts',
                    played: 'PJ',
                    won: 'PG',
                    drawn: 'PE',
                    lost: 'PP',
                    gf: 'GF',
                    ga: 'GC'
                },
                results: {
                    filter: 'Solo partidos de Furbito FIC',
                    unknown: 'Desconocido'
                },
                users: {
                    pos: 'Pos',
                    user: 'Usuario',
                    balance: 'Saldo'
                },
                loading: 'Cargando datos...',
                error: 'Error al cargar los datos. Por favor, inténtalo de nuevo.'
            },
            betSlip: {
                title: 'Boleto de Apuestas',
                totalOdds: 'Cuota Total',
                amount: 'Cantidad (€)',
                potentialReturn: 'Ganancia Potencial',
                placeBet: 'Apostar'
            }
        },
        gl: {
            nav: {
                home: 'Inicio',
                myBets: 'As miñas Apostas',
                ranking: 'Clasificación',
                admin: 'Admin',
                logout: 'Pechar Sesión',
                welcome: 'Benvido'
            },
            ranking: {
                title: 'Clasificación',
                tabs: {
                    standings: 'Clasificación',
                    results: 'Resultados',
                    users: 'Usuarios'
                },
                standings: {
                    pos: 'Pos',
                    team: 'Equipo',
                    pts: 'Pts',
                    played: 'XX',
                    won: 'XG',
                    drawn: 'XE',
                    lost: 'XP',
                    gf: 'GF',
                    ga: 'GC'
                },
                results: {
                    filter: 'Só partidos de Furbito FIC',
                    unknown: 'Descoñecido'
                },
                users: {
                    pos: 'Pos',
                    user: 'Usuario',
                    balance: 'Saldo'
                },
                loading: 'Cargando datos...',
                error: 'Erro ao cargar os datos. Por favor, inténtao de novo.'
            },
            betSlip: {
                title: 'Boleto de Apostas',
                totalOdds: 'Cota Total',
                amount: 'Cantidade (€)',
                potentialReturn: 'Ganancia Potencial',
                placeBet: 'Apostar'
            }
        },
        en: {
            nav: {
                home: 'Home',
                myBets: 'My Bets',
                ranking: 'Standings',
                admin: 'Admin',
                logout: 'Logout',
                welcome: 'Welcome'
            },
            ranking: {
                title: 'Standings',
                tabs: {
                    standings: 'Standings',
                    results: 'Results',
                    users: 'Users'
                },
                standings: {
                    pos: 'Pos',
                    team: 'Team',
                    pts: 'Pts',
                    played: 'P',
                    won: 'W',
                    drawn: 'D',
                    lost: 'L',
                    gf: 'GF',
                    ga: 'GA'
                },
                results: {
                    filter: 'Only Furbito FIC matches',
                    unknown: 'Unknown'
                },
                users: {
                    pos: 'Pos',
                    user: 'User',
                    balance: 'Balance'
                },
                loading: 'Loading data...',
                error: 'Error loading data. Please try again.'
            },
            betSlip: {
                title: 'Bet Slip',
                totalOdds: 'Total Odds',
                amount: 'Amount (€)',
                potentialReturn: 'Potential Return',
                placeBet: 'Place Bet'
            }
        }
    }

    const t = (key) => {
        const keys = key.split('.')
        let value = translations[currentLanguage.value]
        for (const k of keys) {
            if (value && value[k]) {
                value = value[k]
            } else {
                return key // Return key if translation not found
            }
        }
        return value
    }

    function setLanguage(lang) {
        if (translations[lang]) {
            currentLanguage.value = lang
        }
    }

    return { currentLanguage, setLanguage, t }
})
