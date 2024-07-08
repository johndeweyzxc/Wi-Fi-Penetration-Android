# Wi-Fi penetration Android

This android application controls the ESP32 by generating instruction codes, selecting targets, securely storing hashes in a local database, receing GPS coordinates and the transmission of data to the rest api server. The ESP32 receives command from the Android device via serial communication, it executes the attack based on the instruction code sent by Android device. The source code for the ESP32 can be found [here](https://github.com/johndeweyzxc/Wi-Fi-Penetration-ESP32)

## Features

- **Local Database** - Temporary local database for PMKID, MIC and EAPOL data intercepted by ESP32
- **GPS** - Uses GPS functionality to record the location of Wi-Fi devices
- **Rest API** - Uses HTTP to transfer captured PMKID, MIC, and EAPOL data to a local REST API server, the source code can be found in restapi directory
- **Automatic Attack Mode** - Automates the penetration of nearby access points while preventing re-attacks on previously targeted access points

## Architecture

<img src="visuals/Command-Launch-Module-Software-Architecture.png" width="800">

#### This module consists of 4 main components, the view, view model, repository and the apis. Here is the brief description of each component:

- **View** - Fragments, buttons, dialogs and other UI components, this is where the user interacts with the system
- **View Model** - Connects the view and the repository, it validates user input and also formats data to be consume by the view
- **Repository** - This is the business part of the application where it is responsible for processing inputs and outputs to and from the APIs
- **APIs** - Primary data source for the repository such as controlling the ESP32, sending HTTP requests to a server, using GPS functions and persisting data in a local database database

## ESP32 in combination with an Android device

<img src="visuals/AWPS-Hardware.png" width="500">

- **[1] Android Phone** - Android device that can be use as an interface to control the ESP32
- **[2] USB OTG** - Enables communication between the Android device and USB
- **[3] Micro USB Male to USB Male** - Establishes connection between the ESP32 device and USB OTG
- **[4] ESP32** - Execute attacks by receiving commands from the Android device

## Disclaimer

The project is intended for educational purposes, with the primary goal of raising awareness and understanding of cybersecurity in a legal and ethical context. It is essential to clarify that this tool is NOT INTENDED to encourage or promote any form of unauthorized or unethical hacking activities. Ethical hacking, conducted with proper authorization and consent, plays a crucial role in enhancing the security of digital systems. This project seeks to promote responsible use of technology and responsible disclosure of vulnerabilities to help protect and secure digital environments.

USAGE OF ALL TOOLS on this project for attacking targets without prior mutual consent is ILLEGAL. It is the end user’s responsibility to obey all applicable local, state, and federal laws. I assume no liability and are not responsible for any misuse or damage caused by this project or software.
