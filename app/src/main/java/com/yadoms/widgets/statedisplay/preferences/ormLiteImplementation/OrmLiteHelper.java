package com.yadoms.widgets.statedisplay.preferences.ormLiteImplementation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import com.yadoms.widgets.statedisplay.Widget;

import java.sql.SQLException;

public class OrmLiteHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME    = "ormlite.db";
    private static final int    DATABASE_VERSION = 1;

    private Dao<WidgetDto, Integer> m_widgetDao = null;

    public OrmLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, WidgetDto.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, WidgetDto.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* WidgetDto*/

    public Dao<WidgetDto, Integer> getWidgetDao() throws SQLException {
        if (m_widgetDao == null) {
            m_widgetDao = getDao(WidgetDto.class);
        }

        return m_widgetDao;
    }

    @Override
    public void close() {
        m_widgetDao = null;

        super.close();
    }

    @DatabaseTable(tableName = WidgetDto.TABLE_NAME_WIDGETS)
    public static class WidgetDto {

        public static final String TABLE_NAME_WIDGETS = "widgets";

        @DatabaseField(id = true)
        private int widgetId;

        @DatabaseField()
        private int keywordId;

        @DatabaseField()
        private String label;

        public WidgetDto(Widget widget)
        {
            this.widgetId = widget.id;
            this.keywordId = widget.keywordId;
            this.label = widget.label;
        }

        public WidgetDto() {
            // Empty constructor needed by ORMLite
        }

        /** Getters & Setters **/

        public int getWidgetId() {
            return widgetId;
        }
        public int getKeywordId() {
            return keywordId;
        }
        public String getLabel() {
            return label;
        }

        public Widget toWidget() {
            return new Widget(widgetId,
                    keywordId,
                    label);
        }
    }
}



