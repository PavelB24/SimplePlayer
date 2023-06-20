package com.barinov.simpleplayer.base

interface ItemInteractor<T> {

    fun onClick(item: T)

    fun onLongClick(item: T): Boolean
}