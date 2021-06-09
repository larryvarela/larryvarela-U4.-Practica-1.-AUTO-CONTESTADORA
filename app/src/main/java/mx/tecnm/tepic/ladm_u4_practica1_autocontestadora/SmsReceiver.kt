package mx.tecnm.tepic.ladm_u4_practica1_autocontestadora

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.lang.Exception

class CallReceiver :BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            Toast.makeText(context,"Funciona",Toast.LENGTH_LONG)
                .show()
        }catch (e : Exception){


        }
    }

}