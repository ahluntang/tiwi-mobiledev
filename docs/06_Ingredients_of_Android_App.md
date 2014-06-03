
* in Java
* elke app bevat één of meerdere loosely coupled componenten
    * manifest bepaalt
        * hoe deze componenten samenwerken
        * app requirements
        * app permissions
* app resources (images, audio, visuals, representation)
    * beschreven in XML bestanden
* componenten, manifest & resources worden verpakt in Android package (`.apk`)

## App components

* bouwstenen van Android app
* elke component is een eigen entiteit met eigen lifecycle
    * heeft een specifieke rol in algemene werking van de app
* 4 types componenten: elk met eigen doel en eigen lifecycle

### Activities

* presentatielaag van de app
* elke activity = single screen met user interface

### Services 

* componenten die op achtergrond draaien
* voor langdurige operaties of achtergrondtaken (onzichtbare)
* heeft geen UI
* bv. voor muziek op achtergrond

### Content Providers

* om data te beheren en onthouden
    * kan op permanente plaats opgeslagen worden
    * FileSystem, SQLite, Web, ...
* via Content Providerkan de data gedeeld worden tussen apps
* Android biedt meerdere Content Providers aan dat nuttige databases blootstelt
    * media store, contacten, ...

### Broadcast Receiver

* componenten die reageren op systeem broadcasts
* apps mogen broadcasts sturen (bv. als een file gedownload is)
* veel broadcasts komen echter van Android systeem zelf
    * scherm uit, lage batterij, foto genomen
* receivers: doen zeer weinig werk

## Managed components

* verschil Android app met normale OS app
    * normale OS app: main entrypoint
        * van hieruit wordt de rest uitgevoerd
        * main beheert alles
    * Android: alle componenten worden beheerd door het systeem
        * elke app draait in eigen Linux proces
        * Android beslist om proces te starten/sluiten
* als een eigen app een activiteit van de camera app start
    * activiteit draait in proces van camera app, niet die van eigen app
    * elke component heeft eigen lifecycle
    * niet nodig dat alle componenten van app tegelijk draaien
    * componenten moeten gestart kunnen worden vanaf meerdere locaties
    * Android framework beheert de staat van elk component

## Intents

* om componenten in andere app te activeren
* bericht zenden naar het systeem dat je 'intent' specificeert dat je een component wilt starten
* het systeem activeert dan het component
* `Intent`-object:
    * met bericht om specifieke component of type component te activeren
    * intent ~ abstracte beschrijving van operatie dat een activity nodig heeft van een andere activity
* zorgt voor loose coupling om activities te starten en resultaten te krijgen
* mogelijk dat meerdere activities geregistreerd zijn om bepaalde operaties uit te voeren
* voor activities & services: intent definieert actie, eventueel met bijkomende data
    * soms wil je resultaat ontvangen, wordt in Intent gestoken
* voor Broadcast receivers
    * intent definieert aankondiging die gebroadcast wordt
* content provider
    * kan niet via intents
    * om veiligheidsredenen

## Resources

* `/res`
    * om gemakkelijk karakteristieken van app te updaten zonder code te wijzigen
* `drawable/`
    * afbeeldingen
* `layout/`
    * XML bestanden die interface layout definiëren (XML per activity)
* `values/`
    * XML bestanden met simpele waarden
    * integers, colors. strings
* alternatieve resources: voor verschillende toestellen
    * qualifier toevoegen aan die subfolders
        * `drawable-xxx/` voor verschillende resoluties
        * `values-nl/` voor vertaling
* toegang tot resources
    * precompiler utility (`aapt`) converteert de toegang tot de resources (maak `gen/` met `R.java` klasse)
    * voor elk type resource is er een `R` subclass
    * om aan de hand van resource ID gemakkelijk aan resource te geraken




    
