package com.barinov.simpleplayer.domain

interface RandomAccessIterator<T>: Iterator<T> {

    fun setCurrentPosition(position: Int)

    fun getCurrentPosition(): Int

    fun previous(): T

    fun hasPrevious(): Boolean

}