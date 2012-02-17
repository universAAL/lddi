The KNX network driver bundle is copied from the DOG2 project http://domoticdog.sourceforge.net/

Modifications are done within the european funded research project "universAAL".

Main modifications are done to get independent to other DOG bundles, especially DOG2Library and DOG model.

Mods:
- Using Logger from OSGi framework (Felix) (Activator.java)
- KnxDriver.java strongly depends on other DOG bundles -> heavyly modified
- 