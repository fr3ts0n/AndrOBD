# AndrOBD Menu Structure Documentation

## Overview

The AndrOBD application provides a comprehensive menu interface that gives users direct access to all key features from the main activity. The menu structure is designed for maximum usability and follows Android design guidelines for consistency and accessibility.

## Menu Compatibility

- **Target Android Version**: Compatible with Android 4.1+ (API Level 16+)
- **Menu Components**: Uses standard Android menu components (`Menu`, `MenuItem`) for maximum compatibility
- **Icons**: Utilizes Android's built-in drawable resources for consistency across devices
- **Theme Integration**: Automatically adapts to app's light/dark theme settings

## Main Menu Structure

The main menu (`R.menu.main`) is organized into logical functional groups:

### 1. Connection Management
- **Connect** (`R.id.secure_connect_scan`)
  - Icon: Custom `@drawable/action_connect` 
  - Function: Initiates connection to OBD adapter
  - Visibility: Always shown when disconnected
  
- **Disconnect** (`R.id.disconnect`)
  - Icon: Custom `@drawable/action_disconnect`
  - Function: Terminates active OBD connection
  - Visibility: Shown only when connected

### 2. Data Management
- **Save Measurement** (`R.id.save`)
  - Icon: `@android:drawable/ic_menu_save`
  - Function: Exports recorded OBD data to file
  - Uses threaded operation for performance
  
- **Load Measurement** (`R.id.load`)
  - Icon: Custom `@drawable/ic_action_load`
  - Function: Imports previously saved measurement data

### 3. Display and View Options
- **Day/Night Mode** (`R.id.day_night_mode`)
  - Icon: Custom `@drawable/ic_daynight`
  - Function: Toggles between light and dark themes
  - Persists setting in SharedPreferences

### 4. Configuration and Management
- **Settings** (`R.id.settings`)
  - Icon: `@android:drawable/ic_menu_preferences`
  - Function: Opens application settings activity
  - Access to OBD, display, and export configuration
  
- **Plugin Manager** (`R.id.plugin_manager`)
  - Icon: `@android:drawable/ic_menu_manage`
  - Function: Manages AndrOBD extension plugins
  
- **Reset Preselections** (`R.id.reset_preselections`)
  - Icon: `@android:drawable/ic_menu_delete`
  - Function: Clears stored preferences and restarts app

### 5. OBD Diagnostic Services (Submenu)
- **OBD Services** (`R.id.obd_services`)
  - Icon: `@android:drawable/ic_menu_more`
  - Function: Contains submenu with diagnostic functions
  - Enabled only when adapter is connected

## OBD Services Submenu

The OBD Services submenu (`R.menu.obd_services`) provides access to vehicle diagnostic functions:

### Vehicle Information Services
- **Vehicle Info** (`R.id.service_vid_data`)
  - Function: Retrieves basic vehicle identification data
  - Icon: `@android:drawable/ic_menu_info_details`

### Live Data Monitoring  
- **Live Data** (`R.id.service_data`)
  - Function: Real-time engine parameter monitoring
  - Icon: `@android:drawable/ic_menu_view`

### Diagnostic History
- **Freeze Frames** (`R.id.service_freezeframes`)
  - Function: Views stored diagnostic snapshots
  - Icon: `@android:drawable/ic_menu_recent_history`
  - Enabled only when fault codes are present

### Advanced Diagnostics
- **Test Control** (`R.id.service_testcontrol`)
  - Function: Performs OBD system tests
  - Icon: `@android:drawable/ic_menu_slideshow`
  
### Fault Code Management
- **Read Codes** (`R.id.service_codes`)
  - Function: Retrieves current diagnostic trouble codes
  - Icon: `@android:drawable/ic_menu_myplaces`
  
- **Clear Codes** (`R.id.service_clearcodes`)
  - Function: Clears fault codes and resets MIL
  - Icon: `@android:drawable/ic_menu_delete`
  - Includes safety confirmation dialog

## Context Menus

### Chart Activity Menu (`R.menu.chart`)
Available when viewing data charts:
- **Screenshot** (`R.id.snapshot`): Captures chart image
- **Share Data** (`R.id.share`): Exports chart data via CSV

### Data Visualization Context Menu (`R.menu.context_graph`)
Provides quick access to different data presentation modes:
- **Chart View**: Graphical data analysis
- **Dashboard**: Real-time monitoring layout
- **HUD**: Head-up display for minimal distraction
- **Filter**: Data selection and filtering

## Dynamic Menu Behavior

The menu system includes intelligent state management:

### Connection-Based Visibility
- Connect/Disconnect buttons toggle based on connection status
- OBD Services submenu enabled only when connected
- Diagnostic functions require active OBD connection

### Feature-Based Enablement
- Freeze Frames enabled only when fault codes exist
- Test controls available based on vehicle capability
- Export functions available when data is present

### Visual Feedback
- Disabled items are visually dimmed (50% opacity)
- Connection status reflected in menu state
- Progress indicators for long-running operations

## Implementation Details

### Menu Creation
The `onCreateOptionsMenu()` method inflates the main menu and OBD services submenu:
```java
public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    getMenuInflater().inflate(R.menu.obd_services, 
        menu.findItem(R.id.obd_services).getSubMenu());
    return true;
}
```

### Menu Selection Handling
The `onOptionsItemSelected()` method provides organized handling with clear functional grouping and comprehensive documentation for maintainability.

### Utility Methods
- `setMenuItemEnable()`: Controls item enabled state with visual feedback
- `setMenuItemVisible()`: Manages item visibility based on context

## Accessibility Considerations

- All menu items include descriptive titles from string resources
- Icons supplement text labels for better recognition
- Menu structure follows logical grouping for screen readers
- Visual feedback provided for disabled states
- Compatible with Android accessibility services

## Maintenance Guidelines

- Menu items are defined in XML resources for easy localization
- String resources used for all text content
- Standard Android icons used where possible for consistency
- Menu structure documented in code comments
- State management centralized in utility methods

This menu structure provides users with intuitive access to all AndrOBD features while maintaining compatibility with Android 4.1+ and following Android design best practices.
