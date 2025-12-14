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
                statistics: 'Estadísticas',
                profile: 'Perfil',
                userRanking: 'Usuarios',
                help: 'Ayuda'
            },
            help: {
                title: 'Centro de Ayuda FurbitoBET',
                subtitle: 'Tu guía completa para dominar la plataforma',
                intro: 'Bienvenido a la sección de ayuda. Aquí encontrarás todo lo necesario para navegar, apostar y competir en FurbitoBET.',
                registration: {
                    title: 'Registro y Cuenta',
                    desc: 'Para comenzar tu viaje en FurbitoBET, necesitas una cuenta verificada.',
                    details: [
                        '**Usuario Único**: Elige un nombre de usuario que te represente en los rankings.',
                        '**Email Real**: Es crucial usar un correo válido, ya que recibirás un enlace de confirmación.',
                        '**Activación**: Tu cuenta no estará activa hasta que hagas clic en el enlace enviado a tu correo. ¡Revisa tu carpeta de spam si no lo ves!'
                    ]
                },
                betting: {
                    title: 'Sistema de Apuestas',
                    desc: 'La emoción del juego está a un clic de distancia. Aprende a realizar tus jugadas.',
                    details: [
                        '**Cuotas (Odds)**: Representan el multiplicador de tu ganancia. Si apuestas 10€ a cuota 2.50, recibirás 25€ (15€ de ganancia neta).',
                        '**Boleto de Apuestas**: Al seleccionar una cuota, se añade a tu boleto en la parte inferior.',
                        '**Combinadas**: Puedes combinar varios eventos para multiplicar tus cuotas, pero recuerda que no puedes combinar mercados contradictorios del mismo evento.',
                        '**Ganancia Potencial**: Te muestra cuánto ganarás si aciertas todos tus pronósticos.'
                    ]
                },
                ranking: {
                    title: 'Clasificaciones y Competencia',
                    desc: 'Demuestra que eres el mejor pronosticador de la liga.',
                    details: [
                        '**Clasificación de Liga**: Tabla oficial de los equipos de Furbito FIC.',
                        '**Ranking de Usuarios**: Compite contra otros usuarios. Se ordena por "Ganancia Bruta" (Total ganado), no solo por saldo actual.',
                        '**Saldo vs Ganancia**: El saldo es lo que tienes disponible para apostar. La ganancia es el acumulado histórico de tus victorias.'
                    ]
                },
                stats: {
                    title: 'Estadísticas Furbito',
                    desc: 'Información detallada para fundamentar tus decisiones.',
                    details: [
                        'Consulta el rendimiento de los jugadores de la liga.',
                        'Goles, asistencias, tarjetas y partidos jugados.',
                        'Utiliza estos datos para predecir mejor los resultados de los partidos.'
                    ]
                },
                pwa: {
                    title: 'Instala la App en Cualquier Dispositivo',
                    subtitle: 'Acceso rápido desde tu pantalla de inicio - Móvil y PC',
                    description: 'FurbitoBET es una PWA (Progressive Web App) que puedes instalar en <strong>cualquier dispositivo</strong>: Android, iPhone, Windows, Mac o Linux. Disfruta de acceso instantáneo sin abrir el navegador.',
                    benefits: {
                        faster: {
                            title: 'Más Rápido',
                            desc: 'Carga instantánea desde tu pantalla de inicio'
                        },
                        offline: {
                            title: 'Funciona Offline',
                            desc: 'Visualiza contenido aunque pierdas conexión'
                        },
                        noBrowser: {
                            title: 'Sin Navegador',
                            desc: 'Experiencia de app nativa completa'
                        },
                        multiplatform: {
                            title: 'Multiplataforma',
                            desc: 'Funciona en todos tus dispositivos'
                        }
                    },
                    instructions: {
                        title: '📖 Instrucciones de Instalación por Plataforma',
                        android: {
                            title: 'Android (Chrome/Edge)',
                            step1: 'Abre <strong>FurbitoBET</strong> en Chrome o Edge',
                            step2: 'Busca el botón flotante <strong>"📱 Instalar App"</strong> o el icono ⊕ en la barra de direcciones',
                            step3: 'Toca <strong>"Instalar"</strong> o <strong>"Añadir a pantalla de inicio"</strong>',
                            step4: '¡Listo! Encontrarás el icono en tu pantalla de inicio'
                        },
                        ios: {
                            title: 'iPhone/iPad (Safari)',
                            step1: 'Abre <strong>FurbitoBET</strong> en Safari (⚠️ debe ser Safari)',
                            step2: 'Toca el botón <strong>Compartir</strong> en la barra inferior',
                            step3: 'Desplázate y selecciona <strong>"Añadir a pantalla de inicio"</strong>',
                            step4: 'Toca <strong>"Añadir"</strong> y ¡disfruta de la app!'
                        },
                        windows: {
                            title: 'Windows (Chrome/Edge)',
                            step1: 'Abre <strong>FurbitoBET</strong> en Chrome o Edge',
                            step2: 'Busca el icono <strong>⊕</strong> o <strong>🖥️</strong> en la barra de direcciones',
                            step3: 'Haz clic en <strong>"Instalar FurbitoBET"</strong>',
                            step4: 'La app se abrirá en una ventana independiente y aparecerá en el menú de inicio',
                            tip: '💡 También puedes usar: Menú (⋮) → "Instalar FurbitoBET"'
                        },
                        mac: {
                            title: 'Mac (Chrome/Edge/Safari)',
                            step1: 'Abre <strong>FurbitoBET</strong> en Chrome, Edge o Safari',
                            step2: '<strong>Chrome/Edge:</strong> Busca el icono ⊕ en la barra de direcciones',
                            step3: 'Haz clic en <strong>"Instalar FurbitoBET"</strong>',
                            step4: 'La app aparecerá en el Dock y en Aplicaciones',
                            tip: '💡 Safari: Archivo → "Añadir a Dock" (macOS Sonoma+)'
                        },
                        linux: {
                            title: 'Linux (Chrome/Firefox/Edge)',
                            chromeBrowser: 'Chrome/Chromium/Edge:',
                            chromeStep1: 'Abre FurbitoBET en el navegador',
                            chromeStep2: 'Busca el icono ⊕ en la barra de direcciones',
                            chromeStep3: 'Haz clic en "Instalar FurbitoBET"',
                            chromeStep4: 'La app aparecerá en tu menú de aplicaciones',
                            firefoxBrowser: 'Firefox:',
                            firefoxStep1: 'Abre FurbitoBET en Firefox',
                            firefoxStep2: 'Menú (☰) → "Instalar sitio como aplicación"',
                            firefoxStep3: 'Confirma la instalación',
                            firefoxStep4: 'Accede desde el menú de aplicaciones'
                        }
                    },
                    tip: '💡 <strong>Tip:</strong> Una vez instalada, la app se abrirá en pantalla completa sin la barra del navegador, funcionará más rápido y podrás acceder incluso sin conexión a internet.',
                    troubleshooting: '¿Problemas con la instalación? Asegúrate de usar un navegador compatible (Chrome, Edge, Safari o Firefox)'
                },
                contact: 'Soporte Técnico',
                contactDesc: '¿Tienes algún problema? Contacta con administración a través del correo oficial.'
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
                    title: 'Clasificación de Usuarios',
                    pos: 'Pos',
                    user: 'Usuario',
                    balance: 'Saldo',
                    profit: 'Ganancia'
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
                registrationFailed: 'Fallo en el registro',
                forgotPassword: '¿Has olvidado tu contraseña?',
                registrationSuccess: '¡Registro exitoso! Por favor revisa tu correo para confirmar tu cuenta.',
                invalidToken: 'Token inválido o expirado.',
                confirmationFailed: 'Error al confirmar la cuenta.',
                verifying: 'Verificando cuenta...',
                accountVerified: '¡Cuenta Verificada!',
                accountVerifiedDesc: 'Tu cuenta ha sido activada con éxito. Ya puedes iniciar sesión y empezar a apostar.',
                loginNow: 'Iniciar Sesión Ahora',
                error: 'Error'
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
                cancel: 'Cancelar Apuesta',
                netProfit: 'Ganancia Neta'
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
                statistics: 'Estatísticas',
                profile: 'Perfil',
                userRanking: 'Usuarios',
                help: 'Axuda'
            },
            help: {
                title: 'Centro de Axuda FurbitoBET',
                subtitle: 'A túa guía completa para dominar a plataforma',
                intro: 'Benvido á sección de axuda. Aquí atoparás todo o necesario para navegar, apostar e competir en FurbitoBET.',
                registration: {
                    title: 'Rexistro e Conta',
                    desc: 'Para comezar a túa viaxe en FurbitoBET, precisas unha conta verificada.',
                    details: [
                        '**Usuario Único**: Elixe un nome de usuario que te represente nos rankings.',
                        '**Email Real**: É crucial usar un correo válido, xa que recibirás unha ligazón de confirmación.',
                        '**Activación**: A túa conta non estará activa ata que fagas clic na ligazón enviada ao teu correo. Revisa o spam!'
                    ]
                },
                betting: {
                    title: 'Sistema de Apostas',
                    desc: 'A emoción do xogo está a un clic de distancia. Aprende a realizar as túas xogadas.',
                    details: [
                        '**Cotas (Odds)**: Representan o multiplicador da túa ganancia. Se apostas 10€ a cota 2.50, recibirás 25€.',
                        '**Boleto de Apostas**: Ao seleccionar unha cota, engádese ao teu boleto na parte inferior.',
                        '**Combinadas**: Podes combinar varios eventos para multiplicar as túas cotas.',
                        '**Ganancia Potencial**: Amósache canto gañarás se acertas todos os teus pronósticos.'
                    ]
                },
                ranking: {
                    title: 'Clasificacións e Competición',
                    desc: 'Demostra que es o mellor pronosticador da liga.',
                    details: [
                        '**Clasificación de Liga**: Táboa oficial dos equipos de Furbito FIC.',
                        '**Ranking de Usuarios**: Compite contra outros usuarios. Ordénase por "Ganancia Bruta".',
                        '**Saldo vs Ganancia**: O saldo é o que tens dispoñible. A ganancia é o acumulado das túas vitorias.'
                    ]
                },
                stats: {
                    title: 'Estatísticas Furbito',
                    desc: 'Información detallada para fundamentar as túas decisións.',
                    details: [
                        'Consulta o rendemento dos xogadores da liga.',
                        'Goles, asistencias, tarxetas e partidos xogados.',
                        'Utiliza estes datos para predicir mellor os resultados.'
                    ]
                },
                pwa: {
                    title: 'Instala a App en Calquera Dispositivo',
                    subtitle: 'Acceso rápido desde a túa pantalla de inicio - Móbil e PC',
                    description: 'FurbitoBET é unha PWA (Progressive Web App) que podes instalar en <strong>calquera dispositivo</strong>: Android, iPhone, Windows, Mac ou Linux. Goza de acceso instantáneo sen abrir o navegador.',
                    benefits: {
                        faster: {
                            title: 'Máis Rápido',
                            desc: 'Carga instantánea desde a túa pantalla de inicio'
                        },
                        offline: {
                            title: 'Funciona Offline',
                            desc: 'Visualiza contido aínda que perdas conexión'
                        },
                        noBrowser: {
                            title: 'Sen Navegador',
                            desc: 'Experiencia de app nativa completa'
                        },
                        multiplatform: {
                            title: 'Multiplataforma',
                            desc: 'Funciona en todos os teus dispositivos'
                        }
                    },
                    instructions: {
                        title: '📖 Instrucións de Instalación por Plataforma',
                        android: {
                            title: 'Android (Chrome/Edge)',
                            step1: 'Abre <strong>FurbitoBET</strong> en Chrome ou Edge',
                            step2: 'Busca o botón flotante <strong>"📱 Instalar App"</strong> ou o icono ⊕ na barra de enderezos',
                            step3: 'Toca <strong>"Instalar"</strong> ou <strong>"Engadir á pantalla de inicio"</strong>',
                            step4: 'Listo! Atoparás o icono na túa pantalla de inicio'
                        },
                        ios: {
                            title: 'iPhone/iPad (Safari)',
                            step1: 'Abre <strong>FurbitoBET</strong> en Safari (⚠️ debe ser Safari)',
                            step2: 'Toca o botón <strong>Compartir</strong> na barra inferior',
                            step3: 'Desprázate e selecciona <strong>"Engadir á pantalla de inicio"</strong>',
                            step4: 'Toca <strong>"Engadir"</strong> e goza da app!'
                        },
                        windows: {
                            title: 'Windows (Chrome/Edge)',
                            step1: 'Abre <strong>FurbitoBET</strong> en Chrome ou Edge',
                            step2: 'Busca o icono <strong>⊕</strong> ou <strong>🖥️</strong> na barra de enderezos',
                            step3: 'Fai clic en <strong>"Instalar FurbitoBET"</strong>',
                            step4: 'A app abrirase nunha ventá independente e aparecerá no menú de inicio',
                            tip: '💡 Tamén podes usar: Menú (⋮) → "Instalar FurbitoBET"'
                        },
                        mac: {
                            title: 'Mac (Chrome/Edge/Safari)',
                            step1: 'Abre <strong>FurbitoBET</strong> en Chrome, Edge ou Safari',
                            step2: '<strong>Chrome/Edge:</strong> Busca o icono ⊕ na barra de enderezos',
                            step3: 'Fai clic en <strong>"Instalar FurbitoBET"</strong>',
                            step4: 'A app aparecerá no Dock e en Aplicacións',
                            tip: '💡 Safari: Arquivo → "Engadir ao Dock" (macOS Sonoma+)'
                        },
                        linux: {
                            title: 'Linux (Chrome/Firefox/Edge)',
                            chromeBrowser: 'Chrome/Chromium/Edge:',
                            chromeStep1: 'Abre FurbitoBET no navegador',
                            chromeStep2: 'Busca o icono ⊕ na barra de enderezos',
                            chromeStep3: 'Fai clic en "Instalar FurbitoBET"',
                            chromeStep4: 'A app aparecerá no teu menú de aplicacións',
                            firefoxBrowser: 'Firefox:',
                            firefoxStep1: 'Abre FurbitoBET en Firefox',
                            firefoxStep2: 'Menú (☰) → "Instalar sitio como aplicación"',
                            firefoxStep3: 'Confirma a instalación',
                            firefoxStep4: 'Accede desde o menú de aplicacións'
                        }
                    },
                    tip: '💡 <strong>Tip:</strong> Unha vez instalada, a app abrirase en pantalla completa sen a barra do navegador, funcionará máis rápido e poderás acceder incluso sen conexión a internet.',
                    troubleshooting: 'Problemas coa instalación? Asegúrate de usar un navegador compatible (Chrome, Edge, Safari ou Firefox)'
                },
                contact: 'Soporte Técnico',
                contactDesc: '¿Tes algún problema? Contacta coa administración.'
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
                    pts: 'Pts',
                    played: 'PX',
                    won: 'PG',
                    drawn: 'PE',
                    lost: 'PP',
                    gf: 'GF',
                    ga: 'GC'
                },
                results: {
                    filter: 'Só partidos de Furbito FIC',
                    unknown: 'Descoñecido'
                },
                users: {
                    title: 'Clasificación de Usuarios',
                    pos: 'Pos',
                    user: 'Usuario',
                    balance: 'Saldo',
                    profit: 'Ganancia'
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
                noAccount: '¿Non tes conta?',
                hasAccount: '¿Xa tes conta?',
                registerHere: 'Rexístrate aquí',
                loginHere: 'Inicia sesión aquí',
                passwordsDoNotMatch: 'Os contrasinais non coinciden!',
                registrationFailed: 'Fallo no rexistro',
                forgotPassword: '¿Esqueciches o teu contrasinal?',
                registrationSuccess: 'Rexistro exitoso! Por favor revisa o teu correo para confirmar a túa conta.',
                invalidToken: 'Token inválido ou expirado.',
                confirmationFailed: 'Erro ao confirmar a conta.',
                verifying: 'Verificando conta...',
                accountVerified: 'Conta Verificada!',
                accountVerifiedDesc: 'A túa conta activouse con éxito. Xa podes iniciar sesión e comezar a apostar.',
                loginNow: 'Iniciar Sesión Agora',
                error: 'Erro'
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
                success: '¡Evento creado con éxito!',
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
                cancel: 'Cancelar Aposta',
                netProfit: 'Ganancia Neta'
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
                statistics: 'Stats',
                profile: 'Profile',
                userRanking: 'Users',
                help: 'Help'
            },
            help: {
                title: 'FurbitoBET Help Center',
                subtitle: 'Your complete guide to mastering the platform',
                intro: 'Welcome to the help section. Here you will find everything you need to navigate, bet, and compete on FurbitoBET.',
                registration: {
                    title: 'Registration & Account',
                    desc: 'To start your journey on FurbitoBET, you need a verified account.',
                    details: [
                        '**Unique Username**: Choose a username that represents you in the rankings.',
                        '**Real Email**: It is crucial to use a valid email, as you will receive a confirmation link.',
                        '**Activation**: Your account will not be active until you click the link sent to your email. Check spam!'
                    ]
                },
                betting: {
                    title: 'Betting System',
                    desc: 'The thrill of the game is just a click away. Learn how to place your bets.',
                    details: [
                        '**Odds**: Represent your winning multiplier. If you bet 10€ at 2.50 odds, you get 25€.',
                        '**Bet Slip**: Selecting an odd adds it to your slip at the bottom.',
                        '**Combos**: Combine multiple events to multiply your odds.',
                        '**Potential Win**: Shows how much you will win if all your predictions are correct.'
                    ]
                },
                ranking: {
                    title: 'Rankings & Competition',
                    desc: 'Prove you are the best predictor in the league.',
                    details: [
                        '**League Standings**: Official table of Furbito FIC teams.',
                        '**User Ranking**: Compete against others. Ordered by "Gross Profit".',
                        '**Balance vs Profit**: Balance is what you have available to bet. Profit is your historical accumulated winnings.'
                    ]
                },
                stats: {
                    title: 'Furbito Stats',
                    desc: 'Detailed info to support your decisions.',
                    details: [
                        'Check the performance of league players.',
                        'Goals, assists, cards, and matches played.',
                        'Use this data to better predict match outcomes.'
                    ]
                },
                pwa: {
                    title: 'Install the App on Any Device',
                    subtitle: 'Quick access from your home screen - Mobile & PC',
                    description: 'FurbitoBET is a PWA (Progressive Web App) that you can install on <strong>any device</strong>: Android, iPhone, Windows, Mac, or Linux. Enjoy instant access without opening a browser.',
                    benefits: {
                        faster: {
                            title: 'Faster',
                            desc: 'Instant loading from your home screen'
                        },
                        offline: {
                            title: 'Works Offline',
                            desc: 'View content even when you lose connection'
                        },
                        noBrowser: {
                            title: 'No Browser',
                            desc: 'Complete native app experience'
                        },
                        multiplatform: {
                            title: 'Cross-Platform',
                            desc: 'Works on all your devices'
                        }
                    },
                    instructions: {
                        title: '📖 Installation Instructions by Platform',
                        android: {
                            title: 'Android (Chrome/Edge)',
                            step1: 'Open <strong>FurbitoBET</strong> in Chrome or Edge',
                            step2: 'Look for the floating <strong>"📱 Install App"</strong> button or the ⊕ icon in the address bar',
                            step3: 'Tap <strong>"Install"</strong> or <strong>"Add to home screen"</strong>',
                            step4: 'Done! You\'ll find the icon on your home screen'
                        },
                        ios: {
                            title: 'iPhone/iPad (Safari)',
                            step1: 'Open <strong>FurbitoBET</strong> in Safari (⚠️ must be Safari)',
                            step2: 'Tap the <strong>Share</strong> button in the bottom bar',
                            step3: 'Scroll and select <strong>"Add to Home Screen"</strong>',
                            step4: 'Tap <strong>"Add"</strong> and enjoy the app!'
                        },
                        windows: {
                            title: 'Windows (Chrome/Edge)',
                            step1: 'Open <strong>FurbitoBET</strong> in Chrome or Edge',
                            step2: 'Look for the <strong>⊕</strong> or <strong>🖥️</strong> icon in the address bar',
                            step3: 'Click <strong>"Install FurbitoBET"</strong>',
                            step4: 'The app will open in a separate window and appear in the start menu',
                            tip: '💡 You can also use: Menu (⋮) → "Install FurbitoBET"'
                        },
                        mac: {
                            title: 'Mac (Chrome/Edge/Safari)',
                            step1: 'Open <strong>FurbitoBET</strong> in Chrome, Edge, or Safari',
                            step2: '<strong>Chrome/Edge:</strong> Look for the ⊕ icon in the address bar',
                            step3: 'Click <strong>"Install FurbitoBET"</strong>',
                            step4: 'The app will appear in the Dock and Applications',
                            tip: '💡 Safari: File → "Add to Dock" (macOS Sonoma+)'
                        },
                        linux: {
                            title: 'Linux (Chrome/Firefox/Edge)',
                            chromeBrowser: 'Chrome/Chromium/Edge:',
                            chromeStep1: 'Open FurbitoBET in the browser',
                            chromeStep2: 'Look for the ⊕ icon in the address bar',
                            chromeStep3: 'Click "Install FurbitoBET"',
                            chromeStep4: 'The app will appear in your applications menu',
                            firefoxBrowser: 'Firefox:',
                            firefoxStep1: 'Open FurbitoBET in Firefox',
                            firefoxStep2: 'Menu (☰) → "Install site as app"',
                            firefoxStep3: 'Confirm installation',
                            firefoxStep4: 'Access from the applications menu'
                        }
                    },
                    tip: '💡 <strong>Tip:</strong> Once installed, the app will open in full screen without the browser bar, run faster, and you can access it even without an internet connection.',
                    troubleshooting: 'Having installation issues? Make sure to use a compatible browser (Chrome, Edge, Safari, or Firefox)'
                },
                contact: 'Technical Support',
                contactDesc: 'Having issues? Contact administration via official email.'
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
                    title: 'User Ranking',
                    pos: 'Pos',
                    user: 'User',
                    balance: 'Balance',
                    profit: 'Profit'
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
                registrationFailed: 'Registration failed',
                forgotPassword: 'Forgot your password?'
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
                cancel: 'Cancel Bet',
                netProfit: 'Net Profit'
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
