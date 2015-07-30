package com.mckuai.mctools.item.entity;

import android.content.pm.ApplicationInfo;


/**
 * Created by kyly on 2015/7/8.
 */
public class AddonListItem {
    private static String a = "";
    private static String b = " (disabled)";
    public final ApplicationInfo appInfo;
    public String displayName;
    public boolean enabled = true;

    public AddonListItem(ApplicationInfo applicationInfo, boolean isEnabled)
    {
        this.appInfo = applicationInfo;
        this.displayName = applicationInfo.packageName;
        this.enabled = isEnabled;
    }

    public String toString()
    {
        StringBuilder localStringBuilder = new StringBuilder().append(this.displayName);
        if (this.enabled) {}
        for (String str = a;; str = b) {
            return str;
        }
    }
}
