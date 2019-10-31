package com.tongbanjie.hoop.api.utils;

import com.tongbanjie.hoop.api.model.Pair;

/**
 * tsId构建器
 * <p>
 * 接入系统必须保证每笔分布式事务记录的tsId是唯一的
 * <p>
 * 业务类型 + 该业务类型下唯一单号
 *
 * @author xu.qiang
 * @date 18/8/7
 */
public class TsIdBuilder {

    public static final String UnderLine = "_";


    /**
     * 构建tsId
     *
     * @param businessType 业务类型  不能是 _
     * @param serialNo     业务类型下唯一流水号
     * @return
     */
    public static String build(String businessType, String serialNo) {

        if (businessType == null) {
            throw new IllegalArgumentException("获取tsId，businessType不能为空");
        }

        if (serialNo == null) {
            throw new IllegalArgumentException("获取tsId，serialNo不能为空");
        }

        String tsId = new StringBuilder(businessType).append(UnderLine).append(serialNo).toString();

        if (tsId.length() > 128) {
            throw new IllegalArgumentException("tsId非法，长度过长,tsId:" + tsId);
        }

        return tsId;
    }


    /**
     * 解析TsId
     *
     * @param tsId
     * @return
     */
    public static Pair<String, String> getTsIdPair(String tsId) {
        int i = tsId.indexOf(UnderLine);
        return new Pair<String, String>(tsId.substring(0, i), tsId.substring(i + 1, tsId.length()));
    }


    public static void main(String[] args) {

        String tsId = TsIdBuilder.build("1001", "123456");

        Pair<String, String> tsIdPair = TsIdBuilder.getTsIdPair(tsId);
        System.out.println(tsIdPair.getKey() + " ---> " + tsIdPair.getValue());
    }
}
