package xyz.a1api.multirecycler.app

import android.os.Bundle
import android.support.design.widget.BottomNavigationView

import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import xyz.a1api.multirecycler.BaseQuickAdapter
import xyz.a1api.multirecycler.BaseViewHolder
import xyz.a1api.multirecycler.Binder

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
        BaseQuickAdapter().register<String>(String::class.java).to(object : Binder<String, BaseViewHolder>() {
            override fun convert(holder: BaseViewHolder, item: Any) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getLayoutId(): Int {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }).withClassLinker { position, t ->
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }


        val ad = BaseQuickAdapter().register(String::class.java, object : Binder<String, HBaseViewHolder>() {
            override fun convert(holder: HBaseViewHolder, item: Any) {
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
