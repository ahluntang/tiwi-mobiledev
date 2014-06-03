
## Bespreek de nood aan abstractie en isolatei in een mobiel OS

* om stabiliteit en veiligheid te waarborgen voor individuele apps en platform
    * apps moeten afgezonderd worden en mogen het platform of andere apps niet storen/belemeren
* hardware abstractie en application isolation geïmplementeerd via Dalvik Virtual Machine en Linux proces permissies
* elke app draait in eigen Dalvik VM in een proces met eigen user- en groepsrechten
* Proces VM:
    * platformonafhankelijkheid (programmeeromgeving)
    * toepassing moet hetzelfde doen op eender welk toestel, onafhankelijk van hardware en software
    * wordt eerst in Javabyte omgezet en dan in Dalvik bytecode (dex)
    * bij runtime wordt de code geïnterpreteerd door de VM en geconverteerd naar machine specifieke instructies
* Security
    * restricties van linux OS: proces en user limieten
    * Android oorspronkelijk voor 1 persoon
    * ipv multi-user support: nieuwe gebruiker en groep voor elke app
    * elke app draait met andere userrechten 
        * bestanden zijn voor enkel toegankelijk voor apps van dezelfde vendor
    * elke app heeft een eigen filesystem silo
* voordelen proces isolatie
    * security: elk proces is gesandboxed en draait in aparte identiteit
    * stabiliteit: als proces zich misdraagt of crashed, dan heeft dit geen gevolgen voor de andere processen
    * memory management: processe ndie niet nodig zijn worden verwijderd
