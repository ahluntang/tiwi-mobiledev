
* sensing is onderscheidend tegenover gewone computers
* zonder sensing: mobiel toestel zou gewoon een underpowered laptop zijn met hee lklein scherm en rare inputmechanismes
* er is informatie over toestel en omgeving beschikbaar
    * toestel en app kunnn hier gebruik van maken om zich aan te passen aan de context
    * context awareness
* gebruikmaken van omgeving vermindert de limitaties van de formfactor van mobiele devices
    * speech recognition: trage manuele input voor tekst

## sensing capabilities

* gaan verder dan fysieke metingen
* sensors combineren of CPU en cloud gebruiken om toestellen intelligenter en responsiever te maken
* continu meten zou batterijduur vreten
    * energy aware systeemdesign
    * fidelity tegenover systeem resources (batterij)
        * maat van overeenkomst van output met de input
            * accuracy
            * sampling (frequency)
            * resolutie
            * quantisatie
            * lokalisatiefout

## 2 categorieën van sensor

* Motion & position sensors
    * meten acceleratie, krachten, rotaties over 3 assen
    * fysieke positie van toestel
    * accelerometer, gravity, gyroscope, rotatievectors
* Environment sensors
    * temperatuur, druk, licht, vochtigheid
    * barometers, thermometers

## android Sensor API

* 2 types: raw (composite) of synthetic (virtueel)

### Raw sensors (MEMS)

* raw data
* fysieke component in toestel
* Micro Electrical Mechanical Sensors
* miniatuurversie van mechanische en electromechanische elementen via microfabricatie
    * op siliconenchips via technologieën uit de computer(chips)wereld
* MEMS refereert naar feit dat er minstens 1 element met mechanische functies is (onafhankelijk dat ze bewegen of niet)

### Synthetische sensors

* abstractielaag tussen app en code en lowlevel toestelcomponent
* kan meerdere sensoren/data combineren
    * orientatie
    * kompas (NESW)
    * accelerometer (tilt)
* kan raw sensor output wijzigen voordat het die doorgeeft
* betere detectie van orientatie
    * gyroscope gebruiken samen met magnetometer en accelerometer

## Sensing environment

* mogelijk om vochtigheid, luminantie, luchtdruk, proximity, temperatuur te meten
* alle sensoren zijn hardware gebaseerd
    * enkel beschikbaar als hardwarefabriken ze geïmplementeerd heeft
* lichtsensor staat op alle devices
    * voor schermhelderheid
* environment sensor: bij elk event: enkele waarde
    * vereisen meestal geen kalibratie of datafiltering/processing

### types

* `TYPE_AMBIENT_TEMPERATURE`
* `TYPE_LIGHT`
* `TYPE_PRESSURE`
* `TYPE_PROXIMITY`
* `TYPE_RELATIVE_HUMIDITY`

### Ambient light sensor

* vaak zichtbaar op voorkant van toestel
* photodiode werkt zoals LED, maar omgekeerd
    * in plaats van licht genereren als er spanning over gaat
    * genereert spanning als er licht op valt
* waarden in lux
* wordt vaak gebruikt om schermhelderheid aan te passen

### Proximity sensor

* zwakke infrarode LED naast een photodetector
* als iemand dicht genoeg komt bij de sensor, dan detecteert de photodetector de gereflecteerde infraroodlicht
* LED staat niet continu aan, pulseert aan en uit
    * photodetector controleert op deze frequentie
    * sensor negeert dan licht dat niet verandert op deze frequentie
    * vermijdt false positives: zoals verplaatsing van een lichte kamer naar een donkere kamer
    * frequentie kan niet aangepast worden
        * 3rd party sensor die dit allemaal intern regelt
* sommige sensoren kunnen afstand tot object meten
    * anderen kunnen enkel weergeven of een object aanwezig of afwezig is binnen bepaalde threshold
* afstand is afhankelijk van reflectie van object
* sensoren die enkel aanwezigheid van object geven
    * combinatie van helderheid van LED, gevoeligheid van detector & reflectie van object (range: 2-3cm)
    * erbuiten: dan moet sensor ma van range rapporteren
    * erbinnen: juiste afstand rapporteren
* sensors met binary output
    * zijn interruptgebaseerd (geen polling)
    * `onSensorChanged()` callback
        * als proximity staat verandert (transitie)
            * near->far, of far->near
* use case: scherm uitzetten tijdens bellen
    * om flikkeren te voorkomen: threshold
    * binnen threshold blijft vorige staat

### Barometer

* MEMS: altitude op plaatsen waar geen GPS fix is (binnen)
* enkel beschikbaar op bepaalde toestellen
* misverstand: over meten van druk van vinger op scherm of luchtdruk
    * omdat er `MotionEvent.getPressure()` methode bestaat
        * is voor druk van vinger op scherm
* MEMS druksensor: drumvel op kamer met gekende druk binnenin
    * als buitendruk verandert, dan zla vel buigen
        * resunante motion, lucht dat de motion belemmert wordt gemeten
    * gerelateerd aan luchtdichtheid ddat gerelateerd is aan luchtdruk bij bepaald temperatuur
* altitude: kan berekend worden via luchtdruk via `SensorManager.getAltitude()`
    * geeft altitude boven zeewater, gebaseerd opd e gemeten druk \\( P \\) en druk op zeeniveau \\( P_0 \\)
    * \\(P_0\\): standaarddruk via constante `PRESSURE_STANDARD_ATMOSPHERE`
        * geeft deftige resultaten voor relatieve hoogtes
        * niet voor absolute hoogte
* effectieve zeeniveau druk
    * gerapporteerd door luchthaven of weerstation
    * geeft betere resultaten (relatief en absoluut)
    * moelijker te implementeren
    * als beter eaccuraatheid nodig is
* druksensoren zullen vooral gebruikt worden voor meten van veranderingen in hoogte: zowel absoluut als relatief
* absoluut
    * sneller dan GPS fix
    * druksensor is snel, maar minder accuraat
* relatief
    * om te weten op welke verdieping je bent door verandering in druk
    * sensoren zijn gevoelig genoeg om zo'n verschil op te merken
    * bij trap naar boven/onder: luchtdruk fluctueert snel genoeg om het te detecteren
* Sensing Weather: niet zo gebruikelijk, weerstations hebben betere barometers

### Relative humidity

* % waterdamp in lucht
* hoeveelheid waterdamp in lucht vergeleken met max waterdamp dat lucht kan hebben voor een bepaalde temperatuur (100%)
* waarde is humidity die ook gebruikt wordt in weerberichten
* relatieve vochtigheid kan samen met temperatuur gebruikt worden om dauwpunt en absolute vochtigheid te berekenen
    * dauwpunt: temperatuur waarbij waterdamp condenseert
    * absolute vochtigheid: massa water in volume lucht

### Ambient temperature

* kamertemperatuur in Celsius
* vervangt `SENSOR_TYPE_TEMPERATURE` (deprecated)

## Device orientation & movement

* orientatie en movement gemeten via inertial sensors
* sensoren beschrijven wat het toestel doet tov de omgeving
* sommige zijn volledig hardware gebaseerd
* andere halen data uit andere sensoren en berekenen de juiste gegevens
* `TYPE_ACCELEROMETER` motion detection 
    * hardware: versnelling \\( \frac{m}{s^2} \\) over 3 assen (inclusief z)
* `TYPE_GRAVITY` motion detection
    * software of hardware: zwaartekracht \\( \frac{m}{s^2} \\) over alle 3 de assen
* `TYPE_GYROSCOPE`
    * hardware: meet rotatiesnelheid in rad/s over 3 assen
* `TYPE_LINEAR_ACCELERATION`
    * software of hardware: versnelling in \\( \frac{m}{s^2}\\) op 3 assen, **zonder zwaartekracht**
    * om versnelling over 1 as te meten
* `TYPE_MAGNETIC_FIELD`
    * hardware: geomagnitisch veld voor 3 assen in \\( \mu T \\)
    * voor kompas
* `TYPE_ROTATION_VECTOR`
    * software of hardware: meet orientatie van toestel via rotatievector
    * voor motion en rotation
* motion sensors: voor tilt, shake, rotation, swing
* beweging is meestal als gevolg 
    * van user input
    * of van omgeving (rijdende auto)
* bij userinput
    * beweging relatief tegenover frame van toestel als referentie
* bij omgeving
    * beweging relatief tegenover frame van wereld als referentie
* worden niet gebruikt om positie te bepalen
    * kunnen gebruikt worden met andere sensoren (geomagnetic)
    * om relatieve positie tegenover wereldframe te bepalen

### Device coordinate system

* gedeeltelijk bepaald door standaardorientatie van toestel
    * afhankelijk van type toestel
    * smartphones: portrait
    * tablets: landscape
* coordinatensysteem is vast op toestel
    * wordt niet aangepast als orientatie verandert
* sensor: altijd gebaseerd op natuurlijke/default orientatie van toestel
* beweging kan niet enkel in translaties beschreven worden langs de 3 assen
    * ook rotaties langs de assen
    * rotatie langs verticale ass: yaw/azimuth/heading
    * rotatie lans X-as: pitch
    * rotatie langs Y-as: roll
    * positief: rechterhandregel

![](/assets/coordinatesystem.jpg)

### Accelerometer
* meet versnelling van toestelling (variatie op versnelling van sensor)
* waarden van sensor kunnen aangeven dat er 
    * versneld/vertraagd wordt in een rechte lijn
    * geschud werd
    * scherp gedraaid wordt in auto
* ook stilstaande massa's worden beïnvloedt door ee nconstante neerwaartse versnelling (zwaartekracht)
* \\( F=ma \\): als er kracht is, dan is er een versnelling evenredig met massa van object
* intern: kleine massa's op kleine veertjes
    * versnelling wordt gemeten als de afstand dat de massa afwijkt van centrale positie
* accelerometer meet juiste versnelling
    * is niet vector som van alle krachten werkend op de sensor
    * maar versnelling dat het ondergaat relatief tot de waarnemer in vrije val
    * meet dus ook de zwaartekracht
* vrije val:
    * zwaartekracht werkt nog steeds naar beneden
    * maar de massa en frame errond hebben dezelfde versnelling zodat de accelerometer 0g meet
* accelerometer gebruikt standaard coordinaat systeem
    * als het neer ligt: \\(x=0, y=0, z=+ 9,81 \frac{m}{s^2} \\)
    * bovenwaartse versnelling tov waarnemer in vrije val

### Accelerometer, gravity sensor, linear accelerometer

* accelerometer via `Sensor.TYPE_ACCELEROMETER` als me nsensor opvraagt via `SensorManager`
* goede sensor om beweging van toestel te controleren
* verbruikt heel weinig energie
* staat op de meeste Android toestellen
* waarden zijn inclusief effect van zwaartekracht op versnelling
    * om dit weg te werken: high pass filter op raw data uitvoeren
        * vermindert de offset dat er kwam doorzwaartekracht
    * richting en grootte van zwaartekracht kan via low pass filter
        * 2 sensor types beschikbaar om deze componenten apart te verkijgen
* `Sensor.TYPE_GRAVITY`
    * geeft richting en grootte van zwaartekracht via 3 dimensionale vector
* `Sensor.TYPE_LINEAR_ACCELERATION`
    * 3 dimensionale vector met versnelling langs elke as, zonder zwaartekracht
    * als je versnelling wilt weten zonder invloed van zwaartekracht
    * bv. om te zien hoe snel de auto rijdt
    * heeft altijd een offset
        * offset verwijderen: kalibratie in app steken
        * gebruiker vragen om toestel op tafel te leggen
            * offsets lezen op alle assen
            * telkens deze aftrekken van accelerationsensors output

### Gyroscope

* ook kleine massa's op kleine veertjes
* ipv versnelling wordt er andere kracht gemeten: Corioliskracht
* Corioliskracht: neiging van object om af te wijken van een beweging binnen roterend systeem
* gyroscopen: trillend
    * een trillend object neight om verder te trillen op zelfde vlak dat roteert
    * als gyroscoop roteert, dan zal Corioliskracht de massa doen afwijken van richting dat het aan het trillen was
    * start te bewegen langs andere as
    * deze beweging langs de andere as wordt elektrisch waargenomen via capaciteitsplaten
        * ene plaat hangt vast aa nframe
        * andere aan massa
* recente implementaties: gyroscoop en accelerometer geïntegreerd in 1 IC
* corioliskracht werkt enkel als toestel roteert
    * meten dus enkel hoeksnelheid ( snelheid waarmee toestel roteert in rad/sec)
* als toestel stilstaat
    * alle 3 assen zullen 0 meten
* coordinaatsysteem van sensor: positief roteren is tegen de klok
    * als men vanuit positieve zin in as kijkt
    * range: tot 35 graden/sec
* hoek berekenen: integreren: gyroscoopruis zorgt voor grote fouten
    * compsenseren via andere sensoren (gravity of accelerometer)

### Geomagnetic field sensor

* magnetisch veld van aarde meten
* `Sensor.TYPE_MAGNETIC_FIELD`
* sensoren meten magnetisch veld over 3 assen (3 sensoren)
    * sterkte in \\(\mu T\\)
* resultaten niet zo accuraat vergeleken met andere sensoren
    * normaal moet het het absolute magnetisch veld meten van de aarde
    * in praktijk: gemeten waardes veranderen over tijd naargelang huidige lokale magnetische omgeving en geschiedenis van toestel


#### Hall effect sensor

* stroom door draad
* magnetisch veld component loodrecht op draad
* zorgt ervoor dat elektronen aan 1 kant van draad hogere dichtheid hebben
* spanning dat evenredig is met magnetisch veld

#### Lorentz kracht sensors

* werken op gelijkaardige manier
* meten mechanische afbuiging van draad ipv spanning

### Absolute orientation

* sensoren die gebruikmaken van absolute orientatie gebruiken as
* volgens aarde:
    * centrum in punt op oppervlak aarde
    * x = horizontaal
    * y = verticaal
    * z = loodrecht op oppervlak van aarde
* absolute orientatie beschreven via rotatiematrix
    * beschrijft benodigde rotatie om globale coordinatensysteem overeen te laten overeenkomen met coordinaten van toestel
    * bv. als toestel plat op grond ligt in portait mode met bovenkant naar noorden
        * matrix = eenheidsmatrix
* rotatiematrix te verkrijgen door waarden van accelerometer en magnetometer door te geven aan `SensorManager.getRotationMatrix`
* ook te verkrijgen als je sensor `Sensor.TYPE_ROTATION_VECTOR` gebruikt
    * synthetische sensor: berekent rotatiehoek rekenhoudend met coordinaatsysteem van accelerometer, magnetometer en gyroscoop
    * gebruikt dus meerdere sensoren om juiste data te genereren
* rotatievector
    * representeert orientatie van toestel als combinatie van hoek en as: toestel is geroteerd om hoek \\(\theta \\) rond ass x,y,z
* alternatieve representatie: quaternion
* rotatievectors omvormen naar rotatiematrix via `SensorManager.getRotationMatrixFromVector`
* daarna toestel orientatie gebaseerd op rotatiematrix berekenen van `SensorManager.getOrientation()`
    * orientatie is tegenover aarde
    * Noord, Oost, Zuid of West
    * tilt hoek met platte grond
* `getOrientation()` heeft x en y as geïnverteerd van globale coordinaatsysteem (om historische redenen)
* Pitch, roll en azimuth: volgens rechterhandregel
* `values[0]`: Azimuth/healing/yaw
    * rotatie rond z-as
    * platliggend toestel, met bovenkand naar
        * noorden: \\( 0 rad \\)
        * oosten: \\( \frac{\pi}{2} rad \\)
        * westen: \\( -\frac{\pi}{2} rad \\)
        * zuiden: \\( \pi rad \\)
* `values[1]`: pitch
    * rotatie rond x-as
    * platliggend toestel:  \\( 0 rad \\)
    * recht op grond zetten: \\( - \frac{\pi}{2} rad \\)
    * omgekeerd op zetten: \\( \frac{\pi}{2} rad \\)
    * met voorkant op grond: \\( \pi rad \\)
* `values[2]`: roll
    * rotatie rond y-as
    * platliggend toestel:  \\( 0 rad \\)
    * rechterkant opheffen: \\( - \frac{\pi}{2} rad \\) (scherm wijst naar westen)
    * linkerkant opheffen: \\( \frac{\pi}{2} rad \\) (scherm naar oosten)
    * scherm naar onder: \\( \pi rad \\)


