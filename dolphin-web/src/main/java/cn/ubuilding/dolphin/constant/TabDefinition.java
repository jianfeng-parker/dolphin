package cn.ubuilding.dolphin.constant;

/**
 * @author Wu Jianfeng
 * @since 16/3/6 21:37
 */

public enum TabDefinition {

    Introduction(0, "Introduction", "introduction"), Dev(1, "Dev", "settings"),
    Test(2, "Test", "settings"), Prod(3, "Prod", "settings"), Admin(4, "Admin", "system/admin");

    private int code;

    private String name;

    private String viewName;

    TabDefinition(int code, String name, String viewName) {
        this.code = code;
        this.name = name;
        this.viewName = viewName;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getViewName() {
        return viewName;
    }

    public static String getViewName(int code) {
        for (TabDefinition definition : TabDefinition.values()) {
            if (code == definition.getCode()) return definition.getViewName();
        }
        return "";
    }
}
