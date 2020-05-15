# re:visit

## Idee
Gemäss Empfehlung des Bundes sollen alle Gastrobetriebe die Namen und Telefonnummern ihrere Gäste zu registrieren. Dies soll das tracken von Kontakten im Falle einer Erkrankung eines Gastes vereinfachen. Diese Applikation soll die Gastrobetriebe dabei unterstützen, dies auf einfache und sichere Art zu machen.

## Entwicklung
Das Projekt wurde in einem Wochenend Effort der re:thincers realisiert um den Gastro-Sektor in diesen speziellen Zeiten zu unterstützen.

**Disclaimer:** Die Code Qualität soll nicht als referenz genommen werden ;) Das Ziel war es möglichst in kurzer Zeit eine funktionierende Anwendung "zusammen zu häcken" als proof of concept und sofort einsetzbares Produkt. Falls die Plattform grösseres Interesse stösst, werden wir die Codebase überarbeiten, damit wir auch mehr freude am Sourcecode der Anwendung haben!


### Ablauf
Ein Gastrobetrieb registriert sich auf der Platform und lädt eine App auf sein Smartphone und loggt sich ein.

Der Kunde geht mit seinem Mobiltelefon auf eine Webseite und gibt seine Daten (Namen, Telefonnummer) an. Die Webseite generiert aus diesen Daten einen QRCode. Der Kunde speichert den QRCode auf seinem Gerät. Es werden keine Daten sonst gesepeichert. Die Webseite dient nur der Generierung des QRCodes.

Im Gastrobetrieb angekommen zeigt der Kunde seinen QRCode vor. Dieser wird mit der App des Gastrobetriebes gescannt und die Daten des Kunden sowie der Besuchszeitpunkt werden verschlüsselt auf einem Server abgelegt. Dabei wird das Passwort des Gastrobetriebes zum Verschlüsseln der Daten genutzt. Somit ist sichergestellt, dass nur der Gastrobetrieb Zugang zu den Daten hat.

Über eine Weboberfläche kann sich der Gastrobetrieb einloggen und sich die Liste der Gäste ansehen. Alternativ kann er auf dieser Oberfläche auch Gäste manuell eintragen.

* Guide für Gäste: [https://revisit.ch/#help-modal](https://revisit.ch/#help-modal)
* Guide für Gastrobetriebe: [https://revisit.ch/restaurant#info](https://revisit.ch/restaurant#info)


## Implementation
Format für den QRCode:
```
{
	"name": "Bettina Probiert",
	"phone": "+41776665544"
}
```

## Credits & Libraries
* Vielen Dank an Martin Brandtner für den Security Review.
* JavaScript QrCode Generator ➔ https://github.com/kazuhikoarase/qrcode-generator
* Crypto-JS ➔ https://github.com/brix/crypto-js
