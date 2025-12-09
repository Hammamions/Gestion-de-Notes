
# 📱 Gestion des Notes - Application Android

Une application Android pour gérer les notes scolaires avec calcul automatique des moyennes selon des coefficients spécifiques.

## ✨ Fonctionnalités

- **📊 Gestion des matières** : Ajouter, modifier et supprimer des matières avec coefficients
- **📝 Gestion des notes** : Gérer les notes par type (DS, Examen, TP, Oral)
- **🧮 Calcul intelligent** : Calcul automatique des moyennes selon des règles spécifiques
- **📈 Statistiques** : Affichage de la moyenne générale et du statut académique
- **🤝 Partage** : Partager sa moyenne générale via d'autres applications

## 📋 Règles de calcul des moyennes

L'application applique des coefficients spécifiques selon les types de notes présentes :

### Cas 1 : DS + Examen
- DS : Coefficient 0.3
- Examen : Coefficient 0.7

### Cas 2 : DS + Examen + TP
- DS : Coefficient 0.1
- TP : Coefficient 0.25
- Examen : Coefficient 0.65

### Cas 3 : DS + Examen + Oral
- DS : Coefficient 0.2
- Oral : Coefficient 0.1
- Examen : Coefficient 0.7

### Cas 4 : DS + Examen + TP + Oral
- DS : Coefficient 0.2
- TP : Coefficient 0.2
- Oral : Coefficient 0.1
- Examen : Coefficient 0.5

**Important** : DS et Examen sont obligatoires pour le calcul de la moyenne.

## 🛠️ Technologies utilisées

- **Kotlin** : Langage de programmation principal
- **SQLite** : Base de données locale
- **RecyclerView** : Affichage des listes
- **Material Design** : Interface utilisateur moderne
- **AndroidX** : Bibliothèques Android modernes
## 📁 Structure du projet
app/src/main/java/isim/ia2kotlin/projet/
├── Subject.kt # Modèle de données pour les matières
├── Note.kt # Modèle de données pour les notes
├── DBHelper.kt # Gestion de la base de données SQLite
├── MainActivity.kt # Écran principal avec statistiques
├── SubjectListActivity.kt # Liste des matières
├── NotesActivity.kt # Liste des notes d'une matière
├── AddEditSubjectActivity.kt # Ajout/édition d'une matière
├── AddEditNoteActivity.kt # Ajout/édition d'une note
├── SubjectAdapter.kt # Adapteur pour la liste des matières
├── NoteAdapter.kt # Adapteur pour la liste des notes
└── SwipeToDeleteCallback.kt # Gestion du swipe pour supprimer
