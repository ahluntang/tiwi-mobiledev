
## Componenten van een app kunnen communiceren met componenten die in ander proces draaien. Toch lijkt het alsof men communiceert met een object in het eigen proces. Bespreek hoe Binder object mapping en referincing implementeert. Hoe vindt men de remote Binder-objecten?

## Object mapping & referencing

* Wanneer Binder een transactie wilt uitvoeren
    * moet ontvangende Binder specificeren
* Object referenties zijn virtuele geheugenadressen
* remote Binder is dus enkel gekend in het oproepend proces via een abstracte 32 bit handler
    * 2 vormen van Binder object referentie
        * als adres (pointer) in (virtuele) memory space van proces
        * als abstracte 32 bit handle
* alle referenties in proces naar objecten lokaal bij dat proces zijn van de vorm "virtueel geheugenadres"
* alle referentie naar objecten in ander proces zijn vna vorm van handle
* taak van driver om de mapping van objectreferentie van ene proces naar ander
* driver houdt binaire boom bij per proces
    * driver zal handle herschrijven in transactie data naar lokaal adres in ontvangende Binder van ontvangend proces
* Binder driver kent niets over een specifieke Binder object tot het object via de driver gezonden werd naar ander proces
    * ddriver voegt adres van object toe aan mapping tabel en vraagt aan owning proces om een referentie bij te houden
    * telkens deze referentie gedeeld is met ander proces, wordt reference count geïncrementeerd
    * decrementeren kan expliciet of automatisch ( als proces stopt)
    * als geen enkel (ander) proces over object kent
        * wordt verwijderd uit mappingstabel
        * voorkomt dat driver state van object moet blijven onderhouden terwijl dat niet nodig is
            * als het enkel in eigen proces gebruikt wordt
* Binder framework ondersteunt notificaties bij death van Binder objecten
    * via observer pattern
    * lokale Binder object: dat wilt weten wanneer remote Binder afsluit
        * moet zichzelf toevoegen aan observer lijst
    * als remote Binder object afsluit, dan wordt observer geïnformeerd zodat het kan reageren erop

## Context Manager

* hoe handle van remote Binder objecten kennen?
* geen optie om de adresse nvan Remote Binder a priori te weten
    * anders zou elk proces dat token kunnen gebruiken
    * security van Binder zou doorbroken worden
* als geen adre gekend is, dan kan er ook geen communicatie plaatsvinden
* opl: 1 proces mag zichzelf registreren als Context MAnager
    * proces zal Binder object hosten met de enige vaste en vooraf gekende handle: 0
    * Context Manager: systeem om namen toe te kennen aan een Binder interface
* implementatie van Context Manager maakt geen deel uit van Binder framework

### Implementatie: Service Manager (servicemanager daemon)

* om veiligheidsredenen mag er maar 1 ContextManager geregistreerd worden
* servicemanager is 1 van de eerste services die starten
* werkt als gele gids voor alle systeemservices
* init proces start ServiceManager
    * registreert zich als context manager bij Binder kernel driver
    * als system service start, dan registreert het bij elke instantie bij de servicemanager
* als app later wilt gebruik maken van systemservice
    * vragen aan service manager voor een handle naar deze service (via `getSystemService()`)
    * system service geeft Binder object terug aan app
        * Binder object heeft methode voor de service die via Binder verlopen
* een call naar service component binnen eigen app, gaat niet via binder en wordt niet via service manager opgezocht
* Service manager geeft Binder objects via naam
    * relatief gemakkelijk om systeemservice te vervangen door eigen service te schrijven en registreren met zelfde naam
    * om systeemservice te beschermen, zal ServiceManager enkel registraties accepteren van vertrouwede UIDs
    * 3rd party services worden gemanaged via de Activity Manager
        * apps die willen communiceren met zo'n service kunnen de handler van Binder object verkrijgen via Activity Manager
        * elke service class heeft een methode `onBind()` dat Binder object teruggeeft
