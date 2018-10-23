package m2iformation.fr.sqliteannuaire;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.text.method.TextKeyListener.clear;

public class MainActivity extends AppCompatActivity implements AlertDialog.OnClickListener{
    private EditText id;
    private EditText name;
    private EditText tel;
    private EditText search;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id = findViewById(R.id.etId);
        name = findViewById(R.id.etName);
        tel = findViewById(R.id.etTel);
        search = findViewById(R.id.etSearch);

        //Mise en oeuvre de la base de données
        //Création et mises à jour éventuelles
        DbInit dbInit = new DbInit(this);
        db = dbInit.getWritableDatabase();
        //db.query() : pour récupérer des données
        //db.insert() : pour ajouter des données
        //db.update() : pour mettre à jour une donnée existante
        //db.delete() : pour effacer des données
    }

    public void save(View view) {
        ContentValues values = new ContentValues();
        values.put("name", name.getText().toString());
        values.put("tel", tel.getText().toString());

        if (id.getText().toString().equals("")) {
            db.insert("contacts", "", values);
            //insert INTO contacts SET nom= "...", tel="..."
        } else {
            String critere = "id = " + id.getText().toString();
            db.update("contacts", values, critere, null);
            //UPDATE contacts SET nom="...", tel="..." WHERE id"..."
        }
        Toast.makeText(this, "Contact enregistré", Toast.LENGTH_LONG).show();
    }

    public void search(View view) {
        String colonnes [] = {"id", "name", "tel"};
        String nom = search.getText().toString();
        nom = nom.toUpperCase();
        nom = nom.replace("'", "''");
        String critere = "UPPER(name) = '" + nom + "'";

        Cursor curseur = db.query("contacts", colonnes, critere, null, null, null, null);
        if (curseur.getCount()>0) {
            curseur.moveToFirst();
            id.setText(curseur.getString(0));
            name.setText(curseur.getString(1));
            tel.setText(curseur.getString(2));
        }else {
            Toast.makeText(this, "Nom introuvable", Toast.LENGTH_LONG).show();
        }
    }

    public void delete(View view) {
        createAndShowDialog();
    }

    void createAndShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Suppression");
        builder.setMessage("Confirmez-vous la supression ?");
        builder.setPositiveButton("OUI", this);
        builder.setNegativeButton("NON", this);
        builder.create().show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_NEGATIVE:
                // int which = -2
                dialog.dismiss();
                break;
            case BUTTON_POSITIVE:
                // int which = -1
                String critere = "id = " + id.getText().toString();
                db.delete("contacts", critere, null);
                clear(null);
                dialog.dismiss();
                break;
        }
    }

    public void clear(View view) {
        id.setText("");
        name.setText("");
        tel.setText("");
    }
}
