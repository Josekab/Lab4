package cr.ac.una.lab4


import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

import com.google.gson.GsonBuilder
import cr.ac.una.lab4.dao.TransactionDAO
import cr.ac.una.lab4.entity.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList
import android.graphics.Bitmap
import cr.ac.una.lab4.entity.Transactions


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //API
        val interceptor= HttpLoggingInterceptor()
        interceptor.level= HttpLoggingInterceptor.Level.BODY

        val client= OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor("aXn4aMdB0d4fPe8E6ZNVBQyL_fpPV8q_b7slK4MozcLo6pb3Gw"))
            .addInterceptor(interceptor)
            .build()

        val gson= GsonBuilder().setPrettyPrinting().create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://crudapi.co.uk/api/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(TransactionDAO::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            val transaction = Transaction(
                _uuid = "", // Asigna un valor por defecto o un valor real en lugar de null
                monto = 0.0, // Asigna un valor por defecto o un valor real en lugar de null
                tipoTarjeta = "", // Asigna un valor por defecto o un valor real en lugar de null
                fecha = "", // Asigna un valor por defecto o un valor real en lugar de null
                foto = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Crea un Bitmap vacío en lugar de null
            )

            var items : ArrayList<Transaction>
            items= ArrayList()
            items.add(transaction)
            var salida= gson.toJson(transaction)
            print(salida)

            // Crea una lista que contenga tu objeto transaction
            val transactions = listOf(transaction)

            // Pasa la lista al método createItem
            val createdItems = apiService.createItem(transactions)

            withContext(Dispatchers.Main) {
                // Procesar la respuesta del API
            }
        }


        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)

        var toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val title: Int
        lateinit var fragment: Fragment
        when (menuItem.getItemId()) {
            R.id.nav_camera -> {
                title = R.string.menu_camera
                fragment = HomeFragment()
            }
            R.id.nav_gallery -> {
                title = R.string.menu_gallery
                fragment= CameraFragment()
            }
            R.id.nav_manage -> title = R.string.menu_tools
            else -> throw IllegalArgumentException("menu option not implemented!!")
        }
        supportFragmentManager
            .beginTransaction()
//.setCustomAnimations(R.anim.bottom_nav_enter, R.anim.bottom_nav_exit)
            .replace(R.id.home_content, fragment)
            .commit()
        setTitle(getString(title))
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}