package com.aranirahan.pembangkitkata

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import kotlin.math.abs
import kotlin.random.Random

/**
 * Run a work to show a notification on a background thread by the {@link WorkManger}.
 */
class GeneticWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val liveDataHelper by lazy { LiveDataHelper.instance }

    private var reference = arrayListOf<Gen>()
    private var countGenReference = 0
    private var firstPopulate = arrayListOf<Individual?>()
    private var rwsList = arrayListOf<Individual?>()
    private var crossOverList = arrayListOf<Individual?>()
    private var mutationList = arrayListOf<Individual?>()
    private var generation = 0

    private val bestIndividual by lazy { Individual() }
    private val worstIndividual by lazy { Individual() }

    private val bestIndividualOfMutation by lazy { Individual() }
    private val worstIndividualOfMutation by lazy { Individual() }

    private val referenceText = inputData.getString("reference")!!

    override fun doWork(): Result {

        runNiceGeneticAlgorithm()

        return Result.success()
    }

    private fun runNiceGeneticAlgorithm() {
        while (bestIndividualOfMutation.countFitness != 275) {
            setupGeneration()
            setupReferences()
            setupFirstPopulate()
            setupBestIndividual()
            setupWorstIndividual()
            setupRws()
            setupCrossOver()
            setupMutation()
            setupBestIndividualOfMutation()
            setupWorstIndividualOfMutation()
            liveDataHelper.updateGeneration(generation)
            liveDataHelper.updateIndividual(bestIndividualOfMutation)

            val bestIndividualFinal = "\nBest Individual " +
                        "${bestIndividualOfMutation.gens.map { it?.huruf }.toListCharView()} " +
                        " -  fitness = ${bestIndividualOfMutation.countFitness}\n"
            Log.d("bestIndividual", bestIndividualFinal)
        }
    }

    private fun setupGeneration() {
        generation += 1
    }

    private fun setupReferences() {
        val referenceText = referenceText
        Log.d("referenceText", referenceText)
        reference.clear()
        referenceText.toCharArray().forEach {
            reference.add(Gen(it))
        }
        countGenReference = reference.size
    }

    private fun setupFirstPopulate() {
        if (mutationList.isNullOrEmpty()) {
            firstPopulate.clear()
            repeat(10) {
                Log.d("countGenReference", countGenReference.toString())
                val wokenList = countGenReference.awakeIndividual().toList()
                val countFitness = reference.countFitness(wokenList)
                Log.d("wokenList", Gson().toJson(wokenList.map { it?.huruf }))
                firstPopulate.add(Individual(wokenList, countFitness))
            }
        } else {
            firstPopulate.clear()
            firstPopulate.addAll(mutationList)
        }
    }

    private fun setupBestIndividual() {
        var maxFitness = 0
        firstPopulate.forEach {
            if (maxFitness < it!!.countFitness) {
                maxFitness = it.countFitness
                bestIndividual.gens = it.gens
                bestIndividual.countFitness = it.countFitness
            }
        }
    }

    private fun setupWorstIndividual() {
        var minFitness = Int.MAX_VALUE
        firstPopulate.forEach {
            if (minFitness > it!!.countFitness) {
                minFitness = it.countFitness
                worstIndividual.gens = it.gens
                worstIndividual.countFitness = it.countFitness
            }
        }
    }

    private fun setupRws() {
        rwsList.clear()
        repeat(10) {
            with(firstPopulate) {
                rwsList.add(rouletteWheelSelection(getProbabilities(getTotalFitness())))
            }
        }
    }

    //TODO - what individuals can execute cross over
    private fun setupCrossOver() {
        crossOverList.clear()
        var index = 0
        repeat(5) {
//            val generateCrossOver = crossoverUniform(rwsList[index]!!, rwsList[index + 1]!!, Math.random())
            val generateCrossOver = crossoverUniform(rwsList[index]!!, rwsList[index + 1]!!, Random.nextDouble(0.6,0.9))
            crossOverList.addAll(generateCrossOver.toList())
            index += 2
        }

//        crossOverList.clear()
//        val pc = Random.nextDouble(0.6, 0.9)
//        val random0to1List = arrayListOf<Double>()
//        repeat(10) {
//            val random0to1 = Random.nextDouble(0.0, 1.0)
//            random0to1List.add(random0to1)
//        }
//
//        random0to1List.forEach {
//            if(it == pc){
//
//            }
//        }
    }

    private fun setupMutation() {
        mutationList.clear()
        crossOverList.forEach {
            val generateMutation = mutation(it!!, Random.nextDouble(0.0, 1.0)).apply {
                countFitness = reference.countFitness(mutation(it, Random.nextDouble(0.0, 1.0)).gens)
            }

            Log.d("countFitnessX", generateMutation.countFitness.toString())

            mutationList.add(generateMutation)
        }
    }

    private fun setupBestIndividualOfMutation() {
        mutationList.forEach {
            if (bestIndividualOfMutation.countFitness < it!!.countFitness) {
                bestIndividualOfMutation.gens = it.gens
                bestIndividualOfMutation.countFitness = it.countFitness
            }
        }
    }

    private fun setupWorstIndividualOfMutation() {
        var minFitness = Int.MAX_VALUE
        mutationList.forEach {
            if (minFitness > it!!.countFitness) {
                minFitness = it.countFitness
                worstIndividualOfMutation.gens = it.gens
                worstIndividualOfMutation.countFitness = it.countFitness
            }
        }
    }

    //===== AWAKE =====//
    private fun Int.awakeIndividual(): Array<Gen?> {
        val gens = arrayOfNulls<Gen>(this)
        for (i in 0 until this) {
            val r = (Math.random() * 26 + 1).toInt()
            gens[i] = Gen(r)
        }
        return gens
    }

    //===== FITNESS =====//
    private infix fun List<Gen>.countFitness(samples: List<Gen?>): Int {
        val countGen = this.size
        var fitness = countGen * 25
        var sigma = 0
        for (i in 0 until countGen) {
            sigma += abs(this[i].angka - samples[i]!!.angka)
        }
        fitness -= sigma
        return fitness
    }

    //===== RWS =====//
    private fun List<Individual?>.rouletteWheelSelection(doubleArray: DoubleArray): Individual? {
        val r = Math.random()
        var i = 0
        var sum = doubleArray[i]
        while (sum < r) {
            i++
            sum += doubleArray[i]
            if (i == this.size - 1) break
        }
        return this[i]
    }

    private fun List<Individual?>.getProbabilities(totalFitness: Int): DoubleArray {
        val probabilities = DoubleArray(this.size)
        for (i in this.indices) {
            probabilities[i] = this[i]?.countFitness!!.toDouble() / totalFitness
        }
        return probabilities
    }

    private fun List<Individual?>.getTotalFitness(): Int {
        var totalAllFitness = 0
        for (individual in this) {
            totalAllFitness += individual!!.countFitness
        }
        return totalAllFitness
    }

    //===== CROSSOVER UNIFORM =====//
    //TODO - ask pc??
    private fun crossoverUniform(parentA: Individual, parentB: Individual, pc: Double): Array<Individual?> {
        val childA = parentA
        val childB = parentB

        for (i in parentA.gens.indices) {
            val r = Math.random()
            //    val r = Random.nextDouble(0.6,0.9)
            //System.out.println("r ke-"+i+" -> "+r+" ");
            if (r <= pc) {
                childA.gens[i]?.huruf = parentB.gens[i]?.huruf!!
                childB.gens[i]?.huruf = parentA.gens[i]?.huruf!!
            }
        }
        val lastValue = arrayOfNulls<Individual>(2)
        lastValue[0] = childA.apply { countFitness = reference.countFitness(childA.gens) }
        lastValue[1] = childB.apply { countFitness = reference.countFitness(childB.gens) }
        return lastValue
    }

    //===== MUTATION =====//
    //TODO - ask pm??
    private fun mutation(parent: Individual, pm: Double): Individual {
        val countGen = parent.gens.size
        for (i in 0 until countGen) {
            val r = Math.random()
            //System.out.println("r ke-"+i+" -> "+r+" ");
            if (r <= pm) {
                val newNumberGen = (Math.random() * 25 + 1).toInt()
                parent.gens[i]?.angka = newNumberGen
            }
        }
        return parent
    }
}