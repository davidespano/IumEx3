package com.example.davide.iumex3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by davide on 17/10/14.
 */
public class Series<T> implements Serializable{
    protected List<T> items;
    protected List<Double> values;

    public Series(){
        items = new ArrayList<T>();
        values = new ArrayList<Double>();
    }

    public void addElement(T item, Double value){
        this.items.add(item);
        this.values.add(value);
    }

    public T itemAt(int i){
        if(i < 0 || i >= items.size()){
            return null;
        }
        return items.get(i);
    }

    public Double valueAt(int i){
        if(i < 0 || i >= values.size()){
            return null;
        }
        return values.get(i);
    }

    public int getCount(){
        return items.size();
    }

    public boolean replaceElement (int i, T item, Double value){
        if(i < 0 || i > items.size()){
            return false;
        }

        this.items.set(i, item);
        this.values.set(i, value);

        return true;
    }

    public Iterable<Double> getValues(){
        return this.values;
    }


    public Iterable<T> getItems(){
        return this.items;

    }
}
