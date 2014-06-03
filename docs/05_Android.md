
* linux gebaseerd
* voor touchscreen toesteleln (smartphones en tablets)
* origineel van Open Handset Allicance (OHA)
    * consortium van hardware, software- en telecommunicatiebedrijven
* vooral marketinfront van Google om industriële ondersteuning te tonen voor Android
* licentie: opensource en permissive
    * mag vrij aangepast worden en gedistribueerd door toestelfabrikanten, developers en mobiele operators
* Google staat alternatieve app markets toe
* deze factoren zorgde ervoor dat Android 's werelds meest gebruikte smartphoneplatform werd
* wordt ook gebruikt bij televisies, gameconsoles, digitale cameras, etc.

## Fragmentatie

* android platform: 
    * geeft een API framework aan applicaties om te integreren met het androidsysteem 
    * geïdentificeerd met API level
* elk Android platform ondersteunt exact 1 API level
    * ondersteuning voor vorige API levels is impliciet
    * updates aan API is zodanig dat het compatibel blijft metvorige API level
    * meeste veranderingen zijn bijkomende features of vervangingen van features (oudere worden dan gemarkeerd als deprecated)
        * deprecated: worden niet verwijderd, bestaande applicaties kunnen het blijven gebruiken
* snelle releases: zorgt voor grote fragmentaties
* er bestaan nog veel oudere in de markt
    * goedkope telefoons
* Google steunt op OEM om nieuwe versies uit te brengen
    * ook voor upgrades van oudere telefoons (OTA) 
    * duurt enkele maanden (voor veel smartphones gebeurt dat zelfs niet)
* laatste updates: minder baanbrekende nieuwe features
    * android wordt matuur

## Google Play Services

* innovatieve features worden niet meer toegevoegd aan Android Open Source Project, maar aan Play Services
* Play Services draait constant op achtergrond, bijna elke Google App heeft het nodig
* updates gaan niet via Play Store, maar via een eigen stille auto update (buitenom controle van gebruiker)
* werkt vanaf Android 2.2
* werkt als shim tussen normale apps en Android OS
* componenten in Google Play Services
    * account sync
    * malware scanning
    * Google Play Games
    * Google Maps API
    * Push messages
    * ...

## Play Store

* bevat meer en meer apps die vroeger standaard in Android zaten
    * Google Search, Google Now, Hangouts, Calendar, ...
* geruchten dat ze hetzelfde gaan doen voor camera app of Android Launcher
* OS zelf wordt
    * system UI
    * lock screen
    * APIs
    * kernel
    * drivers
* Reden: Google kan veel nieuwe features brengen zonder te wachten op OEMs of netwerk operators
* Bijna alles dat uit OS kan gehaald worden wordt eruit gehaald
    * enkel: hardware support, Application Framework APIs en Apps met specifieke toegang (lockscreen, telefoonsettings) vereisen systeemupdate
    * voor al de rest kan Google snel updates uitrollen: beter voor developers
* Hardwarefabrikanten zijn dan wel harder gelinkt aan Google
    * Play services is niet open source en niet vrij te verspreiden
    * Samsung maakt kloon aps en concurrent Tizen
    * Samsung is goed voor 40% van alle Andoird toestellen
* Google en Samsung deal
    * delen patenten voor volgende 10 jaar
    * Google verkoopt Motorola
    * Samsung stopt met Android ecosystem te hijacken

## Open Source, not community driven

* Open Source: belangrijke feature/troef
* in tegenstelling tot meeste Open Source projecten
    * Android wordt achter gesloten deuren ontwikkeld
    * geen publieke mailing lists/forums
    * geen publieke repository voor broncode met laatste dev branch
    * Google released code enkel als er nieuwe versies van Android gelanceerd wordt op een nieuw toestel (gemiddeld om de 6 maanden)
    * Contributions naar Android is zeer gelimiteerd
    * geen publieke roadmap beschikbaar
* Google werkt gewoonlijk met 1 fabrikant voor volgende release
    * nauwe samenwerking
    * doel: volgend flagship device
* eens gelanceerd, code wordt gedumped op publieke repositories

### Linux kernel en Android Platform

* moeilijk om een vanilla kernel te gebruiken
    * patch nodig
* kernel developers proberen actief de benodigde features te ondersteunen voor het Android Platform
* veel van de features zijn ondertussen gemerged of vervangen
    * Android Mainlining Project
* Android Platform
    * linux distributie met userspace packages
    * biedt een specifieke UX met specifieke tools, interfaces en dev APIs
    * AOSP (distributie bovenop Android compatibele kernel)
* Licensing: kernel: GNU GPLv2
    * niet toegestaan om wijzigingen in andere licentievorm dan GPL te distribueren
    * moet ook beschikbaar zijn in GPL
    * Applicatie bovenop linux kernel mag gedistribueerd worden onder elke licentievorm
    * Google probeert zoveel mogelijk GPL te vermijden
        * er werd zelfs gekeken om andere kernels te gebruiken
* Google heeft ervoor gezorgd dat veel userspace componenten niet dezelfde problemen heeft als GPL
* veel GPL en LGPL componenten zijn niet te vinden in AOSP
* Google heeft eigen componenten gebouwd en released in Apache License
    * Bionic lib als vervanging voor glibc
    * Toolbox utility ipv BusyBox
* klassieke open source projecten in `external\` directory
* binaire bestanden zorgen voor geen problemen (wordt niet aangepast door OEMs)

## Platform Architecture 

![](/assets/platform_architecture.png)

### Kernel

* hart van systeem
* laatste versie: Linux 3.4 kernel
* Linux: kernel gekozen voor
    * driver model (+ bestaande drivers)
    * memory en process management
* bevat
    * HAL (hardware abstraction layer)
    * drivers
        * scherm
        * touch input
        * netwerk
        * power management
        * storage
* componenten
    * voor memory management en inter proces communicatie
    * andere low-level functies
* bevat meerdere voor Android specifieke patches
    * binder
        * inter process communicatie en remote method invocation
        * vergemakkelijkt communicatie tussen processen
        * behandelt de memory management
        * behandelt management van lifecycle van objecten die gedeeld worden tussen processen
    * process kills
        * aggressief, wacht niet tot er geen geheugen meer vrij is
    * Wake lock
        * controversieel: om te voorkomen dat toestel naar low power state gaat

### Android runtime

* Dalvik Virtual Machine
* core runtime component van Android
* proces gebaseerde virtuele machine, geoptimaliseerd voor een low memory footprint
* draait bovenop de Android kernel
    * gebruikt kernel voor low level functies (multithreading en memory management)
* elke applicatie draait in eigen instantie van Dalvik VM
    * Dalvik VM draait als een proces beheerd door de kernel
* Android OS is zodanig ontworpen zodat meerdere instanties van de VM tegelijk kunnen draaien zonder nadelig effect voor UX
* Sandbox: zorgt voor security
* als er extra toegang nodig is (bv voor contacten, textberichten, bluetooth)
    * er moet expliciet toestemming gevraagd worden
* applicaties zelf worden in Java dialect geschreven
    * wordt gecompileerd naar JVM compatibele `.class`
    * wordt dan naar Dalvik Excecutable (`.dex`) bestand gecompileerd

### System Libraries

* system libs in C/C++ beschikbaar via Application Framework
* vb. libc
    * font rendering
    * database
    * hardware acceleratie voor 2D en 3D rendering
    * 2D en 3D compositing (Surface Manager library)
* niet volledige GNU Clib
* web driven: Blink
    * web browser engine, als web view te embedden
    * bestaat ook in Chrome
    * belangrijk voor HTML5 apps en hybrid apps
* Android Media Library
    * biedt ook mogelijkheden om op te nemen of af te spelen (audio, beeld, video)

### Application framework

* Apps geschreven in Java
* Application Framework biedt high level API voor applicatie ontwikkelaars
* Grote verschillen tussen standaard Java APIs en Android APIs
* JVM wordt niet gebruikt
* geen toegang tot AWT en Swing in Android
* veel systeemservices toegankelijk via `Manager` object
* `NotificationManager`: notificaties zenden naar gebruiker (in vorm van icon, LED flash, geluid,...)
* `ResourceManager`: om statische resources te scheiden van app code (layouts, afbeeldingen, strings)
    * kan gebruikt worden om applicatie aan te passen aan het type toestel dat het wordt uitgerold
* `LocationManager`: locatie services toegang
* `TelephonyManager`
* `SMSManager`
* `PackageManager`: om te installeren, upgraden of verwijderen van apps (`.apk`)
    * bij installatie wordt `.apk` geparsed en worden de nodige toestemmingen gevraagd.
























































