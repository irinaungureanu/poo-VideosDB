# poo-VideosDB
Student: Ungureanu Irina-Nicoleta
Grupa: 334CB

Tema POO - VideosDB

Clasele pe care le-am creat si legatura dintre ele:
- Actor ~ pentru reprezentarea actorilor din filme si seriale
- Video ~ pentru repezentarea oricarui tip de video (film/serial) - 
caracteristici comune film-serial precum nume, an lansare, genuri etc.
- Movie ~ pentru reprezentarea video-urilor de tip film
- Show ~ pentru reprezentarea video-urilor de tip serial - fiecare serial 
are mai multe sezoane, deci in show va fii si o lista cu elemente de tip Season
(clasa care este din scheletul initial si contine informatii despre un sezon
al unui serial)
- User ~ pentru reprezentarea utilizatorilor; contine si anumite metode care
au legatura cu utilizatorii si video-urile (un utilizator adauga ca vizionat un
film sau serial, da rating unui film sau serial etc.)
- Database ~ face legatura intre toate clasele prezentate anterior. Este baza
de date in care avem informatii despre toti utilizatorii si toate video-urile.
Contine metodele pentru query-uri si recomandari, acestea alegandu-se din baza
de date existenta (cei mai premiati actori, video-uri favorite, recomandari de
video-uri etc.)

Primul pas este incarcarea in obiecte a datelor citite din fisierele de test.
Aceste obiecte nou create vor fi folosite pentru crearea bazei de date a 
platformei cu filme si seriale. Am ales sa o reprezint folosindu-ma de 
Design Pattern-ul Singleton pentru ca avem o unica baza de date (este
initializata o singura data, apoi modificata aceasta in functie de actiunile
date).

Dupa ce am populat baza de date cu informatiile din fisiere, iau pe rand
fiecare actiune din lista de actiuni. In functie de tipul fiecarei actiuni,
apelez metoda corespunzatoare (apelarea se face din main):
- command -> metodele specifice sunt in User (pentru ca se adauga un film ca
vizionat, favorit sau i se da un rating de catre un user dat in actiune)
    - favorite: daca a fost vizionat un video dat, atunci il adaug la lista
    de favorite
    - view: daca nu a fost vizionat un video pana cand este data aceasta
    comanda, atunci se adauga acest video ca vizionat (numarul de vizonari
    va fii egal cu 1). Daca a mai fost vizionat, doar se incrementeaza
    numarul de vizionari.
    - rating: se adauga un rating video-ului dat in actiune. Se verifica
    daca video-ul este film sau serial
- query -> metodele specifice sunt in Database (cererile se obtin din baza de
date)
    - actors: cautari efectuate de utilizator dupa actori (in functie de media
    rating-urilor, premiile castigate si descrierea actorilor)
    - movies: cautari efectuate de utilizator dupa filme (in functie de rating,
    favorite, durata si cele mai vizualizate filme)
    - shows: cautari efectuate de utilizator dupa seriale (in functie de 
    rating, favorite, durata si cele mai vizualizate seriale)
    - users: cautari efectuate dupa utilizatori (in functie de numarul de
    rating-uri date)
- recommendation
    - standard: primul video nevazut de utilizator din baza de date
    - best_unseen: video-ul nevizualizat cu cel mai mare rating
    - popular: primul video nevizualizat din cel mai popular gen
    - favorite: cel mai des adaugat video pe lista de favorite a celorlalti
    utilizatori decat cel care cere recomandarea
    - search: toate video-urile nevazute de utilizatorul care cere recomandare,
    dar care au un anumit gen dat
    
La finalul fiecarei metode de comanda, cerere sau recomandare, se returneaza
un obiect de tip JSONObject in care se va gasi rezultatul actiunii respective.
In main, dupa primirea rezultatului (obiectului) unei actiuni, se adauga acest
rezultat la un array de tip JSONArray care se va scrie in fisierul de output
dupa ce au fost parcurse toate actiunile date in fisierul de input.

