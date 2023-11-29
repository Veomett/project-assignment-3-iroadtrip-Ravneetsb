package org.example;

import java.util.HashMap;

public class Country implements Comparable<Country> {
    private final String name;
    private String code;
    private String id;
    private final HashMap<String, String> neighbours;

    public Country(String name) {
        this.name = name;
        neighbours = new HashMap<>();
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
    public void addNeighbour(String country, String distance) {
        neighbours.put(country, distance);
    }
    public HashMap<String, String> getNeighbours() {
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
        return "Country{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", id='" + id + '\'' +
                ", neighbours=" + neighbours +
                '}';
    }
}
