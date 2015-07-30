/**
 * 
 */
package com.mckuai.mctools.io.db;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * @author kyly
 *leveldb的key的结构
 */
public class DBKey
{
	public static final int CHUNK = 48;           	       //此类型为terrain data
	public static final int TILE_ENTITY = 49;		       //此类型为tile entity data
    public static final int ENTITY = 50;         	       //实体数据
    public static final int PLACEHOLDER = 118;      //1bit的数据
    private int x;                                                    //数据块的x索引b
    private int z;                                                    //数据块的z索引c
    private int type;                                               // 数据类型

    private static final  String TAG = "DBKey";
	  
    public DBKey()
    {
        this(0, 0, 0);
    }

    /**
     * 构造一个DBKey
     * @param x 数据块的x索引
     * @param z 数据块的z索引
     * @param type  数据类型
     */
	  public DBKey(int x, int z, int type)
	  {
          this.x = x;
          this.z = z;
          this.type = type;
	  }
	  
	  public DBKey(DBKey key)
	  {
	    this(key.x, key.z, key.type);
	  }


	  public boolean equals(Object object)
	  {
          if (!(object instanceof DBKey)) {
              return false;
          }
          else {
              DBKey key = (DBKey)object;
              return (key.getType() == type) && (key.getX() == x) && (key.getZ() == z);
          }
	  }
	  

	  
	  public int getType()
	  {
	    return this.type;
	  }
	  
	  public int getX()
	  {
	    return this.x;
	  }
	  
	  public int getZ()
	  {
	    return this.z;
	  }
	  
	  public int hashCode()
	  {
	    return ((this.x + 31) * 31 + this.z) * 31 + this.type;
	  }
	  
	  public DBKey setType(int type)
	  {
	    this.type = type;
	    return this;
	  }
	  
	  public DBKey setX(int x)
	  {
	    this.x = x;
	    return this;
	  }
	  
	  public DBKey setZ(int z)
	  {
	    this.z = z;
	    return this;
	  }

    /**
     * 将一个byte数组转换成key
     * @param bytes 要转换的byte数组
     */
    public void fromBytes(byte[] bytes)
    {
        this.x = (bytes[0] | bytes[1] << 8 | bytes[2] << 16 | bytes[3] << 24);
        this.z = (bytes[4] | bytes[5] << 8 | bytes[6] << 16 | bytes[7] << 24);
        this.type = (bytes[8] & 0xFF);
    }

	/**
	 * 将key转换成一个9个字的字段
	 * @return
	 */
	  public byte[] toBytes()
	  {
	    try
	    {
	      ByteArrayOutputStream outputStream = new ByteArrayOutputStream(9);
	      DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
	      dataOutputStream.writeInt(Integer.reverseBytes(this.x));
	      dataOutputStream.writeInt(Integer.reverseBytes(this.z));
	      dataOutputStream.writeByte(this.type);
	      
	      return (byte[])outputStream.toByteArray();
	    }
	    catch (Exception e)
	    {
	      //throw new RuntimeException(e);
            Log.e(TAG,"将DBKey转换成btye数组时失败，原因："+e.getLocalizedMessage());
            return null;
	    }
	  }
	  
	  public String toString()
	  {
	    return getClass().getSimpleName() + ": " + this.x + "_" + this.z + "_" + this.type;
	  }
}
