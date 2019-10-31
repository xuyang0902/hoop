package com.tongbanjie.hoop.api.transaction;

import com.tongbanjie.hoop.api.model.HoopBranch;
import com.tongbanjie.hoop.api.model.HoopGlobal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 事务对象
 *
 * @author xu.qiang
 * @date 18/8/14
 */
public class HoopTransaction implements Serializable {

    private static final long serialVersionUID = -5942813092725050604L;

    /**
     * 活动日志
     */
    private HoopGlobal hoopGlobal;

    /**
     * 事务参与者原子服务
     */
    private List<HoopBranch> participants = new ArrayList<HoopBranch>(4);

    public HoopGlobal getHoopGlobal() {
        return hoopGlobal;
    }

    public void setHoopGlobal(HoopGlobal hoopGlobal) {
        this.hoopGlobal = hoopGlobal;
    }

    public List<HoopBranch> getParticipants() {
        return participants;
    }

    public void setParticipants(List<HoopBranch> participants) {
        this.participants = participants;
    }

    public void addParticipant(HoopBranch action) {
        participants.add(action);
    }

}
