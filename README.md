# AWPS (Automatic Wi-Fi Penetration System) Command Launch Module

Automatic WiFi Penetration System, or AWPS is a software designed to penetrate most Wifi enabled devices that is using WPA2. AWPS leverages the formidable capabilities of the ESP32 microcontroller to perform a crucial task in penetrating a Wifi network: capturing critical data elements when a STA initiated authentication with the AP.  Capturing critical data elements, the PMKID and MIC enables us to conduct offline brute force attacks, facilitating the identification of the PSK.

## Command Launch Module

The Android device, or any Android-powered device, serves as the central hub for all penetration operations. It orchestrates the Launcher Module through the generation of instruction codes, meticulously selects targets for attack, securely stores hashes in a local database, uses GPS to persists the location of where the hashes was captured and facilitates the transmission of these hashes to a dedicated server. The server, in turn, executes the cracking operations. This comprehensive system offers a human friendly user interface, ensuring a seamless and modern user experience. The Command Launch Module plays a pivotal role by supplying both power and instructions to the Launcher Module, harmonizing their functionalities for efficient and effective penetration testing operations. A 'hashes' is a concise term referring to critical data elements such as PMKID, MIC, and other EAPOL data. These elements play a pivotal role in the process of determining the password associated with the target access point.

The AWPS Launcher Module is an ESP32 microcontroller it functions as the executor of instruction codes issued by the Command Launch Module. Serving as a streamlined input-output system, the Launcher Module receives instruction or control codes, processes them, and executes the corresponding actions. It is important to note that the Launcher Module does not independently validate user input commands. Therefore, the presence of a Command Launch Module becomes crucial for crafting accurate instruction codes, mitigating errors, and ensuring precise execution.

For a deeper dive into the Launcher Module's capabilities and further insights, explore the repository here [INSERT REPO LINK].

## Features
- **Material User Interface Design Standard** incorporates the Material Design standard, established by Google, to present a contemporary and visually appealing user interface.
- **Local Database** serves as a temporary repository for PMKID or MIC and EAPOL data intercepted by the Launcher Module. Additionally, it captures pertinent details, including the location and timestamp of the data interception, enriching the stored information with contextual relevance.
- **Location Aware** leverages GPS functionality to precisely record the location of data interception. This includes capturing longitude, latitude, and corresponding address information, enhancing the precision and contextual awareness of the recorded data interception events.
- **Hash Transmission** enables the seamless transfer of captured PMKID, MIC, and EAPOL data to a local REST API server. This server, integrated with a database, responsibly stores the transported information. Furthermore, the server meticulously generates a crackable file, compatible with hashcat, to facilitate efficient discovery of the password.
- **Automatic Attack Mode (Currently under development)** automates the penetration of nearby access points with a while preventing re-attacks on previously targeted access points. Utilizing a database, the system intelligently checks for previous interactions with the access point before initiating an attack. All intercepted and relevant data is meticulously stored in a local database for comprehensive record-keeping.
- **Manual Attack Mode** is a manual method for penetrating nearby access points, equipped with a capability to prevent re-attacks on previously targeted access points. Leveraging a simple database, the system meticulously examines previous interactions with the access point before initiating an attack. All intercepted and pertinent data is systematically stored in a local database, fostering comprehensive record-keeping.

## How to Hack or Penetrate the Wi-Fi using AWPS and GPU

<img src="visuals/How-to-hack-the-Wi-Fi.png" alt="How to hack the Wi-Fi">

Note: The android device presented in the story is the Command Launch Module, the Launcher Module is attached to the Command Launch Module. The actor in step 3 does not send an HTTP request to the Rest API server. The actor directly configures the GPU via a software by typing in the commands and or configuring the rules for brute force, selecting / managing which GPU to use, etc. The Rest API server is also not available in the public internet as stated in step 2, it is only available as a local Rest API server in the LAN. Its primary role is to transport hash from the AWPS mobile to the server. It does not provide any input data validation nor any best practices when it comes it Rest API security.

## Software Architecture

<img src="visuals/Command-Launch-Module-Software-Architecture.png" alt="Launcher Module Software Architecture">

## Software Components

This module consists of 4 main components, the view, view model, repository and the apis. Here is the brief description of each component:
- **View** is the application's user interface encompasses activities, fragments, and other UI-related code. This is where the user interacts with the system.
- **View Model** component is the bridge between the view and the repository, undertaking the crucial tasks of validating user input and formatting data to be consume by the view.
- **Repository** also known as the model or the business part of the application is responsible for processing inputs and outputs to and from the APIs.
- **APIs** serve as the primary data source for the repository, encompassing functionalities such as controlling the launcher module, executing HTTP POST requests to a server, and persisting data in a database.

## Hardware Components

In this project, three hardware components are employed: an Android device, USB OTG (On The Go), and the ESP32. While the project has been rigorously tested on the ESP32-WROOM-32D variant, it is expected to be compatible with any microcontroller based on the ESP32-WROOM-32 platform.

[INSERT PICTURE OF ESP32 ATTACHED TO ANDROID DEVICE VIA USB OTG]

- **[1] Android Phone** This project employs an Android device as the command and control interface for the launcher module. The interface features a dedicated application that establishes communication with the launcher module through a USB On-The-Go (OTG) connection. The application is configured with a minimum API level of 24 (Android 7.0) and a maximum API level of 33 (Android 13). It is important to note that the application has been thoroughly tested on physical devices running Android versions 8.1 and 11.
- **[2] USB OTG (On The Go)** The USB On-The-Go (OTG) interface facilitates both power supply and data transmission, enabling seamless communication between the command and control module and the launcher module. The USB On-The-Go (OTG) cable can be extended to a greater length, enabling the discreet placement of the ESP32 microcontroller inside a pocket or bag.
- **[3] Micro USB Male to USB Male** The micro USB to USB male cable establishes a connection between the ESP32 device and a USB male port.
- **[4] ESP32-WROOM-32D** This project leverages the ESP32 microcontroller as the dedicated launcher module. This microcontroller hosts an application that actively monitors incoming instruction codes and subsequently initiates the corresponding armament procedures in accordance with the received instructions. The ESP32 microcontroller is available for purchase at a competitive price point, starting from as low as $3.34 (approximately ₱190). The ESP32 is a compact and portable microcontroller, designed to easily fit within a pocket or bag, thus facilitating the concept of inconspicuous penetration testing.

## DISCLAIMER

The project AWPS is intended for educational purposes, with the primary goal of raising awareness and understanding of cybersecurity in a legal and ethical context. It is essential to clarify that this tool is NOT INTENDED to encourage or promote any form of unauthorized or unethical hacking activities. Ethical hacking, conducted with proper authorization and consent, plays a crucial role in enhancing the security of digital systems. This project seeks to promote responsible use of technology and responsible disclosure of vulnerabilities to help protect and secure digital environments.

USAGE OF ALL TOOLS on this project for attacking targets without prior mutual consent is ILLEGAL. It is the end user’s responsibility to obey all applicable local, state, and federal laws. I assume no liability and are not responsible for any misuse or damage caused by this project or software.
