package com.aranirahan.pembangkitkata.model;

public class Gen {

    private char huruf;

    private int angka;

    public Gen(int a) {
        this.angka = a;
        this.huruf = (char) (this.angka + 64);
    }

    public Gen(char h) {
        this.huruf = h;
        this.angka = (int) this.huruf - 64;
    }

    public void setHuruf(char h) {
        this.huruf = h;
        this.angka = (int) this.huruf - 64;
    }

    public void setAngka(int a) {
        this.angka = a;
        this.huruf = (char) (this.angka + 64);
    }

    public char getHuruf() {
        return this.huruf;
    }

    public int getAngka() {
        return this.angka;
    }
}