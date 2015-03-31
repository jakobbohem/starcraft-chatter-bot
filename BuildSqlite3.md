# Introduction #

This application is developed with use of NetBeans IDE. It should run on any Java enabled machine with Java version >= 1.5
The database sqlite3 is handled with a very simple JDBC driver here: http://www.ch-werner.de/javasqlite/overview-summary.html#jdbc_driver

a sqlite.jar file is included in the program package but it might need to be recompiled for the specific system you are running.
The source can be downloaded from that same web page (direct link: http://www.ch-werner.de/javasqlite/javasqlite-20120209.tar.gz)

Instruction to compile on Mac OS X are found here: http://www.ch-werner.de/javasqlite/scnotes.txt

I have tested the compilation with those instructions on OS X Lion in July 2012 and it seems to work well.

# Project modification #

The file sqlite3.jar which contains the interfaces for the database connector is in ./bot/lib in the program main directory. replace this file with the sqlite3.jar file from the compiled files on your computer.

Add your content here.  Format your content with:
  * Text in **bold** or _italic_
  * Headings, paragraphs, and lists
  * Automatic links to other wiki pages