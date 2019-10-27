package com.aranirahan.pembangkitkata

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.aranirahan.pembangkitkata.helper.LiveDataHelper
import com.aranirahan.pembangkitkata.helper.toListCharView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.btn_generate
import kotlinx.android.synthetic.main.activity_main.et_reference
import kotlinx.android.synthetic.main.activity_main.load
import kotlinx.android.synthetic.main.activity_main.pc
import kotlinx.android.synthetic.main.activity_main.pm
import kotlinx.android.synthetic.main.activity_main.tv_best_individual
import kotlinx.android.synthetic.main.activity_main.tv_best_individualfitness
import kotlinx.android.synthetic.main.activity_main.tv_count_fitness
import kotlinx.android.synthetic.main.activity_main.tv_count_fitness_cross
import kotlinx.android.synthetic.main.activity_main.tv_count_fitness_elitisme
import kotlinx.android.synthetic.main.activity_main.tv_count_fitness_mutation
import kotlinx.android.synthetic.main.activity_main.tv_count_fitness_rws
import kotlinx.android.synthetic.main.activity_main.tv_cross_over
import kotlinx.android.synthetic.main.activity_main.tv_elitisme
import kotlinx.android.synthetic.main.activity_main.tv_first_populate
import kotlinx.android.synthetic.main.activity_main.tv_fitnes_ref
import kotlinx.android.synthetic.main.activity_main.tv_generation
import kotlinx.android.synthetic.main.activity_main.tv_mutation
import kotlinx.android.synthetic.main.activity_main.tv_rws
import kotlinx.android.synthetic.main.activity_main.v_line

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private var firstPopulate = arrayListOf<Individu?>()
    private var rwsList = arrayListOf<Individu?>()
    private var crossOverList = arrayListOf<Individu?>()
    private var mutationList = arrayListOf<Individu?>()
    private var elitismeList = arrayListOf<Individu?>()
    private var bestIndividualList = arrayListOf<Individu?>()
    private val vmObserve by lazy { LiveDataHelper.instance }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        et_reference.setSelection(et_reference.text.length)
        hideSoftKeyboard()
        setupBtnGenerate()
    }

    private fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    private fun setupBtnGenerate() {
        btn_generate.setOnClickListener {
            load.visibility = View.VISIBLE
            v_line.visibility = View.GONE
            btn_generate.visibility = View.GONE
            runGeneticWork()
            observeWorker()
        }
    }

    private fun runGeneticWork() {
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val data = Data.Builder()
        data.putString("reference", et_reference.text.toString())
        data.putDouble("pm", pm.value.toDouble() / 10)
        data.putDouble("pc", pc.value.toDouble() / 10)

        val workRequest = OneTimeWorkRequest.Builder(GenWorker::class.java)
            .setConstraints(constraint)
            .setInputData(data.build())
            .build()

        val workManager = WorkManager.getInstance()
        workManager.enqueue(workRequest)
    }

    private fun observeWorker() {
        vmObserve.observeGeneration().observe(this, Observer {
            tv_generation.text = "Generation = $it"
            initView()
        })

        vmObserve.observeFitnessRef().observe(this, Observer {
            tv_fitnes_ref.text = "Fitness of word reference = $it"
        })

        vmObserve.observeBestIndiviual().observe(this, Observer {
            bestIndividualList = Gson().fromJson(it, object : TypeToken<List<Individu>>() {}.type)
        })

        vmObserve.observePopulate().observe(this, Observer {
            firstPopulate = Gson().fromJson(it, object : TypeToken<List<Individu>>() {}.type)
        })

        vmObserve.observeRws().observe(this, Observer {
            rwsList = Gson().fromJson(it, object : TypeToken<List<Individu>>() {}.type)
        })

        vmObserve.observeCrossOver().observe(this, Observer {
            crossOverList = Gson().fromJson(it, object : TypeToken<List<Individu>>() {}.type)
        })

        vmObserve.observeMutation().observe(this, Observer {
            mutationList = Gson().fromJson(it, object : TypeToken<List<Individu>>() {}.type)
        })

        vmObserve.observeElitisme().observe(this, Observer {
            elitismeList = Gson().fromJson(it, object : TypeToken<List<Individu>>() {}.type)
        })
    }

    private fun initView() {

        //bestIndividual
        var bestIndividualText = ""
        var lastBest = ""
        bestIndividualList.forEach {
            bestIndividualText += it!!.gens.map { it?.getAllele() }.toListCharView() + "\n"
            lastBest = it.gens.map { it?.getAllele() }.toListCharView()
        }
        tv_best_individual.text = bestIndividualText

        var countFitnessTextBestIndividual = ""
        bestIndividualList.forEach {
            countFitnessTextBestIndividual += "fitness = " + it!!.getFitness().toString() + "\n"
        }
        tv_best_individualfitness.text = countFitnessTextBestIndividual

        //stop loader
        if (lastBest == et_reference.text.toString()) {
            load.visibility = View.GONE
            v_line.visibility = View.VISIBLE
            btn_generate.visibility = View.VISIBLE
        }

        //first populate
        var firstPopulateText = ""
        firstPopulate.forEach {
            firstPopulateText += it!!.gens.map { it?.getAllele() }.toListCharView() + "\n"
        }
        tv_first_populate.text = firstPopulateText

        var countFitnessText = ""
        firstPopulate.forEach {
            countFitnessText += "fitness = " + it!!.getFitness().toString() + "\n"
        }
        tv_count_fitness.text = countFitnessText

        //rws
        var rwsText = ""
        rwsList.forEach {
            rwsText += it!!.gens.map { it?.getAllele() }.toListCharView() + "\n"
        }
        tv_rws.text = rwsText

        var countFitnessTextRws = ""
        rwsList.forEach {
            countFitnessTextRws += "fitness = " + it!!.getFitness().toString() + "\n"
        }
        tv_count_fitness_rws.text = countFitnessTextRws

        //cross over
        var crossOverText = ""
        crossOverList.forEach {
            crossOverText += it?.gens?.map { it?.getAllele() }?.toListCharView() + "\n"
        }
        tv_cross_over.text = crossOverText

        var countFitnessTextCross = ""
        crossOverList.forEach {
            countFitnessTextCross += "fitness = " + it?.getFitness().toString() + "\n"
        }
        tv_count_fitness_cross.text = countFitnessTextCross

        //mutation
        var mutationText = ""
        mutationList.forEach {
            mutationText += it?.gens?.map { it?.getAllele() }?.toListCharView() + "\n"
        }
        tv_mutation.text = mutationText

        var countFitnessTextMutation = ""
        mutationList.forEach {
            countFitnessTextMutation += "fitness = " + it?.getFitness().toString() + "\n"
        }
        tv_count_fitness_mutation.text = countFitnessTextMutation

        //elitisme
        var elitismeText = ""
        elitismeList.forEach {
            elitismeText += it?.gens?.map { it?.getAllele() }?.toListCharView() + "\n"
        }
        tv_elitisme.text = elitismeText

        var countFitnessTextElitisme = ""
        elitismeList.forEach {
            countFitnessTextElitisme += "fitness = " + it?.getFitness().toString() + "\n"
        }
        tv_count_fitness_elitisme.text = countFitnessTextElitisme
    }
}
