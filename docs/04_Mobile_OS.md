
## Universal aspects

### Hardware

* onderscheidend voor een mobiel toestel tegenover andere computertoestellen
* grotendeels ARM processors: specifieke ARM kernels vereist

### HAL: hardware abstraction layers

* HAL moet sensoren (gps signalen, versnelling, rotatie, licht,... ) beheren
    * moet interface aanbieden om sensoren aan en uit te zetten (om energie te besparen)

### Libraries & system services

* ondersteunende services voor mobiele systemen
    * om telefoongesprek te starten
    * gebruikerslocatie vaststellen en beheren
* maakt gebruik van sensor input om een output te bieden op hoger niveau
* andere services
    * teogang tot cloud
    * gezichtsdetectie
    * ...

### Application runtime

* implementeert de kernfunctionaliteit van computertaal (C, C++, C#, Java...)
* communiceert met toepassing over zijn levenscyclus en garandeert isolatie tussen applicaties
*  levensduur processen
    * veel geavanceerder op mobiele toestellen
    * desktop: zelden moet een proces gekilled worden
    * mobiel: van design oogpunt zelf, al moet met life cycle management rekening gehouden worden
* desktop
    * permissies per gebruiker of per groep (komt van mainframes)
    * apps geïnstalleerd door een gebruiker had gemakkelijk toegang tot de bestanden/processen van dezelfde gebruiker
* mobiele OS: initieel voor smartphones
    * smartphone: wordt doorgaans gebruikt door 1 gebruiker
    * gebruikerspermissies gebruiken voor per app authorisatiemodel
    * elke app zit in eigen runtime environment
    * gebruikers moeten toestemming geven aan app om functies te gebruiken
        * apps die kosten zouden maken (telefonie, SMS, internet)
        * apps die privacy van gebruiker uitleest (GPS)
    * intern wordt UID toegekend aan elke app
* Tablets
    * door meerdere gebruikers gebruikt
    * per user mechanisme terug nodig
    * meest recente Android versies ondersteunen meerdere gebruikersaccounts (iOS nog niet)

### Application framework

* collectie APIs voor developers
* geven toegang tot de application runtime
    * lifecycle management
    * communicatie met andere apps
* geavanceerde servides en drivers (energiebeheer)
* sensors (GPS)
* los onderdeel (geen deel van OS zelf)
    * reflecteerd verschillende functionaliteiten in mobiele systemen

