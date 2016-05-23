package com.brilliantbear.smscoder.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Created by Bear on 2016-5-22.
 */
public class ClipUtils {
    public static void setText(Context context, String label, String str) {
        // 复制到剪贴板
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context
                .CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(label, str);
        clipboardManager.setPrimaryClip(clipData);
    }
}
