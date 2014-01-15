package com.runbytech.yijubolin.repository;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.runbytech.yijubolin.model.Favorite;

/**
 * Created by wenzhili on 14-1-9.
 */
public class FavoritesRepository {

    private DatabaseHelper db;
    Dao<Favorite, Integer> favoritesDao;


    public FavoritesRepository(Context ctx){
        try {
            DatabaseManager dbManager = new DatabaseManager();
            db = dbManager.getHelper(ctx);
            favoritesDao = db.getFavoritesDao();
        } catch (SQLException e) {
            // TODO: Exception Handling
            e.printStackTrace();
        }

    }

    public void empty(){
        try {
            favoritesDao.delete(favoritesDao.queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int createOnce(Favorite favorite)
    {
        try {
            //duplicate check
            List<Favorite> exists = favoritesDao.queryForEq("business_id", favorite.getBusiness_id());
            if(exists!=null && exists.size()>0) return 0;

            return favoritesDao.create(favorite);
        } catch (SQLException e) {
            // TODO: Exception Handling
            e.printStackTrace();
        }
        return 0;
    }
    public int update(Favorite favorite)
    {
        try {
            return favoritesDao.update(favorite);
        } catch (SQLException e) {
            // TODO: Exception Handling
            e.printStackTrace();
        }
        return 0;
    }
    public int delete(Favorite favorite)
    {
        try {
            return favoritesDao.delete(favorite);
        } catch (SQLException e) {
            // TODO: Exception Handling
            e.printStackTrace();
        }
        return 0;
    }

    public List<Favorite> getAll()
    {
        try {
            return favoritesDao.queryForAll();
        } catch (SQLException e) {
            // TODO: Exception Handling
            e.printStackTrace();
        }
        return null;
    }

    public List<Favorite> getAllByOrder(String field){
        try {
            return favoritesDao.queryBuilder().orderBy(field, false).query();
        } catch (SQLException e) {
            // TODO: Exception Handling
            e.printStackTrace();
        }
        return null;
    }


}
