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
    private short id;
    private String name;
    private int imageId;

    public EntityItem(short id, String name) {
        this.id = id;
        this.name = name;
    }

    public EntityItem(short id, String name, int imageId) {
        this.id = id;
        this.name = name;
        this.imageId = imageId;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
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

    public static ArrayList<EntityItem> getAllItem() {
        HashMap<Short, EntityItem> items = getAllEntityItem();
        if (null != items) {
            ArrayList<EntityItem> itemList = new ArrayList<>(items.size());
            Iterator iterator = items.entrySet().iterator();
            while (iterator.hasNext()) {
                java.util.Map.Entry entry = (java.util.Map.Entry) iterator.next();
                itemList.add((EntityItem) entry.getValue());
            }
            return itemList;
        }
        return null;
    }

    private static HashMap<Short, EntityItem> getAllEntityItem() {
        HashMap<Short, EntityItem> itemMap = new HashMap<>();
        addItem(itemMap, new EntityItem((short) 0, "空气"));
        addItem(itemMap, new EntityItem((short) 1, "石头"));
        addItem(itemMap, new EntityItem((short) 2, "草方块"));
        addItem(itemMap, new EntityItem((short) 3, "泥土"));
        addItem(itemMap, new EntityItem((short) 4, "圆石"));
        addItem(itemMap, new EntityItem((short) 5, "木板"));
        addItem(itemMap, new EntityItem((short) 6, "树苗"));
        addItem(itemMap, new EntityItem((short) 7, "基岩"));
        addItem(itemMap, new EntityItem((short) 8, "水"));
        addItem(itemMap, new EntityItem((short) 9, "静止的水"));
        addItem(itemMap, new EntityItem((short) 10, "岩浆"));
        addItem(itemMap, new EntityItem((short) 11, "静止的岩浆"));
        addItem(itemMap, new EntityItem((short) 12, "沙子"));
        addItem(itemMap, new EntityItem((short) 13, "沙砾"));
        addItem(itemMap, new EntityItem((short) 14, "金矿石"));
        addItem(itemMap, new EntityItem((short) 15, "铁矿石"));
        addItem(itemMap, new EntityItem((short) 16, "煤矿石"));
        addItem(itemMap, new EntityItem((short) 17, "木头"));
        addItem(itemMap, new EntityItem((short) 18, "树叶"));
        addItem(itemMap, new EntityItem((short) 20, "玻璃"));
        addItem(itemMap, new EntityItem((short) 21, "青金石矿石"));
        addItem(itemMap, new EntityItem((short) 22, "青金石块"));
        addItem(itemMap, new EntityItem((short) 24, "沙石"));
        addItem(itemMap, new EntityItem((short) 26, "床"));
        addItem(itemMap, new EntityItem((short) 30, "蜘蛛网"));
        addItem(itemMap, new EntityItem((short) 31, "高草丛 "));
        addItem(itemMap, new EntityItem((short) 35, "羊毛"));
        addItem(itemMap, new EntityItem((short) 37, "蒲公英"));
        addItem(itemMap, new EntityItem((short) 38, "青花"));
        addItem(itemMap, new EntityItem((short) 39, "棕色蘑菇"));
        addItem(itemMap, new EntityItem((short) 40, "红色蘑菇"));
        addItem(itemMap, new EntityItem((short) 41, "金块"));
        addItem(itemMap, new EntityItem((short) 42, "铁块"));
        addItem(itemMap, new EntityItem((short) 43, "双石台阶"));
        addItem(itemMap, new EntityItem((short) 44, "石台阶"));
        addItem(itemMap, new EntityItem((short) 45, "砖块"));
        addItem(itemMap, new EntityItem((short) 46, "TNT"));
        addItem(itemMap, new EntityItem((short) 47, "书架"));
        addItem(itemMap, new EntityItem((short) 48, "苔石"));
        addItem(itemMap, new EntityItem((short) 49, "黑曜石"));
        addItem(itemMap, new EntityItem((short) 50, "火把"));
        addItem(itemMap, new EntityItem((short) 51, "火"));
        addItem(itemMap, new EntityItem((short) 53, "木楼梯"));
        addItem(itemMap, new EntityItem((short) 54, "箱子"));
        addItem(itemMap, new EntityItem((short) 56, "钻石矿石"));
        addItem(itemMap, new EntityItem((short) 57, "钻石块"));
        addItem(itemMap, new EntityItem((short) 58, "工作台"));
        addItem(itemMap, new EntityItem((short) 59, "小麦种子"));
        addItem(itemMap, new EntityItem((short) 60, "耕地"));
        addItem(itemMap, new EntityItem((short) 61, "熔炉"));
        addItem(itemMap, new EntityItem((short) 62, "燃烧的熔炉"));
        addItem(itemMap, new EntityItem((short) 63, "告示牌"));
        addItem(itemMap, new EntityItem((short) 64, "木门"));
        addItem(itemMap, new EntityItem((short) 65, "梯子"));
        addItem(itemMap, new EntityItem((short) 67, "圆石楼梯"));
        addItem(itemMap, new EntityItem((short) 68, "墙上的告示牌"));
        addItem(itemMap, new EntityItem((short) 71, "铁门"));
        addItem(itemMap, new EntityItem((short) 73, "红石矿石"));
        addItem(itemMap, new EntityItem((short) 74, "发光的红石矿石"));
        addItem(itemMap, new EntityItem((short) 78, "雪"));
        addItem(itemMap, new EntityItem((short) 79, "冰"));
        addItem(itemMap, new EntityItem((short) 80, "雪块"));
        addItem(itemMap, new EntityItem((short) 81, "仙人掌"));
        addItem(itemMap, new EntityItem((short) 82, "粘土块"));
        addItem(itemMap, new EntityItem((short) 83, "甘蔗"));
        addItem(itemMap, new EntityItem((short) 85, "栅栏"));
        addItem(itemMap, new EntityItem((short) 87, "地狱岩"));
        addItem(itemMap, new EntityItem((short) 89, "萤石"));
        addItem(itemMap, new EntityItem((short) 95, "隐形基岩"));
        addItem(itemMap, new EntityItem((short) 98, "石砖"));
        addItem(itemMap, new EntityItem((short) 102, "玻璃板"));
        addItem(itemMap, new EntityItem((short) 103, "西瓜"));
        addItem(itemMap, new EntityItem((short) 105, "西瓜梗"));
        addItem(itemMap, new EntityItem((short) 107, "栅栏门"));
        addItem(itemMap, new EntityItem((short) 109, "石砖楼梯"));
        addItem(itemMap, new EntityItem((short) 108, "砖块楼梯"));
        addItem(itemMap, new EntityItem((short) 112, "地狱砖块"));
        addItem(itemMap, new EntityItem((short) 114, "地狱砖楼梯"));
        addItem(itemMap, new EntityItem((short) 128, "沙石楼梯"));
        addItem(itemMap, new EntityItem((short) 155, "石英块"));
        addItem(itemMap, new EntityItem((short) 156, "石英楼梯"));
        addItem(itemMap, new EntityItem((short) 245, "石材切割机"));
        addItem(itemMap, new EntityItem((short) 246, "发光的黑曜石"));
        addItem(itemMap, new EntityItem((short) 247, "下界反应核"));
        addItem(itemMap, new EntityItem((short) 248, "游戏更新方块"));
        addItem(itemMap, new EntityItem((short) 249, "游戏更新方块"));
        addItem(itemMap, new EntityItem((short) 253, "草方块"));
        addItem(itemMap, new EntityItem((short) 254, "树叶"));
        addItem(itemMap, new EntityItem((short) 255, ".name"));
        addItem(itemMap, new EntityItem((short) 256, "铁锹"));
        addItem(itemMap, new EntityItem((short) 257, "铁镐"));
        addItem(itemMap, new EntityItem((short) 258, "铁斧"));
        addItem(itemMap, new EntityItem((short) 259, "打火石"));
        addItem(itemMap, new EntityItem((short) 260, "红苹果"));
        addItem(itemMap, new EntityItem((short) 261, "弓"));
        addItem(itemMap, new EntityItem((short) 262, "箭"));
        addItem(itemMap, new EntityItem((short) 263, "煤炭"));
        addItem(itemMap, new EntityItem((short) 264, "钻石"));
        addItem(itemMap, new EntityItem((short) 265, "铁锭"));
        addItem(itemMap, new EntityItem((short) 266, "金锭"));
        addItem(itemMap, new EntityItem((short) 267, "铁剑"));
        addItem(itemMap, new EntityItem((short) 268, "木剑"));
        addItem(itemMap, new EntityItem((short) 269, "木锹"));
        addItem(itemMap, new EntityItem((short) 270, "木镐"));
        addItem(itemMap, new EntityItem((short) 271, "木斧"));
        addItem(itemMap, new EntityItem((short) 272, "石剑"));
        addItem(itemMap, new EntityItem((short) 273, "石锹"));
        addItem(itemMap, new EntityItem((short) 274, "石镐"));
        addItem(itemMap, new EntityItem((short) 275, "石斧"));
        addItem(itemMap, new EntityItem((short) 276, "钻石剑"));
        addItem(itemMap, new EntityItem((short) 277, "钻石锹"));
        addItem(itemMap, new EntityItem((short) 278, "钻石镐"));
        addItem(itemMap, new EntityItem((short) 279, "钻石斧"));
        addItem(itemMap, new EntityItem((short) 280, "木棍"));
        addItem(itemMap, new EntityItem((short) 281, "碗"));
        addItem(itemMap, new EntityItem((short) 282, "蘑菇煲"));
        addItem(itemMap, new EntityItem((short) 283, "金剑"));
        addItem(itemMap, new EntityItem((short) 284, "金锹"));
        addItem(itemMap, new EntityItem((short) 285, "金镐"));
        addItem(itemMap, new EntityItem((short) 286, "金斧"));
        addItem(itemMap, new EntityItem((short) 287, "线"));
        addItem(itemMap, new EntityItem((short) 288, "羽毛"));
        addItem(itemMap, new EntityItem((short) 289, "火药"));
        addItem(itemMap, new EntityItem((short) 298, "皮革头盔"));
        addItem(itemMap, new EntityItem((short) 299, "皮革胸甲"));
        addItem(itemMap, new EntityItem((short) 300, "皮革护腿"));
        addItem(itemMap, new EntityItem((short) 301, "皮革靴子"));
        addItem(itemMap, new EntityItem((short) 302, "链甲头盔"));
        addItem(itemMap, new EntityItem((short) 303, "链甲胸甲"));
        addItem(itemMap, new EntityItem((short) 304, "链甲护腿"));
        addItem(itemMap, new EntityItem((short) 305, "链甲靴子"));
        addItem(itemMap, new EntityItem((short) 306, "铁头盔"));
        addItem(itemMap, new EntityItem((short) 307, "铁甲"));
        addItem(itemMap, new EntityItem((short) 308, "铁护腿"));
        addItem(itemMap, new EntityItem((short) 309, "铁鞋子"));
        addItem(itemMap, new EntityItem((short) 310, "钻石头盔"));
        addItem(itemMap, new EntityItem((short) 311, "钻石甲"));
        addItem(itemMap, new EntityItem((short) 312, "钻石护腿"));
        addItem(itemMap, new EntityItem((short) 313, "钻石鞋子"));
        addItem(itemMap, new EntityItem((short) 314, "金头盔"));
        addItem(itemMap, new EntityItem((short) 315, "金甲"));
        addItem(itemMap, new EntityItem((short) 316, "金护腿"));
        addItem(itemMap, new EntityItem((short) 317, "金鞋子"));
        addItem(itemMap, new EntityItem((short) 292, "燧石"));
        addItem(itemMap, new EntityItem((short) 296, "小麦"));
        addItem(itemMap, new EntityItem((short) 321, "画"));
        addItem(itemMap, new EntityItem((short) 323, "告示牌"));
        addItem(itemMap, new EntityItem((short) 324, "木 门"));
        addItem(itemMap, new EntityItem((short) 325, "桶D"));
        addItem(itemMap, new EntityItem((short) 329, "鞍"));
        addItem(itemMap, new EntityItem((short) 330, "铁门"));
        addItem(itemMap, new EntityItem((short) 332, "雪球"));
        addItem(itemMap, new EntityItem((short) 334, "皮革"));
        addItem(itemMap, new EntityItem((short) 336, "红砖"));
        addItem(itemMap, new EntityItem((short) 337, "粘土"));
        addItem(itemMap, new EntityItem((short) 338, "甘蔗"));
        addItem(itemMap, new EntityItem((short) 339, "纸"));
        addItem(itemMap, new EntityItem((short) 340, "书"));
        addItem(itemMap, new EntityItem((short) 341, "粘液球"));
        addItem(itemMap, new EntityItem((short) 344, "鸡蛋"));
        addItem(itemMap, new EntityItem((short) 345, "指南针"));
        addItem(itemMap, new EntityItem((short) 347, "钟"));
        addItem(itemMap, new EntityItem((short) 348, "萤石粉"));
        addItem(itemMap, new EntityItem((short) 351, "染料"));
        addItem(itemMap, new EntityItem((short) 353, "糖"));
        addItem(itemMap, new EntityItem((short) 359, "剪刀"));
        addItem(itemMap, new EntityItem((short) 383, "刷怪蛋"));
        addItem(itemMap, new EntityItem((short) 405, "地狱砖"));
        addItem(itemMap, new EntityItem((short) 406, "下界石英"));
        addItem(itemMap, new EntityItem((short) 456, "相机"));
        addItem(itemMap, new EntityItem((short) 64, "掉落物"));
        addItem(itemMap, new EntityItem((short) 65, "点燃的 TNT"));
        addItem(itemMap, new EntityItem((short) 81, "扔出的 雪球"));
        addItem(itemMap, new EntityItem((short) 82, "扔出的 鸡蛋"));
        addItem(itemMap, new EntityItem((short) 83, "画"));
        addItem(itemMap, new EntityItem((short) 36, "僵尸猪人"));
        addItem(itemMap, new EntityItem((short) 35, "蜘蛛"));
        addItem(itemMap, new EntityItem((short) 34, "骷髅"));
        addItem(itemMap, new EntityItem((short) 33, "爬行者"));
        addItem(itemMap, new EntityItem((short) 32, "僵尸"));
        addItem(itemMap, new EntityItem((short) 10, "鸡"));
        addItem(itemMap, new EntityItem((short) 11, "牛"));
        addItem(itemMap, new EntityItem((short) 12, "猪"));
        addItem(itemMap, new EntityItem((short) 13, "羊"));
        return itemMap;
    }

    private static void addItem(HashMap<Short, EntityItem> map, EntityItem item) {
        map.put(item.getId(), item);
    }

    public static String getNameById(Short id) {
        HashMap<Short,EntityItem> itemMap = getAllEntityItem();
        EntityItem item;
        item=itemMap.get(id);
        if (null != item) {
            return item.getName();
        }
        return "未知";
    }
}
