package com.aranirahan.pembangkitkata

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.activity_main.btn_generate
import kotlinx.android.synthetic.main.activity_main.et_reference
import kotlinx.android.synthetic.main.activity_main.load
import kotlinx.android.synthetic.main.activity_main.tv_best_individual
import kotlinx.android.synthetic.main.activity_main.tv_count_fitness
import kotlinx.android.synthetic.main.activity_main.tv_count_fitness_cross
import kotlinx.android.synthetic.main.activity_main.tv_count_fitness_mutation
import kotlinx.android.synthetic.main.activity_main.tv_count_fitness_rws
import kotlinx.android.synthetic.main.activity_main.tv_cross_over
import kotlinx.android.synthetic.main.activity_main.tv_first_populate
import kotlinx.android.synthetic.main.activity_main.tv_generation
import kotlinx.android.synthetic.main.activity_main.tv_mutation
import kotlinx.android.synthetic.main.activity_main.tv_rws
import kotlinx.android.synthetic.main.activity_main.tv_worst_individual
import kotlin.concurrent.thread
import kotlin.math.abs
import kotlin.random.Random
import androidx.lifecycle.Observer
import com.aranirahan.pembangkitkata.helper.LiveDataHelper
import com.aranirahan.pembangkitkata.helper.toListCharView
import com.aranirahan.pembangkitkata.model.Gen
import com.aranirahan.pembangkitkata.model.Individual
import kotlinx.android.synthetic.main.activity_main.tv_mt

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private val reference = arrayListOf<Gen>()
    private var countGenReference = 0
    private val firstPopulate = arrayListOf<Individual>()
    private val rwsList = arrayListOf<Individual>()
    private val crossOverList = arrayListOf<Individual?>()
    private val mutationList = arrayListOf<Individual?>()
    private var generation = 0

    private var bestIndividual = Individual()
    private var worstIndividual = Individual()

    private var bestIndividualOfMutation = Individual()
    private var worstIndividualOfMutation = Individual()
    private var onChange: () -> Unit = {
        initView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBtnGenerate()
    }

    private fun setupBtnGenerate() {
        btn_generate.setOnClickListener {
            load.visibility = View.VISIBLE
//            runNiceGeneticAlgorithm()
            runGeneticWork()
            observeWorker()
        }
    }

    private fun observeWorker() {
        LiveDataHelper.instance.observeIndividual()
            .observe(this, Observer {
                bestIndividualOfMutation = it
                tv_best_individual.text =
                        "\nBest Individual ${bestIndividualOfMutation.gens.map { it?.huruf }.toListCharView()} " +
                        " -  fitness = ${bestIndividualOfMutation.countFitness}\n"
            })
        LiveDataHelper.instance.observeMutation()
            .observe(this, Observer {
                tv_mt.text = it
            })
        LiveDataHelper.instance.observeGeneration()
            .observe(this, Observer {
                tv_generation.text = "Generation = $it"
            })
    }

    private fun runGeneticWork() {
        Log.d("runGeneticWork", "run")
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val data = Data.Builder()
        data.putString("reference", et_reference.text.toString())

        val workRequest = OneTimeWorkRequest.Builder(GeneticWorker::class.java)
            .setConstraints(constraint)
            .setInputData(data.build())
            .build()

        val workManager = WorkManager.getInstance()
        workManager.enqueue(workRequest)
    }

    private fun runNiceGeneticAlgorithm() {
        thread(start = true) {

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


            if (bestIndividualOfMutation.countFitness != 275) {
                runOnUiThread {
                    tv_generation.text = "Generation = $generation"
                    tv_best_individual.text =
                        "Best Individual ${bestIndividualOfMutation.gens.map { it?.huruf }.toListCharView()} " +
                                " -  fitness = ${bestIndividualOfMutation.countFitness}"
                    onChange.invoke()
                }
                runNiceGeneticAlgorithm()
            } else {
                runOnUiThread {
                    initView()
                }
                return@thread
            }
        }
    }

    private fun initView() {
        load.visibility = View.GONE

        var firstPopulateText = ""

        //first populate
        firstPopulate.forEach {
            firstPopulateText += it.gens.map { it?.huruf }.toListCharView() + "\n"
        }
        tv_first_populate.text = firstPopulateText

        var countFitnessText = ""
        firstPopulate.forEach {
            countFitnessText += "fitness = " + it.countFitness.toString() + "\n"
        }
        tv_count_fitness.text = countFitnessText

        tv_best_individual.text =
            "Best Individual ${bestIndividual.gens.map { it?.huruf }.toListCharView()} " +
                    " -  fitness = ${bestIndividual.countFitness}"

        tv_worst_individual.text =
            "Worst Individual ${worstIndividual.gens.map { it?.huruf }.toListCharView()} " +
                    " -  fitness = ${worstIndividual.countFitness}"

        //rws
        var rwsText = ""
        rwsList.forEach {
            rwsText += it.gens.map { it?.huruf }.toListCharView() + "\n"
        }
        tv_rws.text = rwsText

        var countFitnessTextRws = ""
        rwsList.forEach {
            countFitnessTextRws += "fitness = " + it.countFitness.toString() + "\n"
        }
        tv_count_fitness_rws.text = countFitnessTextRws

        //cross over
        var crossOverText = ""
        crossOverList.forEach {
            crossOverText += it?.gens?.map { it?.huruf }?.toListCharView() + "\n"
        }
        tv_cross_over.text = crossOverText

        var countFitnessTextCross = ""
        crossOverList.forEach {
            countFitnessTextCross += "fitness = " + it?.countFitness.toString() + "\n"
        }
        tv_count_fitness_cross.text = countFitnessTextCross

        //mutation
        var mutationText = ""
        mutationList.forEach {
            mutationText += it?.gens?.map { it?.huruf }?.toListCharView() + "\n"
        }
        tv_mutation.text = mutationText

        var countFitnessTextMutation = ""
        mutationList.forEach {
            countFitnessTextMutation += "fitness = " + it?.countFitness.toString() + "\n"
        }
        tv_count_fitness_mutation.text = countFitnessTextMutation


        tv_best_individual.text =
            "Best Individual ${bestIndividualOfMutation.gens.map { it?.huruf }.toListCharView()} " +
                    " -  fitness = ${bestIndividualOfMutation.countFitness}"
        tv_worst_individual.text =
            "Worst Individual ${worstIndividualOfMutation.gens.map { it?.huruf }.toListCharView()} " +
                    " -  fitness = ${worstIndividualOfMutation.countFitness}"
    }

    private fun setupGeneration() {
        generation += 1
    }

    private fun setupReferences() {
        val referenceText = et_reference.text.toString()
        referenceText.toCharArray().forEach {
            reference.add(Gen(it))
        }
        countGenReference = reference.size
    }

    private fun setupFirstPopulate() {
        repeat(10) {
            val wokenList = countGenReference.awakeIndividual().toList()
            val countFitness = reference.countFitness(wokenList)
            firstPopulate.add(Individual(wokenList, countFitness))
        }
    }

    private fun setupBestIndividual() {
        var maxFitness = 0
        firstPopulate.forEach {
            if (maxFitness < it.countFitness) {
                maxFitness = it.countFitness
                bestIndividual = it
            }
        }
    }

    private fun setupWorstIndividual() {
        var minFitness = Int.MAX_VALUE
        firstPopulate.forEach {
            if (minFitness > it.countFitness) {
                minFitness = it.countFitness
                worstIndividual = it
            }
        }
    }

    private fun setupRws() {

        repeat(10) {
            with(firstPopulate) {
                rwsList.add(rouleteeWheelSelection(getProbability(getTotalFitness())))
            }
        }
    }

    private fun setupCrossOver() {
        var index = 0
        repeat(5) {
            val generateCrossOver = crossoverUniform(rwsList[index], rwsList[index + 1], Math.random())
            crossOverList.addAll(generateCrossOver.toList())
            index += 2
        }
    }

    private fun setupMutation() {
        crossOverList.forEach {
            val generateMutation = mutation(it!!, Random.nextDouble(0.0, 1.0))
            mutationList.add(generateMutation)
        }
    }

    private fun setupBestIndividualOfMutation() {
        var maxFitness = 0
        mutationList.forEach {
            if (maxFitness < it!!.countFitness) {
                maxFitness = it.countFitness
                bestIndividualOfMutation = it
            }
        }
    }

    private fun setupWorstIndividualOfMutation() {
        var minFitness = Int.MAX_VALUE
        mutationList.forEach {
            if (minFitness > it!!.countFitness) {
                minFitness = it.countFitness
                worstIndividualOfMutation = it
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
    private fun List<Individual>.rouleteeWheelSelection(doubleArray: DoubleArray): Individual {
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

    private fun List<Individual>.getProbability(totalFitness: Int): DoubleArray {
        val probabilities = DoubleArray(this.size)
        for (i in this.indices) {
            probabilities[i] = this[i].countFitness.toDouble() / totalFitness
        }
        return probabilities
    }

    private fun List<Individual>.getTotalFitness(): Int {
        var totalAllFitness = 0
        for (individual in this) {
            totalAllFitness += individual.countFitness
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
            //System.out.println("r ke-"+i+" -> "+r+" ");
            if (r <= pc) {
                childA.gens[i]?.huruf = parentB.gens[i]?.huruf!!
                childB.gens[i]?.huruf = parentA.gens[i]?.huruf!!
            }
        }
        val lastValue = arrayOfNulls<Individual>(2)
        lastValue[0] = childA
        lastValue[1] = childB
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
