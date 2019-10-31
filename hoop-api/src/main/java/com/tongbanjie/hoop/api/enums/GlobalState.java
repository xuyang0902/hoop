package com.tongbanjie.hoop.api.enums;

/**
 * 活动状态
 *
 * @author xu.qiang
 * @date 18/8/13
 */
public enum GlobalState {

    /*初始*/
    INIT("I", "初始"),
    /*未知 */
    UNKOWN("U", "未知"),
    /*提交中 */
    COMMIT("C", "提交中"),
    /*回滚中 */
    ROLLBACK("R", "回滚中"),
    /*已完成 */
    FINISH("F", "已完成");

    private String code;

    private String message;

    GlobalState(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


    public static GlobalState getByCode(String code) {
        if (code != null) {
            for (GlobalState globalState : values()) {
                if (code.equals(globalState.getCode())) {
                    return globalState;
                }
            }
        }
        return null;
    }


}
