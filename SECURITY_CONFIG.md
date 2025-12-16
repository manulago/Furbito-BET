# 🔒 CONFIGURACIÓN DE SEGURIDAD - FurbitoBET

## ⚠️ IMPORTANTE: Configuración Obligatoria para Producción

### 1. JWT Secret Key

**CRÍTICO:** El JWT secret DEBE ser configurado mediante variable de entorno en producción.

#### Generar un Secret Seguro:
```bash
# Opción 1: OpenSSL (Recomendado)
openssl rand -base64 64

# Opción 2: Node.js
node -e "console.log(require('crypto').randomBytes(64).toString('base64'))"

# Opción 3: Python
python3 -c "import secrets; print(secrets.token_urlsafe(64))"
```

#### Configurar en Producción:

**Render.com / Heroku / Railway:**
```bash
JWT_SECRET=<tu_secret_generado_aqui>
```

**Docker:**
```bash
docker run -e JWT_SECRET=<tu_secret> ...
```

**Archivo .env (NO COMMITEAR):**
```env
JWT_SECRET=<tu_secret_generado>
JWT_EXPIRATION=36000000
```

### 2. CORS Configuration

**CRÍTICO:** Actualizar CORS para permitir solo tu dominio en producción.

#### Opción A: Variable de Entorno (Recomendado)
```properties
# application.properties
allowed.origins=${ALLOWED_ORIGINS:https://furbitobet.vercel.app}
```

#### Opción B: Hardcodear solo en producción
Cambiar en TODOS los controladores:
```java
// ANTES (INSEGURO):
@CrossOrigin(origins = "*")

// DESPUÉS (SEGURO):
@CrossOrigin(origins = "https://furbitobet.vercel.app")
```

### 3. Database Credentials

Asegúrate de que las credenciales de base de datos estén en variables de entorno:
```bash
DB_URL=jdbc:postgresql://tu-servidor:5432/furbitobet
DB_USER=tu_usuario
DB_PASSWORD=tu_password_seguro
```

### 4. Email API Key

```bash
BREVO_API_KEY=tu_api_key_real
```

### 5. Admin Password

```bash
ADMIN_PASSWORD=<contraseña_fuerte_admin>
```

---

## 🛡️ Checklist de Seguridad para Producción

- [ ] JWT_SECRET configurado con valor aleatorio fuerte (64+ caracteres)
- [ ] CORS configurado solo para tu dominio (no "*")
- [ ] DB_PASSWORD es fuerte y único
- [ ] BREVO_API_KEY configurado
- [ ] ADMIN_PASSWORD es fuerte (8+ chars, mayúsculas, minúsculas, números, símbolos)
- [ ] HTTPS habilitado en el servidor
- [ ] Firewall configurado para permitir solo puertos necesarios
- [ ] Logs de seguridad activados
- [ ] Backups automáticos de base de datos configurados

---

## 🔐 Recomendaciones Adicionales

### Rate Limiting
Considera implementar rate limiting para:
- `/api/auth/login` - 5 intentos por minuto
- `/api/auth/register` - 3 intentos por hora
- `/api/auth/forgot-password` - 3 intentos por hora
- `/api/rewards/spin` - Ya implementado (12 horas)

### Monitoring
Monitorear:
- Intentos de login fallidos
- Cambios de contraseña
- Apuestas grandes (>100€)
- Cancelaciones de apuestas
- Accesos admin

### Backups
- Base de datos: Diario
- Configuración: Semanal
- Logs: Mensual

---

## 🚨 En Caso de Compromiso de Seguridad

1. **Cambiar inmediatamente:**
   - JWT_SECRET
   - DB_PASSWORD
   - ADMIN_PASSWORD
   - BREVO_API_KEY

2. **Invalidar todas las sesiones:**
   - Cambiar JWT_SECRET invalida todos los tokens existentes
   - Usuarios deberán hacer login nuevamente

3. **Revisar logs:**
   - Buscar actividad sospechosa
   - Identificar cuentas comprometidas

4. **Notificar a usuarios afectados**

---

## 📞 Contacto de Seguridad

Si encuentras una vulnerabilidad de seguridad, por favor repórtala de forma responsable a:
- Email: security@furbitobet.com (ejemplo)
- No publicar vulnerabilidades públicamente hasta que sean corregidas

---

**Última actualización:** 16/12/2025  
**Versión de Seguridad:** 4.0
