package com.barinov.simpleplayer

import com.barinov.simpleplayer.domain.MusicFileIterator
import com.barinov.simpleplayer.domain.RandomAccessIterator
import com.barinov.simpleplayer.domain.RandomAccessIteratorImpl
import com.barinov.simpleplayer.domain.model.MusicFile


fun List<MusicFile>.musicFileIterator(): MusicFileIterator {
    return RandomAccessIteratorImpl(this)
}

inline fun <T> List<T>.indexOrNull(predicate: (T) -> Boolean): Int? {
    val iterator = this.listIterator()
    while (iterator.hasNext()) {
        if (predicate(iterator.next())) {
            return iterator.previousIndex()
        }
    }
    return null
}