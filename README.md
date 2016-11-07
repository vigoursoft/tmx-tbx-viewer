# tmx-tbx-viewer
A webapp for viewing Translation memory exchange (.tmx) and termbase (.tbx) files

    Upload TMX files or TBX files to a server for viewing over the network.
    Once deployed, users go to http://server:port/tmx to get the index page.
    The rest is self explanatory.

    Files are uploaded into the directory "uploads" under "user.home". The folder will be
    created on first use if it does not exist.

    The content of a tmx file is displayed in a table that has as many columns as the tmx has 
    languages (xml:lang attribute values).

    To edit an entry, click it, and make the desired change.
    When the mouse cursor leaves the text field, the change is sent to the server. The changed
    file is saved under the name "output.tmx". 

    NOTE: In this initial version, there is no synchronization for different concurrent users.
    You can, however, collaborate using the output.tmx (rename a desired file to output.tmx).

    Download the   .war file here.  or get the Netbeans sources 
     Netbeans sources with all libraries and build it yourself.

    Requirements: Java 1.8 and Tomcat 8.x (8.0.27 or higher, or equivalent).
