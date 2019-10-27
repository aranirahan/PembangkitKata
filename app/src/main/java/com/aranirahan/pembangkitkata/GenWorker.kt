package com.aranirahan.pembangkitkata

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.aranirahan.pembangkitkata.helper.LiveDataHelper
import com.google.gson.Gson

class GenWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val liveDataHelper by lazy { LiveDataHelper.instance }
    private var firstPopulate = arrayListOf<Individu?>()
    private var rwsList = arrayListOf<Individu?>()
    private var crossOverList = arrayListOf<Individu?>()
    private var mutationList = arrayListOf<Individu?>()
    private var elitismeList = arrayListOf<Individu?>()
    private var bestList = arrayListOf<Individu?>()
    private lateinit var bestIndividualOfMutation: Individu
    private var isFirstTime = true

    private val referenceText = inputData.getString("reference")!!
    private val pm = inputData.getDouble("pm", 0.5)
    private val pc = inputData.getDouble("pc", 0.5)

    override fun doWork(): Result {
        Log.d("WordRef", referenceText)
        Log.d("WordSize", referenceText.toCharArray().size.toString())
        Log.d("WordPm", pm.toString())
        Log.d("WordPc", pc.toString())
        runNiceGeneticAlgorithm()
        return Result.success()
    }

    private fun runNiceGeneticAlgorithm() {
        val jmlIndividu = 10
        var totalFitness = 0
        val fitnessI = DoubleArray(jmlIndividu)
        val fitnessIndividu = DoubleArray(jmlIndividu)
        var i = arrayOfNulls<Individu>(jmlIndividu)
        val probabilitas = DoubleArray(jmlIndividu)

        bestIndividualOfMutation = Individu(referenceText.toCharArray().size)

        val acuan = arrayOfNulls<Gen>(referenceText.toCharArray().size)
        for ((index, charReference) in referenceText.toCharArray().withIndex()) {
            acuan[index] = Gen(charReference)
        }
        Proses.setAcuan(acuan)

        //woke individual
        for (a in 0 until jmlIndividu) {
            i[a] = Individu(referenceText.toCharArray().size)
            i[a]!!.bangkitkanIndividu()
        }

        var iterasi = 0
        do {
            iterasi++
            // Population
            firstPopulate.clear()
            for (a in 0 until jmlIndividu) {
                firstPopulate.add(i[a]!!)
                liveDataHelper.updatePopulate(Gson().toJson(firstPopulate))
            }

            //count fitness total
            for (a in 0 until jmlIndividu) {
                totalFitness += i[a]!!.getFitness()
            }

            //count probability for each individual
            for (a in 0 until jmlIndividu) {
                probabilitas[a] = i[a]!!.getFitness().toDouble() / totalFitness
            }

            //rws selection
            rwsList.clear()
            val seleksi = arrayOfNulls<Individu>(jmlIndividu)
            for (a in 0 until jmlIndividu) {
                seleksi[a] = Proses.RWS(i, probabilitas)
                rwsList.add(i[a]!!)
                liveDataHelper.updateRws(Gson().toJson(rwsList))
            }

            //cross over
            crossOverList.clear()
            val individu = arrayOfNulls<Individu>(jmlIndividu)
            var anak: Array<Individu>
            run {
                var a = 0
                while (a < jmlIndividu) {
                    anak = Proses.crossOver(
                        seleksi[a],
                        seleksi[a + 1],
                        pc,
                        referenceText.toCharArray().size
                    )
                    individu[a] = anak[0]
                    individu[a + 1] = anak[1]
                    crossOverList.add(individu[a])
                    crossOverList.add(individu[a + 1])
                    liveDataHelper.updateCrossOver(Gson().toJson(crossOverList))
                    a += 2
                }
            }

            //mutation
            mutationList.clear()
            for (a in 0 until jmlIndividu) {
                individu[a] = Proses.mutasi(individu[a], pm)
                mutationList.add(individu[a])
                liveDataHelper.updateMutation(Gson().toJson(mutationList))
            }

            //Elitisme
            elitismeList.clear()
            for (a in 0 until jmlIndividu) {
                fitnessI[a] = i[a]!!.getFitness().toDouble()
            }
            for (a in 0 until jmlIndividu) {
                fitnessIndividu[a] = individu[a]!!.getFitness().toDouble()
            }

            if (Proses.getMax(fitnessIndividu)[0] < Proses.getMax(
                    fitnessI
                )[0]) {
                individu[Proses.getMin(fitnessIndividu)[1].toInt()] = i[Proses.getMax(
                    fitnessI
                )[1].toInt()]
            }

            i = individu
            for (a in 0 until jmlIndividu) {
                fitnessI[a] = i[a]!!.getFitness().toDouble()
            }

            for (a in i.indices) {
                elitismeList.add(i[a])
                liveDataHelper.updateElitisme(Gson().toJson(elitismeList))
                if (isFirstTime) {
                    liveDataHelper.updateFitnessRef(acuan.size.toDouble() * 25)
                    bestList.add(i[a])
                    bestIndividualOfMutation = i[a]!!
                    isFirstTime = false
                }
                if (i[a]!!.getFitness() > bestIndividualOfMutation.getFitness()) {
                    bestList.add(i[a])
                    bestIndividualOfMutation = i[a]!!
                    liveDataHelper.updateBestIndiviual(Gson().toJson(bestList))
                }
            }
            liveDataHelper.updateGeneration(iterasi)
        } while (Proses.getMax(fitnessI)[0] != acuan.size.toDouble() * 25)
    }
}