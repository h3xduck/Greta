# Greta

Greta is a mobile application built for Android. It has been created as a side project with the purpose of practicing my Android, Databases and Cryptography skills.
I am still (self-)learning Android development, so please bear in mind that optimality and good design models are conspicuous by their absence.

--------------------
## Objective
Greta is a mobile app which supports the creation and management of text notes created by the user. Notes will be encrypted and stored in the system using SQLite (only locally). Export and Import of notes is supported.

Once the app is finished, Greta will be integrated into a larger, more ambitious project which I will eventually release as well.

--------------------
## Features (so far)
* SQLite tables for notes, and encryption salts are created and managed with DBFlow.
* User can create, view, modify and delete their notes. Long-clicking on a note displays a menu with more options.
* A fragment showing the SQLite tables was created (for debugging, I might keep it tho).
* User can easily encrypt and decrypt the notes, both permanently or automatically while editing them. AES with a 128-bit key is used in CBC mode.
* User can provide a password, for which an encryption key is derived. Different salts are used for each element and stored in the DB.
 The key is NOT stored in the DB at any moment.
* The user can export the notes, storing them in a JSON file. Notes can be imported too, and a file picker has been integrated.
* Licenses for the third party libraries are shown in an About fragment.

--------------------
## TODO
The full Kanban board will be publicly released once I clean it up. As a summary, Greta still needs to:
* The app needs ***a lot*** of refactoring. Some fragments have too many responsibilities.
* Clean the UI, many parts need to be improved and others are just for debugging.
* Include more ~~bugs~~ functionalities, similarly to other notes apps.
-------------------------
## License
This project is released under the GPL v3 license. See LICENSE.