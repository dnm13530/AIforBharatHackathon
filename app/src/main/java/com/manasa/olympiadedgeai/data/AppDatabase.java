package com.manasa.olympiadedgeai.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// THE FIX: Version is incremented to 2 to force a database recreation for your demo.
@Database(entities = {Question.class, Attempt.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract QuestionDao questionDao();
    public abstract AttemptDao attemptDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "olympiad_edge_db")
                            .fallbackToDestructiveMigration() // This ensures the old DB is dropped and recreated
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                QuestionDao dao = INSTANCE.questionDao();
                dao.deleteAll();

                List<Question> questions = new ArrayList<>();
                questions.add(new Question("Algebra", "Foundation",
                        "If \\(x + \\frac{1}{x} = 2\\), find the value of \\(x^2 + \\frac{1}{x^2}\\).",
                        "2", "Try squaring both sides of the given equation.", null));

                questions.add(new Question("Algebra", "Foundation",
                        "If \\(x + \\frac{1}{x} = 3\\), find the value of \\(x^4 + \\frac{1}{x^4}\\).",
                        "47", "First find \\(x^2 + \\frac{1}{x^2}\\) by squaring the initial equation, then square the result again.", null));

                questions.add(new Question("Geometry", "Foundation",
                        "An isosceles triangle has a perimeter of 20 cm. The equal sides are 7 cm each. Find the third side.",
                        "6", "Perimeter is the sum of all sides of the triangle.", "https://olympiad-edge-diagrams.s3.amazonaws.com/isosceles_20.png"));

                questions.add(new Question("Geometry", "Olympiad",
                        "An isosceles triangle has a perimeter of 32 cm. The equal sides are each 13 cm. The third side is x cm. Find the value of x.",
                        "6", "Perimeter is the sum of all sides of the triangle.", "https://olympiad-edge-diagrams.s3.amazonaws.com/isosceles_32.png"));

                dao.insertAll(questions);
            });
        }
    };
}
