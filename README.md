freelance platforma - it355 pz1

ovo je spring mvc aplikacija za objavljivanje freelance poslova i slanje
proposal prijava.

projekat koristi thymeleaf za stranice, a podaci se cuvaju u memoriji dok je
aplikacija pokrenuta. zato nije potrebna baza za pokretanje projekta.


sta treba da imas instalirano:

1. java 17 ili novija verzija
2. git

maven ne mora posebno da se instalira jer projekat vec ima maven wrapper.


mac setup:

1. kloniraj projekat:

git clone https://github.com/djokicmihajlo/IT355-PZ1.git
cd IT355-PZ1

2. pokreni aplikaciju:

./mvnw spring-boot:run

3. otvori aplikaciju u browseru:

http://localhost:8080


windows setup:

1. kloniraj projekat:

git clone https://github.com/djokicmihajlo/IT355-PZ1.git
cd IT355-PZ1

2. pokreni aplikaciju:

mvnw.cmd spring-boot:run

3. otvori aplikaciju u browseru:

http://localhost:8080


gemini podesavanje:

gemini api kljuc nije potreban za osnovni rad aplikacije. potreban je samo ako
hoces da radi automatsko generisanje proposal drafta.

u glavnom folderu projekta napravi .env fajl i dodaj:

GEMINI_API_KEY=tvoj_api_kljuc
GEMINI_MODEL=gemini-3.5-flash

.env fajl se ne salje na github.


demo nalozi:

client_milan / client123
client_jelena / client123

freelancer_ana / freelancer123
freelancer_marko / freelancer123


testovi:

mac:

./mvnw test

windows:

mvnw.cmd test


napomena:

korisnici, poslovi i prijave se cuvaju u application scope-u i resetuju se kada
se aplikacija ugasi.

uploadovani cv fajlovi se cuvaju lokalno u folderu uploads/cv i ne salju se na
github.
