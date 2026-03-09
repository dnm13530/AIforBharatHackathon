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

// FINAL DEMO VERSION: Includes all 19 questions with correct LaTeX escaping.
@Database(entities = {Question.class, Attempt.class}, version = 8, exportSchema = false)
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
                            .fallbackToDestructiveMigration()
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
                addFoundationalQuestions(questions);
                addIntermediateOlympiadQuestions(questions);
                addAdvancedOlympiadQuestions(questions);
                
                dao.insertAll(questions);
            });
        }
    };

    private static void addFoundationalQuestions(List<Question> q) {
        q.add(new Question("Algebra", "Foundation",
            "If \\(x = \\frac{\\sqrt{3} + 1}{2}\\), find the numerical value of \\(4x^3 + 2x^2 - 8x + 7\\).", "10",
            "Consider \\(2x-1 = \\sqrt{3}\\) and square both sides to find a simpler polynomial for x.", null));
        q.add(new Question("Algebra", "Foundation",
            "If \\(x + \\frac{1}{x} = 3\\), find the value of \\(x^4 + \\frac{1}{x^4}\\).", "47",
            "First find \\(x^2 + \\frac{1}{x^2}\\) by squaring the initial equation, then square the result again.", null));
        q.add(new Question("Geometry", "Foundation",
            "An isosceles triangle has a perimeter of 32 cm. The equal sides are each 13 cm. The third side is x cm. Find the value of x.", "6",
            "Remember the perimeter is the total distance around the triangle.", "https://olympiad-edge-diagrams.s3.amazonaws.com/isosceles_32.png"));
        q.add(new Question("Geometry", "Foundation",
            "A cone and a cylinder have the same radius and the same height. If the ratio of their curved surface areas is \\(8 : 5\\), find the ratio of their radius to their height.", "3:4",
            "Use the formulas for curved surface area: \\(\\pi r l\\) for a cone and \\(2 \\pi r h\\) for a cylinder.", null));
        q.add(new Question("Probability", "Foundation",
            "Seven boys and six girls are to be seated in a row. Find the probability that all the girls sit together.", "2/143",
            "Treat the six girls as a single block. Arrange the 7 boys and this 1 block.", null));
        q.add(new Question("Logic", "Foundation",
            "Identify the next number in the sequence: \\(1, 3, 11, 43, 171, \\dots\\)", "683",
            "The pattern is \\(term_{n+1} = 4 \\times term_n - 1\\).", null));
        q.add(new Question("Arithmetic", "Foundation",
            "Find the smallest 4-digit number that is divisible by \\(15, 25,\\) and \\(35\\).", "1050",
            "Find the Least Common Multiple (LCM) of 15, 25, and 35.", null));
    }

    private static void addIntermediateOlympiadQuestions(List<Question> q) {
        q.add(new Question("Number Theory", "Olympiad", 
            "Find the number of pairs of positive integers \\((m, n)\\) such that \\(m^2 + n^2 \\leq 2024\\) and \\(m^2 + n^2\\) is divisible by \\(mn\\).", "1", 
            "Use Vieta jumping on the quadratic \\(m^2 - (kn)m + n^2 = 0\\).", null));
        q.add(new Question("Algebra", "Olympiad", 
            "Find the number of ordered pairs of integers \\((x, y)\\) such that \\(x^2 + y^2 = x^3\\).", "3", 
            "Rearrange to \\(y^2 = x^2(x-1)\\).", null));
        q.add(new Question("Geometry", "Olympiad", 
            "In rectangle \\(ABCD, AB = 8\\) and \\(BC = 6\\). Let \\(P\\) be a point inside the rectangle such that the areas of \\(\\triangle PAB, \\triangle PBC,\\) and \\(\\triangle PCD\\) are in arithmetic progression. Find the max area of \\(\\triangle PAD\\).", "24", 
            "The arithmetic progression gives a linear constraint on the coordinates of P.", null));
        q.add(new Question("Combinatorics", "Olympiad", 
            "How many 4-digit numbers \"abcd\" are there such that \\(a < b < c < d\\) and the digits are in arithmetic progression?", "7",
            "Let the common difference be \\(k\\). Since \\(d \\leq 9\\), we have \\(a+3k \\leq 9\\).", null));
        q.add(new Question("Polynomials", "Olympiad", 
            "Let \\(P(x) = x^2 + ax + b\\). If \\(P(P(1)) = P(P(2)) = 0\\) and \\(1 \\neq 2\\), find the value of \\(P(0)\\).", "-2",
            "Let the roots of \\(P(x)\\) be \\(r_1, r_2\\). Then \\({P(1), P(2)} = {r_1, r_2}\\).", null));
    }

    private static void addAdvancedOlympiadQuestions(List<Question> q) {
        q.add(new Question("Geometry", "Olympiad Advanced", 
            "In \\(\\triangle ABC, AB = 13, BC = 14,\\) and \\(CA = 15\\). Let \\(D\\) be the midpoint of \\(BC\\). Find the distance between the touch points of the incircles of \\(\\triangle ABD\\) and \\(\\triangle ADC\\) on \\(AD\\).", "1",
            "The distance is \\(|(s_1-a_1) - (s_2-a_2)|\\).", null));
        q.add(new Question("Number Theory", "Olympiad Advanced", 
            "Find all integers \\(n \\geq 1\\) such that \\(n^2 + 3^n\\) is a perfect square.", "1", 
            "Let \\(n^2 + 3^n = k^2\\). Both factors of \\((k-n)(k+n)\\) must be powers of 3.", null));
        q.add(new Question("Inequalities", "Olympiad Advanced", 
            "Let \\(a, b, c > 0\\) such that \\(a + b + c = 3\\). Prove: \\(a/(1 + b^2) + b/(1 + c^2) + c/(1 + a^2) \\geq 3/2\\).", "Proof",
            "Use the identity \\(a/(1+b^2) = a - ab^2/(1+b^2)\\).", null));
        q.add(new Question("Combinatorics", "Olympiad Advanced", 
            "If the chords of a circle with \\(n\\) points divide the interior into exactly 31 regions, find \\(n\\).", "6",
            "Use the formula \\(R = 1 + \\binom{n}{2} + \\binom{n}{4}\\).", null));
        q.add(new Question("Geometry", "Olympiad Advanced", 
            "In \\(\\triangle ABC\\), altitude foot \\(D\\) from \\(A\\) to \\(BC\\) and point \\(P\\) on \\(AD\\) are given. Prove \\(\\angle EDF = \\angle BDC\\).", "Proof",
            "Show that points \\(F, P, D, E\\) are concyclic.", null));
    }
}
