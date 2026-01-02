package io.github.renhaowan.bizlog.core.log.action;

import lombok.Getter;

/**
 * 业务操作类型
 * 系统内置动作
 */
@Getter
public enum StdBizAction {
    LOGIN("LOGIN", "登录"),
    CREATE("CREATE", "创建"),
    UPDATE("UPDATE", "更新"),
    DELETE("DELETE", "删除"),
    QUERY("QUERY", "查询"),
    EXPORT("EXPORT", "导出"),
    IMPORT("IMPORT", "导入"),
    OTHER("OTHER", "其他");

    public static final String LOGIN_CODE = "LOGIN";
    public static final String CREATE_CODE = "CREATE";
    public static final String UPDATE_CODE = "UPDATE";
    public static final String DELETE_CODE = "DELETE";
    public static final String QUERY_CODE = "QUERY";
    public static final String EXPORT_CODE = "EXPORT";
    public static final String IMPORT_CODE = "IMPORT";
    public static final String OTHER_CODE = "OTHER";

    private final String code;
    private final String desc;

    StdBizAction(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
