package com.daksh.scalpelandroid.storage.room.dao

import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Update
import io.reactivex.Flowable

interface BaseDao<T> {

    @Insert
    fun insert(vararg t: T)

    @Insert
    @JvmSuppressWildcards
    fun insert(t: List<T>)

    fun getAll(): List<T>

    fun getAllRx(): Flowable<List<T>>

    @Update
    fun update(vararg t: T)

    @Update
    @JvmSuppressWildcards
    fun update(t: List<T>)

    @Delete
    fun delete(vararg t: T)

    @Delete
    @JvmSuppressWildcards
    fun delete(t: List<T>)
}
