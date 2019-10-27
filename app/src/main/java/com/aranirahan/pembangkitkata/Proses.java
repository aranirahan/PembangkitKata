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
public class Proses {
    public static Gen[] acuan;
    public static int jmlGen;
    
    public int fitness(Gen[] gens){
        jmlGen = acuan.length;
        int fitness = jmlGen * 25;
        int sigma = 0;
        for(int i=0; i<jmlGen; i++){
            sigma += Math.abs(acuan[i].getAngka()-gens[i].getAngka());
        }
        fitness = fitness - sigma;
        return fitness;
    }
    
    public static Individu RWS(Individu[] individu, double[] probabilitas){
        double r = Math.random();
        byte i = 0;
        double sum = probabilitas[i];
        while(sum < r){
            i++;
            sum += probabilitas[i];
            if(i == 9){
                break;
            }
        }
        return individu[i];
    }
    
    public static Individu[] crossOver(Individu induk1, Individu induk2, double pc, int size){
        Individu anak1 = new Individu(size);
        Individu anak2 = new Individu(size);
        
        for(int i=0; i<induk1.getJmlGen(); i++){
            double r = Math.random();
            if(r<=pc){
               anak1.setGen(i, induk2.getGen(i));
               anak2.setGen(i, induk1.getGen(i));
            }else{
               anak1.setGen(i, induk1.getGen(i));
               anak2.setGen(i, induk2.getGen(i));
            }
        }
        
        Individu[] hasil = new Individu[2];
        hasil[0] = anak1;
        hasil[1] = anak2;
//        System.out.println(Arrays.toString(hasil[0].individu));
//        System.out.println(Arrays.toString(hasil[1].individu));
        return hasil;
    }
    
    public static double[] getMax(double[] fitness) {
        double maxValue = fitness[0];
        double index = 0;
        double[] hasil = new double[2];
        for (int i = 1; i < fitness.length; i++) {
            if (fitness[i] > maxValue) {
                maxValue = fitness[i];
                index = i;
            }
        }
        hasil[0] = maxValue;
        hasil[1] = index;
        return hasil;
    }
    
    public static double[] getMin(double[] fitness) {
        double minValue = fitness[0];
        double index = 0;
        double[] hasil = new double[2];
        for (int i = 1; i < fitness.length; i++) {
            if (fitness[i] < minValue) {
                minValue = fitness[i];
                index = i;
            }
        }
        hasil[0] = minValue;
        hasil[1] = index;
        return hasil;
    }
    
    public static Individu mutasi(Individu induk, double pm){
        for(int i=0; i<jmlGen; i++){
            double r = Math.random();
            if(r <= pm){
                int genBaru = (int) (Math.random()*25+1);
                char hurufBaru = (char) (genBaru+64);
                Gen g = new Gen(hurufBaru);
                induk.setGen(i, g);
            }
        }
        return induk;
    }

    public static Gen[] getAcuan() {
        return acuan;
    }

    public static void setAcuan(Gen[] acuan) {
        Proses.acuan = acuan;
    }

    public static int getJmlGen() {
        return jmlGen;
    }

    public static void setJmlGen(int jmlGen) {
        Proses.jmlGen = jmlGen;
    }
    
    
}
