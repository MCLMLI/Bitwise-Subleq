import java.io.IOException;

/**
 * 语言配置类 - 管理中英文输出
 * Language configuration class - manages Chinese/English output
 */
public class Lang {
    private static boolean useChinese = true; // 默认中文 / Default to Chinese

    /**
     * 设置语言模式
     * Set language mode
     */
    public static void setLanguage(String lang) {
        useChinese = lang == null || lang.equalsIgnoreCase("zh") || lang.equalsIgnoreCase("cn");
    }

    /**
     * 获取本地化消息
     * Get localized message
     */
    public static String get(String zh, String en) {
        return useChinese ? zh : en;
    }

    /**
     * 检查是否使用中文
     * Check if using Chinese
     */
    public static boolean isChinese() {
        return useChinese;
    }
}

