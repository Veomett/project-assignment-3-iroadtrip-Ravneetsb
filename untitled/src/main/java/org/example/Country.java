package org.example;

import java.util.HashMap;

public class Country implements Comparable<Country> {
    private final String name;
    private String code;
    private String id;
    private String alias = null;
    private final HashMap<Country, String> neighbours;
//    private final HashMap<Country, String> Neighbours;

    public Country(String name) {
        this.name = name;
        this.neighbours = new HashMap<>();
    }
    @Override
    public int compareTo(Country c) {
        return this.code.compareTo(c.getId());
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return this.code;
    }
    public String getName() {
        return this.name;
    }
    public void addNeighbour(Country country, String distance) {
        neighbours.put(country, distance);
    }
    public HashMap<Country, String> getNeighbours() {
        return neighbours;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        String res;
        if (this.alias != null) {
            res = "{ Name: " + this.name +
                    " | Alias: " + this.alias +
                    " }";
        } else {
            res = "{ Name: " + this.name +
            " }";
        }
        return res;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
