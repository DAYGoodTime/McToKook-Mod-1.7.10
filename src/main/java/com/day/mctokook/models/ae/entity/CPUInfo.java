package com.day.mctokook.models.ae.entity;

// getCPUs当中的CPU信息
public class CPUInfo {

    public long storage;// CPU容量(字节)
    public String name;// CPU名字
    public Boolean busy;// CPU是否正在处理合成中
    public Long coprocessors;// 协(并行)处理器数量
    public Integer ID;// CPU编号
    public AEItem CraftingItem;// 正在合成的物品
}
