package com.day.mctokook.modules.ae.entity;

public class AEItem {

    public String name;// 物品id
    public String label;// 物品名
    public Integer damage;// 物品副id
    public Long size;// 物品数量
    public Integer maxDamage;
    public Boolean hasTag;// Tag标签，但是实际上OC的Tag编码有问题
    // @Deprecated
    // private String tag;//Tag,错误编码
    public Boolean isCraftable;// 是否可合成
    public Long maxSize;// 物品最大堆叠数量(属实有点幽默的属性)
    public String labelImage; // 脱敏物品名(图片地址)
}
