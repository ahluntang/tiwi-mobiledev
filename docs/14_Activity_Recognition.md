
* mensen ingerageren met toestellen in dagelijks leven
* ubiquitous sensing: onderzoeksgebied voor kennis te halen uit data van pervasive sensors
    * herkenning van menselijke activiteiten
        * medische doeleinden
            * patienten met diabetes, obesitas, hartziekten moetn elk een vastgelegde routine volgen als onderdeel van hun behandeling
        * militair
        * beveiliging
    * activiteiten: wandelen, lopen, fietsen
        * om door te geven aan verzorgen die het kan interpreteren en feedback geven
    * activiteiten die abnormaal zijn detecteren, bv voor dementerenden of andere mentale pathologieën om ongewenste gevolgen te voorkomen
    * soldatenL activiteiten, locaties en gezonddheid meten voor hun performantie en veiligheid

## Human Activity Recognition: HAR

* nog steeds veel obstakels
* nieuwe technieken nodig om accuraatheid te verbeteren bij realistische use cases
* uitdagingen
    * attributen selecteren die gemeten moet worden
    * mobiel, niet opvallende en goedkope data verwervingssysteem
    * kenmerken herknnen en conclusie methodes
    * flexibiliteit om nieuwe gebruikers t e ondersteunen zonder het systeem opnieuw te trainen
    * implementatie in mobiele toestellen (aangepast energieverbruik)
* 2 types: externe en wearable sensors
    * extern: toestellen op points of interests, activiteiten herkennen hangt af van vrijwillige interactie van gebruikers met sensoren (bv intelligente huizen)
        * werkt niet als gebruiker buiten bereik is
        * huishoudtoestellen, kranen en cameras
            * camera: activiteiten van beelden herkennen
    * wearable: sensoren hangen aan gebruiker
* vb. kinect: interageren met games via gestures, zonder controller
    * problemen: 
        * privacy: niet iedereen wilt permanent opgenomen worden
        * pervasiveness: video recording zijn moeilijk te hechten aan gebruikers om lichaam te controleren
        * complexiteit: video processing is duur (vergt veel rekencapaciteit)
            * hindert schaalbaarheid van HAR
* HAR: wearable sensors
* meeste van de gemeten attributen hebben betrekking tot beweging van gebruiker (accelerometer/GPS), omgeving (temperatuur/vochtigheid) of fysiologische (hartritme) signalen
* data wordt geïndexeerd over tijd zodat menselijke activiteit herkennen gedefineerd wordt als
    * set S met k tijdreeksen (elk van specifiek gemeten attribuut binnen een tijdsinterval I)
    * doel: tijdelijke partitie van I vinden uit data in set S en labels die activiteit representeert gedurende elke interval in I
    * activiteiten mogen niet simultaan gebeuren: persoon kan niet wandelen en lopen op zelfde moment
* kan niet deterministisch opgelost worden
    * aantal combinaties van attributen en activiteiten kan zeer groot zijn
    * transitiepunten vinden wordt moeiljk aangezien lengte van elke activity onbekend is
    * via machine learning tools activiteiten herkennen
    * relaxed versie: tijdreeksen opdelen tijdsintervallen van vaste lengte (tijd)

## Relaxed HAR

* Set W van m tijdsintervallen van gelijkke grootte (volledig of gedeeltelijk gelabeld)
* elke tijdsinterval bevat set van tijdreeksen S ven elke van de k gemeten attributen
* set A van activity labels
    * mappingsfunctie zoeken waarbij tijdreeks op juiste label afgebeeld wordt voor alle mogelijke tijdreeksen
    * zodat de functie op tijdreeksen gelijkaardig is aan de activiteit tijdens een tijdsinterval
* relaxation: zorg voor een fout in model tijdens transities
    * gebruiker kan meerdere activiteiten doen tijdens 1 tijdsinterval
    * aantal verwachte transities is veel kleiner dan totaal aantal tijdsintervallen
        * relaxation errors zijn insignificant voor meeste toepassingen
* design van HAR systeem hangt af van activiteiten die herkend moeten worden
    * als je activiteiten (set A) verandert, dan wordt de HARP een compleet ander probleem
* type activiteiten gegroepeerd volgens HAR systeem
    * Ambulation: wandelen, lopen, zitten, staan liggen, klimmen
    * Transport: bus, fiets, rijden
    * Telefoongebruik: SMS, bellen
    * Dagelijkse activiteiten: eten, drinken, lezen
    * Oefeningen/fitness: roeien, ewichtheffen, spinning, push-ups
    * Militair: kruipen, knielen, situatiebeoordeling
    * Bovenlichaam: kauwen, spreken, slikken, hoofd bewegen

## General structure

![](/assets/har_structure.png)

* activity herkenning: 2 stages: training & testing/evaluation
* Training stage
    * vereist tijdreeks dataset van gemeten attributen van individuen die elke activiteit doen
    * tijdreeksen worden gesplits in tijdsintervallen om kenmerken eruit te halen
    * relevante informatie filteren uit de raw signals
    * learning methods: om herkenningsmodel voor activiteit te genereren uit de dataset van eruitgehaalde kenmerken
* Testing
    * data wordt verzameld
    * kenmerken worden eruit gehaald
    * kenmerk wordt geëvalueerd met model uit trainingstage
        * genereert een voorspelling: activity label
* Wearable sensors worden geplaatst op het lichaam om de gewilde atrributen/kenmerken te meten
* sensoren moeten met elkaar communiceren via een integrationdevice
    * smartphone, laptop,...
* doel integration device
    * data dat ontvangen werd preprocessen
    * doorsturen naar server voor realtime monitoring, visualisatie

## Design issues

* 7 grote issues
    * selectie van kenmerken en sensoren
    * opdringerigheid (obtrusiveness)
    * data verzamelingsprotocol
    * herkenningsperformantie
    * energieverbruik
    * processing/verwerking (op integration device? op server?)
    * flexibiliteit (specifiek vs monolithic herkenningsmodel)
        * specifiek: gebouwd voor specifieke individueel
            * systeem moet getraind worden voor elke nieuwe gebruiker
        * monolithic:  flexibel om te werken met meerdere gebruikers

## Herkenningsmethoden voor activiteiten

* voor herknning van menselijke activiteiten moet de raw data eerst door feature extraction process gaan
* herkenningsproces is dan gebouwd uit set van features via machine-learning
* eens model getraind is: ongeziene instanties (tijdsintervallen) kan geëvalueerd worden in herkenningsmodel
    * zorgt voor voorspelling van activiteit

### Feature extraction

* activiteiten worden vaak gedurende relatief lange tijd gedaan (in vergelijking met sampling rate van sensor, ~250Hz)
* activiteiten moeten herkend worden in tijdsintervallen ipv per sample
* hoe twee tijdsintervallen vergelijken?
    * signalen zijn bijna nooit eact hetzelfde, zelfs als het van dezelfde persoon komt
* waarom feature extraction?
    * relefante informatie filteren
    * kwantitatieve meting verkrijgen zodat signalen vergeleken kunen worden
* 2 methoden: statistisch en structureel
    * statistisch
        * Fourier transformatie of Wavelet transformatie
        * kwantitatieve karakteristieken van data om features eruit te halen
    * structureel
        * houdt ook rekening met interrelaties in data
        * raw dataset met tijdreeksen wordt getransformeerd in set van feature vectors
        * elke instantie van de verwerkte dataset correspondeert met een featurevector dat uit alle signalen van een tijdsinterval gehaald werd
* Features:
    * Time domain:
        * altitude, vochtigheid, temperatuur
    * Time domain & frequency domain: barrometerdruk, licht
    * speech recognition: audio

### Selectie van attirbuten en sensoren

* 4 groepen van attributen
    * omgeving
    * versnelling
    * locatie
    * fysiologisch

#### Omgeving

* temperatuur, vochtigheid, audio
* context info over omgeving van gebruiker
* sensors individueel: misschien niet voldoende info
    * bepaalde activiteit kan onder diverse condities gebeuren

#### Versnelling

* om stappen, lopen, liggen te herkennen
* acelerometer: goedkoop en energie-efficiënt
* goede herkenning
* niet alle dagelijkse taken kan via versnelling herkend worden
* studies over herkenningsgraad bij 
    * verschillende sampling rate
        * geen grote verbetering eens je boven 29Hz gaat
    * amplitude
        * +/- 2g is genoeg om activiteit te herkenning
    * plaatsing accelerometer: afhankelijk van toepassing
        * broekzak, tas, riem, pols,...

#### Locatie

* locatiegebaseerde services op veel smartphones
* handig voor context aware apps: transport van gebruiker
* locatie van gebruiker kan bepalen wat activiteit is
* GPS is energieverslindend, wordt vaak gebruikt in combinatie met accelerometer
* privacy issues

#### Fysiologisch

* data over vitale functies (hartsnelheid, ademhaling, huidtemperatuur)
* vitale functie kunnen enkel gebruikt worden om accuraatheid te verbeteren

### Learning

 * data verzamelen is niet genoeg
 * veel applicaties vereisen meer
    * raw data van sensors zijn meestal onbruikbaar
    * HAR gebruikt hiervoor machine learning tools
        * om patronen te beschrijven
        * analyseren van data
        * data voorspellen
* patronen worden ontdekt uit training set
    * voorbeelden in training set kunnen gelabeld zijn
* 2 learning methodes
    * supervised (labeled data) en unsupervised (unlabeled data)
    * meesta HAR werken in supervised mode
    * het is moeilijk om activiteiten te onderscheiden in unsupervised mode
    * soms is het ook gedeeltelijk gelabeld: semi-supervised

#### supervised

* beslissimgsboom
    * hierarchisch model opbouwen waarbij 
        * attributen afgebeeld worden op knopen
        * waarden op takken/verbindingen
        * elke weg vanuit wortel naar blad iseen classificatieregel

#### semi-supervised

* als labelen vna data moeilijk is
* als individuen niet volledig kan/wil meewerken aan verzamelingsproces





