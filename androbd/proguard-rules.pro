# Add project specific ProGuard rules here.
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# The Save/Load menu items (MainActivity -> FileHelper.saveData/loadData)
# serialize live ProcessVar objects (ObdProt.PidPvs/VidPvs/tCodes,
# MainActivity.mPluginPvs) with plain Java serialization (ObjectOutputStream).
# Deserialization matches by class name, so keep the whole hierarchy -
# including any future subclasses - and the no-arg constructor, or a
# previously-saved file fails to load after R8 renames these classes.
# (PvXMLHandler's own reflective Class.forName() load path is dead code on
# Android - only reachable from its own standalone main(), never called by
# the app - so it's not actually what's at risk here, despite the obvious
# first guess. See issue #339 / roadmap for the full trace.)
-keep class com.fr3ts0n.pvs.ProcessVar {
    public <init>();
}
-keep class * extends com.fr3ts0n.pvs.ProcessVar {
    public <init>();
}

# R8's default class repackaging moves classes into the root package, which
# breaks any *relative* Class.getResource()/getResourceAsStream() call -
# EcuDataItems.loadFromResource() does exactly this
# (getClass().getResource("prot/obd/res/pids.csv")), and the resource file
# itself is untouched (present in the APK either way) but the lookup path
# no longer matches once the class moves. Crashed on every launch of the
# first R8-shrunk build (NullPointerException in ObdProt's static
# initializer). Keeping package names avoids this whole bug class for any
# current or future relative-resource lookup, at negligible size cost.
-keeppackagenames
