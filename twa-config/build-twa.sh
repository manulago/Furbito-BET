#!/bin/bash

# ===========================================
# FurbitoBET - TWA Build Script
# Para Samsung Galaxy Store
# ===========================================

set -e

echo "🎭 FurbitoBET TWA Builder"
echo "========================="
echo ""

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Directorio del proyecto
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TWA_DIR="$PROJECT_DIR/twa-build"

# Verificar requisitos
check_requirements() {
    echo "📋 Verificando requisitos..."
    
    # Node.js
    if ! command -v node &> /dev/null; then
        echo -e "${RED}❌ Node.js no está instalado${NC}"
        exit 1
    fi
    echo -e "${GREEN}✅ Node.js $(node -v)${NC}"
    
    # NPM
    if ! command -v npm &> /dev/null; then
        echo -e "${RED}❌ NPM no está instalado${NC}"
        exit 1
    fi
    echo -e "${GREEN}✅ NPM $(npm -v)${NC}"
    
    # Java (necesario para firmar el APK)
    if ! command -v java &> /dev/null; then
        echo -e "${YELLOW}⚠️  Java no está instalado (necesario para firmar APK)${NC}"
    else
        echo -e "${GREEN}✅ Java instalado${NC}"
    fi
    
    echo ""
}

# Instalar Bubblewrap si no está
install_bubblewrap() {
    if ! command -v bubblewrap &> /dev/null; then
        echo "📦 Instalando Bubblewrap CLI..."
        npm install -g @nicholasbraun/nicholasbraun-nicholasbraun-nicholasbraun@nicholasbraun 2>/dev/null || npm install -g @nicholasbraun/nicholasbraun 2>/dev/null || {
            echo -e "${YELLOW}⚠️  Instalando bubblewrap desde npm...${NC}"
            npm install -g nicholasbraun 2>/dev/null || true
        }
    fi
}

# Crear directorio TWA
setup_twa_dir() {
    echo "📁 Configurando directorio TWA..."
    mkdir -p "$TWA_DIR"
    cd "$TWA_DIR"
}

# Generar keystore si no existe
generate_keystore() {
    KEYSTORE_PATH="$TWA_DIR/android.keystore"
    
    if [ ! -f "$KEYSTORE_PATH" ]; then
        echo ""
        echo "🔐 Generando keystore para firmar el APK..."
        echo -e "${YELLOW}IMPORTANTE: Guarda la contraseña que uses!${NC}"
        echo ""
        
        keytool -genkeypair \
            -alias furbitobet \
            -keyalg RSA \
            -keysize 2048 \
            -validity 10000 \
            -keystore "$KEYSTORE_PATH" \
            -storepass furbitobet123 \
            -keypass furbitobet123 \
            -dname "CN=FurbitoBET, OU=Development, O=FurbitoBET, L=Coruna, ST=Galicia, C=ES"
        
        echo -e "${GREEN}✅ Keystore generado en: $KEYSTORE_PATH${NC}"
        echo ""
        
        # Obtener SHA256 fingerprint
        echo "📋 SHA256 Fingerprint para assetlinks.json:"
        keytool -list -v -keystore "$KEYSTORE_PATH" -alias furbitobet -storepass furbitobet123 | grep "SHA256:"
        echo ""
    else
        echo -e "${GREEN}✅ Keystore ya existe${NC}"
    fi
}

# Mostrar instrucciones para PWABuilder
show_pwabuilder_instructions() {
    echo ""
    echo "=========================================="
    echo "🚀 OPCIÓN RECOMENDADA: PWABuilder"
    echo "=========================================="
    echo ""
    echo "1. Ve a: https://www.pwabuilder.com/"
    echo ""
    echo "2. Ingresa la URL de tu PWA:"
    echo -e "   ${GREEN}https://furbitobet.vercel.app${NC}"
    echo ""
    echo "3. Haz clic en 'Start' y espera el análisis"
    echo ""
    echo "4. Selecciona 'Package for stores'"
    echo ""
    echo "5. Elige 'Android' (TWA)"
    echo ""
    echo "6. Configura:"
    echo "   - Package ID: com.furbitobet.twa"
    echo "   - App name: FurbitoBET"
    echo "   - Version: 1.0.0"
    echo ""
    echo "7. Descarga el APK/AAB generado"
    echo ""
    echo "=========================================="
}

# Mostrar instrucciones para Galaxy Store
show_galaxy_store_instructions() {
    echo ""
    echo "=========================================="
    echo "📱 SUBIR A SAMSUNG GALAXY STORE"
    echo "=========================================="
    echo ""
    echo "1. Crear cuenta en Samsung Seller Portal:"
    echo -e "   ${GREEN}https://seller.samsungapps.com/${NC}"
    echo ""
    echo "2. Verificar tu identidad (gratis)"
    echo ""
    echo "3. Clic en 'Add New App' → 'Android'"
    echo ""
    echo "4. Subir el APK/AAB generado"
    echo ""
    echo "5. Completar información:"
    echo "   - App Name: FurbitoBET"
    echo "   - Category: Entertainment → Sports"
    echo "   - Content Rating: 18+ (apuestas simuladas)"
    echo "   - Descripción: (ver descripción en GALAXY_STORE_GUIDE.md)"
    echo ""
    echo "6. Subir screenshots (mínimo 2):"
    echo "   - Resolución: 1080x1920 o 1440x2560"
    echo "   - Formato: PNG o JPG"
    echo ""
    echo "7. Subir iconos:"
    echo "   - 512x512 PNG para la tienda"
    echo ""
    echo "8. Enviar para revisión"
    echo ""
    echo "=========================================="
}

# Mostrar estado de archivos
show_files_status() {
    echo ""
    echo "📁 Estado de archivos:"
    echo ""
    
    # Iconos
    ICONS_DIR="$PROJECT_DIR/frontend/public/icons"
    if [ -d "$ICONS_DIR" ] && [ "$(ls -A $ICONS_DIR 2>/dev/null)" ]; then
        echo -e "${GREEN}✅ Iconos generados en: frontend/public/icons/${NC}"
        ls -1 "$ICONS_DIR" | head -5
        echo "   ..."
    else
        echo -e "${RED}❌ Iconos no encontrados${NC}"
    fi
    echo ""
    
    # Screenshots
    SCREENSHOTS_DIR="$PROJECT_DIR/frontend/public/screenshots"
    if [ -d "$SCREENSHOTS_DIR" ] && [ "$(ls -A $SCREENSHOTS_DIR 2>/dev/null)" ]; then
        echo -e "${GREEN}✅ Screenshots en: frontend/public/screenshots/${NC}"
        ls -1 "$SCREENSHOTS_DIR"
    else
        echo -e "${YELLOW}⚠️  Screenshots no encontrados (se generaron placeholders)${NC}"
    fi
    echo ""
    
    # AssetLinks
    ASSETLINKS="$PROJECT_DIR/frontend/public/.well-known/assetlinks.json"
    if [ -f "$ASSETLINKS" ]; then
        echo -e "${GREEN}✅ assetlinks.json configurado${NC}"
        echo -e "${YELLOW}   ⚠️  Recuerda actualizar el SHA256 fingerprint!${NC}"
    else
        echo -e "${RED}❌ assetlinks.json no encontrado${NC}"
    fi
    echo ""
}

# Main
main() {
    check_requirements
    show_files_status
    show_pwabuilder_instructions
    show_galaxy_store_instructions
    
    echo ""
    echo "=========================================="
    echo "📋 RESUMEN DE PASOS"
    echo "=========================================="
    echo ""
    echo "1. Despliega la PWA a producción (Vercel)"
    echo "2. Ve a PWABuilder.com y genera el APK"
    echo "3. Crea cuenta en Samsung Seller Portal"
    echo "4. Sube el APK con la información requerida"
    echo "5. Espera la aprobación (1-5 días)"
    echo ""
    echo -e "${GREEN}¡Buena suerte! 🎉${NC}"
}

main "$@"
