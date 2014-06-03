
## Leg uit welke soorten no-sleep bugs er bestaan, eventueel adhv pseudo-code

* no sleep codepath
* no sleep race condition
* no sleep dilation

## no sleep codepath

* een code pad in app dat een component aan zet
* vb. wakelock verkrijgen maar niet terug in slaapstand zetten
    * geen release van wakelock
* meeste van no sleep bugs
* 3 oorzaken
    * programmeur vergeet te releasen of deet het in een if sectie en niet meer in de else sectie
    * in onverwachte codepad wordt er geen release lock gedaan
        * bv bij exception: finally block gebruiken
    * higher level conditie voorkwam dat code tot bij het punt geraakte van releasen van wakelock
        * bv app level deadlock
        * kan gemakkelijk gebeuren bij smartphones doordat het eventbased is
        * veel mogelijke codebranches maken het moeilijk om alle mogelijke codepaden te anticiperen
* vaak voorkomende oorzaak is dat developers de lifecycle van Android proces niet volledig verstaan
    * veel developers releasen wakelock in `onDestroy()` in plaats van `onPause()`
    * `onDestroy()` wordt enkel uitgevoerd wanneer app component destroyed wordt
    * app wordt niet altijd direct destroyed, tenzij er te weinig resources over zijn.

## no sleep race condition

* bij multithreaded apps
* power management van specifieke componenten in verschillende threads van app
* ene thread zet een component aan
* ander thread zet het uit
* in een bepaald geval kan het gebeuren dat de thread om component aan te zetten later gactiveerd wordt dan de thread die het uit zet
    * race conditie tussen beide threads
* moeilijk op te lossen
    * vereist om alle executievolgordes van threads uit te voeren

## no sleep dilation

* component wordt in slaapstand gezet door app, maar niet op juiste moment ,maar na een substantieel langere periode dan verwacht en/of nodig
* app delay en app optimizations als oorzaak
* app delay
    * bv door GPS driver
    * driver had wakelocks voor langer dan nodig
    * soms geeft de driver een wait, nadat er een wakelock geplaatst werd
    * nadat driver een event krijgt, werd nog een wait gegeven
    * tijdperiode van wakelock werd nog eens verlengd
* slechte plaatsing van component wakeup code in app
    * bv wakelock plaatsen bij starten van app en wakelock verwijderne bij sluiten app
        * component pas nodig als men op knop start drukt
        * effectieve periode wanneer wakelock nodig is veel korter.

