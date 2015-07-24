package com.mckuai.utils;

import android.content.Context;
import android.text.format.Formatter;

/**
 * Created by kyly on 2015/7/9.
 */
public class MathUtil {
    public static String getFileSizeWithByte(Context context, int size)
    {
        String str = "未知";
        try
        {
            str = Formatter.formatFileSize(context, size);
            return str;
        }
        catch (Exception e) {}
        return "未知";
    }

    public static String getFileSizeWithByte(Context context, String size)
    {
        String str = "未知";
        try
        {
            int i = Integer.valueOf(size.replaceAll("\\D+", "").replaceAll("\r", "").replaceAll("\n", "").trim()).intValue();
            size = str;
            if (i >= 0) {
                size = Formatter.formatFileSize(context, i);
            }
            return size;
        }
        catch (Exception paramContext) {}
        return "未知";
    }
}
