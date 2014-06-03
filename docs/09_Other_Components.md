
## Services

* draaien op achtergrond
* hebben geen UI
* nuttig voor acties die een tijdje duren, ongeacht van wat er op het scherm gebeurt
    * bv. muziek app met service om op achtergrond af te spelen
* ~ Windows services of Linux daemons
* android services
    * zijn niet zo low level
    * maken deel uit van de Android app
    * zijn altijd beschikbaar, maar hoeven niet altijd iets actief te doen
* wanneer service gebruiken?
    * als app langdurige en intensieve berekeningen doet dat geen user input vereist
    * als app bepaalde functies herhaaldelijk moet uitvoeren (uploaden, downloaden van nieuwe inhoud)
    * als app langdurige actie moet uitvoeren dat zou onderbrekene als app stopt
    * als app langdurige taak moet uitvoeren terwijl gebruiker naar andere activity gaat
    * als app data of info moet naar buiten brengen als service voor andere apps, zonder dat er UI voor nodig is
* Service `extends Service` (of één van zijn subclasses)
* service heeft enkele methoden om de lifecycle te beïnvloeden
* lifecycle
    * simpeler dan van activity
    * services staan langer aan (<=> activity wordt vaak gestart, gestopt, ...)
    * voor langdurige operaties
* services zijn nodig voor achtergrondtaken en inter proces communicatie tussen apps op hetzelfde toestel 
* achtergrondtaak
    * zenden van email
    * offloaden naar background thread
* inter proces communicatie
* bv vertaal app: service moet text accepteren dat vertaald moet worden
    * alle apps kunnen met de service contact opnemen ipv logica zelf te doen
* service op achtergrond
    * betekent niet noodzakelijk dat het in aparte thread zit
    * service object maakt niet zelf zijn eigen threads automatisch
    * als je service opstart dat veel werk moet verrichten, start het dan op via AsyncTask
        * anders blokkeert het de UI
    * `IntentService` subclass van Service, draait in eigen thread

### Service types

* service component kan in 2 vormen: started en bound
* service component kan op beide manieren werken
    * kan gestart worden (om te blijven draaien)
    * kan ook gebonden worden, afhankelijk van implementatie van callback methodes
* ongeacht of service gestart of gebonden (of beide) is, app component kan de service gebruiken op dezelfde manier dat ee ncomponent een activity kan gebruiken
    * via `Intent`
* service kan geblokkeerd worden voor andere applicaties
    * door het als private te zetten in de manifest

#### started

* via `startService()`
* lifecycle onafhankelijk van component vanwaar het gestart wordt
* kan in pricnipe voor onbepaalde duur blijven draaien
    * service moet zichzelf dus stoppen wanneer taak voltooid is, of ander component kan het stoppen
* bv. 1 operatie dat geen resultaat van oproepende component teruggeeft 

#### bound

* via `bindService()`
* client-service interface: componenten kunnen interageren met de service
    * requests zenden
    * resultaten krijgen
    * IPC
* draait enkel tot component dat verbonden is ontkoppeld wordt
* meerdere componenten kunnen gekoppeld worden
    * pas als er geen gekoppelde componenten zijn, wordt de service destroyed
* belangrijk als er interactie met service nodig is (IPC)

### service lifecycle

* simpeler dan activity
* running of stopped
* beheert door developers, niet  door Android systeem
    * moeten ervoor zorgen dat het niet teveel resources verbruikt
    * android probeert services pas zo laat mogelijk te killen
    * service is beschikbaar tenzij er echt zeer weinig geheugen over is

### Auto-stop service: IntentService

* om service taken gemakkelijk asynchroon uit te voeren
* sluit zichzelf af indien het klaar is
* `IntentService extends Service`
* behandelt asynchroon requests on demand
* wordt gestart als normale service
* voert taak uit in een worker thread
* sluit af als taak voltooid is
* maakt een work queue dat 1 intent doorgeeft per keer aan de `onHandleIntent()` implementatie
    * deze methode draait in een aparte thread
* als alle requests afgehandeld zijn: service stopt automatisch
* bij implementatie `onHandleIntent()`
    * als taak voltooid is: `BroadcastIntent` om te laten weten dat het voltooid is
* `BroadcastReceiver` kan een toast of notification tonen

### Foreground service

* iets waarvan gebruiker kennis van heeft
* geen kandidaat om te killen
* moet een notificatie geven voor de "ongoing" heading
    * notification kan niet weggedaan worden, tenzij de service gestopt wordt of niet meer als foregroundservice werkt
* vb. muziek: notificatie met info over huidige song.

## Content Provider

* beheert toegang tot repository van data
* standaard draait android elke app in eigen sandbox
    * alle data van app is geïsoleerd van andere apps
* kleine porties data kan via Intents doorgegeven worden
* Content Provider beter voor persistente data
* bevat standaard DB methodes
    * `insert()`, `update()`, `delete()`, `query()`
* presenteert data aan externe apps als 1 of meerdere tabellen ~ RDBMS
* rij: instance van type data
* kolom: stuk van data van instantie
* onderliggend moet er geen DB zijn, mag wrapper zijn voor bestand of webservice
* Android bevat meerdere ingebouwde content providers
    * bv. ContactProvider, Media Store, Settings, Browser, CallLog
* Opdeling van data en UI
    * zorgt voor flexibeler systeem om delen van systeem te mash-uppen
* App communiceert met Content Provider via een ContentResolver client object
    * heeft methode die dezelfde methode in provider oproept
    * biedt CRUD voor persistente storage
    * in client app proces
* ContentProvider in app dat de data heeft
    * interproces communicatie tussen Content Resolver en ContentProvider automatisch afgehandeld
    * ContentProvider dient ook als abstractielaag tussen data en externe toegang tot data als tabellen.

### Reading data

* om data van ContentProvider op te halen: URI specificeren
* Content URIs: bevatten symbolische naam van provider (authority) en naam dat verwijst naar de tabel
* volledige naam van authority niet nodig voor ingebouwde Android content providers
    * `contacts` ipv `com.google.android.contacts`
    * URI van personen: `content://contacts/people`
    * URI van specifieke persoon: `content://contacts/people/10`

#### eerste methode

* ingebouwde content provideres hebben contract class met constanten om te helpen te werken met content URIs
* bieden ook constanten voor andere features voor de content provider

#### tweede methode: projection

* array van strings: beschrijven kolommen die je wilt hebben (constanten in contract class)
* mogelijk om criteria voor sorteren, filteren/selectie van rijen te specificeren of `null` (zonder filtering)
* bij `query()` er wordt een cursor teruggegeven
    * implementeert `android.database.Cursor` interface
    * waarmee je object kan ophalen per keer
        * index wordt automatisch bijgewerkt per object dat je ophaalt
    * best asynchroon inlezen: via `CursorLoader` class

## BroadcastReceiver

* om app te registreren voor systeemevents of appevents
* receivers = observer pattern
* alle geregistreerde receivers worden genotifieerd bij een event
* bericht zelf in Android BroadcastIntent
    * kan meer dan 1 receiver opstarten
* systeem broadcast continu events
    * SMS, oproep, lage batterij, systeem wordt gestart,...
* Receivers moeten dan de juiste acties ondernemen
* Apps kunnen gemakkelijk berichten broadcasten via `sendBroadcast(Intent)`
* ontvangende componenten van gebroadcaste Intents:
    * moeten erven van `BroadcastReceiver`
    * `onReceive()` overriden
* receiver moet in manifest geregistreerd zijn
    * aanduiden dat de class geïntereseerd is in bepaalde type broadcast intent

### Receiver Lifecycle

* hebben geen visuele representatie, draaien ook niet actief in geheugen
* worden geïnstantieerd wanneer ze opgeroepen zijn via intent en `BroadcastReceiver` is enkel geldig voor duurtijd van oproep naar `onReceive(Context, Intent)`
* eens `onReceive` returned: wordt gekilled
    * niet mogelijk om iets async te doen
    * draait in main thread: werk limiteren
        * anders: Application Not Responding dialog (ANR dialog)
    * er mag geen dialog of geen service gekoppeld worden in een `BroadcastReceiver`
    * wel: 
        * notificatie tonen via `NotificationManager`
        * backgroundservice starten om event verder af te handelen (via `startService()`, niet via koppeling/binding)






























































