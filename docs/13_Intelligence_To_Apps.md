
## Multimedia serach

### Music search: Shazam

* muziek identificatie
* klein stukje opnemen en vergelijken met database
    * bij match: info over artiest, titel en album wordt teruggegeven
* detectie algoritme
    * elke audiofile is fingerprinted op een manier waarbij hash tokens eruit gehaald worden
    * moet herhaalbaar zijn
* database en sample volgen dezelfde analyse
* fingerprints van sample worden vergeleken met grote set fingerprints uit database
* fringerprinting
    * unieke features uit muziek halen
    *intelligent hashen voor snel zoeken/matchen

#### Feature detection

* temportal location: elke fingerprint moet berekend worden opbasis van zeer kort audiofragment
* translation invariance: fingerprint voor bepaald fragment moet reproduceerbaar zijn onafhankelijk van lengte of positie van fragment in audiobestand.
    * onbekende samples kunnen van elk deel uit originele track komen
* robustness: fingerprints moeten reproduceerbaar zijn van minder goede geluidsbronnen (met ruis)
* high entropy: minimaliseren kans op false matches
* vereisten
    * woren voldaan via perks in spectogram van muziek
    * spectogram: 3 dimensionale grafiek
        * x: tijd
        * y: frequentie
        * ander as: intensiteit van frequentie (amplitude)
* horizontale lijn op spectogram
    * continue pure toon
* verticale lijn
    * onmiddellijke uitbarsting van geluid
* algirtme identificeert pieken in intensiteiten
    * om genoeg dekking te hebben en robuustheid tegen vervormingen wordt in elke tijdsfrequentie telkens de hoogste amplitude piek gekozen
* resulterende plot: sterrenmap/constellatiemap
* patroon van punten moet gelijk zijn voor matchende audio fragmenten

####  Fast combinatorial hashing

* entropie: hoeveelheid informatie van een punt in constellatiemap is relatief laag
* meer info te vinden in combinatie van 2 punten
* fingerprint hashes worden gevormd uit constellatiemap
* paren tijdsfrequentiepunten worden combinatorisch geassocieerd
* er worden ankerpunten gekozen met een target zone
* elk ankerpunt is sequentieel gepaired met punten in zijn target zone
* elk par houdt 2 frequentie componenten + tijdverschil tussen de punten
* hashes zijn reproduceerbaar, zelfs bij ruis en voice codec compressie
    * elke hash past in 32-bit uint
    * elke hash is geassocieerd met tijdsverschuiving tov begin van bestand tot ankerpunt (absolute tijd maakt geen deel uit van hash)
* Track IDs: 64 bit struct: 
    * 32 bits voor hash
    * 32 bits voor time offset en trackID
* snel behandelen: sorteren volgens hash token waarde

#### Searching en scoring

* om te zoeken: fingerprinting van fragment om een set hashtime offsets te berekenen
* elke hash uit samples wordt gebruikt in database
* bij elke matchend hash in database
    * overeenkomende verschuiving tov begin van sample en DB files worden geassocieerd in tijdsparen
    * de paren worden gedistribueerd in bins volgens trackID
    * eens alle sample hashes gebruikt zijn om overeenkomende tijdsparen te vormen, worden de bins gescand voor overeenkomst
    * binnen elke bin representeren de set tijdsparen een scatterplot (spreidingsgrafiek) met associatie tusse nsample en database
    * als bestanden overeenkomen
        * de overeenomende kenmerken moeten op gelijkaardige plaatsen voorkomen
        * sequentie van hashes zou dus ook in database file moete nvoorkomen met dezelfde relatieve tijdssequentie
    * overeenkomst zoeken
        * detecteren van significante cluster punten die een diagonale lijn vormen in een spreidingsgrafiek
        * via heuristieken
