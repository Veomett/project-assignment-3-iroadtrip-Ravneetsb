package org.example;

import java.util.ArrayList;
import java.util.HashMap;

public class Country implements Comparable<Country> {
    private final String name;
    private String code;
    private String id;
    private final ArrayList<String> alias = new ArrayList<>();
    private final HashMap<Country, Integer> neighbours;
    public Country(String name) {
        this.name = name;
        this.neighbours = new HashMap<>();
    }
    @Override
    public int compareTo(Country c) {
        return this.code.compareTo(c.getId());
    }

    /**
     * Getters and Setters.
     */

    public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return this.code;
    }
    public String getName() {
        return this.name;
    }
    public void addNeighbour(Country country, Integer distance) {
        neighbours.put(country, distance);
    }
    public HashMap<Country, Integer> getNeighbours() {
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
        if (!this.alias.isEmpty()) {
            res = "{ Name: " + this.name +
                    " | Alias: " + this.alias +
                    " }";
        } else {
            res = "{ Name: " + this.name +
            " }";
        }
        return res;
    }

    public ArrayList<String> getAlias() {
        return alias;
    }

    public void addAlias(String alias) {
        this.alias.add(alias);
    }


    /**
     * Debugging Function.
     * Prints the details of the country in a nice format.
     */
    public void details() {
        String details = "---------------------------------\n" +
                "Object Country: \n" +
                "\t Name: " + this.name + "\n" +
                "\t Alias: " + this.alias + "\n" +
                "\t Code: " + this.code + "\n" +
                "\t ID: " + this.id + "\n" +
                "\t Neighbours" + this.neighbours +
                "\n---------------------------------";
        System.out.println(details);
    }
}
