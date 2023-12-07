package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gives the path between 2 countries, if it exists.
 */
public class IRoadTrip {
    private final ArrayList<Country> countries = new ArrayList<>();
    private final HashMap<Country, ArrayList<String>> neighbours = new HashMap<>();
    private final HashMap<String, String> neighbourAlias = new HashMap<>();
    public int[][] Matrix;
    public int[][] pathMatrix;
    public IRoadTrip (String [] args) {
        if (args.length != 3) {
            System.err.println("Usage: java IRoadTrip txt-file csv-file tsv-file");
            System.exit(-1);
        }
        readTXT(args[0]);
        updateNeighbours();
        dataFix();
        readTSV(args[2]);
        readCSV(args[1]);
        initMatrix();
    }

    /**
     * read the CSV file and populate the respective country object.
     * @param file csv file path.
     */
    private void readCSV(String file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                String firstCountry = fields[1];
                String secondCountry = fields[3];
                int distance = Integer.parseInt(fields[4]);
                Country c1 = findCountryByName(firstCountry);
                Country c2 = findCountryByName(secondCountry);
                if (c1 != null && c2 != null) {
                    if (isNeighbour(c1, c2)) {
                        c1.getNeighbours().replace(c2, distance);
                        c2.getNeighbours().replace(c1, distance);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("capdist.csv not found.");
        }
    }

    /**
     * Are 2 countries neighbours?
     * @param a Country
     * @param b Country
     * @return true if a and b are neighbours
     */
    private boolean isNeighbour(Country a, Country b) {
        return a.getNeighbours().containsKey(b);
    }

    /**
     * extract country alias from brackets.
     * @param line line from the file being read.
     */
    private void getNeighbourAlias(String line) {
        Pattern pattern = Pattern.compile(".+;\\s(?<Country>.+)\\s\\((?<Alias>.+)\\).*");
        Matcher match = pattern.matcher(line);
        while (match.find()) {
            String country = match.group("Country");
            String alias = match.group("Alias");
            if (country != null && alias != null) {
                neighbourAlias.put(formatCountryName(country), formatCountryName(alias));
            }
        }
    }

    /**
     * Read the txt file and create the Country instance and populate Country's neighbours.
     * @param file txt file path
     */
    @SuppressWarnings("unchecked")
    private void readTXT(String file) {
        // TO DO: special cases for countries with a "," in the name.
        String countryRegex = "(?<Country>[A-Za-z\\s,\\-']+)";
        Pattern pattern;
        Matcher match;

        try (BufferedReader br = new BufferedReader(new FileReader(file))){
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
                            nameHasComma(countryName, country);
                            if (countryName.equals("United States"))
                                country.addAlias("US");
                            if (alias != null) {
                                country.addAlias(alias);
                                country.addAlias(countryName + " (" + alias + ")");
                            }
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

    /**
     * extract alias from country name.
     * @param data country name
     * @param country true if country, false if neighbours
     * @return alias if extracted else null.
     */
    private String getAlias(String data, boolean country) {
        Pattern pattern;
        if (country)
            pattern = Pattern.compile(".+\\((?<Alias>.+)\\).+=");
        else pattern = Pattern.compile(".+\\((?<Alias>.+)\\).+");
        Matcher match = pattern.matcher(data);
        if (match.find())
            return formatCountryName(match.group("Alias").toLowerCase());
        else {
            return null;
        }
    }

    /**
     * extract alias from a country name, add it to aliases and return the clean country name.
     * @param data country name
     * @return country name after cleaning.
     */
    private String slashCheck(String data) {
        String[] dataArr = data.split("/");
        if (dataArr.length > 1) {
            Country c = findCountryByName(dataArr[0]);
            c.addAlias(dataArr[1]);
        }
        return dataArr[0];
    }

    /**
     * Read the tsv file and populate the state-ID and aliases for countries.
     * @param file tsv file path.
     */
    private void readTSV(String file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
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
                        }
                    }

                }
            }
        } catch (IOException e) {
            System.out.println("state_name.tsv not found");
        }
    }

    /**
     * Format the country name, to make the country names have the same case format across files.
     * @param name country name / alias / ID
     * @return formatted string.
     */
    private String formatCountryName(String name) {
        Pattern pattern = Pattern.compile("^t|\\b(?!(of|the|and)\\b)\\w");
        Matcher match = pattern.matcher(name);
        StringBuilder sb = new StringBuilder();
        while (match.find()) {
            match.appendReplacement(sb, match.group().toUpperCase());
        }
        match.appendTail(sb);
        return sb.toString();
    }

    /**
     * After countries have been created, populate the neighbours.
     */
    private void updateNeighbours() {
        for (Country c: countries) {
            ArrayList<String> n = this.neighbours.get(c);
            if (n != null) {
                for (String s : n) {
                    Country co = findCountryByName(s);
                    if (co != null) {
                        c.addNeighbour(co, Integer.MAX_VALUE);
                    }
                }
            }
        }
    }

    /**
     * Returns the Country either by name, alias or ID.
     * @param name Country name / alias / ID
     * @return Country
     */
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

    /**
     * Alias generation and handling extremely disconnected data.
     */

    private void dataFix() {
        nameHasThe();
        nameHasAnd();
        findCountryByName("United States").addAlias("United States of America");
        findCountryByName("Canada").addNeighbour(findCountryByName("United States"), 731);
        findCountryByName("Germany").addAlias("German Federal Republic");
        findCountryByName("Suriname").addAlias("Surinam");
        findCountryByName("North Korea").addAlias("Korea, People'S Republic of");
        findCountryByName("North Korea").addAlias("Korea, People's Republic of");
        findCountryByName("South Korea").addAlias("Korea, Republic of");
        findCountryByName("North Macedonia").addAlias("Macedonia");
        findCountryByName("Vietnam").addAlias("Vietnam, Democratic Republic of");
        findCountryByName("Romania").addAlias("Rumania");
        findCountryByName("Cabo Verde").addAlias("Cape Verde");
        findCountryByName("Kyrgyzstan").addAlias("Kyrgyz Republic");
        findCountryByName("Democratic Republic of the Congo").addAlias("Congo, Democratic Republic of (Zaire)");
        findCountryByName("Democratic Republic of the Congo").addAlias("Democratic Republic of Congo");
        findCountryByName("Democratic Republic of the Congo").addAlias("Zaire");
        findCountryByName("Zaire").getAlias().remove("Congo");
        findCountryByName("Republic of the Congo").addAlias("Congo");
        findCountryByName("United Kingdom").addAlias("UK");
        findCountryByName("Timor-Leste").addAlias("East Timor");
        findCountryByName("Czechia").addAlias("Czech Republic");
        findCountryByName("Denmark").getAlias().remove("Greenland");
        neighbourAliasFix();
    }

    /**
     * helper function for alias generation.
     */
    private void neighbourAliasFix() {
        neighbourAlias.remove("Morocco");
        for (String country: neighbourAlias.keySet()) {
            Country c = findCountryByName(country);
            if (c != null) {
                c.addAlias(neighbourAlias.get(country));
                c.addAlias(country + " (" + neighbourAlias.get(country) + ")");
            }

            else System.out.println(country + " is null");
        }
    }
    /**
     * helper function for alias generation.
     */
    private void nameHasComma(String name, Country country) {
        String[] nameArr = name.split(",");
        if (nameArr.length > 1) {
            country.addAlias(nameArr[1].trim() + " " + nameArr[0].trim());
        }
    }
    /**
     * helper function for alias generation.
     */
    private void nameHasThe() {
        for (Country c: countries) {
            String name = c.getName();
            String[] nameArr =name.split(",");
            if (nameArr.length > 1) {
                c.addAlias(formatCountryName(nameArr[1].trim() + " " + nameArr[0].trim()));
                if (!nameArr[0].equals("Congo") && !nameArr[0].equals("Korea")) {
                    c.addAlias(formatCountryName(nameArr[0].trim()));
                }

            }
        }
    }
    /**
     * helper function for alias generation.
     */
    private void nameHasAnd() {
        Pattern pattern = Pattern.compile("[A-Za-z]+\\sand\\s[A-Za-z]+");
        Matcher match;
        for (Country c: countries) {
            String name = c.getName();
            match = pattern.matcher(name);
            if (match.find()) {
                String[] nameArr = name.split("\\sand\\s");
                c.addAlias(nameArr[0].trim() + "-" + nameArr[1].trim());
            }
        }
    }

    /**
     * Gets the distance between two countries.
     * @param country1 Country
     * @param country2 Country
     * @return -1 if they are not neighbours, the distance between their capitals otherwise.
     */
    public int getDistance (String country1, String country2) {
        Country c1 = findCountryByName(country1);
        Country c2 = findCountryByName(country2);
        if (c1 != null && c2 != null) {
            if (isNeighbour(c1, c2)) {
                if (c1.getNeighbours().get(c2) == Integer.MAX_VALUE) {
                    if (c2.getNeighbours().get(c1) == Integer.MAX_VALUE) {
                        return -1;
                    } else {
                        return c2.getNeighbours().get(c1);
                    }
                } else {
                    return c1.getNeighbours().get(c2);
                }
            }

        }
        return -1;
    }

    /**
     * Construct the path between two countries from the path matrix.
     * @param country1 Source Country
     * @param country2 Destination Country
     * @return List of path travelled from country to country to reach destination.
     */
    public List<String> findPath (String country1, String country2) {
        int indexC1 = countries.indexOf(findCountryByName(country1));
        int indexC2 = countries.indexOf(findCountryByName(country2));
        List<Integer> path = findPath(indexC1, indexC2);
        return formatPath(path);
    }

    /**
     * Helper function to format the path retrieved from the path matrix.
     * @param path path from path matrix.
     * @return formatted path.
     */
    private List<String> formatPath(List<Integer> path) {
        if (path == null) {
            return new ArrayList<>();
        }
        List<String> finalPath = new ArrayList<>();
        Iterator<Integer> pathIter = path.iterator();
        Country c1 = countries.get(pathIter.next());
        Country c2;
        while(pathIter.hasNext()) {
            c2 = countries.get(pathIter.next());
            int distance = getDistance(c1.getName(), c2.getName());
            if (distance == -1) {
                distance = getDistance(c2.getName(), c1.getName());
            }
            String pathString = c1.getName() + " --> " + c2.getName() + " (" + distance + " km.)";
            finalPath.add(pathString);
            c1 = c2;
        }
        return finalPath;
    }

    /**
     * reconstruct the path from the path matrix.
     * @param indexC1 index of Source Country
     * @param indexC2 index of Destination Country.
     * @return List containing integer path.
     */
    private List<Integer> findPath(int indexC1, int indexC2) {
        List<Integer> path = new LinkedList<>();
        if (pathMatrix[indexC1][indexC2] == -1) {
            return null;
        }
        path.add(indexC1);

        while (indexC1 != indexC2) {
            indexC1 = pathMatrix[indexC2][indexC1];
            path.add(indexC1);
        }
        return path;
    }

    /**
     * Floyd's Algorithm.
     */
    private void initMatrix() {
        int numCountries = countries.size();
        pathMatrix = new int[countries.size()][countries.size()];
        Matrix = new int[countries.size()][countries.size()];
        for (int i = 0; i < numCountries; i++) {
            for (int j = 0; j < numCountries; j++) {
                if (i == j) {
                    Matrix[i][j] = 0;
                    pathMatrix[i][j] = i;
                } else {
                    Matrix[i][j] = Integer.MAX_VALUE;
                    pathMatrix[i][j] = -1;
                }
            }
        }

        for (int i = 0; i < numCountries; i++) {
            Country country = countries.get(i);
            HashMap<Country, Integer> neighbours = country.getNeighbours();
            for (Country neighbour : neighbours.keySet()) {
                int neighbourIndex = countries.indexOf(neighbour);
                int distance = neighbours.get(neighbour);
                Matrix[i][neighbourIndex] = distance;
                Matrix[neighbourIndex][i] = distance;
                pathMatrix[i][neighbourIndex] = i;
                pathMatrix[neighbourIndex][i] = neighbourIndex;
            }
        }

        for (int k = 0; k < numCountries; k++) {
            for (int i = 0; i < numCountries; i++) {
                for (int j = 0; j < numCountries; j++) {
                    if (Matrix[i][k] != Integer.MAX_VALUE && Matrix[k][j] != Integer.MAX_VALUE &&
                            Matrix[i][k] + Matrix[k][j] < Matrix[i][j]) {
                        Matrix[i][j] = Matrix[i][k] + Matrix[k][j];
                        pathMatrix[i][j] = pathMatrix[k][j];
                    }
                }
            }
        }
    }

    /**
     * Accept user input to give the path between two countries.
     */
    public void acceptUserInput() {
        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.print("Enter the name of the first country (type EXIT to quit): ");
            String country1 = scan.nextLine();
            if (country1.equals("EXIT"))
                System.exit(0);
            Country source = findCountryByName(country1);
            while (source == null) {
                System.out.println("Invalid country name. Please enter a valid country name");
                System.out.print("Enter the name of the first country (type EXIT to quit): ");
                country1 = scan.nextLine();
                source = findCountryByName(country1);

            }
            Country destination;
            System.out.print("Enter the name of the second country (type EXIT to quit): ");
            String country2 = scan.nextLine();
            if (country2.equals("EXIT"))
                System.exit(0);
            destination = findCountryByName(country2);
            while (destination == null) {
                System.out.println("Invalid country name. Please enter a valid country name");
                System.out.print("Enter the name of the second country (type EXIT to quit): ");
                country2 = scan.nextLine();
                if (country2.equals("EXIT"))
                    System.exit(0);
                destination = findCountryByName(country2);
            }
            List<String> path = findPath(source.getName(), destination.getName());
            if (path.isEmpty()) {
                System.out.println("NO PATH");
            } else {
                System.out.println("Route from " + country1 + " to " + country2 + ": ");
                for (String p: path)
                    System.out.println(p);
            }
        }
    }

    /**
     * Main function
     * @param args path for files.
     */
    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);
        a3.acceptUserInput();
    }
}

