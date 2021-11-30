package mx.tecnm.tepic.ladm_u4_practica1_autocontestadora
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CallLog
import android.telephony.SmsManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val siPermiso = 1
    var mensajeEnvio =""
    val Permiso_Telefono = 2
    var hilo = Hilo(this)
    var Datos = ArrayList<Persona>()
    var ID_Llamada = ""
    var Inicio = true
    var BaseRemota = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.SEND_SMS
                ) !=
                PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.SEND_SMS,android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.READ_CALL_LOG), siPermiso
            )
        }

        button1.setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
        }
        button2.setOnClickListener {
            startActivity(Intent(this, MainActivity3::class.java))
        }

        button3.setOnClickListener {
            startActivity(Intent(this, MainActivity4::class.java))
        }
        button4.setOnClickListener {
            /*PERMISO - LEER LLAMADAS*/
            if(ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_CALL_LOG)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CALL_LOG), Permiso_Telefono)
            }
            hilo.start()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == siPermiso) {
        }

    }


    public fun Accion(){

        var Cursor = contentResolver.query(CallLog.Calls.CONTENT_URI,null,null,null,"date DESC")

        if(Cursor!!.moveToFirst()){
            var Num_Telefono = Cursor.getColumnIndex(CallLog.Calls.NUMBER)
            var Tipo_Llamada = Cursor.getColumnIndex(CallLog.Calls.TYPE)
            var ID = Cursor.getColumnIndex(CallLog.Calls._ID)

            if(Inicio){ID_Llamada = Cursor.getString(ID)
                Inicio = false}

            if (Cursor.getString(ID) != ID_Llamada){
                // El Tipo "3" corresponde a las llamadas perdidas o no contestadas
                if (Cursor.getInt(Tipo_Llamada) == 3){
                    for (i in 0..(Datos.size-1)){
                        //Si el numero de telefono coincide con alguno de nuestras listas
                        if(Cursor.getString(Num_Telefono) == Datos.get(i).Telefono){
                            Enviar_SMS(Datos.get(i))
                            break
                        }//if
                    }//for
                }//if
                ID_Llamada = Cursor.getString(ID)
            }
        }
    }//Accion

    private fun Enviar_SMS(persona: Persona){
        var Mensaje = "NO DEVOLVERE TU LLAMADA, POR FAVOR NO INSISTAS"

        if (persona.Deseado.equals("LISTA BLANCA")){
            Mensaje = "NO ESTOY DISPONIBLE AL RATO TE HABLO"
        } else {
            var datosInsertar = hashMapOf(
                "NUMERO DESEADO" to "NO",
                "Nombre Contacto" to persona.Nombre,
                "Telefono" to persona.Telefono
            )

            BaseRemota.collection("Contestadora")
                .add(datosInsertar)
                .addOnSuccessListener {}
                .addOnFailureListener {
                    Mensaje("ERROR NO DESEADOS: ${it.message!!}")
                }
        }

        SmsManager.getDefault().sendTextMessage(persona.Telefono,null,
            Mensaje,null,null)
    }//Enviar_SMS

    private fun Mensaje(s: String) {
        AlertDialog.Builder(this)
            .setTitle("ATENCIÓN")
            .setMessage(s)
            .setPositiveButton("OK"){d,i->

            }
            .show()
    }//Mensaje
}

class Hilo(p:MainActivity) : Thread(){

    val Puntero = p

    override fun run() {
        super.run()
        while (true){
            sleep(1000)
            Puntero.runOnUiThread {
                Puntero.Accion()
            }
        }
    }
}//class Hilo