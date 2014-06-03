
## Waarvoor gebruikt men user-level wakelocks? Wie beheert deze? Hoe implementeert men wakelocks in de kernel? Wat is de samenwerking tussen beide niveaus? 

## User level wakelocks en Power Manager Service

* Power Management framework staat enkel expliciete controle van display & backlight, keyboard backlight en button backlight toe.
* systeem kan ook uitgezet worden via Battery Service: als batterij zeer laag is
* wakelocks

| Flag                      | CPU  | Screen | Keyboard  |
| ------------------------- |------| -------|-----------|
| `PARTIAL_WAKE_LOCK`       | ON   | OFF    | OFF       |
| `SCREEN_DIM_WAKE_LOCK`    | ON   | DIM    | OFF       |
| `SCREEN_BRIGHT_WAKE_LOCK` | ON   | BRIGHT | OFF       |
| `FULL_WAKE_LOCK`          | ON   | BRIGHT | BRIGHT    |

* vb. YouTube & Muziek apps
    * YouTube
        * wakelaock als er video gestreamed wordt
        * tijdens video blijft scherm aan
        * als gebruiker op power drukt, dan zal toestel scherm uitzetten en audio/video stoppen
    * Muziek
        * andere wakelock
        * verandert niets aan display
        * maar als display uit staat, dan zal wakelock CPU aan laten zodat muziek kan blijven spelen
        * zelfs als men op power knop drukt

### PowerManagerService

* monitored continu user timeouts of acties zoals powerknop drukken
* als er op power knop gedrukt wordt, dna worden alle wakelocks, behalve partial impliciet released
* opmerking: PowerManagerSerice heeft enkel 1 wakelock in de kernellaag, onafhankelijk van aantal userlevel wakelocks genomen door app en/of systemservice
* wakelocks moeten expliciet released worden: leidt tot no-sleep bugs
* expliciete management werd abstracter gemaakt in nieuwere versies van Android
    * als developer het toestel volledig aan wilt laten tijdens hun app, dan is er geen volledige wakelock meer nodig
    * beter om een vlag te zetten in WindowManager voor een View van app als scherm niet uit mag gaan
    * effecten blijven geïsoleerd binnen de app
    * als er naar andere app geswitched wordt, dan zal WindowManager automatisch wakelock releasen
* voor I/O
    * ook abstractie via API
    * registreren van Listener bij SensorManager of LocationManager
    * Vanuit API perspectief: power management onzichtbaar
        * maar gebruik van deze abstracties/API maakt ook intensief gebruik van wakelocks
    * niet deregistreren fan listener kan resulteren in significante batterijverspilling

## Kernel level wakelocks

* men wilt het toestel zoveel en zo snel mogelijk in slaapstand zetten
* via driver en userspace
* op linux: suspend vanuit userspace door te schrijven naar bestand in sysfs
* initiële android versie met standaard linux kernel
    * PowerManagerService gaf opdracht aan kernel om te suspenden vanaf alle user level wakelocks released zijn
    * probleem: races tussen systeem suspend proces en wakeup events
* 1 van de eerste zaken die gedaan worden bij suspend: user space proces pauzeren, hierna kan userspace niet reageren op events vanuit kernel
* als er wakeup event gebeurt op moment dat suspend gestart wordt, dan kan het zijn dat userspace gepauzeerd is voordat het kans krijgt om het event af te handelen en zal dat maar kunnen nadat het systeem wakker wordt vanuit slaapstand (door een ander wakeup event)
* probleem: als die ene wakeup event (dat niet afgehandeld werd) van een telefoon komt (iemand belt), dan is dit scenario niet gewenst
* oplossing: kernel level wakelocks
    * is object dat in 2 staten kan verkeren: actief of inactief
    * systeem kan niet in slaapstand als minstens 1 wakelock actief is
    * kernel sybsysteem dat wakeup event behandelt, activeert een wakelock wanneer zo'n event gebeurt en zal de wakelock pas deactiveren als event doorgegeven werd aan user space
        * vermijdt race ocnditions
* nog steeds probleem:
    * userspace kan gepauzeerd zijn nadat kernel alle wakelocks gedeactiveerd heeft, userspace kan wakeup event handling niet voltooien
    * userspace kan ook kernel level wakelocks creëren, activeren, deactiveren en destroyen
    * vb. binnenkomende oproep
        * userspace coe die oproep afhandelt zal eerst een wakelock nemen voordat het de kernelevent consumeert
        * kernel driver zal zijn eigen wakelock loslaten na het doorgeven van het event naar userspace
        * systeem zal niet in slaapstand gaan want er is nog steeds ee nwakelock van userspace
* meerdere apps kunnen wakelocks zetten via PowerManager API
    * powermanager registreert intern enkel 1 kernel wakelock eom meerdere user level wakelocks te beschermen
    * als alle user level wakelocks released zijn, dan pas zal PowerManager zijn kernel wakelock releasen

### Suspend decision verplaatsen naar kernel

* android wilt suspenden vanaf het moment er geen actieve wakelocks zijn
* elke deactivatie van laatste wakelock start het suspend proces
* beter om suspend te starten vanuit kernel space
    * anders zou userspace hiervoor telkens moetne schrijven naar sysfs om de kernel te bevelen om te suspenden
* in de kernel zit er een item waarvan de taak is om het suspend proces te starten las laatste actieve wakelock gedeactiveerd is
