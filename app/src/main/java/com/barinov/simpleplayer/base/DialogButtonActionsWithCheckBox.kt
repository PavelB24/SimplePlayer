package com.barinov.simpleplayer.base

interface DialogButtonActionsWithCheckBox<T> {

    fun onPositiveButtonClicked(state: T)

    fun onNegativeButtonClicked(state: T)


}