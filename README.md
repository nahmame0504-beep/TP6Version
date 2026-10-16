TP 6 : Simulation d'un Conflit de Réservation Concurrent et Verrouillage Optimiste avec @Version

 Introduction

Dans les applications multi-utilisateurs (ex: systèmes de réservation d'hôtels, de billets de train, e-commerce), plusieurs transactions peuvent tenter de modifier simultanément la même ressource. Sans gestion d'accès concurrent, cela entraîne des problèmes tels que les mises à jour perdues (Lost Updates).

Ce TP met en pratique le verrouillage optimiste (Optimistic Locking) avec JPA/Hibernate à l'aide de l'annotation @Version. Vous allez simuler un conflit d'accès concurrentiel entre deux utilisateurs tentant de réserver la même ressource en même temps, observer la réaction de JPA et implémenter une stratégie de gestion de conflit.

 Objectifs d'apprentissage

À la fin de ce TP, vous serez capable de :

Comprendre le concept fondamental du verrouillage optimiste en JPA.

Utiliser l'annotation @Version sur une entité JPA pour contrôler la concurrence.

Simuler un conflit de réservation concurrente (à l'aide de threads ou de deux EntityManager distincts).

Attraper et gérer l'exception OptimisticLockException (ou ObjectOptimisticLockingFailureException dans Spring Data JPA).

Mettre en place une stratégie de résolution face à un conflit de données.

 Prérequis techniques

Avant de commencer, assurez-vous de disposer de :

JDK 8 ou supérieur installé et configuré.

Apache Maven configuré sur votre machine.

Un IDE compatible Java/JEE (IntelliJ IDEA, Eclipse, NetBeans, VS Code).

Connaissances de base sur JPA et Hibernate (Mapping d'entités, cycle de vie, transactions).

Compréhension élémentaire du fonctionnement des transactions en Java/JEE.

 Ce que vous allez apprendre

Ce TP approfondit la gestion de la concurrence dans les applications JEE utilisant JPA/Hibernate. Vous découvrirez :

Comment fonctionne @Version : À chaque modification de l'entité, Hibernate vérifie que le numéro de version en base correspond à celui en mémoire, puis l'incrémente automatiquement (UPDATE ... WHERE id = ? AND version = ?).

Comment détecter les conflits : Si un premier utilisateur met à jour la donnée, la version s'incrémente. Lorsque le deuxième utilisateur tente de sauvegarder avec l'ancienne version, JPA lève une OptimisticLockException.

Comment gérer l'exception : Recharger la donnée fraîche, informer l'utilisateur ou retenter la transaction.

 Structure du Projet (Recommandée)

tp6-optimistic-locking/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/tp/concurrence/
        │       ├── model/
        │       │   └── Reservation.java      # Entité avec @Version
        │       ├── service/
        │       │   └── ReservationService.java # Logique métier
        │       └── MainApp.java             # Code de simulation du conflit
        └── resources/
            └── META-INF/
                └── persistence.xml          # Configuration JPA

               
  <img width="946" height="756" alt="image" src="https://github.com/user-attachments/assets/62df9e90-938a-489b-9678-90f54c6181c5" />
  <img width="942" height="762" alt="image" src="https://github.com/user-attachments/assets/ccfbed79-77a3-421d-ab46-9ca534455a20" />
  <img width="947" height="756" alt="image" src="https://github.com/user-attachments/assets/22e2404f-3ac9-4e68-80c0-5e273a2e122d" />
  <img width="935" height="772" alt="image" src="https://github.com/user-attachments/assets/ee4f79f8-35c1-4e6a-8afb-5a7b03d300d0" />
  <img width="917" height="762" alt="image" src="https://github.com/user-attachments/assets/7f0d5a09-eb9c-4e45-9fa8-482061eecb5a" />
  <img width="922" height="762" alt="image" src="https://github.com/user-attachments/assets/44796e24-ebae-4a5d-a0a1-08cb33989b7d" />
  <img width="831" height="537" alt="image" src="https://github.com/user-attachments/assets/bbd480c1-9d78-4ed1-a0be-2a1bc5ec3abc" />







