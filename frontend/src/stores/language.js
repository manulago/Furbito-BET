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
                welcome: 'Bienvenido',
                statistics: 'Estadísticas Furbito'
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
            },
            auth: {
                login: 'Iniciar Sesión',
                register: 'Registrarse',
                username: 'Usuario',
                email: 'Correo Electrónico',
                password: 'Contraseña',
                confirmPassword: 'Confirmar Contraseña',
                noAccount: '¿No tienes cuenta?',
                hasAccount: '¿Ya tienes cuenta?',
                registerHere: 'Regístrate aquí',
                loginHere: 'Inicia sesión aquí',
                passwordsDoNotMatch: '¡Las contraseñas no coinciden!',
                registrationFailed: 'Fallo en el registro'
            },
            home: {
                liveEvents: 'Eventos en Vivo',
                upcomingEvents: 'Próximos Eventos',
                noEvents: 'No hay eventos disponibles.'
            },
            results: {
                board: 'Panel de Resultados',
                loading: 'Cargando resultados...',
                noCompleted: 'Aún no hay eventos completados.',
                viewResults: 'Ver Resultados',
                wagered: 'Apostado',
                won: 'Ganado',
                net: 'Neto',
                filter: 'Solo partidos de Furbito FIC',
                unknown: 'Desconocido',
                noBetsEvent: 'No hay apuestas en este evento.',
                user: 'Usuario',
                betsBy: 'Apuestas de',
                bet: 'Apuesta',
                wager: 'Apostado',
                potWin: 'Gan. Pot.',
                selectUser: 'Selecciona un usuario para ver sus apuestas'
            },
            event: {
                loading: 'Cargando...',
                notFound: 'Evento no encontrado',
                uncategorized: 'Sin Categoría'
            },
            admin: {
                dashboard: 'Panel de Administración',
                events: 'Eventos',
                users: 'Usuarios',
                categories: 'Categorías',
                players: 'Jugadores',
                createEvent: 'Crear Evento',
                newEvent: 'Nuevo Evento',
                eventName: 'Nombre del Evento',
                dateTime: 'Fecha y Hora',
                cancel: 'Cancelar',
                create: 'Crear',
                simulateResult: 'Simular Resultado',
                home: 'Local',
                away: 'Visitante',
                resolveSettle: 'Resolver y Liquidar',
                edit: 'Editar',
                delete: 'Eliminar',
                clone: 'Clonar',
                outcomes: 'Mercados',
                addOutcome: 'Añadir Mercado',
                newOutcome: 'Nuevo Mercado',
                description: 'Descripción',
                category: 'Categoría',
                odds: 'Cuota',
                save: 'Guardar',
                manageBalance: 'Gestionar Saldo',
                currentBalance: 'Saldo Actual',
                addFunds: 'Añadir Fondos',
                security: 'Seguridad y Zona de Peligro',
                resetPassword: 'Restablecer Contraseña',
                newPassword: 'Nueva Contraseña',
                reset: 'Restablecer',
                emailSystem: 'Sistema de Email',
                sendTestEmail: 'Enviar Email de Prueba',
                deleteUser: 'Eliminar Cuenta de Usuario',
                initDefaults: 'Inic. Por Defecto',
                newCategory: 'Nueva Categoría',
                categoryName: 'Nombre de Categoría',
                clickToEdit: 'Clic para Editar',
                clickToManage: 'Clic para Gestionar',
                backToEvents: 'Volver a Eventos',
                backToUsers: 'Volver a Usuarios',
                role: 'Rol',
                editDetails: 'Editar Detalles',
                confirmClone: '¿Clonar este evento?',
                confirmDeleteEvent: '¿Eliminar este evento? Esta acción no se puede deshacer.',
                confirmDeleteUser: '¿Eliminar este usuario? Esta acción no se puede deshacer.',
                confirmDeleteCategory: '¿Eliminar categoría?',
                confirmDeleteOutcome: '¿Eliminar mercado?',
                confirmResolve: 'Resolver evento',
                eventResolved: '¡Evento resuelto y apuestas liquidadas!',
                balanceUpdated: '¡Saldo actualizado!',
                passwordReset: '¡Contraseña restablecida!',
                emailSent: '¡Email enviado con éxito!',
                emailFailed: 'Fallo al enviar email.',
                createEventBtn: '+ Crear Evento',
                confirmCreate: '¿Crear evento para',
                success: '¡Evento creado con éxito!',
                fail: 'Fallo al crear el evento.',
                selectCategory: 'Seleccionar Categoría',
                playersTitle: 'Jugadores Furbito FIC',
                addPlayer: 'Añadir Jugador',
                editPlayer: 'Editar Jugador',
                newPlayer: 'Nuevo Jugador',
                noPlayers: 'No hay jugadores registrados.',
                playerName: 'Nombre',
                playerTeam: 'Equipo',
                goals: 'Goles',
                assists: 'Asistencias',
                matchesPlayed: 'PJ',
                matchesStarted: 'Titular',
                yellowCards: 'T. Amarillas',
                redCards: 'T. Rojas',
                actions: 'Acciones',
                saveChanges: 'Guardar Cambios',
                confirmDeletePlayer: '¿Estás seguro de que quieres eliminar a este jugador?'
            },
            profile: {
                loading: 'Cargando perfil...',
                balance: 'Saldo',
                history: 'Historial de Apuestas',
                noBets: 'Aún no hay apuestas.',
                wager: 'Apostado',
                potWin: 'Ganancia Pot.',
                notFound: 'Usuario no encontrado'
            },
            common: {
                active: 'Activas',
                finished: 'Finalizadas',
                all: 'Todas',
                status: {
                    UPCOMING: 'Próximo',
                    COMPLETED: 'FINALIZADO',
                    PENDING: 'PENDIENTE',
                    LIVE: 'EN VIVO',
                    CANCELLED: 'CANCELADO',
                    WON: 'GANADA',
                    LOST: 'PERDIDA',
                    VOID: 'ANULADA'
                }
            },
            myBets: {
                noBets: 'No se encontraron apuestas.',
                cancel: 'Cancelar Apuesta'
            }
        },
        gl: {
            nav: {
                home: 'Inicio',
                myBets: 'As miñas Apostas',
                ranking: 'Clasificación',
                admin: 'Admin',
                logout: 'Pechar Sesión',
                welcome: 'Benvido',
                statistics: 'Estatísticas Furbito'
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
            },
            auth: {
                login: 'Iniciar Sesión',
                register: 'Rexistrarse',
                username: 'Usuario',
                email: 'Correo Electrónico',
                password: 'Contrasinal',
                confirmPassword: 'Confirmar Contrasinal',
                noAccount: 'Non tes conta?',
                hasAccount: 'Xa tes conta?',
                registerHere: 'Rexístrate aquí',
                loginHere: 'Inicia sesión aquí',
                passwordsDoNotMatch: 'Os contrasinais non coinciden!',
                registrationFailed: 'Fallo no rexistro'
            },
            home: {
                liveEvents: 'Eventos en Vivo',
                upcomingEvents: 'Próximos Eventos',
                noEvents: 'Non hai eventos dispoñibles.'
            },
            results: {
                board: 'Panel de Resultados',
                loading: 'Cargando resultados...',
                noCompleted: 'Aínda non hai eventos completados.',
                viewResults: 'Ver Resultados',
                wagered: 'Apostado',
                won: 'Gañado',
                net: 'Neto',
                filter: 'Só partidos de Furbito FIC',
                unknown: 'Descoñecido',
                noBetsEvent: 'Non hai apostas neste evento.',
                user: 'Usuario',
                betsBy: 'Apostas de',
                bet: 'Aposta',
                wager: 'Apostado',
                potWin: 'Gan. Pot.',
                selectUser: 'Selecciona un usuario para ver as súas apostas'
            },
            event: {
                loading: 'Cargando...',
                notFound: 'Evento non atopado',
                uncategorized: 'Sen Categoría'
            },
            admin: {
                dashboard: 'Panel de Administración',
                events: 'Eventos',
                users: 'Usuarios',
                categories: 'Categorías',
                players: 'Xogadores',
                createEvent: 'Crear Evento',
                newEvent: 'Novo Evento',
                eventName: 'Nome do Evento',
                dateTime: 'Data e Hora',
                cancel: 'Cancelar',
                create: 'Crear',
                simulateResult: 'Simular Resultado',
                home: 'Local',
                away: 'Visitante',
                resolveSettle: 'Resolver e Liquidar',
                edit: 'Editar',
                delete: 'Eliminar',
                clone: 'Clonar',
                outcomes: 'Mercados',
                addOutcome: 'Engadir Mercado',
                newOutcome: 'Novo Mercado',
                description: 'Descrición',
                category: 'Categoría',
                odds: 'Cota',
                save: 'Gardar',
                manageBalance: 'Xestionar Saldo',
                currentBalance: 'Saldo Actual',
                addFunds: 'Engadir Fondos',
                security: 'Seguridade e Zona de Perigo',
                resetPassword: 'Restablecer Contrasinal',
                newPassword: 'Novo Contrasinal',
                reset: 'Restablecer',
                emailSystem: 'Sistema de Email',
                sendTestEmail: 'Enviar Email de Proba',
                deleteUser: 'Eliminar Conta de Usuario',
                initDefaults: 'Inic. Por Defecto',
                newCategory: 'Nova Categoría',
                categoryName: 'Nome de Categoría',
                clickToEdit: 'Clic para Editar',
                clickToManage: 'Clic para Xestionar',
                backToEvents: 'Volver a Eventos',
                backToUsers: 'Volver a Usuarios',
                role: 'Rol',
                editDetails: 'Editar Detalles',
                confirmClone: '¿Clonar este evento?',
                confirmDeleteEvent: '¿Eliminar este evento? Esta acción non se pode desfacer.',
                confirmDeleteUser: '¿Eliminar este usuario? Esta acción non se pode desfacer.',
                confirmDeleteCategory: '¿Eliminar categoría?',
                confirmDeleteOutcome: '¿Eliminar mercado?',
                confirmResolve: 'Resolver evento',
                eventResolved: '¡Evento resolto e apostas liquidadas!',
                balanceUpdated: '¡Saldo actualizado!',
                passwordReset: '¡Contrasinal restablecido!',
                emailSent: '¡Email enviado con éxito!',
                emailFailed: 'Fallo ao enviar email.',
                createEventBtn: '+ Crear Evento',
                confirmCreate: '¿Crear evento para',
                success: 'Evento creado con éxito!',
                fail: 'Fallo ao crear o evento.',
                selectCategory: 'Seleccionar Categoría',
                playersTitle: 'Xogadores Furbito FIC',
                addPlayer: 'Engadir Xogador',
                editPlayer: 'Editar Xogador',
                newPlayer: 'Novo Xogador',
                noPlayers: 'Non hai xogadores rexistrados.',
                playerName: 'Nome',
                playerTeam: 'Equipo',
                goals: 'Goles',
                assists: 'Asistencias',
                matchesPlayed: 'PX',
                matchesStarted: 'Titular',
                yellowCards: 'T. Amarelas',
                redCards: 'T. Vermellas',
                actions: 'Accións',
                saveChanges: 'Gardar Cambios',
                confirmDeletePlayer: '¿Estás seguro de que queres eliminar este xogador?'
            },
            profile: {
                loading: 'Cargando perfil...',
                balance: 'Saldo',
                history: 'Historial de Apostas',
                noBets: 'Aínda non hai apostas.',
                wager: 'Apostado',
                potWin: 'Ganancia Pot.',
                notFound: 'Usuario non atopado'
            },
            common: {
                active: 'Activas',
                finished: 'Finalizadas',
                all: 'Todas',
                status: {
                    UPCOMING: 'Próximo',
                    COMPLETED: 'FINALIZADO',
                    PENDING: 'PENDENTE',
                    LIVE: 'EN VIVO',
                    CANCELLED: 'CANCELADO',
                    WON: 'GAÑADA',
                    LOST: 'PERDIDA',
                    VOID: 'ANULADA'
                }
            },
            myBets: {
                noBets: 'Non se atoparon apostas.',
                cancel: 'Cancelar Aposta'
            }
        },
        en: {
            nav: {
                home: 'Home',
                myBets: 'My Bets',
                ranking: 'Standings',
                admin: 'Admin',
                logout: 'Logout',
                welcome: 'Welcome',
                statistics: 'Furbito Statistics'
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
            },
            auth: {
                login: 'Login',
                register: 'Register',
                username: 'Username',
                email: 'Email',
                password: 'Password',
                confirmPassword: 'Confirm Password',
                noAccount: "Don't have an account?",
                hasAccount: 'Already have an account?',
                registerHere: 'Register here',
                loginHere: 'Login here',
                passwordsDoNotMatch: 'Passwords do not match!',
                registrationFailed: 'Registration failed'
            },
            home: {
                liveEvents: 'Live Events',
                upcomingEvents: 'Upcoming Events',
                noEvents: 'No events available.'
            },
            results: {
                board: 'Results Board',
                loading: 'Loading results...',
                noCompleted: 'No completed events yet.',
                viewResults: 'View Results',
                wagered: 'Wagered',
                won: 'Won',
                net: 'Net',
                filter: 'Only Furbito FIC matches',
                unknown: 'Unknown',
                noBetsEvent: 'No bets placed on this event.',
                user: 'User',
                betsBy: 'Bets by',
                bet: 'Bet',
                wager: 'Wager',
                potWin: 'Pot. Win',
                selectUser: 'Select a user to view bets'
            },
            event: {
                loading: 'Loading...',
                notFound: 'Event not found',
                uncategorized: 'Uncategorized'
            },
            admin: {
                dashboard: 'Admin Dashboard',
                events: 'Events',
                users: 'Users',
                categories: 'Categories',
                players: 'Players',
                createEvent: 'Create Event',
                newEvent: 'New Event',
                eventName: 'Event Name',
                dateTime: 'Date & Time',
                cancel: 'Cancel',
                create: 'Create',
                simulateResult: 'Simulate Result',
                home: 'Home',
                away: 'Away',
                resolveSettle: 'Resolve & Settle',
                edit: 'Edit',
                delete: 'Delete',
                clone: 'Clone',
                outcomes: 'Outcomes (Markets)',
                addOutcome: 'Add Outcome',
                newOutcome: 'New Outcome',
                description: 'Description',
                category: 'Category',
                odds: 'Odds',
                save: 'Save',
                manageBalance: 'Manage Balance',
                currentBalance: 'Current Balance',
                addFunds: 'Add Funds',
                security: 'Security & Danger Zone',
                resetPassword: 'Reset Password',
                newPassword: 'New Password',
                reset: 'Reset',
                emailSystem: 'Email System',
                sendTestEmail: 'Send Test Email to User',
                deleteUser: 'Delete User Account',
                initDefaults: 'Init Defaults',
                newCategory: 'New Category',
                categoryName: 'Category Name',
                clickToEdit: 'Click to Edit',
                clickToManage: 'Click to Manage',
                backToEvents: 'Back to Events',
                backToUsers: 'Back to Users',
                role: 'Role',
                editDetails: 'Edit Details',
                confirmClone: 'Clone this event?',
                confirmDeleteEvent: 'Delete this event? This action cannot be undone.',
                confirmDeleteUser: 'Delete this user? This action cannot be undone.',
                confirmDeleteCategory: 'Delete category?',
                confirmDeleteOutcome: 'Delete outcome?',
                confirmResolve: 'Resolve event',
                eventResolved: 'Event resolved and bets settled!',
                balanceUpdated: 'Balance updated!',
                passwordReset: 'Password reset!',
                emailSent: 'Email sent successfully!',
                emailFailed: 'Failed to send email.',
                createEventBtn: '+ Create Event',
                confirmCreate: 'Create event for',
                success: 'Event created successfully!',
                fail: 'Failed to create event.',
                selectCategory: 'Select Category',
                playersTitle: 'Furbito FIC Players',
                addPlayer: 'Add Player',
                editPlayer: 'Edit Player',
                newPlayer: 'New Player',
                noPlayers: 'No players registered.',
                playerName: 'Name',
                playerTeam: 'Team',
                goals: 'Goals',
                assists: 'Assists',
                matchesPlayed: 'Played',
                matchesStarted: 'Started',
                yellowCards: 'Y. Cards',
                redCards: 'R. Cards',
                actions: 'Actions',
                saveChanges: 'Save Changes',
                confirmDeletePlayer: 'Are you sure you want to delete this player?'
            },
            profile: {
                loading: 'Loading profile...',
                balance: 'Balance',
                history: 'Bet History',
                noBets: 'No bets placed yet.',
                wager: 'Wager',
                potWin: 'Potential Win',
                notFound: 'User not found'
            },
            common: {
                active: 'Active',
                finished: 'Finished',
                all: 'All',
                status: {
                    UPCOMING: 'Upcoming',
                    COMPLETED: 'COMPLETED',
                    PENDING: 'PENDING',
                    LIVE: 'LIVE',
                    CANCELLED: 'CANCELLED',
                    WON: 'WON',
                    LOST: 'LOST',
                    VOID: 'VOID'
                }
            },
            myBets: {
                noBets: 'No bets found.',
                cancel: 'Cancel Bet'
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
