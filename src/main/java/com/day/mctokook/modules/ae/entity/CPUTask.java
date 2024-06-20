package com.day.mctokook.modules.ae.entity;

import java.util.List;

// CPU详情里面的合成任务
public class CPUTask {

    public int id;// CPU id
    public List<AEItem> craftings;// 合成中物品
    public List<AEItem> storeds;// 所有存在里面的物品
    public List<AEItem> pendings;// 计划合成中的物品
    public AEItem finalOutput;// 最终输出
    public Boolean busy;// 是否合成中
}
