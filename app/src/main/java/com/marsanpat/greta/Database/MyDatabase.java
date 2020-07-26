package com.marsanpat.greta.Database;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

@Database(name = MyDatabase.NAME, version = MyDatabase.VERSION)
public class MyDatabase {
        public static final String NAME = "MyDatabase";
        public static final int VERSION = 4;

        @Migration(version = 4, database = MyDatabase.class)
        public static class Migration1 extends AlterTableMigration<Element> {



                public Migration1(Class<Element> table) {
                        super(table);
                }

                @Override
                public void onPreMigrate() {
                        super.onPreMigrate();
                        addColumn(SQLiteType.INTEGER, "priority");
                }

                @Override
                public void onPostMigrate(){
                        super.onPostMigrate();
                        Log.d("debug", "Migration took place");
                }
        }

}