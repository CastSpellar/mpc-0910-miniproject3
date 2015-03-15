# Mini-project 3 #

## Setup ##

This mini-project should be executed in groups of two students. It is not required that students have the same lab schedule. It is not required to keep the same groups as in mini-project 1.
The algorithms to develop should run on the simulator introduced in lab 11.

## Overview ##

This mini project is intended to introduce students to the design of algorithms for sensor networks. The simulator used is not accurate, presenting a number of simplification, namely in the radio model (e.g. no transmission collisions).

The algorithms to develop should propagate information obtained in the several sensor nodes to the base station (BS, at the bottom left of the simulation area) .

## Basic features (15 val) ##

Develop algorithms for propagating the following information:

minimum, maximum and average temperature in the area;
current temperature at each location.

## Extended features ##

Improve your algorithms by tuning radio power to extend system lifetime.

## News ##

21-05-2010: Corrected bug in energy consumption - download the full copy or the diff from version 0.75 in lab 11 code - followin this link.

## FAQ ##

Q: When developing our algorithm, it takes too long for energy to be completely consumed. Can I make nodes start with less energy?
A: Yes: change the constants defined in class BatteryImpl - file scmu/sensor/sys/AbstractSensorNode.java. Please remeber to change the value again when getting result for the project report.

Q: The size of the messages does not seem to influence the energy consumed. Is anything wrong?
You should define the method length in all classes of messages. This method should return the number of byte of the message.