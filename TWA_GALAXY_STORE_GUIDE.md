# 🎭 FurbitoBET - Guía TWA para Samsung Galaxy Store

## 📋 Resumen

Esta guía explica cómo empaquetar FurbitoBET como TWA (Trusted Web Activity) y subirla a Samsung Galaxy Store.

---

## ✅ Checklist de Archivos (YA GENERADOS)

| Archivo | Estado | Ubicación |
|---------|--------|-----------|
| Iconos 48-512px | ✅ | `frontend/public/icons/` |
| Iconos maskable | ✅ | `frontend/public/icons/maskable-*.png` |
| Screenshots | ✅ | `frontend/public/screenshots/` |
| manifest.json | ✅ | `frontend/public/manifest.json` |
| assetlinks.json | ⚠️ Pendiente SHA256 | `frontend/public/.well-known/assetlinks.json` |
| twa-manifest.json | ✅ | `twa-config/twa-manifest.json` |

---

## 🚀 PASO 1: Desplegar la PWA

Asegúrate de que la PWA esté desplegada en HTTPS:

```bash
cd frontend
npm run build
# Despliega a Vercel (ya configurado)
```

URL de producción: **https://furbitobet.vercel.app**

---

## 🔧 PASO 2: Generar APK con PWABuilder (RECOMENDADO)

### 2.1 Ir a PWABuilder
1. Abre: **https://www.pwabuilder.com/**
2. Ingresa: `https://furbitobet.vercel.app`
3. Clic en **"Start"**

### 2.2 Revisar puntuación
PWABuilder analizará tu PWA y mostrará una puntuación. Debería ser alta gracias a:
- ✅ Manifest completo
- ✅ Service Worker
- ✅ HTTPS
- ✅ Iconos en múltiples tamaños

### 2.3 Empaquetar para Android
1. Clic en **"Package for stores"**
2. Selecciona **"Android"**
3. Configura:

| Campo | Valor |
|-------|-------|
| Package ID | `com.furbitobet.twa` |
| App name | `FurbitoBET` |
| Short name | `Furbito` |
| App version | `1.0.0` |
| Version code | `1` |
| Host | `furbitobet.vercel.app` |
| Start URL | `/` |
| Theme color | `#111827` |
| Background color | `#111827` |
| Display mode | `standalone` |
| Orientation | `portrait` |

### 2.4 Opciones de firma
- **Opción A**: Dejar que PWABuilder genere una clave (más fácil)
- **Opción B**: Usar tu propia keystore (más control)

### 2.5 Descargar
Descarga el archivo `.apk` o `.aab` (Android App Bundle).

---

## 🔐 PASO 3: Configurar Digital Asset Links

### 3.1 Obtener SHA256 Fingerprint
Después de generar el APK, necesitas el fingerprint:

```bash
# Si usaste PWABuilder, lo encontrarás en el ZIP descargado
# O si tienes el keystore:
keytool -list -v -keystore tu-keystore.jks -alias tu-alias
```

### 3.2 Actualizar assetlinks.json
Edita `frontend/public/.well-known/assetlinks.json`:

```json
[
  {
    "relation": ["delegate_permission/common.handle_all_urls"],
    "target": {
      "namespace": "android_app",
      "package_name": "com.furbitobet.twa",
      "sha256_cert_fingerprints": [
        "TU_SHA256_FINGERPRINT_AQUI"
      ]
    }
  }
]
```

### 3.3 Redesplegar
```bash
cd frontend
npm run build
# Despliega de nuevo a Vercel
```

### 3.4 Verificar
Comprueba que el archivo es accesible:
```
https://furbitobet.vercel.app/.well-known/assetlinks.json
```

---

## 📱 PASO 4: Subir a Samsung Galaxy Store

### 4.1 Crear cuenta (GRATIS)
1. Ve a: **https://seller.samsungapps.com/**
2. Regístrate con tu email
3. Verifica tu identidad

### 4.2 Crear nueva app
1. Clic en **"Add New App"**
2. Selecciona **"Android"**

### 4.3 Información básica

| Campo | Valor |
|-------|-------|
| App Name | FurbitoBET |
| Default Language | Spanish |
| Category | Entertainment > Sports |
| Content Rating | 18+ |

### 4.4 Descripción corta (80 caracteres)
```
Pronósticos deportivos entre amigos - ¡Sin dinero real!
```

### 4.5 Descripción completa
```
🎮 ENTRETENIMIENTO 100% GRATUITO - SIN DINERO REAL 🎮

FurbitoBET es una app social para hacer pronósticos deportivos entre amigos del Furbito FIC. ¡Compite por diversión y demuestra quién sabe más de fútbol!

⚠️ IMPORTANTE: Esta aplicación NO involucra dinero real ni apuestas reales. Es únicamente para entretenimiento y diversión entre amigos.

🏆 CARACTERÍSTICAS:
• Haz pronósticos en los partidos del Furbito
• Sistema de puntos y ranking entre amigos
• Estadísticas detalladas de tus aciertos
• Comparte resultados con tu grupo
• Temas especiales (Navidad, Carnaval)

🎯 CÓMO FUNCIONA:
1. Regístrate gratis
2. Consulta los próximos partidos
3. Haz tus pronósticos
4. ¡Gana puntos y sube en el ranking!

📱 DISEÑO MODERNO:
• Interfaz intuitiva y fácil de usar
• Modo oscuro elegante
• Optimizado para Samsung Galaxy

✅ 100% GRATIS
✅ SIN COMPRAS IN-APP
✅ SIN DINERO REAL
✅ SOLO DIVERSIÓN
```

### 4.6 Subir APK/AAB
- Arrastra el archivo APK o AAB generado

### 4.7 Subir assets

**Screenshots** (mínimo 2):
- `frontend/public/screenshots/home.png`
- `frontend/public/screenshots/bets.png`
- Resolución: 1080x1920 o mayor

**Icono de la tienda**:
- `frontend/public/icons/icon-512x512.png`

### 4.8 Política de privacidad
Necesitas una URL con tu política de privacidad. Puedes:
- Crear una página en tu web
- Usar un servicio como Termly o PrivacyPolicies.com

### 4.9 Enviar para revisión
1. Revisa toda la información
2. Clic en **"Submit"**
3. Espera 1-5 días para la revisión

---

## ⚠️ Consideraciones legales

### Apuestas simuladas
Tu app NO involucra dinero real, así que:
- ✅ Categoría: Entertainment (no Gambling)
- ✅ No necesitas licencias de gambling
- ✅ Incluye disclaimer claro

### Disclaimer recomendado
Añade en la descripción y en la app:
```
Esta aplicación es solo para entretenimiento. 
No se apuesta dinero real. 
Los puntos son virtuales y no tienen valor monetario.
```

---

## 🛠️ Solución de problemas

### Error: "Digital Asset Links validation failed"
- Verifica que `assetlinks.json` sea accesible
- Comprueba que el SHA256 fingerprint es correcto
- El archivo debe servirse con `Content-Type: application/json`

### Error: "Package name already exists"
- Cambia el package ID a algo único
- Ejemplo: `com.furbitobet.app.2026`

### La app no abre en modo fullscreen
- Verifica el Digital Asset Links
- Puede tardar hasta 24h en propagarse

---

## 📚 Recursos útiles

- [Samsung Seller Portal](https://seller.samsungapps.com/)
- [PWABuilder](https://www.pwabuilder.com/)
- [Digital Asset Links Validator](https://developers.google.com/digital-asset-links/tools/generator)
- [Samsung Galaxy Store Guidelines](https://developer.samsung.com/galaxy-store/guidelines.html)

---

## 🎉 ¡Listo!

Una vez aprobada, tu app estará disponible en Samsung Galaxy Store para millones de usuarios de Samsung.

**Tiempo estimado total**: 2-7 días (incluyendo revisión)

---

*Última actualización: Febrero 2026*
