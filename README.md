# Gastro Check In


## Idee
Aufgrund von COVID-19 sollen gemäss Empfehlung des Bundes alle Gastrobetriebe die Namen und Telefonnummern ihrere Gäste zu registrieren. Dies soll das tracken von Kontakten im Falle einer COVID-19 Erkrankung eines Gastes vereinfachen.

Diese Applikation soll die Gastrobetriebe dabei unterstützen, dies auf einfache und sichere Art zu machen.

### Ablauf
Ein Gastrobetrieb registriert sich auf der Platform und lädt eine App auf sein Smartphone und loggt sich ein.

Der Kunde geht mit seinem Mobiltelefon auf eine Webseite und gibt seine Daten (Namen, Telefonnummer) an. Die Webseite generiert aus diesen Daten einen QRCode. Der Kunde speichert den QRCode auf seinem Gerät. Es werden keine Daten sonst gesepeichert. Die Webseite dient nur der Generierung des QRCodes.

Im Gastrobetrieb angekommen zeigt der Kunde seinen QRCode vor. Dieser wird mit der App des Gastrobetriebes gescannt und die Daten des Kunden sowie der Besuchszeitpunkt werden verschlüsselt auf einem Server abgelegt.
Dabei wird das Passwort des Gastrobetriebes zum Verschlüsseln der Daten genutzt. Somit ist sichergestellt, dass nur der Gastrobetrieb Zugang zu den Daten hat.

Über eine Weboberfläche kann sich der Gastrobetrieb einloggen und sich die Liste der Gäste ansehen. Alternativ kann er auf dieser Oberfläche auch Gäste manuell eintragen.


## Implementation
Format für den QRCode:
```
{
	"name": "Bettina Probiert",
	"phone": "+41776665544"
}
```




