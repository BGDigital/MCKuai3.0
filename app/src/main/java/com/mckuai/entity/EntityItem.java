package com.mckuai.entity;

import com.mckuai.bean.Map;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kyly on 2015/7/15.
 */
public class EntityItem {
    private int id;
    private String name;
    private int imageId;

    public EntityItem(int id,String name){
        this.id = id;
        this.name = name;
    }

    public EntityItem(int id,String name,int imageId){
        this.id = id;
        this.name = name;
        this.imageId = imageId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public static ArrayList<EntityItem> getAllItem(){
        HashMap<Integer,EntityItem> items = getAllEntityItem();
        if (null != items){
            ArrayList<EntityItem> itemList = new ArrayList<>(items.size());
            Iterator iterator = items.entrySet().iterator();
            while (iterator.hasNext()){
                java.util.Map.Entry entry = (java.util.Map.Entry) iterator.next();
                itemList.add((EntityItem) entry.getValue());
            }
            return itemList;
        }
        return null;
    }

    private static   HashMap<Integer,EntityItem> getAllEntityItem(){
        HashMap<Integer,EntityItem> itemMap = new HashMap<>();
        addItem(itemMap, new EntityItem(0, "空气"));
        addItem(itemMap,new EntityItem(1,"石头"));
        addItem(itemMap,new EntityItem(2,"草方块"));
        addItem(itemMap,new EntityItem(3,"泥土"));
        addItem(itemMap,new EntityItem(4,"圆石"));
        addItem(itemMap,new EntityItem(5,"木板"));
        addItem(itemMap,new EntityItem(6,"树苗"));
        addItem(itemMap,new EntityItem(7,"基岩"));
        addItem(itemMap,new EntityItem(8,"水"));
        addItem(itemMap,new EntityItem(9,"静止的水"));
        addItem(itemMap,new EntityItem(10,"岩浆"));
        addItem(itemMap,new EntityItem(11,"静止的岩浆"));
        addItem(itemMap,new EntityItem(12,"沙子"));
        addItem(itemMap,new EntityItem(13,"沙砾"));
        addItem(itemMap,new EntityItem(14,"金矿石"));
        addItem(itemMap,new EntityItem(15,"铁矿石"));
        addItem(itemMap,new EntityItem(16,"煤矿石"));
        addItem(itemMap,new EntityItem(17,"木头"));
        addItem(itemMap,new EntityItem(18,"树叶"));
        addItem(itemMap,new EntityItem(20,"玻璃"));
        addItem(itemMap,new EntityItem(21,"青金石矿石"));
        addItem(itemMap,new EntityItem(22,"青金石块"));
        addItem(itemMap,new EntityItem(24,"沙石"));
        addItem(itemMap,new EntityItem(26,"床"));
        addItem(itemMap,new EntityItem(30,"蜘蛛网"));
        addItem(itemMap,new EntityItem(31,"高草丛 "));
        addItem(itemMap,new EntityItem(35,"羊毛"));
        addItem(itemMap,new EntityItem(37,"蒲公英"));
        addItem(itemMap,new EntityItem(38,"青花"));
        addItem(itemMap,new EntityItem(39,"棕色蘑菇"));
        addItem(itemMap,new EntityItem(40,"红色蘑菇"));
        addItem(itemMap,new EntityItem(41,"金块"));
        addItem(itemMap,new EntityItem(42,"铁块"));
        addItem(itemMap,new EntityItem(43,"双石台阶"));
        addItem(itemMap,new EntityItem(44,"石台阶"));
        addItem(itemMap,new EntityItem(45,"砖块"));
        addItem(itemMap,new EntityItem(46,"TNT"));
        addItem(itemMap,new EntityItem(47,"书架"));
        addItem(itemMap,new EntityItem(48,"苔石"));
        addItem(itemMap,new EntityItem(49,"黑曜石"));
        addItem(itemMap,new EntityItem(50,"火把"));
        addItem(itemMap,new EntityItem(51,"火"));
        addItem(itemMap,new EntityItem(53,"木楼梯"));
        addItem(itemMap,new EntityItem(54,"箱子"));
        addItem(itemMap,new EntityItem(56,"钻石矿石"));
        addItem(itemMap,new EntityItem(57,"钻石块"));
        addItem(itemMap,new EntityItem(58,"工作台"));
        addItem(itemMap,new EntityItem(59,"小麦种子"));
        addItem(itemMap,new EntityItem(60,"耕地"));
        addItem(itemMap,new EntityItem(61,"熔炉"));
        addItem(itemMap,new EntityItem(62,"燃烧的熔炉"));
        addItem(itemMap,new EntityItem(63,"告示牌"));
        addItem(itemMap,new EntityItem(64,"木门"));
        addItem(itemMap,new EntityItem(65,"梯子"));
        addItem(itemMap,new EntityItem(67,"圆石楼梯"));
        addItem(itemMap,new EntityItem(68,"墙上的告示牌"));
        addItem(itemMap,new EntityItem(71,"铁门"));
        addItem(itemMap,new EntityItem(73,"红石矿石"));
        addItem(itemMap,new EntityItem(74,"发光的红石矿石"));
        addItem(itemMap,new EntityItem(78,"雪"));
        addItem(itemMap,new EntityItem(79,"冰"));
        addItem(itemMap,new EntityItem(80,"雪块"));
        addItem(itemMap,new EntityItem(81,"仙人掌"));
        addItem(itemMap,new EntityItem(82,"粘土块"));
        addItem(itemMap,new EntityItem(83,"甘蔗"));
        addItem(itemMap,new EntityItem(85,"栅栏"));
        addItem(itemMap,new EntityItem(87,"地狱岩"));
        addItem(itemMap,new EntityItem(89,"萤石"));
        addItem(itemMap,new EntityItem(95,"隐形基岩"));
        addItem(itemMap,new EntityItem(98,"石砖"));
        addItem(itemMap,new EntityItem(102,"玻璃板"));
        addItem(itemMap,new EntityItem(103,"西瓜"));
        addItem(itemMap,new EntityItem(105,"西瓜梗"));
        addItem(itemMap,new EntityItem(107,"栅栏门"));
        addItem(itemMap,new EntityItem(109,"石砖楼梯"));
        addItem(itemMap,new EntityItem(108,"砖块楼梯"));
        addItem(itemMap,new EntityItem(112,"地狱砖块"));
        addItem(itemMap,new EntityItem(114,"地狱砖楼梯"));
        addItem(itemMap,new EntityItem(128,"沙石楼梯"));
        addItem(itemMap,new EntityItem(155,"石英块"));
        addItem(itemMap,new EntityItem(156,"石英楼梯"));
        addItem(itemMap,new EntityItem(245,"石材切割机"));
        addItem(itemMap,new EntityItem(246,"发光的黑曜石"));
        addItem(itemMap,new EntityItem(247,"下界反应核"));
        addItem(itemMap,new EntityItem(248,"游戏更新方块"));
        addItem(itemMap,new EntityItem(249,"游戏更新方块"));
        addItem(itemMap,new EntityItem(253,"草方块"));
        addItem(itemMap,new EntityItem(254,"树叶"));
        addItem(itemMap,new EntityItem(255,".name"));
        addItem(itemMap,new EntityItem(256,"铁锹"));
        addItem(itemMap,new EntityItem(257,"铁镐"));
        addItem(itemMap,new EntityItem(258,"铁斧"));
        addItem(itemMap,new EntityItem(259,"打火石"));
        addItem(itemMap,new EntityItem(260,"红苹果"));
        addItem(itemMap,new EntityItem(261,"弓"));
        addItem(itemMap,new EntityItem(262,"箭"));
        addItem(itemMap,new EntityItem(263,"煤炭"));
        addItem(itemMap,new EntityItem(264,"钻石"));
        addItem(itemMap,new EntityItem(265,"铁锭"));
        addItem(itemMap,new EntityItem(266,"金锭"));
        addItem(itemMap,new EntityItem(267,"铁剑"));
        addItem(itemMap,new EntityItem(268,"木剑"));
        addItem(itemMap,new EntityItem(269,"木锹"));
        addItem(itemMap,new EntityItem(270,"木镐"));
        addItem(itemMap,new EntityItem(271,"木斧"));
        addItem(itemMap,new EntityItem(272,"石剑"));
        addItem(itemMap,new EntityItem(273,"石锹"));
        addItem(itemMap,new EntityItem(274,"石镐"));
        addItem(itemMap,new EntityItem(275,"石斧"));
        addItem(itemMap,new EntityItem(276,"钻石剑"));
        addItem(itemMap,new EntityItem(277,"钻石锹"));
        addItem(itemMap,new EntityItem(278,"钻石镐"));
        addItem(itemMap,new EntityItem(279,"钻石斧"));
        addItem(itemMap,new EntityItem(280,"木棍"));
        addItem(itemMap,new EntityItem(281,"碗"));
        addItem(itemMap,new EntityItem(282,"蘑菇煲"));
        addItem(itemMap,new EntityItem(283,"金剑"));
        addItem(itemMap,new EntityItem(284,"金锹"));
        addItem(itemMap,new EntityItem(285,"金镐"));
        addItem(itemMap,new EntityItem(286,"金斧"));
        addItem(itemMap,new EntityItem(287,"线"));
        addItem(itemMap,new EntityItem(288,"羽毛"));
        addItem(itemMap,new EntityItem(289,"火药"));
        addItem(itemMap,new EntityItem(298,"皮革头盔"));
        addItem(itemMap,new EntityItem(299,"皮革胸甲"));
        addItem(itemMap,new EntityItem(300,"皮革护腿"));
        addItem(itemMap,new EntityItem(301,"皮革靴子"));
        addItem(itemMap,new EntityItem(302,"链甲头盔"));
        addItem(itemMap,new EntityItem(303,"链甲胸甲"));
        addItem(itemMap,new EntityItem(304,"链甲护腿"));
        addItem(itemMap,new EntityItem(305,"链甲靴子"));
        addItem(itemMap,new EntityItem(306,"铁头盔"));
        addItem(itemMap,new EntityItem(307,"铁甲"));
        addItem(itemMap,new EntityItem(308,"铁护腿"));
        addItem(itemMap,new EntityItem(309,"铁鞋子"));
        addItem(itemMap,new EntityItem(310,"钻石头盔"));
        addItem(itemMap,new EntityItem(311,"钻石甲"));
        addItem(itemMap,new EntityItem(312,"钻石护腿"));
        addItem(itemMap,new EntityItem(313,"钻石鞋子"));
        addItem(itemMap,new EntityItem(314,"金头盔"));
        addItem(itemMap,new EntityItem(315,"金甲"));
        addItem(itemMap,new EntityItem(316,"金护腿"));
        addItem(itemMap,new EntityItem(317,"金鞋子"));
        addItem(itemMap,new EntityItem(292,"燧石"));
        addItem(itemMap,new EntityItem(296,"小麦"));
        addItem(itemMap,new EntityItem(321,"画"));
        addItem(itemMap,new EntityItem(323,"告示牌"));
        addItem(itemMap,new EntityItem(324,"木 门"));
        addItem(itemMap,new EntityItem(325,"桶D"));
        addItem(itemMap,new EntityItem(329,"鞍"));
        addItem(itemMap,new EntityItem(330,"铁门"));
        addItem(itemMap,new EntityItem(332,"雪球"));
        addItem(itemMap,new EntityItem(334,"皮革"));
        addItem(itemMap,new EntityItem(336,"红砖"));
        addItem(itemMap,new EntityItem(337,"粘土"));
        addItem(itemMap,new EntityItem(338,"甘蔗"));
        addItem(itemMap,new EntityItem(339,"纸"));
        addItem(itemMap,new EntityItem(340,"书"));
        addItem(itemMap,new EntityItem(341,"粘液球"));
        addItem(itemMap,new EntityItem(344,"鸡蛋"));
        addItem(itemMap,new EntityItem(345,"指南针"));
        addItem(itemMap,new EntityItem(347,"钟"));
        addItem(itemMap,new EntityItem(348,"萤石粉"));
        addItem(itemMap,new EntityItem(351,"染料"));
        addItem(itemMap,new EntityItem(353,"糖"));
        addItem(itemMap,new EntityItem(359,"剪刀"));
        addItem(itemMap,new EntityItem(383,"刷怪蛋"));
        addItem(itemMap,new EntityItem(405,"地狱砖"));
        addItem(itemMap,new EntityItem(406,"下界石英"));
        addItem(itemMap,new EntityItem(456,"相机"));
        addItem(itemMap,new EntityItem(64,"掉落物"));
        addItem(itemMap,new EntityItem(65,"点燃的 TNT"));
        addItem(itemMap,new EntityItem(81,"扔出的 雪球"));
        addItem(itemMap,new EntityItem(82,"扔出的 鸡蛋"));
        addItem(itemMap,new EntityItem(83,"画"));
        addItem(itemMap,new EntityItem(36,"僵尸猪人"));
        addItem(itemMap,new EntityItem(35,"蜘蛛"));
        addItem(itemMap,new EntityItem(34,"骷髅"));
        addItem(itemMap,new EntityItem(33,"爬行者"));
        addItem(itemMap,new EntityItem(32,"僵尸"));
        addItem(itemMap,new EntityItem(10,"鸡"));
        addItem(itemMap,new EntityItem(11,"牛"));
        addItem(itemMap,new EntityItem(12,"猪"));
        addItem(itemMap,new EntityItem(13,"羊"));
        return itemMap;
    }

    private static void addItem(HashMap<Integer,EntityItem> map, EntityItem item){
        map.put(item.getId(),item);
    }

    public static String  getNameById(int id){
        HashMap itemMap = getAllEntityItem();
        String name = null;
        EntityItem item = (EntityItem) itemMap.get(id);
        if (null != item){
            return item.getName();
        }
        return "未知";
    }
}
