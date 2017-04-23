# Project-CSCI-2020U
Text Editor - William Rory Chisholm, Jeremy Chong
-Text editor automatically opens up the TextFile.txt; clients are able to recieve a file from the Server Storage, and send data to javafx ui.
-Text editor automatically opens up the TextFile.txt.
-By typing text into the editor, and hitting "Update", what is in the text area will be saved.
-This will also generate a change log, called TextFile-FileLog.
-The files can be found in the ServerStorage folder.
-If the files do not exist, they are created. 
-The change log tracks the key and position via carat positions as well as what was added and deleted; something like "add 'abc' to position '3'.
-Deletion is somewhat buggy - must be deleted with backspace, and struggles with deleting lines.
-Intended functionality was to synchronize multiple clients by constantly sending over the changelog.
-Changelog constantly updates whenever the textfile does, and by sending locations as well as changes, they can be made.
-by doing so, changes could be implemented in real time via the timer.
-Timer ticks constantly.