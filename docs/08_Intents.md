
* late runtime-time binding tussen componenten in dezelfde of verschillende apps
* bericht-object om een actie te vragen aan een ander component
* android zal de intent leveran aan het juiste component
    * component moet niet in zelfde proces zitten, moet zelfs niet draaien wanneer de intent gemaakt is
* activities en services worde ngeactiveerd via intent (al dan niet gegenereerd door android systeem zelf, als app gestart wordt via homescreen)
* intents kunnen expliciet of impliciet zijn
    * expliciete: class specificeren van component om te laden
    * impliciet: actie bescrhijven dat uitgevoerd moet worden
* intents worden ook gebruikt om berichten te broadcasten doorheen het systeem
    * apps kunnen `BroadCastReceivers` registreen en naar de `BroadCastIntents` luisteren
    * om event driven apps te creëren, gebaseerd op events van systeem, intern of 3rd party apps
    * android `BroadcastIntents`: om systeemevents aan te kondigen (verbinding, batterij)
        * native android apps (zoals Dialer, SMS manager),  kunnen luisteren naar specifieke `BroadcastIntents`
            * zoals binnekomende oproepen of ontvangen bericht
        * je kan ook de native apps vervangen door eigen `BroadcastReceiver` te registeren die luisteren naar dezelfde intents

## Expliciete intents

* specificeren de volledige class
* worden gebruikt om activities of services te starten binnen eenzelfde app
* intent starten met package context in constructor
    * package van app (en componenten) is gedeclareerd in manifest
    * activity van zelfde package starten via constructor
* setClassName: om package met classname te specificeren van component op te starten

## Impliciete Intent

* vernoemen niet specifieke component
* declareren een algemene actie om uit te voeren
    * component van andere app kan dit afhandelen
    * locatie op map tonen: 
* fundamenteel onderdeel van Android
    * zorgt voor decoupling van componenten
    * basis van simpel model om apps naadloos uit te breiden met componenten van andere apps
* android zoekt de juiste component door de inhoud van de intent met intent  filters te vergelijken
* intent wordt abstract beschreven: mogelijk dat er meerdere intent filters compatibel zijn
    * systeem zal een dialoogvenster tonen zodat de gebruiker de juiste app kan kiezen ( en eventueel als default zetten)
    * of kiest de standaard app gedefinieerd odor de user
* informatie in imliciete intents voor de componenten
    * actie om te ondernemen
    * data voor de actie
    * info voor android systeem (categorie van component, instructies over hoe activity op te starten)

### Action

* string: om actie te benoemen
* bij broadcastintents: actie dat gebeurd is, wordt gerapporteerd
* gebruik best action constanten van Intent class
    * `ACTION_VIEW`: om iets te tonen in andere app
* definieert hoe data en extra velden gestructureerd zijn

### Data

* URI van data om op te reageren
    * met MIME type van data
* `ACTION_EDIT`: document URI om iets te bewerken
* `ACTION_CALL`: uri met tel:

### Category

* string: met extra info voor soort component om intent af te handelen
* `CATEGORY_LAUNCHER`: meeste intents vereisen niet een categorie














































































































































































































