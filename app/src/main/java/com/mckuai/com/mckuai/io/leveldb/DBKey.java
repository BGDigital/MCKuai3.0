/**
 * 
 */
package com.mckuai.com.mckuai.io.leveldb;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * @author kyly
 *
 */
public class DBKey
{
	public static final int CHUNK = 48;
	  public static final int ENTITY = 50;
	  public static final int PLACEHOLDER = 118;
	  public static final int TILE_ENTITY = 49;
	  private int a;
	  private int b;
	  private int c;
	  
	  public DBKey()
	  {
	    this(0, 0, 0);
	  }
	  
	  public DBKey(int paramInt1, int paramInt2, int paramInt3)
	  {
	    this.b = paramInt1;
	    this.c = paramInt2;
	    this.a = paramInt3;
	  }
	  
	  public DBKey(DBKey paramDBKey)
	  {
	    this(paramDBKey.b, paramDBKey.c, paramDBKey.a);
	  }
	  
	  public boolean equals(Object paramObject)
	  {
	    if (!(paramObject instanceof DBKey)) {
	    	return false;
	    }
	    else {
			DBKey key = (DBKey)paramObject;
			return (key.getType() == a) && (key.getX() == b) && (key.getZ() == c);
		}
	  }
	  
	  public void fromBytes(byte[] paramArrayOfByte)
	  {
	    this.b = (paramArrayOfByte[0] | paramArrayOfByte[1] << 8 | paramArrayOfByte[2] << 16 | paramArrayOfByte[3] << 24);
	    this.c = (paramArrayOfByte[4] | paramArrayOfByte[5] << 8 | paramArrayOfByte[6] << 16 | paramArrayOfByte[7] << 24);
	    this.a = (paramArrayOfByte[8] & 0xFF);
	  }
	  
	  public int getType()
	  {
	    return this.a;
	  }
	  
	  public int getX()
	  {
	    return this.b;
	  }
	  
	  public int getZ()
	  {
	    return this.c;
	  }
	  
	  public int hashCode()
	  {
	    return ((this.b + 31) * 31 + this.c) * 31 + this.a;
	  }
	  
	  public DBKey setType(int paramInt)
	  {
	    this.a = paramInt;
	    return this;
	  }
	  
	  public DBKey setX(int paramInt)
	  {
	    this.b = paramInt;
	    return this;
	  }
	  
	  public DBKey setZ(int paramInt)
	  {
	    this.c = paramInt;
	    return this;
	  }
	  
	  public byte[] toBytes()
	  {
	    try
	    {
	      ByteArrayOutputStream outputStream = new ByteArrayOutputStream(9);
	      DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
	      dataOutputStream.writeInt(Integer.reverseBytes(this.b));
	      dataOutputStream.writeInt(Integer.reverseBytes(this.c));
	      dataOutputStream.writeByte(this.a);
//	      outputStream = outputStream.toByteArray();
	      
	      return (byte[])outputStream.toByteArray();
	    }
	    catch (Exception localIOException)
	    {
	      throw new RuntimeException(localIOException);
	    }
	  }
	  
	  public String toString()
	  {
	    return getClass().getSimpleName() + ": " + this.b + "_" + this.c + "_" + this.a;
	  }
}
