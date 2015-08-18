package com.mckuai.mctools;

import com.mckuai.mctools.item.entity.Player;
import com.mckuai.mctools.item.tileentity.TileEntity;
import com.mckuai.mctools.item.entity.Entity;

import java.io.Serializable;
import java.util.List;


public class Level implements Serializable{
	//游戏类型，0为生存模式，1为创造模式
	private int gameType;
	//最近一次玩游戏的时间
	private long lastPlayed;
	//地图名称
	private String levelName;
	//平台，PE版的为2
	private int platform;
	//游戏角色实体信息，0.90（StorageVersion=3）版以前的游戏才有此字段
	private Player player;
	//Level seed
	private long randomSeed;
	//存档大小
	private long sizeOnDisk;
	//玩家出生点坐标
	private int spawnX, spawnY, spawnZ;
	//存档版本（0.81及之前版本为3,其后为4）
	private int storageVersion;
	//以刻的形式保存的当天时间（游戏里的一天有14400刻，0为白天的开始，7200为日落，8280是夜晚的开始，13320是日出，）
	private long time;

    private int  DayCycleStopTime = -1;//0.11
	private long dayCycleStopTime = -1;  //0.9
	//是否启用spawnMobs
	private boolean spawnMobs = true;
	//玩家所在的维度，0为主世界
	private int dimension = 0;
	//世界类型，0.9及之后的版本支持，,包括old(旧的世界格式，有长宽高限制),infinite(默认格式，没有长宽高限制)或者flat（超平坦）
	private int generator = -1;
	//当世界类型为旧世界时，世界的大小限制
	private int limitedworldoriginy = -1;
	private int limitedworldoriginx = -1;
	private int limitedworldoriginz = -1;
	//0.11版本新加入的新内容
	private long cureationTime = -1;
	private long currentTick = -1;
    private long worldStartCount = -1;
	//
	private List<Entity> entities;

	private List<TileEntity> tileEntities;

	public int getGameType() {
		return gameType;
	}

	public void setGameType(int gameType) {
		this.gameType = gameType;
	}

	public long getLastPlayed() {
		return lastPlayed;
	}

	public void setLastPlayed(long lastPlayed) {
		this.lastPlayed = lastPlayed;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public int getPlatform() {
		return platform;
	}

	public void setPlatform(int platform) {
		this.platform = platform;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public long getRandomSeed() {
		return randomSeed;
	}

	public void setRandomSeed(long randomSeed) {
		this.randomSeed = randomSeed;
	}

	public long getSizeOnDisk() {
		return sizeOnDisk;
	}

	public void setSizeOnDisk(long sizeOnDisk) {
		this.sizeOnDisk = sizeOnDisk;
	}

	public int getSpawnX() {
		return spawnX;
	}

	public void setSpawnX(int spawnX) {
		this.spawnX = spawnX;
	}

	public int getSpawnY() {
		return spawnY;
	}

	public void setSpawnY(int spawnY) {
		this.spawnY = spawnY;
	}

	public int getSpawnZ() {
		return spawnZ;
	}

	public void setSpawnZ(int spawnZ) {
		this.spawnZ = spawnZ;
	}

	public int getStorageVersion() {
		return storageVersion;
	}

	public void setStorageVersion(int storageVersion) {
		this.storageVersion = storageVersion;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

    /**
     * 获取地图的dayCycleStopTime
     * @return  dayCycleStopTime
     * 如果是0.11版，则使用getDayCycleStopTimeNewVer
     */
	public Long getDayCycleStopTime() {
		return dayCycleStopTime;
	}

    /**
     * 设置地图的dayCycleStopTime
     * 如果是0.11版，则使用setDayCycleStopTimeNewVer
     */
	public void setDayCycleStopTime(long dayCycleStopTime) {
		this.dayCycleStopTime = dayCycleStopTime;
	}

    /**
     * 获取地图的DayCycleStopTime
     * @return  DayCycleStopTime
     * 如果是0.10以前的版本，则使用getDayCycleStopTime
     */
    public int getDayCycleStopTimeNewVer(){
        return DayCycleStopTime;
    }

    /**
     * 设置地图的DayCycleStopTime
     * 如果是0.10以前的版本，则使用setDayCycleStopTime
     */
    public void setDayCycleStopTimeNewVer(int dayCycleStopTime){
        this.DayCycleStopTime = dayCycleStopTime;
    }

	public boolean getSpawnMobs() {
		return spawnMobs;
	}

	public void setSpawnMobs(boolean spawnMobs) {
		this.spawnMobs = spawnMobs;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}

	public List<TileEntity> getTileEntities() {
		return tileEntities;
	}

	public void setTileEntities(List<TileEntity> tileEntities) {
		this.tileEntities = tileEntities;
	}

	public int getDimension() {
		return this.dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	public int getGenerator() {
		return this.generator;
	}

	public void setGenerator(int generator) {
		this.generator = generator;
	}

    public boolean isSpawnMobs() {
        return spawnMobs;
    }

    public int getLimitedworldoriginy() {
        return limitedworldoriginy;
    }

    public void setLimitedworldoriginy(int limitedworldoriginy) {
        this.limitedworldoriginy = limitedworldoriginy;
    }

    public int getLimitedworldoriginx() {
        return limitedworldoriginx;
    }

    public void setLimitedworldoriginx(int limitedworldoriginx) {
        this.limitedworldoriginx = limitedworldoriginx;
    }

    public int getLimitedworldoriginz() {
        return limitedworldoriginz;
    }

    public void setLimitedworldoriginz(int limitedworldoriginz) {
        this.limitedworldoriginz = limitedworldoriginz;
    }

    public long getCureationTime() {
        return cureationTime;
    }

    public void setCureationTime(long cureationTime) {
        this.cureationTime = cureationTime;
    }

    public long getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(long currentTick) {
        this.currentTick = currentTick;
    }

    public long getWorldStartCount() {
        return worldStartCount;
    }

    public void setWorldStartCount(long worldStartCount) {
        this.worldStartCount = worldStartCount;
    }
}
