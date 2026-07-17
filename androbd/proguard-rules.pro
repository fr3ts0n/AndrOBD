# Add project specific ProGuard rules here.
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# PvXMLHandler reconstructs saved ProcessVar objects by fully-qualified class
# name (PvXMLWriter writes pv.getClass().getName() as the XML "type"
# attribute, PvXMLHandler reads it back via Class.forName(...).newInstance()).
# Keep the whole hierarchy - including any future subclasses - and the
# no-arg constructor that reflection needs, or saved PV files fail to load
# after R8 renames/removes these classes. See issue #339.
-keep class com.fr3ts0n.pvs.ProcessVar {
    public <init>();
}
-keep class * extends com.fr3ts0n.pvs.ProcessVar {
    public <init>();
}
