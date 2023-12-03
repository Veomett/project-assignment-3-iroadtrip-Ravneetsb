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
        dataFix();
        readTSV();
//        for (Country c: countries) {
//            c.details();
//        }

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

    private void getNeighbourAlias(String line) {
        Pattern pattern = Pattern.compile(".+=[.+;]+\\s(?<Country>[A-Za-z]+)(?<Alias>\\(.+\\))");
        Matcher match = pattern.matcher(line);
        while (match.find()) {
            String country = match.group("Country");
            String alias = match.group("Alias");
            Country c = findCountryByName(country);
            c.addAlias(alias);
        }
    }

    @SuppressWarnings("unchecked")
    private void readTXT() {
        // TO DO: special cases for countries with a "," in the name.
        String countryRegex = "(?<Country>[A-Za-z\\s,\\-']+)";
        Pattern pattern;
        Matcher match;

        try (BufferedReader br = new BufferedReader(new FileReader("borders.txt"))){
            String line;
            Country country = null;
            pattern = Pattern.compile(countryRegex);
            while ((line = br.readLine()) != null) {
                line = line.toLowerCase();
                String alias = getAlias(line, true);
                getNeighbourAlias(line);
                match = pattern.matcher(line);
                boolean firstCountry = true;
                while (match.find()) {
                    try {
                        if (firstCountry) {
                            String countryName = formatCountryName(match.group("Country").trim());
                            country = new Country(countryName);
                            if (alias != null)
                                country.addAlias(alias);
                            firstCountry = false;
                        } else {
                            String temp = formatCountryName(match.group("Country").trim());
                            if (!temp.equals("km")) {
                                if (!neighbours.containsKey(country)) {
                                    ArrayList<String> neighbours = new ArrayList<>();
                                    neighbours.add(temp);
                                    this.neighbours.put(country, (ArrayList<String>) neighbours.clone());
                                } else {
                                    this.neighbours.get(country).add(temp);
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

    private String getAlias(String data, boolean country) {
        Pattern pattern;
        if (country)
            pattern = Pattern.compile(".+\\((?<Alias>.+)\\).+=");
        else pattern = Pattern.compile(".+\\((?<Alias>.+)\\).+");
        Matcher match = pattern.matcher(data);
        if (match.find())
            return formatCountryName(match.group("Alias").toLowerCase());
        else {
//            System.out.println(data);
            return null;
        }
    }

    private String slashCheck(String data) {
        String[] dataArr = data.split("/");
        if (dataArr.length > 1) {
            Country c = findCountryByName(dataArr[0]);
            c.addAlias(dataArr[1]);
        }
        return dataArr[0];
    }

    private void readTSV() {
        HashMap<String, String> countryCodes = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("state_name.tsv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.toLowerCase();
                String alias = getAlias(line, false);
                String[] data = line.split("\t");
                if (data[4].equals("2020-12-31")) {
                    String country = formatCountryName(data[2].split("\\(")[0].trim());
                    country = slashCheck(country);
                    String countryCode = data[1].toUpperCase();
                    Country c = findCountryByName(country);
//                    System.out.println("call on " + country);
                    if (c != null) {
                        if (alias != null)
                            c.addAlias(alias);
                        c.setCode(countryCode);
                        c.setId(data[0]);
                    } else {
                        if (alias != null) {
                            c = findCountryByName(alias);
                            if (c != null) {
                                c.setId(data[0]);
                                c.setCode(countryCode);
                                c.addAlias(country);
                            } else {
                                System.out.println(line);
                            }
//                            c.details();
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
        Pattern pattern = Pattern.compile("^t|\\b(?!(of|the|and)\\b)\\w");
        Matcher match = pattern.matcher(name);
        StringBuffer sb = new StringBuffer();
        while (match.find()) {
            match.appendReplacement(sb, match.group().toUpperCase());
        }
        match.appendTail(sb);
        return sb.toString();
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
            if (c.getAlias().contains(name)) {
                return c;
            }
            if (c.getCode() != null) {
                if (c.getCode().equals(name))
                    return c;
            }
        }
        return null;
    }

    private void dataFix() {
//        findCountryByName("United States").addAlias("United States of America");
//        findCountryByName("Germany").addAlias("German Federal Republic");
//        findCountryByName("Suriname").addAlias("Surinam");
        nameHasThe();
        nameHasAnd();
    }

    private void nameHasThe() {
        for (Country c: countries) {
            String name = c.getName();
            String[] nameArr =name.split(",");
            if (nameArr.length > 1) {
                if (name.contains("the")){
                    c.addAlias(formatCountryName(nameArr[1].trim() + " " + nameArr[0].trim()));
                    if (!nameArr[0].equals("Congo")) {
                        c.addAlias(formatCountryName(nameArr[0].trim()));
                    }
//                    c.details();
                }
            }
        }
    }

    private void nameHasAnd() {
        Pattern pattern = Pattern.compile("[A-Za-z]+\\sand\\s[A-Za-z]+");
        Matcher match;
        for (Country c: countries) {
            String name = c.getName();
            match = pattern.matcher(name);
            if (match.find()) {
                String[] nameArr = name.split("\\sand\\s");
                c.addAlias(nameArr[0].trim() + "-" + nameArr[1].trim());
//                c.details();
            }
        }
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
        a3.findCountryByName("Denmark").details();
//        a3.acceptUserInput();

    }

}

