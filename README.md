# Greta

Greta is a mobile application built for Android. It has been created as a side project with the purpose of practicing my Android, Databases and Cryptography skills.
I am still (self-)learning Android development, so please bear in mind that optimality and good design models are conspicuous by their absence.

--------------------
## Objective
Greta is thought to be a mobile app which supports the creation and management of text notes created by the user. Notes will be encrypted and stored in the system using SQLite (only locally). Export and Import of notes will be supported.

Once the app is finished, Greta will be integrated into a larger, more ambitious project which I will eventually release as well.

--------------------
## Features (so far)
* SQLite tables for notes, user and salt (temporary, this will be moved to the android keystore) are created and managed with DBFlow.
* User can create, view, modify and delete their notes. Long-clicking on a note displays a menu with more options.
* A fragment showing the SQLite tables was created (for debugging, I might keep it tho).

--------------------
## TODO
The full Kanban board will be publicly released once I clean it up. As a summary, Greta still needs to:
* The app needs ***a lot*** of refactoring. Also many icons and names are the default ones from android studio, it needs to be changed.
* Encrypt notes before storing them in the DB.
* Clean the UI, many parts need to be improved and others are just for debugging.
* Store salt in a secure way.
* Allow the user to export the notes and also to import them.
* Include more ~~bugs~~ functionalities, similarly to other notes apps.
* Add an About page displaying info about the app and libraries licenses (MIT).
-------------------------
## License
This project is released under the GPL v3 license. See LICENSE.