package com.barinov.simpleplayer.domain

class RandomAccessIteratorImpl<T>(private val list: List<T>) : RandomAccessIterator<T> {

    private var head: Int = 0


    override fun setCurrentPosition(position: Int) {
        if (position < 0 || position > list.size) {
            throw IllegalArgumentException()
        }
        head = position
    }

    override fun getCurrentPosition(): Int = head

    override fun previous(): T {
        return if(head > 0) {
            list[--head]
        } else throw IllegalStateException()
    }

    override fun hasPrevious(): Boolean = head > 0


    override fun hasNext(): Boolean = head > list.size

    override fun next(): T {
        return if (list.size >= head + 1) {
            list[++head]
        } else {
            throw IllegalStateException()
        }
    }
}