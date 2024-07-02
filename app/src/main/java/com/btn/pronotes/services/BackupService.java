/*
package com.btn.pronotes.services;

import static com.btn.pronotes.utils.CONSTANTS.BACKUP_ACTION;
import static com.btn.pronotes.utils.CONSTANTS.BACKUP_NOTIFCATION_CHANNEL_ID;
import static com.btn.pronotes.utils.CONSTANTS.EMAIL;
import static com.btn.pronotes.utils.CONSTANTS.RESTORE_ACTION;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.btn.pronotes.Database.RoomDB;
import com.btn.pronotes.Models.Media;
import com.btn.pronotes.Models.Notes;
import com.btn.pronotes.R;
import com.btn.pronotes.interfaces.BackupListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class BackupService extends Service {
    private static final int NOTIFICATION_ID = 116;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final int[] uploaded = {0};
        if (Objects.equals(intent.getAction(), BACKUP_ACTION)) {
            if (intent.hasExtra(EMAIL)) {
                String email = intent.getStringExtra(EMAIL);
                Log.e("foreGround service", "start");
                startForegroundService(100, 20, "Backup");

                storeDataTOFireStore(email, new BackupListener() {
                    @Override
                    public void onSuccess(int total) {
                        uploaded[0]++;
                        Log.e("foreGround service", "total:" + total + "\n uploaded:" + uploaded[0]);

                        if (total == uploaded[0]) {
                            Log.e("foreGround service", "stop");
                            stopForeground(true);
                        } else {
                            createNotification(100, uploaded[0] * 20, "Backup");
                        }
                    }
                });

            }

        } else if (Objects.equals(intent.getAction(), RESTORE_ACTION)) {
            if (intent.hasExtra(EMAIL)) {

                startForegroundService(100, 50, "Restore");
                restoreBackupFromFirestore(intent.getStringExtra(EMAIL));
            }
        }

        return START_STICKY;
    }

    private void storeDataTOFireStore(String email, BackupListener listener) {

//        deleteNotesFirestoreDocument(email);

        RoomDB roomDB = RoomDB.getInstance(getApplicationContext());
        List<Notes> notesList = new ArrayList<>(roomDB.mainDAO().getAll());
        StorageReference storageRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child("notes_images").child(email);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notesCollection = db.collection("backup_notes");
        for (int i = 0; i < notesList.size(); i++) {
            WriteBatch batch = db.batch();

            final Notes currentNote = notesList.get(i);
            List<Media> mediaList = roomDB.mainDAO().getAllMedia(currentNote.getID());
            List<String> stringMediaList = new ArrayList<>();

            if (!mediaList.isEmpty()) {
                for (Media media : mediaList) {
                    UploadTask uploadTask = storageRef.child(String.valueOf(media.getId())).putFile(Uri.fromFile(new File(media.getPath())));
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        StorageMetadata downloadPathMetaData = taskSnapshot.getMetadata();
                        if (downloadPathMetaData != null) {
                            StorageReference downloadPathRef = downloadPathMetaData.getReference();
                            if (downloadPathRef != null) {
                                downloadPathRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    stringMediaList.add(uri.toString());
                                    if (stringMediaList.size() == mediaList.size()) {
                                        // All media items for this note have been uploaded
                                        currentNote.setMediaItems(stringMediaList);
                                        batch.set(notesCollection.document(email).collection("notes").document(String.valueOf(currentNote.getID())), currentNote);
                                        batch.commit().addOnCompleteListener(task -> {
                                            listener.onSuccess(notesList.size());
                                        });
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(e -> {

                        batch.set(notesCollection.document(email).collection("notes").document(String.valueOf(currentNote.getID())), currentNote);
                        batch.commit().addOnCompleteListener(task -> {
                            listener.onSuccess(notesList.size());
                        });
                    });
                }
            } else {

                batch.set(notesCollection.document(email).collection("notes").document(String.valueOf(currentNote.getID())), currentNote);
                batch.commit().addOnCompleteListener(task -> {
                    listener.onSuccess(notesList.size());
                });
            }

        }


    }

    private void startForegroundService(int total, int current, String title) {
        // Create a notification for the foreground service
        Notification notification = createNotification(total, current, title);

        // Start the service as a foreground service
        startForeground(NOTIFICATION_ID, notification);
    }

    private Notification createNotification(int total, int current, String title) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    BACKUP_NOTIFCATION_CHANNEL_ID,
                    "Channel Name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, BACKUP_NOTIFCATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText("Syncing....")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setOnlyAlertOnce(true);
        builder.setProgress(total, current, false);

        // Return the notification
        return builder.build();
    }

    private void deleteNotesFirestoreDocument(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

// Reference to the "notes" collection within "/backup_notes/ukhan1753@gmail.com"
        CollectionReference notesCollectionRef = db.collection("backup_notes")
                .document(documentId)
                .collection("notes");

        notesCollectionRef
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<WriteBatch> batches = new ArrayList<>();
                    WriteBatch batch = db.batch();
                    int batchSize = 0;

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Notes note = document.toObject(Notes.class);

                        if (note != null) {
                            for (String path : note.getMediaItems()) {
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference fileRef = storage.getReference().child(path);

                                fileRef.delete()
                                        .addOnSuccessListener(aVoid -> {
                                            // File deleted successfully
                                            Log.d("Delete File", "File deleted successfully\n" + path);
                                        })
                                        .addOnFailureListener(exception -> {
                                            // Handle any errors that occur during file deletion
                                            Log.e("Delete File", "Deletion failed: " + exception.getMessage());
                                        });
                            }
                        }
                        batch.delete(document.getReference());
                        batchSize++;

                        // Commit the batch after every 500 documents or if it's the last batch
                        if (batchSize == 500 || batchSize == querySnapshot.size()) {
                            batches.add(batch);
                            batch = db.batch();
                            batchSize = 0;
                        }
                    }

                    for (WriteBatch writeBatch : batches) {
                        writeBatch.commit().addOnFailureListener(e -> {
                            // Handle any errors that occur during deletion
                            Log.e("Delete Collection", "Deletion failed: " + e.getMessage());
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that occur while retrieving the documents
                    Log.e("Delete Collection", "Error retrieving documents: " + e.getMessage());
                });

    }

    private void restoreBackupFromFirestore(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notesCollection = db
                .collection("backup_notes")
                .document(email)
                .collection("notes");

        notesCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Notes> backupNotesList = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Notes backupNote = documentSnapshot.toObject(Notes.class);
                backupNotesList.add(backupNote);
            }

            RoomDB roomDB = RoomDB.getInstance(getApplicationContext());
            roomDB.mainDAO().deleteAllNotes();
            roomDB.mainDAO().deleteALLMedia();
            deleteAllFilesFromPrivateStorage();
            roomDB.mainDAO().insertListOFNotes(backupNotesList);
            for (Notes notes : backupNotesList) {
                List<Media> mediaList = new ArrayList<>();

                for (String path : notes.getMediaItems()) {
                    StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(path);

                    // Get the local file path where the image will be stored
                    File filePath = new File(getExternalFilesDir(null), "IMG" + System.currentTimeMillis() + ".jpg");

                    imageRef.getFile(filePath).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                            mediaList.add(new Media(notes.getID(), filePath.getAbsolutePath()));
                            if (mediaList.size() == notes.getMediaItems().size()) {
                                roomDB.mainDAO().insertMedia(mediaList);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                }

            }
            stopForeground(true);

        }).addOnFailureListener(e -> {
            stopForeground(true);
        });
    }

    private void deleteAllFilesFromPrivateStorage() {
        File privateDir = getExternalFilesDir(null);
        if (privateDir != null && privateDir.isDirectory()) {
            File[] files = privateDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
    }

    private void downloadImage(String storagePath, String fileName) {
        // Get a reference to the Firebase Storage file

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
*/
