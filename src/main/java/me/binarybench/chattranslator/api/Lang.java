package me.binarybench.chattranslator.api;

import javax.annotation.Nullable;

/**
 * Created by Bench on 5/13/2016.
 */
public enum Lang {
    DETECT_LANG("Detect", "auto"),
    DISABLED("Disabled", "dis"),

    ENGLISH("English", "en"),
    GERMAN("German", "de")
    ;

    private String displayName;
    private String id;

    Lang(String displayName, String id)
    {
        this.displayName = displayName;
        this.id = id.toLowerCase();
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static Lang getLang(String name)
    {
        for (Lang lang : values())
            if (lang.getId().equalsIgnoreCase(name) || lang.getDisplayName().equalsIgnoreCase(name))
                return lang;
        return null;
    }

    @Nullable
    public static Lang fromFromId(String id)
    {
        for (Lang lang : values())
            if (lang.getId().equalsIgnoreCase(id))
                return lang;
        return null;
    }

    @Nullable
    public static Lang fromDisplayName(String displayName)
    {
        for (Lang lang : values())
            if (lang.getDisplayName().equalsIgnoreCase(displayName))
                return lang;
        return null;
    }

}
