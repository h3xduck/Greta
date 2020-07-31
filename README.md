# Greta

Greta is a lightweight yet powerful notes application built for Android which supports encryption and with a focus on security.
It is completely free as in freedom, free of charge, and free of trackers or ads, forever.

--------------------
## Features (so far)
* Notes are stored **only locally**, the app does not access the Internet **at any time**.
* Material Design UI, dark mode by default. Simple but intuitive menus.
* Create/View/Modify/Delete functionalities are clear and represented by icons, long-clicking on a note displays a larger menu.
* Easy and secure encryption and decryption of notes:
    * Different passwords for each note.
    * After editing a previously encrypted note, it is re-encrypted automatically.
    * Encryption keys are securely generated from users' passwords:
        * Passwords or keys are **never** stored in your phone, either temporarily or permanently.
        * A different salt is generated for each encrypted note.
    * Notes can be encrypted/decrypted from the main screen by clicking on meaningful icons.
    * Technical details about encryption:
        * AES-128 bit keys.
        * CBC encryption mode, PKCS5 padding.
        * HMAC for integrity checking, hash function used is SHA256.
* Notes can be marked as favourite, highlighted in color.
* Export and Import of notes is supported:
    * Notes (encrypted or not) and preferences (favourites) can be easily exported into a JSON file.
    * Notes can be imported by selecting the corresponding JSON file. Warning: Currently overwrites the previous notes if any.
* Notes can be ordered on screen according to different methods:
    * By date, descending.
    * By date, ascending.
    * Favourites first, then by date descending.
    * Favourites last, then by date ascending.
* Includes Settings menu for easy access to different app options.
* Licenses for third party libraries are clearly shown in About menu.

--------------------
## TODO
The full Kanban board will be publicly released once I clean it up. As a summary, Greta still needs to:
* Refactoring is needed. Some fragments have too many responsibilities.
* Include more ~~bugs~~ functionalities, similarly to other notes apps:
    * Support tags for notes, and filtering by tag.
    * Include a deleted/archived notes menu.
    * Support notes with different colors.
    * Etc...
* Unit tests / espresso tests needed.
------------------------
## Disclaimer
Greta has been created as a side project with the purpose of practicing my Android, Databases and Cryptography skills, and of course for fun.
I am still (self-)learning Android development, so please bear in mind that optimality and good design models are conspicuous by their absence.

Note that, although it has been released to the public and I am actively maintaining it, this app is of personal use and offered AS-IS
 **I am not responsible** for harm of any nature derived from its use.

In the future, Greta will be integrated into a larger, more ambitious project which I will eventually release as well.

-------------------------
## License
This project is released under the GPL v3 license. See LICENSE.
