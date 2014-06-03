
* component van app
* levert scherm waarmee gebruikers kunnen interageren
* elke activity krijgt een scherm waarvan het zijn UI kan tekenen
* UI van activity vult meestal het volledig scherm van toestel
    * kan ook kleiner zijn en boven andere vensters zweven
* app bestaat uit meerdere activities die loosely bound zijn
* 1 activity wordt als `main` activity beschouwd
    * wordt als eerste opgestart als app opstart
* gebruikers kunnen navigeren van activity naar activity
    * om te navigeren naar activity in andere app: Intent gebruiken
      (kan ook gebruikt worden om activity binnen de app te starten)

## Activity back stack

* Android sorteert alle activities in een back stack
    * volgens vologrde wanneer de activity geopend wrd
* als user een app start uit de app launcher
    * main activity wordt toegevoegd aan back stack
* als huidige activity een nieuwe start
    * nieuwe activity wordt bovenop de stack gepushed
    * vorige activity blijft op de stack (maar is gestopt)
    * gestopte activity: staat van UI wordt bijgehouden door systeem
* `back` knop: LIFO
    * huidige activity wordt verwijderd van stack (destroy)
    * vorige activity resumed (staat  van UI wordt hersteld)
* volgorde van activities in stack verandert nooit
    * enkel push en pop
    * als activity vanuit meerdere andere activities gestart kunnen worden, dan wordt telkens een nieuwe instantie van de activity gecreëerd en op de stack gepushed (ipv vorige instantie van activity bovenaan te brengen)
        * staat meerdere keren in stack
        * bij back knop wordt het meerdere keren getoond (elk met eigen UI staat)
* 1 back stack is niet genoeg
* tasks:
    * wordt gestart als gebruiker op een launcher icon drukt
    * collectie van activities waar gebruiker mee werkt om een taak te voltooien
    * elke task heeft eigen back stack
* als nieuwe taak begint, wordt de oude op achtergrond geplaatst
    * al de activities van background task worden gestopt
    * back stack blijft intact
* home knop
    * huidige task gaat naar achtergrond
* als gebruiker dan op launcher icon drukt
    * als er ee nachtergrondtask is: die wordt naar voorgrond gebracht met zijn back stack
    * anderes nieuwe task
* activities kunnen meerdere keren geïnstantieerd worden, zelfs van verschillende taken.

## Life cycle

* activities starten kunnen intensief zijn
    * kan nieuw Linux proces starten: geheugen reserveren voor alle UI objecten, alle objecten inflaten vanuit XML layouts en sterm opstellen
* gebruikers navigeren heen en weer tussen activities
    * volledige initialisatie telkens doen zou zeer inefficiënt zijn
* elke activity heeft een life cycle dat beheerd wordt door `ActivityManager`
* `ActivityManager`: systeemservice dat verantwoordelijk is voor
    * creëren, destroyen & managen van activities en hun staat
    * houdt vorige activities in memory bij zodat het later sneller gestart wordt
    * destroyed activities (die langst niet gebruikt wrdt eerst) om geheugen vrij te maken

### Resumed/running

* op voorgrond en heeft user focus
* er is enkel 1 running activity op het moment
* heeft hoogste prioriteit (voor memory en resources)    

### Paused

* als andere activity op voorgrond zit en focus heeft, maar deze nog steeds deels zichtbaar is
* dialogboxes
* opm: om activity te stoppen, wordt activity ook eerst gepauzeerd
* hebben nog steeds hoge prioriteit

### Stopped

* activity is niet meer zichtbaar en andere activity heeft focus
* stopped activity wilt niet noodzakelijk zeggen dat het niets doet
    * kan als backgroundproces draaien
* stopped activities worden in memory behouden
    * mogelijk gaat de user terug willen naar deze activity
* zijn eerste kandidaten om destroyed te worden als er te weinig memory is
* destroyed: bij herstarten moeten ze terug volledig gecreëerd worden
* activity: 
    * code dat gestart/gestopt worden aan de hand van UI interacties

### Lifecycle methods

* activity krijgt melding als het in andere staten gaat
    * via callbacks
    * overriden om bepaald werk eerst uit te voeren (vb. video pauzeren)
* 3 geneste loops in lifecycle
    * volledige lifecycle ligt tussen `onCreate()` en `onDestroy()`

![](/assets/application_states.png)

#### Visible lifetime

* tussen `onStart()` en `onStop()`
* hiertussen resources bijhouden om activity te tonen aan de user

#### Foreground lifetime

* tussen `onResume()` en `onPause()`
* activity kan vaak wisselen van voorgrond naar achtergrond
* code in methode moet lightweight zijn

#### onCreate()

* activity is aangemaakt, initialisatie
* views aanmaken, persistente databestanden openen

#### onStart()

* net voordat activity op scherm verschijnt

#### onResume()

* net wanneer activity voorgrond activity wordt

#### onPause()

* wanneer activity gestopt wordt en niet meer zichtbaar op voorgrond
* laatste methode dat zeker opgeroepen wordt voordat proces gekilled kan worden
    * `onStop()` en `onDestroy()` wordt niet opgeroepen als systeem killed voor memory
* veel blocking code zorgt voor trage transities

#### onStop()

* wanneer activity niet meer zichtbaar is
* als andere activity op voorgrond komt of als activity destroyed wordt

#### onDestroy()

* wanneer activity afgewerkt is en mag destroyed worden.
* methode wordt niet altijd opgeroepen
* systeem kan gewoon proces terminaten

### Saving activity State

* staat van activity wordt in geheugenbewaard als die gepauzeerd of gestopt worden
* bij destroy (als systeem memory nodig heeft) is staat ook weg
    * activity zit wel nog in back stack
    * als activity naar voor/boven op stack gebracht werd, moet het systeem de activity opnieuw maken
    * gebruiker moet niet weten dat activity destroyed werd
    * state moet hersteld worden: van `onSaveInstanceState()`
        * voordat het destroyed wordt
        * systeem zal het `Bundle` obejct doorgevan aan `onCreate()`
* als `Bundle == null`: niets om te restoren
* activity staat kan zo over meerdere instanties van Activity restored worden
* gebruiker denkt dat hij verderwerkt met iets, maar het kan een volledige nieuwe instantie zijn
* niet altijd nodig om `onSaveInstanceState()` te overriden
    * standaard wordt al een heleboel bewaard (grafische componenten,... )
    * grafische componenten moet je ook niet zelf restoren (onmiddellijk na `onStart()`)
* `onSaveInstance` wordt niet altijd opgeroepen
    * bv. van Activity B op back drukken naar activity A
        * B wordt destroyed
        * B wordt niet meer restored
        * niet nodig om state te bewaren
* enkel UI staat afhandelen (transient state) in deze methode
* nooit om persistente data op te slaan: `onPause()` gebruiken

### Cahcing device configuration

* device config zoals schermoriëntatie kan veranderen terwijl activity draait
* tijdens zo'n verandering zal Android de activity opnieuw maken
    * door `onPause()`, `onStop()`, `onDestroy()` op te roepen
    * gevolgd door `onCreate()`, `onStart()` en `onResume()`
* grootste deel van creatie is toch UI opbouwen

## Building UI

* via XML files in `res/layout`
* kan aangepast worden zonder code recompilen
* kan voor verschillende schermgroottes en orientatie gemaakt worden
* elke `View` krijg een `ID` (zodat de code een referentie heeft)
    * in `android:id`: @+id/waarde
        * @ : directive voor XML parser om het te expanden
        * + : nieuwe resource naam dat toegevoegd moet worden aan R
* maten: in dp (density independent pixels)
* `onClickListener` class
    * om `onClick` van UI element in te stellen

### Inflating layout in sourcecode

* elke XML layout wordt gecompileerd in een resource toegankelijk in `R.java`
* inflater: resource laden via `setContentView` in `onCreate()`
    * juiste referentie meegeven: `R.layout.bestandsnaam`

### Common layouts

* `ViewGroup` subclasses
    * elk met unieke manier om contained `Views` te tonen
* layouts kunnen genest worden
    * vergroot drawing tijd
* `LinearLayout`: horizontaal of verticaal rangschikken
* `ListView: scrollable items
* `RelativeLayout`, `ImageView`

## Supporting multiple devices & orientations

* screen sizes: 
    * phone: 3" tot 5"
    * tablet: 7" tot 12"
* oriëntatie: portret en landschap
* devs: moeten meerdere layouts maken voor verschillende sizes en orientaties
    * gebruiker krijgt gevoel dat app voor devices specifiek gemaakt zijn
* fragments: om gemakkelijk aan te passen welke componenten in 1 activity/screen te tonen afhankelijk van screen size
* standaard resized android het layout voor het scherm
* er kunnen ook qualifieres gebruikt worden om specifieke layouts te definiëren voor bepaalde schermen

### Creating different layouts

* om de UX te optimaliseren
* qualifiers
    * `-port` en `-land` voor orientaties
    * `-sw600dp`
        * sw: smallest width
        * dp: density independent pixels
* in textSize: `@dimen/text_size` oproepen
    * die kan je dan definiëren in `res/values/dimens.xml` en `res/values-sw720dp/dimens.xml`

## Fragments

* activity: vult volledig scherm
    * gebruikers gaan van scherm naar scherm on verschillende onderdelen van het UI te bekijken
    * activity ondersteunt de back stack om snel en intuïtief navigeren volgens boomstructuur
* op grotere schermen: meer plaats voor componenten
    * sommige componenten blijven op het scherm doorheen de navigatie
    * simpele stack is niet voldoende
* layout onderverdelen in "mini-activities": Fragments
* om activities in fragmenten te verdelen
    * herbruikbare modules
    * elk met eigen interface en lifecycle
* afhankelijk van schermgrootte: fragments toevoegen of verwijderen in activities of portrait/landscape mode
* fragment: atomair deel van UI van Android app
    * kunnen dynamisch toegevoegd/verwijderd worden: voor betere UX
    * moet in context van activity

### Fragment lifecycle

* fragment maken: `Fragment` extenden
* lifecycle callback methodes implementeren
    * 3 staten: resumed/running, paused & stopped
* wordt beïnvloed door lifecycle van activity waarin fragment zit
    * als activity gepauzeerd is: alle fragments erin zijn gepauzeerd
    * als het niet meer zichtbaar is: wordt gestopt
        * doordat activity gestopt is
        * of als fragment van activity verwijderd werd
    * alle info wordt in memory bewaard, fargment wordt gekilled als activity gekilled wordt
* verschil met activity lifecycle
    * manier hoe het in backstack opgeslagen wordt
    * activity wordt in backstack gestoken met activities dat gemanaged worden door systeem
    * fragment wordt in backstack gestoken dat gemanaged wordt door host activity als je het expliciet vraagt om op te slaan
        * via `addToBackStack()`
        * als je fragment wilt verwijderen

### Fragment Layout

* elke fragment heeft eigen XML layout

### Fragment code

* Java klasse: laadt de UI vanuit XML file
* extends `Fragment`
* `onCreateView()` om UI te tekenen
    * `LayoutInflater` inflates UI vanuit XML file
* `isDualPane()` : controleert of het toestel beide fragmenten toont van activity
    * bv. in landscape (of op een tablet)
    * wordt gebruikt als er geklikt wordt in een list: `onItemClick()`
        * als er genoeg plaats is: `onOptionSelected()` zal de geselecteerde item doorgeven als parameter aan het ander fragment
        * als er niet genoeg plaats is: fragment wordt op een ander scherm getoond, via een andere activity (`DisplayItemActivity`) via `Intent`
* `onItemClickListener` moet aan `ListView` gekoppeld worden
* `geActivity()` geeft activity waarin huidig fragment geassocieerd is

### Encompassing activities

* grootste voordeel van fragments: vrijheid om fragments toe te voegen
    * als toestel van portait naar landschap gaat
* 2 layouts definiëren
    * met enkel list of met beide
        * in voorgrondactivity
            * onOptionSelected
            * fragment opladen
            * methode oproepen om velden in te vullen
* opmerking: 
    * finish in `onCreate()` (voor return)
        * als device in landscape zit
        * als mainactivity over wilt nemen en de fragments naast elkaar wilt tonen
    * root content view wordt niet gezet via setContentView()
        * add() : fragment resource via `android.R.id.naam`
        * `FragmentManager` gebruiken om fragments te beheren in activity
        * stelt je in staat om `FragmentTransaction` te doen om fragments toe te voegen, verwijderen, vervangen,... 
        * transactie: 1 of meerdere acties die tegelijk moeten gebeuren
            * uitvoeren van de transacite: `commit()`

