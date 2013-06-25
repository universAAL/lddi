After some additional research it seems that the x64 version of the native library released by Toshiba stuff (Toshiba SDK) does not work. Although
the application does not crash, the aforementioned file is unable to open new HDP channels (this is the first mandatory required step in the process).

If you have a PC with an x86_64 architecture one possible solution is to load the x86_32 version of the DLL (just take into account that the Eclipse 
IDE as well as the Java JDK available in your computer must be a version for 32 bits. Otherwise it does not work).

Ángel Martínez-Cavero (amartinez@tsbtecnologias.es)
TSB (Valencia, Spain)