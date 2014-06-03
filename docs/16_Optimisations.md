
## Bespreek beknopt alle optimalisaties van build-time tot runtime die Dalvik VM karakteriseren. Maak telkens duidelijk hoe deze specifieke designkeuzes belangrijk zijn voor mobiele devices.

* minimalisatie van geheugengebruik
    * app moet gecompileerd worden in vorm dat zo klein mogelijk is
        * zorgt voor snellere laadtijden en minder opslag nodig
    * geheugenallocatie bij runtime moet geoptimaliseerd worden bij elke app

## Build time: applicatie footprint reduceren

* footprint afhankelijk van grootte van gecompileerde versie
* grootte van bytecode (wordt in memory geladen)
* Java `.class` bestanden worden in nog compactere bytecode gecompileerd
* 3 technieken
    * alle bytecode groeperen in 1 bestand
    * referenties delen over alle klassen
    * compactere instructiecodes

### Grouping in single DEX 

* Java broncode wordt in Java bytecode gecompileerd en opgeslaan in class bestanden
* bestand voor elke klasse ( ook inner class en anonymous classes apart)
* alles wordt in Java ARchive gegroepeerd
* bij android: .class bestanden worden via dx-tool gegroepeerd in 1 dex bestand (Dalvik Executable)
    * apk bevat 1 dex file

### Shared constant pool

* constant pool: array van elementen (string literals, method signatures, classnames, interfaces)
    * constant pool per class file
* elk onderdeel is een entry in constant pool
* grootste deel van class bestand is constant pool (61%)
* alle class files groeperen in 1 dex
    * duplicaten wegwerken
    * herhaling voorkomen
    * meer logische pointers

### Byte code format

* VM instructie in interpreter
    * dispatch instructie
        * volgende instructie ophalen uit geheugen en op basis hiervan de juiste interne functies activeren
        * register VM is iets sneller
    * accessing operands
        * locatie van operands moeten ofwel
            * expliciet in register code zitten
                * code groter
                *  instructie overhead wordt gereduceerd
            * relatief in stack code
                * code is kleiner
                * moet meer VM instructies fetchen
    * performing computation
        * kleinste kost
        * invariante en vaak voorkomende expressions lineair gemakkelijker op register
* dx elimineert constanten door ze inline in te vullen in bytecode
* ambiguous primitive types
    * zelfde opcode voor
        * int/float
        * long/double
        * 0/null
* object references
    * objecten vergelijken: 1 opcode via integer vergelijking
* opslag van primitive arrays
    * Dalvik heeft ambigue opcodes 
        * aget voor primitieven in arrays (int/float)
        * aget-wide voor long/double

## Install time optimizations

* installatie: apk moet uitgepakt worden
    * byte code verifieren en optimaliseren

### Verificatie

* sanity checks: voorkomen van illegaletoegang tot methodes en objecten
* er is geen garantie dat bytecode niet gewijzigd is tussen compilatie en installatie
* gatekeeper: verzekert of code wel mag in Dalvik VM uitgevoerd worden
* dexopt initialiseert een VM instantie en gaat door alle instructies in alle methodes van elke klasse
    * om illegale instructies te detecteren
    * gecheckte classes worden gemarkeerd zodat het niet opnieuw geverifieerd wordt
    * bij dex file wordt een checksum opgeslagen
    * bij runtime moet validatie niet meer opnieuw gebeuren

### Optimalisatie

* na verificatie: optimalisatie via dexopt
* toestel is exact gekend en VM ook
    * dex file kan voorbereid worden om gemapped te worden in geheugen vantoestel
    * symbolische lookups (vtable) kan vervangen worden door hun juiste offset in geheugen
    * opcode kan vervangen worden door VM specifieke operaties
* zeer afhankelijk van doelVM  en host platform
* resultaat : odex, opgeslagen in `/data/dalvik-cache`
    * ongeoptimaliseerde bytecode gemixed met geoptimaliseerde code
* dexopt optimalisaties kunnen enkel maar gedaan worden op hostplatform
    * byte swappen van code voor processor architectuur
    * lege methodes wegknippen en vervangen
    * inline simpele datasturcuters en vaakgebruikte methodes vervangen met toegewijde opcodes
    * symbolische indices vervangen door byte offsets
    * virtuele methode lookups elimineren at runtime door te vervangen met de vtable indices
    * korte datatypes mergen
    * member vars aligneren in 32 bits of 64 bits 

## Runtime: optimalisaties van geheugenallocatie en verbruik

* elke app draait in eigen Dalvik VM
* android kan linux kernel protection mechanisme gebruiken voor geheugen en bestanden af te schermen gebaseerd o UID en GID
* VMs moeten snel kunnen starten en memory footprint van VM moet minimaal zijn
* er zijngroot aantal core library classes en heap structure dat door meerdere apps nodig zijn
* heap structures zijn over algemeen read only
* data en classes dat meeste apps gebruiken maar niet bewerken
* Zygote
    * om code te delen tussen VM instances
    * proces dat start bij opstarten systeem
        * initialiseert eerste VM en laadt en initialiseerd core libs in RAM
    * luistert naar requests op socket: dat het zich moet forken
        * cold start zou te lang duren
* Fork
    * nieuw proces maken met exact zelfde inhoud van parent proces
    * linux kernel implementeert copy-on-write voor forks
    * COW: kopieert niet effectie, maar mapt de pages voor nieuwe proces over deze van parent proces
    * er wordt enkel gekopieerd wanneer een nieuw proces schrijft naar de pagina
    * zoveel mogelijk delen van code
        * terwijl apps elkaar niet mogen interfereren, veiligheid tussen apps en proces grenzen
    * apps schrijven bijna nooit naar de core Java libs
    * enkel 1 kopie van systeemclasses en resources wordt in RAM geladen, onafhankelijk van Dalvik VMs
* Dirty memory
    * nhoud in memory si recenter dan dat it bestand waarop het gemapped wrd
    * kernel moet een actie uitvoeren om het vrij te maken (bv bestnad wegschrijven)
* elke Dalvik VM
    * heeft eigen dex bestand dat gemapped wordt in geheugen
    * heeft eigen privé dirty memory (app heap en beschrijvbare controle datastructures)


### Garbage collection

* geheugen vrijamken
* VM scant de heap en gebruikt mark bits om aant e duiden welke objecten bereikbaar zijn
* heap van elke VM wordt onafhankelijk GC (ook al delen ze geheugen)
* GC moet hiervoor aangepast worden
* mark bits: opslaan in tabel ipv bij objecten op de heap
    * want bij wijziging zou er een COW geïnitialiseerd wroden
    * objecten kunne nzo inshared memory blijven
* private dirty memory minimaliseren
    * markt bits worden gezet net voor een GC en direct nadien vrijgegegven
