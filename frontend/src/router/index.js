import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
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
            path: '/event/:id',
            name: 'event-detail',
            component: () => import('../views/EventDetailView.vue')
        },
        {
            path: '/ranking',
            name: 'ranking',
            component: () => import('../views/RankingView.vue')
        }
    ]
})

router.beforeEach((to, from, next) => {
    const auth = useAuthStore()
    const publicPages = ['/login', '/register']
    const authRequired = !publicPages.includes(to.path)

    if (authRequired && !auth.user) {
        next('/login')
    } else if (to.meta.requiresAdmin && auth.user?.role !== 'ADMIN') {
        next('/')
    } else {
        next()
    }
})

export default router
