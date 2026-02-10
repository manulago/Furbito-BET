# Guía para Publicar FurbitoBET en Samsung Galaxy Store

## 📱 Requisitos Previos

### 1. Cuenta de Desarrollador Samsung
- Regístrate en [Samsung Seller Portal](https://seller.samsungapps.com/)
- Costo: **GRATIS** (a diferencia de Google Play que cobra $25)
- Necesitas un correo electrónico válido y verificar tu identidad

### 2. Assets Necesarios

#### Iconos (PNG, fondo transparente recomendado)
Crear los siguientes iconos en `/frontend/public/icons/`:
- `icon-48x48.png`
- `icon-72x72.png`
- `icon-96x96.png`
- `icon-144x144.png`
- `icon-192x192.png`
- `icon-512x512.png`
- `maskable-192x192.png` (con área segura para recorte circular)
- `maskable-512x512.png` (con área segura para recorte circular)

#### Screenshots (PNG, 1080x1920 recomendado)
Crear capturas en `/frontend/public/screenshots/`:
- `home.png` - Pantalla principal
- `bets.png` - Pantalla de apuestas
- Al menos 2 screenshots son requeridos

#### Icono para Galaxy Store
- **512x512 PNG** (obligatorio para la tienda)
- **1024x500 PNG** para banner promocional (opcional pero recomendado)

---

## 🚀 Opción 1: PWA Builder (Más Fácil)

### Paso 1: Desplegar la PWA
Asegúrate de que tu PWA esté desplegada en una URL HTTPS:
```bash
# Si usas Vercel (ya configurado en tu proyecto)
cd frontend
npm run build
# Despliega a Vercel o tu hosting preferido
```

### Paso 2: Usar PWA Builder
1. Ve a [PWABuilder.com](https://www.pwabuilder.com/)
2. Ingresa la URL de tu PWA desplegada
3. PWA Builder analizará tu manifest.json
4. Haz clic en "Package for stores"
5. Selecciona "Samsung Galaxy Store"
6. Descarga el paquete generado (archivo `.apk`)

### Paso 3: Subir a Galaxy Store
1. Inicia sesión en [Samsung Seller Portal](https://seller.samsungapps.com/)
2. Clic en "Add New App" → "Android"
3. Sube el APK generado
4. Completa la información requerida (ver sección Metadata)

---

## 🔧 Opción 2: Bubblewrap/TWA (Más Control)

### Instalación de Bubblewrap
```bash
npm install -g @anthropic/bubblewrap-cli
```

### Generar proyecto TWA
```bash
cd /home/manulago/Escritorio/FurbitoBET
bubblewrap init --manifest https://TU-URL.com/manifest.json
```

### Configuración (responder a las preguntas):
- **App name:** FurbitoBET
- **Short name:** Furbito
- **Package ID:** com.furbitobet.app
- **Key store:** Crear nueva o usar existente
- **Launcher name:** Furbito

### Compilar APK
```bash
bubblewrap build
```

El APK estará en `app-release-signed.apk`

---

## 📝 Metadata para Galaxy Store

### Información Básica
| Campo | Valor |
|-------|-------|
| App Name | FurbitoBET |
| Category | Sports / Entertainment |
| Content Rating | 18+ (por ser de apuestas) |
| Default Language | Spanish |

### Descripción Corta (80 caracteres)
```
Pronósticos deportivos entre amigos - ¡Sin dinero real!
```

### Descripción Larga (4000 caracteres máx)
```
🎮 ENTRETENIMIENTO 100% GRATUITO - SIN DINERO REAL 🎮

FurbitoBET es una app social para hacer pronósticos deportivos entre amigos del Furbito FIC. ¡Compite por diversión y demuestra quién sabe más de fútbol!

⚠️ IMPORTANTE: Esta aplicación NO involucra dinero real ni apuestas reales. Es únicamente para entretenimiento y diversión entre amigos.

🏆 CARACTERÍSTICAS PRINCIPALES:
• Haz pronósticos en los partidos del Furbito
• Sistema de puntos y ranking entre amigos
• Estadísticas detalladas de tus aciertos
• Ruleta de premios virtuales
• Comparte resultados con tu grupo

🎯 CÓMO FUNCIONA:
1. Regístrate gratis con tu cuenta
2. Consulta los próximos partidos
3. Haz tus pronósticos
4. ¡Gana puntos virtuales y sube en el ranking!

📱 DISEÑO MODERNO:
• Interfaz intuitiva y fácil de usar
• Modo oscuro elegante
• Optimizado para dispositivos Samsung Galaxy

👥 PERFECTO PARA:
• Grupos de amigos
• Ligas amateur
• Competencias amistosas
• Diversión sin riesgos

✅ 100% GRATIS
✅ SIN COMPRAS IN-APP
✅ SIN DINERO REAL
✅ SOLO DIVERSIÓN
```

### Palabras Clave
```
apuestas, deportes, furbito, fic, amigos, ranking, estadisticas
```

---

## ⚠️ Requisitos Legales para Apps de Apuestas

Samsung tiene políticas estrictas para apps de gambling/apuestas:

### Si es apuestas con dinero real:
- Necesitas licencias de gambling válidas
- Restricciones geográficas obligatorias
- Verificación de edad (+18)
- Documentación legal extensa

### Si es apuestas simuladas (sin dinero real):
- Declarar claramente que es "para entretenimiento"
- Incluir aviso de que no hay dinero real involucrado
- Marcar como "Simulated Gambling" en la categoría

**Recomendación:** Si FurbitoBET es solo entre amigos sin dinero real, asegúrate de:
1. Incluirlo claramente en la descripción
2. Añadir un disclaimer en la app
3. Seleccionar categoría "Entertainment" en lugar de "Gambling"

---

## 📋 Checklist Final

- [ ] Cuenta de Samsung Seller creada y verificada
- [ ] PWA desplegada en HTTPS
- [ ] Iconos en todos los tamaños requeridos
- [ ] Al menos 2 screenshots (1080x1920)
- [ ] Descripción en español completada
- [ ] Política de privacidad URL
- [ ] APK generado con PWABuilder o Bubblewrap
- [ ] Rating de contenido completado
- [ ] Declaración sobre apuestas/gambling

---

## 🔗 Enlaces Útiles

- [Samsung Seller Portal](https://seller.samsungapps.com/)
- [PWA Builder](https://www.pwabuilder.com/)
- [Samsung Galaxy Store Policies](https://developer.samsung.com/galaxy-store/guidelines.html)
- [Bubblewrap Documentation](https://github.com/nicholasbraun/nicholasbraun-nicholasbraun-nicholasbraun-nicholasbraun/nicholasbraun-nicholasbraun-nicholasbraun-nicholasbraun-nicholasbraun-nicholasbraun-nicholasbraun-nicholasbraun-nicholasbraun)

---

## 📞 Soporte

Si tienes problemas durante el proceso:
- [Samsung Developer Forum](https://forum.developer.samsung.com/)
- [Galaxy Store Help](https://seller.samsungapps.com/help/)

---

*Última actualización: Enero 2026*
