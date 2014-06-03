
## Browser support

* HTML5: collectie features
    * niet elke browser ondersteunt alles
    * altijd controleren en graceful fallback aanbeiden
        * detectie door bv DOM te inspecteren
        * sommige objecten hebben meer eigenschappen dan andere
        * sommige features zijn uniek per browser
        * library: Modernizr

### Modernizr: HTML5 detection lib

* global: Modernizr
* bevat booleans voor elke feature dat het kan detecteren

### Polyfills

* als browser een feature niet ondersteunt: polyfill toevoegen
* JavaScript shim dat een feature repliceert als fallback voor oudere browsers
* er bestaat een polyfill voor bijna iedere HTML5 of CSS3 feature om het in niet ondersteunende browsers te gebruiken
* veel compatibility scripts inladen heeft nadelige invloed op UX
* Modernizr.load()
    * resourceloader: met test, als test fails, via nope script inladen

## Geolocatio nAPI

* biedt scripted toegang tot geografische locatie
* API definieert objecten dat in JS kan gebruikt worden om positie te achterhalen
    * highlevel interface
    * developer hoeft zich geen zorgen te make nover de details over hoe locatie bepaald wordt
    * onderliggend: combinatie van GPS, IP, CellID wordt gebruikt
* API geeft enkel resultaten

### privacy

* W3C
    * gebruikerspermissie nodig
    * moet optie hebben om permissie in te trekken
    * via popup dialog (met sitenaam/URI)

### Positie opvragen

* via geolocation object (zit in navigator object)
* best eerst controleren of het object beschikbaar is
* `geolocation.getCurrentLocation`
    * callbacks instellen
    * param in callback: `position`
        * `position.coords.latitude`
        * `position.coords.longitude`
        * `position.coords.altitude`
        * `position.coords.accuracy`
* `watchPosition` : geolocatio nper interval
    * zal ook direct een waarde teruggeven
    * identificeert de watch operation
* `options`
    * enableHighAccuracy
    * timeout (ms)
    * maximumAge (ms)


## Working Offline

* schokkende verbindinge nzijn achileshiel van netwerken
* apps die enkel af en toe verbinding nodig hebben kunnen resources lokaal opslaan
* offline applicatio ncache
    * om web app offline te kunen gebruiken
    * in manifest file de resources specificeren die offline beschikbaar moeten zijn
    * browser leest manifest en download de resources
* cache
    * reduceert ook bandbreedte
    * zorgen voor offline gebruik
    * werkt sneller

### The Cache manifest

* `<html manifest="application.appcache">`
* elke pagina in web app heeft een manifest attribuut nodig
    * anders wort de pagina niet gecached
* syntax

```
CACHE MANIFEST                  # bestanden die gecached worden
#2014-06-03 v1.0.0              # main html resource moet niet gespecificeerd worden
/thema.css                      # wel alle andere pagina's
http://google.com/logo.png

NETWORK:                        # bestanden die netwerkverbinding nodig hebben
login.asp                       
*                               # *: alles dat niet gelijst wordt, wordt gedownload anneer nodig

FALLBACK:                       # fallback pagina's indien een pagina niet beschikbaar is
/html/ /offline.html            # alle requests naar /html/ wordt offline.html
```

* cache wordt gebruikt, ook al is de app online
* best expliciet alle resources opsommen
    * * werkt niet, zal gewoon iets opvragen met * als naam
    * als er iets niet kan gedownload worden, dan vervalt de volledige cache update
* is niet HTTP caching: 
    * http: 
        * enkel objecten van pagina's die bezocht worden worden gecached
        * browser controleert zelf of resource geupdate is
    * appcache: 
        * browser cached alles dat in manifest aangeduid staat
        * controleert eerst inhoud van manifest om te zien of de cache geupdate moet

### AppCache API

* `window.applicationCache`
* status: staat van cache
* als er manifest gedetecteerd wordt, dan wordt reeks events gestart
    * checking event: vanaf het manifest in <html>  zit
    * als manifest nog nooit gebruikt is geweest
        * donwloading event: om resources te downloaden
        * progress event: om status te toonen
        * cached event: als alles succesvol gedownload is
    * als manifest al eens gebruikt is geweest (ook als taat het op andere locatie/naam, maar dezelfde pagina)
        * noupdate event: als manifest ongewijzigd is tov vorige keer
        * downloading event: als manifest nieuw is
        * progress event
        * update ready event
            * offline beschikbaar
            * nog niet in gebruik
            * pagina was al geladen
            * `window.applicatonCache.swapCache()`

## Web Storage

* Web Storage API : key/value store = DOM storage
* persistente lokale storage zoals native apps
* cookies? beperkte opslag
* verschil:
    * cookies
        * zitten in elke HTTP request
        * vertraagt app door dezelfde data telkens te versturen
        * gelimiteerd tot 4 KB
    * web storage
        * 5MB per origin (protocol/hostname/port)
        * 2 storage areas
            * local: per origin en blijft bestaan als browser gesloten wordt
            * session: per pagina, per venster en gelimiteerd tot levensduur van venster
                * voor verschillende instanties tegelijk te gebruiken zonder dat ze elkaar beïnvloeden

### Interface en datamodel

* `getItem()` en `setItem()`
* ook als associatieve array op te halen/zetten (met [])
* alles wordt als string opgeslagen
    * om andere datatypes eruit te halen: bv `parseInt()` gebruiken
    * zonder expliciete conversie: suboptimale resultaten
    * beter om objecten in JSON om te vormen
        * `JSON.stringify(...)`
        * `JSON.parse(...)`

## Responsive Web Design (RWD)

* sites passen zich aan naargelang de omgeving van de browser
* vooral door aangepaste CSS rules
    * afhankelijk van huidige waarde van browser condities
        * window size
        * orientatie
        * aspectratio
        * ...
    * andere set CSS regels
* met behulp van fluid grid layouts, CSS3 media queries, fluid images en media

### Fluid grid layouts

* relatieve breedtes ipv absolute waarden
* bij verschillende breedtes zal de inhoud zich aanpassen om de beschikbare ruimte op te vullen
* elk element moet schalen volgens de grootte van viewport
* alle relatieve groottes worden geëvalueerd volgens inhoud en ouder element
* hoogtes worden nauwelijk gespecificeerd (nooit echt exact te weten)
    * veritcaal srollen is intuïtiever dan horizontaal

### CSS3 Media Queries

* als breedte te smal wordt, dan volstaan fuild grids niet meer
* bv. 3 kolommen is teveel: enkel middelkolom met inhoud tonen
* media queries
    * media type
    * media features
    * resultaat: true of false
* `@media screen and (min-width: 600px ) {}`
* types: screen, print, braille, aural, all
* features: orientation, min-device-width (scherm toestel), min-width (viewport)

### Fluid images en media

* schalen proportioneel binnen de layout
* afbeeldingen en media blijven binne ngrenzen van hun ouderelementen
    * bv max-width: 100%
* img heeft maar 1 src attribuut
    * niet optimaal
    * <picture> nog geen volledige browsersupport
    *suboptimale methodes via CSS en javascript
    * browsers preloaden toch alle images voordat je CSS en JS uitvoert
    * backendoplossing: nodig dat prloaden voorkomt
        * nog steeds niet optimaal
        * Sencha.io
            * src attribuut naar Sencha servers laten wijzen
            * server zend beste img volgens User-Agent in HTTP-request
* javascript in footer: aangeraden
    * laadt in na HTML en CSS is geparsed
    * mogelijk dat pagina herpaint/reflowed als er een img in DOM toegevoegd wordt
        * browser herberekent dimensies van elementen van pagina en tekent ze opnieuw

### Viewport

* CSS layout (breedte) wordt berekent tegenover layout viewport
* <html> neemt 100% van viewport in
* Apple: viewport groter dan device schermbreedte
    * CSS wordt eerst berekend volgens deze vergrote viewport
    * browser zoomt uit
    * methode overgenome ndoor alle mobile browsers
    * probleem voor developers die hun site responsive wilden maken
        * oplossing: viewport metatag instellen
        * `<meta name="viewport" content="width=device-width, initial-scale=1" >`
            * width aanpassen naar device-width
            * initial-scale: document moet in normale schaal getoond worden

### Flexibel fonts

* fontsize unit: ems
* body beschouwen als 100% = 1em
    * expliciete referentie voor ander fontsizes in CSS
    * gebruiker kan deze zelf instellen wat er gebruikt moet worden als referentie

## Mobile first

* vroeger: graceful degradation: developers begonnen met desktop optimized versie en herwerken deze voor mobiel
* mobile first: progressive enhancement: met nadruk op gelaagde pagina's 
    * eerste laag: semantisch gestructureerde inhoud
    * volgende laag: presentatie (via css) en gedrag (via javascript)
    * ervanuit gaan dat browsers maar enkele features ondersteunen en de extra support bieden voor browsers die meer kunnen
    * in plaats van media queries om functionaliteit over te slaan
        * beginnen van simpelste versie om gradueel aan toe te voegen

### Design rules

* inhoud heeft voorrang op design
    * devs moeten eerst denkenen wat meest relevante inhoud is
    * bv. navigatie kan naar onder verplaatst worden
* image sprites: om afbeeldingen te groeperen in 1 bestand
* CSS en javascript bundelen: concat
    * aantal requests reduceren
* CSS en javascript minifyen
    * whitespace chars verwijderen, newlines,...
* limiteren/verwijderen afhankelijkheid van zware JS libs
* gebruik libs vanaf een CDN
    * grotere kans dat het in cache zit
* gebruik juiste HTTP headers zodat bestanden juist gecached worden in browser geheugen
* indien mogelijk, proberen gebruik te maken van moderne browsers zoals appcache
* gebruik CSS3 voor gradients en ronde hoeken ipv background images

## Hybrid apps

### Single page apps

* web app in 1 HTML pagina: index.html
* alle app logica in javascript
* eens pagina geladen: JS initialiseert de app en rendert de HTML voor UI
* als user navigeert
    * views van app worden geïnjecteert in pagina en eruit  verwijderd 
* interessant voor mobile apps en MVC frameworks
* JS libs moeten enkel geinitialiseerd zijn bij laden van pagina
* geen pagina refreshes: zorgt voor meer native experience
* UI is volledig opgebouwd op clientside, zonder server dependency
* ideaal voor offline apps

### Multipage apps

* werken meer zoals traditionele webpaginas
* elke pagina of HTML file bevat beperkte en discrete set functionaliteit en beperkte client side dynamische updates
    * pagina laadt, inhoud wordt getoond
    * gebruiker interageert en andere pagina wordt getoond
* pagina's kunnen lokaal of van server geladen worden
* nadelen
    * transitie tussen pagina's
    * visuele artifacten bij laden pagina's
    * frameowrk, data, ... moet telkens herladen worden.
    * niet veel verschil met een mobile site
        * apps die sites nabootsen kunnen zelfs geweigerd worden in bepaalde app stores




