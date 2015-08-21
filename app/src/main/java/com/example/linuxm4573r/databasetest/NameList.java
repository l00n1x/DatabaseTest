package com.example.linuxm4573r.databasetest;

import android.provider.BaseColumns;

import java.util.Random;

/**
 * Created by linuxm4573r on 8/18/2015.
 */
public class NameList {
    private Random myRand;
    private String[] names = {"Pam",
            "Chia",
            "Neomi",
            "Serafina",
            "Maribel",
            "Kellie",
            "Wendell",
            "Miguelina",
            "Phillip",
            "Merrie",
            "Shan",
            "Haywood",
            "Althea",
            "Lakita",
            "Al",
            "Caroyln",
            "Allen",
            "Meryl",
            "Emerita",
            "Luetta",
            "Fatima",
            "Alysha",
            "Francis",
            "Troy",
            "Eusebia",
            "Deanna",
            "Fae",
            "Octavio",
            "Danilo",
            "Mauricio",
            "Yang",
            "Shemika",
            "Justin",
            "Lucile",
            "Esmeralda",
            "Marquetta",
            "Yen",
            "Diamond",
            "Ramona",
            "Darline",
            "Tawnya",
            "Rolando",
            "France",
            "Merlyn",
            "Cristy",
            "Ellie",
            "Ji",
            "Suanne",
            "Margherita",
            "Eda"};

    public NameList()
    {
        myRand = new Random();
    }
    public String getRandomName()
    {
        int index=myRand.nextInt(names.length-1);
        return names[index];
    }
}
