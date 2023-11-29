package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.security.spec.ECField;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IRoadTrip {
    private final ArrayList<Country> countries = new ArrayList<>();
    public IRoadTrip (String [] args) {
        // Replace with your code
        readTXT();
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

    private void readTXT() {
        // TO DO: special cases for countries with a "," in the name.
        String countryRegex = "(?<Country>[A-Za-z\\s]+)";
        Pattern pattern;
        Matcher match;

        try (BufferedReader br = new BufferedReader(new FileReader("borders.txt"))){
            String line;
            Country country = null;
            pattern = Pattern.compile(countryRegex);
            while ((line = br.readLine()) != null) {
                match = pattern.matcher(line);
                boolean firstCountry = true;
                while (match.find()) {
                    try {
                        if (firstCountry) {
                            country = new Country(match.group("Country").trim());
                            firstCountry = false;
                        } else {
                            String temp = match.group("Country");
                            if (!temp.trim().equals("km")) {
                                country.addNeighbour(temp, null);
                            }
                        }
                    } catch (Exception e) {
                        //Do nothing
                    }
                }
                countries.add(country);
            }
        } catch (IOException e) {
            System.out.println("borders.txt not found");
            System.exit(-1);
        }
    }

    private void readTSV() {
        HashMap<String, String> countryCodes = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("state_name.tsv"))) {
            String line;
            while ((line = br.readLine()) != null) {
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

//        a3.acceptUserInput();

    }

}

