import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import StatisticsView from '../views/StatisticsView.vue'
import AdminView from '../views/AdminView.vue'
import MyBetsView from '../views/MyBetsView.vue'
import { useAuthStore } from '../stores/auth'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView
        },
        {
            path: '/login',
            name: 'login',
            component: LoginView
        },
        {
            path: '/register',
            name: 'register',
            component: () => import('../views/RegisterView.vue')
        },
        {
            path: '/forgot-password',
            name: 'forgot-password',
            component: () => import('../views/ForgotPasswordView.vue')
        },
        {
            path: '/reset-password',
            name: 'reset-password',
            component: () => import('../views/ResetPasswordView.vue')
        },
        {
            path: '/statistics',
            name: 'statistics',
            component: StatisticsView
        },
        {
            path: '/admin',
            name: 'admin',
            component: AdminView,
            meta: { requiresAdmin: true }
        },
        {
            path: '/my-bets',
            name: 'my-bets',
            component: MyBetsView
        },
        {
            path: '/ranking',
            name: 'ranking',
            component: () => import('../views/RankingView.vue')
        },
        {
            path: '/results',
            name: 'results',
            component: () => import('../views/ResultsView.vue')
        },
        {
            path: '/event/:id',
            name: 'event-detail',
            component: () => import('../views/EventDetailView.vue')
        },
        {
            path: '/user/:id',
            name: 'user-profile',
            component: () => import('../views/UserProfileView.vue')
        },
        {
            path: '/profile/settings',
            name: 'profile-settings',
            component: () => import('../views/ProfileSettingsView.vue')
        },
        {
            path: '/profile/confirm',
            name: 'profile-confirm',
            component: () => import('../views/ProfileConfirmationView.vue')
        },
        {
            path: '/confirm-account',
            name: 'confirm-account',
            component: () => import('../views/AccountConfirmationView.vue')
        },
        {
            path: '/user-ranking',
            name: 'user-ranking',
            component: () => import('../views/UserRankingView.vue')
        },
        {
            path: '/help',
            name: 'help',
            component: () => import('../views/HelpView.vue')
        },
        {
            path: '/roulette',
            name: 'roulette',
            component: () => import('../views/RouletteView.vue')
        },
    ]
})

router.beforeEach((to, from, next) => {
    const auth = useAuthStore()
    const publicPages = ['/login', '/register', '/statistics', '/forgot-password', '/reset-password', '/confirm-account', '/help']
    const guestOnlyPages = ['/login', '/register']
    const authRequired = !publicPages.includes(to.path)

    // If logged in and trying to access login/register, redirect to home
    if (guestOnlyPages.includes(to.path) && auth.user) {
        return next('/')
    }

    if (authRequired && !auth.user) {
        next('/login')
    } else if (to.meta.requiresAdmin && auth.user?.role !== 'ADMIN') {
        next('/')
    } else {
        next()
    }
})

export default router
