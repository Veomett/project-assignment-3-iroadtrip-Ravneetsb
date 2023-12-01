package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IRoadTrip {
    private final ArrayList<Country> countries = new ArrayList<>();
    private final HashMap<Country, ArrayList<String>> neighbours = new HashMap<>();
    public IRoadTrip (String [] args) {
        // Replace with your code
        readTXT();
        updateNeighbours();
        readTSV();
//        readCSV();
    }

    private void readCSV() {
        // TO DO: add data to hashmap.
        try (BufferedReader br = new BufferedReader(new FileReader("capdist.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                for (String field: fields) {
                    System.out.println(field + " ");
                }

                System.out.println();
            }
        } catch (IOException e) {
            System.out.println("capdist.csv not found.");
        }
    }

    @SuppressWarnings("unchecked")
    private void readTXT() {
        // TO DO: special cases for countries with a "," in the name.
        String countryRegex = "(?<Country>[A-Za-z\\s,]+)";
        Pattern pattern;
        Matcher match;

        try (BufferedReader br = new BufferedReader(new FileReader("borders.txt"))){
            String line;
            Country country = null;
            pattern = Pattern.compile(countryRegex);
            while ((line = br.readLine()) != null) {
                line = line.toLowerCase();
                String alias = getAlias(line);
                match = pattern.matcher(line);
                boolean firstCountry = true;
                while (match.find()) {
                    try {
                        if (firstCountry) {
                            String countryName = formatCountryName(match.group("Country").trim());
                            country = new Country(countryName);
                            if (alias != null)
                                country.setAlias(alias);
                            firstCountry = false;
                        } else {
                            String temp = match.group("Country");
                            if (!temp.trim().equals("km")) {
                                if (!neighbours.containsKey(country)) {
                                    ArrayList<String> neighbours = new ArrayList<>();
                                    neighbours.add(temp.trim());
                                    this.neighbours.put(country, (ArrayList<String>) neighbours.clone());
                                } else {
                                    this.neighbours.get(country).add(temp.trim());
                                }

                            }
                        }
                    } catch (Exception e) {
                        //Do nothing
                    }
                }
//                System.out.println(neighbours);
                countries.add(country);
            }
        } catch (IOException e) {
            System.out.println("borders.txt not found");
            System.exit(-1);
        }
    }

    private String getAlias(String data) {
        Pattern pattern = Pattern.compile(".+\\((?<Alias>.+)\\).+=");
        Matcher match = pattern.matcher(data);
        if (match.find())
            return match.group("Alias").toLowerCase();
        else
            return null;
    }

    private void readTSV() {
        HashMap<String, String> countryCodes = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("state_name.tsv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.toLowerCase();
                String[] data = line.split("\t");
                if (data[4].equals("2020-12-31")) {
                    String country = data[2];
                    String countryCode = data[1];
//                    System.out.println(country + " " + countryCode);
                    for (Country c: countries) {
                        if (c.getName().equals(country)) {
                            c.setCode(countryCode);
                            c.setId(data[0]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("state_name.tsv not found");
        }
        System.out.println(countries);
//        updateCountries();
    }

    private String formatCountryName(String name) {
        Pattern pattern = Pattern.compile("\\b(?!(of|the|and)\\b)\\w");
        Matcher match = pattern.matcher(name);
        StringBuffer res = new StringBuffer();
        while (match.find()) {
            match.appendReplacement(res, match.group().toUpperCase());
        }
        match.appendTail(res);
        return res.toString();
    }
    private void updateNeighbours() {
        for (Country c: countries) {
            ArrayList<String> n = this.neighbours.get(c);
            for (String s : n) {
                Country co = findCountryByName(s);
                if (co != null) {
                    c.addNeighbour(co, " ");
                }
            }
        }
    }

    public Country findCountryByName(String name) {
        for (Country c: countries) {
            if (c.getName().equals(name))
                return c;
        }
        return null;
    }

    public int getDistance (String country1, String country2) {
        // Replace with your code
        return -1;
    }

    public List<String> findPath (String country1, String country2) {
        // Replace with your code
        return null;
    }


    public void acceptUserInput() {
        // Replace with your code
        System.out.println("org.example.IRoadTrip - skeleton");
    }


    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);
        System.out.println(a3.findCountryByName("Congo, Democratic Republic of the"));
//        a3.acceptUserInput();

    }

}

