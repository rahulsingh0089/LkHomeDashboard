/*
 * Copyright 2011 WonderMedia Technologies, Inc. All Rights Reserved. 
 *  
 * This PROPRIETARY SOFTWARE is the property of WonderMedia Technologies, Inc. 
 * and may contain trade secrets and/or other confidential information of 
 * WonderMedia Technologies, Inc. This file shall not be disclosed to any third party, 
 * in whole or in part, without prior written consent of WonderMedia. 
 *  
 * THIS PROPRIETARY SOFTWARE AND ANY RELATED DOCUMENTATION ARE PROVIDED AS IS, 
 * WITH ALL FAULTS, AND WITHOUT WARRANTY OF ANY KIND EITHER EXPRESS OR IMPLIED, 
 * AND WonderMedia TECHNOLOGIES, INC. DISCLAIMS ALL EXPRESS OR IMPLIED WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT.  
 */
package lenkeng.com.welcome.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * LruCache, note there are 2 map, mLruMap for strong reference 
 * mWeakMap is for weak reference.
 */
public final class LruCache<K, V> {
    private final HashMap<K, V> mLruMap;
    private final HashMap<K, Entry<K, V>> mWeakMap =
            new HashMap<K, Entry<K, V>>();
	private ReferenceQueue<V> mQueue = new ReferenceQueue<V>();
	private OnWeakRemoveListener mOnWeakRemoveListener;

	public interface OnWeakRemoveListener {
		public void onWeakRemove(Object object);
	}

    @SuppressWarnings("serial")
	public LruCache(final int capacity) {
        mLruMap = new LinkedHashMap<K, V>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;           
            }
        };
    }

    private static class Entry<K, V> extends WeakReference<V> {
        K mKey;

        @SuppressWarnings("rawtypes")
		public Entry(K key, V value, ReferenceQueue queue) {
            super(value, queue);
            mKey = key;
        }
    }

    @SuppressWarnings("unchecked")
    private void cleanUpWeakMap() {
		Entry<K, V> entry = (Entry<K, V>) mQueue.poll();
		while (entry != null) {
			if (mOnWeakRemoveListener != null) {
				mOnWeakRemoveListener.onWeakRemove(entry.get());
			}
            mWeakMap.remove(entry.mKey);
            entry = (Entry<K, V>) mQueue.poll();
        }
    }

	public void setWeakRemoveListener(OnWeakRemoveListener onWeakRemoveListener) {
		mOnWeakRemoveListener = onWeakRemoveListener;
	}
    public synchronized V put(K key, V value) {
        cleanUpWeakMap();
        mLruMap.put(key, value);
        Entry<K, V> entry = mWeakMap.put(
                                        key, new Entry<K, V>(key, value, mQueue));
        return entry == null ? null : entry.get();
    }

    public synchronized V get(K key) {
        cleanUpWeakMap();
        V value = mLruMap.get(key);
        if (value != null) return value;
        Entry<K, V> entry = mWeakMap.get(key);
        return entry == null ? null : entry.get();
    }

    public synchronized void clear() {
        mLruMap.clear();
        mWeakMap.clear();
        mQueue = new ReferenceQueue<V>();
    }

    public synchronized Collection<V> values()
    {
        return mLruMap.values();
    }

    public synchronized Set<K> keySet()
    {
        return mLruMap.keySet();
    }

    public synchronized void remove(K key){
        cleanUpWeakMap();
        mLruMap.remove(key);
        mWeakMap.remove(key);
    }

    public int getSize(){
        return mLruMap.size();
    }
}
