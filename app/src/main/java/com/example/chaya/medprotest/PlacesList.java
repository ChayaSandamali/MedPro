package com.example.chaya.medprotest;
import java.io.Serializable;
import java.util.List;

import com.google.api.client.util.Key;

/** Implement this class from "Serializable"
 * So that you can pass this class Object to another using Intents
 * Otherwise you can't pass to another actitivy
 * */

/**
 * Created by Chaya on 9/2/2014.
 */
public class PlacesList implements Serializable  {
    @Key
    public String status;

    @Key
    public List<Place> results;
}
