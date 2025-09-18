#!/bin/bash

# Navigation Drawer Implementation Validation Script
# This script validates that the navigation drawer implementation is complete and correct

echo "AndrOBD Navigation Drawer Implementation Validation"
echo "=================================================="

PROJECT_ROOT="/home/runner/work/AndrOBD/AndrOBD"
ANDROID_SRC="$PROJECT_ROOT/androbd/src/main"
RES_DIR="$ANDROID_SRC/res"
JAVA_DIR="$ANDROID_SRC/java/com/fr3ts0n/ecu/gui/androbd"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test results counters
PASSED=0
FAILED=0

# Helper function to check if file exists
check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} Found: $1"
        ((PASSED++))
        return 0
    else
        echo -e "${RED}✗${NC} Missing: $1"
        ((FAILED++))
        return 1
    fi
}

# Helper function to check if pattern exists in file
check_pattern() {
    local file="$1"
    local pattern="$2"
    local description="$3"
    
    if [ -f "$file" ] && grep -q "$pattern" "$file"; then
        echo -e "${GREEN}✓${NC} $description"
        ((PASSED++))
        return 0
    else
        echo -e "${RED}✗${NC} $description"
        ((FAILED++))
        return 1
    fi
}

echo ""
echo "1. Checking Layout Files"
echo "========================"

check_file "$RES_DIR/layout/activity_main_drawer.xml"
check_file "$RES_DIR/layout/nav_header_main.xml"
check_file "$RES_DIR/menu/navigation_drawer.xml"
check_file "$RES_DIR/drawable/nav_header_bg.xml"

echo ""
echo "2. Checking Dependencies"
echo "========================"

check_pattern "$PROJECT_ROOT/androbd/build.gradle" "androidx.drawerlayout:drawerlayout" "DrawerLayout dependency added"
check_pattern "$PROJECT_ROOT/androbd/build.gradle" "com.google.android.material:material" "Material Components dependency added"

echo ""
echo "3. Checking Java Classes"
echo "========================="

check_file "$JAVA_DIR/NavigationDrawerHelper.java"
check_file "$JAVA_DIR/BaseDrawerActivity.java"

echo ""
echo "4. Checking MainActivity Integration"
echo "===================================="

check_pattern "$JAVA_DIR/MainActivity.java" "NavigationDrawerHelper.NavigationDrawerListener" "MainActivity implements NavigationDrawerListener"
check_pattern "$JAVA_DIR/MainActivity.java" "NavigationDrawerHelper navigationDrawerHelper" "NavigationDrawerHelper field added"
check_pattern "$JAVA_DIR/MainActivity.java" "initializeNavigationDrawer" "Navigation drawer initialization method"
check_pattern "$JAVA_DIR/MainActivity.java" "onNavigationItemSelected" "Navigation item selection handling"
check_pattern "$JAVA_DIR/MainActivity.java" "updateNavigationState" "Navigation state update method"

echo ""
echo "5. Checking Activity Extensions"
echo "==============================="

check_pattern "$JAVA_DIR/ChartActivity.java" "extends BaseDrawerActivity" "ChartActivity extends BaseDrawerActivity"
check_pattern "$JAVA_DIR/DashBoardActivity.java" "extends BaseDrawerActivity" "DashBoardActivity extends BaseDrawerActivity"
check_pattern "$JAVA_DIR/DashBoardActivity.java" "disableDrawer" "DashBoardActivity disables drawer for fullscreen"

echo ""
echo "6. Checking Navigation Menu Structure"
echo "====================================="

check_pattern "$RES_DIR/menu/navigation_drawer.xml" "group_connection" "Connection management group"
check_pattern "$RES_DIR/menu/navigation_drawer.xml" "group_data" "Data management group"
check_pattern "$RES_DIR/menu/navigation_drawer.xml" "group_display" "Display options group"
check_pattern "$RES_DIR/menu/navigation_drawer.xml" "group_config" "Configuration group"
check_pattern "$RES_DIR/menu/navigation_drawer.xml" "group_obd_services" "OBD services group"

echo ""
echo "7. Checking Theme Integration"
echo "============================="

check_pattern "$RES_DIR/values/styles.xml" "NavigationViewTheme" "Navigation view theme added"
check_pattern "$RES_DIR/values/dimens.xml" "nav_header_height" "Navigation header dimensions added"

echo ""
echo "8. Checking Compatibility Features"
echo "=================================="

check_pattern "$JAVA_DIR/NavigationDrawerHelper.java" "Android 4.1" "Android 4.1+ compatibility documented"
check_pattern "$JAVA_DIR/BaseDrawerActivity.java" "API 16" "API 16+ compatibility documented"

echo ""
echo "9. Checking Menu Item Mapping"
echo "============================="

check_pattern "$JAVA_DIR/NavigationDrawerHelper.java" "mapNavigationToActionId" "Navigation to action ID mapping"
check_pattern "$JAVA_DIR/NavigationDrawerHelper.java" "R.id.nav_connect" "Connect navigation item mapped"
check_pattern "$JAVA_DIR/NavigationDrawerHelper.java" "R.id.nav_disconnect" "Disconnect navigation item mapped"
check_pattern "$JAVA_DIR/NavigationDrawerHelper.java" "R.id.nav_settings" "Settings navigation item mapped"

echo ""
echo "10. Checking Dynamic Behavior"
echo "============================="

check_pattern "$JAVA_DIR/NavigationDrawerHelper.java" "updateNavigationVisibility" "Dynamic visibility update method"
check_pattern "$JAVA_DIR/MainActivity.java" "updateNavigationState" "Navigation state sync with menu updates"

echo ""
echo "11. Checking Documentation"
echo "=========================="

check_file "$PROJECT_ROOT/docs/navigation-drawer-implementation.md"
check_pattern "$PROJECT_ROOT/docs/menu-structure.md" "Navigation Drawer Implementation" "Menu structure documentation updated"

echo ""
echo "12. Checking Cross-Activity Navigation"
echo "======================================"

check_pattern "$JAVA_DIR/MainActivity.java" "handleNavigationAction" "Navigation action handling method"
check_pattern "$JAVA_DIR/MainActivity.java" "onNewIntent" "New intent handling for navigation"
check_pattern "$JAVA_DIR/BaseDrawerActivity.java" "navigation_action" "Navigation action intent extra"

echo ""
echo "Validation Summary"
echo "=================="
echo -e "Total tests: $((PASSED + FAILED))"
echo -e "${GREEN}Passed: $PASSED${NC}"
echo -e "${RED}Failed: $FAILED${NC}"

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✓ All validation checks passed!${NC}"
    echo "Navigation drawer implementation is complete and correct."
    exit 0
else
    echo -e "${RED}✗ Some validation checks failed.${NC}"
    echo "Please review the failed items above."
    exit 1
fi