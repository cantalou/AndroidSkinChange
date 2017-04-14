package skin.util;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.util.TypedValue;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author Lin Zhiwei
 * @date 16-10-11 下午10:43
 */
public class ResourcesUtil {

    protected boolean isColor(TypedValue value) {
        return value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT;
    }

    public static int loadResourcesIdFromManifest(Activity activity, String attributeName) {
        int resourcesId = 0;
        try {
            final String thisPackage = activity.getClass().getName();

            final String packageName = activity.getApplicationInfo().packageName;
            final AssetManager am = activity.createPackageContext(packageName, 0).getAssets();
            final XmlResourceParser xml = am.openXmlResourceParser("AndroidManifest.xml");

            int eventType = xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String name = xml.getName();

                    if ("application".equals(name)) {
                        // Check if the <application> has the attribute

                        for (int i = xml.getAttributeCount() - 1; i >= 0; i--) {

                            if (attributeName.equals(xml.getAttributeName(i))) {
                                resourcesId = xml.getAttributeResourceValue(i, 0);
                                break; // out of for loop
                            }
                        }
                    } else if ("activity".equals(name)) {
                        // Check if the <activity> is us and has the attribute
                        Integer activityResourceId = null;
                        String activityPackage = null;
                        boolean isOurActivity = false;

                        for (int i = xml.getAttributeCount() - 1; i >= 0; i--) {

                            // We need both uiOptions and name attributes
                            String attrName = xml.getAttributeName(i);
                            if (attributeName.equals(attrName)) {
                                activityResourceId = xml.getAttributeResourceValue(i, 0);
                            } else if ("name".equals(attrName)) {
                                activityPackage = cleanActivityName(packageName, xml.getAttributeValue(i));
                                if (!thisPackage.equals(activityPackage)) {
                                    break; // on to the next
                                }
                                isOurActivity = true;
                            }

                            // Make sure we have both attributes before
                            // processing
                            if ((activityResourceId != null) && (activityPackage != null)) {
                                // Our activity, logo specified, override with
                                // our value
                                resourcesId = activityResourceId.intValue();
                            }
                        }
                        if (isOurActivity) {
                            // If we matched our activity but it had no logo
                            // don't
                            // do any more processing of the manifest
                            break;
                        }
                    }
                }
                eventType = xml.nextToken();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourcesId;
    }

    /**
     * Attempt to programmatically load the icon from the manifest file of an
     * activity by using an XML pull parser. This should allow us to read the
     * icon attribute regardless of the platform it is being run on.
     *
     * @param activity Activity instance.
     * @return Icon resource ID.
     */
    public static int loadIconFromManifest(Activity activity) {
        return loadResourcesIdFromManifest(activity, "icon");
    }

    /**
     * Attempt to programmatically load the logo from the manifest file of an
     * activity by using an XML pull parser. This should allow us to read the
     * logo attribute regardless of the platform it is being run on.
     *
     * @param activity Activity instance.
     * @return Logo resource ID.
     */
    public static int loadLogoFromManifest(Activity activity) {
        return loadResourcesIdFromManifest(activity, "logo");
    }

    public static String cleanActivityName(String manifestPackage, String activityName) {
        if (activityName.charAt(0) == '.') {
            // Relative activity name (e.g., android:name=".ui.SomeClass")
            return manifestPackage + activityName;
        }
        if (activityName.indexOf('.', 1) == -1) {
            // Unqualified activity name (e.g., android:name="SomeClass")
            return manifestPackage + "." + activityName;
        }
        // Fully-qualified activity name (e.g., "com.my.package.SomeClass")
        return activityName;
    }
}
