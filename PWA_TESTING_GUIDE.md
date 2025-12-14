# 📱 Guía para Probar la PWA de FurbitoBET

## ✅ Archivos Creados

1. **`/frontend/public/manifest.json`** - Configuración de la PWA
2. **`/frontend/public/sw.js`** - Service Worker (caché y offline)
3. **`/frontend/src/components/InstallPrompt.vue`** - Botón de instalación
4. **Modificados:**
   - `index.html` - Meta tags PWA
   - `main.js` - Registro del Service Worker
   - `App.vue` - Componente de instalación

---

## 🧪 Cómo Probar en Local

### **Opción 1: Chrome/Edge (Desktop)**

1. **Abre la app en local:**
   ```
   http://localhost:5175
   ```

2. **Abre DevTools (F12)**
   - Ve a la pestaña **"Application"**
   - En el menú izquierdo, busca **"Manifest"**
   - Deberías ver: `FurbitoBET` con el logo

3. **Verifica el Service Worker:**
   - En **"Application" → "Service Workers"**
   - Deberías ver: `sw.js` con estado "activated"

4. **Instalar la app:**
   - En la barra de direcciones, aparecerá un icono de **"Instalar"** (➕ o 💻)
   - O haz clic en el botón flotante **"📱 Instalar App"** que aparece abajo a la derecha
   - Haz clic y confirma

5. **Resultado:**
   - Se abrirá una ventana nueva sin barra de navegador
   - Aparecerá un icono en tu escritorio/menú de aplicaciones

---

### **Opción 2: Chrome Android (Móvil)**

1. **Abre Chrome en tu móvil**

2. **Navega a:**
   ```
   http://TU_IP_LOCAL:5175
   ```
   (Ejemplo: `http://192.168.1.100:5175`)
   
   **Para saber tu IP local:**
   ```bash
   # En Linux/Mac:
   ip addr show | grep inet
   
   # O simplemente:
   hostname -I
   ```

3. **Instalar:**
   - Aparecerá un banner abajo: **"Añadir FurbitoBET a la pantalla de inicio"**
   - O toca el menú (⋮) → **"Añadir a pantalla de inicio"**
   - O usa el botón flotante **"📱 Instalar App"**

4. **Resultado:**
   - Icono en tu pantalla de inicio
   - Se abre como app nativa (pantalla completa)

---

### **Opción 3: Safari iOS (iPhone/iPad)**

**⚠️ Nota:** iOS no soporta el botón de instalación automático, pero sí PWAs.

1. **Abre Safari** (no Chrome)

2. **Navega a tu IP local:**
   ```
   http://TU_IP:5175
   ```

3. **Instalar:**
   - Toca el botón de **Compartir** (📤)
   - Selecciona **"Añadir a pantalla de inicio"**
   - Confirma

4. **Resultado:**
   - Icono en tu pantalla de inicio
   - Se abre como app

---

## 🔍 Verificar que Funciona

### **Test 1: Service Worker**
1. Abre la consola del navegador (F12)
2. Deberías ver: `✅ Service Worker registered: http://localhost:5175/`

### **Test 2: Manifest**
1. DevTools → Application → Manifest
2. Verifica:
   - **Name:** FurbitoBET
   - **Short name:** Furbito
   - **Theme color:** #10b981 (verde)
   - **Icons:** logo.jpg

### **Test 3: Instalabilidad**
1. DevTools → Application → Manifest
2. Abajo verás: **"Add to home screen"** o **"Install"**
3. Si dice "⚠️ No matching service worker detected", refresca la página

### **Test 4: Offline (Opcional)**
1. Instala la app
2. Abre DevTools → Network
3. Marca **"Offline"**
4. Refresca la app
5. Debería cargar la última versión cacheada (aunque sin datos del servidor)

---

## 📦 Desplegar en Producción

### **Vercel (tu actual hosting)**

1. **Sube los cambios a GitHub:**
   ```bash
   git add .
   git commit -m "feat: add PWA support"
   git push origin main
   ```

2. **Vercel detectará automáticamente:**
   - El `manifest.json` en `/public`
   - El `sw.js` en `/public`
   - Los meta tags en `index.html`

3. **Después del deploy:**
   - Ve a `https://furbitobet.vercel.app`
   - Espera 1-2 minutos
   - Deberías ver el botón de instalación

4. **Verificar en producción:**
   - Chrome → DevTools → Lighthouse
   - Run audit → **"Progressive Web App"**
   - Deberías obtener 90-100 puntos

---

## 🎯 Qué Esperar

### **Experiencia de Usuario:**

1. **Primera visita:**
   - Usuario entra a la web
   - Después de 3-5 segundos, aparece el botón flotante **"📱 Instalar App"**

2. **Instalación:**
   - Usuario hace clic en "Instalar"
   - Se descarga (instantáneo, solo ~2MB)
   - Aparece icono en el móvil/escritorio

3. **Uso posterior:**
   - Usuario abre la app desde el icono
   - Se ve como app nativa (sin barra de navegador)
   - Funciona exactamente igual que la web
   - **Sigue necesitando internet** para apuestas en vivo

---

## 🐛 Solución de Problemas

### **No aparece el botón de instalación:**
- Verifica que estés en **HTTPS** (en producción)
- En local, `localhost` está permitido
- Refresca la página (Ctrl+Shift+R)
- Verifica la consola por errores

### **Service Worker no se registra:**
- Verifica que `/public/sw.js` existe
- Mira la consola por errores
- Intenta en modo incógnito

### **En iOS no funciona:**
- iOS requiere Safari (no Chrome)
- No hay botón automático, usa el menú de compartir

---

## 📊 Estadísticas PWA

Una vez desplegado, puedes ver en **Google Analytics** (si lo configuras):
- Cuántos usuarios instalan la app
- Cuántos la usan vs la web
- Tiempo de carga mejorado

---

## ✨ Próximos Pasos (Opcional)

Si quieres mejorar aún más la PWA:

1. **Notificaciones Push:**
   - Requiere configurar Firebase Cloud Messaging
   - Notificar nuevos eventos, resultados, etc.

2. **Modo Offline Avanzado:**
   - Guardar apuestas pendientes localmente
   - Sincronizar cuando vuelva la conexión

3. **Iconos Personalizados:**
   - Crear iconos de 192x192 y 512x512 específicos
   - Actualmente usa `logo.jpg`

---

**¿Listo para probar?** Abre `http://localhost:5175` y mira la consola 🚀
