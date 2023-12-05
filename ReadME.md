# Assignment 3

The goal of this project was to use an algorithm to find the path between two countries where, you can only travel to a country's neighbor to go to a different country.
Example :-

* India to Pakistan is a valid journey. (India and Pakistan are neighbors.)
* India to Uzbekistan is not a valid journey. (India and Uzbekistan are neighbors.)

---

## Usage

```cmd
java IRoadTrip borders.txt capdist.csv state_name.tsv
```

## Algorithm Choice

I chose to use Floyd's Algorithm. The reasons for choosing Floyd's algorithm over Dijkstra's Algorithm were :-

* When using Dijkstra's Algorithm, we run the algorithm every time a prompt is entered. This is because 
  this algorithm gives all the paths from source vertex to all other vertex.
* On the other hand, Floyd's algorithm gives you the path from every vertex to every other vertex.
* Floyd's Algorithm needs to be run just once, at the start of the program, while Dijkstra's algorithm must run
  every time.
* This is an advantage, although the path Matrix must be traversed every prompt to reconstruct the required path output.

Having said this, one of the main reasons I chose Floyd was to see if I could use a different algorithm. I knew Dijkstra's algorithm would work (since the prompt mentioned using Dijkstra's algorithm) so I wanted to see if something else works.

## Object Oriented Breakdown

IRoadTrip contained methods that had to be implemented. Therefore, all I added to that file were helper functions.

I choose to use a Country object instead of multiple HashMaps because:-

* It is simpler to use an object instead of multiple HashMaps where keeping the keys synchronized to maintain data continuity would be one of the concerns.
* I like it.

## Challenges

* modify Floyd's Algorithm to maintain a path matrix.
* Recognize and handle data discontinuity.
* A lot of data discontinuity.

I had fun during this project.

