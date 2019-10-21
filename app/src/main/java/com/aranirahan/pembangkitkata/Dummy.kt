//package com.aranirahan.pembangkitkata
//
//class Dummy {
//
//    val jml_gen = 0
//    val acuan = arrayListOf<Gen>()
//    val gens = arrayListOf<Gen>()
//    val jml_individu = 0
//
//
//    var total_fitness = 0
//    var probabilitas = DoubleArray(jml_individu)
//
//    fun hitungFitness(): Int {
//        var fitness = jml_gen * 25
//        var sigma = 0
//        for (i in 0 until jml_gen) {
//            sigma += Math.abs(acuan[i].getAngka() - gens[i].getAngka())
//        }
//        fitness -= sigma
//        return fitness
//    }
//
//    fun bangkitkan() {
//        gens = arrayListOf<Gen>(jml_gen)
//        for (i in 0 until jml_gen) {
//            val r = (Math.random() * 26 + 1).toInt()
//            gens[i] = Gen(r)
//        }
//    }
//
//    fun totalFitnes(){
//        for (i in 0 until jml_individu) {
//            total_fitness += individus[i].hitung_fitness()
//        }
//    }
//
//    fun probabilitas(){
//        for (i in 0 until jml_individu) {
//            probabilitas[i] = individus[i].hitung_fitness() as Double / total_fitness
//        }
//    }
//
//    fun rouleteeWheelSelection(){
//        val r = Math.random()
//        var i: Byte = 0
//        var sum = probabilitas[i]
//        while (sum < r) {
//            i++
//            sum += probabilitas[i]
//            if (i == jml_individu - 1) break
//        }
//        return individus[i]
//    }
//
//    fun mutasi(induk: Individual, pm: Double): Individual {
//        val jml_gen = induk.get_jml_gen()
//        for (i in 0 until jml_gen) {
//            val r = Math.random()
//            //System.out.println("r ke-"+i+" -> "+r+" ");
//            if (r <= pm) {
//                val angka_baru = (Math.random() * 25 + 1).toInt()
//                induk.get_gens()[i].set_angka(angka_baru)
//            }
//        }
//        return induk
//    }
//}