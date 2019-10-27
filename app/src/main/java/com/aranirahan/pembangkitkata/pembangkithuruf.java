/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aranirahan.pembangkitkata;

import java.util.Arrays;

/**
 *
 * @author Yayang Wijaya
 */
public class pembangkithuruf {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        int jmlIndividu = 10;
        int totalFitness = 0;
        double[] fitnessI = new double[jmlIndividu];
        double[] fitnessIndividu = new double[jmlIndividu];
        Individu[] i = new Individu[jmlIndividu];
        double[] probabilitas = new double[jmlIndividu];
        
        Gen[] acuan = new Gen[11];
        acuan[0] = new Gen('I');
        acuan[1] = new Gen('N');
        acuan[2] = new Gen('F');
        acuan[3] = new Gen('O');
        acuan[4] = new Gen('R');
        acuan[5] = new Gen('M');
        acuan[6] = new Gen('A');
        acuan[7] = new Gen('T');
        acuan[8] = new Gen('I');
        acuan[9] = new Gen('K');
        acuan[10]= new Gen('A');
        Proses.setAcuan(acuan);
        
        //membangkitkan individu
        //System.out.println("Populasi awal");
        for(int a=0; a<jmlIndividu; a++){
            i[a] = new Individu(11);
            i[a].bangkitkanIndividu();
            //System.out.println(Arrays.toString(i[a].individu)+" Fitness : "+i[a].getFitness());

        }
        
        int iterasi = 0;
        do{
            iterasi++;
            System.out.println(" \n \n");
            System.out.println("=============== GENERASI KE : " +iterasi+ "===============");
            System.out.println("=== > Populasi awal < ===");
            for(int a=0; a<jmlIndividu; a++){
                System.out.println(Arrays.toString(i[a].individu)+" Fitness : "+i[a].getFitness());

            }
        
            
            //menghitung total fitness
            for(int a=0; a<jmlIndividu; a++){
                totalFitness += i[a].getFitness();
            }

            //menghitung probabilitas tiap individu
            for(int a=0; a<jmlIndividu; a++){
                probabilitas[a] = (double) (i[a].getFitness())/totalFitness;
            }

            //Seleksi RWS
            System.out.println("\n === > Seleksi < === ");
            Individu seleksi[] = new Individu[jmlIndividu];
            for(int a=0; a<jmlIndividu; a++){
                seleksi[a] = Proses.RWS(i, probabilitas);
                System.out.println(Arrays.toString(i[a].individu)+" Fitness : "+i[a].getFitness());
                //System.out.println(Arrays.toString(probabilitas));
            }

            //cross over
            System.out.println("\n === > Cross Over < === ");
            Individu[] individu = new Individu[jmlIndividu];
            Individu[] anak;
            for(int a=0; a<jmlIndividu; a+=2){
                anak = Proses.crossOver(seleksi[a], seleksi[a+1], 0.9,11);
                individu[a] = anak[0];
                individu[a+1] = anak[1];
                System.out.println(Arrays.toString(individu[a].individu)+" Fitness : "+individu[a].getFitness());
                System.out.println(Arrays.toString(individu[a+1].individu)+" Fitness : "+individu[a+1].getFitness());
            }
            
            //Mutasi
            System.out.println("\n === > Mutasi < ===");
            for(int a=0; a<jmlIndividu; a++){
                individu[a] = Proses.mutasi(individu[a], 0.1);
                System.out.println(Arrays.toString(individu[a].individu)+" Fitness : "+individu[a].getFitness());
            }
            
            //Elitisme
            System.out.println("\n === > Elitisme < ===");
            for(int a=0; a<jmlIndividu; a++){
                fitnessI[a] = i[a].getFitness();
            }
            for(int a=0; a<jmlIndividu; a++){
                fitnessIndividu[a] = individu[a].getFitness();
            }
            
//            System.out.println(Proses.getMax(fitnessI)[0]);
//            System.out.println(Proses.getMax(fitnessI)[1]);
            
            if(Proses.getMax(fitnessIndividu)[0]<Proses.getMax(fitnessI)[0]){
                individu[(int) Proses.getMin(fitnessIndividu)[1]] = i[(int) Proses.getMax(fitnessI)[1]];
            }
            
            i=individu;
            for(int a=0; a<jmlIndividu; a++){
                fitnessI[a] = i[a].getFitness();
            }
            
            for(int a=0; a<i.length; a++){
                System.out.println(Arrays.toString(i[a].getIndividu())+" Fitness : "+i[a].getFitness());
            }
//            System.out.println(Proses.getMax(fitnessI)[0]);

        }while(Proses.getMax(fitnessI)[0] != 275);
        
        System.out.println();
        System.out.println("****************************************************");
        System.out.println("   KATA BERHASIL DITEMUKAN PADA GENERASI KE : " +iterasi);
        System.out.println("           Dengan Fitness maksimal : 275 " );
        System.out.println("****************************************************");
    }
    
}
