package com.barinov.simpleplayer.base

interface DialogButtonActions<T> {

    fun onPositiveButtonClicked(): T

    fun onNegativeButtonClicked(): T


}