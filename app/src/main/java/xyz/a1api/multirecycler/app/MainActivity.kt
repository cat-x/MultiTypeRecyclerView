package xyz.a1api.multirecycler.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import xyz.a1api.multirecycler.base.BaseQuickAdapter
import xyz.a1api.multirecycler.base.BaseViewHolder
import xyz.a1api.multirecycler.base.Binder
import xyz.a1api.multirecycler.base.multi.ClassLinker

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                message.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BaseQuickAdapter().register<String, BaseViewHolder>(String::class.java).to(object : Binder<String, BaseViewHolder>() {
            override fun <K : BaseViewHolder?> convert(holder: K, item: Any) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getLayoutId(): Int {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }).withClassLinker(object : ClassLinker<String, BaseViewHolder> {
            override fun index(position: Int, t: String): Class<out Binder<BaseViewHolder, *>> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })


        val ad = BaseQuickAdapter().register(String::class.java, object : Binder<String, BaseViewHolder>() {
            override fun <K : BaseViewHolder?> convert(holder: K, item: Any) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getLayoutId(): Int {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
        )
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
