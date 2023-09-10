# Wave Function Collapse

A [libgdx](https://libgdx.com/) project written in [Scala](https://www.scala-lang.org/).

A basic maze generator using the wave function collapse
algorithm. The algorithm picks the cell on the grid with the
fewest possible options, and 'collapses' it by picking one of
its available configurations. The possible configurations
available to neighboring tiles are then constrained so that they
are consistent with the collapsed tile. The algorithm repeats
until the grid is complete.
