package com.litl.leveldb;

/**
 * Created by kyly on 2015/7/6.
 */
public class DBIterator extends  NativeObject {
    DBIterator(long iterPtr) {
        super(iterPtr);
    }

    @Override
    protected void closeNativeObject(long ptr) {
        nativeDestroy(ptr);
    }

    public void seekToFirst() {
        assertOpen("DBIterator is closed");
        nativeSeekToFirst(mPtr);
    }

    public void seekToLast() {
        assertOpen("DBIterator is closed");
        nativeSeekToLast(mPtr);
    }

    public void seek(byte[] target) {
        assertOpen("DBIterator is closed");
        if (target == null) {
            throw new IllegalArgumentException();
        }
        nativeSeek(mPtr, target);
    }

    public boolean isValid() {
        assertOpen("DBIterator is closed");
        return nativeValid(mPtr);
    }

    public void next() {
        assertOpen("DBIterator is closed");
        nativeNext(mPtr);
    }

    public void prev() {
        assertOpen("DBIterator is closed");
        nativePrev(mPtr);
    }

    public byte[] getKey() {
        assertOpen("DBIterator is closed");
        return nativeKey(mPtr);
    }

    public byte[] getValue() {
        assertOpen("DBIterator is closed");
        return nativeValue(mPtr);
    }

    private static native void nativeDestroy(long ptr);

    private static native void nativeSeekToFirst(long ptr);

    private static native void nativeSeekToLast(long ptr);

    private static native void nativeSeek(long ptr, byte[] key);

    private static native boolean nativeValid(long ptr);

    private static native void nativeNext(long ptr);

    private static native void nativePrev(long ptr);

    private static native byte[] nativeKey(long dbPtr);

    private static native byte[] nativeValue(long dbPtr);
}
