package com.aranirahan.pembangkitkata
//
//fun Char.alphabetToInt(): Int {
//    val map = HashMap<Char, Int>().apply {
//        this['a'] = 1
//        this['b'] = 2
//        this['c'] = 3
//        this['d'] = 4
//        this['e'] = 5
//        this['f'] = 6
//        this['g'] = 7
//        this['h'] = 8
//        this['i'] = 9
//        this['j'] = 10
//        this['k'] = 11
//        this['l'] = 12
//        this['m'] = 13
//        this['n'] = 14
//        this['o'] = 15
//        this['p'] = 16
//        this['q'] = 17
//        this['r'] = 18
//        this['s'] = 19
//        this['t'] = 20
//        this['u'] = 21
//        this['v'] = 22
//        this['w'] = 23
//        this['x'] = 24
//        this['y'] = 25
//        this['z'] = 26
//    }
//
//    map.forEach {
//        if (it.key == this) {
//            return it.value
//        }
//    }
//    return 0
//}
//
//fun hitungFitness(countGen:Int, references:List<Gen>, references:List<Gen>,): Int {
//    var fitness = countGen * 25
//    var sigma = 0
//    for (i in 0 until countGen) {
//        sigma += Math.abs(acuan[i].getAngka() - gens[i].getAngka())
//    }
//    fitness -= sigma
//    return fitness
//}

fun List<Char?>.toListCharView():String{

    val builder = StringBuilder()
    this.forEach {
        builder.append(it)
    }
    return builder.toString()
}