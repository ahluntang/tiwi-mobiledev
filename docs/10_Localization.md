## Methods

* verschillende methodes: location providers
* elk met eigen voordelen en nadelen
* elk met unieke karakteristieken
* kunnen in verschillende situaties aangewend worden
* app zelf heeft weinig controle over werking van providers
    * kan wel beslissen welke provider gebruikt moet worden

### GPS provider

* Global Positioning System: satellieten rond de wereld
    * helpen de ontvanger om locatie te detecteren
* GPS: 27 satellieten continu rond de wereld
    * zenden continu informatie uit
    * volgen een gedefinieerd pad, verzekeren dat vanuit elk punt op de wereld een "zichtbaar" pad is naar minstens 4 satellieten
    * line os sight nodig om locatie te weten via GPS
* locatie berekenen: GPS receiver moet afstand tot meerdere satellieten kunnen vaststellen
* satelliet verstuurt data
    * zijn positie
    * tijd wanneer transmissie gestart werd
        * GPS satelliet bevat accuraat mechanisme om tijd bij te houden, zodat het gesynchroniseerd is met de andere satellieten
        * klokken moeten goed gesynchroniseerd zijn om de juiste locatie te berekenen (zowel voor satellieten als voor receivers)
        * kleinste verschil in tijd kan resulteren in grote fouten
* via starttijd van verzending en constante lichtsnelheid
    * receiver kan tijd berekenen dat nodig waso m info te ontvangen
    * met afstand tot meerdere satellieten: receiver kan locatie bepalen
    * punt waar alle sferen snijden = locatie van receiver
        * minimaal 3 satellieten nodig om 2D locatie (lat, lon) te vinden
            * geeft 2 locaties: 1 op aarde en 1 in ruimte
        * 4 satellieten nodig voor clock error 
        * meer satellieten zorgt voor meer accuraatheid
            * maar er is een bovenlimiet

### Assisted GPS

* voordat locatie berekend kan worden: signaal van meerdere satellieten moet gevonden worden
    * frequentie van satellietsignaal moet gevonden worden
    * satellieten zenden een gekende frequentie uit
        * bij receiver wordt signaal verschoven (Doppler effect) door relatieve verschuiving van satelliet
* op locatie zullen maar een handvol satellieten in line of sight zitten
    * geblokkeerd door gebouwen
    * onder horizon
    * receiver moet dus al de fragmenten en code delay ruimtes overlopen
* tijd reduceren: satelliet verzendt een almanac
    * met info over satellieten in de constallatie
    * info over zichzelf (orbiting data en staat van systeem)
    * kan assisteren in bepalen van welke satellieten gebruikt moet worden voor bepaalde locatie
    * ontvanger kan Doppler verschuiving van frequency benaderen (ipv over volledige frequentieruimte te zoeken)
    * minder energieverbruik
    * betere gevoeligheid
    * meer tijd beschikbaar om accurater te zoeken
* als toestel geen recent almanac data heeft, moet nog steeds GPS satelliet zoeken om de almanac data te krijgen
    * A-GPS: cellular network towers hebben GPS receivers
        * halen contant satellietinfo op
        * kunnen GPS almanac verzenden met bijkomende info naar mobiel toestel
        * was origineel bedoeld voor US: alle carriers moeten geolocatie van 911 emergency call doorgeven

#### Mobile Station Assisted

* positie berekening door de server die info van GPS receiver gebruiken
* mobiele service provider logt continu GPS info (almanac) van GPS satelliet via een A-GPS server in systeem
* het toestel neemt snapshot van GPS signaal op, met benaderende tijd
    * assistance server heeft goed signaal en genoeg kracht om gefragmenteerde signalen door te sturen

#### Mobile Station Based

* info wordt gebruikt om satellieten sneller te vinden
* almanac wordt naar GPS receiver gestuurd
* GPS receiver kan sneller satellieten vinden
* precieze tijd wordt doorgegeven
* satellietpositie en versnellingsdata (via assistance server) en initiële positie van receiver (CellID) reduceert aantal frequentieruimtes dat doorzocht moet worden door receiver.
* Niet alle carriers ondersteunen A-GPS voor gebruiker
    * data overdracht kan soms betalend zijn

### Simultaneous GPS

* toestellen gebruiken vaak dezelfde hardware om te communiceren met satellieten en om telefoongesprekken te maken
    * enkel 1 van 2 kan tegelijk uitgevoerd worden
* S-GPS: bijkomende hardware zodat GPS radio en cellular radio simultaan gebruikt kan worden
* kan GPS data overdracht versnellen want kan tegelijk data ontvangen terwijl cellular netwerk radio actief is
* S-GPS: beschikbaar in nieuwste telefoons.

### Limitations

* ononderbroken pad nodig naar GPS satelliet
* zullen niet indoor werken
* kunnen problemen hebben als lucht niet zichtbaar is buiten (gebouwen, bomen,...)
* meerdere GPS satellieten nodig
    * kan tijdje duren voordat locatie gevonden is
    * veel toestellen gebruiken low-powered GPS radios
    * andere bronnen van info nodig
* er kunnen objecten het GPS signaal reflecteren voordat de receiver het ontvangt
    * gereflecteerde signalen hebben een ander pad van GPS satelliet tot GPS receiver
    * berekening van afstand fout: multipath errors
    * locatie kan van ene plaats naar andere springen
    * vaak voorkomend in steden waar GPS signaal afketst van gebouwen

### Network Providers

* localisatie via WiFi accesspoints of cellular networks

#### WiFi AP

* wifi radio moet aan staan (verbruikt minder dan GPS hardware)
* er werden vroeger veel APs uitgerold op veel locaties
* AP broadcast signaal om bestaan aan te konden in de buurt
* signalen gaan honderd meter ver in meerdere richtingen
* dichtheid van signalen in steden is zeer hoog (overlappend)
* kan referentiesysteem zijn om locatie te bepalen
    * fingerprint voor unieke locatie
* WiFi detecteert de signalen en signaalsterkte en vraagt via Google Location Service welke fingerprint het best past (verschillende algo's mogelijk, bv k-nearest neighbour estimation)
* Google heeft veel info verzameld via Google Street View
    * bewaarde hotspots met MAC adres, SSID en signaalsterkte
    * werd gecorreleerd met GPS coördinaten
    * wardriving
* Fingerprints worden ook via Android toestellen verzameld, zelfs als je Google Maps/Latitude e.d. niet gebruikt
    * GPS, CellID, WiFi SSID en signaalsterktes worden toch verzameld ne naar Google verzonden
    * voordeel
        * locatie kan bepaald worden op plaatsen waar GPS dit niet kan
        * in stedelijke gebieden wordt WiFi lokalisatie accurater door de hogere densiteit van WiFi APs
* Limitaties
    * WiFi netwerken moeten binnen bereik zijn
    * Netwerken moeten SSID broadcasten en moeten ingesteld zijn om niet genegeerd te wroden door Android: _nomap suffix
    * locaties kunnen verkeerd zijn
        * Google staat niet toe om locatie van AP expliciet in te stellen
        * Android doet dit zelf, Google wacht tot andere toestellen de locatie bevestigen voordat de DB bijgewerkt wordt

#### CellIDs

* cellular network voor lokalisatie
* cellular networks zijn verdeeld in cells
* elke cell heeft basisstation (cellular tower)
* locatie van elke toren is exact en gekend door de operators
    * CellID met LAC (Location Area Code)
* als toestel verplaatst
    * mogelijk dat het verbindt met een andere toren als signaalsterke van andere toren sterker wordt
* als het toestel verbindt met netwerk: toestel wordt gekoppeld met basisstation met sterkste signaal naar dat toestel.
* basis cellular positioning
    * gekende locatie van basesttation
    * Cell Identification (CellID)
    * accuraatheid hangt af van grootte cell
* meerdere technieken om accuraatheid te verbeteren
    * cells worden onderverdeeld in sectoren via directionele antennes
    * verbeteringen met betrekking tot signaalsterkte
        * wordt beïnvloed door meerdere factoren zoals fading, topografie, obstakels,...
    * timing advance: berekend door basisstation
        * Enhanced Cell ID (E-CID)
* Android & Google Location Service werken op dezelfde manier om dit DB bij te werken zoals bij WiFi
* Google Location Service kan "map" van celltowers maken
* toestellen sturen Cell ID van huidige toren en vorige torens die het gebruikt heeft
* als het meerdere IDs doorstuurt, dan kan er tirangulatie gebeuren om een accuratere locatie te vinden.
* limitaties
    * gelijkaardige limitaties als bij WiFi
    * Locatie van cell towers veranderen minder dan locaties van WiFi AP





