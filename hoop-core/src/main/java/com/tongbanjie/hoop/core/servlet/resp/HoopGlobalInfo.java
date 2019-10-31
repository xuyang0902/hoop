package com.tongbanjie.hoop.core.servlet.resp;

import java.io.Serializable;
import java.util.List;

/**
 * @author xu.qiang
 * @date 18/8/31
 */
public class HoopGlobalInfo implements Serializable {
    private static final long serialVersionUID = -6720192053702681756L;

    private List<HoopGlobalVo> list;

    private Integer count;

    public List<HoopGlobalVo> getList() {
        return list;
    }

    public void setList(List<HoopGlobalVo> list) {
        this.list = list;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
