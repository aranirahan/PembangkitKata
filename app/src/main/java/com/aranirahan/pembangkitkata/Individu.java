/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aranirahan.pembangkitkata;

/**
 *
 * @author Yayang Wijaya
 */
public class Individu {
    public int jmlGen;
    public Gen[] gens;
    public char[] individu;
    public int fitness;

    public Individu(int jmlGen) {
        gens = new Gen[jmlGen];
        individu = new char[jmlGen];
        this.jmlGen = jmlGen;
    }

    public Individu(int jmlGen, Gen[] gens, char[] individu) {
        this.jmlGen = jmlGen;
        this.gens = gens;
        this.individu = individu;
    }
    
    
    public void bangkitkanIndividu(){
        gens = new Gen[jmlGen];
        individu = new char[jmlGen];
        for(int i=0; i<jmlGen; i++){
            gens[i] = new Gen();
            gens[i].randomAllele();
            individu[i] = gens[i].getAllele();
        }
    }
    
    public int getFitness(){
        Proses o = new Proses();
        return o.fitness(gens);
    }

    public int getJmlGen() {
        return jmlGen;
    }
    
    public void setGen(int i, Gen g){
        gens[i] = new Gen();
        gens[i] = g;
        individu[i] = g.getAllele();
    }

    public void setGens(Gen[] gens) {
        this.gens = gens;
    }


    public Gen getGen(int i) {
        return gens[i];
    }

    public char[] getIndividu() {
        return individu;
    }    
    
    
}
