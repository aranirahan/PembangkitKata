package com.aranirahan.pembangkitkata.helper

fun List<Char?>.toListCharView():String{

    val builder = StringBuilder()
    this.forEach {
        builder.append(it)
    }
    return builder.toString()
}