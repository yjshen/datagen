package io.spaceandtime;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class App {

    // create table person
    public void createTable(String table, String config) throws SQLException {
        try (Connection conn = DataSource.getInstance(config).getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS " + table);
            stmt.executeUpdate(
                    "CREATE TABLE " + table + "(id BIGINT, name VARCHAR, city_id BIGINT, extra VARCHAR, " +
                            "PRIMARY KEY (id)) WITH \"template=deltastore,immutable=true\""
            );
        } catch (SQLException e) {
            throw e;
        }
    }

    // batch insert
    private void insertBatch(String table, int start, int end, String config) throws SQLException {
        try (Connection conn = DataSource.getInstance(config).getConnection();
             Statement stmt = conn.createStatement()) {
            for (int i = start; i < end; i++) {
                stmt.addBatch("INSERT INTO " + table + " VALUES (" + i + ", 'John Doe " + i + "', " + 3 + ", 'Temp')");
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw e;
        }
    }

    // insert
    private void insert(String table, int start, int end, String config) throws SQLException {
        String threadName = Thread.currentThread().getName();
        try (Connection conn = DataSource.getInstance(config).getConnection();
             Statement stmt = conn.createStatement()) {
            for (int i = start; i < end; i++) {
                if (i % 1000 == 0) {
                    System.out.println("Thread " + threadName + " insert total " + (i - start));
                }
                String sql = "INSERT INTO " + table + " VALUES (" + i + ", 'John Doe " + i + "', " + 3 + ", 'Temp')";

                // retry 3 times
                int retry = 0;
                while (retry < 3) {
                    try {
                        stmt.executeUpdate(sql);
                        break;
                    } catch (SQLException e) {
                        System.err.println("Thread " + threadName + " insert failed, retry " + retry + " times");
                        e.printStackTrace();
                        retry++;
                        if (retry == 3) {
                            throw e;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw e;
        }
    }


    // multithreaded insert
    public void insertMultiThread(String table, int start, int end, int threadNum, String config) throws SQLException {
        int step = (end - start) / threadNum;

        // await all threads finish
        Thread[] threads = new Thread[threadNum];

        for (int i = 0; i < threadNum; i++) {
            int startId = start + i * step;
            int endId = start + (i + 1) * step;

            threads[i] = new Thread(() -> {
                try {
                    insert(table, startId, endId, config);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }, "thread-" + i);
            threads[i].start();
        }

        for (int i = 0; i < threadNum; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Done");
    }

    public static void main(String[] args) {
        // parse args to get table name, start id, end id, thread number, config file path
        String table = args[0];
        int start = Integer.parseInt(args[1]);
        int end = Integer.parseInt(args[2]);
        int threadNum = Integer.parseInt(args[3]);
        String config = args[4];

        App app = new App();
        try {
            app.createTable(table, config);
            app.insertMultiThread(table, start, end, threadNum, config);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
