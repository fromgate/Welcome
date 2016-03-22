package ru.nukkit.welcome.provider.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable (tableName = "players")
public class PasswordsTable {

    @DatabaseField (id=true)
    private String name;

    @DatabaseField(canBeNull = false)
    private String password;

    public PasswordsTable(){}

    public PasswordsTable(String name, String password){
        this.name = name;
        this.password = password;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String password){
        this.password = password;
    }

}
