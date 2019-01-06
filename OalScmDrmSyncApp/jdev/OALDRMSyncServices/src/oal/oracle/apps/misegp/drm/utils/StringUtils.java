package oal.oracle.apps.misegp.drm.utils;

public class StringUtils {
    public StringUtils() {
        super();
    }

    public static String toCamelCase(String s) {
        String[] parts = s.split("_");
        String camelCaseString = parts[0].toLowerCase();
        
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            if (part != null && part.trim().length() > 0)
                camelCaseString = camelCaseString + toProperCase(part);
            else
                camelCaseString = camelCaseString + part + " ";
        }
        return camelCaseString;
    }

    public static String toProperCase(String s) {
        String temp = s.trim();
        String spaces = "";
        if (temp.length() != s.length()) {
            int startCharIndex = s.charAt(temp.indexOf(0));
            spaces = s.substring(0, startCharIndex);
        }
        temp = temp.substring(0, 1).toUpperCase() + spaces + temp.substring(1).toLowerCase();
        return temp;

    }

    public static void main(String[] args) {
        String string = "ALLOW_EXPRESS_DELIVERY_FLAG";
        System.out.println(toCamelCase(string));
    }
}
