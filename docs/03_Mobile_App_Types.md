
* web app, hybrid app & native app

## Native apps

* geïnstalleerd als binary
* via App/Play store
* geïmplementeerd in de standaardtaal van platform
* kan best gebruikmaken van de apparaatfuncties: camera, GPS, sensors, ...
* netwerkverbinding niet vereist om app te starten

## Web apps

* geoptimaliseerde websites in HTML5, CSS3 & JS
* in browsers: *write once, run everywhere*
* weinig binaire gegevens (gecachete objecten)
* actieve netwerkverbinding vereist
* browser: sandbox
    * voorkomt toegang tot het onderliggend toestel
* HTML5: nieuwe APIs om web apps functoineel equivalent te maken met native apps
    * API voor sensortoegang en offline werking
* Uitdagingen
    * heterogeniteit ( <=> native SDK is stabiel op alle devices)
    * performantie 
    * grote variatie tussen browsers (HTML5 support)
* sandbox
    * extra laag : ten koste van performantie

## Hybrid apps

* tussenweg: geschreven in HTML5
    * in een native container herverpakt voor installatie
* zitten in app store
* maken gebruik van native features (uit native SDK)
* rendering via HTML, browser embedded in app
* om development en onderhoudskosten te drukken
    * PHoneGap/Cordova en Sencha Touch

## Voordelen en nadelen van web apps

### Device features

* web apps kunnen nog steeds niet alle toestelspecifieke features gebruiken (camera, gestures, notifications)

### Offline functioning

* browser cache (HTML5) gelimiteerd/beperkt tegenover native

### Discoverability

* web content, veel gemakkelijker te ontdekken op het web dan een app
* mensen zoeken antwoorden in een zoekmachine, niet in app store
* meeste gebruikers installeren niet graag aparte apps

### Installation

* bookmark op homescreen: wordt minder snel gedaan
* <=> native/hybrid: installatie via store

### Speed

* native apps zijn sneller (zie Facebook)

### Maintenance

* native: ingewikkeld, zeker als je meerdere versies hebt
* web: simpel zoals website onderhouden

### Platform independence

* web: delen van code kan zeker hergebruikt worden

### Content restrictions, approval process & fees

* native en hybrid: vereist goedkeuring en inhoudsrestricties
* web: vrij voor iedereen
* Apple: 30% commision

### User Interface

* consistentie met OS en andere apps: moeilijker bij web apps
    * kan deels opgevangen worden door hybrid apps

### Development cost

* native: vereisen skills voor elk van de platform (talen)
* web/hybrid: skills dat verderbouwt op web technologie
    * HTML5: redelijk nieuw

## Is HTML5 alternatief voor native?

* HTML5 kan niet dezelfde resultaten als native bereiken
* *write once, run everywhere*: kan gezien worden als kostenbesparend
    * tenietgedaan door browserfragmentatie en slechte UX
* HTML5: geen uniforme standaard
    * fragmentatie
    * implementatie verschilt van browser tot browser
* performantie: rendering en laden
    * mensen hebben geen geduld
    * verwachten dezelfde snelheid als native apps






