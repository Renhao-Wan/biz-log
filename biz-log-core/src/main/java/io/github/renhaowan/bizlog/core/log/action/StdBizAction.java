package io.github.renhaowan.bizlog.core.log.action;

import lombok.Getter;

/**
 * @author wan
 * 业务操作类型
 * 系统内置动作
 */
@Getter
public enum StdBizAction {

    /**
     * 登录
     */
    LOGIN("LOGIN", "登录"),

    /**
     * 创建
     */
    CREATE("CREATE", "创建"),

    /**
     * 更新
     */
    UPDATE("UPDATE", "更新"),

    /**
     * 删除
     */
    DELETE("DELETE", "删除"),

    /**
     * 查询
     */
    QUERY("QUERY", "查询"),

    /**
     * 导出
     */
    EXPORT("EXPORT", "导出"),

    /**
     * 导入
     */
    IMPORT("IMPORT", "导入"),

    /**
     * 其他
     */
    OTHER("OTHER", "其他");

    /**
     * 组件内置动作编码: LOGIN
     */
    public static final String LOGIN_CODE = "LOGIN";

    /**
     * 组件内置动作编码: CREATE
     */
    public static final String CREATE_CODE = "CREATE";

    /**
     * 组件内置动作编码: UPDATE
     */
    public static final String UPDATE_CODE = "UPDATE";

    /**
     * 组件内置动作编码: DELETE
     */
    public static final String DELETE_CODE = "DELETE";

    /**
     * 组件内置动作编码: QUERY
     */
    public static final String QUERY_CODE = "QUERY";

    /**
     * 组件内置动作编码: EXPORT
     */
    public static final String EXPORT_CODE = "EXPORT";

    /**
     * 组件内置动作编码: IMPORT
     */
    public static final String IMPORT_CODE = "IMPORT";

    /**
     * 组件内置动作编码: OTHER
     */
    public static final String OTHER_CODE = "OTHER";

    private final String code;
    private final String desc;

    /**
     * 构造函数
     *
     * @param code 业务操作编码
     * @param desc 业务操作描述
     */
    StdBizAction(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
