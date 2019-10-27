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
public class Gen {
    public char allele;

    public Gen() {
    }

    public Gen(char allele) {
        this.allele = allele;
    }
    
    
    public void randomAllele(){
        int r = (int) (Math.random()*26+1);
        this.allele = (char) (r+64);
    }
    
    public int getAngka(){
        return (int) allele-64;
    }

    public char getAllele() {
        return allele;
    }

    public void setAllele(char allele) {
        this.allele = allele;
    }
       
    
}
